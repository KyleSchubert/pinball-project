package com.pinball.mygame;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;


public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setTitle("Pinball Game");
		config.setWindowedMode(1200, 900); // 800x900 looks fine I guess... +400 for scoreboard
		config.useVsync(true);
		config.setForegroundFPS(60);
		new Lwjgl3Application(new MyGame(), config);
	}
}
