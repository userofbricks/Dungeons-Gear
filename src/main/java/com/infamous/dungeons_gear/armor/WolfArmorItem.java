package com.infamous.dungeons_gear.armor;

import static com.infamous.dungeons_gear.items.ArmorList.FOX_ARMOR;
import static com.infamous.dungeons_gear.items.ArmorList.FOX_ARMOR_HOOD;
import static com.infamous.dungeons_gear.items.ArmorList.WOLF_ARMOR;
import static com.infamous.dungeons_gear.items.ArmorList.WOLF_ARMOR_HOOD;

import java.util.List;
import java.util.UUID;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.infamous.dungeons_gear.DungeonsGear;
import com.infamous.dungeons_gear.armor.models.WolfArmorModel;
import com.infamous.dungeons_gear.interfaces.IArmor;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class WolfArmorItem extends ArmorItem implements IArmor {

    private static final UUID[] ARMOR_MODIFIERS = new UUID[]{
            UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"),
            UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"),
            UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"),
            UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};

    private final boolean unique;
    private final int damageReduceAmount;
    private final float toughness;
    private final Multimap<String, AttributeModifier> attributeModifiers;

    public WolfArmorItem(IArmorMaterial armorMaterial, EquipmentSlotType slotType, Properties properties, boolean unique) {
        super(armorMaterial, slotType, properties);
        this.unique = unique;

        this.damageReduceAmount = armorMaterial.getDamageReductionAmount(slot);
        this.toughness = armorMaterial.getToughness();

        ImmutableMultimap.Builder<String, AttributeModifier> builder = ImmutableMultimap.builder();
        UUID uuid = ARMOR_MODIFIERS[slot.getIndex()];
        builder.put(SharedMonsterAttributes.ARMOR.getName(), new AttributeModifier(uuid, "Armor modifier", (double)this.damageReduceAmount, AttributeModifier.Operation.ADDITION));
        builder.put(SharedMonsterAttributes.ARMOR_TOUGHNESS.getName(), new AttributeModifier(uuid, "Armor toughness", (double)this.toughness, AttributeModifier.Operation.ADDITION));
        builder.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(uuid, "Armor attack damage boost", 0.20D * 0.5D, AttributeModifier.Operation.MULTIPLY_BASE));
        this.attributeModifiers = builder.build();
    }


    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type) {
        if(this.unique) return DungeonsGear.MODID + ":textures/models/armor/fox_armor.png";
        return DungeonsGear.MODID + ":textures/models/armor/wolf_armor.png";
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    @OnlyIn(Dist.CLIENT)
    public <A extends BipedModel<?>> A getArmorModel(LivingEntity entityLiving, ItemStack stack, EquipmentSlotType armorSlot, A _default) {
        if(stack.getItem() == WOLF_ARMOR || stack.getItem() == WOLF_ARMOR_HOOD){
            return (A) new WolfArmorModel<>(1.0F, slot, entityLiving);
        }
        else if(stack.getItem() == FOX_ARMOR || stack.getItem() == FOX_ARMOR_HOOD){
            return (A) new WolfArmorModel<>(1.0F, slot, entityLiving);
        }
        return null;
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot) {
        return equipmentSlot == this.slot ? this.attributeModifiers : super.getAttributeModifiers(equipmentSlot);
    }

    @Override
    public Rarity getRarity(ItemStack itemStack){
        if(this.unique) return Rarity.RARE;
        return Rarity.UNCOMMON;
    }

    @Override
    public void addInformation(ItemStack stack, World world, List<ITextComponent> list, ITooltipFlag flag) {
        super.addInformation(stack, world, list, flag);

        if (this.unique) {
            list.add(new StringTextComponent(TextFormatting.WHITE + "" + TextFormatting.ITALIC + "Ancient Villager tribes created this armor to honor the fox, who is a great and agile warrior. This piece strikes terror into the hearts of many enemies as it has a reputation for being quite difficult to pierce."));

        }
        else{
            list.add(new StringTextComponent(TextFormatting.WHITE + "" + TextFormatting.ITALIC + "Many warriors wear the heads of wolves into battle to strike fear into the hearts of their enemies."));
        }
        list.add(new TranslationTextComponent(
                "attribute.name.healthPotionsHealNearbyAllies")
                .applyTextStyle(TextFormatting.GREEN));
    }

    @Override
    public double getChanceToNegateHits() {
        if(this.unique) return 15;
        else return 0;
    }

    @Override
    public boolean doHealthPotionsHealNearbyAllies() {
        return true;
    }

    @Override
    public double getRangedDamage() {
        return 10;
    }
}
