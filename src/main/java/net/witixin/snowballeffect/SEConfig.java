package net.witixin.snowballeffect;

import net.minecraftforge.common.ForgeConfigSpec;

public class SEConfig {
    public static final ForgeConfigSpec GENERAL_SPEC;
    public static ForgeConfigSpec.ConfigValue<Integer> UNSITTABLE_AGE;
    public static ForgeConfigSpec.ConfigValue<Double> GROWTH_CONSTANT;
    public static ForgeConfigSpec.ConfigValue<Integer> MAX_AGE;
    public static ForgeConfigSpec.ConfigValue<Integer> MAX_FOLLOW_DIST;
    public static ForgeConfigSpec.ConfigValue<Integer> MELTING_COOLDOWN_TICKS;
    public static ForgeConfigSpec.ConfigValue<Integer> EATING_COOLDOWN_TICKS;

    static {
        ForgeConfigSpec.Builder configBuilder = new ForgeConfigSpec.Builder();
        setupConfig(configBuilder);
        GENERAL_SPEC = configBuilder.build();
    }

    private static void setupConfig(ForgeConfigSpec.Builder builder) {
        builder.comment("Igloof configurable values");
        builder.push("Igloof");
        UNSITTABLE_AGE = builder.comment("If the igloof is older than this value, the owner will not be able to order it to sit anymore").define("igloof_unsittable_age", 30);
        GROWTH_CONSTANT = builder.comment("To calculate it's size, this number ^ age is used.").define("igloof_growth_constant", 1.08D);
        MAX_AGE = builder.comment("The maximum age the igloof can grow to").define("igloof_max_age", 40);
        MAX_FOLLOW_DIST =  builder.comment("Used to calculate the maximum distance the igloof follows a player").define("igloof_follow_factor", 30);
        MELTING_COOLDOWN_TICKS =  builder.comment("The time in ticks until a magic torch can be used to remove a layer from the Igloof").define("igloof_melt_ticks", 1200*5);
        EATING_COOLDOWN_TICKS = builder.comment("The time in ticks until the igloof can eat snow from the ground in order to grow again").define("igloof_eat_ticks", 1200*6);

        builder.pop();
    }

}
