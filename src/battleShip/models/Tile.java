package battleShip.models;

import battleShip.mainPage.Controller;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class Tile extends Pane {
    public int x,y;
    boolean attacked = false;

    Piece piece = null;
    public Tile(int x, int y){
        this.x = x;
        this.y = y;

        setStyle("-fx-border-color: darkgray; -fx-background-color: white");

        setPrefWidth(Controller.TILE_WIDTH + 1);
        setPrefHeight(Controller.TILE_WIDTH + 1);

        setLayoutX(x * Controller.TILE_WIDTH);
        setLayoutY(y * Controller.TILE_WIDTH);

        setOnMouseEntered((e) -> {
            setStyle("-fx-border-color: darkgray; -fx-background-color: #ecf0f1");

        });

        setOnMouseExited(e ->{
            setStyle("-fx-border-color: darkgray; -fx-background-color: white");

        });
    }

    public void disableCell(){
        ImageView disableImage = new ImageView();
        disableImage.setImage(new Image("battleShip/models/target.png"));
        disableImage.setFitWidth(Controller.TILE_WIDTH - 6);
        disableImage.setFitHeight(Controller.TILE_WIDTH - 6);
        disableImage.setLayoutX(3);
        disableImage.setLayoutY(3);
        getChildren().addAll(disableImage);


        this.setDisable(true);
    }

    public void attackCell(){
        if (attacked)
            return;
        attacked = true;

        ImageView imageView = new ImageView(new Image("battleShip/models/attack.png"));
        imageView.setFitHeight(Controller.TILE_WIDTH - 6);
        imageView.setFitWidth(Controller.TILE_WIDTH - 6);
        imageView.setLayoutX(3);
        imageView.setLayoutY(3);
        getChildren().addAll(imageView);
        this.setDisable(true);
    }

    public void setPiece(Piece piece){
        this.piece = piece;
    }

    public boolean hasPiece(){
        return piece!=null;
    }

    public Piece getPiece() {
        return piece;
    }
}
