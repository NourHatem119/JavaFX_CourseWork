package uk.ac.soton.comp1206.ux;

import java.io.File;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class Multimedia {

  public static final Media menuMusic = new Media(new File("d:\\Uni\\P_II"
      + "\\Coursework\\coursework\\src\\main\\resources\\music\\menu.mp3").toURI().toString());
  private static final String path = "d:\\Uni\\P_II\\Coursework\\coursework\\src\\main"
      + "\\resources";
  public static final Media challengeMusic = new Media(
      new File(path + "\\music\\game.wav").toURI().toString());
  public static final Media rotateEffect =
      new Media(new File(path + "\\sounds\\rotate.wav").toURI().toString());
  public static final Media placeEffect =
      new Media(new File(path + "\\sounds\\place.wav").toURI().toString());
  public static final Media lineClearEffect =
      new Media(new File(path + "\\sounds\\clear.wav").toURI().toString());
  public static final Media failEffect =
      new Media(new File(path + "\\sounds\\fail.wav").toURI().toString());
  public static final Media levelUpEffect =
      new Media(new File(path + "\\sounds\\level.wav").toURI().toString());
  public static final Media gameOverEffect =
      new Media(new File(path + "\\sounds\\explode.wav").toURI().toString());
  public static final Media lifeLostEffect =
      new Media(new File(path + "\\sounds\\lifelose.wav").toURI().toString());
  public static final Media notification =
      new Media(new File(path + "\\sounds\\message.wav").toURI().toString());
  public static final Media clickEffect =
      new Media(new File(path + "\\sounds\\click.wav").toURI().toString());
  public static final Media exitEffect =
      new Media(new File(path + "\\sounds\\Exit.wav").toURI().toString());
  public static final Media opening =
      new Media(new File(path + "\\music\\gameStart.wav").toURI().toString());
  public static MediaPlayer audio;
  private static MediaPlayer Music;

  public static MediaPlayer getMusic() {
    return Music;
  }

  static public void playAudio(Media sound) {
    audio = new MediaPlayer(sound);
    audio.setCycleCount(1);
    audio.play();
  }

  static public void playBackGroundMusic(Media music) {
    Music = new MediaPlayer(music);
    Music.play();
    Music.setCycleCount(MediaPlayer.INDEFINITE);
    Music.setVolume(0.25);
  }

  public static void stopMusic() {
    Music.stop();
  }
}
