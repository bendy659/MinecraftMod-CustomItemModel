package ru.benos.custom_item_model.client

import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.fabricmc.fabric.api.resource.ResourcePackActivationType
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.Minecraft
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.resources.ResourceManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory

interface IModLoader {
    val loader: String

    fun isLoaded(pLoader: String): Boolean
}

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

                override fun onResourceManagerReload(resourceManager: ResourceManager) { ModelsData.reloadModels(resourceManager) }
            }
        )

        ResourceManagerHelper.registerBuiltinResourcePack(
            "cim:example_models".rl,
            FabricLoader.getInstance()
                .getModContainer(MODID)
                .get(),
            "(CIM) Example models",
            ResourcePackActivationType.NORMAL
        )
        //? }


    }

    // Util's //

    val Number.F: Float get() = this.toFloat()
    val String.rl: ResourceLocation get() = ResourceLocation.parse(this)
}