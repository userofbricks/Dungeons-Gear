package com.infamous.dungeons_gear.enchantments.armor;

import com.infamous.dungeons_gear.enchantments.types.HealthAbilityEnchantment;
import com.infamous.dungeons_gear.utilties.EnchantUtils;
import com.infamous.dungeons_gear.enchantments.lists.ArmorEnchantmentList;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.infamous.dungeons_gear.DungeonsGear.MODID;

@Mod.EventBusSubscriber(modid= MODID)
public class CowardiceEnchantment extends HealthAbilityEnchantment {

    public CowardiceEnchantment() {
        super(Rarity.RARE, EnchantmentType.ARMOR_CHEST, new EquipmentSlotType[]{
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
        return !(enchantment instanceof HealthAbilityEnchantment);
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event){
        PlayerEntity player = event.player;
        if(player == null) return;
        if(event.phase == TickEvent.Phase.START) return;
        if(player.isAlive()){
            float maxHealth = player.getMaxHealth();
            float currentHealth = player.getHealth();
            if(currentHealth == maxHealth){
                if(EnchantUtils.hasEnchantment(player, ArmorEnchantmentList.COWARDICE)){
                    int cowardiceLevel = EnchantmentHelper.getMaxEnchantmentLevel(ArmorEnchantmentList.COWARDICE, player);
                    EffectInstance strengthBoost = new EffectInstance(Effects.STRENGTH, 0, cowardiceLevel + 1);
                    player.addPotionEffect(strengthBoost);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onDamage(LivingDamageEvent event) {
        if (event.getSource() instanceof IndirectEntityDamageSource) {
            if (event.getSource().getImmediateSource() instanceof AbstractArrowEntity) {
                AbstractArrowEntity arrowEntity = (AbstractArrowEntity) event.getSource().getImmediateSource();
                Entity shooterEntity = arrowEntity.getShooter();
                if (shooterEntity instanceof PlayerEntity) {
                    PlayerEntity playerEntity = (PlayerEntity) arrowEntity.getShooter();
                    if(playerEntity != null){
                        if (playerEntity.getHealth() == playerEntity.getMaxHealth()) {
                            if (EnchantUtils.hasEnchantment(playerEntity, ArmorEnchantmentList.COWARDICE)) {
                                int cowardiceLevel = EnchantmentHelper.getMaxEnchantmentLevel(ArmorEnchantmentList.COWARDICE, playerEntity);
                                float originalDamage = event.getAmount();
                                event.setAmount(originalDamage +
                                        (originalDamage *0.1F) + (originalDamage * 0.1F * cowardiceLevel));
                            }
                        }
                    }
                }
            }
        }
    }
}
