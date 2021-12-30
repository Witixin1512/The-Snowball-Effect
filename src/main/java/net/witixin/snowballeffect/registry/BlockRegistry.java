package net.witixin.snowballeffect.registry;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.particles.ParticleTypes;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.witixin.snowballeffect.Reference;

import static net.witixin.snowballeffect.Reference.MODID;

public class BlockRegistry {

    public static final DeferredRegister<Block> BLOCK_REG = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final RegistryObject<Block> MAGIC_TORCH = BLOCK_REG.register("magic_torch_floor", () ->
            new TorchBlock(AbstractBlock.Properties.of(Material.DECORATION).noCollission().instabreak().lightLevel((lightLevel) -> {return 15;}).sound(SoundType.WOOD), Reference.MAGIC_TORCH_PARTICLE.orElseGet(() -> ParticleTypes.FLAME)));
    public static final RegistryObject<Block> WALL_MAGIC_TORCH = BLOCK_REG.register("magic_torch_standing", () ->
            new WallTorchBlock(AbstractBlock.Properties.of(Material.DECORATION).noCollission().instabreak().lightLevel((lightLevel) -> {return 15;}).sound(SoundType.WOOD).dropsLike(MAGIC_TORCH.get()), Reference.MAGIC_TORCH_PARTICLE.orElseGet(() -> ParticleTypes.FLAME)));

    public static DeferredRegister<?> get(){
        return BLOCK_REG;
    }
}
