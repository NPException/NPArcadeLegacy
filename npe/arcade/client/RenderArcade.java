package npe.arcade.client;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import npe.arcade.tileentities.TileEntityArcade;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;

public class RenderArcade extends TileEntitySpecialRenderer {

    public static final ResourceLocation texture = new ResourceLocation("npearcade", "notexturehere");//new ResourceLocation("npearcade", "textures/models/arcade.png");

    public void renderArcade(TileEntityArcade arcade, double x, double y, double z, float partialTickTime) {
        if (arcade.isTopPart()) {
            return;
        }

        GL11.glPushMatrix();
        GL11.glTranslated(x + .5f, y + .5f, z + .5f);
        GL11.glScalef(.5f, .5f, .5f);

        GL11.glRotatef(270 * arcade.getFacing(), 0, 1, 0);

        GL11.glColor4f(1, 1, 1, 1f);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(texture);
        Models.modelArcadeMachine.renderAll();

        GL11.glPopMatrix();
    }

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float partialTickTime) {
        renderArcade((TileEntityArcade)tileEntity, x, y, z, partialTickTime);
    }
}
