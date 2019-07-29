package battleShip.mainPage;

import battleShip.core.App;
import battleShip.models.Member;
import battleShip.models.Message;
import battleShip.models.Piece;
import battleShip.models.Tile;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static battleShip.mainPage.Controller.TILE_WIDTH;


public class Game {
    public static final int HAVE_PIECE = 1;
    public static final int NONE = 2;
    public static final int NORMAL = 3;
    public static final int OUT_OF_BOUND = 4;
    public static final int DISTANCE_PROBLEM = 5;

    private final static int SHIP = 1;
    private final static int ATTACKED_SHIP = 2;
    private final static int EMPTY = 3;
    private final static int ATTACKED_EMPTY = 4;

    boolean myTurn = false;
    private boolean firtTurn;

    public Controller controller;

    private boolean member1Ready = false;
    private boolean member2Ready = false;
    private boolean[][] boolBoard1 = null;
    private boolean[][] boolBoard2 = null;


    Tile[][] board1 = new Tile[10][10];
    Tile[][] board2 = new Tile[10][10];

    Piece[] group4_1 = new Piece[4];
    Piece[] group3_1 = new Piece[3];
    Piece[] group3_2 = new Piece[3];
    Piece[] group2_1 = new Piece[2];
    Piece[] group2_2 = new Piece[2];
    Piece[] group2_3 = new Piece[2];
    Piece[] group1_1 = new Piece[1];
    Piece[] group1_2 = new Piece[1];
    Piece[] group1_3 = new Piece[1];
    Piece[] group1_4 = new Piece[1];
    //    public Piece[][] ships;
    ArrayList<Piece[]> ships = new ArrayList<>();
    public ArrayList<Piece[]> shipsAlive = new ArrayList<>();
    private ArrayList<Piece[]> shipsOpponent = new ArrayList<>();
    public ArrayList<Piece[]> shipsOpponentAlive = new ArrayList<>();

    Map<ArrayList<Piece[]>, Member> shipMember = new HashMap<>();
    ArrayList<ArrayList> totalShips = new ArrayList<>();





    private int[][] board2Status = new int[10][10];
    App app;
    private boolean playWithPc;

    public Game(){
        totalShips.add(ships);
        totalShips.add(shipsOpponent);
    }

    public void setShipArrayList() {
        ships.add(group1_1);
        ships.add(group1_2);
        ships.add(group1_3);
        ships.add(group1_4);
        ships.add(group2_1);
        ships.add(group2_2);
        ships.add(group2_3);
        ships.add(group3_1);
        ships.add(group3_2);
        ships.add(group4_1);
    }

    Piece[] createPiece(int boardX, int boardY, int len, boolean vertical) {
        Piece[] pieces = new Piece[len];


        int x = boardX;
        int y = boardY;

        for (int i = 0; i < len; i++) {
            Piece piece = new Piece(x, y);
            piece.board = board1;
            piece.pieceGroup = pieces;

            board1[x][y].setPiece(piece);

            piece.setOnMouseReleased(e -> {
                move(piece);
            });

            if (vertical)
                y++;
            else x++;

            pieces[i] = piece;
        }
        return pieces;
    }

    private int tryToMove(Piece piece) {
        for (Piece piece1 : piece.pieceGroup) {
            if (piece1.getLayoutY() < 0)
                return OUT_OF_BOUND;

            if (piece1.getLayoutX() < 0)
                return OUT_OF_BOUND;

            int newBoardX = toBoard(piece1.getLayoutX());
            int newBoardY = toBoard(piece1.getLayoutY());

            if (newBoardX < 0 || newBoardX >= 10)
                return OUT_OF_BOUND;

            if (newBoardY < 0 || newBoardY >= 10)
                return OUT_OF_BOUND;

            if (board1[newBoardX][newBoardY].hasPiece())
                return HAVE_PIECE;

            for (int i = newBoardX - 1; i <= newBoardX + 1; i++)
                for (int j = newBoardY - 1; j <= newBoardY + 1; j++) {
                    if (i < 0 || j < 0 || i > 9 || j > 9)
                        continue;

                    if (board1[i][j].hasPiece())
                        return DISTANCE_PROBLEM;
                }
        }
        return NORMAL;


    }

