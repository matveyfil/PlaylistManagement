package utils;

import business.Song;


public class Playlist {
    private Song[] catalogue;
    private int size;

    /**
     * Construct a playlist by array
     *
     * @param data initial song array
     */
    public Playlist(Song [] data){
        this.catalogue = data;
        this.size = data.length;
    }

    /**
     * Adds a song if it's not already present
     * Expands storage manually when neeeded
     *
     * @param s song to add
     * @return trure if added, false if null, duplicate, or capacity issue
     */
    public boolean addSong(Song s){
        if (s == null) return false; //false if null input

        for (int i = 0; i< size; i++){
            Song existing = catalogue[i]; //current song
            if (existing != null && existing.equals(s)) { //duplicate check
                return false;
            }
        }

        ensureCapacity(size + 1); //grow array if needed
        catalogue[size] = s;
        size++;
        return true;
    }

    /**
     * Finds all songs containing the provided tag using linear search.
     *
     * @param tag tag to search for
     * @return array of matching songs (possibly empty)
     */
    public Song[] searchByTag(String tag) {
        if (tag == null || size == 0) return new Song[0]; // nothing to search

        int matchCount = 0; // counter for matches
        for (int i = 0; i < size; i++) { // first pass count
            Song s = catalogue[i]; // current song
            if (s != null && s.containsTag(tag)) { // has tag?
                matchCount++; // tally match
            }
        }

        if (matchCount == 0) return new Song[0]; //no matches

        Song[] results = new Song[matchCount]; // allocate result array
        int idx = 0; // result index
        for (int i = 0; i < size; i++) { // second pass collect
            Song s = catalogue[i]; // current song
            if (s != null && s.containsTag(tag)) { // has tag?
                results[idx] = s; // store match
                idx++; // advance result index
            }
        }
        return results; // return matches
    }

    /**
     * Returns a trimmed copy of all songs currently in the playlist.
     *
     * @return array of songs of length size
     */
    public Song[] getAllSongs() {
        Song[] copy = new Song[size]; // allocate exact-length array
        for (int i = 0; i < size; i++) { // copy used songs
            copy[i] = catalogue[i]; // copy entry
        }
        return copy; // return clone
    }

    /**
     * Finds the song with the highest rating (first occurrence when tied).
     *
     * @return most popular song, or null if playlist is empty or only nulls
     */
    public Song findMostPopular() {
        if (size == 0) return null; // empty playlist
        int bestIndex = -1; // track index of best
        double bestRating = Double.NEGATIVE_INFINITY; // track best rating

        for (int i = 0; i < size; i++) { // scan songs
            Song s = catalogue[i]; // current song
            if (s == null) continue; // skip null slots
            double rating = s.getRating(); // song rating
            if (bestIndex == -1 || rating > bestRating) { // better rating?
                bestRating = rating; // update best rating
                bestIndex = i; // remember index
            }
        }

        return bestIndex >= 0 ? catalogue[bestIndex] : null; // return best or null
    }

    /**
     * Collects one copy of each unique tag (case-insensitive) across all songs.
     *
     * @return array of unique tags (may be empty)
     */
    public String[] getAllTags() {
        if (size == 0) return new String[0]; // no songs

        String[] unique = new String[8]; // initial buffer
        int uniqueCount = 0; // number of unique tags stored

        for (int i = 0; i < size; i++) { // scan songs
            Song s = catalogue[i]; // current song
            if (s == null) continue; // skip nulls
            String[] tags = s.getTags(); // song tags array
            int tagCount = s.getTagCount(); // used tags count
            for (int t = 0; t < tagCount; t++) { // iterate tags
                String tag = tags[t]; // current tag
                if (tag == null) continue; // skip null tags
                if (!containsIgnoreCase(unique, uniqueCount, tag)) { // new tag?
                    if (uniqueCount >= unique.length) { // need growth?
                        unique = growStringArray(unique); // expand buffer
                    }
                    unique[uniqueCount] = tag; // store tag
                    uniqueCount++; // increment unique counter
                }
            }
        }

        String[] trimmed = new String[uniqueCount]; // exact-sized array
        for (int i = 0; i < uniqueCount; i++) { // copy uniques
            trimmed[i] = unique[i]; // copy tag
        }
        return trimmed; // return unique tags
    }

