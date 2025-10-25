package ru.benos.custom_item_model.client

import net.minecraft.resources.ResourceLocation
import ru.benos.custom_item_model.client.CIM.rl
import ru.benos.custom_item_model.client.data.serializables.CimModelData
import ru.benos.custom_item_model.client.data.finals.CimModelDataFinal
import ru.benos.custom_item_model.client.data.serializables.CimModelProperties

object CimModelsFinal {
    private val _MAP: MutableMap<List<ResourceLocation>, CimModelDataFinal> = mutableMapOf()
    val MAP: MutableMap<List<ResourceLocation>, CimModelDataFinal> = _MAP

    private val _PROPERTIES = mutableMapOf<ResourceLocation, CimModelProperties>()
    val PROPERTIES: Map<ResourceLocation, CimModelProperties> get() = _PROPERTIES

    fun addModelData(data: CimModelData) {
        data.entries.forEach { context ->
            val items = context.items.map { it.rl }
            _MAP[items] = CimModelDataFinal(context.predicate, context.model.rl, context.force)
        }
    }

    fun addModelProperties(location: ResourceLocation, properties: CimModelProperties) {
        _PROPERTIES[location] = properties
    }
}