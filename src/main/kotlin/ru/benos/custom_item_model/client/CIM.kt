package ru.benos.custom_item_model.client

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.fabricmc.fabric.api.resource.ResourcePackActivationType
import net.fabricmc.loader.api.ModContainer
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.PackType
import java.util.*

interface IModLoader {
    val loader: String

    fun isLoaded(pLoader: String): Boolean
    fun getModContainer(modId: String): Optional<ModContainer>
}

@Environment(EnvType.CLIENT)
object CIM {
    const val MODID: String = "cim"
    val LOGGER: CIMLogger = CIMLogger(MODID.uppercase())
    val MC: Minecraft by lazy { Minecraft.getInstance() }
    lateinit var LOADER: IModLoader

    fun launch(pLoader: IModLoader) {
        LOADER = pLoader

        LOGGER.info("Mod launch in ${LOADER.loader} loader!")

        //? if fabric {
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES)
            .registerReloadListener(Reload)

        ResourceManagerHelper
            .registerBuiltinResourcePack(
                "cim:example_models".rl,
                LOADER.getModContainer(MODID).get(),
                "(CIM) Example models".literal,
                ResourcePackActivationType.NORMAL
            )
        //? }


    }

    // Util's //

    val String.rl: ResourceLocation get() = ResourceLocation.parse(this)
    val String.literal: Component get() = Component.literal(this)

    val ResourceLocation.normalizeLocation: ResourceLocation get() {
        val prefix = "cim_models/"
        val pathWithoutPrefix = this.path.removePrefix(prefix)

        // #Опиши
        val parts = pathWithoutPrefix.split("/")

        // Убираем расширение только у последнего сегмента
        val last = parts.last()
        val lastWithoutExt = last.substringBeforeLast('.')

        // Собираем обратно путь
        val normalizedPath = (parts.dropLast(1) + lastWithoutExt).joinToString("/")

        return "${this.namespace}:$normalizedPath".rl
    }
}