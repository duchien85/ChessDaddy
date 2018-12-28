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
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.math.Vector3;
import java.util.ArrayList;

public class TwoPlayerMatch implements Screen, InputProcessor{
	private Game game;
	private Viewport viewport;
	private Camera camera;
	private SpriteBatch batch;
	private Texture img, img2;
	private int width, height;
	private int blockSize;
	private BoardCell[][] board = new BoardCell[8][8];
	//pieces
	private Texture[][] pieceTextures = new Texture[2][6];
	//game mechanics
	private boolean whitesTurns = true;
	private boolean pieceActive = false;
	private BoardPosition activePosition = new BoardPosition(0,0);
	private boolean[][] possibleMoves =  {
			{false, false, false, false, false, false, false, false},
			{false, false, false, false, false, false, false, false},
			{false, false, false, false, false, false, false, false},
			{false, false, false, false, false, false, false, false},
			{false, false, false, false, false, false, false, false},
			{false, false, false, false, false, false, false, false},
			{false, false, false, false, false, false, false, false},
			{false, false, false, false, false, false, false, false}
	};

	public TwoPlayerMatch(Game g) {
		game = g;
	}

	@Override
	public void show() {
		width = 2000;
		height = 2000;
		camera = new OrthographicCamera();
		viewport = new FitViewport(width, height, camera);
		viewport.apply();
		camera.position.set(camera.viewportWidth/2,camera.viewportHeight/2,0);

		//width = Gdx.graphics.getWidth();
		//height = Gdx.graphics.getHeight();
		Gdx.input.setInputProcessor(this);
		blockSize = height/8;
		batch = new SpriteBatch();
		img = new Texture("white1.png");
		//board color
		Color lightColor = Color.LIGHT_GRAY;
		Color darkColor = Color.DARK_GRAY;
		//piece textures
		loadPieceTextures();

		//Add block background
		for(int r=0; r<board.length; r++) {
			for(int c=0; c<board[r].length; c++) {
				BoardCell boardTile = new BoardCell(img, ((c%2==0 && r%2==0) || (c%2==1 && r%2==1)) ? darkColor: lightColor, r, c);
				boardTile.setSize(blockSize, blockSize);
				boardTile.setPosition(blockSize*c, blockSize*r);//FIXED row should ne HEIGHT, COLUMN SHOULD BE X
				boardTile.setOriginCenter();
				boardTile.setPiecePosition(r, c);
				board[r][c] = boardTile;
			}
		}

		//Spawn pieces
		spawnPieces();

	}

