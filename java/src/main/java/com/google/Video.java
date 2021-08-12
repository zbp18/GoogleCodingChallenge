package com.google;

import java.util.Collections;
import java.util.List;

/**
 * A class used to represent a Video.
 */
class Video implements Comparable<Video> {

  private final String title;
  private final String videoId;
  private final List<String> tags;

  Video(String title, String videoId, List<String> tags) {
    this.title = title;
    this.videoId = videoId;
    this.tags = Collections.unmodifiableList(tags);
  }

  /**
   * Returns a list of videos in the library that aren't flagged.
   */
  String getTitle() {
    return title;
  }

  /**
   * Returns the video id of the video.
   */
  String getVideoId() {
    return videoId;
  }

  /**
   * Returns a readonly collection of the tags of the video.
   */
  List<String> getTags() {
    return tags;
  }

  /**
   * Allows lexicographical ordering of videos by their title.
   */
  @Override
  public int compareTo(Video otherVideo) {
    return getTitle().compareTo(otherVideo.getTitle());
  }

  /**
   * Displays a video in the following format: title (video_id) [tags].
   */
  @Override
  public String toString() {
    return String.format("%s (%s) [%s]", title, videoId, String.join(" ", tags));
  }
}
