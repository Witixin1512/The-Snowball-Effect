package net.witixin.snowballeffect;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.witixin.snowballeffect.entity.IgloofEntityRenderer;
import net.witixin.snowballeffect.client.TorchParticle;
import net.witixin.snowballeffect.registry.EntityRegistry;


@Mod.EventBusSubscriber(modid = Reference.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityRegistry.IGLOOF.get(), IgloofEntityRenderer::new);
    }
    @SubscribeEvent
    public static void particlesOrSomething(final ParticleFactoryRegisterEvent event){
        Minecraft.getInstance().particleEngine.register(Reference.MAGIC_TORCH_PARTICLE.get(), TorchParticle.Provider::new);
    }

}