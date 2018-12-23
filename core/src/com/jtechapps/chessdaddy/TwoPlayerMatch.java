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
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class TwoPlayerMatch implements Screen, InputProcessor{
	private Game game;
	private SpriteBatch batch;
	private Texture img, img2;
	private int width, height;
	private int blockSize;
	private BoardCell[][] board = new BoardCell[8][8];
	//pieces
	private Texture[][] pieceTextures = new Texture[2][6];
	private ArrayList<Piece> pieces;

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
		img = new Texture("purple.png");//dark
		img2 = new Texture("cyan.png");//light
		//piece textures
		loadPieceTextures();

		//Add block background
		for(int r=0; r<board.length; r++) {
			for(int c=0; c<board[r].length; c++) {
				BoardCell boardTile = new BoardCell(((c%2==0 && r%2==0) || (c%2==1 && r%2==1)) ? img: img2);
				boardTile.setSize(blockSize, blockSize);
				boardTile.setPosition(blockSize*r, blockSize*c);
				boardTile.setOriginCenter();
				boardTile.setPiecePosition(c, r);
				board[r][c] = boardTile;
			}
		}

		//Spawn pieces
		pieces = new ArrayList<Piece>();
		//white
		for(int r=0; r<2; r++) {
			for(int c=0; c<board[r].length; c++) {
				if(r==0) {

				} else {
					//all pawns
					Piece piece = new Piece(pieceTextures[0][0], true, PieceType.PAWN, new Vector2(c, r), blockSize);
					pieces.add(piece);
				}
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
		}
		//draw pieces
		for(Piece piece : pieces) {
			piece.draw(batch);
		}
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
		for(int r=0; r<pieceTextures.length; r++) {
			for(int c=0; c<pieceTextures.length; c++) {
				pieceTextures[r][c].dispose();
			}
		}
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

	private void loadPieceTextures() {
		for(int r=0; r<pieceTextures.length; r++) {
			for(int c=0; c<pieceTextures[r].length; c++) {
				if(r==0) {
					//white
					if(c==0) {
						//PAWN
						pieceTextures[r][c] = new Texture("whitepawn.png");
					} else if(c==1) {
						//KNIGHT
						pieceTextures[r][c] = new Texture("whiteknight.png");
					} else if(c==2) {
						//BISHOP
						pieceTextures[r][c] = new Texture("whitebishop.png");
					} else if(c==3) {
						//ROOK
						pieceTextures[r][c] = new Texture("whiterook.png");
					} else if(c==4) {
						//QUEEN
						pieceTextures[r][c] = new Texture("whitequeen.png");
					} else if(c==5) {
						//KING
						pieceTextures[r][c] = new Texture("whiteking.png");
					}
				} else {
					//black
					if(c==0) {
						//PAWN
						pieceTextures[r][c] = new Texture("blackpawn.png");
					} else if(c==1) {
						//KNIGHT
						pieceTextures[r][c] = new Texture("blackknight.png");
					} else if(c==2) {
						//BISHOP
						pieceTextures[r][c] = new Texture("blackbishop.png");
					} else if(c==3) {
						//ROOK
						pieceTextures[r][c] = new Texture("blackrook.png");
					} else if(c==4) {
						//QUEEN
						pieceTextures[r][c] = new Texture("blackqueen.png");
					} else if(c==5) {
						//KING
						pieceTextures[r][c] = new Texture("blackking.png");
					}
				}
			}
		}
	}
}
