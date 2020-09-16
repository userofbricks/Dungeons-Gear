package com.infamous.dungeons_gear.armor;

import static com.infamous.dungeons_gear.items.ArmorList.FULL_METAL_ARMOR;
import static com.infamous.dungeons_gear.items.ArmorList.FULL_METAL_ARMOR_HELMET;
import static com.infamous.dungeons_gear.items.ArmorList.PLATE_ARMOR;
import static com.infamous.dungeons_gear.items.ArmorList.PLATE_ARMOR_HELMET;

import java.util.List;
import java.util.UUID;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.infamous.dungeons_gear.DungeonsGear;
import com.infamous.dungeons_gear.armor.models.PlateArmorModel;
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
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PlateArmorItem extends ArmorItem implements IArmor {

    private static final UUID[] ARMOR_MODIFIERS = new UUID[]{
            UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"),
            UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"),
            UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"),
            UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};

    private final boolean unique;
    private final int damageReduceAmount;
    private final float toughness;
    private final Multimap<String, AttributeModifier> attributeModifiers;

    public PlateArmorItem(IArmorMaterial armorMaterial, EquipmentSlotType slotType, Properties properties, boolean unique) {
        super(armorMaterial, slotType, properties);
        this.unique = unique;

        this.damageReduceAmount = armorMaterial.getDamageReductionAmount(slot);
        this.toughness = armorMaterial.getToughness();

        ImmutableMultimap.Builder<String, AttributeModifier> builder = ImmutableMultimap.builder();
        UUID uuid = ARMOR_MODIFIERS[slot.getIndex()];
        builder.put(SharedMonsterAttributes.ARMOR.getName(), new AttributeModifier(uuid, "Armor modifier", (double)this.damageReduceAmount, AttributeModifier.Operation.ADDITION));
        builder.put(SharedMonsterAttributes.ARMOR_TOUGHNESS.getName(), new AttributeModifier(uuid, "Armor toughness", (double)this.toughness, AttributeModifier.Operation.ADDITION));
        if(this.unique){
            builder.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(uuid, "Armor attack damage boost", 0.30D * 0.5D, AttributeModifier.Operation.MULTIPLY_BASE));
        }
        this.attributeModifiers = builder.build();
    }


    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type) {
        if(this.unique) return DungeonsGear.MODID + ":textures/models/armor/full_metal_armor.png";
        return DungeonsGear.MODID + ":textures/models/armor/plate_armor.png";
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    @OnlyIn(Dist.CLIENT)
    public <A extends BipedModel<?>> A getArmorModel(LivingEntity entityLiving, ItemStack stack, EquipmentSlotType armorSlot, A _default) {
        if(stack.getItem() == PLATE_ARMOR || stack.getItem() == PLATE_ARMOR_HELMET){
            return (A) new PlateArmorModel<>(1.0F, slot, entityLiving);
        }
        else if(stack.getItem() == FULL_METAL_ARMOR || stack.getItem() == FULL_METAL_ARMOR_HELMET){
            return (A) new PlateArmorModel<>(1.0F, slot, entityLiving);
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
            list.add(new StringTextComponent(TextFormatting.WHITE + "" + TextFormatting.ITALIC + "Full Metal Armor is destined for the great defenders of the Overworld."));
        }
        else{
            list.add(new StringTextComponent(TextFormatting.WHITE + "" + TextFormatting.ITALIC + "Plate Armor turns the average soldier into a fortress but comes with reduced mobility."));
        }
    }

    @Override
    public double getChanceToNegateHits() {
        return 15;
    }

    @Override
    public double getLongerJumpAbilityCooldown() {
        return 50;
    }
}
