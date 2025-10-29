package ru.benos.custom_item_model.client

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemDisplayContext
import ru.benos.custom_item_model.client.CIM.normalizeLocation
import ru.benos.custom_item_model.client.CIM.rl
import ru.benos.custom_item_model.client.data.finals.CimModelsFinal
import ru.benos.custom_item_model.client.data.serializables.CimItemDisplayContext
import software.bernie.geckolib.animatable.GeoAnimatable
import software.bernie.geckolib.animation.Animation
import software.bernie.geckolib.animation.AnimationProcessor
import software.bernie.geckolib.cache.`object`.BakedGeoModel
import software.bernie.geckolib.model.GeoModel

@Environment(EnvType.CLIENT)
class FakeModel<T: GeoAnimatable>(
    location: ResourceLocation,
    val displayContext: ItemDisplayContext
): GeoModel<T>() {
    private var currentModel: BakedGeoModel? = null
    private val processor: AnimationProcessor<T> = AnimationProcessor(this)
    private val correctPropertiesLocation = "$location".rl
    private val propsEntry = CimModelsFinal.PROPERTIES[correctPropertiesLocation]
    private val displayModelInfo: Map<ItemDisplayContext, CimItemDisplayContext> by lazy {
        val rawMap = propsEntry?.displayContext ?: emptyMap()
        rawMap.mapKeys { (key, _) ->
            when (key.lowercase()) {
                "thirdperson_righthand" -> ItemDisplayContext.THIRD_PERSON_RIGHT_HAND
                "thirdperson_lefthand" -> ItemDisplayContext.THIRD_PERSON_LEFT_HAND
                "firstperson_righthand" -> ItemDisplayContext.FIRST_PERSON_RIGHT_HAND
                "firstperson_lefthand" -> ItemDisplayContext.FIRST_PERSON_LEFT_HAND
                "gui" -> ItemDisplayContext.GUI
                "ground" -> ItemDisplayContext.GROUND
                "fixed" -> ItemDisplayContext.FIXED
                "head" -> ItemDisplayContext.HEAD
                "none" -> ItemDisplayContext.NONE
                else -> {
                    println("CIM: unknown display context key '$key' in properties for $location")
                    ItemDisplayContext.NONE
                }
            }
        }
    }

    // helper для дефолтного ResourceLocation по шаблону
    private fun defaultPath(resource: ResourceLocation, suffix: String): ResourceLocation =
        "${resource.namespace}:cim_models/${resource.path}/$suffix".rl

    val modelLocation: ResourceLocation = run {
        val ctx = displayModelInfo[displayContext]
        val modelStr = ctx?.model
        when {
            modelStr == "." -> defaultPath(location, "model.geo.json")
            modelStr != null -> modelStr.rl
            propsEntry != null -> {
                // если есть общий дефолт в entry, попытаться его использовать, иначе fallback
                propsEntry.displayContext.values.firstOrNull()?.model?.takeIf { it != "." }?.rl
                    ?: defaultPath(location, "model.geo.json")
            }
            else -> defaultPath(location, "model.geo.json")
        }
    }

    val textureLocation: ResourceLocation = run {
        val ctx = displayModelInfo[displayContext]
        val textureStr = ctx?.texture
        when {
            textureStr == "." -> defaultPath(location, "texture.png")
            textureStr != null -> textureStr.rl
            else -> defaultPath(location, "texture.png")
        }
    }

    val animationLocation: ResourceLocation = run {
        val ctx = displayModelInfo[displayContext]
        val animFile = ctx?.animation?.file
        when {
            animFile == "." -> defaultPath(location, "animations.json")
            animFile != null -> animFile.rl
            else -> defaultPath(location, "animations.json")
        }
    }

    override fun getModelResource(animatable: T): ResourceLocation = modelLocation

    override fun getTextureResource(animatable: T): ResourceLocation = textureLocation

    override fun getAnimationResource(animatable: T): ResourceLocation = animationLocation

    override fun getBakedModel(location: ResourceLocation): BakedGeoModel? {
        val bakedModel = CimModelsFinal.BAKED_MODELS[location.normalizeLocation]
            ?: CimModelsFinal.BAKED_MODELS["cim:cim_models/error/model.geo.json".rl.normalizeLocation]
            ?: throw IllegalArgumentException("Unable to find model file: $location (and error model is missing)")

        if (bakedModel != currentModel) {
            processor.setActiveModel(bakedModel)
            currentModel = bakedModel
        }

        return currentModel
    }

    override fun getAnimation(animatable: T?, name: String?): Animation? {
        if (name == null) return null

        val normalizedLocation = animationLocation.normalizeLocation
        val bakedAnimations = CimModelsFinal.BAKED_ANIMATIONS[normalizedLocation]

        if (bakedAnimations != null) {
            val animation = bakedAnimations.getAnimation(name)
            if (animation != null) return animation
        }

        println("CIM: Animation '$name' not found")
        return null
    }

    override fun getAnimationProcessor(): AnimationProcessor<T> = processor
}