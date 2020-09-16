package com.infamous.dungeons_gear.enchantments.melee;

import com.infamous.dungeons_gear.enchantments.types.AOEDamageEnchantment;
import com.infamous.dungeons_gear.enchantments.types.DamageBoostEnchantment;
import com.infamous.dungeons_gear.utilties.EnchantUtils;
import com.infamous.dungeons_gear.enchantments.ModEnchantmentTypes;
import com.infamous.dungeons_gear.enchantments.lists.MeleeEnchantmentList;
import com.infamous.dungeons_gear.utilties.AbilityUtils;
import net.minecraft.enchantment.*;
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

import static com.infamous.dungeons_gear.DungeonsGear.MODID;

@Mod.EventBusSubscriber(modid= MODID)
public class SwirlingEnchantment extends AOEDamageEnchantment {

    public SwirlingEnchantment() {
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
            if(EnchantUtils.hasEnchantment(mainhand, MeleeEnchantmentList.SWIRLING)){
                int swirlingLevel = EnchantmentHelper.getEnchantmentLevel(MeleeEnchantmentList.SWIRLING, mainhand);
                // gets the attack damage of the original attack before any enchantment modifiers are added
                float attackDamage = (float)attacker.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getValue();
                float cooledAttackStrength = attacker.getCooledAttackStrength(0.5F);
                attackDamage *= 0.2F + cooledAttackStrength * cooledAttackStrength * 0.8F;

                float damageDealt = attackDamage * 0.5F;
                if(swirlingLevel == 1) damageDealt *= 1;
                if(swirlingLevel == 2) damageDealt *= 1.5;
                if(swirlingLevel == 3) damageDealt *= 2;
                victim.world.playSound((PlayerEntity) null, victim.getPosX(), victim.getPosY(), victim.getPosZ(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, SoundCategory.PLAYERS, 64.0F, 1.0F);
                AbilityUtils.spawnCritCloud(attacker, victim, 1.5f);
                AbilityUtils.causeSwirlingAttack(attacker, victim, damageDealt, 1.5f);
            }
        }
    }
}
