package com.jtechapps.chessdaddy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;

/**
 * Created by JacobM on 12/23/18.
 */

//TODO make each piece type into a separate class that extends Piece

enum PieceType {
    PAWN, KNIGHT, BISHOP, ROOK, QUEEN, KING;
}

public class Piece extends Sprite {
    private String pieceColor;
    private boolean isWhite;
    private PieceType pieceType;
    //board position
    private BoardPosition boardPosition = new BoardPosition(0,0);
    private int blockSize;
    //game mechanics
    private int numberOfMoves = 0;
    private int previousTurn = 0;
    private int currentTurn = 0;
    //slide animation
    private Vector2 nextPosition = new Vector2(0 ,0);
    private Vector2 moveDirection = new Vector2(0 ,0);
    private float moveSpeed = 1.0f;// 1/t to complete animation
    private boolean isAnimating = false;

    public Piece(Texture texture, boolean isWhite, PieceType pieceType, BoardPosition boardPosition, int blockSize) {
        super(texture);
        this.isWhite = isWhite;
        this.pieceColor = (isWhite) ? "white" : "black";
        this.pieceType = pieceType;
        this.blockSize = blockSize;
        setBoardPosition(boardPosition);
        setPosition(blockSize*boardPosition.column, blockSize*boardPosition.row);
        nextPosition.set(blockSize*boardPosition.column, blockSize*boardPosition.row);
        setSize(blockSize, blockSize);
    }

    public Piece(Piece piece) {
        this.pieceColor = piece.getPieceColor();
        this.isWhite = piece.getIsWhite();
        this.pieceType = piece.getPieceType();
        this.boardPosition = new BoardPosition(piece.getBoardPosition());
        this.blockSize = piece.getBlockSize();
        this.numberOfMoves = piece.getNumberOfMoves();
    }

    public void setPieceColor(String color) { pieceColor = color;};
    public String getPieceColor() { return pieceColor;};

    public int getBlockSize() {
        return blockSize;
    }

    public int getNumberOfMoves() {
        return numberOfMoves;
    }

    public boolean getIsWhite() {
        return isWhite;
    }

    public PieceType getPieceType() {
        return pieceType;
    }

    public void setPieceType(PieceType pieceType) {
        this.pieceType = pieceType;
    }

    public void setWhite(boolean white) {
        this.isWhite = white;
    }

    public BoardPosition getBoardPosition() {
        return boardPosition;
    }

    /**
     * The board position row, column vector
     * @param position
     */
    public void setBoardPosition(BoardPosition position) {
        this.boardPosition = position;
        //set next position for move animation
        nextPosition.set(blockSize*boardPosition.column, blockSize*boardPosition.row);
        moveDirection.set(nextPosition.x-this.getX(), nextPosition.y-this.getY());
        moveDirection.scl(moveSpeed);
        isAnimating = true;
    }

    @Override
    public void draw(Batch batch) {
        super.draw(batch);
        float deltaT = Gdx.graphics.getDeltaTime();
        if(isAnimating) {
            if (Math.abs(nextPosition.x - getX()) < 5 && Math.abs(nextPosition.y - getY()) < 5) {
                //close enough to next position it should be, set it.
                setPosition(nextPosition.x, nextPosition.y);
                isAnimating = false;
            } else {
                this.setPosition(getX() + moveDirection.x * deltaT, getY() + moveDirection.y * deltaT);
            }
        }

    }

