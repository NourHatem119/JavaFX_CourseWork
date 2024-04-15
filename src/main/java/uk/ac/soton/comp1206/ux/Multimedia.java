package uk.ac.soton.comp1206.ux;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class Multimedia {

  MediaPlayer audio;
  MediaPlayer music;
  public void playAudio(Media audio) {
    this.audio = new MediaPlayer(audio);
    this.audio.play();
  }

  public void playBackGroundMusic(Media music) {
    this.music = new MediaPlayer(music);
    this.music.play();
    this.music.setCycleCount(MediaPlayer.INDEFINITE);
  }
  public void stopMusic() {
    this.music.stop();
  }
}
