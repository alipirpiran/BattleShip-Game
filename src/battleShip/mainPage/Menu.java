package battleShip.mainPage;

import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerSlideCloseTransition;
import javafx.animation.Transition;
import javafx.animation.TranslateTransition;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class Menu {

    private Transition close;
    private TranslateTransition transition = new TranslateTransition();
    private boolean opened, opening, closed, closing;
    private VBox menuPane;

    public Menu(JFXHamburger hambur, VBox menuPane){
        closed = true;

        this.menuPane = menuPane;
        close = new HamburgerSlideCloseTransition(hambur);
        transition.setNode(menuPane);
        transition.setDuration(Duration.millis(500));
        close.setRate(-1);

        hambur.setOnMouseClicked(event -> menuPane());
    }

    void menuPane() {
        if (closing || opening)
            return;

        if (closed) {
            show();
            return;
        }

        if (opened) {
            close();
            return;
        }
    }

    void show() {
        menuPane.toFront();
        opening = true;
        transition.setToX(288);
        transition.setOnFinished((event) -> {
            opened = true;
            opening = false;
            closed = false;
            closing = false;
            transition.stop();
        });
        transition.play();

        close.setRate(close.getRate() * -1);
        close.play();

    }

    public void close() {
        if (!opened)
            return;

        closing = true;
        transition.setToX(0);
        transition.setOnFinished((event -> {
            closed = true;
            closing = false;
            opening = false;
            opened = false;
            transition.stop();
        }));
        transition.play();

        close.setRate(close.getRate() * -1);
        close.play();

    }
}
