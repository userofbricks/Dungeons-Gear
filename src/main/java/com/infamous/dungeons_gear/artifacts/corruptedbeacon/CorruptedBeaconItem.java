package com.infamous.dungeons_gear.artifacts.corruptedbeacon;

import java.util.List;

import com.infamous.dungeons_gear.interfaces.IArtifact;
import com.infamous.dungeons_gear.interfaces.ISoulGatherer;
import com.infamous.dungeons_gear.items.ArtifactList;

import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class CorruptedBeaconItem extends Item implements IArtifact, ISoulGatherer {
    public static final int maxDamage = 100;

    public CorruptedBeaconItem(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand handIn) {
        ItemStack stack = player.getHeldItem(handIn);
        if (player.experienceTotal >= 6 || player.isCreative()) {
            if (!world.isRemote) {
                BeamEntity beamEntity = new BeamEntity(world,player);

                Vec3d upVector = player.getUpVector(1.0F);
                Quaternion quaternion = new Quaternion(new Vector3f(upVector), 0, true);
                Vec3d lookVector = player.getLook(1.0F);
                Vector3f vector3f = new Vector3f(lookVector);
                vector3f.transform(quaternion);
                beamEntity.shoot((double)vector3f.getX(), (double)vector3f.getY(), (double)vector3f.getZ(), 10.0F, 0.0F);
                //beamEntity.setArrowMotion(player, player.rotationPitch, player.rotationYaw, 0.0F, 8.0F, 0.0F);
                //beamEntity.setNoGravity(true);
                world.playSound((PlayerEntity)null, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.BLOCK_BEACON_ACTIVATE, SoundCategory.PLAYERS, 64.0F, 1.0F);
                world.addEntity(beamEntity);
            }
            if (!player.isCreative()) {
                player.giveExperiencePoints(-6);
                stack.damageItem(1, player, (entity) -> {
                    entity.sendBreakAnimation(handIn);
                });
            }
        }
        return new ActionResult<>(ActionResultType.SUCCESS,stack);
    }

    public Rarity getRarity(ItemStack itemStack){
        return Rarity.RARE;
    }

    @Override
    public void addInformation(ItemStack stack, World world, List<ITextComponent> list, ITooltipFlag flag)
    {
        super.addInformation(stack, world, list, flag);

        if(stack.getItem() == ArtifactList.CORRUPTED_BEACON){
            list.add(new StringTextComponent(TextFormatting.WHITE + "" + TextFormatting.ITALIC +
                    "The Corrupted Beacon holds immense power within. It waits for the moment to unleash its wrath."));
            list.add(new StringTextComponent(TextFormatting.GREEN +
                    "Fires a high-powered beam that continuously damages mobs."));
            list.add(new StringTextComponent(TextFormatting.LIGHT_PURPLE +
                    "+1 XP Gathering"));
            list.add(new StringTextComponent(TextFormatting.BLUE +
                    "Requires 6 XP"));
        }
    }

    @Override
    public int getGatherAmount(ItemStack stack) {
        return 1;
    }
}
