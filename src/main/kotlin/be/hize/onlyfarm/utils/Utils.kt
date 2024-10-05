package be.hize.onlyfarm.utils

import be.hize.onlyfarm.OnlyFarmMod
import be.hize.onlyfarm.data.TitleManager
import be.hize.onlyfarm.mixin.transformers.MixinBlockAccessor
import net.minecraft.block.Block
import net.minecraft.block.BlockCarrot
import net.minecraft.block.BlockCrops
import net.minecraft.block.BlockNetherWart
import net.minecraft.block.BlockPotato
import net.minecraft.client.Minecraft
import net.minecraft.launchwrapper.Launch
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.common.FMLCommonHandler
import kotlin.time.Duration

object Utils {

    private val CARROT_POTATO_BOX = arrayOf(
        AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.125, 1.0),
        AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.1875, 1.0),
        AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.25, 1.0),
        AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.3125, 1.0),
        AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.375, 1.0),
        AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.4375, 1.0),
        AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0),
        AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.5625, 1.0)
    )

    private val WHEAT_BOX = arrayOf(
        AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.125, 1.0),
        AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.25, 1.0),
        AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.375, 1.0),
        AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0),
        AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.625, 1.0),
        AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.75, 1.0),
        AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.875, 1.0),
        AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)
    )

    private val NETHER_WART_BOX = arrayOf(
        AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.3125, 1.0),
        AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0),
        AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.6875, 1.0),
        AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.875, 1.0)
    )

    fun shutdownMinecraft(reason: String? = null) {
        System.err.println("OnlyFarm-${OnlyFarmMod.version} forced the game to shutdown.")
        reason?.let {
            System.err.println("Reason: $it")
        }
        FMLCommonHandler.instance().handleExit(-1)
    }

    fun isInDevEnviromen() = Launch.blackboard["fml.deobfuscatedEnvironment"] as Boolean

    fun updateCropsMaxY(world: World, pos: BlockPos, block: Block) {
        if (!HypixelUtils.onSkyblock) return
        val blockState = world.getBlockState(pos)
        val blockAccessor = block as MixinBlockAccessor
        val b = blockState.block
        val age = if (b is BlockNetherWart) blockState.getValue(BlockNetherWart.AGE) else blockState.getValue(BlockCrops.AGE)
        if (OnlyFarmMod.feature.farming.changeCropHitbox && Minecraft.getMinecraft().theWorld != null) {
            val box = when (b) {
                is BlockPotato,
                is BlockCarrot -> CARROT_POTATO_BOX

                is BlockNetherWart -> NETHER_WART_BOX
                else -> WHEAT_BOX
            }

            blockAccessor.setMaxY(box[age].maxY)
            return
        }
        blockAccessor.setMaxY(0.25)
    }

    fun sendTitle(text: String, duration: Duration, height: Double = 1.8, fontSize: Float = 4f) {
        TitleManager.sendTitle(text, duration, height, fontSize)
    }
}
