package battleShip.core.server;

import battleShip.models.Message;
import javafx.scene.layout.BorderStroke;
import sun.security.provider.SHA;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class PcGamer extends Player {
    public static void main(String[] a) {
        PcGamer pcGamer = new PcGamer(0, null, null);
        pcGamer.createBoard();
    }


    private final static int SHIP = 1;
    private final static int ATTACKED_SHIP = 2;
    private final static int EMPTY = 3;
    private final static int ATTACKED_EMPTY = 4;

    public Ship group4_1 = new Ship(4, true);
    public Ship group3_1 = new Ship(3, false);
    public Ship group3_2 = new Ship(3, false);
    public Ship group2_1 = new Ship(2, true);
    public Ship group2_2 = new Ship(2, true);
    public Ship group2_3 = new Ship(2, false);
    public Ship group1_1 = new Ship(1, false);
    public Ship group1_2 = new Ship(1, false);
    public Ship group1_3 = new Ship(1, false);
    public Ship group1_4 = new Ship(1, false);

    private Ship[] ships = {group1_1, group1_2, group1_3, group1_4, group2_1, group2_2, group2_3, group3_1, group3_2, group4_1};

    int level;
    boolean turn = true;

    int[][] board = new int[10][10];
    boolean[][] boolBoard = new boolean[10][10];
    Boolean[][] opponentBoard = new Boolean[10][10];


//    public Player opponent;


    public PcGamer(int level, Player opponent, Server server) {
//        System.out.println("pc created !");
        this.level = level;
        this.opponent = opponent;
        this.server = server;
        username = "Computer";


    }

    private void createBoard() {
        // make the board empty
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                board[i][j] = EMPTY;
            }
        }

        // put ships in random positions in board
        Random random = new Random();
        for (Ship ship : ships) {
//            System.out.println(ship.len);
            int x;
            int y;
            do {
                x = random.nextInt(10);
                y = random.nextInt(10);
            } while (!isShipPossibleInPosition(ship, x, y));

            ship.x = x;
            ship.y = y;

            if (ship.vertical) {
                for (int i = y; i < y + ship.len; i++) {
                    board[x][i] = SHIP;
                }
            } else {
                for (int i = x; i < x + ship.len; i++) {
                    board[i][y] = SHIP;
                }
            }
        }

        for (int i = 0; i < board.length; i++) {
            String tmp = "";
            for (int j = 0; j < board[i].length; j++) {
                if (board[j][i] == SHIP)
                    tmp += " + ";
                else tmp += " - ";
            }
            System.out.println(tmp);
        }

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                boolBoard[i][j] = board[i][j] == SHIP;
            }
        }
    }

    private boolean isShipPossibleInPosition(Ship ship, int x, int y) {
        if (ship.vertical)
            if (y + ship.len - 1 > 9)
                return false;
        if (!ship.vertical)
            if (x + ship.len - 1 > 9)
                return false;

        int longLen;
        int temp;

        if (ship.vertical) {
            temp = x;
            longLen = y;
        } else {
            temp = y;
            longLen = x;
        }
        for (int i = longLen - 1; i < longLen + ship.len + 1; i++)
            for (int j = temp - 1; j < temp + 2; j++) {
                if (i < 0 || i > 9)
                    continue;
                if (j < 0 || j > 9)
                    continue;
                if (ship.vertical) {
                    if (SHIP == board[j][i])
                        return false;
                } else {
                    if (board[i][j] == SHIP)
                        return false;
                }

            }

        return true;

    }

    @Override
    public void attack(Message message) {
//        super.attack(message);
    }

    @Override
    public void receiveAttack(int x, int y) {
//        System.out.println("attack received on " + x + " " + y);
        if (x < 0 && y < 0)
            turn = !turn;
        else {
            if (board[x][y] == SHIP) {
                board[x][y] = ATTACKED_SHIP;

            } else if (board[x][y] == EMPTY) {
                board[x][y] = ATTACKED_EMPTY;
                turn = !turn;
            }
        }

        if (turn)
            tryToAttack();
    }

    private void tryToAttack() {
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Random random = new Random();
        int attackX;
        int attackY;
        do {
            attackX = random.nextInt(10);
            attackY = random.nextInt(10);
        } while (!attackPossible(attackX, attackY));

        turn = opponentBoard[attackX][attackY];

        System.out.println("pc attacking to  : " + attackX + " " + attackY);

        opponentBoard[attackX][attackY] = null;
        opponent.receiveAttack(attackX, attackY);

        if (turn) {
            tryToAttack();
        }

    }

    private boolean attackPossible(int x, int y) {
        return opponentBoard[x][y] != null;
    }

    private String[] getShipsData() {
        ArrayList<String> data = new ArrayList<>();
        for (Ship ship : ships) {
            String temp = ship.len + "#";
            for (int i = 0; i < ship.len; i++) {
                if (ship.vertical) {
                    temp += ship.x + "-";
                    temp += ship.y + i;
                } else {
                    temp += (ship.x + i) + "-";
                    temp += ship.y;
                }
                temp += "#";
            }
            data.add(temp);
        }
        return data.toArray(new String[0]);
    }


    @Override
    public void startGame() {
        createBoard();
        server.ready(username, boolBoard, getShipsData());
//        this.opponent.sendMessageToClient(Message.readyMessage(boolBoard));

    }

    @Override
    public void startPlay() {
        tryToAttack();
    }

    @Override
    public void otherPlayerReady(boolean[][] board, String[] shipsData) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                opponentBoard[i][j] = board[i][j];
            }
        }

    }

    class Ship {
        int x, y, len, status;
        int[] cells;
        boolean vertical;

        Ship(int len, boolean vertical) {
            this.len = len;
            this.vertical = vertical;
            this.cells = new int[len];
        }

        Ship(int x, int y, int len) {
            this.x = x;
            this.y = y;
            this.len = len;
            this.cells = new int[len];
        }
    }
}
