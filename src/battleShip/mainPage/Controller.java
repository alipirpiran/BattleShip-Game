package battleShip.mainPage;

import battleShip.core.App;
import battleShip.mainPage.chatBox.ChatBox;
import battleShip.models.*;
import battleShip.profile.Profile;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.transitions.hamburger.HamburgerSlideCloseTransition;
import javafx.animation.Transition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Time;
import java.util.*;

public class Controller {
    public static int WIDTH = 400;
    public static int TILE_WIDTH = 40;

    private static final int EASY = 1;
    private static final int NORMAL = 2;
    private static final int HARD = 3;

    @FXML
    public AnchorPane mainPane;

    @FXML
    private ImageView image1;

    @FXML
    private Label playerName1;

    @FXML
    private Label playerName2;

    @FXML
    private ImageView image2;

    @FXML
    private HBox player1Hbox;
    @FXML
    private HBox player2Hbox;
    @FXML
    private JFXHamburger hambur;
    @FXML
    private VBox menuPane;
    @FXML
    private VBox onlineUsers;
    @FXML
    private AnchorPane contentPane1;

    @FXML
    private AnchorPane contentPane2;

    @FXML
    private Label serverStatus;

    @FXML
    VBox notification;
    @FXML
    public JFXButton ready1;
    @FXML
    public JFXButton ready2;
    @FXML
    private Label logedInUserName;
    @FXML
    private ImageView logedInUserImage;
    @FXML
    private VBox logedInUserPane;
    @FXML
    VBox onlineUsers1;
    @FXML
    AnchorPane mainMenuPane;
    @FXML
    private Ellipse turnShape2;
    @FXML
    private Ellipse turnShape1;
    @FXML
    private Label turnLabel;
    @FXML
    private Rectangle timeLine;
    @FXML
    private Label timeLabel;
    @FXML
    private JFXButton playWithComputer;
    @FXML
    private JFXButton playWithOnlinePlayers;
    @FXML
    private TilePane shipsContent1, shipsContent2;

    // ----------------- RESULT PAGE ------------------
    @FXML
    private ImageView winnerImage;
    @FXML
    private Label winnerName;
    @FXML
    private ImageView looserImage;
    @FXML
    private Label looserName;
    @FXML
    Pane resultPane;
    @FXML
    JFXButton returnToMenu;

    @FXML Label totalTimeLabel;


    boolean isMovedReady1 = false;
    boolean isMovedReady2 = false;

    private boolean isComputerPaneOpened = false;
    private VBox playWithPCVbox;

    Game game = new Game();


    TranslateTransition playWithOnlineUsersTransition;
    TranslateTransition onlineUsersTransition;
    TranslateTransition resultPaneTransition;
    TranslateTransition menuPaneTransition;



    Timer timer = new Timer();
    Timer totalTime;
    private boolean transitionInit = false;
    public Menu menu;
    private ChatBox chatBox;

    Map<Piece[], Rectangle> shipsShapes = null;
    Map<Piece[], Rectangle> shipsShapesOpponent = null;


    public void initialize() {
        game.controller = this;
        createContent();
        setUsersDetails();
        mainPane.getChildren().add(chatBox = new ChatBox(this, 0, 580));
        chatBox.toBack();

        playWithComputer.setOnMouseClicked(e -> playWithComputerBtn());
        menu = new Menu(hambur, menuPane);

    }

    void startTimeCheck() {
        timeLine.setLayoutX(15);
        timeLabel.setText("15");
        if (timer != null) {
            timer.cancel();
            timer = new Timer();
        } else timer = new Timer();

        timer.schedule(new TimerTask() {
            int sec = 0;

            @Override
            public void run() {
                Platform.runLater(() -> {
                    timeLabel.setText(15 - sec + "");
                    timeLine.setLayoutX(timeLine.getLayoutX() + timeLine.getWidth() / 15);
                });

                if (sec == 15) {
                    timer.cancel();
                    if (game.myTurn) {
                        game.finishTurn();
                    }
                    game.changeTurn(-1);
                }
                sec++;
            }
        }, 1000, 1000);
    }

