package npe.arcade.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import npe.arcade.tileentities.TileEntityArcade;

import org.lwjgl.opengl.GL11;

public class RenderArcadeTechne extends TileEntitySpecialRenderer {

    //The model of your block
    private final ModelArcadeTechne modelBody;
    private final ModelArcadeScreenTechne modelScreen;
    private final ResourceLocation textureBody;
    private final ResourceLocation textureScreen;

    public RenderArcadeTechne() {
        modelBody = new ModelArcadeTechne();
        textureBody = new ResourceLocation("npearcade:textures/models/arcadeTechne.png");
        modelScreen = new ModelArcadeScreenTechne();
        textureScreen = new ResourceLocation("npearcade:textures/models/arcadeScreenTechne.png");
    }

    private void render(TileEntityArcade arcade, double x, double y, double z, float scale) {
        GL11.glColor4f(1f, 1f, 1f, 1f);
        //The PushMatrix tells the renderer to "start" doing something.
        GL11.glPushMatrix();
        //This is setting the initial location.
        GL11.glTranslatef((float)x + 0.5F, (float)y + 1.5F, (float)z + 0.5F);

        GL11.glPushMatrix();

        GL11.glRotatef(180 - 90 * arcade.getFacing(), 0, 1, 0);
        GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);

        modelBody.isTopPart = arcade.isTopPart();

        if (modelBody.isTopPart) {
            GL11.glTranslatef(0f, 1f, 0f);
        }
        // RENDER CASING
        Minecraft.getMinecraft().renderEngine.bindTexture(textureBody);
        modelBody.render((Entity)null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);

        if (modelBody.isTopPart) {
            // SCREEN RENDERING PART //
            Minecraft.getMinecraft().renderEngine.bindTexture(textureScreen);

            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_BLEND);
            Tessellator.instance.setColorOpaque_F(1f, 1f, 1f);
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 0f, 240f);
            modelScreen.render((Entity)null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
            GL11.glEnable(GL11.GL_LIGHTING);
        }

        GL11.glPopMatrix();
        GL11.glPopMatrix();
    }

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float scale) {
        render((TileEntityArcade)te, x, y, z, scale);
    }

    //    //Set the lighting stuff, so it changes it's brightness properly.       
    //    private void adjustLightFixture(World world, int i, int j, int k, Block block) {
    //        Tessellator tess = Tessellator.instance;
    //        float brightness = block.getBlockBrightness(world, i, j, k);
    //        int skyLight = world.getLightBrightnessForSkyBlocks(i, j, k, 0);
    //        int modulousModifier = skyLight % 65536;
    //        int divModifier = skyLight / 65536;
    //        tess.setColorOpaque_F(brightness, brightness, brightness);
    //        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, modulousModifier, divModifier);
    //    }
}
