package uk.ac.soton.comp1206.scene;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.ux.Multimedia;

public class LobbyScene extends BaseScene {

  private static final Logger logger = LogManager.getLogger(LobbyScene.class);

  Communicator communicator;


  Timer timer;

  VBox channelChat; //Channel Chat and other relevant components.

  SimpleBooleanProperty host = new SimpleBooleanProperty(false); //Whether a host or not.


  TextFlow channels; //ui display of the channels.
  VBox leftPanel; //title, create a game button, and current channels.
  TextFlow users = new TextFlow(); //Current users in the channel.

  Button startGame;
  Button leaveChannel;

  TextFlow chat;

  Text currentGames;
  Text createChannel;

  boolean joined;
  ArrayList<String> channelsNames = new ArrayList<>();

  /**
   * Create a new scene, passing in the GameWindow the scene will be displayed in
   *
   * @param gameWindow the game window
   */
  public LobbyScene(GameWindow gameWindow) {
    super(gameWindow);
    communicator = gameWindow.getCommunicator();
  }

  /**
   * handles when a message is received from the server.
   *
   * @param message message received from the Server.
   */
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
    } else if (message.startsWith("JOIN")) {
      handleJoin(message);
    }
  }

  /**
   * handles joining when joining/creating a channel.
   *
   * @param message message received from the Server.
   */
  private void handleJoin(String message) {
    String[] messageParts = message.split(" ");
    if (!joined) {
      Multimedia.playAudio(Multimedia.notification);
      showChannelChat(messageParts[1]);
      joined = true;
    }
  }

  /**
   * Handles when an error happens.
   *
   * @param message message received from the Server.
   */
  private void handleError(String message) {
    Alert alert = new Alert(AlertType.ERROR);
    alert.setTitle("Error");
    alert.setContentText(message);
    alert.showAndWait();
  }

  /**
   * Handles Sending a message through the ui.
   *
   * @param s message received from the Server.
   */
  private void handleMessages(String s) {
    Multimedia.playAudio(Multimedia.notification);
    String message = s.replace("MSG ", "");
    String[] messageParts = message.split(":");
    message = messageParts[0] + ": " + messageParts[1].concat("\n");
    Text messageText = new Text("[" + LocalTime.now().format(DateTimeFormatter.ofPattern("HH"
        + ":mm")) + "] " + message);
    messageText.setWrappingWidth(chat.getWidth());
    chat.getChildren().add(messageText);
  }

  /**
   * Handles adding the usernames of the current users to the ui.
   *
   * @param message message received from the Server.
   */
  private void handleUsers(String message) {
    this.users.getChildren().clear();
    String[] users = message.replace("USERS ", "").split("\n");
    for (String user : users) {
      this.users.getChildren().add(new Text(user));
    }
  }

  /**
   * Handles adding the channels to the ui.
   *
   * @param message message received from the Server.
   */
  private void handleChannels(String message) {
    this.channels.getChildren().clear();
    String[] channels = message.replace("CHANNELS ", "").split("\n");
    channelsNames.addAll(List.of(channels));
    for (String channel : channels) {
      var channelName = new Text(channel + "\n");
      channelName.getStyleClass().add("heading");
      channelName.setOnMouseClicked(e -> {
        joinChannel(channel);
        if (!joined) {
          showChannelChat(channel);
        }
        host.set(message.contains("HOST"));
      });
      this.channels.getChildren().add(channelName);
    }
  }


  /**
   * Initialise the Lobby.
   */
  @Override
  public void initialise() {
    communicator.addListener(message ->
        Platform.runLater(() ->
            handle(message)));
    createTimer();
    scene.setOnKeyPressed(this::keyClicked);
  }

  /**
   * Builds and displays the left panel which contains the channels received from the server, a
   * button that allows player to create a new channel, and a title.
   */
  void buildLeftPanel() {

    leftPanel = new VBox();
    currentGames = new Text("Current Games");
    currentGames.getStyleClass().add("title");

    createChannel = new Text("Host a new Game");
    createChannel.getStyleClass().add("title");
    createChannel.setOnMouseClicked(e -> {
      leftPanel.getChildren().clear();
      leftPanel.getChildren().addAll(currentGames, createChannel);
      var newChannelName = new TextField();
      newChannelName.setPromptText("Enter channel name");
      leftPanel.getChildren().add(newChannelName);

      leftPanel.getChildren().add(channels);
      newChannelName.setOnKeyPressed(key -> {
        if (key.getCode().equals(KeyCode.ENTER)) {
          createChannel(newChannelName.getText());
          if (!joined && !channelsNames.contains(newChannelName.getText())) {
            showChannelChat(newChannelName.getText());
          }
          leftPanel.getChildren().remove(newChannelName);
        }
      });
    });

    channels = new TextFlow();
    channels.setLineSpacing(15);
    channels.setPadding(new Insets(25, channels.getPadding().getRight(),
        channels.getPadding().getBottom(), channels.getPadding().getLeft()));

    leftPanel.getChildren().addAll(currentGames, createChannel, channels);
  }

  /**
   * Builds the whole ui display of the channel chat and other relevant components.
   */
  void buildChannelChat() {
    channelChat = new VBox();
    channelChat.setSpacing(10);
    channelChat.setMaxWidth(gameWindow.getWidth() / 2.0);

    startGame = new Button("Start");
    leaveChannel = new Button("Leave");
    startGame.setOnAction(e -> {
      communicator.send("START");
      startGame();
    });
    leaveChannel.setOnAction(e -> leaveChannel());
  }


  /**
   * Build the Lobby.
   */
  @Override
  public void build() {

    root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

    var lobbyPane = new BorderPane();
    lobbyPane.setMaxWidth(gameWindow.getWidth());
    lobbyPane.setMaxHeight(gameWindow.getHeight());
    lobbyPane.getStyleClass().add("menu-background");
    root.getChildren().add(lobbyPane);

    buildLeftPanel();
    buildChannelChat();

    lobbyPane.setRight(channelChat);
    lobbyPane.setLeft(leftPanel);
  }

  /**
   * Creates a timer that periodically requests the channels that exist from the server.
   */
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

  /**
   * handles when a key is clicked in the presence of the chat Box.
   *
   * @param chatBox The chatBox that the event happened in its presence
   * @param e       the Event that happened in the presence of the chatBox
   */
  void handleKeyClicked(TextField chatBox, KeyEvent e) {
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
    } else if (e.getCode().equals(KeyCode.ESCAPE) && joined) {
      leaveChannel();
      timer.cancel();
      timer.purge();
    }
  }

  /**
   * Displays the chat of the channel joined or created.
   *
   * @param nameOfChannel The name of the channel Joined/Created
   */
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
    chat.setMaxWidth(scrollChat.getWidth());
    scrollChat.setContent(chat);
    channelChat.getChildren().add(scrollChat);
    channelChat.getChildren().add(chatBox);
    HBox buttons = new HBox(startGame, leaveChannel);
    startGame.visibleProperty().bind(host);
    buttons.setSpacing(50);
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
      handleKeyClicked(chatBox, e);
    });
  }

  /**
   * Allows the player to join a channel.
   *
   * @param channelName The name of the channel to be joined
   */
  private void joinChannel(String channelName) {
    communicator.send("JOIN " + channelName);
  }

  /**
   * Allows the Player to create a channel.
   *
   * @param channelName The name of the channel to be created
   */
  private void createChannel(String channelName) {
    communicator.send("CREATE " + channelName);
  }

  /**
   * Allows the Player to leave the channel.
   */
  private void leaveChannel() {
    communicator.send("PART");
    joined = false;
    leftPanel.getChildren().clear();
    channelChat.getChildren().clear();
    leftPanel.getChildren().add(currentGames);
    leftPanel.getChildren().add(createChannel);
    leftPanel.getChildren().add(channels);
  }

  /**
   * starts the game and stops the timer requesting the channels.
   */
  protected void startGame() {
    Multimedia.stopMusic();
    gameWindow.startMultiplayer();
    timer.cancel();
    timer.purge();
  }

  /**
   * handles when a key is casually clicked
   *
   * @param keyClicked The Key that has been clicked
   */
  @Override
  protected void keyClicked(KeyEvent keyClicked) {
    super.keyClicked(keyClicked);
    if (keyClicked.getCode().equals(KeyCode.ESCAPE)) {
      timer.cancel();
      timer.purge();
    }
  }
}
