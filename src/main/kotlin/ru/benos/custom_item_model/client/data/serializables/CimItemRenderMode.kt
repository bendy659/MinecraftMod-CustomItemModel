package ru.benos.custom_item_model.client.data.serializables

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class CimItemRenderMode {
    @SerialName("normal") NORMAL,
    @SerialName("add") ADD,
    @SerialName("divide") DIVIDE,
    @SerialName("invert") INVERT
}