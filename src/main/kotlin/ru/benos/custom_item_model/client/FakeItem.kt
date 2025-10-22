package ru.benos.custom_item_model.client

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.block.model.ItemTransform
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import org.joml.Quaternionf
import ru.benos.custom_item_model.client.CIM.F
import ru.benos.custom_item_model.client.CIM.LOGGER
import ru.benos.custom_item_model.client.CIM.rl
import software.bernie.geckolib.animatable.GeoAnimatable
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache
import software.bernie.geckolib.animation.AnimatableManager
import software.bernie.geckolib.animation.AnimationController
import software.bernie.geckolib.animation.RawAnimation
import software.bernie.geckolib.model.GeoModel
import software.bernie.geckolib.renderer.GeoObjectRenderer
import software.bernie.geckolib.util.GeckoLibUtil
import software.bernie.geckolib.util.RenderUtil

@Environment(EnvType.CLIENT)
class FakeItem: GeoAnimatable {
    val CACHE: AnimatableInstanceCache = GeckoLibUtil.createInstanceCache(this)

    override fun registerControllers(p0: AnimatableManager.ControllerRegistrar) {
        p0.add(
            AnimationController(this) { state ->
                state.setAndContinue(
                    RawAnimation.begin().thenLoop("all")
                )
            }
        )
    }

    override fun getAnimatableInstanceCache(): AnimatableInstanceCache = CACHE

    override fun getTick(p0: Any?): Double = RenderUtil.getCurrentTick()

}

@Environment(EnvType.CLIENT)
class FakeModel(model: ResourceLocation): GeoModel<FakeItem>() {
    val namespace: String = model.namespace
    val path: String = model.path.removePrefix("item/")

    override fun getModelResource(p0: FakeItem?): ResourceLocation =
        "$namespace:geo/$path.geo.json".rl

    override fun getTextureResource(p0: FakeItem?): ResourceLocation =
        "$namespace:textures/item/$path.png".rl

    override fun getAnimationResource(p0: FakeItem?): ResourceLocation =
        "$namespace:animations/$path.animation.json".rl
}

@Environment(EnvType.CLIENT)
object FakeRenderer {
    private val animatableCache = mutableMapOf<String, FakeItem>()
    private val rendererCache = mutableMapOf<String, GeoObjectRenderer<FakeItem>>()

    fun render(
        stack: ItemStack,
        context: ItemDisplayContext,
        leftHand: Boolean,
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        light: Int,
        overlay: Int,
        customModelData: Int
    ) {
        if (stack.components.get(DataComponents.CUSTOM_MODEL_DATA)?.value == 0) return

        val registryItems = BuiltInRegistries.ITEM.getKey(stack.item)
        val itemId = "%s:item/%s".format( registryItems.namespace, registryItems.path )
        val modelPath = ModelsData.MAP[itemId]?.get(customModelData) ?: "cim:item/error"

        val animatable = animatableCache.computeIfAbsent(modelPath) { FakeItem() }
        val renderer = rendererCache.computeIfAbsent(modelPath) { GeoObjectRenderer( FakeModel(modelPath.rl) ) }

        poseStack.pushPose()
        poseStack.clear()

        // Apply transforms //
        val transformData = ModelsData.DISPLAY_TRANSFORM[modelPath]
        if (transformData != null) {
            val transform = transformData[context.serializedName] ?: ItemTransform.NO_TRANSFORM

            if (
                context.serializedName.contains("hand", true) ||
                context.serializedName.contains("gui", true)
            ) {
                val i = if (leftHand) -1 else 1
                val f = transform.rotation.x
                val g = transform.rotation.y * i
                val h = transform.rotation.z * i

                poseStack.translate(
                    transform.translation.x / 16.0,
                    transform.translation.y / 16.0,
                    transform.translation.z / 16.0
                )
                poseStack.mulPose(
                    Quaternionf().rotateXYZ(
                        (f * Math.PI / 180f).F,
                        (g * Math.PI / 180f).F,
                        (h * Math.PI / 180f).F
                    )
                )
                poseStack.scale(
                    transform.scale.x,
                    transform.scale.y,
                    transform.scale.z
                )

            } else transform.apply(leftHand, poseStack)
            poseStack.translate(-0.5, -0.5, -0.5)
        }

        renderer.render(poseStack, animatable, buffer, null, null, light, RenderSystem.getShaderGameTime())
        poseStack.popPose()
    }
}