package npe.arcade.proxies;

import npe.arcade.client.RenderArcadeBase;
import npe.arcade.client.RenderArcadeStool;
import npe.arcade.client.RenderArcadeTop;
import npe.arcade.client.TickHandlerClient;
import npe.arcade.tileentities.TileEntityArcade;
import npe.arcade.tileentities.TileEntityArcadeBase;
import npe.arcade.tileentities.TileEntityArcadeStool;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class ClientProxy extends CommonProxy {
	@Override
	public void initSounds() {
		// TODO Auto-generated method stub
	}

	@Override
	public void initRenderers() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityArcade.class, new RenderArcadeTop());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityArcadeBase.class, new RenderArcadeBase());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityArcadeStool.class, new RenderArcadeStool());

		TickRegistry.registerTickHandler(new TickHandlerClient(), Side.CLIENT);
		//MinecraftForgeClient.registerItemRenderer(BlockInfo.ARCADE_ID, new RenderArcadeTechne());
	}
}
