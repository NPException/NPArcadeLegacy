package npe.arcade.games.crapracer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;

import npe.arcade.games.AbstractArcadeGame;

public class CrapRacer extends AbstractArcadeGame {

	private static final String TITLE = "Crap Racer";

	BufferedImage truckImage;
	BufferedImage racecarImage;

	private AbstractGameState currentGameState;
	private final Map<Class<? extends AbstractGameState>, AbstractGameState> availableGamestates;

	public CrapRacer() {
		availableGamestates = new HashMap<Class<? extends AbstractGameState>, AbstractGameState>();
	}

	Random getRandom() {
		return rand;
	}

	BufferedImage getGameGraphics() {
		return gameGraphics;
	}

	Color getBackgroundColor() {
		return arcadeMachine.getScreenBackgroundColor();
	}

	@Override
	public String getTitle() {
		return TITLE;
	}

	@Override
	public void initialize() {
		super.initialize();

		boolean ok = true;
		try {
			if (truckImage == null) {
				truckImage = ImageIO.read(CrapRacer.class.getResourceAsStream("assets/truck.png"));
			}
			if (racecarImage == null) {
				racecarImage = ImageIO.read(CrapRacer.class.getResourceAsStream("assets/racecar.png"));
			}
		}
		catch (IOException e) {
			e.printStackTrace();
			ok = false;
		}

		if (!ok) {
			setGameState(ErrorGameState.class);
		}
		else {
			setGameState(StartScreenGameState.class);
		}
	}

	@SuppressWarnings("unchecked")
	<T extends AbstractGameState> T setGameState(Class<T> clazz) {
		try {
			AbstractGameState state = availableGamestates.get(clazz);
			if (state == null) {
				state = clazz.getConstructor(CrapRacer.class).newInstance(this);
				availableGamestates.put(clazz, state);
			}
			currentGameState = state;
			currentGameState.initialize();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return (T)currentGameState;
	}

	@Override
	public void gameTick() {
		currentGameState.doTick();
	}

	@Override
	public int[] renderGraphics() {
		currentGameState.doRender((Graphics2D)gameGraphics.getGraphics());
		return super.renderGraphics();
	}
}
