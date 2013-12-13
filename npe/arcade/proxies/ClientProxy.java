package npe.arcade.proxies;

import npe.arcade.client.ModelArcade;
import npe.arcade.client.RenderArcade;
import npe.arcade.entities.EntityArcade;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy {
    @Override
    public void initSounds() {
        // TODO Auto-generated method stub
    }

    @Override
    public void initRenderers() {
        ModelArcade model = new ModelArcade();
        RenderingRegistry.registerEntityRenderingHandler(EntityArcade.class, new RenderArcade(model));
        // TODO:
        //MinecraftForgeClient.registerItemRenderer(ItemInfo.ARCADE_ID + 256, new RenderArcadeItem(model));
    }
}
