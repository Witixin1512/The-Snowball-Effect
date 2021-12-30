package net.witixin.snowballeffect;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.witixin.snowballeffect.registry.EntityRegistry;

@Mod.EventBusSubscriber(modid = Reference.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonEvents {

    @SubscribeEvent
    public static void registerEntityAttributes(final EntityAttributeCreationEvent event) {
        event.put(EntityRegistry.IGLOOF.get(), CreatureEntity.createMobAttributes().add(Attributes.MAX_HEALTH, 1.0D).add(Attributes.ATTACK_DAMAGE, 1.0D).add(Attributes.FOLLOW_RANGE, 300.0D).add(Attributes.MOVEMENT_SPEED, 0.25D).add(Attributes.ATTACK_SPEED, 2.5D).build());
    }


}