    void stopTimeCheck(){
        timer.cancel();
    }


    private void setUsersDetails() {
        player1Hbox.setOnMouseEntered((e) -> player1Hbox.setStyle("-fx-background-color: #bdc3c7"));
        player1Hbox.setOnMouseExited((e) -> player1Hbox.setStyle(""));
        player1Hbox.setOnMouseClicked((e) -> {
            if (game.app.member1 == null)
                return;
            Profile.show(game.app.member1);
        });

        player2Hbox.setOnMouseEntered((e) -> player2Hbox.setStyle("-fx-background-color: #bdc3c7"));
        player2Hbox.setOnMouseExited((e) -> player2Hbox.setStyle(""));
        player2Hbox.setOnMouseClicked((e) -> {
            if (game.app.member2 == null)
                return;
            Profile.show(game.app.member2);
        });

    }

    public void showFinishWindow(boolean wined) {
        resultPaneTransition = new TranslateTransition();
        resultPaneTransition.setNode(resultPane);
        resultPaneTransition.setToY(607 + 43);
        resultPaneTransition.setDuration(Duration.seconds(1));
        Member winner;
        Member loser;
        if (wined) {
            winner = game.app.member1;
            loser = game.app.member2;
        } else {
            winner = game.app.member2;
            loser = game.app.member1;
        }

        winnerName.setText(winner.getFullName());

        try {
            winnerImage.setImage(new Image(new ByteArrayInputStream(winner.getImageData())));
            looserImage.setImage(new Image(new ByteArrayInputStream(loser.getImageData())));
        } catch (Exception e) {
            looserName.setText(loser.getFullName());
            e.printStackTrace();
        }

        returnToMenu.setOnMouseClicked(event -> returnToMenu());
        resultPaneTransition.play();

        chatBox.toBack();


    }

    private void returnToMenu() {
        resultPaneTransition.setToY(0);
        resultPaneTransition.play();

        showMainMenuPain();


    }

    public void setOnlineUsers(ArrayList<Member> members) {
        onlineUsers.getChildren().clear();
        onlineUsers1.getChildren().clear();

        for (Member member : members) {
            onlineUsers1.getChildren().add(new OnlineUser(member, game.app));
            onlineUsers.getChildren().add(new OnlineUser(member, game.app));
        }
    }

    void createContent() {
        for (int i = 0; i < 10; i++)
            for (int j = 0; j < 10; j++) {
                Tile tile = new Tile(i, j);
                game.board1[i][j] = tile;
                contentPane1.getChildren().add(tile);

                // opponent board
                Tile tile1 = new Tile(i, j);
                tile1.setOnMouseClicked(e -> game.attackToCell(tile1));
                game.board2[i][j] = tile1;
                contentPane2.getChildren().add(tile1);
            }


        game.group4_1 = game.createPiece(6, 3, 4, true);
        game.group3_1 = game.createPiece(0, 1, 3, false);
        game.group3_2 = game.createPiece(1, 8, 3, false);
        game.group2_1 = game.createPiece(9, 2, 2, true);
        game.group2_2 = game.createPiece(9, 5, 2, true);
        game.group2_3 = game.createPiece(7, 8, 2, false);
        game.group1_1 = game.createPiece(7, 0, 1, false);
        game.group1_2 = game.createPiece(4, 3, 1, false);
        game.group1_3 = game.createPiece(1, 4, 1, false);
        game.group1_4 = game.createPiece(3, 6, 1, false);

        contentPane1.getChildren().addAll(game.group4_1);
        contentPane1.getChildren().addAll(game.group3_1);
        contentPane1.getChildren().addAll(game.group3_2);
        contentPane1.getChildren().addAll(game.group2_1);
        contentPane1.getChildren().addAll(game.group2_2);
        contentPane1.getChildren().addAll(game.group2_3);
        contentPane1.getChildren().addAll(game.group1_1);
        contentPane1.getChildren().addAll(game.group1_2);
        contentPane1.getChildren().addAll(game.group1_3);
        contentPane1.getChildren().addAll(game.group1_4);

        game.setShipArrayList();

    }

