package net.witixin.snowballeffect.registry;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.witixin.snowballeffect.SnowballEffect;
import net.witixin.snowballeffect.block.MagicTorchBlock;
import net.witixin.snowballeffect.block.MagicWallTorchBlock;

import static net.witixin.snowballeffect.SnowballEffect.MODID;

public class BlockRegistry {


    public static final DeferredRegister<Block> BLOCK_REG = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final RegistryObject<Block> MAGIC_TORCH = BLOCK_REG.register("magic_torch_floor", () ->
            new MagicTorchBlock(BlockBehaviour.Properties.of().noCollission().instabreak().lightLevel((lightLevel) -> 15)
                    .sound(SoundType.WOOD), SnowballEffect.MAGIC_TORCH_PARTICLE));
    public static final RegistryObject<Block> WALL_MAGIC_TORCH = BLOCK_REG.register("magic_torch_standing", () ->
            new MagicWallTorchBlock(BlockBehaviour.Properties.of().noCollission().instabreak().lightLevel((lightLevel) -> 15)
                    .sound(SoundType.WOOD).lootFrom(MAGIC_TORCH), SnowballEffect.MAGIC_TORCH_PARTICLE));

    public static DeferredRegister<Block> get(){
        return BLOCK_REG;
    }
}
