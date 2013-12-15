package npe.arcade.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import npe.arcade.tileentities.TileEntityArcadeBase;

import org.lwjgl.opengl.GL11;

public class RenderArcadeBase extends TileEntitySpecialRenderer {

    private final ModelArcadeBase model;
    private final ResourceLocation texture;

    public RenderArcadeBase() {
        model = new ModelArcadeBase();
        texture = new ResourceLocation("npearcade:textures/models/arcade.png");
    }

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float scale) {
        render((TileEntityArcadeBase)te, x, y, z, scale);
    }

    private void render(TileEntityArcadeBase arcadeBase, double x, double y, double z, float scale) {
        GL11.glColor4f(1f, 1f, 1f, 1f);
        //The PushMatrix tells the renderer to "start" doing something.
        GL11.glPushMatrix();
        //This is setting the initial location.
        GL11.glTranslatef((float)x + 0.5F, (float)y + 1.5F, (float)z + 0.5F);

        GL11.glPushMatrix();

        GL11.glRotatef(180 - 90 * arcadeBase.getBlockMetadata(), 0, 1, 0);
        GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);

        Minecraft.getMinecraft().renderEngine.bindTexture(texture);
        model.render((Entity)null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);

        GL11.glPopMatrix();
        GL11.glPopMatrix();
    }

}
