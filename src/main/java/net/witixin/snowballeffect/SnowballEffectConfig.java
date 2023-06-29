package net.witixin.snowballeffect;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;

public class SnowballEffectConfig {
    public static final ForgeConfigSpec GENERAL_SPEC;
    public static final ForgeConfigSpec.ConfigValue<Integer> UNSITTABLE_AGE;
    public static final ForgeConfigSpec.ConfigValue<Double> GROWTH_CONSTANT;
    public static final ForgeConfigSpec.ConfigValue<Integer> MAX_AGE;
    public static final ForgeConfigSpec.ConfigValue<Integer> MAX_FOLLOW_DIST;
    public static final ForgeConfigSpec.ConfigValue<Integer> MELTING_COOLDOWN_TICKS;
    public static final ForgeConfigSpec.ConfigValue<Integer> EATING_COOLDOWN_TICKS;

    public static final ForgeConfigSpec.ConfigValue<Integer> AGE_THAT_BREAKS_BLOCKS;

    static {
        ForgeConfigSpec.Builder configBuilder = new ForgeConfigSpec.Builder();
        configBuilder.comment("Igloof configurable values");
        configBuilder.push("Igloof");
        UNSITTABLE_AGE = configBuilder.comment("If the igloof is older than this value, the owner will not be able to order it to sit anymore")
                .defineInRange("igloof_unsittable_age", 30, 1, Integer.MAX_VALUE);
        GROWTH_CONSTANT = configBuilder.comment("To calculate it's size, this number ^ age is used.")
                .defineInRange("igloof_growth_constant", 1.08D, 1.0d, Double.MAX_VALUE);
        MAX_AGE = configBuilder.comment("The maximum age the igloof can grow to")
                .defineInRange("igloof_max_age", 40, 1, Integer.MAX_VALUE);
        MAX_FOLLOW_DIST =  configBuilder.comment("Used to calculate the maximum distance the igloof follows a player")
                .defineInRange("igloof_follow_factor", 30, 1, Integer.MAX_VALUE);
        MELTING_COOLDOWN_TICKS =  configBuilder.comment("The cooldown in ticks until a magic torch can be used to remove a layer from the Igloof")
                .defineInRange("igloof_melt_ticks", 1200*5, 1, Integer.MAX_VALUE);
        EATING_COOLDOWN_TICKS = configBuilder.comment("The cooldown in ticks until the igloof can eat snow from the ground in order to grow again")
                .defineInRange("igloof_eat_ticks", 1200*6, 1, Integer.MAX_VALUE);
        AGE_THAT_BREAKS_BLOCKS = configBuilder.comment("The age at which the Igloof starts breaking blocks. The blocks in question are configurable using the 'igloof_breakables' tag")
                .defineInRange("igloof_age_break", 25, 1, Integer.MAX_VALUE);
        configBuilder.pop();
        GENERAL_SPEC = configBuilder.build();
    }
}
