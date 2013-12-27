package npe.arcade.entities;

import npe.arcade.ArcadeMod;
import cpw.mods.fml.common.registry.EntityRegistry;

public class Entities {

	public static void init() {
		EntityRegistry.registerModEntity(EntityArcadeStool.class, "EntityArcade", 0, ArcadeMod.instance, 80, 3, false);
	}

}
