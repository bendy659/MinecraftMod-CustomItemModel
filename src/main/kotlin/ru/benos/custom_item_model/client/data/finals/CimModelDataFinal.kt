package ru.benos.custom_item_model.client.data.finals

import kotlinx.serialization.json.JsonObject
import net.minecraft.resources.ResourceLocation
import ru.benos.custom_item_model.client.data.serializables.PredicateMode

data class CimModelDataFinal(
    val predicate: List<JsonObject> = listOf(),
    val predicateMode: PredicateMode = PredicateMode.ALL,
    val model: ResourceLocation,
    val force: Boolean = false
)