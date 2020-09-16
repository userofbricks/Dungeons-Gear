package com.infamous.dungeons_gear.enchantments.melee;

import com.infamous.dungeons_gear.utilties.EnchantUtils;
import com.infamous.dungeons_gear.enchantments.ModEnchantmentTypes;
import com.infamous.dungeons_gear.enchantments.lists.MeleeEnchantmentList;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.infamous.dungeons_gear.DungeonsGear.MODID;
import static com.infamous.dungeons_gear.items.WeaponList.THE_LAST_LAUGH;

@Mod.EventBusSubscriber(modid= MODID)
public class ProspectorEnchantment extends Enchantment{

    public ProspectorEnchantment() {
        super(Enchantment.Rarity.RARE, ModEnchantmentTypes.MELEE, new EquipmentSlotType[]{
                EquipmentSlotType.MAINHAND});
    }

    public int getMaxLevel() {
        return 3;
    }

    @SubscribeEvent
    public static void onProspectiveKill(LivingDropsEvent event){
        if(event.getSource().getImmediateSource() instanceof AbstractArrowEntity) return;
        if(event.getSource().getTrueSource() instanceof LivingEntity){
            LivingEntity attacker = (LivingEntity) event.getSource().getTrueSource();
            ItemStack mainhand = attacker.getHeldItemMainhand();
            LivingEntity victim = event.getEntityLiving();
            boolean uniqueWeaponFlag = mainhand.getItem() == THE_LAST_LAUGH;
            if(EnchantUtils.hasEnchantment(mainhand, MeleeEnchantmentList.PROSPECTOR)){
                int prospectorLevel = EnchantmentHelper.getEnchantmentLevel(MeleeEnchantmentList.PROSPECTOR, mainhand);
                float prospectorChance = 0;
                if(prospectorLevel == 1) prospectorChance = 0.25F;
                if(prospectorLevel == 2) prospectorChance = 0.5F;
                if(prospectorLevel == 3) prospectorChance = 0.75F;
                float prospectorRand = attacker.getRNG().nextFloat();
                if(prospectorRand <= prospectorChance){
                    if(victim instanceof MonsterEntity && !isInNether(victim)){
                        ItemEntity drop = new ItemEntity(victim.world, victim.getPosX(), victim.getPosY(), victim.getPosZ(), new ItemStack(Items.EMERALD, 1));
                        event.getDrops().add(drop);
                    }
                    else if(victim instanceof MonsterEntity && isInNether(victim)){
                        ItemEntity drop = new ItemEntity(victim.world, victim.getPosX(), victim.getPosY(), victim.getPosZ(), new ItemStack(Items.GOLD_INGOT, 1));
                        event.getDrops().add(drop);
                    }
                }
            }
            if(uniqueWeaponFlag){
                float prospectorRand = attacker.getRNG().nextFloat();
                if(prospectorRand <= 0.25F) {
                    if (victim instanceof MonsterEntity && !isInNether(victim)) {
                        ItemEntity drop = new ItemEntity(victim.world, victim.getPosX(), victim.getPosY(), victim.getPosZ(), new ItemStack(Items.EMERALD, 1));
                        event.getDrops().add(drop);
                    } else if (victim instanceof MonsterEntity && isInNether(victim)) {
                        ItemEntity drop = new ItemEntity(victim.world, victim.getPosX(), victim.getPosY(), victim.getPosZ(), new ItemStack(Items.GOLD_INGOT, 1));
                        event.getDrops().add(drop);
                    }
                }
            }
        }
    }

    private static boolean isInNether(LivingEntity victim){
        return victim.getEntityWorld().getDimension().getType() == DimensionType.THE_NETHER;
    }
}
