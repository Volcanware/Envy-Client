package mathax.client.systems.modules.render;

import mathax.client.eventbus.EventHandler;
import mathax.client.events.render.RenderItemEntityEvent;
import mathax.client.mixininterface.IItemEntity;
import mathax.client.systems.modules.Categories;
import mathax.client.systems.modules.Module;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.SkullBlock;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.item.*;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;

public class ItemPhysics extends Module {
    public ItemPhysics() {
        super(Categories.Fun, Items.BEEF, "item-physics", "Applies physics to items on the ground.");
    }

    @EventHandler
    private void onRenderItemEntity(RenderItemEntityEvent event) {
        ItemStack itemStack = event.itemEntity.getStack();
        int seed = itemStack.isEmpty() ? 187 : Item.getRawId(itemStack.getItem()) + itemStack.getDamage();
        event.random.setSeed(seed);

        event.matrixStack.push();

        // TODO: Test
        BakedModel bakedModel = event.itemRenderer.getModel(itemStack, event.itemEntity.world, null, 0);
        boolean hasDepthInGui = bakedModel.hasDepth();
        int renderCount = getRenderedAmount(itemStack);
        IItemEntity rotator = (IItemEntity) event.itemEntity;
        boolean renderBlockFlat = false;

        if (event.itemEntity.getStack().getItem() instanceof BlockItem && !(event.itemEntity.getStack().getItem() instanceof AliasedBlockItem)) {
            Block block = ((BlockItem) event.itemEntity.getStack().getItem()).getBlock();
            VoxelShape shape = block.getOutlineShape(block.getDefaultState(), event.itemEntity.world, event.itemEntity.getBlockPos(), ShapeContext.absent());

            if (shape.getMax(Direction.Axis.Y) <= .5) renderBlockFlat = true;
        }

        Item item = event.itemEntity.getStack().getItem(); //how tf does physics work
        if (item instanceof BlockItem && !(item instanceof AliasedBlockItem) && !renderBlockFlat) event.matrixStack.translate(0, -0.06, 0);

        if (!renderBlockFlat) {
            event.matrixStack.translate(0, .185, .0);
            event.matrixStack.multiply(RotationAxis.POSITIVE_X.rotation(1.571F));
            event.matrixStack.translate(0, -.185, -.0);
        }

        boolean isAboveWater = event.itemEntity.world.getBlockState(event.itemEntity.getBlockPos()).getFluidState().getFluid().isIn(FluidTags.WATER);
        if (!event.itemEntity.isOnGround() && (!event.itemEntity.isSubmergedInWater() && !isAboveWater)) {
            float rotation = ((float) event.itemEntity.getItemAge() + event.tickDelta) / 20.0F + event.itemEntity.uniqueOffset; // Calculate rotation based on age and ticks

            if (!renderBlockFlat) {
                event.matrixStack.translate(0, .185, .0);
                event.matrixStack.multiply(RotationAxis.POSITIVE_Z.rotation(rotation));
                event.matrixStack.translate(0, -.185, .0);
                rotator.setRotation(new Vec3d(0, 0, rotation));
            } else {
                event.matrixStack.multiply(RotationAxis.POSITIVE_Y.rotation(rotation));
                rotator.setRotation(new Vec3d(0, rotation, 0));
                event.matrixStack.translate(0, -.065, 0);
            }

            if (event.itemEntity.getStack().getItem() instanceof AliasedBlockItem) event.matrixStack.translate(0, 0, .195);
            else if (!(event.itemEntity.getStack().getItem() instanceof BlockItem)) event.matrixStack.translate(0, 0, .195);
        }

        else if (event.itemEntity.getStack().getItem() instanceof AliasedBlockItem){
            event.matrixStack.translate(0, .185, .0);
            event.matrixStack.multiply(RotationAxis.POSITIVE_Z.rotation((float) rotator.getRotation().z));
            event.matrixStack.translate(0, -.185, .0);
            event.matrixStack.translate(0, 0, .195);
        }

        else if (renderBlockFlat) {
            event.matrixStack.multiply(RotationAxis.POSITIVE_Y.rotation((float) rotator.getRotation().y));
            event.matrixStack.translate(0, -.065, 0);
        }


        else {
            if (!(event.itemEntity.getStack().getItem() instanceof BlockItem)) event.matrixStack.translate(0, 0, .195);

            event.matrixStack.translate(0, .185, .0);
            event.matrixStack.multiply(RotationAxis.POSITIVE_Z.rotation((float) rotator.getRotation().z));
            event.matrixStack.translate(0, -.185, .0);
        }

        if (event.itemEntity.world.getBlockState(event.itemEntity.getBlockPos()).getBlock().equals(Blocks.SOUL_SAND)) event.matrixStack.translate(0, 0, -.1);

        if (event.itemEntity.getStack().getItem() instanceof BlockItem && ((BlockItem) event.itemEntity.getStack().getItem()).getBlock() instanceof SkullBlock) event.matrixStack.translate(0, .11, 0);

        float scaleX = bakedModel.getTransformation().ground.scale.x;
        float scaleY = bakedModel.getTransformation().ground.scale.y;
        float scaleZ = bakedModel.getTransformation().ground.scale.z;

        float x;
        float y;
        if (!hasDepthInGui) {
            float r = -0.0F * (float)(renderCount) * 0.5F * scaleX;
            x = -0.0F * (float)(renderCount) * 0.5F * scaleY;
            y = -0.09375F * (float)(renderCount) * 0.5F * scaleZ;
            event.matrixStack.translate(r, x, y);
        }

        for (int u = 0; u < renderCount; ++u) {
            event.matrixStack.push();
            if (u > 0) {
                if (hasDepthInGui) {
                    x = (event.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    y = (event.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    float z = (event.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    event.matrixStack.translate(x, y, z);
                } else {
                    x = (event.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
                    y = (event.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
                    event.matrixStack.translate(x, y, 0.0D);
                    event.matrixStack.multiply(RotationAxis.POSITIVE_Z.rotation(event.random.nextFloat()));
                }
            }

            event.itemRenderer.renderItem(itemStack, ModelTransformation.Mode.GROUND, false, event.matrixStack,event.vertexConsumerProvider, event.light, OverlayTexture.DEFAULT_UV, bakedModel);

            event.matrixStack.pop();

            if (!hasDepthInGui) event.matrixStack.translate(0.0F * scaleX, 0.0F * scaleY, 0.0625F * scaleZ);
        }

        event.matrixStack.pop();
        //mc.getEntityRenderDispatcher().getRenderer(event.itemEntity).render(event.itemEntity, event.f, event.tickDelta, event.matrixStack, event.vertexConsumerProvider, event.light);
        event.setCancelled(true);
    }

    private int getRenderedAmount(ItemStack stack) {
        int i = 1;

        if (stack.getCount() > 48) i = 5;
        else if (stack.getCount() > 32) i = 4;
        else if (stack.getCount() > 16) i = 3;
        else if (stack.getCount() > 1) i = 2;

        return i;
    }
}
