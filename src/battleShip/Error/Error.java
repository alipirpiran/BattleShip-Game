package battleShip.Error;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;


public class Error {
    @FXML
    TextArea text;
    @FXML
    Label type;

    static TextArea message;
    static Label _type;

    public void initialize(){
        message = text;
        _type = type;

    }

    static public void showError(String message){
        String fxmlDirectori = "src" + File.separator + "sample" + File.separator + "UI" + File.separator + "Error" + File.separator  + "error.fxml";
        Parent parent = null;
        try {
            parent = FXMLLoader.load(Error.class.getResource("error.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setScene(new Scene(parent));
        stage.setTitle(Error._type.getText());

        stage.show();

        Error.message.setText(message);
        Error._type.setText("خطا");



    }

    static public void showMessage(String message){
        Parent parent = null;
        try {
            parent = FXMLLoader.load(Error.class.getResource("error.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setScene(new Scene(parent));
        stage.setTitle(Error._type.getText());

        stage.show();

        Error.message.setText(message);
        Error._type.setText("پیام");


    }

    private static void showWindow(){
        Parent parent = null;
        try {
            parent = FXMLLoader.load(Error.class.getResource("error.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setScene(new Scene(parent));
        stage.setTitle(Error._type.getText());

        stage.show();
    }
}
