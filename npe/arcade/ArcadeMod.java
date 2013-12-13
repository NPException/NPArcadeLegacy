package npe.arcade;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import npe.arcade.blocks.Blocks;
import npe.arcade.config.ConfigHandler;
import npe.arcade.network.PacketHandler;
import npe.arcade.proxies.CommonProxy;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;

@Mod(modid = ModInfo.MOD_ID, name = ModInfo.NAME, version = ModInfo.VERSION)
@NetworkMod(channels = { ModInfo.CHANNEL }, clientSideRequired = true, serverSideRequired = true, packetHandler = PacketHandler.class)
public class ArcadeMod {

    @Instance(ModInfo.MOD_ID)
    public static ArcadeMod instance;

    @SidedProxy(serverSide = "npe.arcade.proxies.CommonProxy", clientSide = "npe.arcade.proxies.ClientProxy")
    public static CommonProxy proxy;

    /**
     * Init Items, Blocks, Sounds, Renderers, and Config here
     * 
     * @param event
     */
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ConfigHandler.init(event.getSuggestedConfigurationFile());

        Blocks.init();

        proxy.initSounds();
        proxy.initRenderers();
    }

    /**
     * Add names and register recipes here
     * 
     * @param event
     */
    @EventHandler
    public void init(FMLInitializationEvent event) {
        Blocks.addNames();
        Blocks.registerRecipes();
        Blocks.registerTileEntities();
    }

    /**
     * I have no idea what I could do here...
     * 
     * @param event
     */
    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {

    }

    public static final CreativeTabs CREATIVE_TAB = new CreativeTabs("npe.arcade.tab") {
        @Override
        public ItemStack getIconItemStack() {
            return new ItemStack(Block.hopperBlock);
        }
    };
}
