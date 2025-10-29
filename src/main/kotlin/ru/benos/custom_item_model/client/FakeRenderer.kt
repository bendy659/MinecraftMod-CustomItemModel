package ru.benos.custom_item_model.client

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.block.model.ItemTransform
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import org.joml.Vector3f
import ru.benos.custom_item_model.client.CIM.normalizeLocation
import ru.benos.custom_item_model.client.CIM.rl
import ru.benos.custom_item_model.client.data.finals.CimModelsFinal
import ru.benos.custom_item_model.client.data.serializables.*
import software.bernie.geckolib.renderer.GeoObjectRenderer

@Environment(EnvType.CLIENT)
object FakeRenderer {
    private val CACHE: MutableMap<ResourceLocation, RendererCache> = mutableMapOf()

    data class Context(
        var itemStack: ItemStack,
        var displayContext: ItemDisplayContext,
        var isLeftHand: Boolean,
        var poseStack: PoseStack,
        var buffer: MultiBufferSource,
        var lightOverlay: Int
    )

    data class RendererCache(
        val item: FakeItem = FakeItem()
    )

    fun render(context: Context): Boolean {
        val origItemId = itemLocation(context.itemStack)
        if (context.itemStack.isEmpty) return false

        val itemList = CimModelsFinal.MAP.keys
        val itemFilter = itemList.any { origItemId in it }
        if (!itemFilter) return false

        val entry = CimModelsFinal.MAP.entries.firstOrNull { (items, _) -> origItemId in items } ?: return false
        val data = entry.value

        val (matched, _) = false to emptyMap<ResourceLocation, Any>()

        if (!matched) return false

        val modelLocation = data.model
        val geoModel = FakeModel<FakeItem>(modelLocation, context.displayContext)
        val cache = CACHE.computeIfAbsent(modelLocation) { RendererCache() }
        var renderSuccess = false

        try {
            context.poseStack.pushPose()
            context.poseStack.clear()

            // Apply transforms //
            val transforms = CimModelsFinal.DISPLAYS["$modelLocation/display".rl]
            if (transforms != null) {
                val transform = transforms[context.displayContext] ?: ItemTransform.NO_TRANSFORM
                val scaledTransform = if (
                    context.displayContext.serializedName.contains("hand", true) ||
                    context.displayContext.serializedName.contains("gui", true) ||
                    context.displayContext.serializedName.contains("fixed", true)
                ) {
                    ItemTransform(
                        transform.rotation,
                        Vector3f(
                            transform.translation.x / 16f,
                            transform.translation.y / 16f,
                            transform.translation.z / 16f
                        ),
                        transform.scale
                    )
                } else transform

                scaledTransform.apply(context.isLeftHand, context.poseStack)
            }

            context.poseStack.translate(-0.5, -0.5, -0.5)

            GeoObjectRenderer(geoModel).render(
                context.poseStack,
                cache.item,
                context.buffer,
                null,
                null,
                context.lightOverlay,
                RenderSystem.getShaderGameTime()
            )

            renderSuccess = true
        } catch (ex: Exception) {
            println("CIM: render failed for $modelLocation: ${ex.message}")
            ex.printStackTrace()
        } finally { context.poseStack.popPose() }

        return renderSuccess
    }

    fun itemLocation(itemStack: ItemStack): ResourceLocation =
        BuiltInRegistries.ITEM.getKey(itemStack.item)

    fun itemMatchesPredicates(
        itemStack: ItemStack,
        predicates: List<IPredicateData>,
        mode: PredicateMode
    ): Pair<Boolean, Map<ResourceLocation, Any>> {

        fun extractPrimitive(obj: Any?): Any? {
            if (obj == null) return null
            if (obj is Number || obj is String || obj is Boolean) return obj

            // попробовать поле "value"
            try {
                val field = obj.javaClass.getDeclaredField("value")
                field.isAccessible = true
                val fv = field.get(obj)
                if (fv is Number || fv is String || fv is Boolean) return fv
            } catch (_: Exception) {}

            try {
                val m = obj.javaClass.methods.firstOrNull {
                    it.parameterCount == 0 && (
                            it.name.equals("getValue", true) ||
                                    it.name.equals("value", true)
                            )
                }
                val mv = m?.invoke(obj)
                if (mv is Number || mv is String || mv is Boolean) return mv
            } catch (_: Exception) {}

            return obj.toString()
        }

        val matchedValues = mutableMapOf<ResourceLocation, Any>()

        val results = predicates.map { pred ->
            val type = BuiltInRegistries.DATA_COMPONENT_TYPE.get(pred.name.rl)
            val component = type?.let { itemStack.components.getTyped(it) }
            val extracted = extractPrimitive(component?.value)

            val ok = when (pred) {
                is PredicateDataInt -> {
                    val actual = when (extracted) {
                        is Number -> extracted.toInt()
                        is String -> extracted.toIntOrNull()
                        else -> null
                    }
                    actual == pred.value
                }
                is PredicateDataFloat -> {
                    val actual = when (extracted) {
                        is Number -> extracted.toDouble()
                        is String -> extracted.toDoubleOrNull()
                        else -> null
                    }
                    actual?.let { kotlin.math.abs(it - pred.value.toDouble()) < 1e-6 } ?: false
                }
                is PredicateDataString -> extracted?.toString() == pred.value
            }

            if (ok && extracted != null) matchedValues[pred.name.rl] = extracted

            ok
        }

        val final = when (mode) {
            PredicateMode.ALL -> results.all { it }
            PredicateMode.ANY -> results.any { it }
            PredicateMode.ONLY -> results.all { it } && predicates.size == itemStack.components.size()
        }

        return final to matchedValues
    }
}