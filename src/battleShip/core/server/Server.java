package battleShip.core.server;

import battleShip.models.Message;
import battleShip.core.server.Database.DataBaseAPI;
import battleShip.models.Member;
import battleShip.models.Status;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    protected ArrayList<Player> players = new ArrayList<>();
    private ArrayList<Member> members = new ArrayList<>();


    private int port;
    private final static int DEFAULT_PORT = 10000;

    public Server(int port) {
        this.port = port;
    }

    public Server() {
        this(DEFAULT_PORT);
    }

    public ServerSocket start() throws Exception {
        ServerSocket serverSocket = new ServerSocket(this.port);
        Server server = this;

        Thread thread = new Thread(() -> {
            while (true) {
                Socket socket = null;
                try {
                    socket = serverSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                System.out.println("Client Accepted " + socket.getLocalSocketAddress());
                ClientHandler clientHandler = new ClientHandler(socket, server);
                players.add(clientHandler);
            }
        });
        thread.setDaemon(true);
        thread.start();

        refreshDataFromDataBase();


        return serverSocket;
    }

    public void refreshDataFromDataBase() {
        members = DataBaseAPI.getMembers();
    }

    public void login(Message message) {
        for (Member member : members) {
            if (member.getUsername().equals(message.getUsername()))
                if (member.getPassword().equals(message.getPassword())) {
                    ClientHandler player = (ClientHandler) getPlayer(message.addr);
                    player.sendMessageToClient(Message.successLoginMessage(member, message.messageId));
                    player.username = member.getUsername();
                    player.member = member;

                    member.setStatus(Status.Online);
                    sendOnlineUsersToClients();
                    return;
                }
        }
        getPlayer(message.addr).sendMessageToClient(Message.failLoginMassage(message.messageId));
    }

    public void refreshUserStatus(Message message) {
        Member member = createMemberFromMessage(message);

        members.forEach(e -> {
            if (e.getUsername().equals(member.getUsername())) {
                e.setStatus(message.getStatus());
            }
        });

        sendOnlineUsersToClients();
    }

    public Player getPlayer(String addrOrUsername) {

        for (Player player : players) {
            if (player.socket != null)
                if (player.socket.getRemoteSocketAddress().toString().equals(addrOrUsername))
                    return player;

            if (player.username.equals(addrOrUsername))
                return player;
        }

        return null;
    }


    public void register(Message message) {
        if (getUser(message.getUsername()) != null) {
            Message res = Message.duplicateUser(message.messageId);
            getPlayer(message.addr).sendMessageToClient(res);
            return;
        }

        Member member = createMemberFromMessage(message);
        DataBaseAPI.addUserToDataBase(member);

        Message res = Message.successRegister(message.messageId);
        getPlayer(message.addr).sendMessageToClient(res);

        refreshDataFromDataBase();
    }

    public Member getUser(String username) {
        for (Member member : members) {
            if (member.getUsername().equals(username))
                return member;
        }
        return null;
    }

    public void sendOnlineUsersToClients() {
        ArrayList<String> onlineUsers = new ArrayList<>();

        for (Player player : players) {
            Member member = getUser(player.username);
            String tmp = player.username + "-" + member.getFullName() + "-" + member.getStatus();
            onlineUsers.add(tmp);
        }

        players.forEach(e -> {
            Message onlinePlayers = Message.onlineUsers(onlineUsers);
            e.sendMessageToClient(onlinePlayers);
        });
    }

    public void requestToPlay(Message message, String localUsername) {
        Member targetMember = getUser(message.getTargetUsername());

        if (targetMember.getStatus() == Status.Playing)
            return;
        // TODO: 7/6/19 send reject for playing user

        Member requestedMember = getUser(localUsername);

        Message requestMessage = Message.requestPlay(requestedMember.getUsername(), requestedMember.getFullName(), -1);
        requestMessage.setUsername(localUsername);
        getPlayer(message.getTargetUsername()).sendMessageToClient(requestMessage);

    }

    public void acceptReuest(Message message, String senderUsername) {
        String targetUsername = message.getTargetUsername();

        // setting opponent for each client handler
        Player cl1 = getPlayer(targetUsername);
        Player cl2 = getPlayer(senderUsername);

        cl1.opponent = cl2;
        cl2.opponent = cl1;

        startGame(targetUsername, senderUsername);
    }

    public void ready(String username, boolean[][] board, String[] shipsData) {
        Player ch = getPlayer(username);
        ch.ready = true;
         ch.opponent.otherPlayerReady(board, shipsData);

        // if opponent is ready start the game
        if (ch.opponent.ready) {
            ch.startPlay();
            ch.opponent.startPlay();
        }

    }

    private void startGame(String user1, String user2) {
        getPlayer(user1).startGame();
        getPlayer(user2).startGame();

        sendOnlineUsersToClients();

    }

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

    public void playWithPc(Message message, ClientHandler clientHandler) {
        PcGamer pcGamer = new PcGamer(message.level, clientHandler, this);
        pcGamer.setMember(getUser(pcGamer.username));
        players.add(pcGamer);
        clientHandler.opponent = pcGamer;

        pcGamer.startGame();
        clientHandler.startGame();
    }

    void finishGame(Message message, ClientHandler clientHandler){
//        System.out.println("server : game finisehd");
        clientHandler.setGameResult(message.wined);
        clientHandler.sendMessageToClient(message);

        message.wined = !message.wined;
        clientHandler.opponent.setGameResult(message.wined);
        clientHandler.opponent.sendMessageToClient(message);

        DataBaseAPI.refreshUserData(clientHandler.member);
        DataBaseAPI.refreshUserData(clientHandler.opponent.member);

        clientHandler.finishGame();
        clientHandler.opponent.finishGame();

        sendOnlineUsersToClients();
    }


}
