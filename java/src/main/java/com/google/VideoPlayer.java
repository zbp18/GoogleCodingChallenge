package com.google;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * A class used to represent a Video Player.
 */
public class VideoPlayer {

  private final VideoLibrary videoLibrary;

  // Maps the name of every playlist in the player to their VideoPlaylist instance.
  private Map<String, VideoPlaylist> playlists;

  // Represents the currently playing video.
  private Video currentVideo;

  // Represents the paused status of the currently playing video or true if there's no video currently playing.
  private boolean isPaused;

  public VideoPlayer() {
    // Constructs a VideoPlayer instance.
    this.videoLibrary = new VideoLibrary();
    // Provide a String Comparator that accounts for case-insensitive playlist names.
    // Please note: I have omitted the use of the "this" keyword from here onwards, to avoid cluttering the code.
    // (No disambiguate variable references, passing over instance to another object, or calling of other constructors).
    playlists = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    // No video is currently playing.
    currentVideo = null;
    isPaused = true;
  }

  public void numberOfVideos() {
    System.out.printf("%s videos in the library%n", videoLibrary.getVideos().size());
  }

  public void showAllVideos() {
    System.out.println("Here's a list of all available videos:");
    List<Video> videos = videoLibrary.getVideos();
    videos.stream()
        // Sorts videos according to the compareTo method override for Video.
        .sorted()
        .collect(Collectors.toList())
        // Retrieves the string representation of each video including its flagged status.
        .forEach(video -> System.out.println(videoLibrary.getVideoString(video.getVideoId())));
  }

  public void playVideo(String videoId) {
    if (videoLibrary.isFlagged(videoId)) {
      String reason = videoLibrary.getFlagReason(videoId);
      System.out.printf("Cannot play video: Video is currently flagged (reason: %s)%n", reason);
    } else {
      Video video = videoLibrary.getVideo(videoId);
      if (video == null) {
        // Continue playing current video if the video does not exist.
        System.out.println("Cannot play video: Video does not exist");
      } else {
        if (currentVideo != null) {
          // Displays a note that this currently playing will be stopped.
          System.out.printf("Stopping video: %s%n", currentVideo.getTitle());
        }
        System.out.printf("Playing video: %s%n", video.getTitle());
        currentVideo = video;
        isPaused = false;
      }
    }
  }

  public void stopVideo() {
    if (currentVideo == null) {
      System.out.println("Cannot stop video: No video is currently playing");
    } else {
      System.out.printf("Stopping video: %s%n", currentVideo.getTitle());
      currentVideo = null;
      isPaused= true;
    }
  }

  public void playRandomVideo() {
    if (currentVideo != null) {
      // Displays a note that this currently playing will be stopped.
      System.out.printf("Stopping video: %s%n", currentVideo.getTitle());
    }
    // Flagged videos cannot be selected.
    List<Video> videos = videoLibrary.getAllowedVideos();
    if (!videos.isEmpty()) {
      Random random = new Random();
      int index = random.nextInt(videos.size());
      Video randomVideo = videos.get(index);
      System.out.printf("Playing video: %s%n", randomVideo.getTitle());
      currentVideo = randomVideo;
      isPaused = false;
    } else {
      // There are no available videos that are not flagged.
      System.out.println("No videos available");
    }
  }

  public void pauseVideo() {
    if (currentVideo == null) {
      System.out.println("Cannot pause video: No video is currently playing");
    } else {
      if (isPaused) {
        System.out.printf("Video already paused: %s%n", currentVideo.getTitle());
      } else {
        System.out.printf("Pausing video: %s%n", currentVideo.getTitle());
      }
      isPaused = true;
    }
  }

  public void continueVideo() {
    if (!isPaused) {
      System.out.println("Cannot continue video: Video is not paused");
    } else {
      if (currentVideo == null) {
        System.out.println("Cannot continue video: No video is currently playing");
      } else {
        System.out.printf("Continuing video: %s%n", currentVideo.getTitle());
        isPaused = false;
      }
    }
  }

