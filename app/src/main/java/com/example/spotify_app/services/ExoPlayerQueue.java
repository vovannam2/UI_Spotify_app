package com.example.spotify_app.services;

import androidx.media3.common.MediaItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ExoPlayerQueue {
    private static ExoPlayerQueue instance = null;
    private final Random random = new Random();
    private List<MediaItem> currentQueue;
    private List<MediaItem> played;
    private boolean shuffle = false;
    private boolean repeat = false;
    private int currentPosition = 0;
    public static ExoPlayerQueue getInstance() {
        if (instance == null) {
            instance = new ExoPlayerQueue();
        }
        return instance;
    }
    private boolean isCurrentPositionOutOfBound(int pos) {
        return pos >= currentQueue.size() || pos < 0;
    }
    public boolean isShuffle() {
        return shuffle;
    }
    public void setShuffle(boolean shuffle) {
        this.shuffle = shuffle;
    }
    public boolean isRepeat() {
        return repeat;
    }
    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }
    public List<MediaItem> getCurrentQueue() {
        return currentQueue;
    }
    public void setCurrentQueue(List<MediaItem> queue) {
        this.played = new ArrayList<>();
        this.currentQueue = queue;
        if (this.shuffle) {
            shuffle();
        }
    }
    public MediaItem getCurrentMediaItem() {
        if (currentQueue == null || currentQueue.isEmpty()) {
            return null;
        }
        return currentQueue.get(currentPosition);
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }

    public void next() {
        played.add(currentQueue.get(currentPosition));
        if (!repeat) {
            currentPosition = (shuffle)
                    ? random.nextInt(currentQueue.size())
                    : isCurrentPositionOutOfBound(currentPosition + 1)
                    ? 0
                    : ++currentPosition;
        }
    }

    public void previous() {
        if (played.isEmpty()) {
            currentPosition = 0;
            return;
        }
        currentPosition = played.size() - 1;
        played.remove(played.size() - 1);
    }

    public void clear() {
        currentQueue = null;
        played = null;
        currentPosition = 0;
    }

    public MediaItem getRandomUnplayedSong() {
        if (currentQueue == null || currentQueue.isEmpty()) {
            return null;
        }
        if (played.size() == currentQueue.size()) {
            played.clear();
        }
        int randomIndex = random.nextInt(currentQueue.size());
        while (played.contains(randomIndex)) {
            randomIndex = random.nextInt(currentQueue.size());
        }
        return currentQueue.get(randomIndex);
    }

    public void addPlayedSong(MediaItem mediaItem) {
        played.add(mediaItem);
    }

    public void shuffle() {
        Collections.shuffle(currentQueue);
    }
}
