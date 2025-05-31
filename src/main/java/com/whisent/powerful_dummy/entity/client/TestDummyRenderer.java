package com.whisent.powerful_dummy.entity.client;

import com.whisent.powerful_dummy.Powerful_dummy;
import com.whisent.powerful_dummy.entity.TestDummyEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TestDummyRenderer extends MobRenderer<TestDummyEntity, TestDummyModel<TestDummyEntity>> {
    public TestDummyRenderer(EntityRendererProvider.Context context) {
        super(context, new TestDummyModel<>(context.bakeLayer(TestDummyModel.LAYER_LOCATION)), 0.5f);
    }
    @Override
    public ResourceLocation getTextureLocation(TestDummyEntity testDummyEntity) {
        return new ResourceLocation(Powerful_dummy.MODID, "textures/entity/test_dummy.png");
    }
}