    private void move(Piece piece) {
        int res = tryToMove(piece);

        switch (res) {
            case HAVE_PIECE:
            case NONE:
            case OUT_OF_BOUND:
            case DISTANCE_PROBLEM:
                piece.abortMove();
                break;

            case NORMAL:
                piece.move();
                break;

        }
    }


    public static int toBoard(double pixel) {
        return ((int) pixel + TILE_WIDTH / 2) / TILE_WIDTH;
    }

    public void receiveAttack(int attackX, int attackY) {
        Platform.runLater(() -> {
            if (!(attackX < 0) || !(attackY < 0)) {
                if (this.board1[attackX][attackY].hasPiece()) {
                    this.board1[attackX][attackY].getPiece().hide();
                    this.board1[attackX][attackY].attackCell();
//                    controller.playAttackSound();
                    for (int x = -1; x <= 1; x++)
                        for (int y = -1; y <= 1; y++) {
                            if ((board1[attackX][attackY].x + x) < 0 || (board1[attackX][attackY].x + x) > 9)
                                continue;
                            if ((board1[attackX][attackY].y + y) < 0 || (board1[attackX][attackY].y + y) > 9)
                                continue;
                            if (x == 0 || y == 0)
                                continue;

                            board1[board1[attackX][attackY].x + x][board1[attackX][attackY].y + y].disableCell();
                        }
                    controller.setShipsContent(false, ships.toArray(new Piece[0][0]));
                    controller.startTimeCheck();

                    if (isGameFinished())
                        finishGameProcess();
                } else {
                    this.board1[attackX][attackY].disableCell();
                    changeTurn(-1);
                }
            }
        });
    }

