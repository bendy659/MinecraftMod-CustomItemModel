//? if fabric {
package ru.benos.custom_item_model.client.loaders.fabric

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.ModContainer
import ru.benos.custom_item_model.client.CIM
import ru.benos.custom_item_model.client.IModLoader
import java.util.Optional

class CIMFabric: ClientModInitializer {
    object CIMFabricLoader: IModLoader {
        override val loader: String = "fabric"

        override fun isLoaded(pLoader: String): Boolean =
            FabricLoader.getInstance().isModLoaded(pLoader)

        override fun getModContainer(modId: String): Optional<ModContainer> =
            FabricLoader.getInstance().getModContainer(modId)
    }

    override fun onInitializeClient() =
        CIM.launch(CIMFabricLoader)
}
//? }