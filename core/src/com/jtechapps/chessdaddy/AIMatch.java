package com.jtechapps.chessdaddy;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class AIMatch implements Screen, InputProcessor{
	private InputProcessor gameInputProcessor;
	private Game game;
	private Viewport viewport;
	private Camera camera;
	private SpriteBatch batch;
	private Texture img;
	private int width, height;
	private int blockSize;
	private BoardCell[][] board = new BoardCell[8][8];
	//pieces
	private Texture[][] pieceTextures = new Texture[2][6];
	//game mechanics
	private boolean whitesTurns = true;
	private boolean pieceActive = false;
	private int whiteMoves = 0;
	private int blackMoves = 0;
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
	//Game stage options
	private Stage promotionStage, endGameStage;
	private boolean promotePawn = false;
	private boolean promoteWhite = false;
	private BoardPosition promotePosition = new BoardPosition(0,0);
	private BitmapFont regularFont;
	private Label winnerLabel;


	public AIMatch(Game g) {
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
		gameInputProcessor = this;
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

		//Stage options for pawn promotion
		promotionStage = new Stage(new FitViewport(1000, 1000));
		endGameStage = new Stage(new FitViewport(1000, 1000));
		//promotionStage.setDebugAll(true);

		//Text stuff
		regularFont = new BitmapFont(Gdx.files.internal("regular.fnt"));
		Label.LabelStyle regularStyle = new Label.LabelStyle();
		regularStyle.font = regularFont;
		regularStyle.fontColor = Color.RED;

		winnerLabel = new Label("", regularStyle);
		winnerLabel.setSize(endGameStage.getWidth(),50);
		winnerLabel.setPosition(0,endGameStage.getHeight()/2-25);
		winnerLabel.setAlignment(Align.center);
		endGameStage.addActor(winnerLabel);

		Image bg = new Image(new TextureRegion(img));
		bg.setSize(1000, 1000);
		bg.setColor(Color.DARK_GRAY);
		promotionStage.addActor(bg);

		Image queenBtn = new Image(new TextureRegion(pieceTextures[0][4]));
		queenBtn.setHeight(256);
		queenBtn.setWidth(256);
		queenBtn.setPosition(500-256,500);
		queenBtn.addListener(new ClickListener() {
			@Override
			public void clicked (InputEvent event, float x, float y) {
				if(promotePawn && promoteWhite) {
					board[promotePosition.row][promotePosition.column].getOccupiedPiece().setTexture(pieceTextures[0][4]);
					board[promotePosition.row][promotePosition.column].getOccupiedPiece().setPieceType(PieceType.QUEEN);
					promotePawn = false;
					checkGame();
					Gdx.input.setInputProcessor(gameInputProcessor);
				} else if(promotePawn && !promoteWhite) {
					board[promotePosition.row][promotePosition.column].getOccupiedPiece().setTexture(pieceTextures[1][4]);
					board[promotePosition.row][promotePosition.column].getOccupiedPiece().setPieceType(PieceType.QUEEN);
					promotePawn = false;
					checkGame();
					Gdx.input.setInputProcessor(gameInputProcessor);
				}
			}
		});
		promotionStage.addActor(queenBtn);

		Image knightBtn = new Image(new TextureRegion(pieceTextures[0][1]));
		knightBtn.setHeight(256);
		knightBtn.setWidth(256);
		knightBtn.setPosition(500,500);
		knightBtn.addListener(new ClickListener() {
			@Override
			public void clicked (InputEvent event, float x, float y) {
				if(promotePawn && promoteWhite) {
					board[promotePosition.row][promotePosition.column].getOccupiedPiece().setTexture(pieceTextures[0][1]);
					board[promotePosition.row][promotePosition.column].getOccupiedPiece().setPieceType(PieceType.KNIGHT);
					promotePawn = false;
					checkGame();
					Gdx.input.setInputProcessor(gameInputProcessor);
				} else if(promotePawn && !promoteWhite) {
					board[promotePosition.row][promotePosition.column].getOccupiedPiece().setTexture(pieceTextures[1][1]);
					board[promotePosition.row][promotePosition.column].getOccupiedPiece().setPieceType(PieceType.KNIGHT);
					promotePawn = false;
					checkGame();
					Gdx.input.setInputProcessor(gameInputProcessor);
				}
			}
		});
		promotionStage.addActor(knightBtn);

		Image rookBtn = new Image(new TextureRegion(pieceTextures[0][3]));
		rookBtn.setHeight(256);
		rookBtn.setWidth(256);
		rookBtn.setPosition(500-256,500-256);
		rookBtn.addListener(new ClickListener() {
			@Override
			public void clicked (InputEvent event, float x, float y) {
				if(promotePawn && promoteWhite) {
					board[promotePosition.row][promotePosition.column].getOccupiedPiece().setTexture(pieceTextures[0][3]);
					board[promotePosition.row][promotePosition.column].getOccupiedPiece().setPieceType(PieceType.ROOK);
					promotePawn = false;
					checkGame();
					Gdx.input.setInputProcessor(gameInputProcessor);
				} else if(promotePawn && !promoteWhite) {
					board[promotePosition.row][promotePosition.column].getOccupiedPiece().setTexture(pieceTextures[1][3]);
					board[promotePosition.row][promotePosition.column].getOccupiedPiece().setPieceType(PieceType.ROOK);
					promotePawn = false;
					checkGame();
					Gdx.input.setInputProcessor(gameInputProcessor);
				}
			}
		});
		promotionStage.addActor(rookBtn);

		Image bishopBtn = new Image(new TextureRegion(pieceTextures[0][2]));
		bishopBtn.setHeight(256);
		bishopBtn.setWidth(256);
		bishopBtn.setPosition(500,500-256);
		bishopBtn.addListener(new ClickListener() {
			@Override
			public void clicked (InputEvent event, float x, float y) {
				if(promotePawn && promoteWhite) {
					board[promotePosition.row][promotePosition.column].getOccupiedPiece().setTexture(pieceTextures[0][2]);
					board[promotePosition.row][promotePosition.column].getOccupiedPiece().setPieceType(PieceType.BISHOP);
					promotePawn = false;
					checkGame();
					Gdx.input.setInputProcessor(gameInputProcessor);
				} else if(promotePawn && !promoteWhite) {
					board[promotePosition.row][promotePosition.column].getOccupiedPiece().setTexture(pieceTextures[1][2]);
					board[promotePosition.row][promotePosition.column].getOccupiedPiece().setPieceType(PieceType.BISHOP);
					promotePawn = false;
					checkGame();
					Gdx.input.setInputProcessor(gameInputProcessor);
				}
			}
		});
		promotionStage.addActor(bishopBtn);

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
				if(board[r][c].isOccupied() && !board[r][c].getOccupiedPiece().isAnimating())
					board[r][c].getOccupiedPiece().draw(batch);
			}
		}
		//draw moving animations on top
		for(int r=0; r<board.length; r++) {
			for(int c=0; c<board[r].length; c++) {
				if(board[r][c].isOccupied() && board[r][c].getOccupiedPiece().isAnimating())
					board[r][c].getOccupiedPiece().draw(batch);
			}
		}
		batch.end();
		if(promotePawn) {
			promotionStage.act(delta);
			promotionStage.draw();
		}
		endGameStage.act();
		endGameStage.draw();
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		viewport.update(width,height);
		camera.position.set(camera.viewportWidth/2,camera.viewportHeight/2,0);
		promotionStage.getViewport().update(width, height, true);
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
		regularFont.dispose();
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
					//Check if touched cell is human player piece
					if(board[r][c].isOccupied() && board[r][c].getOccupiedPiece().getIsWhite() && whitesTurns) {
						activePosition.column = c;
						activePosition.row = r;
						pieceActive = true;
						//load possible move locations
						possibleMoves = board[r][c].getOccupiedPiece().getPossibleMoves(board, (whitesTurns ? whiteMoves : blackMoves), true);
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
							//En Passant
							if(board[r][c].getOccupiedPiece().getPieceType()==PieceType.PAWN && board[r][c].getOccupiedPiece().enPassant) {
								board[r+(whitesTurns ? -1 : 1)][c].setOccupiedPiece(null);
								board[r][c].getOccupiedPiece().enPassant = false;
							}
							//Castling
							//King's side
							if(board[r][c].getOccupiedPiece().canCastleKingSide && board[r][c+1].isOccupied() && board[r][c+1].getOccupiedPiece().getPieceType()==PieceType.ROOK) {
								board[r][c+1].getOccupiedPiece().setBoardPosition(new BoardPosition(r, c-1));
								board[r][c-1].setOccupiedPiece(board[r][c+1].getOccupiedPiece());
								board[r][c+1].setOccupiedPiece(null);
								board[r][c].getOccupiedPiece().canCastleKingSide = false;
							}
							//Queen's side
							if(board[r][c].getOccupiedPiece().canCastleQueenSide && board[r][c-2].isOccupied() && board[r][c-2].getOccupiedPiece().getPieceType()==PieceType.ROOK) {
								board[r][c-2].getOccupiedPiece().setBoardPosition(new BoardPosition(r, c+1));
								board[r][c+1].setOccupiedPiece(board[r][c-2].getOccupiedPiece());
								board[r][c-2].setOccupiedPiece(null);
								board[r][c].getOccupiedPiece().canCastleQueenSide = false;
							}

							if(whitesTurns) {
								whiteMoves++;
								board[r][c].getOccupiedPiece().addMove(whiteMoves);
							} else {
								blackMoves++;
								board[r][c].getOccupiedPiece().addMove(blackMoves);
							}
							//
							//See if pawn can be promoted
							if(board[r][c].getOccupiedPiece().getPieceType()==PieceType.PAWN && r==0 || (board[r][c].getOccupiedPiece().getPieceType()==PieceType.PAWN && r==7)) {
								promotePawn = true;
								promoteWhite = whitesTurns;
								promotePosition.row = r;
								promotePosition.column = c;
								Gdx.input.setInputProcessor(promotionStage);
							}
							//
							whitesTurns = !whitesTurns;//switch turns

							checkGame();

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

					//see if AI should make move
					if(!whitesTurns) {
						//AI turn to make a move
						aiTurn();
						whitesTurns = !whitesTurns;//switch turns
						checkGame();
						//clear moves
						clearPossibleMoves();
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
					boolean[][] moves = board[row][col].getOccupiedPiece().getPossibleMoves(board, (whitesTurns ? whiteMoves : blackMoves), true);
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

	private void checkGame() {
		//See if any piece has possible move to get out of check
		boolean hasPossibleMove = hasPossibleMove();
		//see if player is check mated
		if(Piece.kingChecked(board, (whitesTurns ? whiteMoves : blackMoves), whitesTurns)) {

			if(hasPossibleMove) {
				System.out.println("Check on "+((whitesTurns) ? "white" : "black"));
			} else {
				System.out.println("Checkmate "+((whitesTurns) ? "white" : "black"));
				System.out.println(((!whitesTurns) ? "white" : "black")+ " wins");
				winnerLabel.setText(((!whitesTurns) ? "White" : "Black")+ " Wins");
			}
		} else {
			if(!hasPossibleMove) {
				//STALEMATE
				System.out.println(((whitesTurns) ? "white" : "black") + " in Stalemate");
				winnerLabel.setText(((whitesTurns) ? "White" : "Black") + " in Stalemate");
			}
		}
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

	//AI

	private void aiTurn() {
		aiPlay();
	}

	private void aiPlay() {
		int depth = 2;
		int minMove = 10000;
		int turn = 1;
		boolean max = false;
		int[] move = {0,0,0,0};
		BoardCell[][] tBoard = copyBoard(board);
		for(int row = 0; row < tBoard.length; row++) {
			for(int col = 0; col < tBoard[row].length; col++) {
				if(tBoard[row][col].isOccupied() && !tBoard[row][col].getOccupiedPiece().getIsWhite()) {
					boolean[][] moves = tBoard[row][col].getOccupiedPiece().getPossibleMoves(tBoard, blackMoves, true);
					//Get each pieces moves
					for(int i = 0; i < moves.length; i++) {
						for(int j = 0; j < moves[i].length; j++) {
							if(moves[i][j]) {
								//new move
								BoardCell[][] testBoard = copyBoard(tBoard);
								testBoard[row][col].getOccupiedPiece().setBoardPosition(new BoardPosition(i, j));
								testBoard[i][j].setOccupiedPiece(testBoard[row][col].getOccupiedPiece());
								testBoard[row][col].setOccupiedPiece(null);
								if(testBoard[i][j].getOccupiedPiece().getPieceType()==PieceType.PAWN && testBoard[i][j].getOccupiedPiece().enPassant) {
									testBoard[i+(max ? -1 : 1)][j].setOccupiedPiece(null);
									testBoard[i][j].getOccupiedPiece().enPassant = false;
								}
								testBoard[i][j].getOccupiedPiece().addMove(turn);
								//

								int testMove = minimax(testBoard, false, depth-1);
								if(testMove<minMove) {
									minMove = testMove;
									move[0] = row;
									move[1] = col;
									move[2] = i;
									move[3] = j;
								}
							}
						}
					}
				}
			}
		}
		int r = move[2];
		int c = move[3];
		//actually do ai move
		board[move[0]][move[1]].getOccupiedPiece().setBoardPosition(new BoardPosition(r,c));
		board[r][c].setOccupiedPiece(board[move[0]][move[1]].getOccupiedPiece());
		board[move[0]][move[1]].setOccupiedPiece(null);
		//En Passant
		if(board[r][c].getOccupiedPiece().getPieceType()==PieceType.PAWN && board[r][c].getOccupiedPiece().enPassant) {
			board[r+(whitesTurns ? -1 : 1)][c].setOccupiedPiece(null);
			board[r][c].getOccupiedPiece().enPassant = false;
		}
		//Castling
		//King's side
		if(board[r][c].getOccupiedPiece().canCastleKingSide && board[r][c+1].isOccupied() && board[r][c+1].getOccupiedPiece().getPieceType()==PieceType.ROOK) {
			board[r][c+1].getOccupiedPiece().setBoardPosition(new BoardPosition(r, c-1));
			board[r][c-1].setOccupiedPiece(board[r][c+1].getOccupiedPiece());
			board[r][c+1].setOccupiedPiece(null);
			board[r][c].getOccupiedPiece().canCastleKingSide = false;
		}
		//Queen's side
		if(board[r][c].getOccupiedPiece().canCastleQueenSide && board[r][c-2].isOccupied() && board[r][c-2].getOccupiedPiece().getPieceType()==PieceType.ROOK) {
			board[r][c-2].getOccupiedPiece().setBoardPosition(new BoardPosition(r, c+1));
			board[r][c+1].setOccupiedPiece(board[r][c-2].getOccupiedPiece());
			board[r][c-2].setOccupiedPiece(null);
			board[r][c].getOccupiedPiece().canCastleQueenSide = false;
		}

		blackMoves++;
		board[r][c].getOccupiedPiece().addMove(blackMoves);
		//
		//See if pawn can be promoted
		/*if(board[r][c].getOccupiedPiece().getPieceType()==PieceType.PAWN && r==0 || (board[r][c].getOccupiedPiece().getPieceType()==PieceType.PAWN && r==7)) {
			promotePawn = true;
			promoteWhite = whitesTurns;
			promotePosition.row = r;
			promotePosition.column = c;
			Gdx.input.setInputProcessor(promotionStage);
		}*/
		//
	}

	private int minimax(BoardCell[][] tBoard, boolean max, int depth) {
		if(depth==0) {
			return heuristicValue(tBoard);
		}
		if(max) {
			//Maximize value for white of each move
			//get every white move
			int maxMove = -10000;
			for(int row = 0; row < tBoard.length; row++) {
				for(int col = 0; col < tBoard[row].length; col++) {
					if(tBoard[row][col].isOccupied() && tBoard[row][col].getOccupiedPiece().getIsWhite()) {
						boolean[][] moves = tBoard[row][col].getOccupiedPiece().getPossibleMoves(tBoard, whiteMoves, true);
						//Get each pieces moves
						for(int i = 0; i < moves.length; i++) {
							for(int j = 0; j < moves[i].length; j++) {
								if(moves[i][j]) {
									//new move
									BoardCell[][] testBoard = copyBoard(tBoard);
									testBoard[row][col].getOccupiedPiece().setBoardPosition(new BoardPosition(i, j));
									testBoard[i][j].setOccupiedPiece(testBoard[row][col].getOccupiedPiece());
									testBoard[row][col].setOccupiedPiece(null);
									if(testBoard[i][j].getOccupiedPiece().getPieceType()==PieceType.PAWN && testBoard[i][j].getOccupiedPiece().enPassant) {
										testBoard[i+(max ? -1 : 1)][j].setOccupiedPiece(null);
										testBoard[i][j].getOccupiedPiece().enPassant = false;
									}
									testBoard[i][j].getOccupiedPiece().addMove(2-depth);
									//
									int testMove = minimax(testBoard, !max, depth-1);
									if(testMove>maxMove)
										maxMove = testMove;
								}
							}
						}
					}
				}
			}
			return maxMove;
		} else {
			//minimize value for black
			//get every black move
			int minMove = 10000;
			for(int row = 0; row < tBoard.length; row++) {
				for(int col = 0; col < tBoard[row].length; col++) {
					if(tBoard[row][col].isOccupied() && !tBoard[row][col].getOccupiedPiece().getIsWhite()) {
						boolean[][] moves = tBoard[row][col].getOccupiedPiece().getPossibleMoves(tBoard, whiteMoves, true);
						//Get each pieces moves
						for(int i = 0; i < moves.length; i++) {
							for(int j = 0; j < moves[i].length; j++) {
								if(moves[i][j]) {
									//new move
									BoardCell[][] testBoard = copyBoard(tBoard);
									testBoard[row][col].getOccupiedPiece().setBoardPosition(new BoardPosition(i, j));
									testBoard[i][j].setOccupiedPiece(testBoard[row][col].getOccupiedPiece());
									testBoard[row][col].setOccupiedPiece(null);
									if(testBoard[i][j].getOccupiedPiece().getPieceType()==PieceType.PAWN && testBoard[i][j].getOccupiedPiece().enPassant) {
										testBoard[i+(max ? -1 : 1)][j].setOccupiedPiece(null);
										testBoard[i][j].getOccupiedPiece().enPassant = false;
									}
									testBoard[i][j].getOccupiedPiece().addMove(2-depth);
									//
									int testMove = minimax(testBoard, !max, depth-1);
									if(testMove<minMove)
										minMove = testMove;
								}
							}
						}
					}
				}
			}
			return minMove;
		}
	}

	private BoardCell[][] copyBoard(BoardCell[][] b) {
		BoardCell[][] testBoard = new BoardCell[8][8];
		for(int x = 0; x < b.length; x++) {
			for (int y = 0; y < b[x].length; y++) {
				testBoard[x][y] = new BoardCell(b[x][y]);
			}
		}
		return testBoard;
	}

	/**
	 * Used by the minimax algorithm
	 * this version doesn't take into account the position of the piece
	 * Negative means black is favorable
	 * Positive means white is favorable
	 * @param aBoard the board to evaulate
	 * @return the value of this boards position
	 */
	private int heuristicValue(BoardCell[][] aBoard) {
		int pawn = 10, knight = 30, bishop = 30, rook = 50, queen = 90, king = 900;
		int value = 0;
		for(int row = 0; row < aBoard.length; row++) {
			for(int col = 0; col < aBoard[row].length; col++) {
				if(aBoard[row][col].isOccupied()) {
					PieceType pieceType = aBoard[row][col].getOccupiedPiece().getPieceType();
					int black = (aBoard[row][col].getOccupiedPiece().getIsWhite()) ? 1 : -1;
					int pieceValue = 0;
					if(pieceType==PieceType.PAWN)
						pieceValue = pawn;
					else if(pieceType==PieceType.KNIGHT)
						pieceValue = knight;
					else if(pieceType==PieceType.BISHOP)
						pieceValue = bishop;
					else if(pieceType==PieceType.ROOK)
						pieceValue = rook;
					else if(pieceType==PieceType.QUEEN)
						pieceValue = queen;
					else if(pieceType==PieceType.KING)
						pieceValue = king;
					value+=(black*pieceValue);
				}
			}
		}
		return value;
	}
}