    public void changeTurn(int turn) {
        if (turn == 0) {
            turnShape1.setVisible(true);
            turnShape2.setVisible(false);
            contentPane2.setDisable(false);
            turnLabel.setText("نوبت شما");
        } else if (turn == 1) {
            turnShape1.setVisible(false);
            turnShape2.setVisible(true);
            contentPane2.setDisable(true);
            turnLabel.setText("نوبت حریف");
        } else {
            if (game.myTurn) {
                turnShape1.setVisible(true);
                turnShape2.setVisible(false);
                contentPane2.setDisable(false);
                turnLabel.setText("نوبت شما");
            }
            if (!game.myTurn) {
                turnShape1.setVisible(false);
                turnShape2.setVisible(true);
                contentPane2.setDisable(true);
                turnLabel.setText("نوبت حریف");
            }
        }

        startTimeCheck();
    }

    public void setShipsContent(boolean opponent, Piece[]... ships) {
        int width = 6;
        int height = 6;
        Map map;
        ArrayList<Piece[]> aliveShips;

        // first init the shapes
        if (shipsShapes == null || shipsShapesOpponent == null) {
            TilePane tilePane;

            if (opponent) {
                shipsShapesOpponent = new HashMap<>();
                map = shipsShapesOpponent;
                tilePane = shipsContent2;
            } else {
                shipsShapes = new HashMap<>();
                map = shipsShapes;
                tilePane = shipsContent1;
            }

            for (Piece[] ship : ships) {
                Rectangle rectangle = new Rectangle(width, height * ship.length);
                rectangle.setFill(Color.CORNFLOWERBLUE);
                map.put(ship, rectangle);
                tilePane.getChildren().add(rectangle);
            }

            return;
        }

        if (opponent) {
            map = shipsShapesOpponent;
            aliveShips = game.shipsOpponentAlive;
        } else {
            map = shipsShapes;
            aliveShips = game.shipsAlive;
        }


        main:
        for (Piece[] ship : aliveShips) {
            boolean attacked = true;

            for (Piece piece : ship)
                if (!piece.attacked) {
                    attacked = false;
                    break;
                }

            if (attacked) {
                aliveShips.remove(ship);
                shipAttacked(opponent, ship);
                ((Rectangle) map.get(ship)).setFill(Color.BLACK);
                break main;
            }
        }

    }

    @FXML
    void finish(ActionEvent event) {
        finish(false);
    }
    void finish(boolean wined){
        showFinishWindow(wined);
        createContent();
        shipsShapes = null;
        shipsShapesOpponent = null;
        shipsContent1.getChildren().clear();
        shipsContent2.getChildren().clear();

        TranslateTransition transition = new TranslateTransition();
        transition.setNode(ready1);
        transition.setToX(0);
        transition.setToY(0);
        transition.setDuration(Duration.millis(10));
        transition.play();

        transition = new TranslateTransition();
        transition.setNode(ready2);
        transition.setToY(0);
        transition.setToX(0);
        transition.setDuration(Duration.millis(10));
        transition.play();

        contentPane1.setDisable(false);
        contentPane2.setDisable(true);

    }

    private void shipAttacked(boolean opponent, Piece[] ship) {
        Tile[][] board;
        if (opponent)
            board = game.board2;
        else board = game.board1;

        for (Piece piece : ship) {
            squareAttack(piece.newBoardX, piece.newBoardY, board);
        }
    }

