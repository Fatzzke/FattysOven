package de.fatzzke.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import de.fatzzke.entities.OvenBlockEnity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

import net.minecraft.world.item.ItemDisplayContext;



import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

import javax.annotation.Nonnull;


public class OvenBlockRenderer implements BlockEntityRenderer<OvenBlockEnity> {
    private final BlockEntityRendererProvider.Context context;

    public OvenBlockRenderer(BlockEntityRendererProvider.Context context) {
        super();
        this.context = context;
    }

    @Override
    public void render(@Nonnull OvenBlockEnity blockEntity, float partialTick, @Nonnull PoseStack poseStack,
            @Nonnull MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

        var renderItem = blockEntity.getInventory().getStackInSlot(0);
        if (renderItem.isEmpty()) {
            return;
        }

        poseStack.pushPose();
        var facing = blockEntity.getBlockState().getValue(HORIZONTAL_FACING);
        switch (facing) {
            case EAST:
                poseStack.translate(0.7, 0.5, 0.6);
                poseStack.mulPose(Axis.YP.rotationDegrees(90f));
                poseStack.mulPose(Axis.XP.rotationDegrees(-45f));
                break;
            case WEST:
                poseStack.translate(0.3, 0.5, 0.4);
                poseStack.mulPose(Axis.YP.rotationDegrees(90f));
                poseStack.mulPose(Axis.XP.rotationDegrees(45f));
                break;
            case NORTH:
                poseStack.translate(0.6, 0.5, 0.4);
                poseStack.mulPose(Axis.XP.rotationDegrees(45f));
                break;

            case SOUTH:
                poseStack.translate(0.4, 0.5, 0.7);
                poseStack.mulPose(Axis.XP.rotationDegrees(-45f));
                break;
            default:
                break;

        }

        poseStack.scale(0.5f, 0.5f, 0.5f);
        this.context.getItemRenderer().renderStatic(renderItem, ItemDisplayContext.FIXED,
                packedLight, packedOverlay, poseStack, bufferSource, blockEntity.getLevel(), 0);
        poseStack.popPose();

    }

}
