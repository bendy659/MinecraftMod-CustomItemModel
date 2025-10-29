package ru.benos.custom_item_model.client.data.serializables

import kotlinx.serialization.Serializable

@Serializable
data class CimItemDisplayContext(
    val model: String,
    val animation: CimItemDisplayContextAnimation,
    val texture: String,
    val display: String
) {
    companion object {
        val LOCAL = CimItemDisplayContext(
            "./model.geo.json",
            CimItemDisplayContextAnimation(
                "./animations.json",
                "./animation_controller.json"
            ),
            "./texture.png",
            "./display.json"
        )
    }
}

@Serializable
data class CimItemDisplayContextAnimation(
    val file: String,
    val controller: String?
)