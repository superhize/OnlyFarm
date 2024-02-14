package be.hize.onlyfarm.mixin.transformers;

import be.hize.onlyfarm.utils.Utils;
import net.minecraft.block.BlockBush;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Taken and modified from Patcher under Creative Commons Attribution-NonCommercial-ShareAlike 4.0
 * https://github.com/Sk1erLLC/Patcher/blob/master/LICENSE.md
 *
 * @author Sk1erLLC
 */
@Mixin(BlockCrops.class)
public class BlockCrops extends BlockBush {
    //#if MC==10809
    @Override
    public AxisAlignedBB getSelectedBoundingBox(World worldIn, BlockPos pos) {
        Utils.INSTANCE.updateCropsMaxY(worldIn, pos, worldIn.getBlockState(pos).getBlock());
        return super.getSelectedBoundingBox(worldIn, pos);
    }

    @Override
    public MovingObjectPosition collisionRayTrace(World worldIn, BlockPos pos, Vec3 start, Vec3 end) {
        Utils.INSTANCE.updateCropsMaxY(worldIn, pos, worldIn.getBlockState(pos).getBlock());
        return super.collisionRayTrace(worldIn, pos, start, end);
    }
    //#endif
}
