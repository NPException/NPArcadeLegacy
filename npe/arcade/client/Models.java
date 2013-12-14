package npe.arcade.client;

import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

public final class Models {

    public static IModelCustom modelArcadeMachine;

    public static final String ARCADE_MACHINE = "/assets/npearcade/models/arcadeMachine.obj";

    public static void initModels() {
        modelArcadeMachine = AdvancedModelLoader.loadModel(Models.ARCADE_MACHINE);
    }
}
