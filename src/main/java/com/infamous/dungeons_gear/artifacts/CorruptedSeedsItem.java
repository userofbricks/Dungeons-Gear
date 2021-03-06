package com.infamous.dungeons_gear.artifacts;

import com.infamous.dungeons_gear.armor.BattleRobeItem;
import com.infamous.dungeons_gear.armor.EvocationRobeItem;
import com.infamous.dungeons_gear.armor.GuardsArmorItem;
import com.infamous.dungeons_gear.interfaces.IArtifact;
import com.infamous.dungeons_gear.items.ArtifactList;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.util.List;

import static com.infamous.dungeons_gear.utilties.AbilityUtils.isPetOfAttacker;

public class CorruptedSeedsItem extends Item implements IArtifact {
    public CorruptedSeedsItem(Properties properties) {
        super(properties);
    }

    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);

        List<LivingEntity> nearbyEntities = worldIn.getLoadedEntitiesWithinAABB(LivingEntity.class, new AxisAlignedBB(playerIn.getPosX() - 5, playerIn.getPosY() - 5, playerIn.getPosZ() - 5,
                playerIn.getPosX() + 5, playerIn.getPosY() + 5, playerIn.getPosZ() + 5), (nearbyEntity) -> {
            return nearbyEntity != playerIn && !isPetOfAttacker(playerIn, nearbyEntity) && nearbyEntity.isAlive();
        });
        for(LivingEntity nearbyEntity : nearbyEntities){

            EffectInstance entangled = new EffectInstance(Effects.SLOWNESS, 140, 4);
            EffectInstance poison = new EffectInstance(Effects.POISON, 140, 1);
            nearbyEntity.addPotionEffect(entangled);
            nearbyEntity.addPotionEffect(poison);

        }

        if(!playerIn.isCreative()){
            itemstack.damageItem(1, playerIn, (entity) -> {
                entity.sendBreakAnimation(handIn);
            });
        }
        setArtifactCooldown(playerIn, itemstack.getItem(), 400);
        return new ActionResult<>(ActionResultType.SUCCESS, itemstack);
    }

    public Rarity getRarity(ItemStack itemStack){
        return Rarity.RARE;
    }

    @Override
    public void addInformation(ItemStack stack, World world, List<ITextComponent> list, ITooltipFlag flag)
    {
        super.addInformation(stack, world, list, flag);

        if(stack.getItem() == ArtifactList.CORRUPTED_SEEDS){
            list.add(new StringTextComponent(TextFormatting.WHITE + "" + TextFormatting.ITALIC +
                    "A pouch of poisonous, corrupted seeds which grow into spiky grapple vines, entangling and slowly draining the life from its victims"));
            list.add(new StringTextComponent(TextFormatting.GREEN +
                    "Grow grapple vines, which inflict poison."));
            list.add(new StringTextComponent(TextFormatting.GREEN +
                    "Briefly Entangles Mobs"));
            list.add(new StringTextComponent(TextFormatting.GREEN +
                    "Poisons Entangled Mobs"));
            list.add(new StringTextComponent(TextFormatting.BLUE +
                    "7 Seconds Duration"));
            list.add(new StringTextComponent(TextFormatting.BLUE +
                    "20 Seconds Cooldown"));
        }
    }
}
