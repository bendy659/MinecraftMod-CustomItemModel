package ru.benos.custom_item_model.client.data.serializables

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class CimItemAnimationControllerContextMode {
    @SerialName("time") TIME,
    @SerialName("speed") SPEED
}

@Serializable
enum class CimItemAnimationControllerContextType {
    @SerialName("item_predicate") ITEM_PREDICATE,
    @SerialName("player_data") PLAYER_DATA,
    @SerialName("keybind_action") KEYBIND_ACTION
}

@Serializable
data class CimItemAnimationControllerContext(
    val type: String,
    val target: String,
    val range: List<Float>,
    val reverse: Boolean = false,
    val mode: CimItemAnimationControllerContextMode
)