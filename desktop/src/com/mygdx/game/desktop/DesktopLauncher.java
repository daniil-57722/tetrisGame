package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.game.Constants;
import com.mygdx.game.GameClass;

public class DesktopLauncher {
	public static void main(String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Drop - libgdx tutorial";
		config.width = Constants.STAGE_WIDTH;
		config.height = Constants.STAGE_HEIGHT;
		new LwjglApplication(new GameClass(), config);
	}
}

