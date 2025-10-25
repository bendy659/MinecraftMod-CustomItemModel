package ru.benos.custom_item_model.client.data.serializables

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.minecraft.world.item.ItemDisplayContext

@Serializable
data class CimModelProperties(
    val name: String? = null,
    val authors: List<String> = listOf(),
    val glow: Boolean = false,
    val light: Int = 0,
    @SerialName("render_mode")
    val renderMode: CimItemRenderMode = CimItemRenderMode.NORMAL,
    @SerialName("display_context")
    val displayContext: Map<ItemDisplayContext, CimItemDisplayContext> = mapOf(ItemDisplayContext.NONE to CimItemDisplayContext.LOCAL),
    @SerialName("animation_controller")
    val animationController: Map<String, CimItemAnimationControllerContext> = emptyMap()
)