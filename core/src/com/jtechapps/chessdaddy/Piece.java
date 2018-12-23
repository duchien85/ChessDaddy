package com.jtechapps.chessdaddy;

import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Created by JacobM on 12/23/18.
 */

public abstract class Piece extends Sprite {
    private String pieceColor;

    public void setPieceColor(String color) { pieceColor = color;};
    public String getPieceColor() { return pieceColor;};
}
