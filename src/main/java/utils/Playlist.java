package utils;

import business.Song;

/**
 *
 * @author michelle
 */
public class Playlist {
    private Song[] catalogue;
    private int size;

    public Playlist(Song [] data){
        this.catalogue = data;
        this.size = data.length;
    }

    public boolean addSong(Song s){
        // todo: ADD addSong() LOGIC
        // Should not allow duplicate Songs to be added
        // Two songs are considered equal if they contain the same title, artist and album
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public Song[] searchByTag(String tag){
        // todo: ADD searchByTag() LOGIC
        // Should implement linear search, but not for a single result
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public Song[] getAllSongs(){
        // todo: ADD getAllSongs() LOGIC
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public Song findMostPopular(){
        // todo: ADD findMostPopular() LOGIC
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public String[] getAllTags(){
        // todo: ADD getAllTags() LOGIC
        // Note: Only one copy of each tag should appear in the returned array, no matter what case is used
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void sortSongsByNumTags(){
        // todo: ADD sortSongsByNumTags() LOGIC
        // Should implement merge sort to sort the data in DESCENDING order of tag count
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void addSongs(Song[] toBeAdded){
        // todo: ADD addSongs() LOGIC
        // Note: Should provide concatenation/unordered merging
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
