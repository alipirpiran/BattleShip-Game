package battleShip.profile;

import battleShip.models.Member;
import com.jfoenix.controls.JFXSpinner;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

public class Profile {
    static Member member;
    static Stage stage;

    @FXML
    private ImageView image;

    @FXML
    private Label nameLabel;

    @FXML
    private JFXSpinner winRate;

    @FXML
    private Label loseLabel;

    @FXML
    private Label winLabel;

    public void initialize() {
        double winRateDouble = (double) member.getWins() / (member.getWins() + member.getLooses());
        if (member.getImageData() != null)
            image.setImage(new Image(new ByteArrayInputStream(member.getImageData())));
        nameLabel.setText(member.getFullName());
        winRate.setProgress(winRateDouble);
        loseLabel.setText(member.getLooses() + "");
        winLabel.setText(member.getWins() + "");

    }

    public static void show(Member member) {
        Profile.member = member;
        Parent parent = null;

        try {
            parent = FXMLLoader.load(Profile.class.getResource("UI" + File.separator + "profile.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage = new Stage();
        stage.setTitle("پروفایل");
        stage.setResizable(false);
        stage.setScene(new Scene(parent));

        stage.show();
    }

    @FXML
    void exit(ActionEvent event) {
        stage.close();
    }

}
