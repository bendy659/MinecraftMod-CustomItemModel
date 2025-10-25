package ru.benos.custom_item_model.client.data.serializables

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class PredicateMode {
    @SerialName("all") ALL,
    @SerialName("any") ANY,
    @SerialName("only") ONLY
}