package ru.benos.custom_item_model.client

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import software.bernie.geckolib.animatable.GeoAnimatable
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache
import software.bernie.geckolib.animation.AnimatableManager
import software.bernie.geckolib.animation.AnimationController
import software.bernie.geckolib.animation.RawAnimation
import software.bernie.geckolib.util.GeckoLibUtil
import software.bernie.geckolib.util.RenderUtil

@Environment(EnvType.CLIENT)
class FakeItem: GeoAnimatable {
    val CACHE: AnimatableInstanceCache = GeckoLibUtil.createInstanceCache(this)

    override fun registerControllers(p0: AnimatableManager.ControllerRegistrar) {
        //p0.add( AnimationController(this) { state ->
        //    state.setAndContinue( RawAnimation.begin().thenLoop("all") )
        //} )
    }

    override fun getAnimatableInstanceCache(): AnimatableInstanceCache = CACHE

    override fun getTick(p0: Any?): Double = RenderUtil.getCurrentTick()
}