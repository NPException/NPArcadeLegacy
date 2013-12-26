package npe.arcade.client;

import static org.lwjgl.opengl.GL11.*;
import net.minecraft.client.Minecraft;
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
	private static final ResourceLocation textureTop = new ResourceLocation("npearcade", "textures/models/arcade.png");

	public RenderArcadeTop() {
		modelTop = new ModelArcadeTop();
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

		// SCREEN RENDERING //

		// // keep this stuff here, until it is safe to say that uploading the texture outside of the render thread works.
		//		if (arcade.textureSizeChanged || arcade.glTextureId == -1) {
		//			TextureUtil.allocateTexture(arcade.getGlTextureId(true), arcade.getScreenImage().getWidth(), arcade.getScreenImage().getHeight());
		//			arcade.textureSizeChanged = false;
		//		}
		//		if (arcade.isImageChanged) {
		//			TextureUtil.uploadTexture(arcade.getGlTextureId(false), arcade.getScreenImageData(), arcade.getScreenImage().getWidth(), arcade.getScreenImage().getHeight());
		//			arcade.isImageChanged = false;
		//		}

		glPushMatrix();
		glDisable(GL_LIGHTING);
		glRotatef(25f, -1f, 0f, 0f);
		glTranslatef(0f, 0f, 0.1f);
		glDisable(GL_CULL_FACE);

		// Render default background //
		glPushMatrix();
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		double tx = -0.5d, ty = -0.45d, w = 1, h = 1d;
		glBindTexture(GL_TEXTURE_2D, 0);
		tessellator.setColorRGBA_F(0F, 0F, 0F, 1F);
		tessellator.addVertexWithUV(tx + w, ty, 0, 1, 0);
		tessellator.addVertexWithUV(tx + w, ty + h, 0, 1, 1);
		tessellator.addVertexWithUV(tx, ty + h, 0, 0, 1);
		tessellator.addVertexWithUV(tx, ty, 0, 0, 0);
		tessellator.draw();
		glPopMatrix();

		// render game screen //
		glPushMatrix();
		glTranslatef(-0.005f, -0.06f, -0.005f);
		tessellator = Tessellator.instance;
		if (arcade.isImageChanged) {
			TextureUtil.uploadTexture(arcade.getGlTextureId(false), arcade.getScreenImageData(), arcade.getScreenTextureWidth(), arcade.getScreenTextureHeight());
			arcade.isImageChanged = false;
		}
		glBindTexture(GL_TEXTURE_2D, arcade.getGlTextureId(false));
		tessellator.startDrawingQuads();
		tx = -0.37d;
		ty = -0.39d;
		w = 0.752d;
		h = 0.96d;
		//		float r = 0f, g = 0f, b = 0f;
		//
		//		// broken effect test
		//		Random rand = new Random();
		//		int c = rand.nextInt(3);
		//		boolean doBrokenEffect1 = false;
		//		if (doBrokenEffect1) {
		//			switch (c) {
		//				case 0:
		//					r = 1f;
		//					break;
		//				case 1:
		//					g = 1f;
		//					break;
		//				case 2:
		//					b = 1f;
		//					break;
		//			}
		//		}
		//		else {
		//			r = g = b = 1f;
		//		}

		// render quad
		tessellator.setColorRGBA_F(1f, 1f, 1f, 1F);
		tessellator.addVertexWithUV(tx + w, ty, 0, 1, 0);
		tessellator.addVertexWithUV(tx + w, ty + h, 0, 1, 1);
		tessellator.addVertexWithUV(tx, ty + h, 0, 0, 1);
		tessellator.addVertexWithUV(tx, ty, 0, 0, 0);
		tessellator.draw();
		glPopMatrix();

		glEnable(GL_CULL_FACE);
		glPopMatrix();

		// RENDER CASING //
		glEnable(GL_LIGHTING);
		Tessellator.instance.setColorRGBA_F(1f, 1f, 1f, 1f);
		Minecraft.getMinecraft().renderEngine.bindTexture(textureTop);
		modelTop.render((Entity)null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		glPopMatrix();
		glPopMatrix();
	}
}