    /**
     * Sorts songs in-place in descending order of tag count using merge sort.
     */
    public void sortSongsByNumTags() {
        if (catalogue == null || size <= 1) return; // nothing to sort
        Song[] temp = new Song[size]; // temp buffer for merging
        mergeSort(0, size - 1, temp); // perform merge sort
    }

    /**
     * Concatenates the provided songs onto the end of this playlist (ignores null entries).
     *
     * @param toBeAdded songs to append
     */
    public void addSongs(Song[] toBeAdded) {
        if (toBeAdded.length == 0) return; // nothing to add
        for (Song s : toBeAdded) { // iterate songs to add
            if (s == null) continue; // skip nulls
            ensureCapacity(size + 1); // grow if needed
            catalogue[size] = s; // append song
            size++; // increment size
        }
    }

    /**
     * Ensures the backing array can hold at least the requested capacity
     * Grows by doubling strategy or to the exact minimum, with manual copy
     *
     * @param minCapacity required minimum capacity
     */
    private void ensureCapacity(int minCapacity) {
        if (catalogue == null) { // if no array yet
            catalogue = new Song[minCapacity]; //create new array
            return;
        }
        if (catalogue.length >= minCapacity) return; //already enough space

        int newCapacity = catalogue.length * 2; //double size
        if (newCapacity < minCapacity) { //if still too small
            newCapacity = minCapacity; //bump to minimum
        }
        Song[] grown = new Song[newCapacity]; //new array
        for (int i=0; i < size; i++) {
            grown[i] = catalogue[i]; //manual copy
        }

        catalogue = grown; //replace backing array
    }

    /**
     * membership test over the used prefix of the array.
     */
    private boolean containsIgnoreCase(String[] data, int used, String value) {
        for (int i = 0; i < used; i++) { // scan used entries
            String existing = data[i]; // current value
            if (existing != null && existing.equalsIgnoreCase(value)) { // equal ignoring case?
                return true; // found
            }
        }
        return false; // not found
    }

    /**
     * Grows a string array by doubling its length, copying contents manually.
     */
    private String[] growStringArray(String[] original) {
        int newLength = original.length * 2; // double capacity
        if (newLength == 0) newLength = 1; // ensure at least 1
        String[] copy = new String[newLength]; // allocate new array
        for (int i = 0; i < original.length; i++) { // copy all elements
            copy[i] = original[i]; // manual copy
        }
        return copy; // return grown array
    }

    /**
     * Recursively sorts catalogue[left..right] by descending tag count using merge sort.
     */
    private void mergeSort(int left, int right, Song[] temp) {
        if (left >= right) return; // base case single element
        int mid = (left + right) / 2; // midpoint
        mergeSort(left, mid, temp); // sort left half
        mergeSort(mid + 1, right, temp); // sort right half
        merge(left, mid, right, temp); // merge halves
    }

    /**
     * Merges two sorted halves (by descending tag count) into the temp array, then copies back.
     */
    private void merge(int left, int mid, int right, Song[] temp) {
        int i = left; // pointer into left half
        int j = mid + 1; // pointer into right half
        int k = left; // pointer into temp

        while (i <= mid && j <= right) { // merge while both halves have items
            int leftCount = tagCountSafe(catalogue[i]); // tag count left item
            int rightCount = tagCountSafe(catalogue[j]); // tag count right item
            if (leftCount >= rightCount) { // left has more/equal tags
                temp[k] = catalogue[i]; // take left item
                i++; // advance left pointer
            } else {
                temp[k] = catalogue[j]; // take right item
                j++; // advance right pointer
            }
            k++; // advance temp pointer
        }

        while (i <= mid) { // copy remaining left items
            temp[k] = catalogue[i]; // copy item
            i++; // advance left pointer
            k++; // advance temp pointer
        }

        while (j <= right) { // copy remaining right items
            temp[k] = catalogue[j]; // copy item
            j++; // advance right pointer
            k++; // advance temp pointer
        }

        for (int idx = left; idx <= right; idx++) { // copy merged back
            catalogue[idx] = temp[idx]; // overwrite original slice
        }
    }

    /**
     * Safely returns tag count for a song (zero when null).
     */
    private int tagCountSafe(Song s) {
        return s == null ? 0 : s.getTagCount(); // guard nulls
    }
}
