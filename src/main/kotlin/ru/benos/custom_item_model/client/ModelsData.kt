package ru.benos.custom_item_model.client

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.renderer.block.model.ItemTransform
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.world.item.ItemDisplayContext
import org.joml.Vector3f
import ru.benos.custom_item_model.client.CIM.LOGGER
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache
import software.bernie.geckolib.renderer.GeoObjectRenderer

@Environment(EnvType.CLIENT)
object ModelsData {
    val MAP: MutableMap<String, Map<Int, String>> = mutableMapOf()
    val RENDER_PROVIDER_LIST: MutableList<String> = mutableListOf()
    val DISPLAY_TRANSFORM: MutableMap<String, Map<String, ItemTransform>> = mutableMapOf()
    val ANIMATABLE_INSTANCE_CACHE: MutableMap<String, AnimatableInstanceCache> = mutableMapOf()
    val RENDERER_CACHE: MutableMap<String, GeoObjectRenderer<FakeItem>> = mutableMapOf()

    // Classes //
    @Serializable
    data class ModelInfo(
        val parent: String? = null,
        val overrides: List<PredicateInfo> = emptyList(),
        @SerialName("render_provider") val renderProvider: String = "vanilla",
        val display: Map<String, ItemTransformInfo>? = null
    )

    @Serializable
    data class PredicateInfo(
        val predicate: Map<String, Float>,
        val model: String
    )

    @Serializable
    data class ItemTransformInfo(
        val translation: FloatArray = floatArrayOf(0f ,0f ,0f),
        val rotation: FloatArray = floatArrayOf(0f, 0f, 0f),
        val scale: FloatArray = floatArrayOf(1f, 1f, 1f)
    )

    fun reloadModels(resourceManager: ResourceManager) {
        val json = Json {
            ignoreUnknownKeys = true
            isLenient = true
            coerceInputValues = true
            explicitNulls = false
        }

        // Reset //
        MAP.clear()
        RENDER_PROVIDER_LIST.clear()
        DISPLAY_TRANSFORM.clear()
        ANIMATABLE_INSTANCE_CACHE.clear()
        RENDERER_CACHE.clear()

        // Get all item models //
        val itemModels = resourceManager.listResources("models/item") { it.path.endsWith(".json") }
        LOGGER.info("Start finding models...")

        for (i in 0 until itemModels.size) {
            val loc = itemModels.toList()[i].first
            val itemId = "%s:item/%s".format( loc.namespace, loc.path.substringAfterLast('/').removeSuffix(".json") )
            val itemLine = if (i == itemModels.size - 1) "\\-" else "|-"

            // Parsing //
            val jsonText = resourceManager.getResource(loc).get().openAsReader().use { it.readText() }
            val itemModelInfo = json.decodeFromString<ModelInfo>(jsonText)

            // Model that have a parent //
            if (itemModelInfo.parent != null) { /* Maybe, later... */ }

            // Model that have a render provider //
            if (itemModelInfo.renderProvider == "cim") {
                RENDER_PROVIDER_LIST += itemId
                LOGGER.info("$itemLine Found 'CIM' render provider in '$itemId'")

                // Model that have a display //
                if (itemModelInfo.display != null && itemModelInfo.display.isNotEmpty()) {
                    val contextMap = DISPLAY_TRANSFORM.getOrPut(itemId) { mutableMapOf() }.toMutableMap()

                    for (context in ItemDisplayContext.entries.map { it.serializedName }) {
                        val displayContext = itemModelInfo.display[context]

                        if (itemModelInfo.display.containsKey(context) && displayContext != null) {
                            contextMap[context] = ItemTransform(
                                Vector3f(displayContext.rotation),
                                Vector3f(displayContext.translation),
                                Vector3f(displayContext.scale)
                            )
                        }
                    }

                    if (contextMap.isNotEmpty()) {
                        DISPLAY_TRANSFORM[itemId] = contextMap.toMap()
                        LOGGER.info("$itemLine Found display context in '$itemId'")
                    }
                }
            }

            // Model that have a overrides //
            if (itemModelInfo.overrides.isNotEmpty()) {
                for (j in 0 until itemModelInfo.overrides.size) {
                    val predicate = itemModelInfo.overrides[j]

                    if (predicate.predicate.containsKey("custom_model_data")) {
                        val predicateValue = predicate.predicate["custom_model_data"]?.toInt() ?: continue
                        val predicateModel = predicate.model

                        // Apply to MAP //
                        MAP[itemId] = mapOf(predicateValue to predicateModel)
                        LOGGER.info("$itemLine Found predicate 'custom_model_data' equal '$predicateValue' linked to '$predicateModel'")
                    }
                }
            }
        }

        // End //
        LOGGER.info( "/- Finished scanning. Summary:")
        LOGGER.info( "|- ${MAP.size} models with a 'custom_model_data' predicate,")
        LOGGER.info( "|- ${RENDER_PROVIDER_LIST.size} models supporting the 'cim' render provider,")
        LOGGER.info( "|- ${DISPLAY_TRANSFORM.size} models that define a 'display' context,")
        LOGGER.info("\\- ${itemModels.size} total models were scanned.")
    }
}
