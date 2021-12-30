package net.witixin.snowballeffect.entity;


import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.command.impl.data.EntityDataAccessor;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.SnowballEntity;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.Tags;
import net.witixin.snowballeffect.Reference;
import net.witixin.snowballeffect.SEConfig;
import net.witixin.snowballeffect.registry.EntityRegistry;
import net.witixin.snowballeffect.registry.ItemRegistry;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static net.witixin.snowballeffect.entity.EntityIgloof.GloofRandomStrollGoal.reducedTickDelay;

public class EntityIgloof extends TameableEntity implements IAngerable, IAnimatable {



    public static final Tags.IOptionalNamedTag<Block> GLOOF_BREAKABLES_TAG = BlockTags.createOptional(Reference.rl("blocks/gloof_breakables"));

    private static final int UNSITTABLE_AGE = SEConfig.UNSITTABLE_AGE.get();
    private static final double GROWTH_CONSTANT = SEConfig.GROWTH_CONSTANT.get();
    private static final int MAX_AGE = SEConfig.MAX_AGE.get();
    private static final int MAX_FOLLOW_DIST = SEConfig.MAX_FOLLOW_DIST.get();
    private static final int MELTING_COOLDOWN_TICKS = SEConfig.MELTING_COOLDOWN_TICKS.get();
    private static final int EATING_COOLDOWN_TICKS = SEConfig.EATING_COOLDOWN_TICKS.get();

    public static Map<Block, Float> valueMap = new HashMap<>();

