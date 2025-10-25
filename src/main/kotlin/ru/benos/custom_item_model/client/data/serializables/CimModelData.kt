package ru.benos.custom_item_model.client.data.serializables

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import ru.benos.custom_item_model.client.data.serializables.PredicateMode

@Serializable
data class CimModelData(
    var entries: List<CimModelDataContext>
)

@Serializable
data class CimModelDataContext(
    val items: List<String>,
    val predicate: List<JsonObject> = listOf(),
    val predicateMode: PredicateMode = PredicateMode.ALL,
    val model: String,
    val force: Boolean = false
)