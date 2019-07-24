package battleShip.models;


import battleShip.mainPage.Controller;
import battleShip.mainPage.Game;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

public class Piece extends Rectangle {
    private double mouseX, mouseY;
    private double newX, newY;
    public boolean attacked = false;

    public Piece[] pieceGroup = new Piece[0];

    public Tile[][] board;

    public int newBoardX, newBoardY;

    public double oldX, oldY;
    public ArrayList<Piece> attachedPieces = new ArrayList<>();

    public Piece(int x, int y) {
        relocate(x * Controller.TILE_WIDTH, y * Controller.TILE_WIDTH);

        newBoardX = x;
        newBoardY = y;
        oldX = x * Controller.TILE_WIDTH;
        oldY = y * Controller.TILE_WIDTH;


        setWidth(Controller.TILE_WIDTH);
        setHeight(Controller.TILE_WIDTH);
        setFill(Color.valueOf("#bdc3c7"));

        setOnMousePressed(e -> {
            mouseX = e.getSceneX();
            mouseY = e.getSceneY();
        });

        setOnMouseDragged(e -> {
            newX = e.getSceneX();
            newY = e.getSceneY();

            double totalX = newX - mouseX + oldX;
            double totalY = newY - mouseY + oldY;

            relocateGroup(totalX, totalY);
        });
    }

    void relocateGroup(double totalx, double totaly) {
        for (Piece piece : pieceGroup) {
            piece.relocate((totalx + (piece.newBoardX - newBoardX) * Controller.TILE_WIDTH), (totaly + (piece.newBoardY - newBoardY) * Controller.TILE_WIDTH));
            board[Game.toBoard(piece.oldX)][Game.toBoard(piece.oldY)].setPiece(null);

        }
    }

    public void move() {

        // set last cell null
        for (Piece piece : pieceGroup) {
            board[Game.toBoard(piece.oldX)][Game.toBoard(piece.oldY)].setPiece(null);
        }

        for (Piece piece : pieceGroup) {
            piece.oldX = Game.toBoard(piece.getLayoutX()) * Controller.TILE_WIDTH;
            piece.oldY = Game.toBoard(piece.getLayoutY()) * Controller.TILE_WIDTH;
        }

        for (Piece piece : pieceGroup) {
            piece.relocate(Game.toBoard(piece.getLayoutX()) * Controller.TILE_WIDTH, Game.toBoard(piece.getLayoutY()) * Controller.TILE_WIDTH);

            int boardX = Game.toBoard(piece.getLayoutX());
            int boardY = Game.toBoard(piece.getLayoutY());

            piece.newBoardX = boardX;
            piece.newBoardY = boardY;

            board[boardX][boardY].setPiece(piece);
        }

    }

    public void abortMove() {
        for (Piece piece : pieceGroup) {
            piece.relocate(piece.oldX, piece.oldY);
            board[Game.toBoard(piece.oldX)][Game.toBoard(piece.oldY)].setPiece(piece);
        }
    }

    public void hide() {
        this.setFill(Color.TRANSPARENT);
        attacked = true;
    }


}
