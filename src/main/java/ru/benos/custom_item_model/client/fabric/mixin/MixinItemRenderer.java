package ru.benos.custom_item_model.client.fabric.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.benos.custom_item_model.client.FakeRenderer;
import ru.benos.custom_item_model.client.ModelsData;

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
        String itemId = BuiltInRegistries.ITEM.getKey(itemStack.getItem()).toString();

        if (ModelsData.INSTANCE.getMAP().get(itemId) != null) {
            int cmd;

            if (itemStack.getComponents().has(DataComponents.CUSTOM_MODEL_DATA)) {
                cmd = itemStack.getComponents().get(DataComponents.CUSTOM_MODEL_DATA).value();

                if (ModelsData.INSTANCE.getMAP().containsKey(itemId)) {
                    FakeRenderer.INSTANCE.render(itemStack, displayContext, leftHand, poseStack, bufferSource, combinedLight, combinedOverlay, cmd);
                    ci.cancel();
                }
            }
        }
    }
}
