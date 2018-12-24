package com.jtechapps.chessdaddy;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
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

    public Piece(Texture texture, boolean isWhite, PieceType pieceType, BoardPosition boardPosition, int blockSize) {
        super(texture);
        this.isWhite = isWhite;
        this.pieceColor = (isWhite) ? "white" : "black";
        this.pieceType = pieceType;
        this.blockSize = blockSize;
        setBoardPosition(boardPosition);
        setSize(blockSize, blockSize);
    }

    public void setPieceColor(String color) { pieceColor = color;};
    public String getPieceColor() { return pieceColor;};

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
        //update actual position of sprite
        this.setPosition(blockSize*boardPosition.column, blockSize*boardPosition.row);
    }

    /**
     * Calculate a matrix of possible move locations for this piece
     * @param board the board matrix of the game storing each position
     * @return boolean matrix of possible move locations (true means this player can move to this location)
     */
    public boolean[][] getPossibleMoves(BoardCell[][] board) {
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
        possibleMoves = falsifyArray(possibleMoves);
        possibleMoves = oppositeArray(possibleMoves);
        //

        //find moves that are possible based on piece type
        if(pieceType == PieceType.PAWN) {

            //possibleMoves[boardPosition.y+1][boardPosition.x];
            //only allow pawn to move 2 spots as first move
            //only allow straight movement if nothing is in the way
            //only allow diagonal movement to take enemy position
        } else if(pieceType == PieceType.KNIGHT) {

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
        //if the move results in a the king being in danger, make it false

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



}
