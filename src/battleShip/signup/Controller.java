package battleShip.signup;

import battleShip.Error.Error;
import battleShip.core.App;
import battleShip.models.Member;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;

public class Controller {
    public App app;

    @FXML
    private ImageView imageview;

    @FXML
    private JFXTextField fullNameInput;

    @FXML
    private JFXPasswordField passwordInput;

    @FXML
    private JFXTextField usernameInput;

    File imageFile = null;


    public void initialize(){

    }

    public static void showWindow(App app){
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = null;
        try {
            fxmlLoader = new FXMLLoader(Controller.class.getResource("UI" + File.separator + "signup.fxml"));
            stage.setScene(new Scene(fxmlLoader.load()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Controller controller = fxmlLoader.getController();
        controller.app = app;
        app.signup = stage;

        stage.setTitle("ثبت نام");
        stage.show();
    }


    @FXML
    void loginBtn(ActionEvent event) {
        app.signup.close();
        app.showLoginWindow();

    }

    private void signup(){
        String fullName = fullNameInput.getText();
        String username = usernameInput.getText();
        String password = passwordInput.getText();
        byte[] imageData = null;
        try {
            imageData = Files.readAllBytes(imageFile.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Member member = new Member();
        member.setFullName(fullName);
        member.setUsername(username);
        member.setPassword(password);
        member.setImageData(imageData);

        switch (app.signUp(member)) {
            case SUCCESS:
                Error.showMessage("ثبت نام با موفقیت انجام شد");
                break;

            case DUPLICATE_USER:
                Error.showError("نام کاربری تکراری است");
                break;

            case FAIL:
                Error.showError("خطا پیش آمد");
                break;
        }


    }

    @FXML
    void signUpBtn(ActionEvent event) {
        signup();
    }

    @FXML
    void choseImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image", "*.png", "*.svg")
        );

        imageFile = fileChooser.showOpenDialog(app.signup);
        try {
            if (imageFile == null)
                return;
            imageview.setImage(new Image(imageFile.toURL().toString()));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }


}
