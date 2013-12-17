package npe.arcade.client;

import static org.lwjgl.opengl.GL11.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import npe.arcade.tileentities.TileEntityArcade;
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
        glPushMatrix();

        glColor4f(1f, 1f, 1f, 1f);
        //This is setting the initial location.
        glTranslatef((float)x + 0.5F, (float)y + 1.5F, (float)z + 0.5F);

        glPushMatrix();

        glRotatef(180 - 90 * arcade.getBlockMetadata(), 0, 1, 0);
        glRotatef(180F, 0.0F, 0.0F, 1.0F);

        // the model included the base before, and the top part is still shifted 1 up
        glTranslatef(0f, 1f, 0f);

        // save the oldLightX for the casing render
        float oldLightX = OpenGlHelper.lastBrightnessX;
        float oldLightY = OpenGlHelper.lastBrightnessY;

        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);

        // SCREEN RENDERING //
        if (arcade.isImageChanged) {
            TextureUtil.uploadTextureImageAllocate(arcade.getGlTextureId(), arcade.getScreenImage(), false, false);
            arcade.isImageChanged = false;
        }
        
        
       
      
        glPushMatrix();
        glRotatef(25f, -1f, 0f, 0f);
        glTranslatef(0f, 0f, 0.1f);
        glDisable(GL_CULL_FACE);
        RenderHelper.disableStandardItemLighting();
      
        glPushMatrix();
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        double tx = -0.5d, ty = -0.45d, w = 1, h = 1d;
       // glScalef(0.85f, 1.1f, 1f);
        glBindTexture(GL_TEXTURE_2D, 0);

        tessellator.setColorRGBA_F(0F, 0F, 0F, 1F);
        tessellator.addVertexWithUV(tx+w, ty, 0, 1, 0);
        tessellator.addVertexWithUV(tx+w, ty+h, 0, 1, 1);
        tessellator.addVertexWithUV(tx, ty+h, 0, 0, 1);
        tessellator.addVertexWithUV(tx, ty, 0, 0, 0);
        tessellator.draw();
        glPopMatrix();
 
        glPushMatrix();
        glTranslatef(0f, 0f, -0.005f);
        tessellator = Tessellator.instance;
        glBindTexture(GL_TEXTURE_2D, arcade.getGlTextureId());

        tessellator.startDrawingQuads();
        tx = -0.38d; ty = -0.36d; w = 0.752d; h = 0.96d;
       // glScalef(0.85f, 1.1f, 1f);
        tessellator.setColorRGBA_F(1.0F, 1.0F, 1.0F, 1F);
        tessellator.addVertexWithUV(tx+w, ty, 0, 1, 0);
        tessellator.addVertexWithUV(tx+w, ty+h, 0, 1, 1);
        tessellator.addVertexWithUV(tx, ty+h, 0, 0, 1);
        tessellator.addVertexWithUV(tx, ty, 0, 0, 0);
        tessellator.draw();
        glPopMatrix();
        
        tx = -0.38d; ty = -0.45d; w = 0.752d; h = 0.96d;
 
        glTranslatef(0f, 0f, -0.01f);
		ResourceLocation r = new ResourceLocation("npearcade", "textures/models/arcadeScreenFrame.png");
		Minecraft.getMinecraft().getTextureManager().bindTexture(r);
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_F(1.0F, 1.0F, 1.0F, 1F);
        tessellator.addVertexWithUV(tx+w, ty, 0, 1, 0);
        tessellator.addVertexWithUV(tx+w, ty+h, 0, 1, 1);
        tessellator.addVertexWithUV(tx, ty+h, 0, 0, 1);
        tessellator.addVertexWithUV(tx, ty, 0, 0, 0);
        tessellator.draw();
      //  RenderHelper.enableStandardItemLighting();
        glEnable(GL_CULL_FACE);
        glPopMatrix();
        //Tessellator.instance.setColorOpaque_F(1f, 1f, 1f);
       // modelScreen.render((Entity)null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
        RenderHelper.enableStandardItemLighting();
        // RENDER CASING //
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, oldLightX, oldLightY);
        Minecraft.getMinecraft().renderEngine.bindTexture(textureTop);
        modelTop.render((Entity)null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
        glPopMatrix();
        glPopMatrix();
    }

}