    private void initOpponentBoard(boolean[][] board) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j])
                    board2Status[i][j] = SHIP;
                else board2Status[i][j] = EMPTY;

            }
        }
    }

    public void attackToCell(Tile tile) {

        if (!boolBoard2[tile.x][tile.y]) {
            tile.disableCell();
            board2Status[tile.x][tile.y] = ATTACKED_EMPTY;
            Platform.runLater(() -> changeTurn(-1));
        } else {
            tile.attackCell();
//            controller.playAttackSound();
            board2Status[tile.x][tile.y] = ATTACKED_SHIP;
            tile.getPiece().hide();
            controller.setShipsContent(true, shipsOpponent.toArray(new Piece[0][0]));

            for (int x = -1; x <= 1; x++)
                for (int y = -1; y <= 1; y++) {
                    if ((tile.x + x) < 0 || (tile.x + x) > 9)
                        continue;
                    if ((tile.y + y) < 0 || (tile.y + y) > 9)
                        continue;
                    if (x == 0 || y == 0)
                        continue;

                    board2[tile.x + x][tile.y + y].disableCell();
                }
            controller.startTimeCheck();
            if (isGameFinished())
                finishGameProcess();
        }

        app.attackToCell(tile.x, tile.y);
    }

    public void finishTurn() {
        app.attackToCell(-1, -1);
    }

    public void ready() {
        boolean[][] temp_board = new boolean[10][10];
        for (int i = 0; i < board1.length; i++)
            for (int j = 0; j < board1[i].length; j++)
                temp_board[i][j] = board1[i][j].hasPiece();


        ArrayList<String> shipsData = new ArrayList<>();
        for (Piece[] ship : ships) {
            String temp = ship.length + "#";
            for (Piece piece : ship) {
                temp += piece.newBoardX;
                temp += "-";
                temp += piece.newBoardY;
                temp += "#";
            }
            shipsData.add(temp);
        }


        app.readyToPlay(temp_board, shipsData.toArray(new String[0]));
        boolBoard1 = temp_board;
        member1Ready = true;
        if (!member2Ready)
            myTurn = true;

        shipsAlive = new ArrayList<>(ships);
        shipMember.put(ships, app.member1);

    }

    private boolean isGameFinished() {

        if (shipsAlive.size() == 0){
            app.member2.winGame = true;
            return true;
        }

        if (shipsOpponentAlive.size() == 0){
            app.member1.winGame = true;
            return true;
        }

        return false;
    }


    private void finishGameProcess() {
        if (this.firtTurn || playWithPc)
            app.sendGameResult(app.member1.winGame);

//        controller.showFinishWindow();
    }

    public void finishGameFromServer(boolean wined){
        Platform.runLater(() -> controller.showFinishWindow(wined));

        playWithPc = false;
        controller.finishTimeCheck();
    }

    public void otherPlayerReady(boolean[][] board, String[] shipsData) {
        // this method is called from server
        boolBoard2 = board;
        member2Ready = true;

        // parse shipdata string into ships !
        for (String data : shipsData) {
            String[] info = data.split("#");

            int len = Integer.parseInt(info[0]);
            Piece[] ship = new Piece[len];

            for (int i = 0; i < len; i++) {

                int x = Integer.parseInt(info[i + 1].split("-")[0]);
                int y = Integer.parseInt(info[i + 1].split("-")[1]);

                Piece piece = new Piece(x, y);

                ship[i] = piece;
                board2[piece.newBoardX][piece.newBoardY].setPiece(piece);
            }
            this.shipsOpponent.add(ship);
        }

        Platform.runLater(() -> controller.ready(true));
        initOpponentBoard(board);
        shipsOpponentAlive = new ArrayList<>(shipsOpponent);
        shipMember.put(shipsOpponent, app.member2);
    }

    public void playWithPc(int level) {
        playWithPc = true;
        app.playWithPc(level);
    }


    public static void showWindow(App app) {
        Stage stage = new Stage();
        stage.setTitle("Battle Ship");
        stage.setResizable(false);
        FXMLLoader fxmlLoader = new FXMLLoader(Controller.class.getResource("UI" + File.separator + "mainPage.fxml"));
        try {
            stage.setScene(new Scene(fxmlLoader.load()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Controller controller = fxmlLoader.getController();

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                if (controller.timer != null)
                    controller.timer.cancel();
            }
        });

        app.main = stage;
        app.game = controller.game;
        controller.game.app = app;

        Controller.setData(app, controller);

        stage.show();

        // after show main page notify to set online players
        synchronized (app) {
            app.notify();
        }
        app.firstRun = false;
    }

    public void receiveRequest(String fullName, String username) {
        Platform.runLater(() -> controller.gameRequest(fullName, username));

    }

    public void acceptGameRequest(String username) {
        app.acceptToPlay(username);
    }

    public void declineGameRequest(String username) {
        app.declineToPlay(username);
    }

    public void startGame(Member member1, Member member2) {
        Platform.runLater(() -> controller.startGame(member1, member2));
    }

    public void startPlay() {
        try {
            if (!controller.isMovedReady1)
                synchronized (controller.ready1) {
                    controller.ready1.wait();
                }

            if (!controller.isMovedReady2)
                synchronized (controller.ready2) {
                    controller.ready2.wait();
                }
        } catch (Exception e) {

        }

        Platform.runLater(() -> this.controller.startPlay());
        Platform.runLater(() -> controller.setShipsContent(false, ships.toArray(new Piece[0][0])));
        Platform.runLater(() -> controller.setShipsContent(true, shipsOpponent.toArray(new Piece[0][0])));
    }

    public void changeTurn(int turn) {
        if (turn == 0) {
            Platform.runLater(() -> controller.changeTurn(0));
            firtTurn = true;

        } else if (turn == 1) {
            Platform.runLater(() -> controller.changeTurn(1));
            firtTurn = false;

        } else {
            myTurn = !myTurn;
            Platform.runLater(() -> controller.changeTurn(-1));
        }
    }

    // message
    public void receiveMessageFromServer(Message message) {
        Platform.runLater(() -> controller.receiveMessageFromGame(message));
    }

    public void sendMessageToServer(String messageContent) {
        app.sendMessageToServer(Message.messageText(messageContent));
    }

    public void setOnlineUsers(ArrayList<Member> onlineMembers) {
        Platform.runLater(() -> controller.setOnlineUsers(onlineMembers));

    }
}
