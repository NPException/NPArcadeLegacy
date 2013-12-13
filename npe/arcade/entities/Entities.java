package npe.arcade.entities;

import npe.arcade.ArcadeMod;
import cpw.mods.fml.common.registry.EntityRegistry;

public class Entities {

    public static void init() {
        EntityRegistry.registerModEntity(EntityArcade.class, "EntityArcade", 2, ArcadeMod.instance, 80, 3, false);
    }

}
