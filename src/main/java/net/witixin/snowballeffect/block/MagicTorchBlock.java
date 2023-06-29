package net.witixin.snowballeffect.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

public class MagicTorchBlock extends TorchBlock {

    private final Supplier<SimpleParticleType> particleOptionsSupplier;

    public MagicTorchBlock(Properties p_57491_, Supplier<SimpleParticleType> p_57492_){
        //Hardcoded because I wish to use a custom particle without breaking other mods accessing fireParticle.
        super(p_57491_, ParticleTypes.FLAME);
        this.particleOptionsSupplier = p_57492_;
    }

    @Override
    public void animateTick(BlockState p_222593_, Level p_222594_, BlockPos p_222595_, RandomSource p_222596_){
        double d0 = (double)p_222595_.getX() + 0.5D;
        double d1 = (double)p_222595_.getY() + 0.7D;
        double d2 = (double)p_222595_.getZ() + 0.5D;
        p_222594_.addParticle(ParticleTypes.SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
        p_222594_.addParticle(particleOptionsSupplier.get(), d0, d1, d2, 0.0D, 0.0D, 0.0D);
    }
}
