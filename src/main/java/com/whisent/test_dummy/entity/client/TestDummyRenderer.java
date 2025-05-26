package com.whisent.test_dummy.entity.client;

import com.whisent.test_dummy.Test_dummy;
import com.whisent.test_dummy.entity.TestDummyEntity;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class TestDummyRenderer extends MobRenderer<TestDummyEntity, TestDummyModel<TestDummyEntity>> {
    public TestDummyRenderer(EntityRendererProvider.Context context) {
        super(context, new TestDummyModel<>(context.bakeLayer(TestDummyModel.LAYER_LOCATION)), 0.5f);
    }
    @Override
    public ResourceLocation getTextureLocation(TestDummyEntity testDummyEntity) {
        return new ResourceLocation(Test_dummy.MODID, "textures/entity/test_dummy.png");
    }
}
