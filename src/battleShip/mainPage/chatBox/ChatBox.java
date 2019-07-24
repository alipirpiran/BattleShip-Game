package battleShip.mainPage.chatBox;

import battleShip.core.App;
import battleShip.mainPage.Controller;
import com.jfoenix.controls.JFXTextField;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class ChatBox extends VBox {
    final static int WIDTH = 300;
    final static int HEIGHT_MIN = 30;
    final static int HEIGHT_MAX = 300;
    final static int MESSAGE_HEIGHT = 40;

    VBox messagesVbox = new VBox();
    ScrollPane scrollPane = new ScrollPane();
    JFXTextField msgBox = new JFXTextField();

    Controller controller;
    double x, y;

    public ChatBox(Controller controller, double x, double y) {
        this.controller = controller;
        relocate(x, y);
        this.x = x;
        this.y = y;
        createContent();
    }

    private void createContent() {
        int sendImageWidth = 30;

        setAlignment(Pos.BOTTOM_CENTER);
        setPrefWidth(WIDTH);
        setPrefHeight(HEIGHT_MIN + MESSAGE_HEIGHT);
        setStyle("-fx-background-color: black; -fx-border-color: gray");

        setOnMouseEntered(e -> {
            open();
        });
        setOnMouseExited(e -> {
            close();
        });

        msgBox.setPrefWidth(WIDTH - sendImageWidth);
        msgBox.setPrefHeight(MESSAGE_HEIGHT);
        msgBox.setDisableAnimation(true);
        msgBox.setPromptText("متن پیام");
        msgBox.setAlignment(Pos.CENTER);
        msgBox.setUnFocusColor(Color.TRANSPARENT);
        msgBox.setFocusColor(Color.TRANSPARENT);
        msgBox.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER)
                sendMessage();
        });

        HBox sendBox = new HBox();
        sendBox.getChildren().add(msgBox);
        sendBox.setAlignment(Pos.CENTER_RIGHT);
        sendBox.setPrefHeight(HEIGHT_MIN);
        sendBox.setPrefWidth(WIDTH);
        sendBox.setStyle("-fx-background-color: #bdc3c7");

        ImageView imageView = new ImageView(new Image("battleShip/mainPage/UI/images/right-arrow.png"));
        imageView.setFitHeight(HEIGHT_MIN);
        imageView.setFitWidth(sendImageWidth);
        imageView.setOnMouseClicked(e -> {
            sendMessage();
        });
        sendBox.getChildren().add(new Separator(Orientation.VERTICAL));
        sendBox.getChildren().add(imageView);

        getChildren().add(sendBox);
        messagesVbox.setAlignment(Pos.BOTTOM_CENTER);
        messagesVbox.setRotate(180);
        messagesVbox.setPadding(new Insets(0,0,0,5));
        scrollPane.setContent(messagesVbox);
        scrollPane.setPrefWidth(WIDTH);
        scrollPane.setRotate(180);
//        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(HEIGHT_MAX - HEIGHT_MIN);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setPrefHeight(MESSAGE_HEIGHT);
        getChildren().add(0, scrollPane);

    }

    private void close() {
//        getChildren().remove(scrollPane);
        setPrefHeight(HEIGHT_MIN);
        relocate(x, y);
        scrollPane.setPrefHeight(MESSAGE_HEIGHT);
    }

    private void open() {
        setPrefHeight(HEIGHT_MAX);
        relocate(x, (y - HEIGHT_MAX + HEIGHT_MIN + MESSAGE_HEIGHT));
        scrollPane.setPrefHeight(HEIGHT_MAX - HEIGHT_MIN);
    }

    public void receiveMessage(String senderName, String messageContent) {
        messagesVbox.getChildren().add(createMessage(senderName, messageContent));
        messagesVbox.getChildren().add(new Separator(Orientation.HORIZONTAL));
    }

    private void sendMessage() {
        controller.sendMessageToGame(msgBox.getText());
        msgBox.setText("");
    }

    private HBox createMessage(String senderName, String messageContent) {
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setSpacing(2);
        hBox.setPrefHeight(MESSAGE_HEIGHT);
        hBox.setPrefWidth(WIDTH);
        hBox.getChildren().add(new Label(senderName));
        hBox.getChildren().add(new Separator(Orientation.VERTICAL));
        hBox.getChildren().add(new Label(messageContent));

        return hBox;
    }
}
