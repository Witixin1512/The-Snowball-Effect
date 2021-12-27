package net.witixin.snowballeffect.registry;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.witixin.snowballeffect.Reference;

import static net.witixin.snowballeffect.Reference.MODID;

public class BlockRegistry {


    public static final DeferredRegister<Block> BLOCK_REG = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final RegistryObject<Block> MAGIC_TORCH = BLOCK_REG.register("magic_torch_floor", () ->
            new TorchBlock(BlockBehaviour.Properties.of(Material.DECORATION).noCollission().instabreak().lightLevel((lightLevel) -> {return 15;}).sound(SoundType.WOOD), Reference.MAGIC_TORCH_PARTICLE.orElseGet(() -> ParticleTypes.FLAME)));
    public static final RegistryObject<Block> WALL_MAGIC_TORCH = BLOCK_REG.register("magic_torch_standing", () ->
            new WallTorchBlock(BlockBehaviour.Properties.of(Material.DECORATION).noCollission().instabreak().lightLevel((lightLevel) -> {return 15;}).sound(SoundType.WOOD).dropsLike(MAGIC_TORCH.get()), Reference.MAGIC_TORCH_PARTICLE.orElseGet(() -> ParticleTypes.FLAME)));

    public static DeferredRegister<?> get(){
        return BLOCK_REG;
    }
}
