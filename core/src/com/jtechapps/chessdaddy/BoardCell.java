package com.jtechapps.chessdaddy;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Created by JacobM on 12/23/18.
 */

public class BoardCell extends Sprite {
    private Piece occupiedPiece = null;
    private char row, column;

    public BoardCell(Texture texture) {
        super(texture);
    }

    public boolean isOccupied() {
        return (occupiedPiece!=null);
    }

    public String getOccupiedColor() {
        return occupiedPiece.getPieceColor();
    }

    /**
    convert array to coordinate postions
     */
    public void setPiecePosition(int row, int col) {
        char c;
        switch (row) {
            case 0:
                c = 'A';
                break;
            case 1:
                c = 'B';
                break;
            case 2:
                c = 'C';
                break;
            case 3:
                c = 'D';
                break;
            case 4:
                c = 'E';
                break;
            case 5:
                c = 'F';
                break;
            case 6:
                c = 'G';
                break;
            case 7:
                c = 'H';
                break;
            default:
                c= 'A';
        }
        this.row = c;
        this.column = (char) (col+1 + '0');
    }

    public char[] getPosition() {
        char[] pos = {row, column};
        System.out.println(pos[0]+""+pos[1]);
        return pos;
    }
}
