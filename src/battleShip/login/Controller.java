package battleShip.login;

import battleShip.Error.Error;
import battleShip.core.App;
import battleShip.models.Member;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;

public class Controller {
    TranslateTransition transition = new TranslateTransition();
    public Stage stage;
    public App app;

    @FXML
    private JFXButton signupBtn;
    @FXML
    private AnchorPane mainPane;

    @FXML
    private JFXTextField usernameInput;

    @FXML
    private JFXPasswordField passwordInput;

    @FXML
    private Button startServerBtn;

    @FXML
    private Label status;

    @FXML
    private Pane connectPane;

    @FXML
    private Button connectBtn;

    @FXML
    private TextField serverIP;

    @FXML
    private TextField serverPort;

    @FXML
    private JFXButton connectToServerBtn;
    @FXML
    private Label status1;

    @FXML
    VBox mainVbox;

    boolean opening, closing, opened, closed = false;

    public void initialize() {
        closed = true;

        connectToServerBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                serverPane();
            }
        });

        transition.setNode(mainPane);
        transition.setDuration(Duration.seconds(1));

    }

    private void serverPane() {
        if (closing)
            return;

        if (opening)
            return;

        if (closed)
            show();

        if (opened)
            close();


    }

    private void show() {
        opening = true;
        transition.setToY(-300);
        transition.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                closed = false;
                closing = false;
                opened = true;
                opening = false;
                transition.stop();
            }
        });
        transition.play();


    }

    private void close() {
        closing = true;
        transition.setToY(0);
        transition.setOnFinished((event) -> {
            closing = false;
            closed = true;
            opening = false;
            opened = false;
            transition.stop();
        });

        transition.play();


    }

    private void login() {
        String username = usernameInput.getText();
        String password = passwordInput.getText();

        if (username.equals("") || password.equals("")) {
            Error.showError("تمام ورودی ها را تکمیل گنید");
            return;
        }

        Member logedInMember = app.login(username, password);

        if (logedInMember == null) {
            Error.showError("کاربر یافت نشد");
            return;
        }

        app.login.close();
        app.showMainWindow();
    }

    @FXML
    void loginBtn(ActionEvent event) {
        login();
    }

    @FXML
    void signUpBtn(ActionEvent event) {
        if (app.login == null)
            stage.close();
        else
            app.login.close();

        app.showSignupWindow();
    }

    @FXML
    void connectToServer(ActionEvent event) {
        String ip = serverIP.getText();
        String port = serverPort.getText();

        if (ip.equals("") || port.equals("")) {
            Error.showError("همه ورودی هارا تکمیل کنید");
            return;
        }

        if (app.connectToMainServer(ip, Integer.parseInt(port))){
            connected();
        }
    }

    @FXML
    void startServer(ActionEvent event) {
        ServerSocket ss = app.startMainServer();
        if (ss != null)
            serverStarted(ss);
    }

    private void serverStarted(ServerSocket ss) {
        if (app.connectToMainServer(ss.getInetAddress().getHostAddress(), ss.getLocalPort())) {
            setServerInfo(ss);
        }
    }

    public void setServerInfo(ServerSocket ss) {
        InetAddress inetAddress = null;
        try {
            inetAddress = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        startServerBtn.setText("Server Started");
        startServerBtn.setDisable(true);

        status.setText("Server running on " + ss.getInetAddress());
        status1.setText("Server : " + inetAddress.getHostAddress() + " " + ss.getLocalPort());

        mainVbox.setDisable(false);

        connectBtn.setText("Connected");
        connectBtn.setDisable(true);

        serverIP.setText(inetAddress.getHostAddress());
        serverIP.setDisable(true);

        serverPort.setText(ss.getLocalPort() + "");
        serverPort.setDisable(true);


        connectToServerBtn.setText("Connected");
        connectToServerBtn.setStyle("-fx-background-color: #2ecc71;" +
                "-fx-background-radius: 30");

        signupBtn.setDisable(false);

        close();
    }

    private void connected(){
        startServerBtn.setDisable(true);

        serverIP.setDisable(true);
        serverPort.setDisable(true);
        connectBtn.setDisable(true);

        connectToServerBtn.setText("Connected");
        connectToServerBtn.setStyle("-fx-background-color: #2ecc71;" +
                "-fx-background-radius: 30");

        status.setText("connected to " + serverIP.getText() + " " + serverPort.getText());
        status1.setText("connected to " + serverIP.getText() + " " + serverPort.getText());

        mainVbox.setDisable(false);

        close();

        signupBtn.setDisable(false);


    }

    @FXML
    void closeServerPane(ActionEvent event) {
        serverPane();
    }


    public static void showWindow(App app) {
        Stage stage = new Stage();
        stage.setTitle("ورود");
        stage.setResizable(false);

        FXMLLoader fxmlLoader = new FXMLLoader(Controller.class.getResource("ui/login.fxml"));

        try {
            stage.setScene(new Scene(fxmlLoader.load()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Controller controller = fxmlLoader.getController();
        controller.app = app;
        app.login = stage;

        if (app.isConnectedToServer()) {
//            controller.serverStarted(app.serverSocket);
            controller.connected();
        }

        stage.show();
    }

}
