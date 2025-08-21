package com.whisent.powerful_dummy.entity.client;// Made with Blockbench 4.12.3
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.whisent.powerful_dummy.Powerful_dummy;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;


@OnlyIn(Dist.CLIENT)
public class TestDummyModel<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION =
			new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Powerful_dummy.MODID, "test_dummy"), "main");
	private final ModelPart test_dummy;
	private final ModelPart body;

	public TestDummyModel(ModelPart root) {
		this.test_dummy = root.getChild("test_dummy");
		this.body = this.test_dummy.getChild("body");
	}

    public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition test_dummy = partdefinition.addOrReplaceChild("test_dummy", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition body = test_dummy.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 37).addBox(-13.0F, -27.3125F, -1.0F, 26.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-6.0F, -42.0F, -6.0F, 12.0F, 12.0F, 12.0F, new CubeDeformation(0.0F))
		.texOffs(0, 41).addBox(-5.0F, -29.0F, -3.0F, 10.0F, 14.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(32, 41).addBox(-1.0F, -30.0F, -1.0F, 2.0F, 29.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 24).addBox(-6.0F, -1.0F, -6.0F, 12.0F, 1.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}


	@Override
	public void setupAnim(T t, float v, float v1, float v2, float v3, float v4) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int i, int i1, int i2) {
		test_dummy.render(poseStack, vertexConsumer, i, i1);
	}
}