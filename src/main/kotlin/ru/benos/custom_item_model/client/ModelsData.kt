package ru.benos.custom_item_model.client

import com.google.gson.JsonParser
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.renderer.block.model.ItemTransform
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.world.item.ItemDisplayContext
import org.joml.Vector3f
import ru.benos.custom_item_model.client.CIM.LOGGER
import ru.benos.custom_item_model.client.CIM.rl

@Environment(EnvType.CLIENT)
object ModelsData {
    val MAP: MutableMap<String, MutableMap<Int, String>> = mutableMapOf()
    val RENDER_PROVIDER_LIST: MutableList<String> = mutableListOf()
    val DISPLAY_TRANSFORM: MutableMap<String, Map<ItemDisplayContext, ItemTransform>> = mutableMapOf()

    fun loadItemModels(resourceManager: ResourceManager) {
        // Сброс данных
        MAP.clear()
        RENDER_PROVIDER_LIST.clear()
        DISPLAY_TRANSFORM.clear()

        val itemModels = resourceManager.listResources("models/item") { it.path.endsWith(".json") }
        LOGGER.info("Finding model...")

        var itemIndex = 0
        itemModels.forEach { (location, _) ->
            val s = if (itemIndex == itemModels.size - 1) "\\" else "|"
            val itemId = "${location.namespace}:${location.path.substringAfterLast("/").removeSuffix(".json")}"

            val jsonText = resourceManager.getResource(location).get().openAsReader().use { it.readText() }
            val jsonObj = JsonParser.parseString(jsonText).asJsonObject

            // Overrides
            if (jsonObj.has("overrides")) {
                val overrides = jsonObj.getAsJsonArray("overrides")
                LOGGER.info("$s- Finding in $itemId")

                overrides.forEachIndexed { index, element ->
                    val z = if (index == overrides.size() - 1) "\\" else "|"
                    val predObj = element.asJsonObject.getAsJsonObject("predicate")
                    if (predObj.has("custom_model_data")) {
                        val cmd = predObj.get("custom_model_data").asInt
                        val model = element.asJsonObject.get("model").asString

                        // Читаем модель для проверки render_provider
                        val modelLocation = "${model.rl.namespace}:models/${model.rl.path}.json".rl
                        val modelJsonText = resourceManager.getResource(modelLocation).get().openAsReader().use { it.readText() }
                        val modelJson = JsonParser.parseString(modelJsonText).asJsonObject

                        if (modelJson.has("render_provider") && modelJson.get("render_provider").asString == "cim") {
                            RENDER_PROVIDER_LIST += model
                            LOGGER.info("   \\- Found render provider in '$model'")

                            // Парсим display
                            if (modelJson.has("display")) {
                                val displayObj = modelJson.getAsJsonObject("display")
                                val contextMap = mutableMapOf<ItemDisplayContext, ItemTransform>()

                                for (context in ItemDisplayContext.entries) {
                                    val contextName = context.serializedName

                                    if (displayObj.has(contextName)) {
                                        val tObj = displayObj.getAsJsonObject(contextName)
                                        val translation = tObj.getAsJsonArray("translation")?.map { it.asFloat }?.toFloatArray() ?: floatArrayOf(0f,0f,0f)
                                        val rotation = tObj.getAsJsonArray("rotation")?.map { it.asFloat }?.toFloatArray() ?: floatArrayOf(0f,0f,0f)
                                        val scale = tObj.getAsJsonArray("scale")?.map { it.asFloat }?.toFloatArray() ?: floatArrayOf(1f,1f,1f)
                                        contextMap[context] = ItemTransform(
                                            Vector3f(rotation),
                                            Vector3f(translation),
                                            Vector3f(scale)
                                        )
                                    }
                                }
                                DISPLAY_TRANSFORM[model] = contextMap
                                LOGGER.info("   \\- Parsed display for '$model'")
                            }
                        }

                        // Сохраняем модель в MAP
                        val map = MAP.getOrPut(itemId) { mutableMapOf() }
                        map[cmd] = model
                        LOGGER.info("   $z- Found, '$cmd' -> '$model'")
                    }
                }
            }

            itemIndex++
        }
        LOGGER.info("End finding.")
        LOGGER.info("Map: $MAP")
        LOGGER.info("Render provider: $RENDER_PROVIDER_LIST")
        LOGGER.info(
            "Display transforms: ${DISPLAY_TRANSFORM.map {(key, value) ->
                "$key=" + value.map { (key, value) ->
                    "$key="+ "[TRANSLATION:${value.translation},ROTATION:${value.rotation},SCALE:${value.scale}]"
                }
            }}"
        )
    }
}
