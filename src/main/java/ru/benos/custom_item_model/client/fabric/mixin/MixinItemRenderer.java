package ru.benos.custom_item_model.client.fabric.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.benos.custom_item_model.client.FakeRenderer;

@Mixin(ItemRenderer.class)
public class MixinItemRenderer {
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(
            ItemStack itemStack,
            ItemDisplayContext displayContext,
            boolean leftHand,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int combinedLight,
            int combinedOverlay,
            BakedModel originalModel,
            CallbackInfo ci
    ) {
        var origItemId = FakeRenderer.INSTANCE.itemLocation(itemStack);
        var renderContext = new FakeRenderer.Context(
                itemStack,
                displayContext,
                leftHand,
                poseStack,
                bufferSource,
                combinedOverlay
        );

        var isCancel = FakeRenderer.INSTANCE.render(renderContext);

        if (isCancel) ci.cancel();
    }
}
