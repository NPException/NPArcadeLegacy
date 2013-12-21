package npe.arcade.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import npe.arcade.tileentities.TileEntityArcadeStool;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderArcadeStool extends TileEntitySpecialRenderer {

	private final ModelArcadeSeat model;
	private static final ResourceLocation texture = new ResourceLocation("npearcade:textures/models/arcadeSeat.png");

	public RenderArcadeStool() {
		model = new ModelArcadeSeat();
	}

	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTickTime) {
		GL11.glColor4f(1f, 1f, 1f, 1f);
		//The PushMatrix tells the renderer to "start" doing something.
		GL11.glPushMatrix();
		//This is setting the initial location.
		GL11.glTranslatef((float)x + 0.5F, (float)y + 0.625f, (float)z + 0.5F);

		GL11.glPushMatrix();

		//GL11.glDisable(GL11.GL_LIGHTING);
		Tessellator.instance.setColorRGBA_F(1f, 1f, 1f, 1f);
		//	OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);
		GL11.glRotatef(((TileEntityArcadeStool)te).placementRotation, 0, 1, 0);
		GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);

		Minecraft.getMinecraft().renderEngine.bindTexture(texture);
		model.render((Entity)null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);

		//GL11.glEnable(GL11.GL_LIGHTING);

		GL11.glPopMatrix();
		GL11.glPopMatrix();
	}
}
