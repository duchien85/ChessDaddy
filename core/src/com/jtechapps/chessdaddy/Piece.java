package com.jtechapps.chessdaddy;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by JacobM on 12/23/18.
 */

enum PieceType {
    PAWN, KNIGHT, BISHOP, ROOK, QUEEN, KING;
}

public class Piece extends Sprite {
    private String pieceColor;
    private boolean isWhite;
    private PieceType pieceType;
    //board position
    private Vector2 boardPosition = new Vector2(0,0);
    private int blockSize;

    public Piece(Texture texture, boolean isWhite, PieceType pieceType, Vector2 boardPosition, int blockSize) {
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

    public Vector2 getBoardPosition() {
        return boardPosition;
    }

    public void setBoardPosition(Vector2 position) {
        this.boardPosition = position;
        //update actual position of sprite
        this.setPosition(blockSize*boardPosition.x, blockSize*boardPosition.y);
    }

}
