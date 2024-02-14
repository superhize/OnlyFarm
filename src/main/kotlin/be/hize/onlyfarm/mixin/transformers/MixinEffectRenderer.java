package be.hize.onlyfarm.mixin;

import be.hize.onlyfarm.OnlyFarmMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Taken from Patcher under Creative Commons Attribution-NonCommercial-ShareAlike 4.0
 * https://github.com/Sk1erLLC/Patcher/blob/master/LICENSE.md
 *
 * @author Sk1erLLC
 */
@Mixin(net.minecraft.client.particle.EffectRenderer.class)
public class EffectRenderer {
    @Inject(
            method = {
                    "addBlockDestroyEffects",
                    "addBlockHitEffects(Lnet/minecraft/util/BlockPos;Lnet/minecraft/util/EnumFacing;)V"
            }, at = @At("HEAD"), cancellable = true
    )
    private void onlyfarm$removeBlockBreakingParticles(CallbackInfo ci) {
        if (OnlyFarmMod.Companion.getFeature().farming.removeBreakParticle) {
            ci.cancel();
        }
    }

    @Inject(
            method = "addBlockHitEffects(Lnet/minecraft/util/BlockPos;Lnet/minecraft/util/MovingObjectPosition;)V",
            at = @At("HEAD"), cancellable = true, remap = false
    )
    private void onlyfarm$removeBlockBreakingParticles_Forge(CallbackInfo ci) {
        if (OnlyFarmMod.Companion.getFeature().farming.removeBreakParticle) {
            ci.cancel();
        }
    }
}
