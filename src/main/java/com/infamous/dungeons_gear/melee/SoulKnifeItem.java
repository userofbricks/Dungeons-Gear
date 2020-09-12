package com.infamous.dungeons_gear.melee;

import com.infamous.dungeons_gear.interfaces.IMeleeWeapon;
import com.infamous.dungeons_gear.interfaces.ISoulGatherer;
import com.infamous.dungeons_gear.items.WeaponList;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.item.SwordItem;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.util.List;

public class SoulKnifeItem extends SwordItem implements IMeleeWeapon, ISoulGatherer {

    public SoulKnifeItem(IItemTier tier, int attackDamageIn, float attackSpeedIn, Properties builder) {
        super(tier, attackDamageIn, attackSpeedIn, builder);
    }

    public Rarity getRarity(ItemStack itemStack){

        if(itemStack.getItem() == WeaponList.ETERNAL_KNIFE
                || itemStack.getItem() == WeaponList.TRUTHSEEKER
        ){
            return Rarity.RARE;
        }
        return Rarity.UNCOMMON;
    }

    @Override
    public void addInformation(ItemStack stack, World world, List<ITextComponent> list, ITooltipFlag flag)
    {
        super.addInformation(stack, world, list, flag);
        if(stack.getItem() == WeaponList.TRUTHSEEKER){
            list.add(new StringTextComponent(TextFormatting.WHITE + "" + TextFormatting.ITALIC + "The warden of Highblock Keep kept this unpleasant blade by their side during interrogations."));

            list.add(new StringTextComponent(TextFormatting.GREEN + "Increased Damage To Wounded Mobs (Committed I)"));
            list.add(new StringTextComponent(TextFormatting.LIGHT_PURPLE + "+2 XP Gathering"));
            //list.add(new StringTextComponent(TextFormatting.GREEN + "Thrust Attack"));
        }

        if(stack.getItem() == WeaponList.ETERNAL_KNIFE){
            list.add(new StringTextComponent(TextFormatting.WHITE + "" + TextFormatting.ITALIC + "A disturbing aura surrounds this knife, as if it has existed for all time and will outlive us all."));

            list.add(new StringTextComponent(TextFormatting.LIGHT_PURPLE + "Chance To Gain XP (Soul Siphon I)"));
            list.add(new StringTextComponent(TextFormatting.LIGHT_PURPLE + "+2 XP Gathering"));
            //list.add(new StringTextComponent(TextFormatting.GREEN + "Thrust Attack"));
        }
        if(stack.getItem() == WeaponList.SOUL_KNIFE){
            list.add(new StringTextComponent(TextFormatting.WHITE + "" + TextFormatting.ITALIC + "A ceremonial knife that uses magical energy to hold the wrath of souls inside its blade."));

            list.add(new StringTextComponent(TextFormatting.LIGHT_PURPLE + "+2 XP Gathering"));
            //list.add(new StringTextComponent(TextFormatting.GREEN + "Thrust Attack"));

        }
    }

    @Override
    public int getGatherAmount(ItemStack stack) {
        return 2;
    }
}
