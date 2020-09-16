package com.infamous.dungeons_gear.armor.models;// Made with Blockbench 3.6.6
// Exported for Minecraft version 1.15
// Paste this class into your mod and generate all required imports


import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.monster.AbstractIllagerEntity;
import net.minecraft.entity.monster.ZombiePigmanEntity;
import net.minecraft.entity.monster.ZombieVillagerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

@OnlyIn(Dist.CLIENT)
public class EvocationRobeModel<T extends LivingEntity> extends BipedModel<T> {
	private final EquipmentSlotType slot;
	private final LivingEntity entity;
	private final ModelRenderer Everything;
	private final ModelRenderer LeftArm;
	private final ModelRenderer RightArm;
	private final ModelRenderer Body;
	private final ModelRenderer Head;
    private final ModelRenderer Middle;
	private final ModelRenderer Back;
    private final ModelRenderer Brim;

	public EvocationRobeModel(float modelSize, EquipmentSlotType slot, LivingEntity entity) {
		super(modelSize, 0.0F, 128, 64);
		this.slot = slot;
		this.entity = entity;
		textureWidth = 64;
		textureHeight = 64;

        Everything = new ModelRenderer(this);
        Everything.setRotationPoint(0.0F, 24.0F, 0.0F);

        LeftArm = new ModelRenderer(this);
        LeftArm.setRotationPoint(5.0F, -22.0F, 0.0F);
        Everything.addChild(LeftArm);
        LeftArm.setTextureOffset(16, 31).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 1.0F, 1.0F, 1.0F);

