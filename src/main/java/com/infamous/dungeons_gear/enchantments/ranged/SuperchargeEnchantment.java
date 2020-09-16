package com.infamous.dungeons_gear.enchantments.ranged;

import static com.infamous.dungeons_gear.DungeonsGear.MODID;

import com.infamous.dungeons_gear.enchantments.ModEnchantmentTypes;
import com.infamous.dungeons_gear.enchantments.lists.RangedEnchantmentList;
import com.infamous.dungeons_gear.utilties.EnchantUtils;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid= MODID)
public class SuperchargeEnchantment extends Enchantment {

    public SuperchargeEnchantment() {
        super(Rarity.RARE, ModEnchantmentTypes.RANGED, new EquipmentSlotType[]{
                EquipmentSlotType.MAINHAND});
    }

    public int getMaxLevel() {
        return 3;
    }

    @Override
    public boolean canApplyTogether(Enchantment enchantment) {
        return enchantment != Enchantments.PUNCH || enchantment != Enchantments.POWER;
    }

    @SubscribeEvent
    public static void onSuperchargeImpact(ProjectileImpactEvent.Arrow event){
        if(!EnchantUtils.arrowHitLivingEntity(event.getRayTraceResult())) return;
        AbstractArrowEntity arrow = event.getArrow();
        if(!EnchantUtils.shooterIsLiving(arrow)) return;
        int superchargeLevel = EnchantUtils.enchantmentTagToLevel(arrow, RangedEnchantmentList.SUPERCHARGE);
        boolean uniqueWeaponFlag = arrow.getTags().contains("GuardianBow");
        if(superchargeLevel > 0 || uniqueWeaponFlag){
            double originalDamage = arrow.getDamage();
            int originalKnockback = EnchantUtils.enchantmentTagToLevel(arrow, Enchantments.PUNCH);
            double damageModifier = 0;
            if(superchargeLevel == 1) damageModifier = 1.2D;
            if(superchargeLevel == 2) damageModifier = 1.4D;
            if(superchargeLevel == 3) damageModifier = 1.6D;
            if(uniqueWeaponFlag) damageModifier += 1.2D;
            arrow.setDamage(originalDamage * damageModifier);
            arrow.setKnockbackStrength(originalKnockback + 1);
        }
    }
}
