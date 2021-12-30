package net.witixin.snowballeffect.client;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;

public class TorchParticle extends RisingParticle {


    protected TorchParticle(ClientWorld p_108328_, double p_108329_, double p_108330_, double p_108331_, double p_108332_, double p_108333_, double p_108334_, IAnimatedSprite sprite) {
        super(p_108328_, p_108329_, p_108330_, p_108331_,0.1F, -0.1F, 0.1F, p_108332_, p_108333_, p_108334_, 0.4f, sprite, 0.5F, 20, -0.004D, false);
        this.lifetime = 10;
        this.gravity = 0.5F;
        this.gravity = 0.5F;
        this.xd *= 0.1F;
        this.yd *= 0.1F;
        this.zd *= 0.1F;
        this.xd += p_108332_ * 0.4D;
        this.yd += p_108333_ * 0.4D;
        this.zd += p_108334_ * 0.4D;
        this.quadSize *= 0.5F;
        this.lifetime = 50;
        this.hasPhysics = true;
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }


    public static class Provider implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite set;

        public Provider(IAnimatedSprite set){
            this.set = set;
        }

        @Override
        public Particle createParticle(BasicParticleType typeIn, ClientWorld level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            TorchParticle toReturn = new TorchParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, set);
            return toReturn;
        }
    }
}
