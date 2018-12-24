package com.jtechapps.chessdaddy;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Created by JacobM on 12/23/18.
 */

public class BoardCell extends Sprite {
    private Piece occupiedPiece = null;
    private char row, column;
    private BoardPosition boardPosition;

    public BoardCell(Texture texture, int row, int column) {
        super(texture);
        boardPosition = new BoardPosition(row, column);
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
        //screen position
        char c;
        switch (col) {
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
        this.column = c;
        this.row = (char) (row+1 + '0');
    }

    /**
     *  Currenly prints columnROW
     * @return {row, column} keep in mind row=y column = x
     */
    public char[] getPosition() {
        char[] pos = {row, column};
        System.out.println(pos[1]+""+pos[0]);
        return pos;
    }

    public Piece getOccupiedPiece() {
        return occupiedPiece;
    }

    public void setOccupiedPiece(Piece piece) {
        occupiedPiece = piece;
    }

    public BoardPosition getBoardPosition() {
        return boardPosition;
    }

    private boolean disableGuide = false;
    /**
     * Set this cell's active effects
     */
    public void setActive() {
        if(!disableGuide) {
            this.setColor(Color.BROWN);
        }
    }

    /**
     * Set the cells effect for being possible move for a piece
     */
    public void setPotential() {
        if(!disableGuide) {
            this.setColor(Color.YELLOW);
        }
    }

    /**
     * Set this cell back to it's normal color
     */
    public void setNormal() {
        if(!disableGuide) {
            this.setColor(Color.WHITE);
        }
    }
}
