package uk.ac.soton.comp1206.scene;

import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.ux.Multimedia;

/**
 * The main menu of the game. Provides a gateway to the rest of the game.
 * TODO add Menu Animations and Visual Effects
 */
public class MenuScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);
    private final Multimedia music = new Multimedia();


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

        //For now, let us just add a button that starts the game. I'm sure you'll do something way better.
        var play = new Button("Play");
        var multiplayer = new Button("Multiplayer");
        var instructions = new Button("How To Play");
        var exit = new Button("Exit"); //TODO listen and exit game, using button and escape
        var buttons = new VBox(play, multiplayer, instructions, exit);
        buttons.setAlignment(Pos.CENTER);
        mainPane.setCenter(buttons);

        //Bind the button action to the startGame method in the menu
        play.setOnAction(this::startGame);
        instructions.setOnAction(this::startInstructions);
        multiplayer.setOnAction(this::startLobby);
    }

    private void startLobby(ActionEvent event) {
        gameWindow.startLobby();
    }

    /**
     * Initialise the menu
     */
    @Override
    public void initialise() {
        logger.info("Initialising Menu Scene...");
        Multimedia.playBackGroundMusic(Multimedia.menuMusic);
    }

    /**
     * Handle when the Start Game button is pressed
     * @param event event
     */
    private void startGame(ActionEvent event) {
        music.stopMusic();
        gameWindow.startChallenge();
    }

    private void startInstructions(ActionEvent event) {
        music.stopMusic();
        gameWindow.startInstructions();
    }

}
