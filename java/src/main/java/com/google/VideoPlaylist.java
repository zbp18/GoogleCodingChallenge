package com.google;

import java.util.ArrayList;
import java.util.List;

/**
 * A class used to represent a Playlist
 */
class VideoPlaylist {
  String playlistName;
  List<String> videos;

  public VideoPlaylist(String playlistName) {
    this.videos = new ArrayList<>();
    this.playlistName = playlistName;
  }

  public String getName() {
    return this.playlistName;
  }

  public boolean containsVideo(String videoId) {
    return this.videos.contains(videoId);
  }

  public List<String> getVideos() {
    return new ArrayList<>(videos);
  }

  public void addVideo(String videoId) {
    this.videos.add(videoId);
  }

  public void removeVideo(String videoId) {
    videos.remove(videoId);
  }

  public void clear() {
    videos.clear();
  }
}
