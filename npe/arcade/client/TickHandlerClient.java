package npe.arcade.client;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.GuiIngameForge;
import npe.arcade.entities.EntityArcadeStool;

import org.lwjgl.input.Keyboard;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class TickHandlerClient implements ITickHandler {

	public static boolean canDisablePlayerInput = false;
	private boolean getClientPlayerInput = false;
	private PlayerSettingBackup playerKeys = null;

	public TickHandlerClient() {
		try {
			Gson gson = new Gson();
			File f = new File("config/arcade-[player-data-backup].cfg");
			if (!f.exists()) {
				f.createNewFile();
			}
			playerKeys = gson.fromJson(new FileReader(f), PlayerSettingBackup.class);
			if (playerKeys == null) {
				playerKeys = new PlayerSettingBackup();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static class PlayerSettingBackup {
		private final List<Integer> backupKeys = new ArrayList<Integer>();
		private float backupFov = -999f;

		public void save() {
			try {
				int amt = 0;
				for (int i : backupKeys) {
					if (i == 0) {
						amt++;
					}
				}
				if (amt < 8) {
					Gson gson = new GsonBuilder().setPrettyPrinting().create();
					BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File("config/arcade-[player-data-backup].cfg"))));
					out.write(gson.toJson(this));
					out.close();
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
		if (type.equals(EnumSet.of(TickType.PLAYER))) {
			try {
				EntityClientPlayerMP player = (EntityClientPlayerMP)tickData[0];
				if (player.ridingEntity instanceof EntityArcadeStool) {
					updatePlayerFOV(true);
					if (canDisablePlayerInput) {
						if (getClientPlayerInputEnabled()) {
							System.out.println("Disabled player input!");
							setClientPlayerInput(false);
						}
					}
				}
				else {
					updatePlayerFOV(false);

					if (canDisablePlayerInput) {
						if (!getClientPlayerInputEnabled()) {
							System.out.println("Enabled player input!");
							setClientPlayerInput(true);
						}
					}
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {

	}

	public boolean getClientPlayerInputEnabled() {
		return getClientPlayerInput;
	}

	public void setClientPlayerInput(boolean enabled) {
		GameSettings gameSettings = Minecraft.getMinecraft().gameSettings;

		if (enabled) {
			if (!playerKeys.backupKeys.isEmpty()) {
				for (int i = 0; i < playerKeys.backupKeys.size(); i++) {
					gameSettings.setKeyBinding(i, playerKeys.backupKeys.get(i));
					gameSettings.keyBindings[i].pressed = false;
				}
			}
			KeyBinding.resetKeyBindingArrayAndHash();
			getClientPlayerInput = true;
		}
		else {
			//if(playerKeys.backupKeys.isEmpty())
			{
				playerKeys.backupKeys.clear();
				for (int i = 0; i < gameSettings.keyBindings.length; i++) {
					KeyBinding kb = gameSettings.keyBindings[i];
					gameSettings.keyBindings[i].pressed = false;
					playerKeys.backupKeys.add(kb.keyCode);
					if (!kb.equals(gameSettings.keyBindSneak) && !kb.equals(gameSettings.keyBindUseItem)) {
						gameSettings.setKeyBinding(i, Keyboard.KEY_NONE);
					}
				}
			}
			playerKeys.save();
			KeyBinding.resetKeyBindingArrayAndHash();
			getClientPlayerInput = false;
		}
	}

	public float originalFov = -999;
	public float zoomFov = -0.5f;

	public void updatePlayerFOV(boolean seated) {
		float currentFov = Minecraft.getMinecraft().gameSettings.fovSetting;
		if (playerKeys.backupFov != -999f) {
			originalFov = playerKeys.backupFov;
		}
		if (currentFov >= 0 && originalFov == -999) {
			originalFov = currentFov;
			playerKeys.backupFov = originalFov;
			playerKeys.save();
		}
		if (seated) {
			if (currentFov > zoomFov) {
				currentFov -= 0.05;
				if (GuiIngameForge.renderCrosshairs) {
					GuiIngameForge.renderCrosshairs = false;
				}
			}
		}
		else {
			if (currentFov < originalFov) {
				currentFov += 0.05;
				if (!GuiIngameForge.renderCrosshairs) {
					GuiIngameForge.renderCrosshairs = true;
				}
			}
		}
		Minecraft.getMinecraft().gameSettings.fovSetting = currentFov;
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.PLAYER);
	}

	@Override
	public String getLabel() {
		return "Arcade Client TickHandler";
	}

}
