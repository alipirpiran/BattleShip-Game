package battleShip.core.localServer;

import battleShip.core.App;
import battleShip.models.Message;

import java.net.Socket;

public class LocalServer {

    Socket socket;
    App app;
    public LocalClientHandler localClientHandler;

    int port;
    String ip;
    final static int DEFAULT_PORT = 1090;
    final static String DEFAULT_IP = "localhost";

    public LocalServer(String ip, int port, App app) {
        this.port = port;
        this.ip = ip;
        this.app = app;
    }

    public LocalServer(App app) {
        this(DEFAULT_IP, DEFAULT_PORT, app);
    }

    public boolean start() throws Exception {

        socket = new Socket(this.ip, this.port);
        localClientHandler = new LocalClientHandler(this.socket, this.app, this);
        return true;
    }

    public void sendMessageToServer(Message message){
            localClientHandler.sendToServer(message);

    }

}
