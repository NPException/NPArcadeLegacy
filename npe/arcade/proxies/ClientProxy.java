package npe.arcade.proxies;

import npe.arcade.client.Models;
import npe.arcade.client.RenderArcadeTechne;
import npe.arcade.tileentities.TileEntityArcade;
import cpw.mods.fml.client.registry.ClientRegistry;

public class ClientProxy extends CommonProxy {
    @Override
    public void initSounds() {
        // TODO Auto-generated method stub
    }

    @Override
    public void initRenderers() {
        Models.initModels();
        //ClientRegistry.bindTileEntitySpecialRenderer(TileEntityArcade.class, new RenderArcade());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityArcade.class, new RenderArcadeTechne());
        //MinecraftForgeClient.registerItemRenderer(BlockInfo.ARCADE_ID, new RenderArcade());
    }
}
