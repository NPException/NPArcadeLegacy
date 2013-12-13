package npe.arcade.client;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import npe.arcade.entities.EntityArcade;

import org.lwjgl.opengl.GL11;

public class RenderArcade extends Render {
    private final ModelArcade model;

    public RenderArcade(ModelArcade model) {
        this.model = model;
        //shadowSize = 0.5F;
    }

    public static final ResourceLocation texture = new ResourceLocation("npearcade", "textures/models/arcade.png");

    public void renderArcade(EntityArcade arcade, double x, double y, double z, float yaw, float partialTickTime) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)x, (float)y, (float)z);
        GL11.glScalef(-1F, -1F, 1F);

        bindEntityTexture(arcade);

        model.render(arcade, 0f, 0f, 0f, 0f, 0f, 0f);

        GL11.glPopMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return texture;
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTickTime) {
        renderArcade((EntityArcade)entity, x, y, z, yaw, partialTickTime);
    }
}