    private void squareAttack(int x, int y, Tile[][] board) {
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if ((x + i) < 0 || (x + i) > 9)
                    continue;
                if ((y + j) < 0 || (y + j) > 9)
                    continue;

                if (board[x + i][y + j].hasPiece()) {
                    board[x + i][y + j].attackCell();
                } else {
                    board[x + i][y + j].disableCell();
                }
            }
        }
    }

    static void setData(App app, Controller controller) {
        Member loginMember = app.logedInMember;

        if (loginMember.getImageData() != null) {
            Image image = new Image(new ByteArrayInputStream(loginMember.getImageData()));
            controller.logedInUserImage.setImage(image);
        }
        controller.logedInUserName.setText(loginMember.getFullName());
        controller.logedInUserPane.setOnMouseEntered(e -> {
            controller.logedInUserPane.setStyle("-fx-background-color: #95a5a6");
        });
        controller.logedInUserPane.setOnMouseExited(e -> controller.logedInUserPane.setStyle(""));
        controller.logedInUserPane.setOnMouseClicked(e -> Profile.show(loginMember));

        // server status
        if (app.isServer()) {
            try {
                controller.serverStatus.setText("Server running on : " + InetAddress.getLocalHost().getHostAddress() + ":" + app.serverSocket.getLocalPort());
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
    }

    private void setUserOne(Member member) {
        if (member.getImageData() != null) {
            Image image = new Image(new ByteArrayInputStream(member.getImageData()));
            image1.setImage(image);
        }

        this.playerName1.setText(member.getFullName());
    }

    //  -------------------------------------------------------------------------
    //  ---------------------------------- game request --------------------------------
    //  -------------------------------------------------------------------------

    void gameRequest(String fullName, String username) {
        int NOTIFICATION_WIDTH = 288;

        VBox content = new VBox();
        content.setPrefHeight(400);
        content.setPrefWidth(NOTIFICATION_WIDTH);
        content.setSpacing(5);

        Label title = new Label("درخواست بازی");
        title.setFont(new Font(16));
        title.setPrefWidth(NOTIFICATION_WIDTH);
        title.setAlignment(Pos.CENTER);

        Label name = new Label(fullName);
        name.setAlignment(Pos.CENTER);
        name.setPrefWidth(NOTIFICATION_WIDTH);
        name.setStyle("-fx-text-fill: #95a5a6");
        Label contentText = new Label(" به شما درخواست بازی داده است");
        contentText.setPrefWidth(NOTIFICATION_WIDTH);
        contentText.setAlignment(Pos.CENTER);
        contentText.setStyle("-fx-text-fill: #95a5a6");

        JFXButton acceptBtn = new JFXButton("قبول");
        acceptBtn.setOnAction(e -> acceptGameRequest(username));
        acceptBtn.setStyle("-fx-background-color: #2ecc71");
        acceptBtn.setPrefWidth(NOTIFICATION_WIDTH / 2);

        JFXButton declineBtn = new JFXButton("رد");
        declineBtn.setOnAction(e -> declineGameRequest(username));
        declineBtn.setPrefWidth(NOTIFICATION_WIDTH / 2);
        declineBtn.setStyle("-fx-background-color: #e74c3c");

        HBox btnContent = new HBox();
        btnContent.setAlignment(Pos.CENTER);
        btnContent.setPrefWidth(NOTIFICATION_WIDTH);
        btnContent.setPrefHeight(40);
        btnContent.getChildren().addAll(declineBtn, acceptBtn);

        content.getChildren().addAll(title, new Separator(Orientation.HORIZONTAL), name, contentText, btnContent);

        showNotification(content);
    }

    private void setUserTwo(Member member) {
        if (member.getImageData() != null) {
            Image image = new Image(new ByteArrayInputStream(member.getImageData()));
            image2.setImage(image);
        }

        this.playerName2.setText(member.getFullName());
    }

    private void acceptGameRequest(String username) {
        game.acceptGameRequest(username);
        closeNotificationBar();
    }

    private void declineGameRequest(String username) {
        game.declineGameRequest(username);
        closeNotificationBar();
    }

    //  -------------------------------------------------------------------------
    // ------------------------------ notifiacation -----------------------------
    //  -------------------------------------------------------------------------
    private void showNotification(VBox content) {
        notification.getChildren().addAll(content);

        TranslateTransition transition = new TranslateTransition();
        transition.setNode(notification);
        transition.setDuration(Duration.seconds(1));
        transition.setToY(-102);
        transition.setOnFinished(e -> {
            transition.stop();
        });
        transition.play();

    }

    private void closeNotificationBar() {
        TranslateTransition transition = new TranslateTransition();
        transition.setNode(notification);
        transition.setDuration(Duration.seconds(1));
        transition.setToY(0);
        transition.setOnFinished(e -> {
            transition.stop();
        });
        transition.play();
    }

    @FXML
    void closeNotification(ActionEvent event) {
        closeNotificationBar();
    }

    @FXML
    void test(ActionEvent event) {
        gameRequest("ali", null);
    }

    //  -------------------------------------------------------------------------
    // ------------------------------ computer -----------------------------
    //  -------------------------------------------------------------------------
    private void playWithComputerBtn() {
        if (!transitionInit) {
            playWithOnlineUsersTransition = new TranslateTransition();
            playWithOnlineUsersTransition.setNode(playWithOnlinePlayers);
            playWithOnlineUsersTransition.setToY(100);
            playWithOnlineUsersTransition.setOnFinished(e -> {
                playWithOnlineUsersTransition.stop();
                if (!isComputerPaneOpened)
                    createComputerPlayContent();
                isComputerPaneOpened = !isComputerPaneOpened;
            });

            onlineUsersTransition = new TranslateTransition();
            onlineUsersTransition.setNode(onlineUsers);
            onlineUsersTransition.setToY(100);
            onlineUsersTransition.setOnFinished(e -> {
                onlineUsersTransition.stop();

            });
        }
        if (!isComputerPaneOpened) {
            playWithOnlineUsersTransition.setToY(100);
            onlineUsersTransition.setToY(100);

            playWithOnlineUsersTransition.play();
            onlineUsersTransition.play();
        } else {
            removeComputerPlayContent();
            playWithOnlineUsersTransition.setToY(0);
            onlineUsersTransition.setToY(0);

            playWithOnlineUsersTransition.play();
            onlineUsersTransition.play();
        }
    }

    private void createComputerPlayContent() {
        int btnWidth = 284;
        int btnHeight = 30;
        playWithPCVbox = new VBox();
        playWithPCVbox.setSpacing(3);
        playWithPCVbox.setFillWidth(true);
        playWithPCVbox.setPrefHeight(100);
        playWithPCVbox.setStyle("");
        playWithPCVbox.setPrefWidth(284);
        playWithPCVbox.relocate(258, 282);
        mainMenuPane.getChildren().add(playWithPCVbox);

        JFXButton easyBtn = btn("آسان", btnWidth, btnHeight);
        easyBtn.setOnMouseClicked(e -> playWithPC(EASY));

        JFXButton normalBtn = btn("متوسط", btnWidth, btnHeight);
        normalBtn.setOnMouseClicked(e -> playWithPC(NORMAL));

        JFXButton hardBtn = btn("سخت", btnWidth, btnHeight);
        hardBtn.setOnMouseClicked(e -> playWithPC(HARD));

        playWithPCVbox.getChildren().addAll(easyBtn, normalBtn, hardBtn);
    }

    private JFXButton btn(String text, double width, double height) {
        JFXButton jfxButton = new JFXButton(text);
        jfxButton.setPrefWidth(width);
        jfxButton.setPrefHeight(height);
        jfxButton.setStyle("-fx-background-color: #ecf0f1;-fx-border-color: #bdc3c7");
        return jfxButton;
    }

    private void removeComputerPlayContent() {
        mainMenuPane.getChildren().remove(playWithPCVbox);
    }

    private void playWithPC(int level) {
        switch (level) {
            case EASY:
                break;

            case NORMAL:
                break;

            case HARD:
                break;
        }
//        otherPlayerReady();
        ready(true);
        game.playWithPc(level);

    }

    //  -------------------------------------------------------------------------
    // ------------------------------ game start -----------------------------
    //  -------------------------------------------------------------------------


    void startGame(Member one, Member two) {

        setUserOne(one);
        setUserTwo(two);

        ready1.setDisable(false);
        ready1.setOnAction(e -> ready(false));

        closeMainMenuPane();
        chatBox.toFront();
        startTotalTime();
    }

    public void ready(boolean opponent) {
        double toX;
        double fromX = 0;
        Node node;
        if (opponent) {
            node = ready2;
            toX = -(WIDTH - ready2.getWidth());
        } else {
            node = ready1;
            toX = WIDTH - ready1.getWidth();
        }
        TranslateTransition transition = new TranslateTransition();
        transition.setNode(node);
        transition.setDuration(Duration.seconds(1));
        transition.setToX(toX);
        transition.setFromX(fromX);
        transition.setOnFinished(e -> {
            transition.stop();
            if (opponent)
                isMovedReady2 = true;
            else
                isMovedReady1 = true;
            synchronized (node) {
                node.notify();
            }
        });
        transition.play();
        node.setStyle("-fx-background-color: #2ecc71");

        if (!opponent) {
            contentPane1.setDisable(true);
            game.ready();
        }
    }

    void startPlay() {
        TranslateTransition transition = new TranslateTransition();
        transition.setNode(ready2);
        transition.setDuration(Duration.seconds(1));
        transition.setToY(-(ready2.getHeight()));
        transition.setOnFinished(e -> transition.stop());
        transition.play();

        TranslateTransition transition2 = new TranslateTransition();
        transition2.setNode(ready1);
        transition2.setToY(-(ready1.getHeight()));
        transition2.setDuration(Duration.seconds(1));
        transition2.setOnFinished(e -> transition2.stop());
        transition2.play();

        if (game.myTurn)
            game.changeTurn(0);
        else game.changeTurn(1);
    }

    private void closeMainMenuPane() {
        menuPaneTransition = new TranslateTransition();
        menuPaneTransition.setNode(mainMenuPane);
        menuPaneTransition.setDuration(Duration.seconds(1));
        menuPaneTransition.setToY(-(mainMenuPane.getHeight() + mainMenuPane.getLayoutY()));
        menuPaneTransition.setOnFinished((e) -> {
            menuPaneTransition.stop();
        });
        menuPaneTransition.play();

        hambur.toFront();
    }

    private void showMainMenuPain(){
        menuPaneTransition.setNode(mainMenuPane);
        menuPaneTransition.setDuration(Duration.seconds(1));
        menuPaneTransition.setToY(0);
        menuPaneTransition.setOnFinished((e) -> {
            menuPaneTransition.stop();
        });
        menuPaneTransition.play();
    }

    // messages
    void receiveMessageFromGame(Message message) {
        chatBox.receiveMessage(message.getName(), message.messageContent);
    }

    public void sendMessageToGame(String messageContent) {
        game.sendMessageToServer(messageContent);
        chatBox.receiveMessage(game.app.member1.getFullName(), messageContent);
    }

    private void startTotalTime(){
        final int[] sec = {0};

        totalTime = new Timer();
        totalTime.schedule(new TimerTask() {
            @Override
            public void run() {
                int hours = 0;
                int minute = 0;
                int seccond = 0;

                int temp = sec[0]++;
                hours = temp / 3600;
                minute = (temp - hours * 3600) / 60;
                seccond = (temp - minute * 60);

                String s = "ثانیه";
                String h = "ساعت";
                String m = "دقیقه";

                String time = String.format("%s %d , %s %d , %s %d", h, hours, m, minute, s, seccond);
                Platform.runLater(() -> totalTimeLabel.setText(time));

            }
        }, 0, 1000);
    }

    public void stopTotalTime(){
        totalTime.cancel();
    }

    @FXML
    void randomPlay(ActionEvent event){
        randomPlay();
    }

    void randomPlay(){

        game.randomPlay();
    }


}

