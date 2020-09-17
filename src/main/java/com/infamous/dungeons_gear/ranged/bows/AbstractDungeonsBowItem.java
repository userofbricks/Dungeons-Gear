package com.infamous.dungeons_gear.ranged.bows;

import com.infamous.dungeons_gear.capabilities.weapon.IWeapon;
import com.infamous.dungeons_gear.capabilities.weapon.WeaponProvider;
import com.infamous.dungeons_gear.enchantments.lists.RangedEnchantmentList;
import com.infamous.dungeons_gear.interfaces.IRangedWeapon;
import com.infamous.dungeons_gear.utilties.RangedUtils;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.*;
import net.minecraft.stats.Stats;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

public abstract class AbstractDungeonsBowItem extends BowItem implements IRangedWeapon {
    private float defaultChargeTime;
    private boolean isUnique;

    public AbstractDungeonsBowItem(Properties builder, float defaultChargeTimeIn, boolean isUniqueIn) {
        super(builder);
        this.defaultChargeTime = defaultChargeTimeIn;
        this.isUnique = isUniqueIn;
    }

    public float getDefaultChargeTime(){
        return this.defaultChargeTime;
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, LivingEntity livingEntity, int timeLeft) {
        if (livingEntity instanceof PlayerEntity) {
            PlayerEntity playerentity = (PlayerEntity)livingEntity;
            boolean useInfiniteAmmo = playerentity.abilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack) > 0;
            ItemStack itemstack = playerentity.findAmmo(stack);
            int charge = this.getUseDuration(stack) - timeLeft;
            charge = ForgeEventFactory.onArrowLoose(stack, world, playerentity, charge, !itemstack.isEmpty() || useInfiniteAmmo);
            if (charge < 0) {
                return;
            }

            if (!itemstack.isEmpty() || useInfiniteAmmo) {
                if (itemstack.isEmpty()) {
                    itemstack = new ItemStack(Items.ARROW);
                }

                float arrowVelocity = this.getBowArrowVelocity(stack, charge);
                int multishotLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.MULTISHOT, stack);
                int arrowsToFire = 1;
                if(multishotLevel > 0) arrowsToFire += 2;
                if(this.hasMultishotBuiltIn(stack)) arrowsToFire += 2;
                if(this.hasMultishotWhenCharged(stack) && arrowVelocity == 1.0F) arrowsToFire += 2;

                for(int i = 0; i < arrowsToFire; i++){
                    if ((double)arrowVelocity >= 0.1D) {
                        boolean hasInfiniteAmmo = playerentity.abilities.isCreativeMode || itemstack.getItem() instanceof ArrowItem && ((ArrowItem)itemstack.getItem()).isInfinite(itemstack, stack, playerentity);
                        if (!world.isRemote) {
                            ArrowItem arrowitem = (ArrowItem)((ArrowItem)(itemstack.getItem() instanceof ArrowItem ? itemstack.getItem() : Items.ARROW));
                            AbstractArrowEntity abstractarrowentity = arrowitem.createArrow(world, itemstack, playerentity);
                            abstractarrowentity = this.customeArrow(abstractarrowentity);
                            if(i == 0) abstractarrowentity.shoot(playerentity, playerentity.rotationPitch, playerentity.rotationYaw, 0.0F, arrowVelocity * 3.0F, 1.0F);
                            if(i == 1) abstractarrowentity.shoot(playerentity, playerentity.rotationPitch, playerentity.rotationYaw + 10.0F, 0.0F, arrowVelocity * 3.0F, 1.0F);
                            if(i == 2) abstractarrowentity.shoot(playerentity, playerentity.rotationPitch, playerentity.rotationYaw - 10.0F, 0.0F, arrowVelocity * 3.0F, 1.0F);
                            if(i == 3) abstractarrowentity.shoot(playerentity, playerentity.rotationPitch, playerentity.rotationYaw + 20.0F, 0.0F, arrowVelocity * 3.0F, 1.0F);
                            if(i == 4) abstractarrowentity.shoot(playerentity, playerentity.rotationPitch, playerentity.rotationYaw - 20.0F, 0.0F, arrowVelocity * 3.0F, 1.0F);
                            if(i == 5) abstractarrowentity.shoot(playerentity, playerentity.rotationPitch, playerentity.rotationYaw + 30.0F, 0.0F, arrowVelocity * 3.0F, 1.0F);
                            if(i == 6) abstractarrowentity.shoot(playerentity, playerentity.rotationPitch, playerentity.rotationYaw - 30.0F, 0.0F, arrowVelocity * 3.0F, 1.0F);
                            if (arrowVelocity == 1.0F) {
                                abstractarrowentity.setIsCritical(true);
                            }
                            int powerLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, stack);

                            // Damage Boosters
                            if(this.hasPowerBuiltIn(stack)) powerLevel++;
                            if(this.shootsStrongChargedArrows(stack) && abstractarrowentity.getIsCritical()) powerLevel++;
                            if(this.hasSuperChargedBuiltIn(stack) && abstractarrowentity.getIsCritical()) powerLevel++;
                            if(this.shootsHeavyArrows(stack)) powerLevel++;

                            if (powerLevel > 0) {
                                abstractarrowentity.setDamage(abstractarrowentity.getDamage() + (double)powerLevel * 0.5D + 0.5D);
                            }
                            int punchLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, stack);
                            if(this.hasPunchBuiltIn(stack)) punchLevel++;
                            if(this.hasSuperChargedBuiltIn(stack)) punchLevel++;
                            if (punchLevel > 0) {
                                abstractarrowentity.setKnockbackStrength(punchLevel);
                            }
                            if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, stack) > 0) {
                                abstractarrowentity.setFire(100);
                            }
                            stack.damageItem(1, playerentity, (p_lambda$onPlayerStoppedUsing$0_1_) -> {
                                p_lambda$onPlayerStoppedUsing$0_1_.sendBreakAnimation(playerentity.getActiveHand());
                            });
                            if (hasInfiniteAmmo || playerentity.abilities.isCreativeMode
                                    && (itemstack.getItem() == Items.SPECTRAL_ARROW || itemstack.getItem() == Items.TIPPED_ARROW)) {
                                abstractarrowentity.pickupStatus = AbstractArrowEntity.PickupStatus.CREATIVE_ONLY;
                            }
                            if(i > 0){
                                abstractarrowentity.pickupStatus = AbstractArrowEntity.PickupStatus.CREATIVE_ONLY;
                            }
                            RangedUtils.addWeaponTags(abstractarrowentity, stack);
                            world.addEntity(abstractarrowentity);
                        }

                        world.playSound((PlayerEntity)null, playerentity.getPosX(), playerentity.getPosY(), playerentity.getPosZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (random.nextFloat() * 0.4F + 1.2F) + arrowVelocity * 0.5F);
                        if (!hasInfiniteAmmo && !playerentity.abilities.isCreativeMode) {
                            itemstack.shrink(1);
                            if (itemstack.isEmpty()) {
                                playerentity.inventory.deleteStack(itemstack);
                            }
                        }

                        playerentity.addStat(Stats.ITEM_USED.get(this));
                    }
                }
            }
        }
    }

    public float getBowArrowVelocity(ItemStack stack, int charge) {
        float bowChargeTime = getBowChargeTime(stack);
        if(bowChargeTime <= 0){
            bowChargeTime = 1;
        }
        float arrowVelocity = (float)charge / bowChargeTime;
        arrowVelocity = (arrowVelocity * arrowVelocity + arrowVelocity * 2.0F) / 3.0F;
        if (arrowVelocity > 1.0F) {
            arrowVelocity = 1.0F;
        }

        return arrowVelocity;
    }

    public float getBowChargeTime(ItemStack stack){
        int quickChargeLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.QUICK_CHARGE, stack);
        if(this.hasQuickChargeBuiltIn(stack)) quickChargeLevel++;
        int accelerateLevel = EnchantmentHelper.getEnchantmentLevel(RangedEnchantmentList.ACCELERATE, stack);
        if(this.hasAccelerateBuiltIn(stack)) accelerateLevel++;

        IWeapon weaponCap = stack.getCapability(WeaponProvider.WEAPON_CAPABILITY).orElseThrow(IllegalStateException::new);
        float bowChargeTime = weaponCap.getBowChargeTime();
        long lastFiredTime = weaponCap.getLastFiredTime();

        if(accelerateLevel > 0 && lastFiredTime > 0){
            return Math.max(bowChargeTime - 5 * quickChargeLevel, 0);
        }
        else {
            return Math.max(this.getDefaultChargeTime() - 5 * quickChargeLevel, 0);
        }
    }

    public Rarity getRarity(ItemStack itemStack){

        if(this.isUnique){
            return Rarity.RARE;
        }
        return Rarity.UNCOMMON;
    }
}