    /**
     * Calculate a matrix of possible move locations for this piece
     * @param board the board matrix of the game storing each position
     * @return boolean matrix of possible move locations (true means this player can move to this location)
     */
    public boolean[][] getPossibleMoves(BoardCell[][] board, boolean checkCheck) {
        //start by 8 by 8 array with all false possible moves
        boolean[][] possibleMoves = {
                {false, false, false, false, false, false, false, false},
                {false, false, false, false, false, false, false, false},
                {false, false, false, false, false, false, false, false},
                {false, false, false, false, false, false, false, false},
                {false, false, false, false, false, false, false, false},
                {false, false, false, false, false, false, false, false},
                {false, false, false, false, false, false, false, false},
                {false, false, false, false, false, false, false, false}
        };

        //Allowing all spots for testing with no enforced rules
        //possibleMoves = falsifyArray(possibleMoves);
        //possibleMoves = oppositeArray(possibleMoves);
        //

        //find moves that are possible based on piece type
        if(pieceType == PieceType.PAWN) {
            //only allow pawn to move 2 spots as first move
            //only allow straight movement if nothing is in the way
            //only allow diagonal movement to take enemy position
            //make sure not out of bounds

            int direction = (isWhite) ? 1: -1;
            int row, col;

            //straight ahead ONCE to NON Occupied cell
            row = boardPosition.row+direction;
            col = boardPosition.column;
            if((row < board.length && row >= 0) && (col < board[row].length && col>=0)) {
                if(!board[row][col].isOccupied())
                    possibleMoves[row][col] = true;
            }

            //straight ahead TWICE to NON Occupied cell
            if(numberOfMoves==0) {
                row = boardPosition.row + direction*2;
                col = boardPosition.column;
                //make sure pawn doesnt jump over something
                if((row-direction < board.length && row-direction >= 0) && (col < board[row].length && col>=0) && !board[row-direction][col].isOccupied()) {
                    if ((row < board.length && row >= 0) && (col < board[row].length && col >= 0)) {
                        if (!board[row][col].isOccupied())
                            possibleMoves[row][col] = true;
                    }
                }
            }

            //diagonal left
            row = boardPosition.row+direction;
            col = boardPosition.column-1;
            if((row < board.length && row >= 0) && (col < board[row].length && col>=0)) {
                if(board[row][col].isOccupied() && board[row][col].getOccupiedPiece().getIsWhite()!=getIsWhite())
                    possibleMoves[row][col] = true;
            }
            //diagonal right
            row = boardPosition.row+direction;
            col = boardPosition.column+1;
            if((row < board.length && row >= 0) && (col < board[row].length && col>=0)) {
                if(board[row][col].isOccupied() && board[row][col].getOccupiedPiece().getIsWhite()!=getIsWhite())
                    possibleMoves[row][col] = true;
            }

        } else if(pieceType == PieceType.KNIGHT) {
            //L shape spots make sure spot isn't out of bounds and that there is no same color piece
            int[][] lSpots = {
                    {boardPosition.row-1, boardPosition.column-2},
                    {boardPosition.row-1, boardPosition.column+2},
                    {boardPosition.row+1, boardPosition.column-2},
                    {boardPosition.row+1, boardPosition.column+2},
                    {boardPosition.row-2, boardPosition.column-1},
                    {boardPosition.row-2, boardPosition.column+1},
                    {boardPosition.row+2, boardPosition.column-1},
                    {boardPosition.row+2, boardPosition.column+1},
            };

            for(int p=0; p<lSpots.length; p++) {
                int row = lSpots[p][0];
                int col = lSpots[p][1];
                if((row < board.length && row >= 0) && (col < board[row].length && col>=0)) {
                    if(!board[row][col].isOccupied() || (board[row][col].isOccupied() && board[row][col].getOccupiedPiece().getIsWhite()!=getIsWhite()))
                        possibleMoves[row][col] = true;
                }
            }
        } if(pieceType == PieceType.BISHOP || pieceType == PieceType.QUEEN) {
            //diagonal moving until blocked or out of bounds
            //if blocked by friendly piece don't allow move and break search to farther spot.
            //else if blocked by enemy piece stop search and include spot

            //diagonal top left
            for(int d=1; d<8; d++) {
                int row = boardPosition.row+d;
                int col = boardPosition.column-d;
                if((row < board.length && row >= 0) && (col < board[row].length && col>=0)) {
                    if(board[row][col].isOccupied()) {
                        //path "ran into" a piece
                        if(board[row][col].getOccupiedPiece().getIsWhite()!=getIsWhite())
                            possibleMoves[row][col] = true;
                        break;
                    } else {
                        possibleMoves[row][col] = true;
                        //continue loop
                    }
                } else {
                    break;
                }
            }

            //diagonal top right
            for(int d=1; d<8; d++) {
                int row = boardPosition.row+d;
                int col = boardPosition.column+d;
                if((row < board.length && row >= 0) && (col < board[row].length && col>=0)) {
                    if(board[row][col].isOccupied()) {
                        //path "ran into" a piece
                        if(board[row][col].getOccupiedPiece().getIsWhite()!=getIsWhite())
                            possibleMoves[row][col] = true;
                        break;
                    } else {
                        possibleMoves[row][col] = true;
                        //continue loop
                    }
                } else {
                    break;
                }
            }

            //diagonal bottom left
            for(int d=1; d<8; d++) {
                int row = boardPosition.row-d;
                int col = boardPosition.column-d;
                if((row < board.length && row >= 0) && (col < board[row].length && col>=0)) {
                    if(board[row][col].isOccupied()) {
                        //path "ran into" a piece
                        if(board[row][col].getOccupiedPiece().getIsWhite()!=getIsWhite())
                            possibleMoves[row][col] = true;
                        break;
                    } else {
                        possibleMoves[row][col] = true;
                        //continue loop
                    }
                } else {
                    break;
                }
            }

            //diagonal bottom right
            for(int d=1; d<8; d++) {
                int row = boardPosition.row-d;
                int col = boardPosition.column+d;
                if((row < board.length && row >= 0) && (col < board[row].length && col>=0)) {
                    if(board[row][col].isOccupied()) {
                        //path "ran into" a piece
                        if(board[row][col].getOccupiedPiece().getIsWhite()!=getIsWhite())
                            possibleMoves[row][col] = true;
                        break;
                    } else {
                        possibleMoves[row][col] = true;
                        //continue loop
                    }
                } else {
                    break;
                }
            }
        } if(pieceType == PieceType.ROOK || pieceType == PieceType.QUEEN) {
            //orthogonal moving until blocked or out of bounds
            //if blocked by friendly piece don't allow move and break search to farther spot.
            //else if blocked by enemy piece stop search and include spot

            //LEFT
            for(int d=1; d<8; d++) {
                int row = boardPosition.row;
                int col = boardPosition.column-d;
                if((row < board.length && row >= 0) && (col < board[row].length && col>=0)) {
                    if(board[row][col].isOccupied()) {
                        //path "ran into" a piece
                        if(board[row][col].getOccupiedPiece().getIsWhite()!=getIsWhite())
                            possibleMoves[row][col] = true;
                        break;
                    } else {
                        possibleMoves[row][col] = true;
                        //continue loop
                    }
                } else {
                    break;
                }
            }

            //RIGHT
            for(int d=1; d<8; d++) {
                int row = boardPosition.row;
                int col = boardPosition.column+d;
                if((row < board.length && row >= 0) && (col < board[row].length && col>=0)) {
                    if(board[row][col].isOccupied()) {
                        //path "ran into" a piece
                        if(board[row][col].getOccupiedPiece().getIsWhite()!=getIsWhite())
                            possibleMoves[row][col] = true;
                        break;
                    } else {
                        possibleMoves[row][col] = true;
                        //continue loop
                    }
                } else {
                    break;
                }
            }

            //TOP
            for(int d=1; d<8; d++) {
                int row = boardPosition.row+d;
                int col = boardPosition.column;
                if((row < board.length && row >= 0) && (col < board[row].length && col>=0)) {
                    if(board[row][col].isOccupied()) {
                        //path "ran into" a piece
                        if(board[row][col].getOccupiedPiece().getIsWhite()!=getIsWhite())
                            possibleMoves[row][col] = true;
                        break;
                    } else {
                        possibleMoves[row][col] = true;
                        //continue loop
                    }
                } else {
                    break;
                }
            }

            //BOTTOM
            for(int d=1; d<8; d++) {
                int row = boardPosition.row-d;
                int col = boardPosition.column;
                if((row < board.length && row >= 0) && (col < board[row].length && col>=0)) {
                    if(board[row][col].isOccupied()) {
                        //path "ran into" a piece
                        if(board[row][col].getOccupiedPiece().getIsWhite()!=getIsWhite())
                            possibleMoves[row][col] = true;
                        break;
                    } else {
                        possibleMoves[row][col] = true;
                        //continue loop
                    }
                } else {
                    break;
                }
            }
        } else if(pieceType == PieceType.KING) {
            int[][] kingSpots = {
                    {boardPosition.row-1, boardPosition.column-1},
                    {boardPosition.row, boardPosition.column-1},
                    {boardPosition.row+1, boardPosition.column-1},
                    {boardPosition.row+1, boardPosition.column},
                    {boardPosition.row+1, boardPosition.column+1},
                    {boardPosition.row, boardPosition.column+1},
                    {boardPosition.row-1, boardPosition.column+1},
                    {boardPosition.row-1, boardPosition.column},
            };

            for(int p=0; p<kingSpots.length; p++) {
                int row = kingSpots[p][0];
                int col = kingSpots[p][1];
                if((row < board.length && row >= 0) && (col < board[row].length && col>=0)) {
                    if(!board[row][col].isOccupied() || (board[row][col].isOccupied() && board[row][col].getOccupiedPiece().getIsWhite()!=getIsWhite()))
                        possibleMoves[row][col] = true;
                }
            }
        }

        //if there is a same color piece in the move spot, make it false
        for(int row = 0; row < board.length; row++) {
            for(int col = 0; col < board[row].length; col++) {
                if(board[row][col].isOccupied() && board[row][col].getOccupiedPiece().isWhite == this.isWhite) {
                    //set friendly pieces spots false for possible move locations
                    possibleMoves[row][col] = false;
                }
            }
        }

        if(checkCheck) {
            //if the move results in a the king being in check, make it false
            //algorithm
            //1. Redraw board with each possible move outcome for this player.
            //  The "check" check part
            //  2. Loop through each enemy piece to see if it threatens king
            //  3. Stop if it threatens king mark move false
            for(int j = 0; j < possibleMoves.length; j++) {
                for(int k = 0; k < possibleMoves[j].length; k++) {
                    if(possibleMoves[j][k]) {
                        //copy board and see moves
                        BoardCell[][] testBoard = new BoardCell[8][8];
                        for(int x = 0; x < board.length; x++) {
                            for (int y = 0; y < board[x].length; y++) {
                                testBoard[x][y] = new BoardCell(board[x][y]);
                            }
                        }
                        //do this possible move
                        testBoard[boardPosition.row][boardPosition.column].getOccupiedPiece().setBoardPosition(new BoardPosition(j, k));
                        testBoard[j][k].setOccupiedPiece(board[boardPosition.row][boardPosition.column].getOccupiedPiece());
                        testBoard[boardPosition.row][boardPosition.column].setOccupiedPiece(null);
                        //testBoard[j][k].getOccupiedPiece().addMove();
                        //See if any enemy moves will check king
                        if(kingChecked(testBoard, isWhite))
                            possibleMoves[j][k] = false;
                    }
                }
            }
        }

        return possibleMoves;
    }

