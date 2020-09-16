package com.infamous.dungeons_gear.enchantments.armor;

import com.infamous.dungeons_gear.enchantments.types.ArtifactEnchantment;
import com.infamous.dungeons_gear.utilties.EnchantUtils;
import com.infamous.dungeons_gear.enchantments.ModEnchantmentTypes;
import com.infamous.dungeons_gear.enchantments.lists.ArmorEnchantmentList;
import com.infamous.dungeons_gear.interfaces.IArtifact;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.infamous.dungeons_gear.DungeonsGear.MODID;

@Mod.EventBusSubscriber(modid= MODID)
public class HealthSynergyEnchantment extends ArtifactEnchantment {

    public HealthSynergyEnchantment() {
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
        return !(enchantment instanceof ArtifactEnchantment);
    }

    @SubscribeEvent
    public static void onArtifactUsed(PlayerInteractEvent.RightClickItem event){
        PlayerEntity player = event.getPlayer();
        Hand activeHand = event.getHand();
        ItemStack itemStack = player.getHeldItem(activeHand);
        if(itemStack.getItem() instanceof IArtifact){
            if(EnchantUtils.hasEnchantment(player, ArmorEnchantmentList.HEALTH_SYNERGY)){
                int healthSynergyLevel = EnchantmentHelper.getMaxEnchantmentLevel(ArmorEnchantmentList.HEALTH_SYNERGY, player);
                player.heal(0.2F + (0.1F * healthSynergyLevel));
            }
        }
    }
}
