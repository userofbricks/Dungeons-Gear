package com.infamous.dungeons_gear.ranged.bows;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.util.List;

import static com.infamous.dungeons_gear.items.RangedWeaponList.*;

public class LongbowItem extends AbstractDungeonsBowItem {

    public LongbowItem(Properties builder, float defaultChargeTimeIn, boolean isUniqueIn) {
        super(builder, defaultChargeTimeIn, isUniqueIn);
    }

    @Override
    public boolean shootsStrongChargedArrows(ItemStack stack) {
        return true;
    }

    @Override
    public boolean hasFuseShotBuiltIn(ItemStack stack) {
        return stack.getItem() == RED_SNAKE;
    }

    @Override
    public boolean hasSuperChargedBuiltIn(ItemStack stack) {
        return stack.getItem() == GUARDIAN_BOW;
    }

    @Override
    public void addInformation(ItemStack stack, World world, List<ITextComponent> list, ITooltipFlag flag)
    {
        super.addInformation(stack, world, list, flag);

        if(stack.getItem() == GUARDIAN_BOW){
            list.add(new StringTextComponent(TextFormatting.WHITE + "" + TextFormatting.ITALIC + "Forged from fossilized coral, the Guardian Bow is a remnant from sunken civilizations of lost ages."));

            list.add(new StringTextComponent(TextFormatting.GREEN + "Strong Charged Attacks"));
            list.add(new StringTextComponent(TextFormatting.GREEN + "Super Charged Arrows (Supercharge I)"));
        }
        if(stack.getItem() == RED_SNAKE){
            list.add(new StringTextComponent(TextFormatting.WHITE + "" + TextFormatting.ITALIC + "The Red Snake radiates an explosive heat, making it a deadly fire risk in the dry, desert lands."));

            list.add(new StringTextComponent(TextFormatting.GREEN + "Strong Charged Attacks"));
            list.add(new StringTextComponent(TextFormatting.GREEN + "Chance For Arrows To Explode (Fuse Shot I)"));
        }if(stack.getItem() == LONGBOW){
            list.add(new StringTextComponent(TextFormatting.WHITE + "" + TextFormatting.ITALIC + "The Longbow, crafted for hunting rather than battle, is still useful in a fight."));

            list.add(new StringTextComponent(TextFormatting.GREEN + "Strong Charged Attacks"));
        }
    }
}
