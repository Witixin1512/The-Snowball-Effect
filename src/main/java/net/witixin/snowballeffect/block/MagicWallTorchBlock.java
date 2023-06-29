package net.witixin.snowballeffect.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

public class MagicWallTorchBlock extends WallTorchBlock {

    final Supplier<SimpleParticleType> particleOptionsSupplier;

    public MagicWallTorchBlock(Properties properties, Supplier<SimpleParticleType> particleSupplier){
        //Hardcoded because I wish to use a custom particle without breaking other mods accessing fireParticle.
        super(properties, ParticleTypes.FLAME);
        this.particleOptionsSupplier = particleSupplier;
    }

    @Override
    public void animateTick(BlockState p_222660_, Level p_222661_, BlockPos p_222662_, RandomSource p_222663_){
        Direction direction = p_222660_.getValue(FACING);
        double d0 = (double)p_222662_.getX() + 0.5D;
        double d1 = (double)p_222662_.getY() + 0.7D;
        double d2 = (double)p_222662_.getZ() + 0.5D;
        Direction direction1 = direction.getOpposite();
        p_222661_.addParticle(ParticleTypes.SMOKE, d0 + 0.27D * (double)direction1.getStepX(), d1 + 0.22D, d2 + 0.27D * (double)direction1.getStepZ(), 0.0D, 0.0D, 0.0D);
        p_222661_.addParticle(this.particleOptionsSupplier.get(), d0 + 0.27D * (double)direction1.getStepX(), d1 + 0.22D, d2 + 0.27D * (double)direction1.getStepZ(), 0.0D, 0.0D, 0.0D);
    }
}
