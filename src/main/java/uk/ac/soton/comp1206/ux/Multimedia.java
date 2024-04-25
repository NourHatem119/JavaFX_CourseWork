package uk.ac.soton.comp1206.ux;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class Multimedia {


  private static final String path = "d:\\Uni\\P_II\\Coursework\\coursework\\src\\main"
      + "\\resources";

  public static final Media settingsMusic =
      new Media(new File(path + "\\music\\settings.mp3").toURI().toString());
  public static final Media menuMusic =
      new Media(new File(path + "\\music\\menu.mp3").toURI().toString());
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

  private static final File config = new File("config.txt");
  public static MediaPlayer audio;
  private static MediaPlayer Music;

  private static Double volume = getVolume();

  public static MediaPlayer getMusic() {
    return Music;
  }

  static public void playAudio(Media sound) {
    audio = new MediaPlayer(sound);
    audio.setCycleCount(1);
    audio.play();
    audio.setVolume(volume + 0.3);
  }

  static public void playBackGroundMusic(Media music) {
    if (Music != null)
      Music.stop();
    Music = new MediaPlayer(music);
    Music.play();
    Music.setCycleCount(MediaPlayer.INDEFINITE);
    Music.setVolume(volume);
  }

  public static void stopMusic() {
    Music.stop();
  }

  public static double getVolume() {
    if (!config.exists()) {
      try {
        config.createNewFile();
        BufferedWriter out = new BufferedWriter(new FileWriter(config));
        out.write(String.valueOf(volume));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    try (BufferedReader reader = new BufferedReader(new FileReader(config))) {
      String volumeAsText = reader.readLine();
      volume = Double.parseDouble(volumeAsText);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return volume;
  }

  public static void setVolume(double newVolume) {
    volume = newVolume > 0 ? newVolume : volume;
    if (newVolume > 0) {
      try (BufferedWriter writer = new BufferedWriter(new FileWriter(config))) {
        writer.write(Double.toString(newVolume));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
