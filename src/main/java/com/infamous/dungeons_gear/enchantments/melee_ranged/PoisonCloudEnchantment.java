package com.infamous.dungeons_gear.enchantments.melee_ranged;

import com.infamous.dungeons_gear.capabilities.combo.ComboProvider;
import com.infamous.dungeons_gear.capabilities.combo.ICombo;
import com.infamous.dungeons_gear.damagesources.OffhandAttackDamageSource;
import com.infamous.dungeons_gear.effects.CustomEffects;
import com.infamous.dungeons_gear.utilties.EnchantUtils;
import com.infamous.dungeons_gear.enchantments.ModEnchantmentTypes;
import com.infamous.dungeons_gear.enchantments.lists.MeleeRangedEnchantmentList;
import com.infamous.dungeons_gear.utilties.AbilityUtils;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.infamous.dungeons_gear.DungeonsGear.MODID;
import static com.infamous.dungeons_gear.items.RangedWeaponList.THE_GREEN_MENACE;
import static com.infamous.dungeons_gear.items.WeaponList.*;

@Mod.EventBusSubscriber(modid = MODID)
public class PoisonCloudEnchantment extends Enchantment {

    public PoisonCloudEnchantment() {
        super(Rarity.RARE, ModEnchantmentTypes.MELEE_RANGED, new EquipmentSlotType[]{
                EquipmentSlotType.MAINHAND});
    }

    public int getMaxLevel() {
        return 3;
    }

    @Override
    public void onEntityDamaged(LivingEntity user, Entity target, int level) {
        if(!(target instanceof LivingEntity)) return;
        float chance = user.getRNG().nextFloat();
        if(chance <=  0.3F){
            checkForPlayer(user);
            AbilityUtils.spawnPoisonCloud(user, (LivingEntity)target, level-1);
        }
    }

    @SubscribeEvent
    public static void onPoisonousWeaponAttack(LivingAttackEvent event){
        if(event.getSource().getImmediateSource() instanceof AbstractArrowEntity) return;
        if(event.getSource() instanceof OffhandAttackDamageSource) return;
        if(!(event.getSource().getTrueSource() instanceof LivingEntity)) return;
        LivingEntity attacker = (LivingEntity)event.getSource().getTrueSource();
        LivingEntity victim = event.getEntityLiving();
        ItemStack mainhand = attacker.getHeldItemMainhand();
        if((mainhand.getItem() == VENOM_GLAIVE || mainhand.getItem() == NIGHTMARES_BITE || mainhand.getItem() == VINE_WHIP)){
            float chance = attacker.getRNG().nextFloat();
            if(chance <=  0.3F){
                checkForPlayer(attacker);
                AbilityUtils.spawnPoisonCloud(attacker, victim, 0);
            }
        }
    }

    @SubscribeEvent
    public static void onPoisonBowImpact(ProjectileImpactEvent.Arrow event){
        RayTraceResult rayTraceResult = event.getRayTraceResult();
        //if(!EnchantUtils.arrowHitLivingEntity(rayTraceResult)) return;
        AbstractArrowEntity arrow = event.getArrow();
        if(!EnchantUtils.shooterIsLiving(arrow)) return;
        LivingEntity shooter = (LivingEntity)arrow.getShooter();

        int poisonLevel = EnchantUtils.enchantmentTagToLevel(arrow, MeleeRangedEnchantmentList.POISON_CLOUD);
        boolean uniqueWeaponFlag = arrow.getTags().contains("TheGreenMenace");

        if(poisonLevel > 0){
            if(rayTraceResult instanceof EntityRayTraceResult){
                EntityRayTraceResult entityRayTraceResult = (EntityRayTraceResult) rayTraceResult;
                if(entityRayTraceResult.getEntity() instanceof LivingEntity){
                    LivingEntity victim = (LivingEntity) ((EntityRayTraceResult)rayTraceResult).getEntity();
                    float poisonRand = shooter.getRNG().nextFloat();
                    if(poisonRand <= 0.3F){
                        checkForPlayer(shooter);
                        AbilityUtils.spawnPoisonCloud(shooter, victim, poisonLevel - 1);
                    }
                }
            }
            if(rayTraceResult instanceof BlockRayTraceResult){
                BlockRayTraceResult blockRayTraceResult = (BlockRayTraceResult)rayTraceResult;
                BlockPos blockPos = blockRayTraceResult.getPos();
                float poisonRand = shooter.getRNG().nextFloat();
                if(poisonRand <= 0.3F){
                    checkForPlayer(shooter);
                    AbilityUtils.spawnPoisonCloudAtPos(shooter, true, blockPos, poisonLevel - 1);
                }
            }
        }
        if(uniqueWeaponFlag){
            if(rayTraceResult instanceof EntityRayTraceResult){
                EntityRayTraceResult entityRayTraceResult = (EntityRayTraceResult) rayTraceResult;
                if(entityRayTraceResult.getEntity() instanceof LivingEntity){
                    LivingEntity victim = (LivingEntity) ((EntityRayTraceResult)rayTraceResult).getEntity();
                    float poisonRand = shooter.getRNG().nextFloat();
                    if(poisonRand <= 0.3F){
                        checkForPlayer(shooter);
                        AbilityUtils.spawnPoisonCloud(shooter, victim, 0);
                    }
                }
            }
            if(rayTraceResult instanceof BlockRayTraceResult){
                BlockRayTraceResult blockRayTraceResult = (BlockRayTraceResult)rayTraceResult;
                BlockPos blockPos = blockRayTraceResult.getPos();
                float poisonRand = shooter.getRNG().nextFloat();
                if(poisonRand <= 0.3F){
                    checkForPlayer(shooter);
                    AbilityUtils.spawnPoisonCloudAtPos(shooter, true, blockPos, 0);
                }
            }
        }
    }



    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event){
        PlayerEntity player = event.player;
        if(player == null) return;
        if(event.phase == TickEvent.Phase.START) return;
        if(player.isAlive()){
            ICombo comboCap = player.getCapability(ComboProvider.COMBO_CAPABILITY).orElseThrow(IllegalStateException::new);
            int poisonImmunityTimer = comboCap.getPoisonImmunityTimer();
            if(poisonImmunityTimer <= 0){
                comboCap.setPoisonImmunityTimer(poisonImmunityTimer - 1);
            }
        }
    }



    @SubscribeEvent
    public static void onPoisonEvent(PotionEvent.PotionApplicableEvent event){
        if(event.getPotionEffect().getPotion() == Effects.POISON){
            if(event.getEntityLiving() instanceof PlayerEntity){
                PlayerEntity playerEntity = (PlayerEntity) event.getEntityLiving();
                ICombo comboCap = playerEntity.getCapability(ComboProvider.COMBO_CAPABILITY).orElseThrow(IllegalStateException::new);
                int poisonImmunityTimer = comboCap.getPoisonImmunityTimer();
                if(poisonImmunityTimer > 0){
                    event.setResult(Event.Result.DENY);
                }
            }
        }
    }

    private static void checkForPlayer(LivingEntity livingEntity){
        if(livingEntity instanceof PlayerEntity){
            PlayerEntity playerEntity = (PlayerEntity) livingEntity;

            ICombo comboCap = playerEntity.getCapability(ComboProvider.COMBO_CAPABILITY).orElseThrow(IllegalStateException::new);
            int poisonImmunityTimer = comboCap.getPoisonImmunityTimer();
            if(poisonImmunityTimer <= 0){
                comboCap.setPoisonImmunityTimer(60);
            }
        }
    }
}
