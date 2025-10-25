package ru.benos.custom_item_model.client

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.resources.ResourceLocation
import ru.benos.custom_item_model.client.CIM.rl
import software.bernie.geckolib.animatable.GeoAnimatable
import software.bernie.geckolib.animation.AnimationProcessor
import software.bernie.geckolib.cache.GeckoLibCache
import software.bernie.geckolib.cache.`object`.BakedGeoModel
import software.bernie.geckolib.model.GeoModel

@Environment(EnvType.CLIENT)
class FakeModel<T: GeoAnimatable>(
    val modelLocation: ResourceLocation,
    val animationLocation: ResourceLocation,
    val textureLocation: ResourceLocation
): GeoModel<T>() {
    private var currentModel: BakedGeoModel? = null
    private val processor: AnimationProcessor<T> = AnimationProcessor(this)

    override fun getModelResource(animatable: T): ResourceLocation = modelLocation

    override fun getTextureResource(animatable: T): ResourceLocation = textureLocation

    override fun getAnimationResource(animatable: T): ResourceLocation = animationLocation

    override fun getBakedModel(location: ResourceLocation): BakedGeoModel? {
        val bakedModel = GeckoLibCache.getBakedModels()[location] ?:
            GeckoLibCache.getBakedModels()["cim:cim_models/error/model.geo.json".rl]

        if (bakedModel != currentModel) {
            processor.setActiveModel(bakedModel)
            currentModel = bakedModel
        }

        return currentModel
    }

    override fun getAnimationProcessor(): AnimationProcessor<T> = processor
}