package battleShip.core.server;

import battleShip.models.Member;
import battleShip.models.Message;
import battleShip.models.Status;

import java.net.Socket;

public class Player {
    Server server;
    Member member;
    Socket socket = null;

    public String username;
    Player opponent;
    public boolean ready = false;

    public Member getMember(){
        return server.getUser(this.username);
    }
    public void setMember(Member member) {
        this.member = member;
    }

    public void setGameResult(boolean wined){
        if (wined){
            this.member.setWins(this.member.getWins() + 1);
        } else {
            this.member.setLooses(this.member.getLooses() + 1);
        }
    }

    // game attack
    public void attack(Message message) {}

    public void receiveAttack(int x, int y){}

    public void sendMessageToClient(Message message){}

    public void otherPlayerReady(boolean[][] board, String[] shipsData){}

    public void startPlay(){}

    public void startGame() {}

    public void finishGame(){}

}
