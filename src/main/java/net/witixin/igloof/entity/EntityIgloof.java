package net.witixin.igloof.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.witixin.igloof.Reference;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.EnumSet;
import java.util.UUID;

public class EntityIgloof extends TamableAnimal implements NeutralMob, IAnimatable {

    private boolean icey;
    private boolean eating;
    private int gloofAge = getGloofAge();


    private static final int UNSITTABLE_AGE = 30;
    private static final float GROWTH_CONSTANT = 1.08f;
    private static final int MAX_AGE = 40;
    private static final int MAX_FOLLOW_DIST = 30;


    private static final EntityDataAccessor<Integer> SYNCHED_DATA_AGE = SynchedEntityData.defineId(EntityIgloof.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> ICEY_DATA = SynchedEntityData.defineId(EntityIgloof.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> EATING_DATA = SynchedEntityData.defineId(EntityIgloof.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> ATTACKING_DATA = SynchedEntityData.defineId(EntityIgloof.class, EntityDataSerializers.BOOLEAN);


    private AnimationFactory factory = new AnimationFactory(this);

    public EntityIgloof(EntityType<? extends EntityIgloof> entityType, Level level) {
        super(Reference.IGLOOF.get(), level);
    }

    public static EntityIgloof of(Level level, Player id){
        EntityIgloof toReturn = new EntityIgloof(Reference.IGLOOF.get(), level);
        toReturn.tame(id);
        return toReturn;
    }


    @Override
    public void tick(){
        super.tick();
        if (!this.level.isClientSide && !this.isOrderedToSit()){
            this.navigation.tick();
        }
    }

    public void feed(){
        if (getGloofAge() >40)    return;
        this.saveAge(getGloofAge() + 1);
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(2* getGloofAge());
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(this.getAttributeValue(Attributes.ATTACK_DAMAGE) + (getGloofAge() % 3 == 0 ? 1 : 0));
        this.updateDims();
        this.refreshDimensions();
    }

    private void saveAge(int age){
        this.entityData.set(SYNCHED_DATA_AGE, age);
    }


    @Override
    public EntityDimensions getDimensions(Pose p_33597_) {
        return super.getDimensions(p_33597_).scale(this.getSize());
    }

    public float getSize(){
        return (float) Math.pow(GROWTH_CONSTANT ,getGloofAge());
    }

    private void updateDims(){
        this.dimensions = getDimensions(this.getPose());

    }

    @Override
    protected void registerGoals(){
        this.goalSelector.addGoal(1, new GloofFollowOwnerGoal(this, 1D, 20.0F, 1F, false));
        this.goalSelector.addGoal(2, new GloofSitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.4F));
        this.goalSelector.addGoal(4, new GloofMeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(5, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F, false));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(7, new GloofRandomStrollGoal(this, 0.0D));
        this.goalSelector.addGoal(10, new EatSnowGoal(this, 1200));
        this.goalSelector.addGoal(8, new GloofFloatGoal(this));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));


    }

    private int getGloofAge(){
        int ageGloof = this.entityData.get(SYNCHED_DATA_AGE);
        return ageGloof;
    }

