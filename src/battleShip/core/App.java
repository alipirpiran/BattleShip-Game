package battleShip.core;

import battleShip.core.server.Database.DataBaseAPI;
import battleShip.core.localServer.LocalServer;
import battleShip.core.server.Server;
import battleShip.mainPage.Game;
import battleShip.models.*;
import battleShip.signup.Controller;
import javafx.stage.Stage;

import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class App {
    public int port;
    public String ip;
    public ServerSocket serverSocket = null;
    LocalServer localServer = null;

    public Member logedInMember = null;

    public Member member1 = null;
    public Member member2 = null;


    public boolean firstRun = true;

    //    public battleShip.mainPage.Controller mainPage;
    public Game game;

    public ArrayList<Member> onlineMembers = new ArrayList<>();
    public Stage login, signup, main;
    Map<Message, Message> messageMap = new HashMap<>();

    public App() {
        DataBaseAPI.app = this;
    }

    // ---------------- SERVER --------------------
    // ---------------- SERVER --------------------

    public ServerSocket startMainServer() {
        Server server = new Server();

        try {
            this.serverSocket = server.start();
            return serverSocket;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    public boolean connectToMainServer(String ip, int port) {
        try {
            localServer = new LocalServer(ip, port, this);
            if (localServer.start())
                return true;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void startLocalServer() {
        try {
            localServer = new LocalServer(this);
            localServer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void receiveMessageFromServer(Message message) {
//        System.out.println("receive from server " + message.getMessageType());
        setResponse(message);

        switch (message.messageType) {
            case login:
                break;

            case register:

                break;

            case userInfo:
                break;

            case onlineUsers:
                setOnlineUsers(message);
                break;

            case requestPlay:
                sendRequestNotifyToClient(message);
                break;

            case startGame:
                startGame(message);
                break;

            case ready:
                otherUserReady(message);
                break;


            case startPlay:
                startPlay(message);
                break;

            case attackResult:
                recieveAttackResult(message);
                break;

            case attack:
                receiveAttack(message);
                break;

            case message:
                game.receiveMessageFromServer(message);
                break;

            case finishGame:
                finishGame(message.wined);
        }
    }

    public void sendMessageToServer(Message message) {
        this.localServer.sendMessageToServer(message);

        messageMap.put(message, null);

    }

    public boolean isConnectedToServer() {
        if (localServer != null)
            return true;
        return false;
    }

    private Message getLocalMessage(int id) {
        for (Message message : messageMap.keySet()) {
            if (message.messageId == id)
                return message;
        }
        return null;
    }

    private void setResponse(Message message) {

        // online users doesnt have response  -> id = -1
        if (message.messageId == -1)
            return;

        Message local = getLocalMessage(message.messageId);

        if (local == null)
            return;

        messageMap.replace(local, message);
        synchronized (local) {
            local.notify();
        }
    }

    private Message getResponse(Message message) {
        return messageMap.get(message);
    }

    public boolean isServer() {
        return serverSocket != null;
    }

    public void playWithPc(int level) {
        sendMessageToServer(Message.playWithPc(level));
    }

    // game requests

    public void requestToPlay(Member member) {
        // id = 0 -> generate new id for this message to set response for this message
        sendMessageToServer(Message.requestPlay(member.getUsername(), null, -1));
    }

    public void acceptToPlay(String targetUsername) {
        Member member = new Member();
        member.setUsername(targetUsername);
        Message acceptMsg = Message.acceptMessageForPlay(member);

        sendMessageToServer(acceptMsg);
    }

    public void declineToPlay(String targetUsername) {

        Member member = new Member();
        member.setUsername(targetUsername);
        Message declineMessage = Message.declineMessageForPlay(member);
        sendMessageToServer(declineMessage);
    }

    public void readyToPlay(boolean[][] board, String[] shipsData) {
//        this.board1 = board;
        sendMessageToServer(Message.readyMessage(board, shipsData));
    }

    private void receiveAttack(Message message) {
        game.receiveAttack(message.attackX, message.attackY);
    }

    public void sendGameResult(boolean wined){
        sendMessageToServer(Message.finishGame(wined));
    }

    // ----------------LOGIN --------------------------
    // ----------------LOGIN --------------------------
    public Result signUp(Member member) {

        Message message = Message.registerMessage(member, 0);
        message.addr = this.localServer.localClientHandler.socket.getLocalSocketAddress().toString();
        sendMessageToServer(message);

        try {
            synchronized (message) {
                message.wait();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Message res = getResponse(message);
        if (res.messageType == MessageType.duplicateUser) {
            return Result.DUPLICATE_USER;
        }
        if (res.messageType == MessageType.registerFail) {
            return Result.FAIL;
        }

        return Result.SUCCESS;
    }

    public Member login(String username, String password) {
        Member member = null;

        Message message = Message.loginMessage(username, password, 0);
        message.addr = this.localServer.localClientHandler.socket.getLocalSocketAddress().toString();

        sendMessageToServer(message);
        try {
            synchronized (message) {
                message.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (getResponse(message).messageType == MessageType.loginFail) {
            return null;
        }

        member = createMemberFromMessage(messageMap.get(message));
        logedInMember = member;

        return member;
    }

    public Member getUser(String username) {
        Message message = Message.getUserMessage(username, 0);
        message.addr = this.localServer.localClientHandler.socket.getLocalSocketAddress().toString();


        sendMessageToServer(message);
        try {
            message.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Message message1 = messageMap.get(message);
        return createMemberFromMessage(message1);
    }

    // --------------- GUI ---------------------
    // --------------- GUI ---------------------
    public void showMainWindow() {
//        battleShip.mainPage.Controller.showWindow(this);
        game.showWindow(this);
    }

    public void showSignupWindow() {
        Controller.showWindow(this);
    }

    public void showLoginWindow() {
        battleShip.login.Controller.showWindow(this);
    }

    public void setOnlineUsers(Message message) {
        onlineMembers.clear();

        for (String a : message.onlineUsers) {
            String[] info = a.split("-");
            String username = info[0];
            String fullName = info[1];
            String status = info[2];

            if (username.equals("Computer"))
                continue;

            Member member = new Member();
            member.setFullName(fullName);
            member.setUsername(username);
            member.setStatus(Status.valueOf(status));

            onlineMembers.add(member);
        }

        // wait for first login to online members receive from server
        if (firstRun) {
            try {
                synchronized (this) {
                    this.wait();
                }
            } catch (Exception e) {

            }
        }
        game.setOnlineUsers(onlineMembers);

    }

    private void sendRequestNotifyToClient(Message message) {
        game.receiveRequest(message.getTargetName(), message.getUsername());
    }

    // ----------------------------------------

    public Member createMemberFromMessage(Message message) {
        Member member = new Member();
        member.setUsername(message.getUsername());
        member.setPassword(message.getPassword());
        member.setWins(message.getWins());
        member.setLooses(message.getLooses());
        member.setImageData(message.getImage());
        member.setFullName(message.getName());
        member.setStatus(message.getStatus());

        return member;
    }

    private void startGame(Message message) {
        this.member1 = logedInMember;
        this.member2 = createMemberFromMessage(message);
        game.startGame(this.member1, this.member2);

    }

    private void startPlay(Message message) {
        game.startPlay();
    }

    private void otherUserReady(Message message) {
        // get other user board data
//        this.board2 = message.board;
        game.otherPlayerReady(message.board, message.shipsData);
//        Platform.runLater(() -> this.mainPage.otherPlayerReady());
    }

    private void recieveAttackResult(Message message) {

    }

    public void attackToCell(int x, int y) {
        sendMessageToServer(Message.attack(x, y));
    }

    private void finishGame(boolean wined){
        this.game.finishGameFromServer(wined);
    }
}
