package com.infamous.dungeons_gear.utilties;

import static com.infamous.dungeons_gear.enchantments.lists.RangedEnchantmentList.rangedEnchantmentToStringMap;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;

public class EnchantUtils {

    public static boolean hasEnchantment(ItemStack stack, Enchantment enchantment){
        return enchantment != null && EnchantmentHelper.getEnchantmentLevel(enchantment, stack) > 0;
    }

    public static boolean hasEnchantment(LivingEntity entity, Enchantment enchantment) {
        return enchantment != null && EnchantmentHelper.getMaxEnchantmentLevel(enchantment, entity) > 0;
    }

    public static boolean shooterIsLiving(AbstractArrowEntity arrowEntity) {
        return arrowEntity.getShooter() != null && arrowEntity.getShooter() instanceof LivingEntity;
    }

    public static boolean arrowHitLivingEntity(RayTraceResult rayTraceResult) {
        if(rayTraceResult instanceof EntityRayTraceResult){
            EntityRayTraceResult entityRayTraceResult = (EntityRayTraceResult)rayTraceResult;
            if(entityRayTraceResult.getEntity() instanceof LivingEntity){
                return true;
            } else{
                return false;
            }
        } else{
            return false;
        }
    }

    public static boolean arrowHitMob(RayTraceResult rayTraceResult) {
        if(rayTraceResult instanceof EntityRayTraceResult){
            EntityRayTraceResult entityRayTraceResult = (EntityRayTraceResult)rayTraceResult;
            if(entityRayTraceResult.getEntity() instanceof MobEntity){
                return true;
            } else{
                return false;
            }
        } else{
            return false;
        }
    }

    public static void addEnchantmentTagsToArrow(ItemStack rangedWeapon, AbstractArrowEntity arrowEntity){
        for(Enchantment enchantment : rangedEnchantmentToStringMap.keySet()){
            if(EnchantUtils.hasEnchantment(rangedWeapon, enchantment)){
                int enchantmentLevel = EnchantmentHelper.getEnchantmentLevel(enchantment, rangedWeapon);
                String enchantmentTag = rangedEnchantmentToStringMap.get(enchantment) + enchantmentLevel;
                arrowEntity.addTag(enchantmentTag);
            }
        }
    }

    public static boolean hasEnchantmentTag(AbstractArrowEntity arrowEntity, Enchantment enchantment){
        String enchantmentAsString = rangedEnchantmentToStringMap.get(enchantment);
        for(int i = 1; i < 4; i++){
            String enchantmentTag = enchantmentAsString + i;
            if(arrowEntity.getTags().contains(enchantmentTag)){
                return true;
            }
        }
        return false;
    }

    public static int enchantmentTagToLevel(AbstractArrowEntity arrowEntity, Enchantment enchantment){
        String enchantmentAsString = rangedEnchantmentToStringMap.get(enchantment);
        for(int i = 1; i < 4; i++){
            String enchantmentTag = enchantmentAsString + i;
            if(arrowEntity.getTags().contains(enchantmentTag)){
                return i;
            }
        }
        return 0;
    }
}
