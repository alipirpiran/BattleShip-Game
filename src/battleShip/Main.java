package battleShip;

import battleShip.core.App;
import battleShip.login.Controller;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;

public class Main extends Application{

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        App app = new App();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login" + File.separator + "ui" + File.separator + "login.fxml"));
        Parent parent = fxmlLoader.load();
        Controller controller = fxmlLoader.getController();
        controller.app = app;
        app.login = primaryStage;


        primaryStage.setScene(new Scene(parent));
        primaryStage.setResizable(false);
        primaryStage.setTitle("ورود به بازی");
        primaryStage.show();
    }
}
