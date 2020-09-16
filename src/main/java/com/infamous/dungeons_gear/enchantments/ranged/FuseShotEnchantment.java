package com.infamous.dungeons_gear.enchantments.ranged;

import com.infamous.dungeons_gear.utilties.EnchantUtils;
import com.infamous.dungeons_gear.enchantments.ModEnchantmentTypes;
import com.infamous.dungeons_gear.utilties.AbilityUtils;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.infamous.dungeons_gear.DungeonsGear.MODID;

@Mod.EventBusSubscriber(modid= MODID)
public class FuseShotEnchantment extends Enchantment {

    public FuseShotEnchantment() {
        super(Rarity.RARE, ModEnchantmentTypes.RANGED, new EquipmentSlotType[]{
                EquipmentSlotType.MAINHAND});
    }

    public int getMaxLevel() {
        return 3;
    }


    @SubscribeEvent
    public static void onFuseShotImpact(ProjectileImpactEvent.Arrow event){
        AbstractArrowEntity arrowEntity = event.getArrow();
        if(EnchantUtils.shooterIsLiving(arrowEntity)){
            LivingEntity shooter =(LivingEntity) arrowEntity.getShooter();
            if(arrowEntity.getTags().contains("FuseShot")){
                if(event.getRayTraceResult() instanceof BlockRayTraceResult){
                    BlockRayTraceResult blockRayTraceResult = (BlockRayTraceResult)event.getRayTraceResult();
                    BlockPos blockPos = blockRayTraceResult.getPos();
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
    public static void onFuseShotDamage(LivingDamageEvent event){
        if(event.getSource() instanceof IndirectEntityDamageSource){
            IndirectEntityDamageSource indirectEntityDamageSource = (IndirectEntityDamageSource) event.getSource();
            if(indirectEntityDamageSource.getImmediateSource() instanceof AbstractArrowEntity) {
                AbstractArrowEntity arrowEntity = (AbstractArrowEntity) indirectEntityDamageSource.getImmediateSource();

                LivingEntity victim = event.getEntityLiving();
                if (indirectEntityDamageSource.getTrueSource() instanceof LivingEntity) {
                    LivingEntity archer = (LivingEntity) indirectEntityDamageSource.getTrueSource();
                    if(arrowEntity.getTags().contains("FuseShot")) {
                        victim.world.playSound((PlayerEntity) null, victim.getPosX(), victim.getPosY(), victim.getPosZ(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 64.0F, 1.0F);
                        AbilityUtils.spawnExplosionCloud(archer, victim, 3.0f);
                        AbilityUtils.causeExplosionAttack(archer, victim, event.getAmount(), 3.0f);
                    }
                }
            }
        }
    }
}
