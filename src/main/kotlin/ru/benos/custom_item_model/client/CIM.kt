package ru.benos.custom_item_model.client

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.fabricmc.fabric.api.resource.ResourcePackActivationType
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.resources.ResourceManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ru.benos.custom_item_model.client.data.serializables.CimModelData
import ru.benos.custom_item_model.client.data.serializables.CimModelProperties

interface IModLoader {
    val loader: String

    fun isLoaded(pLoader: String): Boolean
}

@Environment(EnvType.CLIENT)
object CIM {
    const val MODID: String = "cim"
    val LOGGER: Logger = LoggerFactory.getLogger("CIM")
    val MC: Minecraft by lazy { Minecraft.getInstance() }
    lateinit var LOADER: IModLoader

    fun launch(pLoader: IModLoader) {
        LOADER = pLoader

        LOGGER.info("Mod launch in ${LOADER.loader} loader!")

        //? if fabric {
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(
            object: SimpleSynchronousResourceReloadListener {
                override fun getFabricId(): ResourceLocation = "$MODID:resources".rl

                override fun onResourceManagerReload(resourceManager: ResourceManager) { reload(resourceManager) }
            }
        )

        ResourceManagerHelper.registerBuiltinResourcePack(
            "cim:example_models".rl,
            FabricLoader.getInstance().getModContainer(MODID).get(),
            "(CIM) Example models".literal,
            ResourcePackActivationType.NORMAL
        )
        //? }


    }

    // Util's //

    val Number.F: Float get() = this.toFloat()
    val String.rl: ResourceLocation get() = ResourceLocation.parse(this)
    val String.literal: Component get() = Component.literal(this)

    // ====== //

    fun reload(resourceManager: ResourceManager) {
        val json = Json { ignoreUnknownKeys = true }

        // Parsing all `cim_models.json` //
        val namespaces = resourceManager.namespaces
        for (namespace in namespaces) {
            val location = "$namespace:cim_models.json"
            val rawResource = resourceManager.getResource(location.rl)
            if (!rawResource.isPresent) continue

            val jsonText = rawResource.get().openAsReader().use { it.readText() }
            val jsonElement = json.parseToJsonElement(jsonText)

            if ("entries" !in jsonElement.jsonObject) {
                LOGGER.error("An error occurred while reading `$location'. How about adding an `entries` element there?")
                return
            }

            try {
                val parsed = json.decodeFromJsonElement<CimModelData>(jsonElement)
                CimModelsFinal.addModelData(parsed)
            } catch (_: Exception) {
                LOGGER.error("An error occurred while reading '$location. Perhaps some elements are missing? Check for `items`, `predicate` and `model'!")
            }
        }

        // Parsing all from `cim_models/` //
        val cimModels = CimModelsFinal.MAP.map { (_, data) -> data.model }
        for (location in cimModels) {
            val correctLocation = "${location.namespace}:cim_models/${location.path}"

            // Check exist properties.json //
            val propertiesLocation = "$correctLocation/properties.json".rl
            val rawProperties = resourceManager.getResource(propertiesLocation)
            if (!rawProperties.isPresent) {
                LOGGER.error("An error occurred while reading '$propertiesLocation'. How are you going to use it next?")
                continue
            } else {
                try {
                    val jsonText = rawProperties.get().openAsReader().use { it.readText() }
                    val parsed = json.decodeFromString<CimModelProperties>(jsonText)
                    CimModelsFinal.addModelProperties(location, parsed)
                } catch (_: Exception) {
                    LOGGER.error("An error occurred while reading '$propertiesLocation'. Make sure that everything is specified correctly!")
                }
            }
        }
    }
}