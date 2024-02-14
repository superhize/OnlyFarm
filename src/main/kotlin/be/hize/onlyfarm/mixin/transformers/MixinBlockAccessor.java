package be.hize.onlyfarm.mixin.transformers;

import net.minecraft.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Block.class)
public interface MixinBlockAccessor {
    //#if MC==10809
    @Accessor
    void setMaxY(double maxY);
    //#endif
}
