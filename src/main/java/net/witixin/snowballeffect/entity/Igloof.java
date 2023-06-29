package net.witixin.snowballeffect.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import net.witixin.snowballeffect.SnowballEffectConfig;
import net.witixin.snowballeffect.registry.BlockRegistry;
import net.witixin.snowballeffect.registry.EntityRegistry;
import net.witixin.snowballeffect.registry.ItemRegistry;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.HashMap;
import java.util.Map;

public class Igloof extends TamableAnimal implements GeoEntity {

    public static final TagKey<Block> IGLOOF_BREAKABLES_TAG = BlockRegistry.get().createTagKey("igloof_breakables");
    private static final EntityDataAccessor<Integer> IGLOOF_AGE = SynchedEntityData.defineId(Igloof.class,
            EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> IS_ICEY = SynchedEntityData.defineId(Igloof.class,
            EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_EATING = SynchedEntityData.defineId(Igloof.class,
            EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_SITTING = SynchedEntityData.defineId(Igloof.class,
            EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> EATING_COOLDOWN = SynchedEntityData.defineId(Igloof.class,
            EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> MELTING_COOLDOWN = SynchedEntityData.defineId(Igloof.class,
            EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> TICKS_UNTIL_EATING = SynchedEntityData.defineId(Igloof.class,
            EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> SNOW_COUNTER = SynchedEntityData.defineId(Igloof.class,
            EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> SNOW_FEED_COUNTER = SynchedEntityData.defineId(Igloof.class,
            EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> ICE_TICKS = SynchedEntityData.defineId(Igloof.class,
            EntityDataSerializers.INT);
    //TODO Add accessories
    private static final EntityDataAccessor<String> ACCESSORY = SynchedEntityData.defineId(Igloof.class,
            EntityDataSerializers.STRING);
    private static final RawAnimation SIT_ANIMATION = RawAnimation.begin().thenLoop("animation.model.sit");
    private static final RawAnimation WALK_ANIMATION = RawAnimation.begin().thenLoop("animation.model.walk");
    private static final RawAnimation CHOMP_ANIMATION = RawAnimation.begin().thenLoop("animation.model.chomp");
    private static final RawAnimation IDLE_ANIMATION = RawAnimation.begin().thenLoop("animation.model.idle");
    private static final RawAnimation ATTACK_ANIMATION = RawAnimation.begin().thenPlay("animation.model.attack");
    private static final Map<Block, Float> valueMap = new HashMap<>();
    private final AnimatableInstanceCache animatableInstanceCache = GeckoLibUtil.createInstanceCache(this);

    //Contains a Constant ^ age float that is used for rendering on the client side. Cached to avoid recomputing every frame.
    private float animatableSize = 1.0f;

    public Igloof(EntityType<? extends Igloof> entityType, Level level){
        super(EntityRegistry.IGLOOF.get(), level);
    }

    public static Igloof of(Level level, Player id){
        Igloof toReturn = new Igloof(EntityRegistry.IGLOOF.get(), level);
        toReturn.tame(id);
        return toReturn;
    }

    public static boolean matchesSnow(Block block){
        return valueMap.containsKey(block);
    }

    public static void registerEdibleBlock(Block block, float value) {
        valueMap.put(block, value);
    }

    public static void clearEdibles() {
        valueMap.clear();
    }


    /**
     * Only runs on the serverside, can be disabled with noAi
     */
    @Override
    public void customServerAiStep(){
        //this.updatePersistentAnger((ServerLevel)this.level(), true);
        if(!this.isOrderedToSit()) {
            if(getEatingCooldownTicks() > 0) {
                setEatingCooldownTicks(getEatingCooldownTicks() - 1);
            }
            if(getMeltingCooldownTicks() > 0) {
                setMeltingCooldownTicks(getMeltingCooldownTicks() - 1);
            }
            this.canStartToFeed(); //TODO Cleanup
            if(isEating()) {
                if(!(matchesSnow(this.level().getBlockState(this.getOnPos()).getBlock()))) {
                    this.setEating(false);
                    return;
                }
                if((getTicksTillEating() < 100)) {
                    this.setEatingTicks((this.getTicksTillEating() + 1));
                }
                if(this.getTicksTillEating() == 100) {
                    helpFeedOnBlock(this.getOnPos());
                }
            }
            if(getIceTicks() >= 600) {
                setIceTicks(0);
                this.setIcey(true);
                this.setEating(false);
            }
            if(this.getGloofAge() >= SnowballEffectConfig.AGE_THAT_BREAKS_BLOCKS.get()) {
                tryCheckInsideBlocks();
                if(ForgeRegistries.BLOCKS.tags().getTag(IGLOOF_BREAKABLES_TAG).contains(
                        this.getBlockStateOn().getBlock())) {
                    this.level().destroyBlock(this.getOnPos(), true);
                }
            }
        }
    }

    private void helpFeedOnBlock(BlockPos pos){
        this.setSnowCounter(this.getSnowCounter() + valueMap.get(this.level().getBlockState(pos).getBlock()));
        this.level().destroyBlock(pos, false);
        this.setEatingCooldownTicks(SnowballEffectConfig.EATING_COOLDOWN_TICKS.get());
        this.updateSnowFeedAmont();
        setEating(false);
        if(this.getSnowCounter() >= this.getSnowFeed()) {
            this.feed();
            this.updateSnowFeedAmont();
            this.setSnowCounter(0.0f);
        }
    }

    public void feed(){
        if(getGloofAge() == SnowballEffectConfig.MAX_AGE.get()) return;
        this.saveAge(getGloofAge() + 1);
        double maxHealthGrowth = (2D * getGloofAge()) / getIceReduction();
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(maxHealthGrowth);
        this.heal((float) maxHealthGrowth);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue((getGloofAge() % 3 == 0 ? 1 : 0) / getIceReduction());
        this.updateDims();
        this.refreshDimensions();
        this.setEatingCooldownTicks(SnowballEffectConfig.EATING_COOLDOWN_TICKS.get());
    }

    public void unfeed(){
        this.saveAge(getGloofAge() - 1);
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(2 * getGloofAge() / getIceReduction());
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue((this.getAttributeValue(
                Attributes.ATTACK_DAMAGE) + (getGloofAge() % 3 == 0 ? 1 : 0)) / getIceReduction());
        this.updateDims();
        this.refreshDimensions();
    }

    private int getIceReduction(){
        return isIcey() ? 2 : 1;
    }

    private void saveAge(int age){
        this.entityData.set(IGLOOF_AGE, age);
    }

    @Override
    public EntityDimensions getDimensions(Pose pose){
        return super.getDimensions(pose).scale(this.getSize());
    }

    public float getSize(){
        return animatableSize;
    }

    private void updateDims(){
        animatableSize = (float) Math.pow(SnowballEffectConfig.GROWTH_CONSTANT.get(), getGloofAge());
        this.dimensions = getDimensions(this.getPose());
        this.setBoundingBox(this.dimensions.makeBoundingBox(1.0D, 1.0D, 1.0D));
    }

    public boolean canStartToFeed(){
        if(isIcey()) return false;
        if(getEatingCooldownTicks() <= 0 && !this.isEating() && matchesSnow(this.level().getBlockState(this.getOnPos()).getBlock())) {
            setEatingTicks(0);
            this.setEating(true);
            return true;
        }
        return false;
    }

    @Override
    protected void registerGoals(){
        this.goalSelector.addGoal(5, new GloofFollowOwnerGoal(this, 1D, 20.0F, 1F, false));
        this.goalSelector.addGoal(1, new GloofSitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.4F));
        this.goalSelector.addGoal(4, new GloofMeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(5, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F, false));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(7, new GloofRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new GloofFloatGoal(this));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
    }

    private int getGloofAge(){
        return this.entityData.get(IGLOOF_AGE);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source){
        return super.isInvulnerableTo(source) || source.is(DamageTypeTags.IS_FREEZING) || isEntityDamageSourceSnowball(
                source);
    }

    private boolean isEntityDamageSourceSnowball(DamageSource source){
        return source.isIndirect() && source.getDirectEntity() instanceof Snowball;
    }

    @Override
    public InteractionResult mobInteract(Player p_30412_, InteractionHand p_30413_){
        if(p_30412_.level().isClientSide && p_30413_ == InteractionHand.OFF_HAND) return InteractionResult.PASS;
        if(this.getHealth() < this.getMaxHealth() && !this.isIcey() && p_30412_.getItemInHand(
                p_30413_).getItem() == ItemRegistry.MAGIC_COAL.get()) {
            this.heal(4.0f);
            p_30412_.getItemInHand(p_30413_).shrink(1);
            return InteractionResult.SUCCESS;

        }
        if(this.isIcey() && p_30412_.getItemInHand(p_30413_).getItem() == ItemRegistry.MAGIC_TORCH_ITEM.get()) {
            this.setIcey(false);
            p_30412_.getItemInHand(p_30413_).shrink(1);
            return InteractionResult.SUCCESS;
        }
        if(!this.isIcey() && this.getEatingCooldownTicks() < 1000 && this.getGloofAge() < 2 && p_30412_.getItemInHand(
                p_30413_).getItem() == Items.SNOWBALL && p_30412_.getItemInHand(p_30413_).getCount() >= 8) {
            this.feed();
            p_30412_.getItemInHand(p_30413_).shrink(8);
            return InteractionResult.SUCCESS;
        }
        if(!this.isIcey() && p_30412_.getItemInHand(
                p_30413_).getItem() == ItemRegistry.MAGIC_TORCH_ITEM.get() && this.getMeltingCooldownTicks() == 0 && this.getGloofAge() > 0) {
            this.unfeed();
            p_30412_.getItemInHand(p_30413_).shrink(1);
            this.setMeltingCooldownTicks(SnowballEffectConfig.MELTING_COOLDOWN_TICKS.get());
            return InteractionResult.SUCCESS;
        }
        if(this.isTame() && getGloofAge() <= SnowballEffectConfig.UNSITTABLE_AGE.get()) {
            if(this.isOwnedBy(p_30412_) || super.mobInteract(p_30412_, p_30413_).consumesAction()) {
                if(this.isOrderedToSit()) {
                    this.setOrderedToSit(false);
                    this.setRenderSitting(false);
                    this.setInSittingPose(false);
                    return InteractionResult.SUCCESS;
                }
                if(!isOrderedToSit()) {
                    this.setOrderedToSit(true);
                    this.setRenderSitting(true);
                    this.setInSittingPose(true);
                    this.navigation.stop();
                    this.setTarget(null);
                    return InteractionResult.SUCCESS;
                }

            }
        }
        return super.mobInteract(p_30412_, p_30413_);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob mob){
        return null;
    }

    public int getMaxFollowDist(){
        return SnowballEffectConfig.MAX_FOLLOW_DIST.get() + getGloofAge() * getGloofAge();
    }

    @Override
    public void onInsideBlock(BlockState state){
        if(this.getGloofAge() >= SnowballEffectConfig.AGE_THAT_BREAKS_BLOCKS.get() && ForgeRegistries.BLOCKS.tags().getTag(
                IGLOOF_BREAKABLES_TAG).contains(state.getBlock())) {
            destroyBlockInsideOn(state);
        }
    }

    private void destroyBlockInsideOn(BlockState state){
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        AABB aabb = this.getBoundingBox();
        BlockPos blockpos = BlockPos.containing(aabb.minX + 0.001D, aabb.minY + 0.001D, aabb.minZ + 0.001D);
        BlockPos blockpos1 = BlockPos.containing(aabb.maxX - 0.001D, aabb.maxY - 0.001D, aabb.maxZ - 0.001D);
        for(int i = blockpos.getX(); i <= blockpos1.getX(); ++i) {
            for(int j = blockpos.getY(); j <= blockpos1.getY(); ++j) {
                for(int k = blockpos.getZ(); k <= blockpos1.getZ(); ++k) {
                    blockpos$mutableblockpos.set(i, j, k);
                    BlockState blockstate = this.level().getBlockState(blockpos$mutableblockpos);
                    if(state.equals(blockstate)) {
                        this.level().destroyBlock(blockpos$mutableblockpos, true);
                        break;
                    }
                }
            }
        }
    }

    @Override
    protected void defineSynchedData(){
        super.defineSynchedData();
        this.entityData.define(IGLOOF_AGE, 0);
        this.entityData.define(IS_ICEY, false);
        this.entityData.define(IS_EATING, false);
        this.entityData.define(EATING_COOLDOWN, SnowballEffectConfig.EATING_COOLDOWN_TICKS.get());
        this.entityData.define(MELTING_COOLDOWN, SnowballEffectConfig.MELTING_COOLDOWN_TICKS.get());
        this.entityData.define(IS_SITTING, false);
        this.entityData.define(TICKS_UNTIL_EATING, 0);
        this.entityData.define(SNOW_COUNTER, 0.0f);
        this.entityData.define(SNOW_FEED_COUNTER, 0.0f);
        this.entityData.define(ICE_TICKS, 0);
        this.entityData.define(ACCESSORY, "NONE");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag){
        super.addAdditionalSaveData(tag);
        tag.putInt("GloofAge", this.getGloofAge());
        tag.putBoolean("GloofIcey", this.isIcey());
        tag.putBoolean("GloofEating", this.isEating());
        tag.putInt("GloofEatCooldown", this.getEatingCooldownTicks());
        tag.putInt("GloofMeltCooldown", this.getMeltingCooldownTicks());
        tag.putInt("GloofTicks", this.getTicksTillEating());
        tag.putFloat("GloofSnowCounter", this.getSnowCounter());
        tag.putFloat("GloofSnowFeed", this.getSnowFeed());
        tag.putInt("GloofIceTicks", this.getIceTicks());
        tag.putString("Accessory", this.getAccessory());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag){
        super.readAdditionalSaveData(tag);
        this.saveAge(tag.getInt("GloofAge"));
        this.setIcey(tag.getBoolean("GloofIcey"));
        this.setEating(tag.getBoolean("GloofEating"));
        this.setEating(tag.getBoolean("GloofAttacking"));
        this.setEatingCooldownTicks(tag.getInt("GloofEatCooldown"));
        this.setMeltingCooldownTicks(tag.getInt("GloofMeltCooldown"));
        this.setRenderSitting(tag.getBoolean("Sitting"));
        this.setEatingTicks(tag.getInt("GloofTicks"));
        this.setSnowCounter(tag.getFloat("GloofSnowCounter"));
        this.setSnowFeed(tag.getFloat("GloofSnowFeed"));
        this.setIceTicks(tag.getInt("GloofIceTicks"));
        this.setAccessory(tag.getString("Accessory"));
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> entityDataAccessor){
        super.onSyncedDataUpdated(entityDataAccessor);
        if (entityDataAccessor == IGLOOF_AGE) {
            this.updateDims();
        }
    }

    public boolean isRenderSitting(){
        return this.entityData.get(IS_SITTING);
    }

    public void setRenderSitting(boolean value){
        this.entityData.set(IS_SITTING, value);
    }

    public int getTicksTillEating(){
        return this.entityData.get(TICKS_UNTIL_EATING);
    }

    public void setEatingTicks(int i){
        this.entityData.set(TICKS_UNTIL_EATING, i);
    }

    public float getSnowCounter(){
        return this.entityData.get(SNOW_COUNTER);
    }

    public void setSnowCounter(float value){
        this.entityData.set(SNOW_COUNTER, value);
    }

    public float getSnowFeed(){
        return this.entityData.get(SNOW_FEED_COUNTER);
    }

    public void setSnowFeed(float value){
        this.entityData.set(SNOW_FEED_COUNTER, value);
    }

    public int getIceTicks(){
        return this.entityData.get(ICE_TICKS);
    }

    public void setIceTicks(int value){
        this.entityData.set(ICE_TICKS, value);
    }

    public String getAccessory(){
        return this.entityData.get(ACCESSORY);
    }

    public void setAccessory(String accessory){
        this.entityData.set(ACCESSORY, accessory);
    }

    @Override
    public boolean canBeLeashed(Player p_30396_){
        return super.canBeLeashed(p_30396_) && getGloofAge() <= SnowballEffectConfig.UNSITTABLE_AGE.get();
    }

    @Override
    public boolean canMate(Animal animal){
        return false;
    }

    @Override
    public boolean canBreed(){
        return false;
    }

    public boolean isIcey(){
        return this.entityData.get(IS_ICEY);
    }

    public void setIcey(boolean bool){
        this.entityData.set(IS_ICEY, bool);
    }

    public boolean isEating(){
        return this.entityData.get(IS_EATING);
    }

    public void setEating(boolean bool){
        this.entityData.set(IS_EATING, bool);
    }

    public int getEatingCooldownTicks(){
        return this.entityData.get(EATING_COOLDOWN);
    }

    public void setEatingCooldownTicks(int i){
        this.entityData.set(EATING_COOLDOWN, i);
    }

    public int getMeltingCooldownTicks(){
        return this.entityData.get(MELTING_COOLDOWN);
    }

    public void setMeltingCooldownTicks(int i){
        this.entityData.set(MELTING_COOLDOWN, i);
    }

    private void updateSnowFeedAmont(){
        this.setSnowFeed((float) getGloofAge() * 3);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers){
        controllers.add(new AnimationController<>(this, "main_controller", 5, this::animationHandler)
                .triggerableAnim("attack", ATTACK_ANIMATION));
    }

    private PlayState animationHandler(AnimationState<Igloof> animationState){
        final Igloof igloof = animationState.getAnimatable();
        if(igloof.isEating()) {
            return animationState.setAndContinue(CHOMP_ANIMATION);
        }
        if((igloof.isRenderSitting())) {
            return animationState.setAndContinue(SIT_ANIMATION);
        }
        if(animationState.isMoving() && !igloof.isOrderedToSit()) {
            return animationState.setAndContinue(WALK_ANIMATION);
        }
        else {
            return animationState.setAndContinue(IDLE_ANIMATION);
        }
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache(){
        return animatableInstanceCache;
    }

    private static class GloofSitWhenOrderedToGoal extends SitWhenOrderedToGoal {

        private final Igloof entity;

        public GloofSitWhenOrderedToGoal(Igloof entity){
            super(entity);
            this.entity = entity;
        }

        @Override
        public boolean canUse(){
            return entity.getGloofAge() <= 30 && super.canUse();
        }
    }

    private static class GloofFloatGoal extends FloatGoal {

        private final Igloof gloof;

        public GloofFloatGoal(Igloof gloof){
            super(gloof);
            this.gloof = gloof;
        }

        @Override
        public boolean canUse(){
            return super.canUse();
        }

        @Override
        public void tick(){
            super.tick();
            this.gloof.setIceTicks(this.gloof.getIceTicks() + 1);
        }

    }

    private static class GloofRandomStrollGoal extends RandomStrollGoal {
        public GloofRandomStrollGoal(PathfinderMob p_25734_, double p_25735_){
            super(p_25734_, p_25735_);
        }

        @Override
        public boolean canUse(){
            if(this.mob.isVehicle()) {
                return false;
            } else {
                if(!this.forceTrigger) {
                    if(checkNoActionTime && this.mob.getNoActionTime() >= 100) {
                        return false;
                    }

                    if(this.mob.getRandom().nextInt(reducedTickDelay(this.interval)) != 0) {
                        return false;
                    }
                }

                Vec3 vec3 = this.getPosition();
                if(vec3 == null) {
                    return false;
                } else {
                    this.wantedX = vec3.x + ((Igloof) (this.mob)).getGloofAge() / 1.5;
                    this.wantedY = vec3.y;
                    this.wantedZ = vec3.z + ((Igloof) (this.mob)).getGloofAge() / 1.5;
                    this.forceTrigger = false;
                    return true;
                }
            }
        }
    }

    private static class GloofFollowOwnerGoal extends FollowOwnerGoal {

        private final double speedMod;

        public GloofFollowOwnerGoal(TamableAnimal p_25294_, double p_25295_, float p_25296_, float p_25297_, boolean p_25298_){
            super(p_25294_, p_25295_, p_25296_, p_25297_, p_25298_);
            this.speedMod = p_25295_;
        }

        @Override
        public void tick(){
            this.tamable.getLookControl().setLookAt(this.owner, 10.0F, (float) this.tamable.getMaxHeadXRot());
            if(--this.timeToRecalcPath <= 0) {
                this.timeToRecalcPath = this.adjustedTickDelay(10);
                if(!this.tamable.isLeashed() && !this.tamable.isPassenger()) {
                    if(this.tamable.distanceToSqr(this.owner) >= ((Igloof) (tamable)).getMaxFollowDist()) {
                        this.teleportToOwner();
                    } else {
                        tamable.getNavigation().moveTo(this.owner, speedMod);
                    }

                }
            }
        }

        protected int adjustedTickDelay(int p_186072_){
            return this.requiresUpdateEveryTick() ? p_186072_ : reducedTickDelay(p_186072_);
        }

        @Override
        public boolean canUse(){
            LivingEntity livingentity = this.tamable.getOwner();
            if(livingentity == null) {
                return false;
            } else if(livingentity.isSpectator()) {
                return false;
            } else if(this.tamable.isOrderedToSit()) {
                return false;
            } else if(this.tamable.distanceToSqr(livingentity) < (Math.pow(
                    ((Igloof) this.tamable).getMaxFollowDist(), 2))) {
                return false;
            } else {
                this.owner = livingentity;
                return true;
            }
        }

        @Override
        public boolean canContinueToUse(){
            if(this.tamable.getNavigation().isDone()) {
                return false;
            } else if(this.tamable.isOrderedToSit()) {
                return false;
            } else {
                return !(this.tamable.distanceToSqr(this.owner) <= ((Igloof) this.tamable).getMaxFollowDist());
            }
        }
    }

    private static class GloofMeleeAttackGoal extends MeleeAttackGoal {

        public GloofMeleeAttackGoal(PathfinderMob p_25552_, double p_25553_, boolean p_25554_){
            super(p_25552_, p_25553_, p_25554_);
        }


        @Override
        protected void checkAndPerformAttack(LivingEntity target, double distance){
            double d0 = this.getAttackReachSqr(target);
            if (distance <= d0) {
                if (this.getTicksUntilNextAttack() == 5) {
                    ((Igloof) this.mob).triggerAnim("main_controller", "attack");
                }
                if (this.getTicksUntilNextAttack() <= 0) {
                    this.resetAttackCooldown();
                    this.mob.swing(InteractionHand.MAIN_HAND);
                    this.mob.doHurtTarget(target);

                    BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
                    AABB aabb = this.mob.getBoundingBox();
                    BlockPos blockpos = BlockPos.containing(aabb.minX + 0.001D, aabb.minY + 0.001D, aabb.minZ + 0.001D);
                    BlockPos blockpos1 = BlockPos.containing(aabb.maxX - 0.001D, aabb.maxY - 0.001D, aabb.maxZ - 0.001D);
                    for(int i = blockpos.getX(); i <= blockpos1.getX() + (getAttackRange() * getXModifier()); ++i) {
                        for(int j = blockpos.getY(); j <= blockpos1.getY() + getAttackRange(); ++j) {
                            for(int k = blockpos.getZ(); k <= blockpos1.getZ() + (getAttackRange() * getZModifier()); ++k) {
                                blockpos$mutableblockpos.set(i, j, k);
                                BlockState blockstate = this.mob.level().getBlockState(blockpos$mutableblockpos);
                                if(ForgeRegistries.BLOCKS.tags().getTag(IGLOOF_BREAKABLES_TAG).contains(
                                        blockstate.getBlock())) {
                                    this.mob.level().destroyBlock(blockpos$mutableblockpos, true);
                                }
                            }
                        }
                    }
                }
            }
        }

        private int getAttackRange(){
            return ((Igloof) this.mob).getGloofAge() / 5;
        }

        private Vec3i getModifier(){
            return this.mob.getDirection().getNormal();
        }

        private int getXModifier(){
            return getModifier().getX();
        }

        private int getZModifier(){
            return getModifier().getZ();
        }
    }
}