    @Override
    public InteractionResult mobInteract(Player p_30412_, InteractionHand p_30413_) {
        if (p_30412_.level.isClientSide && p_30413_ == InteractionHand.OFF_HAND)return InteractionResult.PASS;
            if (this.getHealth() < this.getMaxHealth() && !this.isIcey() && p_30412_.getItemInHand(p_30413_).getItem() == Reference.MAGIC_COAL.get()){
                this.heal(4.0f);
                p_30412_.getItemInHand(p_30413_).shrink(1);
                return InteractionResult.SUCCESS;
            }
            if (!this.isIcey() && false){

            }
            if (this.isTame() && getGloofAge() <= UNSITTABLE_AGE){
                if (this.isOwnedBy(p_30412_) || super.mobInteract(p_30412_, p_30413_).consumesAction()){
                    if (this.isOrderedToSit()){
                        this.setOrderedToSit(false);
                        this.setInSittingPose(false);
                        return InteractionResult.SUCCESS;
                    }
                    if (!isOrderedToSit()){
                        this.setOrderedToSit(true);
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
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob mob) {
        return null;
    }

    public int getMaxFollowDist(){
        return MAX_FOLLOW_DIST + getGloofAge() * getGloofAge() ;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SYNCHED_DATA_AGE, 0);
        this.entityData.define(ICEY_DATA, false);
        this.entityData.define(EATING_DATA, false);
        this.entityData.define(ATTACKING_DATA, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag p_28156_) {
        super.addAdditionalSaveData(p_28156_);
        p_28156_.putInt("GloofAge", this.getGloofAge());
        p_28156_.putBoolean("GloofIcey", this.isIcey());
        p_28156_.putBoolean("GloofEating", this.isEating());
        p_28156_.putBoolean("GloofAttacking", this.isAttackingData());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag p_28142_) {
        super.readAdditionalSaveData(p_28142_);
        this.saveAge(p_28142_.getInt("GloofAge"));
        this.setIcey(p_28142_.getBoolean("GloofIcey"));
        this.setEating(p_28142_.getBoolean("GloofEating"));
        this.setEating(p_28142_.getBoolean("GloofAttacking"));
    }


    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level.isClientSide) {
            this.updatePersistentAnger((ServerLevel)this.level, true);
        }

    }
    @Override
    public boolean canBeLeashed(Player p_30396_) {
        return !this.isAngry() && super.canBeLeashed(p_30396_) && getGloofAge() <= UNSITTABLE_AGE;
    }
    @Override
    public boolean canMate(Animal animal){
        return false;
    }

    @Override
    public boolean canBreed() {
        return false;
    }


    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        if (event.getAnimatable() instanceof EntityIgloof){
            EntityIgloof gloof = (EntityIgloof) event.getAnimatable();
            if ((gloof.isInSittingPose() && gloof.isOrderedToSit())){
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
            if (gloof.isAttackingData()) {
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
    private boolean isDeltaPositive(Vec3 vec){
        return Math.abs(vec.x) > 0 || Math.abs(vec.y) > 0 || Math.abs(vec.z) > 0;
    }
    public <E extends IAnimatable> PlayState attack(AnimationEvent<E> event) {
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

    @Override
    public int getRemainingPersistentAngerTime() {
        return 0;
    }

    @Override
    public void setRemainingPersistentAngerTime(int p_21673_) {

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
        return block.equals(Blocks.POWDER_SNOW) || block.equals(Blocks.SNOW_BLOCK);
    }

    private static class GloofSitWhenOrderedToGoal extends SitWhenOrderedToGoal{

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

    private static class EatSnowGoal extends Goal {

        private EntityIgloof gloof;
        private int timer = 0;
        private int counter = 0;
        private BlockPos idealPos;

        public EatSnowGoal(EntityIgloof p_26140_, int timer) {
            this.gloof = p_26140_;
            this.timer = timer;
            this.counter = timer;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }


        @Override
        public void stop() {
            super.stop();
            this.gloof.setEating(false);
            this.idealPos = null;
            counter = timer;
        }

        @Override
        public void start() {
            super.start();
            this.gloof.setEating(true);
            this.gloof.level.destroyBlock(idealPos, true);
            this.gloof.feed();
        }


        @Override
        public boolean canUse() {
            if (gloof.isIcey()) return false;
            if (counter == 20){
                this.cachePos();
                if (idealPos == null) return false;
                this.gloof.navigation.createPath(idealPos, 1);
            }
            if (counter == 0){
                if (matchesSnow(this.gloof.level.getBlockState(this.gloof.blockPosition().below()).getBlock())){
                    return true;
                }
            }
            --counter;
            return false;
        }

        private void cachePos(){
            for (int x = -1; x < 2; x++){
                for (int y = 0; y < 2; y++){
                    for (int z = -1; z < 2; z++){
                        if (matchesSnow(this.gloof.level.getBlockState(this.gloof.blockPosition().above(y).north(x).east(z)).getBlock())){
                            idealPos = this.gloof.blockPosition().above(y).north(x).east(z);
                            break;
                        }
                    }
                }
            }
        }

        @Override
        public boolean canContinueToUse() {
            return false;
        }
    }
    private static class GloofFloatGoal extends FloatGoal {

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
            this.gloof.setIcey(true);
        }

    }
    private static class GloofRandomStrollGoal extends RandomStrollGoal {
        public GloofRandomStrollGoal(PathfinderMob p_25734_, double p_25735_) {
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

                Vec3 vec3 = this.getPosition();
                if (vec3 == null) {
                    return false;
                } else {
                    this.wantedX = vec3.x + ((EntityIgloof)(this.mob)).getGloofAge()/3 ;
                    this.wantedY = vec3.y;
                    this.wantedZ = vec3.z + ((EntityIgloof)(this.mob)).getGloofAge()/3;
                    this.forceTrigger = false;
                    return true;
                }
            }
        }
    }
    private static class GloofFollowOwnerGoal extends FollowOwnerGoal {

        private double speedMod;

        public GloofFollowOwnerGoal(TamableAnimal p_25294_, double p_25295_, float p_25296_, float p_25297_, boolean p_25298_) {
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

        public GloofMeleeAttackGoal(PathfinderMob p_25552_, double p_25553_, boolean p_25554_) {
            super(p_25552_, p_25553_, p_25554_);
        }

        @Override
        public void stop() {
            super.stop();
            ((EntityIgloof) this.mob).setAttackingData(false);
        }

        @Override
        protected void checkAndPerformAttack(LivingEntity p_25557_, double p_25558_) {
            super.checkAndPerformAttack(p_25557_, p_25558_);
            ((EntityIgloof) this.mob).setAttackingData(true);
        }
    }
}


