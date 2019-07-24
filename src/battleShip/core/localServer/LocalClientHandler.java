package battleShip.core.localServer;

import battleShip.core.App;
import battleShip.models.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class LocalClientHandler{
    App app;
    public Socket socket;
    public LocalServer localServer;
    ObjectOutputStream outputStream;
    ObjectInputStream inputStream;

    boolean run = true;

    protected LocalClientHandler(Socket socket, App app, LocalServer localServer) throws Exception{
        this.socket = socket;
        this.app = app;
        this.localServer = localServer;
        start();
    }

    private void start() throws Exception{
        outputStream = new ObjectOutputStream(this.socket.getOutputStream());
        inputStream = new ObjectInputStream(this.socket.getInputStream());

        // receiver thread
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run(){
                while (run){
                    try {
                        Message message = (Message) inputStream.readObject();
                        receiveFromServer(message);
                    } catch (IOException e) {
                        stop();
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    void stop(){
        this.run = false;
        try {
            this.outputStream.close();
            this.inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.localServer.localClientHandler = null;

    }

    private void receiveFromServer(Message message){

        app.receiveMessageFromServer(message);
    }

    public void sendToServer(Message message){
        try {
            outputStream.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
