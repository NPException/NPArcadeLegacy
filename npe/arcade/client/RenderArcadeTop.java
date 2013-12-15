package npe.arcade.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import npe.arcade.tileentities.TileEntityArcade;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderArcadeTop extends TileEntitySpecialRenderer {

    //The model of your block
    private final ModelArcadeTop modelTop;
    private final ModelArcadeScreen modelScreen;
    private static final ResourceLocation textureTop = new ResourceLocation("npearcade:textures/models/arcade.png");

    public RenderArcadeTop() {
        modelTop = new ModelArcadeTop();
        modelScreen = new ModelArcadeScreen();
    }

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float scale) {
        render((TileEntityArcade)te, x, y, z, scale);
    }

    private void render(TileEntityArcade arcade, double x, double y, double z, float scale) {

        //The PushMatrix tells the renderer to "start" doing something.
        GL11.glPushMatrix();

        GL11.glColor4f(1f, 1f, 1f, 1f);
        //This is setting the initial location.
        GL11.glTranslatef((float)x + 0.5F, (float)y + 1.5F, (float)z + 0.5F);

        GL11.glPushMatrix();

        GL11.glRotatef(180 - 90 * arcade.getBlockMetadata(), 0, 1, 0);
        GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);

        // the model included the base before, and the top part is still shifted 1 up
        GL11.glTranslatef(0f, 1f, 0f);

        // SCREEN RENDERING //
        if (arcade.isImageChanged) {
            TextureUtil.uploadTextureImageAllocate(arcade.getGlTextureId(), arcade.getScreenImage(), false, false);
            arcade.isImageChanged = false;
        }
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, arcade.getGlTextureId());

        GL11.glDisable(GL11.GL_LIGHTING);
        Tessellator.instance.setColorOpaque_F(1f, 1f, 1f);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 0f, 240f);
        modelScreen.render((Entity)null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
        GL11.glEnable(GL11.GL_LIGHTING);

        // RENDER CASING //
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        Minecraft.getMinecraft().renderEngine.bindTexture(textureTop);
        modelTop.render((Entity)null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
        GL11.glPopMatrix();
    }

}
