package com.svmc.minigame.desktop;

import utils.screen.GameCore;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.svmc.mixxgame.attribute.Constants;
import com.svmc.mixxgame.screens.FlashScreen;

public class DesktopLauncher {
	public static void main(String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		GameCore game = new GameCore() {
			@Override
			public void create() {
				super.create();
				setScreen(new FlashScreen(this));
			}
		};


		config.width = Constants.WIDTH_SCREEN;
		config.height = Constants.HEIGHT_SCREEN;
		config.title = "Mixx Game";

		new LwjglApplication(game, config);
	}
}