    private static final DataParameter<Integer> SYNCHED_DATA_AGE = EntityDataManager.defineId(EntityIgloof.class, DataSerializers.INT);
    private static final DataParameter<Boolean> ICEY_DATA = EntityDataManager.defineId(EntityIgloof.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> EATING_DATA = EntityDataManager.defineId(EntityIgloof.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> ATTACKING_DATA = EntityDataManager.defineId(EntityIgloof.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> RENDER_SITTING = EntityDataManager.defineId(EntityIgloof.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> EATING_COOLDOWN = EntityDataManager.defineId(EntityIgloof.class, DataSerializers.INT);
    private static final DataParameter<Integer> MELTING_COOLDOWN = EntityDataManager.defineId(EntityIgloof.class, DataSerializers.INT);
    private static final DataParameter<Integer> TICKS_TILL_EATING = EntityDataManager.defineId(EntityIgloof.class, DataSerializers.INT);
    private static final DataParameter<Float> SNOW_COUNTER = EntityDataManager.defineId(EntityIgloof.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> SNOW_FEED_COUNTER = EntityDataManager.defineId(EntityIgloof.class, DataSerializers.FLOAT);
    private static final DataParameter<Integer> ICE_TICKS = EntityDataManager.defineId(EntityIgloof.class, DataSerializers.INT);

    private AnimationFactory factory = new AnimationFactory(this);

    public EntityIgloof(EntityType<? extends EntityIgloof> entityType, World level) {
        super(EntityRegistry.IGLOOF.get(), level);

    }

    public static void setupValueMap(){
        valueMap.put(Blocks.SNOW, 0.5f);
        valueMap.put(Blocks.SNOW_BLOCK, 4.0f);
    }

    public static EntityIgloof of(World level, PlayerEntity id){
        EntityIgloof toReturn = new EntityIgloof(EntityRegistry.IGLOOF.get(), level);
        toReturn.tame(id);
        return toReturn;
    }

    @Override
    public void tick(){
        super.tick();
        if (!this.level.isClientSide && !this.isOrderedToSit()){
            if (getEatingCooldownTicks() > 0){
                setEatingCooldownTicks(getEatingCooldownTicks() - 1);
            }
            if (getMeltingCooldownTicks() > 0){
                setMeltingCooldownTicks(getMeltingCooldownTicks() - 1);
            }
            canStartToFeed();
            if (isEating()){
                if (!(matchesSnow(this.level.getBlockState(this.getOnPos()).getBlock()))){
                    this.setEating(false);
                    return;
                }
                if ((getTicksTillEating() < 100)){
                    this.setEatingTicks((this.getTicksTillEating() + 1));
                }
                if (this.getTicksTillEating() == 100){
                    helpFeedOnBlock(this.getOnPos());
                }
            }
            if (getIceTicks() >= 600){
                setIceTicks(0);
                this.setIcey(true);
                this.setEating(false);
            }
        }
    }

    private void helpFeedOnBlock(BlockPos pos){
        this.setSnowCounter(this.getSnowCounter() + valueMap.get(this.level.getBlockState(pos).getBlock()));
        this.level.destroyBlock(pos, false);
        this.setEatingCooldownTicks(EATING_COOLDOWN_TICKS);
        this.updateSnowFeedAmont();
        setEating(false);
        if (this.getSnowCounter() >= this.getSnowFeed()){
            this.feed();
            this.updateSnowFeedAmont();
            this.setSnowCounter(0.0f);
        }
    }

    public void feed(){
        if (getGloofAge() == MAX_AGE)    return;
        this.saveAge(getGloofAge() + 1);
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue((2* getGloofAge()) / getIceReduction());
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue((getGloofAge() % 3 == 0 ? 1 : 0) / getIceReduction() );
        this.updateDims();
        this.refreshDimensions();
        this.setEatingCooldownTicks(EATING_COOLDOWN_TICKS);
    }

    public void unfeed(){
        this.saveAge(getGloofAge() -1);
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(2* getGloofAge() / getIceReduction());
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue((this.getAttributeValue(Attributes.ATTACK_DAMAGE) + (getGloofAge() % 3 == 0 ? 1 : 0)) / getIceReduction() );
        this.updateDims();
        this.refreshDimensions();
    }

    private int getIceReduction(){
        return isIcey() ? 2 : 1;
    }

    private void saveAge(int age){
        this.entityData.set(SYNCHED_DATA_AGE, age);
    }


    @Override
    public EntitySize getDimensions(Pose p_33597_) {
        return super.getDimensions(p_33597_).scale(this.getSize());
    }

    public float getSize(){
        return (float) Math.pow(GROWTH_CONSTANT ,getGloofAge());
    }

    private void updateDims(){
        this.dimensions = getDimensions(this.getPose());
        this.setBoundingBox(this.dimensions.makeBoundingBox(1.0D, 1.0D, 1.0D));
    }

    public boolean canStartToFeed() {
        if (isIcey()) return false;
        if (getEatingCooldownTicks() <= 0 && !this.isEating() && matchesSnow(this.level.getBlockState(this.getOnPos()).getBlock())){
            setEatingTicks(0);
            this.setEating(true);
            return true;
        }
        return false;
    }
    public boolean canFeedOnBlock(){
        return matchesSnow(this.level.getBlockState(this.getOnPos()).getBlock());
    }

    @Override
    protected void registerGoals(){
        this.goalSelector.addGoal(5, new GloofFollowOwnerGoal(this, 1D, 20.0F, 1F, false));
        this.goalSelector.addGoal(1, new GloofSitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.4F));
        this.goalSelector.addGoal(4, new GloofMeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(5, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F, false));
        this.goalSelector.addGoal(3, new LookRandomlyGoal(this));
        this.goalSelector.addGoal(7, new GloofRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new GloofFloatGoal(this));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
    }

    private int getGloofAge(){
        int ageGloof = this.entityData.get(SYNCHED_DATA_AGE);
        return ageGloof;
    }
    @Override
    public boolean isInvulnerableTo(DamageSource source){
        return super.isInvulnerableTo(source)  || isEntityDamageSourceSnowball(source);
    }
    private boolean isEntityDamageSourceSnowball(DamageSource source){
        return source instanceof IndirectEntityDamageSource && source.getDirectEntity() instanceof SnowballEntity;
    }

    @Override
    public ActionResultType mobInteract(PlayerEntity p_30412_, Hand p_30413_) {
        if (p_30412_.level.isClientSide && p_30413_ == Hand.OFF_HAND)return ActionResultType.PASS;
            if (this.getHealth() < this.getMaxHealth() && !this.isIcey() && p_30412_.getItemInHand(p_30413_).getItem() == ItemRegistry.MAGIC_COAL.get()){
                this.heal(4.0f);
                p_30412_.getItemInHand(p_30413_).shrink(1);
                return ActionResultType.SUCCESS.SUCCESS;

            }
            if (this.isIcey() && p_30412_.getItemInHand(p_30413_).getItem() == ItemRegistry.MAGIC_TORCH_ITEM.get()){
                this.setIcey(false);
                p_30412_.getItemInHand(p_30413_).shrink(1);
                return ActionResultType.SUCCESS;
            }
            if (!this.isIcey() && this.getEatingCooldownTicks() < 1000 && this.getGloofAge() < 2 && p_30412_.getItemInHand(p_30413_).getItem() == Items.SNOWBALL && p_30412_.getItemInHand(p_30413_).getCount() >= 8){
                this.feed();
                p_30412_.getItemInHand(p_30413_).shrink(8);
                return ActionResultType.SUCCESS;
            }
            if (!this.isIcey() && p_30412_.getItemInHand(p_30413_).getItem() == ItemRegistry.MAGIC_TORCH_ITEM.get() && this.getMeltingCooldownTicks() == 0 && this.getGloofAge() > 0){
                this.unfeed();
                p_30412_.getItemInHand(p_30413_).shrink(1);
                this.setMeltingCooldownTicks(MELTING_COOLDOWN_TICKS);
                return ActionResultType.SUCCESS;
            }
            if (this.isTame() && getGloofAge() <= UNSITTABLE_AGE){
                if (this.isOwnedBy(p_30412_) || super.mobInteract(p_30412_, p_30413_).consumesAction()){
                    if (this.isOrderedToSit()){
                        this.setOrderedToSit(false);
                        this.setRenderSitting(false);
                        this.setInSittingPose(false);
                        return ActionResultType.SUCCESS;
                    }
                    if (!isOrderedToSit()){
                        this.setOrderedToSit(true);
                        this.setRenderSitting(true);
                        this.setInSittingPose(true);
                        this.navigation.stop();
                        this.setTarget(null);
                        return ActionResultType.SUCCESS;
                    }

                }
            }
        return super.mobInteract(p_30412_, p_30413_);
    }


    public int getMaxFollowDist(){
        return MAX_FOLLOW_DIST + getGloofAge() * getGloofAge() ;
    }

    @Override
    public void onInsideBlock(BlockState state) {
         if (this.getGloofAge() >= 25 && GLOOF_BREAKABLES_TAG.contains(state.getBlock())){
             destroyBlockInsideOn(state);
         }
    }
    private void destroyBlockInsideOn(BlockState state){
        BlockPos.Mutable blockpos$mutableblockpos = new BlockPos.Mutable();
        AxisAlignedBB aabb = this.getBoundingBox();
        BlockPos blockpos = new BlockPos(aabb.minX + 0.001D, aabb.minY + 0.001D, aabb.minZ + 0.001D);
        BlockPos blockpos1 = new BlockPos(aabb.maxX - 0.001D, aabb.maxY - 0.001D, aabb.maxZ - 0.001D);
        for(int i = blockpos.getX(); i <= blockpos1.getX(); ++i) {
            for (int j = blockpos.getY(); j <= blockpos1.getY(); ++j) {
                for (int k = blockpos.getZ(); k <= blockpos1.getZ(); ++k) {
                    blockpos$mutableblockpos.set(i, j, k);
                    BlockState blockstate = this.level.getBlockState(blockpos$mutableblockpos);
                    if (state.equals(blockstate)) {
                        this.level.destroyBlock(blockpos$mutableblockpos, true);
                        System.out.println("Found a block");
                        break;
                    }
                }
            }
        }
    }


    @Nullable
    @Override
    public AgeableEntity getBreedOffspring(ServerWorld p_241840_1_, AgeableEntity p_241840_2_) {
        return null;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SYNCHED_DATA_AGE, 0);
        this.entityData.define(ICEY_DATA, false);
        this.entityData.define(EATING_DATA, false);
        this.entityData.define(ATTACKING_DATA, false);
        this.entityData.define(EATING_COOLDOWN, EATING_COOLDOWN_TICKS);
        this.entityData.define(MELTING_COOLDOWN, MELTING_COOLDOWN_TICKS);
        this.entityData.define(RENDER_SITTING, false);
        this.entityData.define(TICKS_TILL_EATING, 0);
        this.entityData.define(SNOW_COUNTER, 0.0f);
        this.entityData.define(SNOW_FEED_COUNTER, 0.0f);
        this.entityData.define(ICE_TICKS, 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("GloofAge", this.getGloofAge());
        tag.putBoolean("GloofIcey", this.isIcey());
        tag.putBoolean("GloofEating", this.isEating());
        tag.putBoolean("GloofAttacking", this.isAttackingData());
        tag.putInt("GloofEatCooldown", this.getEatingCooldownTicks());
        tag.putInt("GloofMeltCooldown", this.getMeltingCooldownTicks());
        tag.putInt("GloofTicks", this.getTicksTillEating());
        tag.putFloat("GloofSnowCounter", this.getSnowCounter());
        tag.putFloat("GloofSnowFeed", this.getSnowFeed());
        tag.putInt("GloofIceTicks", this.getIceTicks());
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT tag) {
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
    }

    public boolean isRenderSitting(){
        return this.entityData.get(RENDER_SITTING);
    }
    public void setRenderSitting(boolean value){
        this.entityData.set(RENDER_SITTING, value);
    }

    public int getTicksTillEating(){
        return this.entityData.get(TICKS_TILL_EATING);
    }
    public void setEatingTicks(int i){
        this.entityData.set(TICKS_TILL_EATING, i);
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
    public int getIceTicks(){return this.entityData.get(ICE_TICKS);}
    public void setIceTicks(int value){this.entityData.set(ICE_TICKS, value);}

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level.isClientSide) {
            this.updatePersistentAnger((ServerWorld) this.level, true);
        }

    }
    @Override
    public boolean canBeLeashed(PlayerEntity p_30396_) {
        return !this.isAngry() && super.canBeLeashed(p_30396_) && getGloofAge() <= UNSITTABLE_AGE;
    }
    @Override
    public boolean canMate(AnimalEntity animal){
        return false;
    }

    @Override
    public boolean canBreed() {
        return false;
    }


    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        if (event.getAnimatable() instanceof EntityIgloof){
            EntityIgloof gloof = (EntityIgloof) event.getAnimatable();
            if ((gloof.isRenderSitting())){
                event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.model.sit", true));
                return PlayState.CONTINUE;
            }
            if (event.isMoving() && !gloof.isOrderedToSit() && !this.isAttackingData()){
                event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.model.walk", true));
                return PlayState.CONTINUE;
            }
            if (gloof.isEating()){
                event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.model.chomp", true));
                return PlayState.CONTINUE;
            }
            if (gloof.isAttackingData() && gloof.getTarget() != null && gloof.getOnPos().distSqr(gloof.getTarget().blockPosition()) < 3) {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.model.attack", false));
                return PlayState.CONTINUE;
            }
            else {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.model.idle", true));
                return PlayState.CONTINUE;
            }

        }
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<EntityIgloof>(this, "controller", 5, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    public boolean isIcey(){
        return this.entityData.get(ICEY_DATA);
    }

    public void setIcey(boolean bool){
        this.entityData.set(ICEY_DATA, bool);
    }

    public boolean isEating(){
        return this.entityData.get(EATING_DATA);
    }
    public void setEating(boolean bool){
        this.entityData.set(EATING_DATA, bool);
    }

    public boolean isAttackingData(){
        return this.entityData.get(ATTACKING_DATA);
    }

    public void setAttackingData(boolean bool) {
        this.entityData.set(ATTACKING_DATA, bool);
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

    public void setMeltingCooldownTicks(int i ){
        this.entityData.set(MELTING_COOLDOWN, i);
    }

    @Override
    public int getRemainingPersistentAngerTime() {
        return 0;
    }

    @Override
    public void setRemainingPersistentAngerTime(int p_21673_) {

    }
    private void updateSnowFeedAmont(){
        this.setSnowFeed((float)getGloofAge() * 3);
    }

    @Nullable
    @Override
    public UUID getPersistentAngerTarget() {
        return null;
    }

    @Override
    public void setPersistentAngerTarget(@Nullable UUID p_21672_) {

    }

    @Override
    public void startPersistentAngerTimer() {

    }

    public static boolean matchesSnow(Block block) {
        return valueMap.containsKey(block);
    }

    private static class GloofSitWhenOrderedToGoal extends SitGoal{

        private EntityIgloof entity;

        public GloofSitWhenOrderedToGoal(EntityIgloof entity) {
            super(entity);
            this.entity = entity;
        }

        @Override
        public boolean canUse(){
            return entity.getGloofAge() <= 30 && super.canUse();
        }
    }

    private static class GloofFloatGoal extends SwimGoal {

        private EntityIgloof gloof;

        public GloofFloatGoal(EntityIgloof gloof) {
            super(gloof);
            this.gloof = gloof;
        }
        @Override
        public boolean canUse() {
            return super.canUse();
        }

        @Override
        public void tick(){
            super.tick();
            this.gloof.setIceTicks(this.gloof.getIceTicks() + 1);
        }

    }
    protected static class GloofRandomStrollGoal extends RandomWalkingGoal {
        public GloofRandomStrollGoal(CreatureEntity p_25734_, double p_25735_) {
            super(p_25734_, p_25735_);
        }

        @Override
        public boolean canUse(){
            if (this.mob.isVehicle()) {
                return false;
            } else {
                if (!this.forceTrigger) {
                    if (checkNoActionTime && this.mob.getNoActionTime() >= 100) {
                        return false;
                    }

                    if (this.mob.getRandom().nextInt(reducedTickDelay(this.interval)) != 0) {
                        return false;
                    }
                }

                Vector3d vec3 = this.getPosition();
                if (vec3 == null) {
                    return false;
                } else {
                    this.wantedX = vec3.x + ((EntityIgloof)(this.mob)).getGloofAge()/1.5 ;
                    this.wantedY = vec3.y;
                    this.wantedZ = vec3.z + ((EntityIgloof)(this.mob)).getGloofAge()/1.5;
                    this.forceTrigger = false;
                    return true;
                }
            }

        }
        protected static int reducedTickDelay(int p_186074_) {
            return -Math.floorDiv(-p_186074_, 2);
        }
    }
    private static class GloofFollowOwnerGoal extends FollowOwnerGoal {

        private double speedMod;

        public GloofFollowOwnerGoal(TameableEntity p_25294_, double p_25295_, float p_25296_, float p_25297_, boolean p_25298_) {
            super(p_25294_, p_25295_, p_25296_, p_25297_, p_25298_);
            this.speedMod = p_25295_;
        }

        @Override
        public void tick() {
            this.tamable.getLookControl().setLookAt(this.owner, 10.0F, (float)this.tamable.getMaxHeadXRot());
            if (--this.timeToRecalcPath <= 0) {
                this.timeToRecalcPath = this.adjustedTickDelay(10);
                if (!this.tamable.isLeashed() && !this.tamable.isPassenger()) {
                    if (this.tamable.distanceToSqr(this.owner) >= ((EntityIgloof)(tamable)).getMaxFollowDist()) {
                        this.teleportToOwner();
                    } else {
                        tamable.getNavigation().moveTo(this.owner, speedMod);
                    }

                }
            }
        }
        @Override
        public boolean canUse(){
            LivingEntity livingentity = this.tamable.getOwner();
            if (livingentity == null) {
                return false;
            } else if (livingentity.isSpectator()) {
                return false;
            } else if (this.tamable.isOrderedToSit()) {
                return false;
            } else if (this.tamable.distanceToSqr(livingentity) < (Math.pow(((EntityIgloof)this.tamable).getMaxFollowDist(), 2))) {
                return false;
            } else {
                this.owner = livingentity;
                return true;
            }
        }
        protected int adjustedTickDelay(int p_186072_) {
            return this.isInterruptable() ? p_186072_ : reducedTickDelay(p_186072_);
        }
        @Override
        public boolean canContinueToUse() {
            if (this.tamable.getNavigation().isDone()) {
                return false;
            } else if (this.tamable.isOrderedToSit()) {
                return false;
            } else {
                return !(this.tamable.distanceToSqr(this.owner) <= ((EntityIgloof)this.tamable).getMaxFollowDist());
            }
        }
    }
    private static class GloofMeleeAttackGoal extends MeleeAttackGoal {

        public GloofMeleeAttackGoal(CreatureEntity p_25552_, double p_25553_, boolean p_25554_) {
            super(p_25552_, p_25553_, p_25554_);
        }

        @Override
        public void stop() {
            super.stop();
            ((EntityIgloof) this.mob).setAttackingData(false);
        }

        @Override
        public void tick() {
            super.tick();
            System.out.println(this.mob.distanceToSqr(this.mob.getTarget()));
        }

        @Override
        protected void checkAndPerformAttack(LivingEntity p_25557_, double p_25558_) {
            super.checkAndPerformAttack(p_25557_, p_25558_);
            ((EntityIgloof) this.mob).setAttackingData(true);
            BlockPos.Mutable blockpos$mutableblockpos = new BlockPos.Mutable();
            AxisAlignedBB aabb = this.mob.getBoundingBox();
            BlockPos blockpos = new BlockPos(aabb.minX + 0.001D, aabb.minY + 0.001D, aabb.minZ + 0.001D);
            BlockPos blockpos1 = new BlockPos(aabb.maxX - 0.001D, aabb.maxY - 0.001D, aabb.maxZ - 0.001D);
            for(int i = blockpos.getX(); i <= blockpos1.getX() + (getAtackRange() *getXModifier()); ++i) {
                for (int j = blockpos.getY(); j <= blockpos1.getY() + getAtackRange(); ++j) {
                    for (int k = blockpos.getZ(); k <= blockpos1.getZ() + (getAtackRange() * getZModifier()); ++k) {
                        blockpos$mutableblockpos.set(i, j, k);
                        BlockState blockstate = this.mob.level.getBlockState(blockpos$mutableblockpos);
                        if (GLOOF_BREAKABLES_TAG.contains(blockstate.getBlock())){
                            this.mob.level.destroyBlock(blockpos$mutableblockpos, true);
                        }
                    }
                }
            }
        }

        private int getAtackRange(){
            return ((EntityIgloof) this.mob).getGloofAge() / 5;
        }
        private Vector3i getModifier(){
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