    /**
     * Set all to false
     * @param arr
     * @return arr but all false
     */
    private boolean[][] falsifyArray(boolean[][] arr) {
        for(int i=0; i<arr.length; i++) {
            for(int k=0; k<arr[i].length; k++) {
                arr[i][k] = false;
            }
        }
        return arr;
    }

    /**
     *
     * @param arr
     * @return array with all opposite elements
     */
    private boolean[][] oppositeArray(boolean[][] arr) {
        for(int i=0; i<arr.length; i++) {
            for(int k=0; k<arr[i].length; k++) {
                arr[i][k] = !arr[i][k];
            }
        }
        return arr;
    }

    public void addMove(int currentTurn) {
        numberOfMoves++;
        this.previousTurn = this.currentTurn;
        this.currentTurn = currentTurn;
    }

    public static boolean kingChecked(BoardCell[][] testBoard, boolean whiteTeam) {
        for(int x = 0; x < testBoard.length; x++) {
            for(int y = 0; y < testBoard[x].length; y++) {
                if(testBoard[x][y].isOccupied() && testBoard[x][y].getOccupiedPiece().getIsWhite()!=whiteTeam) {
                    //Enemy piece
                    boolean[][] enemyMoves = new boolean[8][8];
                    enemyMoves = testBoard[x][y].getOccupiedPiece().getPossibleMoves(testBoard, false);
                    //Check if enemy move contains the king on this test board
                    for(int p = 0; p < enemyMoves.length; p++) {
                        for(int q = 0; q < enemyMoves[p].length; q++) {
                            if(enemyMoves[p][q]) {
                                if(testBoard[p][q].isOccupied() && (testBoard[p][q].getOccupiedPiece().getPieceType()==PieceType.KING && testBoard[p][q].getOccupiedPiece().getIsWhite()==whiteTeam)) {
                                    //king in check break loop
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean isAnimating() {
        return isAnimating;
    }

}
