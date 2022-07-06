package net.witixin.snowballeffect.registry;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.witixin.snowballeffect.Reference;
import net.witixin.snowballeffect.entity.EntityIgloof;
import net.witixin.snowballeffect.entity.EntitySantaHat;

public class EntityRegistry{


    public static final DeferredRegister<EntityType<?>> ENTITY_REG = DeferredRegister.create(ForgeRegistries.ENTITIES, Reference.MODID);
    public static final RegistryObject<EntityType<EntityIgloof>> IGLOOF = ENTITY_REG.register("igloof", () -> EntityType.Builder.of(EntityIgloof::new, MobCategory.CREATURE).sized(0.6F, 0.85F).clientTrackingRange(10).build("igloof"));
    //public static final RegistryObject<EntityType<EntitySantaHat>> SANTA_HAT = ENTITY_REG.register("santa_hat", () -> EntityType.Builder.of(EntitySantaHat::new, MobCategory.MISC).build("santa_hat"));
    public static DeferredRegister<?> get() {
        return ENTITY_REG;
    }
}


