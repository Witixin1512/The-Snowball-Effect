package net.witixin.snowballeffect;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.witixin.snowballeffect.client.TorchParticle;
import net.witixin.snowballeffect.client.igloof.IgloofRenderer;
import net.witixin.snowballeffect.registry.EntityRegistry;


@Mod.EventBusSubscriber(modid = SnowballEffect.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityRegistry.IGLOOF.get(), IgloofRenderer::new);
    }
    @SubscribeEvent
    public static void particlesOrSomething(final RegisterParticleProvidersEvent event){
        event.registerSpriteSet(SnowballEffect.MAGIC_TORCH_PARTICLE.get(), TorchParticle.Provider::new);
    }

}