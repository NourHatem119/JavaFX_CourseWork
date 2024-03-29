package uk.ac.soton.comp1206.scene;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.App;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * The main menu of the game. Provides a gateway to the rest of the game.
 */
public class MenuScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);

    /**
     * Create a new menu scene
     * @param gameWindow the Game Window this will be displayed in
     */
    public MenuScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Menu Scene");
    }

    /**
     * Build the menu layout
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        var menuPane = new StackPane();
        menuPane.setMaxWidth(gameWindow.getWidth());
        menuPane.setMaxHeight(gameWindow.getHeight());
        menuPane.getStyleClass().add("menu-background");
        root.getChildren().add(menuPane);

        var mainPane = new BorderPane();
        menuPane.getChildren().add(mainPane);

        //Awful title
        var title = new Text("TetrECS");
        title.getStyleClass().add("title");
        mainPane.setTop(title);

        //For now, let us just add a btn1 that starts the game. I'm sure you'll do something way better.
        var btn1 = new Button("Single Player");
        var btn2 = new Button("MultiPlayer");
        var btn3 = new Button("How To Play");
        var btn4 = new Button("Exit");
        VBox box = new VBox(btn1, btn2, btn3, btn4);
        box.setAlignment(Pos.CENTER);
        mainPane.setCenter(box);
        System.out.println(mainPane.getCenter());
        //Bind the btn1 action to the startGame method in the menu
        btn1.setOnAction(this::startGame);
        btn4.setOnMouseClicked(e -> shutdown());
        btn3.setOnMouseClicked(this::howToPlay);
    }

    /**
     * Initialise the menu
     */
    @Override
    public void initialise() {

    }

    /**
     * Handle when the Start Game button is pressed
     * @param event event
     */
    private void startGame(ActionEvent event) {
        gameWindow.startChallenge();
    }
    private void howToPlay(MouseEvent event) {
        gameWindow.loadScene(new HowToPlayScene(gameWindow));
    }

    private void shutdown(){
        App.getInstance().shutdown();
    }

}
