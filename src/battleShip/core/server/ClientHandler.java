package battleShip.core.server;

import battleShip.models.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler extends Player implements Runnable {
    ObjectInputStream ois;
    ObjectOutputStream oos;

    boolean run = true;

    public ClientHandler(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
        try {
            this.ois = new ObjectInputStream(socket.getInputStream());
            this.oos = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Thread thread = new Thread(this);
        thread.setDaemon(true);
        thread.start();

    }

    @Override
    public void run() {
        while (run) {
            try {
                Message message = (Message) this.ois.readObject();
                readMessage(message);
            } catch (Exception e) {
                e.printStackTrace();
                stop();
            }
        }
    }

    private void stop() {
        this.server.players.remove(this);
        this.run = false;
        this.server.sendOnlineUsersToClients();

    }

    private void readMessage(Message message) {
        Member member = new Member();
        member.setUsername(message.getUsername());
        member.setPassword(message.getPassword());
        member.setFullName(message.getName());
        member.setImageData(message.getImage());
        member.setLooses(message.getLooses());
        member.setWins(message.getWins());

        switch (message.messageType) {
            case userInfo:
                break;

            case register:
                server.register(message);
                break;

            case login:
                server.login(message);
                break;


            case changeStatus:
                server.refreshUserStatus(message);
                break;

            case requestPlay:
                server.requestToPlay(message, username);
                break;

            case acceptRequestPlay:
                server.acceptReuest(message, username);
                break;

            case ready:
                server.ready(this.username, message.board, message.shipsData);
                break;

            case attack:
                this.attack(message);
                break;

            case message:
                this.messageText(message);
                break;

            case playWithPc:
                server.playWithPc(message, this);
                break;

        }
    }

    @Override
    public void startPlay() {
        System.out.println("Client Handler start platy");
        sendMessageToClient(Message.playMessage());
    }


    private void messageText(Message message) {
        if (this.opponent == null)
            return;
        message.setName(getMember().getFullName());
        this.opponent.sendMessageToClient(message);
    }

    @Override
    public void attack(Message message) {
//        super.attack(message);
        this.opponent.receiveAttack(message.attackX,message.attackY);
    }

    @Override
    public void receiveAttack(int x, int y) {
        this.sendMessageToClient(Message.attack(x, y));
    }

    @Override
    public void sendMessageToClient(Message message) {
        System.out.println("server : message Send ::" + message.getMessageType());
        try {
            this.oos.writeObject(message);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startGame() {
        this.member.setStatus(Status.Playing);

        Message startGameForUser = Message.startGame(opponent.getMember());
        this.sendMessageToClient(startGameForUser);

    }



    @Override
    public void otherPlayerReady(boolean[][] board, String[] shipsData) {
        sendMessageToClient(Message.readyMessage(board, shipsData));
    }
}
