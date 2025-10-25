package ru.benos.custom_item_model.client

import com.mojang.blaze3d.vertex.PoseStack
import kotlinx.serialization.json.*
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import ru.benos.custom_item_model.client.CIM.rl
import ru.benos.custom_item_model.client.data.serializables.PredicateMode

@Environment(EnvType.CLIENT)
object FakeRenderer {
    private val _CACHE: MutableMap<ResourceLocation, Context> = mutableMapOf()
    val CACHE: Map<ResourceLocation, Context> = _CACHE

    data class Context(
        var itemStack: ItemStack,
        var displayContext: ItemDisplayContext,
        var isLeftHand: Boolean,
        var poseStack: PoseStack,
        var buffer: MultiBufferSource,
        var lightOverlay: Int
    )

    fun render(context: Context): Boolean {
        val origItemId = itemLocation(context.itemStack)
        if (context.itemStack.isEmpty) return false

        val itemList = CimModelsFinal.MAP.keys
        val itemFilter = itemList.any { items -> origItemId in items }
        if (!itemFilter) return false

        itemList.forEach { items ->
            val data = CimModelsFinal.MAP[items] ?: return false
            val matched = itemMatchesPredicates(context.itemStack, data.predicate, data.predicateMode)
            if (!matched.first) return false

            val nameValue = matched.second
            nameValue.forEach { (name, value) ->
                val componentType = BuiltInRegistries.DATA_COMPONENT_TYPE.get(name) ?: return false
                val filtered = context.itemStack.components.filter { it == componentType }
                filtered.forEach { typed ->
                    filtered[typed]
                }
            }
        }

        return true
    }

    fun itemLocation(itemStack: ItemStack): ResourceLocation =
        BuiltInRegistries.ITEM.getKey(itemStack.item)

    fun itemMatchesPredicates(
        itemStack: ItemStack,
        predicates: List<JsonObject>,
        mode: PredicateMode
    ): Pair<Boolean, Map<ResourceLocation, Any>> {
        val matchedValues = mutableMapOf<ResourceLocation, Any>()

        val results = predicates.map { jsonObject ->
            val name = jsonObject["name"]?.jsonPrimitive?.content ?: return@map false
            val rawValue = jsonObject["value"]?.jsonPrimitive ?: return@map false

            val expected: Any = when {
                rawValue.intOrNull != null -> rawValue.int
                rawValue.floatOrNull != null -> rawValue.float
                else -> rawValue.content
            }

            val type = BuiltInRegistries.DATA_COMPONENT_TYPE.get(name.rl) ?: return@map false
            val component = itemStack.components.getTyped(type) ?: return@map false

            val matched = when (expected) {
                is Int -> component.value == expected
                is Float -> component.value == expected
                is String -> component.value == expected
                else -> false
            }

            if (matched) matchedValues[name.rl] = component.value

            matched
        }

        val finalResult = when (mode) {
            PredicateMode.ALL -> results.all { it }
            PredicateMode.ANY -> results.any { it }
            else -> false
        }

        return finalResult to matchedValues
    }
}