package com.infamous.dungeons_gear.ranged.crossbows;

import com.infamous.dungeons_gear.capabilities.weapon.IWeapon;
import com.infamous.dungeons_gear.capabilities.weapon.WeaponProvider;
import com.infamous.dungeons_gear.enchantments.lists.RangedEnchantmentList;
import com.infamous.dungeons_gear.utilties.AbilityUtils;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.infamous.dungeons_gear.DungeonsGear.MODID;
import static com.infamous.dungeons_gear.items.RangedWeaponList.AUTO_CROSSBOW;

@Mod.EventBusSubscriber(modid = MODID)
public class CrossbowEvents {


    @SubscribeEvent
    public static void onAccelerateCrossbowFired(PlayerInteractEvent.RightClickItem event){
        PlayerEntity playerEntity = event.getPlayer();
        World world = playerEntity.getEntityWorld();
        long worldTime = world.getGameTime();
        ItemStack stack = event.getItemStack();
        if(stack.getItem() instanceof CrossbowItem & CrossbowItem.isCharged(stack)){
            IWeapon weaponCap = stack.getCapability(WeaponProvider.WEAPON_CAPABILITY).orElseThrow(IllegalStateException::new);
            long lastFiredTime = weaponCap.getLastFiredTime();
            int crossbowChargeTime = weaponCap.getCrossbowChargeTime();

            int accelerateLevel = EnchantmentHelper.getEnchantmentLevel(RangedEnchantmentList.ACCELERATE, stack);
            if(stack.getItem() == AUTO_CROSSBOW) accelerateLevel++;

            int defaultChargeTime = 25;
            if(stack.getItem() instanceof AbstractDungeonsCrossbowItem){
                defaultChargeTime = ((AbstractDungeonsCrossbowItem)stack.getItem()).getDefaultChargeTime();
            }

            if(accelerateLevel > 0){
                if(lastFiredTime < worldTime - (Math.max(crossbowChargeTime, 0) + 23)
                        && crossbowChargeTime < defaultChargeTime){
                    weaponCap.setCrossbowChargeTime(defaultChargeTime);
                }
                else{
                    int fireRateReduction =
                            (int)(defaultChargeTime * (0.04 + 0.04*accelerateLevel));
                            //(int)(2.5F * accelerateLevel);

                    weaponCap.setCrossbowChargeTime(crossbowChargeTime - fireRateReduction);
                }
                weaponCap.setLastFiredTime(worldTime);
            }
        }
    }

    @SubscribeEvent
    public static void onExplodingCrossbowImpact(ProjectileImpactEvent.Arrow event){
        AbstractArrowEntity arrowEntity = event.getArrow();
        if(arrowEntity.getShooter() instanceof LivingEntity){
            LivingEntity shooter =(LivingEntity) arrowEntity.getShooter();
            boolean explodingCrossbowFlag = arrowEntity.getTags().contains("ExplodingCrossbow")
                    || arrowEntity.getTags().contains("FireboltThrower")
                    || arrowEntity.getTags().contains("ImplodingCrossbow");
            if(explodingCrossbowFlag){
                if(event.getRayTraceResult() instanceof BlockRayTraceResult){
                    BlockRayTraceResult blockRayTraceResult = (BlockRayTraceResult)event.getRayTraceResult();
                    BlockPos blockPos = blockRayTraceResult.getPos();

                    // weird arrow damage calculation from AbstractArrowEntity
                    float f = (float)arrowEntity.getMotion().length();
                    int damage = MathHelper.ceil(MathHelper.clamp((double)f * arrowEntity.getDamage(), 0.0D, 2.147483647E9D));
                    if (arrowEntity.getIsCritical()) {
                        long criticalDamageBonus = (long)shooter.getRNG().nextInt(damage / 2 + 2);
                        damage = (int)Math.min(criticalDamageBonus + (long)damage, 2147483647L);
                    }

                    arrowEntity.world.playSound((PlayerEntity) null, blockPos.getX(), blockPos.getY(), blockPos.getZ(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 64.0F, 1.0F);
                    AbilityUtils.spawnExplosionCloudAtPos(shooter, true, blockPos, 3.0F);
                    AbilityUtils.causeExplosionAttackAtPos(shooter, true, blockPos, damage, 3.0F);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onExplodingCrossbowDamage(LivingDamageEvent event){
        if(event.getSource() instanceof IndirectEntityDamageSource){
            if(event.getSource().getImmediateSource() instanceof AbstractArrowEntity){
                AbstractArrowEntity arrowEntity = (AbstractArrowEntity) event.getSource().getImmediateSource();
                if(arrowEntity.getShooter() instanceof LivingEntity){
                    LivingEntity shooter = (LivingEntity) arrowEntity.getShooter();
                    boolean explodingCrossbowFlag = arrowEntity.getTags().contains("ExplodingCrossbow")
                            || arrowEntity.getTags().contains("FireboltThrower")
                            || arrowEntity.getTags().contains("ImplodingCrossbow");
                    if(explodingCrossbowFlag){
                        LivingEntity victim = event.getEntityLiving();
                        victim.world.playSound((PlayerEntity) null, victim.getPosX(), victim.getPosY(), victim.getPosZ(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 64.0F, 1.0F);
                        AbilityUtils.spawnExplosionCloud(shooter, victim, 3.0F);
                        AbilityUtils.causeExplosionAttack(shooter, victim, event.getAmount(), 3.0F);
                    }
                }
            }
        }
    }
}