        RightArm = new ModelRenderer(this);
        RightArm.setRotationPoint(-5.0F, -22.0F, 0.0F);
        Everything.addChild(RightArm);
        RightArm.setTextureOffset(0, 31).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 1.0F, 1.0F, 1.0F);

        Body = new ModelRenderer(this);
        Body.setRotationPoint(0.0F, -24.0F, 0.0F);
        Everything.addChild(Body);
        Body.setTextureOffset(0, 0).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, 1.0F, 1.0F, 1.0F);
        Body.setTextureOffset(0, 16).addBox(-4.0F, 12.5F, -2.0F, 8.0F, 4.0F, 4.0F, 1.0F, 1.0F, 1.0F);

        Head = new ModelRenderer(this);
        Head.setRotationPoint(0.0F, 0.0F, -5.0F);
        Everything.addChild(Head);
        setRotationAngle(Head, -0.1309F, 0.0F, 0.0F);

		boolean testificate =
				this.entity instanceof AbstractIllagerEntity ||
						this.entity.getType() == ForgeRegistries.ENTITIES.getValue(new ResourceLocation("savageandravage:skeleton_villager")) ||
						this.entity instanceof ZombieVillagerEntity ||
						this.entity instanceof AbstractVillagerEntity ||
						this.entity.getType() == ForgeRegistries.ENTITIES.getValue(new ResourceLocation("guardvillagers:guard"));

		int testificateHelmetShiftUp = testificate ? 2 : 0;

        Back = new ModelRenderer(this);
        Back.setRotationPoint(0.0F, -0.1141F, 11.2547F);
        Head.addChild(Back);
        setRotationAngle(Back, 1.0472F, 0.0F, 0.0F);
        Back.setTextureOffset(40, 25).addBox(-4.0F + 0.001F, -28.7859F + 13.125F - testificateHelmetShiftUp, 32.1453F - 22.75F - 0.25F, 7.99F, 6.0F, 4.0F, 0.5F, 0.5F, 0.5F);

        Middle = new ModelRenderer(this);
        Middle.setRotationPoint(0.0F, 0.0F, -5.0F);
        Head.addChild(Middle);
        Middle.setTextureOffset(32, 0).addBox(-4.0F, -46.0F + 26.0F - testificateHelmetShiftUp, -4.0F + 4.0F, 8.0F, 13.0F, 8.0F, 0.5F, 0.5F, 0.5F);

        Brim = new ModelRenderer(this);
        Brim.setRotationPoint(0.0F, 0.0F, 0.0F);
        Head.addChild(Brim);
        setRotationAngle(Brim, 0.1309F, 0.0F, 0.0F);
        Brim.setTextureOffset(0, 47).addBox(-8.0F, -33.7F + 26.0F - testificateHelmetShiftUp, -8.0F, 16.0F, 1.0F, 16.0F, 0.5F, 0.5F, 0.5F);
        /*
		Everything = new ModelRenderer(this);
		Everything.setRotationPoint(0.0F, 24.0F, 0.0F);


		LeftLeg = new ModelRenderer(this);
		LeftLeg.setRotationPoint(1.9F, -12.0F, 0.0F);
		Everything.addChild(LeftLeg);


		RightLeg = new ModelRenderer(this);
		RightLeg.setRotationPoint(-1.9F, -12.0F, 0.0F);
		Everything.addChild(RightLeg);


		LeftArm = new ModelRenderer(this);
		LeftArm.setRotationPoint(5.0F, -22.0F, 0.0F);
		Everything.addChild(LeftArm);
		LeftArm.setTextureOffset(43, 41).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.5F, false);

		RightArm = new ModelRenderer(this);
		RightArm.setRotationPoint(-5.0F, -22.0F, 0.0F);
		Everything.addChild(RightArm);
		RightArm.setTextureOffset(24, 24).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.5F, false);

		Body = new ModelRenderer(this);
		Body.setRotationPoint(0.0F, -24.0F, 0.0F);
		Everything.addChild(Body);
		Body.setTextureOffset(0, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, 0.5F, 0.5F, 0.6F);
		Body.setTextureOffset(0, 8).addBox(-4.0F, 12.5F, -2.0F, 8.0F, 4.0F, 4.0F, 0.5F, 0.5F, 0.6F);

		Head = new ModelRenderer(this);
		Head.setRotationPoint(0.0F, 0.0F, -1.0F);
		Everything.addChild(Head);
		Head.setTextureOffset(0, 47).addBox(-7.0F, -33.7F + 27.0F, -7.0F, 14.0F, 1.0F, 14.0F, 0.5F, false);
		Head.setTextureOffset(26, 0).addBox(-4.0F, -45.7F + 27.0F, -2.3145F, 8.0F, 13.0F, 6.0F, 0.5F, 0.5F, 0.5F);

		Back = new ModelRenderer(this);
		Back.setRotationPoint(0.0F, -37.8F, 4.2F);
		Head.addChild(Back);
		setRotationAngle(Back, 0.7854F, 0.0F, 0.0F);
		Back.setTextureOffset(42, 25).addBox(-3.95F, -5.4142F + 19.0F, 1.5858F - 19.5F, 7.9F, 8.0F, 4.0F, 0.5F, false);

         */
	}

	@Override
	public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha){
		boolean testificate =
				this.entity instanceof AbstractIllagerEntity ||
						this.entity.getType() == ForgeRegistries.ENTITIES.getValue(new ResourceLocation("savageandravage:skeleton_villager")) ||
						this.entity instanceof ZombieVillagerEntity ||
						this.entity instanceof AbstractVillagerEntity ||
						this.entity.getType() == ForgeRegistries.ENTITIES.getValue(new ResourceLocation("guardvillagers:guard"));

		boolean piglin =
				this.entity instanceof ZombiePigmanEntity;



		if (this.slot == EquipmentSlotType.HEAD) {
		/*
			if (piglin) {
				matrixStackIn.push();
				this.piglin_helmet1.copyModelAngles(this.bipedHead);
				if (this.entity.isChild()) {
					matrixStack.scale(0.8F, 0.8F, 0.8F);
					this.piglin_helmet1.setRotationPoint(0.0F, 15.0F, 0.0F);
				}
				this.piglin_helmet1.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
				matrixStackIn.pop();

			} else if (testificate) {
				matrixStackIn.push();
				this.illager_helmet1.copyModelAngles(this.bipedHead);
				if (this.entity.isChild()) {
					matrixStackIn.scale(0.8F, 0.8F, 0.8F);
					this.illager_helmet1.setRotationPoint(0.0F, 15.0F, 0.0F);
				}
				this.illager_helmet1.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
				matrixStackIn.pop();
			} else {

		 */
			matrixStackIn.push();
			this.Head.copyModelAngles(this.bipedHead);
			if (this.entity.isChild()) {
				matrixStackIn.scale(0.8F, 0.8F, 0.8F);
				this.Head.setRotationPoint(0.0F, 15.0F, 0.0F);
			}
			this.Head.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
			matrixStackIn.pop();
			//}
		}

		if (this.slot == EquipmentSlotType.CHEST) {
			matrixStackIn.push();

			this.Body.copyModelAngles(this.bipedBody);
			this.LeftArm.copyModelAngles(this.bipedLeftArm);
			this.RightArm.copyModelAngles(this.bipedRightArm);
			if (this.entity.isChild()) {
				matrixStackIn.scale(0.5F, 0.5F, 0.5F);
				this.Body.setRotationPoint(0.0F, 24.0F, 0.0F);
				this.LeftArm.setRotationPoint(5.0F, 24.0F, 0.0F);
				this.RightArm.setRotationPoint(-5.0F, 24.0F, 0.0F);
			}
			this.LeftArm.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
			this.RightArm.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
			if (testificate) {
				matrixStackIn.scale(1.0F, 1.0F, 1.3F);
			}
			this.Body.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
			matrixStackIn.pop();
		}
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}