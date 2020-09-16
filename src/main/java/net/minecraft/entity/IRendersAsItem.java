package net.minecraft.entity;

import net.minecraft.item.ItemStack;

/**
 * For some reason the crossbow coremod is attempting to load this in 1.16, so it's being provided just incase the bug is here too.
 * This is a modlauncher/FML bug, and this can be removed once it is fixed.
 */
public interface IRendersAsItem {
    ItemStack func_184543_l();
}
