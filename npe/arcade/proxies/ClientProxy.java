package npe.arcade.proxies;

import npe.arcade.client.RenderArcadeBase;
import npe.arcade.client.RenderArcadeSeat;
import npe.arcade.client.RenderArcadeTop;
import npe.arcade.entities.EntityArcadeSeat;
import npe.arcade.tileentities.TileEntityArcade;
import npe.arcade.tileentities.TileEntityArcadeBase;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy {
    @Override
    public void initSounds() {
        // TODO Auto-generated method stub
    }

    @Override
    public void initRenderers() {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityArcade.class, new RenderArcadeTop());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityArcadeBase.class, new RenderArcadeBase());
        RenderingRegistry.registerEntityRenderingHandler(EntityArcadeSeat.class, new RenderArcadeSeat());
        //MinecraftForgeClient.registerItemRenderer(BlockInfo.ARCADE_ID, new RenderArcadeTechne());
    }
}
