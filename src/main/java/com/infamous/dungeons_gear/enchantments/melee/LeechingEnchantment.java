package com.infamous.dungeons_gear.enchantments.melee;

import com.infamous.dungeons_gear.utilties.EnchantUtils;
import com.infamous.dungeons_gear.enchantments.types.HealingEnchantment;
import com.infamous.dungeons_gear.enchantments.ModEnchantmentTypes;
import com.infamous.dungeons_gear.enchantments.lists.MeleeEnchantmentList;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.infamous.dungeons_gear.DungeonsGear.MODID;
import static com.infamous.dungeons_gear.items.WeaponList.HEARTSTEALER;

@Mod.EventBusSubscriber(modid= MODID)
public class LeechingEnchantment extends HealingEnchantment {

    public LeechingEnchantment() {
        super(Rarity.RARE, ModEnchantmentTypes.MELEE, new EquipmentSlotType[]{
                EquipmentSlotType.MAINHAND});
    }

    public int getMaxLevel() {
        return 3;
    }

    @Override
    public boolean canApplyTogether(Enchantment enchantment) {
        return !(enchantment instanceof HealingEnchantment);
    }

    @SubscribeEvent
    public static void onLeechingKill(LivingDeathEvent event){
        if(event.getSource().getImmediateSource() instanceof AbstractArrowEntity) return;
        if(event.getSource().getTrueSource() instanceof LivingEntity){
            LivingEntity attacker = (LivingEntity) event.getSource().getTrueSource();
            LivingEntity victim = event.getEntityLiving();
            ItemStack mainhand = attacker.getHeldItemMainhand();
            boolean uniqueWeaponFlag = mainhand.getItem() == HEARTSTEALER;
            if(EnchantUtils.hasEnchantment(mainhand, MeleeEnchantmentList.LEECHING)){
                int leechingLevel = EnchantmentHelper.getEnchantmentLevel(MeleeEnchantmentList.LEECHING, mainhand);
                float victimMaxHealth = victim.getMaxHealth();
                float healthRegained = 0;
                if(leechingLevel == 1) healthRegained = 0.04F * victimMaxHealth;
                if(leechingLevel == 2) healthRegained = 0.06F * victimMaxHealth;
                if(leechingLevel == 3) healthRegained = 0.08F * victimMaxHealth;
                if(uniqueWeaponFlag) healthRegained += 0.04F * victimMaxHealth;
                attacker.heal(healthRegained);
            }
        }
    }

}
