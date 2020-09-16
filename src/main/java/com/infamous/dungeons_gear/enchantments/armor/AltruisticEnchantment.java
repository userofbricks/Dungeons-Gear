package com.infamous.dungeons_gear.enchantments.armor;

import static com.infamous.dungeons_gear.DungeonsGear.MODID;

import com.infamous.dungeons_gear.enchantments.ModEnchantmentTypes;
import com.infamous.dungeons_gear.enchantments.lists.ArmorEnchantmentList;
import com.infamous.dungeons_gear.utilties.AbilityUtils;
import com.infamous.dungeons_gear.utilties.EnchantUtils;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid= MODID)
public class AltruisticEnchantment extends Enchantment{

    public AltruisticEnchantment() {
        super(Rarity.RARE, ModEnchantmentTypes.ARMOR, new EquipmentSlotType[]{
                EquipmentSlotType.HEAD,
                EquipmentSlotType.CHEST,
                EquipmentSlotType.LEGS,
                EquipmentSlotType.FEET});
    }

    public int getMaxLevel() {
        return 3;
    }

    @SubscribeEvent
    public static void onDamage(LivingDamageEvent event) {
        LivingEntity livingEntity = event.getEntityLiving();
        if(livingEntity instanceof PlayerEntity){
            PlayerEntity playerEntity = (PlayerEntity) livingEntity;
            if(EnchantUtils.hasEnchantment(playerEntity, ArmorEnchantmentList.ALTRUISTIC)){
                int altruisticLevel = EnchantmentHelper.getMaxEnchantmentLevel(ArmorEnchantmentList.ALTRUISTIC, playerEntity);
                float damage = event.getAmount();
                float damageToHealingMultiplier = 0.25F * altruisticLevel;
                AbilityUtils.healNearbyAllies(playerEntity, damage * damageToHealingMultiplier, 12);

            }
        }
    }
}
