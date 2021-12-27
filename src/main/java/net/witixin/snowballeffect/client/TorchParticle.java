package net.witixin.snowballeffect.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

public class TorchParticle extends TextureSheetParticle {


    protected TorchParticle(ClientLevel p_108328_, double p_108329_, double p_108330_, double p_108331_, double p_108332_, double p_108333_, double p_108334_) {
        super(p_108328_, p_108329_, p_108330_, p_108331_, p_108332_, p_108333_, p_108334_);
        this.lifetime = 10;
        this.gravity = 0.5F;
        this.friction = 0.7F;
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
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }


    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet set;

        public Provider(SpriteSet set){
            this.set = set;
        }

        @Override
        public Particle createParticle(SimpleParticleType typeIn, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            TorchParticle toReturn = new TorchParticle(level, x, y, z, xSpeed, ySpeed, zSpeed);
            toReturn.pickSprite(set);
            return toReturn;
        }
    }
}
