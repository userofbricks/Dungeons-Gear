package com.infamous.dungeons_gear.enchantments.armor;

import static com.infamous.dungeons_gear.DungeonsGear.MODID;

import com.infamous.dungeons_gear.enchantments.ModEnchantmentTypes;
import com.infamous.dungeons_gear.enchantments.lists.ArmorEnchantmentList;
import com.infamous.dungeons_gear.utilties.EnchantUtils;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid= MODID)
public class DeflectEnchantment extends Enchantment {

    public DeflectEnchantment() {
        super(Rarity.RARE, ModEnchantmentTypes.ARMOR, new EquipmentSlotType[]{
                EquipmentSlotType.HEAD,
                EquipmentSlotType.CHEST,
                EquipmentSlotType.LEGS,
                EquipmentSlotType.FEET});
    }

    public int getMaxLevel() {
        return 3;
    }

    @Override
    public boolean canApplyTogether(Enchantment enchantment) {
        return !(enchantment instanceof ProtectionEnchantment);
    }



    @SubscribeEvent
    public static void onDeflectImpact(ProjectileImpactEvent.Arrow event){
        RayTraceResult rayTraceResult = event.getRayTraceResult();
        if(!EnchantUtils.arrowHitLivingEntity(rayTraceResult)) return;
        AbstractArrowEntity arrow = event.getArrow();
        if(!EnchantUtils.shooterIsLiving(arrow)) return;
        LivingEntity victim = (LivingEntity) ((EntityRayTraceResult)rayTraceResult).getEntity();
        if(EnchantUtils.hasEnchantment(victim, ArmorEnchantmentList.DEFLECT)){
            int deflectLevel = EnchantmentHelper.getMaxEnchantmentLevel(ArmorEnchantmentList.DEFLECT, victim);
            double originalDamage = arrow.getDamage();
            double deflectChance = 0;
            if(deflectLevel == 1) deflectChance = 0.2F;
            if(deflectLevel == 2) deflectChance = 0.4F;
            if(deflectLevel == 3) deflectChance = 0.6F;
            float deflectRand = victim.getRNG().nextFloat();
            if(deflectRand <= deflectChance){
                event.setCanceled(true);
                arrow.setDamage(originalDamage * 0.5D);
                arrow.rotationYaw += 180.0F;
                arrow.prevRotationYaw += 180.0F;
            }
        }
    }
}
