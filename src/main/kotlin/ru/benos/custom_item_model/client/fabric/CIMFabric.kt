//? if fabric {
package ru.benos.custom_item_model.client.fabric

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.loader.api.FabricLoader
import ru.benos.custom_item_model.client.CIM
import ru.benos.custom_item_model.client.IModLoader

class CIMFabric: ClientModInitializer {
    object CIMFabricLoader: IModLoader {
        override val loader: String = "fabric"

        override fun isLoaded(pLoader: String): Boolean =
            FabricLoader.getInstance().isModLoaded(pLoader)
    }

    override fun onInitializeClient() =
        CIM.launch(CIMFabricLoader)
}
//? }