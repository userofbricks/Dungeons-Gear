package com.infamous.dungeons_gear.enchantments.melee;

import static com.infamous.dungeons_gear.DungeonsGear.MODID;
import static com.infamous.dungeons_gear.items.WeaponList.WHIRLWIND;

import com.infamous.dungeons_gear.enchantments.ModEnchantmentTypes;
import com.infamous.dungeons_gear.enchantments.lists.MeleeEnchantmentList;
import com.infamous.dungeons_gear.enchantments.types.AOEDamageEnchantment;
import com.infamous.dungeons_gear.enchantments.types.DamageBoostEnchantment;
import com.infamous.dungeons_gear.utilties.AbilityUtils;
import com.infamous.dungeons_gear.utilties.EnchantUtils;

import net.minecraft.enchantment.DamageEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid= MODID)
public class ShockwaveEnchantment extends AOEDamageEnchantment {

    public ShockwaveEnchantment() {
        super(Rarity.RARE, ModEnchantmentTypes.MELEE, new EquipmentSlotType[]{
                EquipmentSlotType.MAINHAND});
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public boolean canApplyTogether(Enchantment enchantment) {
        return !(enchantment instanceof DamageEnchantment) && !(enchantment instanceof DamageBoostEnchantment) && !(enchantment instanceof AOEDamageEnchantment);
    }

    @SubscribeEvent
    public static void onVanillaCriticalHit(CriticalHitEvent event){
        if(event.getPlayer() != null
                //&& event.isVanillaCritical()
        ){
            PlayerEntity attacker = (PlayerEntity) event.getPlayer();
            LivingEntity victim = event.getEntityLiving();
            ItemStack mainhand = attacker.getHeldItemMainhand();
            boolean uniqueWeaponFlag = mainhand.getItem() == WHIRLWIND;
            if(EnchantUtils.hasEnchantment(mainhand, MeleeEnchantmentList.SHOCKWAVE) || uniqueWeaponFlag){
                int shockwaveLevel = EnchantmentHelper.getEnchantmentLevel(MeleeEnchantmentList.SHOCKWAVE, mainhand);
                // gets the attack damage of the original attack before any enchantment modifiers are added
                float attackDamage = (float)attacker.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getValue();
                float cooledAttackStrength = attacker.getCooledAttackStrength(0.5F);
                attackDamage *= 0.2F + cooledAttackStrength * cooledAttackStrength * 0.8F;

                float shockwaveDamage = attackDamage * 0.25F;
                if(shockwaveLevel == 1) shockwaveDamage *= 1;
                if(shockwaveLevel == 2) shockwaveDamage *= 1.5;
                if(shockwaveLevel == 3) shockwaveDamage *= 2;
                if(uniqueWeaponFlag) shockwaveDamage += shockwaveDamage;
                victim.world.playSound((PlayerEntity) null, victim.getPosX(), victim.getPosY(), victim.getPosZ(), SoundEvents.ENTITY_LIGHTNING_BOLT_IMPACT, SoundCategory.PLAYERS, 64.0F, 1.0F);
                AbilityUtils.spawnCritCloud(attacker, victim, 3.0f);
                AbilityUtils.causeShockwave(attacker, victim, shockwaveDamage, 3.0f);
            }
        }
    }
}
