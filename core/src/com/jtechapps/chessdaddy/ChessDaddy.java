package com.jtechapps.chessdaddy;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class ChessDaddy extends Game {
	SpriteBatch batch;
	Texture img;
	
	@Override
	public void create () {
		this.setScreen(new TwoPlayerMatch(this));
	}

	
	@Override
	public void dispose () {
		super.dispose();
	}
}
