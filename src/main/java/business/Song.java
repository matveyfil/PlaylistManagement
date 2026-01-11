package business;

import java.util.Arrays;
import java.util.Objects;

public class Song {
    private String title;
    private String artist;
    private String album;
    private double rating;
    private String genre;
    private String [] tags;
    private int tagCount;

    public Song(String title, String artist, String album,
                double rating, String genre, String[] initialTags) {

        this.title = title;
        this.artist = artist;
        this.album = album;
        this.rating = rating;
        this.genre = genre;

        // creating initial capacity for the internal tags storage
        int capacity = 0;
        if (initialTags != null) {
            capacity = initialTags.length * 2; // x2 space for future addTag calls
        }

        // Create internal storage for tags
        this.tags = new String[capacity];
        this.tagCount = 0;

        /*
         * Insert each initial tag using addTag(...) so we won't have duplicates and tags will remain sorted
         */
        if (initialTags != null) {
            for (int i = 0; i < initialTags.length; i++) {
                addTag(initialTags[i]);
            }
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String[] getTags() {
        return tags;
    }

    // You may not add the setTags method

    public int getTagCount() {
        return tagCount;
    }

    public void setTagCount(int tagCount) {
        this.tagCount = tagCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Song song = (Song) o;
        return Objects.equals(title, song.title) && Objects.equals(artist, song.artist) && Objects.equals(album, song.album);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, artist, album);
    }

    @Override
    public String toString() {
        return "Song{" +
                "title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", album='" + album + '\'' +
                ", rating=" + rating +
                ", genre='" + genre + '\'' +
                ", tags=" + Arrays.toString(tags) +
                ", tagCount=" + tagCount +
                '}';
    }

    /**
     * Adding the tag into the backing tags array in sorted order
     * Doesn't add duplicates (case-sensitive) - performs shifting
     * If the backing array is full, the method returns false.
     *
     * @param tag the tag to add
     * @return true if the tag was added, false otherwise (duplicate, null/empty or no space)
     */
    public boolean addTag(String tag) {
        if (tag == null) return false;
        String newTag = tag.trim();
        if (newTag.isEmpty()) return false;
        if (tags.length == 0) return false;
        if (tagCount < 0) tagCount = 0;

        // If there are no used tags, insert at index 0
        if (tagCount == 0) {
            tags[0] = newTag;
            tagCount = 1;
            return true;
        }

        // If backing array, no more elements
        if (tagCount >= tags.length) return false;

        // Using binary search to find insertion point and detect duplicates
        int low = 0;
        int high = tagCount - 1;
        while (low <= high) {
            //calculate middle index
            int mid = (low + high) >>> 1;
            String midVal = tags[mid];
            int cmp = midVal.compareToIgnoreCase(newTag);
            if (cmp == 0) {
                // duplicate
                return false;
            // corect position must be in right half
            } else if (cmp < 0) {
                low = mid + 1;
            // corect position must be in left half
            } else {
                high = mid - 1;
            }
        }

        int insertIdx = low; // insertion point

        //Shift elemnts right to make space
        for (int i = tagCount; i > insertIdx; i--) {
            //Shift elements at i-1 to i
            tags[i] = tags[i-1];
        }
        // Insert new tag
        tags[insertIdx] = newTag;
        // Increment tag count
        tagCount++;
        return true;
    }

    /**
     * Binary search on the used portion of the tags array.
     *
     * @param tag the tag to find
     * @return true if found or false if not
     */
    public boolean containsTag(String tag){
        //Binary search for tag in used portion of tags array
        if (tag == null || tagCount <= 0) return false;
        //Trim spaces from tag
        String target = tag.trim();
        //If empty after trimming - return fase
        if (target.isEmpty()) return false;

        //Binary search
        int low = 0;
        int high = tagCount - 1;
        while (low <= high) {
            //Calculate mid index
            int mid = (low + high) >>> 1;
            //Get mid value
            String midVal = tags[mid];
            // Compare mid value with target
            int cmp = midVal.compareToIgnoreCase(target);
            //Check comparison result
            if (cmp == 0) return true;
            if (cmp < 0) low = mid + 1;
            else high = mid - 1;
        }

        return false;
    }

    /**
     *Returns a user friendly formatted Song
     * Includes title, artist, album, rating, genre and sorted list of tags
     * Title%%Artist%%Album%%Rating%%Genre%%tag1~~tag2~~tag3
     *
     * @return
     */
    public String format() {
        // Protect against null fields
        String t;
        if (title == null) t = "";
        else t = title;

        String a;
        if (artist == null) a = "";
        else a = artist;

        String al;
        if (album == null) al = "";
        else al = album;

        String g;
        if (genre == null) g = "";
        else g = genre;

        // Convert rating to string
        // If rating is a whole number (e.g. 5.0), output "5" instead of "5.0".
        String ratingStr;
        if (rating == (long) rating) {
            ratingStr = "" + (long) rating;   // whole number
        } else {
            ratingStr = "" + rating;          // keep decimals (e.g. 4.5)
        }

        // Build the tag section (tag1~~tag2~~...)
        // We only use tags[0]..tags[tagCount-1].
        // If tagCount is 0, we append nothing after the final "%%".
        String tagPart = "";

        if (tagCount > 0) {
            // First tag has no separator before it
            if (tags[0] != null) tagPart = tags[0];

            // Every next tag is added with the "~~" separator
            for (int i = 1; i < tagCount; i++) {
                String nextTag = "";
                if (tags[i] != null) nextTag = tags[i];

                tagPart = tagPart + "~~" + nextTag;
            }
        }

        // Combine everything with "%%" separators
        return t + "%%" + a + "%%" + al + "%%" + ratingStr + "%%" + g + "%%" + tagPart;
    }
}