	@Override
	public void render(float delta) {
		camera.update();
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		//draw board
		for(int r=0; r<board.length; r++) {
			for(int c=0; c<board[r].length; c++) {
				board[r][c].draw(batch);
				if(board[r][c].isOccupied())
					board[r][c].getOccupiedPiece().draw(batch);
			}
		}
		//draw pieces
		/*for(Piece piece : pieces) {
			piece.draw(batch);
		}*/
		batch.end();
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		viewport.update(width,height);
		camera.position.set(camera.viewportWidth/2,camera.viewportHeight/2,0);
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
		//Convert screen to camera coordinates
		Vector3 worldPoint = camera.unproject(new Vector3(screenX, screenY, 0), viewport.getScreenX(), viewport.getScreenY(), viewport.getScreenWidth(), viewport.getScreenHeight());
		screenX = (int) worldPoint.x;
		screenY = height-(int) worldPoint.y;
		//
		for(int r=0; r<board.length; r++) {
			for(int c=0; c<board[0].length; c++) {
				if(board[r][c].getBoundingRectangle().contains(screenX, height-screenY)) {
					board[r][c].getPosition();//Print out position touched

					//Algo
					//Check if touched cell has piece of current colors turn
					if(board[r][c].isOccupied() && board[r][c].getOccupiedPiece().getIsWhite()==whitesTurns) {
						activePosition.column = c;
						activePosition.row = r;
						pieceActive = true;
						//load possible move locations
						possibleMoves = board[r][c].getOccupiedPiece().getPossibleMoves(board, true);
						//show possible move locations
						for(int row=0; row<board.length; row++) {
							for(int col=0; col<board[row].length; col++) {
								if(possibleMoves[row][col]) {
									//MOVE IS POSSIBLE TINT CELL
									board[row][col].setPotential();
								} else {
									//MOVE ISN'T POSSIBLE TINT CELL
									board[row][col].setNormal();
								}
							}
						}
						board[r][c].setActive();
					}
					//else if pieceactive check if cell is for possible move then move piece, if cell isn't possible disable pieceActive
					else if(pieceActive) {
						if(possibleMoves[r][c]) {
							//User touched possible move
							//move active piece to new position
							board[activePosition.row][activePosition.column].getOccupiedPiece().setBoardPosition(new BoardPosition(r,c));
							board[r][c].setOccupiedPiece(board[activePosition.row][activePosition.column].getOccupiedPiece());
							board[activePosition.row][activePosition.column].setOccupiedPiece(null);
							board[r][c].getOccupiedPiece().addMove();
							//
							whitesTurns = !whitesTurns;//switch turns

							//See if any piece has possible move to get out of check
							boolean hasPossibleMove = hasPossibleMove();
							//see if player is check mated
							if(Piece.kingChecked(board, whitesTurns)) {

								if(hasPossibleMove) {
									System.out.println("Check on "+((whitesTurns) ? "white" : "black"));
								} else {
									System.out.println("Checkmate "+((whitesTurns) ? "white" : "black"));
									System.out.println(((!whitesTurns) ? "white" : "black")+ " wins");
								}
							} else {
								if(!hasPossibleMove) {
									//STALEMATE
									System.out.println(((whitesTurns) ? "white" : "black") + " in Stalemate");
								}
							}

							//clear moves
							clearPossibleMoves();
						} else {
							//User touched something else clear possible moves
							clearPossibleMoves();
						}
					}

					//test to see what piece is at board position
					if(board[r][c].isOccupied()) {
						System.out.println(board[r][c].getOccupiedPiece().getPieceType().toString());
						System.out.println(board[r][c].getOccupiedPiece().getPieceColor());
						System.out.println(board[r][c].getOccupiedPiece().getIsWhite());

					}
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

	/**
	 * See if current player has a possible move
	 * @return
	 */
	private boolean hasPossibleMove() {
		for(int row = 0; row < board.length; row++) {
			for(int col = 0; col < board[row].length; col++) {
				if(board[row][col].isOccupied() && board[row][col].getOccupiedPiece().getIsWhite()==whitesTurns) {
					boolean[][] moves = board[row][col].getOccupiedPiece().getPossibleMoves(board, true);
					//see if any true
					for(int i = 0; i < moves.length; i++) {
						for(int j = 0; j < moves[i].length; j++) {
							if(moves[i][j]) {
								return true;
							}
						}
					}
				}
			}
		}
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
				pieceTextures[r][c].setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

			}
		}
	}

	private void spawnPieces() {
		//white
		for(int r=0; r<2; r++) {
			for(int c=0; c<board[r].length; c++) {
				if(r==0) {
					if(c==0 || c==board[r].length-1) {
						//ROOKS
						Piece piece = new Piece(pieceTextures[0][3], true, PieceType.ROOK, new BoardPosition(r, c), blockSize);
						board[r][c].setOccupiedPiece(piece);
					} else if(c==1 || c==board[r].length-2) {
						//KNIGHTS
						Piece piece = new Piece(pieceTextures[0][1], true, PieceType.KNIGHT, new BoardPosition(r, c), blockSize);
						board[r][c].setOccupiedPiece(piece);
					} else if(c==2 || c==board[r].length-3) {
						//BISHOPS
						Piece piece = new Piece(pieceTextures[0][2], true, PieceType.BISHOP, new BoardPosition(r, c), blockSize);
						board[r][c].setOccupiedPiece(piece);
					} else if(c==3) {
						//QUEEN
						Piece piece = new Piece(pieceTextures[0][4], true, PieceType.QUEEN, new BoardPosition(r, c), blockSize);
						board[r][c].setOccupiedPiece(piece);
					} else if(c==4) {
						//KING
						Piece piece = new Piece(pieceTextures[0][5], true, PieceType.KING, new BoardPosition(r, c), blockSize);
						board[r][c].setOccupiedPiece(piece);
					}
				} else {
					//all pawns
					Piece piece = new Piece(pieceTextures[0][0], true, PieceType.PAWN, new BoardPosition(r, c), blockSize);
					board[r][c].setOccupiedPiece(piece);
				}
			}
		}

		//black
		for(int r=6; r<8; r++) {
			for(int c=0; c<board[r].length; c++) {
				if(r==7) {
					if(c==0 || c==board[r].length-1) {
						//ROOKS
						Piece piece = new Piece(pieceTextures[1][3], false, PieceType.ROOK, new BoardPosition(r, c), blockSize);
						board[r][c].setOccupiedPiece(piece);
					} else if(c==1 || c==board[r].length-2) {
						//KNIGHTS
						Piece piece = new Piece(pieceTextures[1][1], false, PieceType.KNIGHT, new BoardPosition(r, c), blockSize);
						board[r][c].setOccupiedPiece(piece);
					} else if(c==2 || c==board[r].length-3) {
						//BISHOPS
						Piece piece = new Piece(pieceTextures[1][2], false, PieceType.BISHOP, new BoardPosition(r, c), blockSize);
						board[r][c].setOccupiedPiece(piece);
					} else if(c==3) {
						//QUEEN
						Piece piece = new Piece(pieceTextures[1][4], false, PieceType.QUEEN, new BoardPosition(r, c), blockSize);
						board[r][c].setOccupiedPiece(piece);
					} else if(c==4) {
						//KING
						Piece piece = new Piece(pieceTextures[1][5], false, PieceType.KING, new BoardPosition(r, c), blockSize);
						board[r][c].setOccupiedPiece(piece);
					}
				} else {
					//all pawns
					Piece piece = new Piece(pieceTextures[1][0], false, PieceType.PAWN, new BoardPosition(r, c), blockSize);
					board[r][c].setOccupiedPiece(piece);
				}
			}
		}
	}

	/**
	 * set all posible moves to false and clear moves from screen and piece is no longer active
	 */
	private void clearPossibleMoves() {
		for(int row=0; row<board.length; row++) {
			for(int col=0; col<board[row].length; col++) {
				possibleMoves[row][col] = false;
				board[row][col].setNormal();
			}
		}
		pieceActive = false;
	}
}