  public void showPlaying() {
    if (currentVideo == null) {
      System.out.println("No video is currently playing");
    } else {
      // Display the title, video_id, video tags and paused status of the video that's currently playing.
      System.out.printf("Currently playing: %s%s%n", currentVideo, isPaused ? " - PAUSED" : "");
    }
  }

  public void createPlaylist(String playlistName) {
    // Performs a case insensitive search for playlistName over player's existing playlists.
    if (playlists.containsKey(playlistName)) {
      System.out.println("Cannot create playlist: A playlist with the same name already exists");
    } else {
      // Creates a new empty playlist with a unique name.
      playlists.put(playlistName, new VideoPlaylist(playlistName));
      System.out.printf("Successfully created new playlist: %s%n", playlistName);
    }
  }

  public void addVideoToPlaylist(String playlistName, String videoId) {
    // Cannot add currently flagged video to the playlist.
    if (videoLibrary.isFlagged(videoId)) {
      String reason = videoLibrary.getFlagReason(videoId);
      System.out.printf("Cannot add video to %s: " +
          "Video is currently flagged (reason: %s)%n", playlistName, reason);
    } else {
      if (!playlists.containsKey(playlistName)) {
        System.out.printf("Cannot add video to %s: Playlist does not exist%n", playlistName);
      } else if (videoLibrary.getVideo(videoId) == null) {
        System.out.printf("Cannot add video to %s: Video does not exist%n", playlistName);
      } else {
        VideoPlaylist playlist = playlists.get(playlistName);
        if (videoLibrary.isFlagged(videoId)) {
          System.out.printf("Cannot add video to %s: " +
              "Video is currently flagged (reason: %s)%n", playlistName, videoLibrary.getFlagReason(videoId));
        } else if (playlist.containsVideo(videoId)) {
          System.out.printf("Cannot add video to %s: Video already added%n", playlistName);
        } else {
          playlist.addVideo(videoId);
          System.out.printf("Added video to %s: %s%n", playlistName, videoLibrary.getVideo(videoId).getTitle());
        }
      }
    }
  }

  public void showAllPlaylists() {
    if (playlists.isEmpty()) {
      System.out.println("No playlists exist yet");
    } else {
      System.out.println("Showing all playlists:");
      for (VideoPlaylist playlist: playlists.values()) {
        // Shows the names (those that were originally created in createPlaylist) of all the available playlists.
        System.out.printf("  %s%n", playlist.getName());
      }
    }
  }

  public void showPlaylist(String playlistName) {
    if (!playlists.containsKey(playlistName)) {
      System.out.printf("Cannot show playlist %s: Playlist does not exist%n", playlistName);
    } else {
      System.out.printf("Showing playlist: %s%n", playlistName);
      VideoPlaylist playlist = playlists.get(playlistName);
      List<String> videos = playlist.getVideos();
      if (videos.isEmpty()) {
        System.out.println("  No videos here yet");
      } else {
        for (String videoId: videos) {
          // Displays videos in the same order as they were added to the playlist's video list.
          System.out.println(videoLibrary.getVideoString(videoId));
        }
      }
    }
  }

  public void removeFromPlaylist(String playlistName, String videoId) {
    if (!playlists.containsKey(playlistName)) {
      System.out.printf("Cannot remove video from %s: Playlist does not exist%n", playlistName);
    } else if (videoLibrary.getVideo(videoId) == null) {
      System.out.printf("Cannot remove video from %s: Video does not exist%n", playlistName);
    } else {
      VideoPlaylist playlist = playlists.get(playlistName);
      if (!playlist.containsVideo(videoId)) {
        System.out.printf("Cannot remove video from %s: Video is not in playlist%n", playlistName);
      } else {
        playlist.removeVideo(videoId);
        System.out.printf("Removed video from %s: %s%n", playlistName, videoLibrary.getVideo(videoId).getTitle());
      }
    }
  }

  public void clearPlaylist(String playlistName) {
    if (!playlists.containsKey(playlistName)) {
      System.out.printf("Cannot clear playlist %s: Playlist does not exist%n", playlistName);
    } else {
      // Removes videos from the playlist while keeping the playlist itself.
      playlists.get(playlistName).clear();
      System.out.printf("Successfully removed all videos from %s%n", playlistName);
    }
  }

