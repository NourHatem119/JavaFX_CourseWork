package uk.ac.soton.comp1206.scene;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

public class LobbyScene extends BaseScene {

  private static final Logger logger = LogManager.getLogger(LobbyScene.class);

  Communicator communicator;


  Timer timer;

  VBox channelChat;

  SimpleBooleanProperty host = new SimpleBooleanProperty(false);


  TextFlow channels;
  VBox leftPanel;
  TextFlow users = new TextFlow();

  Button startGame;
  Button leaveChannel;

  TextFlow chat;

  Text currentGames;
  Text create;

  /**
   * Create a new scene, passing in the GameWindow the scene will be displayed in
   *
   * @param gameWindow the game window
   */
  public LobbyScene(GameWindow gameWindow) {
    super(gameWindow);
    communicator = gameWindow.getCommunicator();
  }

  @Override
  public void initialise() {
    communicator.addListener(message ->
        Platform.runLater(() ->
            handle(message)));
    createTimer();
  }


  @Override
  public void build() {

    root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

    var lobbyPane = new BorderPane();
    lobbyPane.setMaxWidth(gameWindow.getWidth());
    lobbyPane.setMaxHeight(gameWindow.getHeight());
    lobbyPane.getStyleClass().add("menu-background");
    root.getChildren().add(lobbyPane);

    currentGames = new Text("Current Games");
    currentGames.getStyleClass().add("title");
    create = new Text("Host a new Game");
    create.getStyleClass().add("title");

    startGame = new Button("Start");
    leaveChannel = new Button("Leave");
    startGame.setOnAction(e -> {
      communicator.send("START");
      startGame();
    });
    leaveChannel.setOnAction(e -> leaveChannel());

    channelChat = new VBox();
    channelChat.setSpacing(10);
    leftPanel = new VBox();
    channels = new TextFlow();
    create.setOnMouseClicked(e -> {
      leftPanel.getChildren().remove(channels);
      var newChannelName = new TextField();
      newChannelName.setPromptText("Enter channel name");
      leftPanel.getChildren().add(newChannelName);
      leftPanel.getChildren().add(channels);
      newChannelName.setOnKeyPressed(key -> {
        if (key.getCode().equals(KeyCode.ENTER)) {
          createChannel(newChannelName.getText());
//          joinChannel(newChannelName.getText());
          showChannelChat(newChannelName.getText());
          leftPanel.getChildren().remove(newChannelName);
        }
      });
    });
    leftPanel.getChildren().addAll(currentGames, create, channels);
    lobbyPane.setRight(channelChat);

    lobbyPane.setLeft(leftPanel);
  }

  private void createTimer() {
    timer = new Timer();
    TimerTask requestChannels = new TimerTask() {
      @Override
      public void run() {
        communicator.send("LIST");
      }
    };
    timer.schedule(requestChannels, 0, 1000);
  }

  private void showChannelChat(String nameOfChannel) {

    var channelName = new Text(nameOfChannel);
    channelName.getStyleClass().add("title");
    channelChat.getChildren().clear();
    channelChat.getChildren().add(channelName);

    chat = new TextFlow();
    chat.getStyleClass().add("messages");
    chat.setMinWidth(300);
    chat.setPrefHeight(300);

    users.setMinWidth(200);
    chat.getChildren().add(users);

    var chatBox = new TextField();
    chatBox.setPromptText("Send a message");
    chatBox.setPrefWidth(40);

    var scrollChat = new ScrollPane();
    scrollChat.getStyleClass().add("scrollchat");
    scrollChat.setMinWidth(gameWindow.getWidth() / 2.0);
    scrollChat.setPrefHeight(400);
    scrollChat.setContent(chat);
    channelChat.getChildren().add(scrollChat);
    channelChat.getChildren().add(chatBox);
    HBox buttons = new HBox(startGame, leaveChannel);
    startGame.visibleProperty().bind(host);
    buttons.setSpacing(330);
    channelChat.getChildren().add(buttons);

    var howToChat = new Text(
        """
                        
            Welcome to the lobby\s
            Type /nick to change nickname\s
            Type /part to quit the channel\s
            Type /start to start the game
                        
            """);

    chat.getChildren().add(howToChat);

    chatBox.setOnKeyPressed(e -> {
      if (e.getCode().equals(KeyCode.ENTER)) {
        if (chatBox.getText().toLowerCase().startsWith("/start") && host.get()) {
          communicator.send("START");
          startGame();
        } else if (chatBox.getText().toLowerCase().startsWith("/part")) {
          leaveChannel();
        } else if (chatBox.getText().toLowerCase().startsWith("/nick")) {
          var nick = chatBox.getText().split(" ")[1];
          communicator.send("NICK " + nick);
        } else {
          communicator.send("MSG " + chatBox.getText());
        }
        chatBox.clear();
      }
    });
  }


  private void handle(String message) {
    if (message.startsWith("CHANNELS")) {
      handleChannels(message);
    } else if (message.startsWith("USERS")) {
      handleUsers(message);
    } else if (message.startsWith("MSG")) {
      handleMessages(message);
    } else if (message.contains("HOST")) {
      host.set(true);
    } else if (message.startsWith("ERROR")) {
      handleError(message);
    } else if (message.startsWith("START")) {
      startGame();
    };
  }

  private void handleError(String message) {
    Alert alert = new Alert(AlertType.ERROR);
    alert.setTitle("Error");
    alert.setContentText(message);
    alert.showAndWait();
  }

  private void handleMessages(String s) {
    String message = s.replace("MSG ", "").concat("\n");
    chat.getChildren().add(new Text("[" + LocalTime.now().format(DateTimeFormatter.ofPattern("HH"
        + ":mm")) + "] " + message));
  }

  private void handleUsers(String message) {
    this.users.getChildren().clear();
    String[] users = message.replace("USERS ", "").split("\n");
    for (String user : users) {
      this.users.getChildren().add(new Text(user));
    }
  }

  private void handleChannels(String message) {
    this.channels.getChildren().clear();
    String[] channels = message.replace("CHANNELS ", "").split("\n");
    for (String channel : channels) {
      var channelName = new Text(channel + "\n");
      channelName.getStyleClass().add("title");
      channelName.setOnMouseClicked(e -> {
        joinChannel(channel);
        showChannelChat(channel);
        host.set(message.contains("HOST"));
      });
      this.channels.getChildren().add(channelName);
    }
  }

  private void joinChannel(String channelName) {
    communicator.send("JOIN " + channelName);
  }

  private void createChannel(String channelName) {
    communicator.send("CREATE " + channelName);
  }

  private void leaveChannel() {
    communicator.send("PART");
    leftPanel.getChildren().clear();
    channelChat.getChildren().clear();
    leftPanel.getChildren().add(currentGames);
    leftPanel.getChildren().add(create);
    leftPanel.getChildren().add(channels);
  }

  protected void startGame() {
//    communicator.send("START");
    gameWindow.startMultiplayer();
    timer.cancel();
    timer.purge();
  }
}
