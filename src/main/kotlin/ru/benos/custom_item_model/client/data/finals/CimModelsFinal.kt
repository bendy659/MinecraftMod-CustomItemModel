package ru.benos.custom_item_model.client.data.finals

import net.minecraft.client.renderer.block.model.ItemTransform
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemDisplayContext
import ru.benos.custom_item_model.client.CIM.rl
import ru.benos.custom_item_model.client.data.serializables.CimModelData
import ru.benos.custom_item_model.client.data.serializables.CimModelProperties
import software.bernie.geckolib.cache.`object`.BakedGeoModel
import software.bernie.geckolib.loading.`object`.BakedAnimations

object CimModelsFinal {
    private val _MAP: MutableMap<List<ResourceLocation>, CimModelDataContextFinal> = mutableMapOf()
    val MAP: MutableMap<List<ResourceLocation>, CimModelDataContextFinal> = _MAP

    private val _PROPERTIES: MutableMap<ResourceLocation, CimModelProperties> = mutableMapOf()
    val PROPERTIES: Map<ResourceLocation, CimModelProperties> get() = _PROPERTIES

    private val _BAKED_MODELS: MutableMap<ResourceLocation, BakedGeoModel> = mutableMapOf()
    val BAKED_MODELS: Map<ResourceLocation, BakedGeoModel> get() = _BAKED_MODELS

    private val _BAKED_ANIMATIONS: MutableMap<ResourceLocation, BakedAnimations> = mutableMapOf()
    val BAKED_ANIMATIONS: Map<ResourceLocation, BakedAnimations> get() = _BAKED_ANIMATIONS

    private val _DISPLAYS: MutableMap<ResourceLocation, Map<ItemDisplayContext, ItemTransform>> = mutableMapOf()
    val DISPLAYS: MutableMap<ResourceLocation, Map<ItemDisplayContext, ItemTransform>> = _DISPLAYS

    fun applyCimModelData(data: CimModelData) =
        data.entries.forEach { context ->
            CimModelDataContextFinal(context.predicate, context.predicateMode, context.model.rl, context.force)
        }
}