  public void deletePlaylist(String playlistName) {
    if (!playlists.containsKey(playlistName)) {
      System.out.printf("Cannot delete playlist %s: Playlist does not exist%n", playlistName);
    } else {
      playlists.remove(playlistName);
      System.out.printf("Deleted playlist: %s%n", playlistName);
    }
  }

  public void searchVideos(String searchTerm) {
    List<Video> videosToPlay = new ArrayList<>();
    // Flagged videos should not show up in search result when searching by text.
    for (Video video: videoLibrary.getAllowedVideos()) {
      // Performs a case-insensitive search over video titles for searchTerm.
      if (video.getTitle().toLowerCase(Locale.ROOT).contains(searchTerm.toLowerCase(Locale.ROOT))) {
        videosToPlay.add(video);
      }
    }
    displayVideos(searchTerm, videosToPlay);
  }

  public void searchVideosWithTag(String tagName) {
    List<Video> videosToPlay = new ArrayList<>();
    // Flagged videos should not show up in search result when searching by tag.
    for (Video video: videoLibrary.getAllowedVideos()) {
      // Performs a case-insensitive search over video tags for tagName.
      if (video.getTags().toString().toLowerCase(Locale.ROOT).contains(tagName.toLowerCase(Locale.ROOT))) {
        videosToPlay.add(video);
      }
    }
    displayVideos(tagName, videosToPlay);
  }

  public void flagVideo(String videoId) {
    flagVideo(videoId, "Not supplied");
  }

  public void flagVideo(String videoId, String reason) {
    Video video = videoLibrary.getVideo(videoId);
    if (video == null) {
      System.out.println("Cannot flag video: Video does not exist");
    } else {
      if (videoLibrary.isFlagged(videoId)) {
        System.out.println("Cannot flag video: Video is already flagged");
      } else {
        // If the video is flagged while it's playing or paused, the flagging also serves as a STOP command.
        if (currentVideo != null && currentVideo.equals(video)) {
          stopVideo();
        }
        // Marks a video as flagged with the specified reason.
        videoLibrary.flagVideo(videoId, reason);
        System.out.printf("Successfully flagged video: %s (reason: %s)%n", video.getTitle(), reason);
      }
    }
  }

  public void allowVideo(String videoId) {
    Video video = videoLibrary.getVideo(videoId);
    if (video == null) {
      System.out.println("Cannot remove flag from video: Video does not exist");
    } else if (!videoLibrary.isFlagged(videoId)) {
      System.out.println("Cannot remove flag from video: Video is not flagged");
    } else {
      // Updates the video to allow it to be playable again.
      videoLibrary.allowVideo(videoId);
      System.out.printf("Successfully removed flag from video: %s%n", video.getTitle());
    }
  }

  /**
   * Prints a display string for the resulting message after performing the search.
   * @param searchQuery user-specified search term or tag.
   * @param searchResult the list of videos whose titles contain searchInput.
   */
  private void displayVideos(String searchQuery, List<Video> searchResult) {
    if (searchResult.isEmpty()) {
      System.out.printf("No search results for %s%n", searchQuery);
    } else {
      System.out.printf("Here are the results for %s:%n", searchQuery);
      // Sorts videos according to the compareTo method override for Video.
      Collections.sort(searchResult);
      // Stores the number of search results.
      AtomicInteger count = new AtomicInteger(0);
      searchResult.stream().sorted().forEach(video -> {
        System.out.printf("  %d) %s%n", count.incrementAndGet(), video);
      });
      System.out.println("Would you like to play any of the above? If yes, specify the number of the video.");
      System.out.println("If your answer is not a valid number, we will assume it's a no.");
      // Read in the user-input. Ignore this if it is not a valid number.
      Scanner scanner = new Scanner(System.in);
      try {
        var input = scanner.nextInt() - 1;
        if (input >= 0 && input <= count.get()) {
          System.out.printf("Playing video: %s%n", searchResult.get(input).getTitle());
        }
      } catch (InputMismatchException e) {
      }
    }
  }
}