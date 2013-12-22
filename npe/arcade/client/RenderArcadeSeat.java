package npe.arcade.client;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import npe.arcade.entities.EntityArcadeSeat;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderArcadeSeat extends Render {

	private final ModelArcadeSeat model;
	private static final ResourceLocation texture = new ResourceLocation("npearcade:textures/models/arcadeSeat.png");

	public RenderArcadeSeat() {
		model = new ModelArcadeSeat();
		shadowSize = 0.4F;
	}

	private void doRenderSeat(EntityArcadeSeat seat, double x, double y, double z, float yaw, float partialTickTime) {
		GL11.glPushMatrix();
		GL11.glTranslatef((float)x, (float)y + 0.625f, (float)z);
		GL11.glScalef(-1F, -1F, 1F);

		GL11.glPushMatrix();
		GL11.glRotatef(seat.placementRotation, 0, 1, 0);

		bindEntityTexture(seat);
		model.render(seat, 0, 0, 0, 0, 0, 0.0625F);

		GL11.glPopMatrix();
		GL11.glPopMatrix();
	}

	@Override
	public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTickTime) {
		doRenderSeat((EntityArcadeSeat)entity, x, y, z, yaw, partialTickTime);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return texture;
	}

}
