package net.witixin.igloof;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Reference.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonEvents {

    @SubscribeEvent
    public static void registerEntityAttributes(final EntityAttributeCreationEvent event) {
        System.out.println("Registered Attributes for Igloof");
        event.put(Reference.IGLOOF.get(), Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 2.0D).add(Attributes.ATTACK_DAMAGE, 1.0D).add(Attributes.FOLLOW_RANGE, 300.0D).add(Attributes.MOVEMENT_SPEED, 0.25D).add(Attributes.ATTACK_SPEED, 2.5D).build());
    }
}
