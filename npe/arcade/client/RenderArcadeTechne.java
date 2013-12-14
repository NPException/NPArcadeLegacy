package npe.arcade.client;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import npe.arcade.tileentities.TileEntityArcade;

import org.lwjgl.opengl.GL11;

public class RenderArcadeTechne extends TileEntitySpecialRenderer {

    //The model of your block
    private final ModelArcadeTechne model;
    private ModelArcadeScreenTechne modelScreen;

    public RenderArcadeTechne() {
        model = new ModelArcadeTechne();
        modelScreen = new ModelArcadeScreenTechne();
    }

    private void render(TileEntityArcade arcade, double x, double y, double z, float scale) {
        if (arcade.isTopPart()) {
            return;
        }

        //The PushMatrix tells the renderer to "start" doing something.
        GL11.glPushMatrix();
        //This is setting the initial location.
        GL11.glTranslatef((float)x + 0.5F, (float)y + 1.5F, (float)z + 0.5F);
        //This is the texture of your block. It's pathed to be the same place as your other blocks here.
        ResourceLocation texture = new ResourceLocation("npearcade:textures/models/arcadeTechne.png");
        //binding the textures
        Minecraft.getMinecraft().renderEngine.bindTexture(texture);

        //This rotation part is very important! Without it, your model will render upside-down! And for some reason you DO need PushMatrix again!                      
        GL11.glPushMatrix();

        GL11.glColor4f(1f, 1f, 1f, .5f);

        GL11.glRotatef(180 - 90 * arcade.getFacing(), 0, 1, 0);
        GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);

        //A reference to your Model file. Again, very important.
        model.render((Entity)null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);

        // SCREEN RENDERING PART //
        //        GL11.glPushMatrix();
        //        GL11.glDisable(GL11.GL_LIGHTING);
        //        GL11.glColor4f(1f, 1f, 1f, 1f);
        texture = new ResourceLocation("npearcade:textures/models/arcadeScreenTechne.png");
        Minecraft.getMinecraft().renderEngine.bindTexture(texture);
        modelScreen = new ModelArcadeScreenTechne();
        modelScreen.render((Entity)null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
        //        GL11.glPopMatrix();

        //Tell it to stop rendering for both the PushMatrix's
        GL11.glPopMatrix();
        GL11.glPopMatrix();
    }

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float scale) {
        render((TileEntityArcade)te, x, y, z, scale);
    }

    //Set the lighting stuff, so it changes it's brightness properly.       
    private void adjustLightFixture(World world, int i, int j, int k, Block block) {
        Tessellator tess = Tessellator.instance;
        float brightness = block.getBlockBrightness(world, i, j, k);
        int skyLight = world.getLightBrightnessForSkyBlocks(i, j, k, 0);
        int modulousModifier = skyLight % 65536;
        int divModifier = skyLight / 65536;
        tess.setColorOpaque_F(brightness, brightness, brightness);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, modulousModifier, divModifier);
    }
}
