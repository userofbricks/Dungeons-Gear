package com.infamous.dungeons_gear.enchantments;

import com.infamous.dungeons_gear.config.DungeonsGearConfig;
import com.infamous.dungeons_gear.interfaces.IArmor;
import com.infamous.dungeons_gear.interfaces.IMeleeWeapon;
import com.infamous.dungeons_gear.interfaces.IRangedWeapon;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.item.*;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

public class ModEnchantmentTypes {
    public static final EnchantmentType MELEE = addEnchantment("melee", item ->
    DungeonsGearConfig.COMMON.ENABLE_ENCHANTS_ON_NON_DUNGEONS_GEAR.get() ?
            (item instanceof SwordItem || item instanceof IMeleeWeapon) :
            (item instanceof IMeleeWeapon)
    		);

    public static final EnchantmentType MELEE_RANGED = addEnchantment("melee_ranged", item ->
    DungeonsGearConfig.COMMON.ENABLE_ENCHANTS_ON_NON_DUNGEONS_GEAR.get() ?
            (item instanceof SwordItem || item instanceof IMeleeWeapon || item instanceof BowItem || item instanceof CrossbowItem) :
            (item instanceof IMeleeWeapon || item instanceof IRangedWeapon)
    		);
    
    public static final EnchantmentType RANGED = addEnchantment("ranged", item ->
    DungeonsGearConfig.COMMON.ENABLE_ENCHANTS_ON_NON_DUNGEONS_GEAR.get() ?
            (item instanceof BowItem || item instanceof CrossbowItem) :
            (item instanceof IRangedWeapon)
);


public static final EnchantmentType ARMOR = addEnchantment("armor", item ->
DungeonsGearConfig.COMMON.ENABLE_ENCHANTS_ON_NON_DUNGEONS_GEAR.get() ?
    (item instanceof ArmorItem) :
    (item instanceof IArmor)
);

public static final EnchantmentType ARMOR_RANGED = addEnchantment("armor_ranged", item ->
    DungeonsGearConfig.COMMON.ENABLE_ENCHANTS_ON_NON_DUNGEONS_GEAR.get() ?
            (item instanceof BowItem || item instanceof CrossbowItem || item instanceof ArmorItem) :
            (item instanceof IRangedWeapon || item instanceof IArmor)
);

    @Nonnull
    public static EnchantmentType addEnchantment(String name, Predicate<Item> condition) {
        return EnchantmentType.create(name, condition);
    }
}
