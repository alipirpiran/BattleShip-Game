package battleShip.models;

import battleShip.Error.Error;
import battleShip.core.App;
import com.jfoenix.controls.JFXButton;
import com.sun.jmx.snmp.SnmpGauge;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

public class OnlineUser extends HBox {

    Member member;
    App app;
    Pane hoverPane;

    String hoverColor = "#2ecc71";
    String defColor = "#27ae60";
    int height = 40;

    public OnlineUser(Member member, App app){
        this.member = member;
        this.app = app;
        setStyleForHbox();
        setInfo();

    }

    private void setStyleForHbox(){
        setAlignment(Pos.CENTER_LEFT);

        setStyle("-fx-background-color: " + defColor);
        this.setOnMouseEntered(e->{
            setStyle("-fx-background-color: " + hoverColor);
            showRequestForm();
        });

        this.setOnMouseExited(e -> {
            setStyle("-fx-background-color: " + defColor);
            hideRequestFrom();
        });

        setPrefHeight(height);
        setPrefWidth(288);
    }

    private void setInfo(){
        Label name = new Label(member.getFullName());
        name.setAlignment(Pos.CENTER);
        name.setPrefWidth(196);
        name.setMinWidth(196);


        Label status = new Label(member.getStatus().name());
        status.setFont(Font.font("bold"));
        status.setAlignment(Pos.CENTER);
        status.setPrefWidth(84);
        status.setMinWidth(84);

        getChildren().addAll(name,new Separator(Orientation.VERTICAL), status);
    }

    private void showRequestForm(){
        hoverPane = new Pane();

        hoverPane.setPrefWidth(50);
        hoverPane.setPrefHeight(40);

        hoverPane.setStyle("-fx-background-color: #7f8c8d");

        JFXButton challengeBTN = new JFXButton();
        if (member.getStatus() == Status.Online)
            challengeBTN.setText("Play");

        if (member.getStatus() == Status.Playing)
            challengeBTN.setText("Playing");

        if (member.getStatus() == Status.Requested)
            challengeBTN.setText("Requested");

        challengeBTN.setStyle("-fx-background-color: #f1c40f; -fx-background-radius: 10");
        challengeBTN.setPrefWidth(60);
        challengeBTN.setPrefHeight(height);
        challengeBTN.setOnAction(e -> {
            if (member.getUsername().equals(app.logedInMember.getUsername())){
                Error.showError("شما نمیتوانید به خودتان درخواست بازی بدهید !");
                return;
            }

            if (member.getStatus() == Status.Playing){
                Error.showError("کاربر در حال انجام بازی است !");
                return;
            }

            if (member.getStatus() == Status.Requested){
                Error.showError("به کاربر درخواست داده شده است");
                return;
            }
            app.game.controller.menu.close();
            app.requestToPlay(this.member);
            challengeBTN.setText("Requested");
        });
        hoverPane.getChildren().add(challengeBTN);

        this.getChildren().add(hoverPane);
    }

    private void hideRequestFrom(){
        this.getChildren().remove(hoverPane);
    }




}
