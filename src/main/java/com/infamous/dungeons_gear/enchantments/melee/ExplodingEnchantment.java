package com.infamous.dungeons_gear.enchantments.melee;

import com.infamous.dungeons_gear.enchantments.types.AOEDamageEnchantment;
import com.infamous.dungeons_gear.enchantments.types.DamageBoostEnchantment;
import com.infamous.dungeons_gear.utilties.EnchantUtils;
import com.infamous.dungeons_gear.enchantments.ModEnchantmentTypes;
import com.infamous.dungeons_gear.enchantments.lists.MeleeEnchantmentList;
import com.infamous.dungeons_gear.utilties.AbilityUtils;
import net.minecraft.enchantment.DamageEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.infamous.dungeons_gear.DungeonsGear.MODID;
import static com.infamous.dungeons_gear.items.WeaponList.BATTLESTAFF_OF_TERROR;
import static com.infamous.dungeons_gear.items.WeaponList.CURSED_AXE;

@Mod.EventBusSubscriber(modid= MODID)
public class ExplodingEnchantment extends AOEDamageEnchantment {

    public ExplodingEnchantment() {
        super(Rarity.RARE, ModEnchantmentTypes.MELEE, new EquipmentSlotType[]{
                EquipmentSlotType.MAINHAND});
    }

    public int getMaxLevel() {
        return 3;
    }

    @Override
    public boolean canApplyTogether(Enchantment enchantment) {
        return !(enchantment instanceof DamageEnchantment) && !(enchantment instanceof DamageBoostEnchantment) && !(enchantment instanceof AOEDamageEnchantment);
    }

    @SubscribeEvent
    public static void onExplodingKill(LivingDeathEvent event){
        if(event.getSource().getImmediateSource() instanceof AbstractArrowEntity) return;
        if(event.getSource().getTrueSource() instanceof LivingEntity){
            LivingEntity attacker = (LivingEntity) event.getSource().getTrueSource();
            LivingEntity victim = event.getEntityLiving();
            ItemStack mainhand = attacker.getHeldItemMainhand();
            boolean uniqueWeaponFlag = mainhand.getItem() == CURSED_AXE || mainhand.getItem() == BATTLESTAFF_OF_TERROR;
            if(EnchantUtils.hasEnchantment(mainhand, MeleeEnchantmentList.EXPLODING) || uniqueWeaponFlag){
                int explodingLevel = EnchantmentHelper.getEnchantmentLevel(MeleeEnchantmentList.EXPLODING, mainhand);
                float explosionDamage = 0;
                if(explodingLevel == 1) explosionDamage = victim.getMaxHealth() * 0.2f;
                if(explodingLevel == 2) explosionDamage = victim.getMaxHealth() * 0.4f;
                if(explodingLevel == 3) explosionDamage = victim.getMaxHealth() * 0.6f;
                if(uniqueWeaponFlag) explosionDamage += (victim.getMaxHealth() * 0.2f);
                victim.world.playSound((PlayerEntity) null, victim.getPosX(), victim.getPosY(), victim.getPosZ(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 64.0F, 1.0F);
                AbilityUtils.spawnExplosionCloud(attacker, victim, 3.0F);
                AbilityUtils.causeExplosionAttack(attacker, victim, explosionDamage, 3.0F);
            }
        }
    }

}
