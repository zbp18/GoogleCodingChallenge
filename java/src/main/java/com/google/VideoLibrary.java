package com.google;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * A class used to represent a Video Library.
 */
class VideoLibrary {

  private final HashMap<String, Video> videos;

  // Keeps track of all the flagged videos and offers efficient retrieval capabilities.
  // Maps the videoId of each flagged video to its flag-reason.
  private Map<String, String> flaggedVideos;

  VideoLibrary() {
    this.videos = new HashMap<>();
    flaggedVideos = new HashMap<>();
    try {
      File file = new File(this.getClass().getResource("/videos.txt").getFile());

      Scanner scanner = new Scanner(file);
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();
        String[] split = line.split("\\|");
        String title = split[0].strip();
        String id = split[1].strip();
        List<String> tags;
        if (split.length > 2) {
          tags = Arrays.stream(split[2].split(",")).map(String::strip).collect(
              Collectors.toList());
        } else {
          tags = new ArrayList<>();
        }
        this.videos.put(id, new Video(title, id, tags));
      }
    } catch (FileNotFoundException e) {
      System.out.println("Couldn't find videos.txt");
      e.printStackTrace();
    }
  }

  List<Video> getVideos() {
    return new ArrayList<>(this.videos.values());
  }

  /**
   * Returns a video given a videoId or null if the corresponding video is not found.
   */
  Video getVideo(String videoId) {
    return this.videos.get(videoId);
  }

  /**
   * Returns a string representation of a video given a videoId or "" if the video is not found.
   */
  String getVideoString(String videoId) {
    StringBuilder stringBuilder = new StringBuilder();
    Video video = this.getVideo(videoId);
    if (video != null) {
      stringBuilder.append("  " + video);
      if (this.isFlagged(video.getVideoId())) {
        stringBuilder.append(" - FLAGGED (reason: " + this.getFlagReason(video.getVideoId()) + ")");
      }
    }
    return stringBuilder.toString();
  }

  void flagVideo(String videoId, String reason) {
    this.flaggedVideos.put(videoId, reason);
  }

  boolean isFlagged(String videoId) {
    return this.flaggedVideos.containsKey(videoId);
  }

  String getFlagReason(String videoId) {
    return this.flaggedVideos.get(videoId);
  }

  void allowVideo(String videoId) {
    this.flaggedVideos.remove(videoId);
  }

  /**
   * Returns a list of videos in the library that aren't flagged.
   */
  List<Video> getAllowedVideos() {
    List<Video> allowedVideos = new ArrayList<>();
    for (String videoId : this.videos.keySet()) {
      if (!this.flaggedVideos.containsKey(videoId)) {
        allowedVideos.add(this.videos.get(videoId));
      }
    }
    return allowedVideos;
  }

  @Override
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    for (String videoId : this.videos.keySet()) {
      Video video = this.getVideo(videoId);
      stringBuilder.append("  " + video);
      if (this.isFlagged(video.getVideoId())) {
        stringBuilder.append(" - FLAGGED (reason: " + this.getFlagReason(video.getVideoId()) + ")");
      }
      stringBuilder.append("\n");
    }
    return stringBuilder.toString();
  }
}
