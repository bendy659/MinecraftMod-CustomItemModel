package ru.benos.custom_item_model.client

import com.google.gson.JsonElement
import com.google.gson.JsonParser
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.resources.ResourceManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileOutputStream
import java.io.Reader

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

                override fun onResourceManagerReload(resourceManager: ResourceManager) {
                    ModelsData.loadItemModels(resourceManager)
                }
            }
        )
        //? }

        createResourcepack()
    }

    // Util's //

    val Any.S: String get() = this.toString()
    val Number.F: Float get() = this.toFloat()

    val String.literal: Component get() = Component.literal(this)
    val String.translate: Component get() = Component.translatable(this)
    val String.rl: ResourceLocation get() = ResourceLocation.parse(this)

    val Reader.toJsonElement: JsonElement get() = this.use { JsonParser.parseReader(it) }

    // ====== //

    fun createResourcepack() {
        val dir = File(".", "resourcepacks")
        val target = File(dir, "(CIM) Example models.zip")

        if (!target.exists())
            try {
                dir.mkdirs()
                CIM::class.java.getResourceAsStream("/assets/${CIM.MODID}/example_models.zip")?.use { input ->
                    FileOutputStream(target).use { output ->
                        val buffer = ByteArray(16384)
                        var len: Int
                        while (input.read(buffer).also { len = it } > 0) {
                            output.write(buffer, 0, len)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
    }
}