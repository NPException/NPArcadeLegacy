package okushama.arcade.system;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class OSSettings {

	private static final File config = new File("config/arcade-okushamaos.cfg");

	public float[] colourForeground = { 1f, 1f, 1f }, colourBackground = { 0f, 0f, 0f };

	public void save() {
		try {
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(config)));
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			out.write(gson.toJson(this));
			out.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static OSSettings load() {
		OSSettings settings = null;
		try {
			if (!config.exists()) {
				config.createNewFile();
			}
			FileReader fr = new FileReader(config);
			settings = new Gson().fromJson(fr, OSSettings.class);
			if (settings == null) {
				settings = new OSSettings();
			}
			else {
				System.out.println("WOOT " + settings.colourBackground[2]);
			}
			settings.save();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return settings;
	}
}
