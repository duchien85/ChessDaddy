package com.jtechapps.chessdaddy;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class TwoPlayerMatch implements Screen, InputProcessor{
	private Game game;
	SpriteBatch batch;
	Texture img, img2;
	private int width, height;
	private int blockSize;
	private BoardCell[][] board = new BoardCell[8][8];

	public TwoPlayerMatch(Game g) {
		game = g;
	}

	@Override
	public void show() {
		width = Gdx.graphics.getWidth();
		height = Gdx.graphics.getHeight();
		Gdx.input.setInputProcessor(this);
		blockSize = height/8;
		batch = new SpriteBatch();
		img = new Texture("purple.png");
		img2 = new Texture("cyan.png");

		for(int r=0; r<board.length; r++) {
			for(int c=0; c<board[0].length; c++) {
				BoardCell boardTile = new BoardCell(((c%2==0 && r%2==0) || (c%2==1 && r%2==1)) ? img: img2);
				boardTile.setSize(blockSize, blockSize);
				boardTile.setPosition(blockSize*r, blockSize*c);
				boardTile.setOriginCenter();
				boardTile.setPiecePosition(r, c);
				board[r][c] = boardTile;
			}
		}

	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		//draw board
		for(int r=0; r<board.length; r++) {
			for(int c=0; c<board[0].length; c++) {
				board[r][c].draw(batch);

			}
		}//
		batch.end();
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		batch.dispose();
		img.dispose();
		img2.dispose();
	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		for(int r=0; r<board.length; r++) {
			for(int c=0; c<board[0].length; c++) {
				if(board[r][c].getBoundingRectangle().contains(screenX, height-screenY)) {
					board[r][c].getPosition();
					board[r][c].rotate(45);
				}

			}
		}
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
}
