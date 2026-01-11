package app;

import business.Song;
import utils.Playlist;
import utils.SongFileUtils;

import java.util.Scanner;

//PlaylistApp is a console menu application for interacting with a Playlist.
public class PlaylistApp {

    /**
     * Entry point of the program.
     *
     * @param args not used
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Load initial playlist using the lecturer-provided file reader
        Playlist playlist = loadInitialPlaylist(scanner);

        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    displayAllSongs(playlist);
                    break;
                case "2":
                    addNewSong(scanner, playlist);
                    break;
                case "3":
                    displayAllTags(playlist);
                    break;
                case "4":
                    sortSongsByTagCount(playlist);
                    break;
                case "5":
                    searchSongsByTag(scanner, playlist);
                    break;
                case "6":
                    addTagToExistingSong(scanner, playlist);
                    break;
                case "7":
                    displayMostPopularSong(playlist);
                    break;
                case "8":
                    mergeSongsFromFile(scanner, playlist);
                    break;
                case "9":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Please choose 1-9.");
            }
        }

        scanner.close();
        System.out.println("Goodbye!");
    }

    /**
     * Loads an initial playlist from a filename entered by the user.
     * If the user enters a blank line - use "sampleSongInput.txt".
     *
     * @param scanner scanner to read user input
     * @return Playlist populated from the file
     */
    private static Playlist loadInitialPlaylist(Scanner scanner) {
        System.out.print("Enter initial songs filename (blank for sampleSongInput.txt): ");
        String file = scanner.nextLine().trim();
        if (file.isEmpty()) {
            file = "sampleSongInput.txt";
        }

        //If reading failed, use an empty list of songs
        Song[] songs = SongFileUtils.readSongFile(file);
        if (songs == null) {
            songs = new Song[0];
        }

        //create the playlist using loaded songs
        return new Playlist(songs);
    }

    /**
     * Prints the menu options to the console.
     */
    private static void printMenu() {
        System.out.println();
        System.out.println("=== Playlist Menu ===");
        System.out.println("1) Display all songs");
        System.out.println("2) Add a new song (manual input)");
        System.out.println("3) Display all unique tags");
        System.out.println("4) Sort songs by number of tags (descending)");
        System.out.println("5) Search songs by tag");
        System.out.println("6) Add a tag to a song");
        System.out.println("7) Find the most popular song");
        System.out.println("8) Add new songs from file (merge)");
        System.out.println("9) Exit");
        System.out.print("Choose an option: ");
    }

    /**
     * Displays all songs in playlist.
     *
     * @param playlist playlist to display
     */
    private static void displayAllSongs(Playlist playlist) {
        // copy of all songs
        Song[] songs = playlist.getAllSongs();
        if (songs.length == 0) {
            System.out.println("No songs in playlist.");
            return;
        }

        //print each song
        for (int i = 0; i < songs.length; i++) {
            Song s = songs[i];
            System.out.println("Song #" + (i + 1));
            if (s == null) {
                System.out.println("(null)");
            } else {
                System.out.println(s.format());
            }
            System.out.println("---------------------------");
        }
    }

    /**
     * Adds a new song using user input.
     *
     * @param scanner scanner for input
     * @param playlist playlist to add into
     */
    private static void addNewSong(Scanner scanner, Playlist playlist) {

        // ask user for all Song fields and create Song object
        Song song = promptForSong(scanner);
        if (song == null) {
            System.out.println("Song creation cancelled.");
            return;
        }

        // adding to playlist
        boolean added = playlist.addSong(song);
        if (added) {
            System.out.println("Song added.");
        } else {
            System.out.println("Song NOT added (duplicate or invalid).");
        }
    }

    /**
     * Displays all unique tags across all songs in the playlist.
     *
     * @param playlist playlist to inspect
     */
    private static void displayAllTags(Playlist playlist) {

        //Playlist returns an array of unique tags
        String[] tags = playlist.getAllTags();

        if (tags.length == 0) {
            System.out.println("No tags found.");
            return;
        }

        System.out.println("Unique tags:");
        for (int i = 0; i < tags.length; i++) {
            System.out.println("- " + tags[i]);
        }
    }

    /**
     * Sorts a playlist songs by number of tags (descending).
     *
     * @param playlist playlist to sort
     */
    private static void sortSongsByTagCount(Playlist playlist) {

        //Sorting is done inside Playlist (merge sort requirement)
        playlist.sortSongsByNumTags();

        System.out.println("Songs sorted by tag count (descending).");
    }

    /**
     * Searches songs by tag and prints matches.
     *
     * @param scanner scanner for input
     * @param playlist playlist to search
     */
    private static void searchSongsByTag(Scanner scanner, Playlist playlist) {
        System.out.print("Enter tag to search: ");

        //Read a tag from user
        String tag = scanner.nextLine().trim();

        //Get all songs that contain this tag
        Song[] matches = playlist.searchByTag(tag);
        if (matches.length == 0) {
            System.out.println("No songs found with that tag.");
            return;
        }

        // Print matching songs
        System.out.println("Matches:");
        for (int i = 0; i < matches.length; i++) {
            Song s = matches[i];
            System.out.println("Match #" + (i + 1));
            if (s != null) {
                System.out.println(s.format());
            }
            System.out.println("---------------------------");
        }
    }

    /**
     * Lets user select a song and add a new tag to it.
     *
     * @param scanner scanner for input
     * @param playlist playlist containing the songs
     */
    private static void addTagToExistingSong(Scanner scanner, Playlist playlist) {

        // Get songs so we can show user a numbered list
        Song[] songs = playlist.getAllSongs();
        if (songs.length == 0) {
            System.out.println("No songs available.");
            return;
        }

        // Print titles so user can choose
        for (int i = 0; i < songs.length; i++) {
            Song s = songs[i];
            String title = (s == null) ? "(null)" : s.getTitle();
            System.out.println((i + 1) + ") " + title);
        }

        System.out.print("Select song number: ");

        // Read the number safely
        int selection = readInt(scanner);

        // Check range
        if (selection < 1 || selection > songs.length) {
            System.out.println("Invalid selection.");
            return;
        }

        // Convert from "1-based number" to "0-based index"
        Song target = songs[selection - 1];
        if (target == null) {
            System.out.println("Selected song is null.");
            return;
        }

        System.out.print("Enter tag to add: ");
        String tag = scanner.nextLine().trim();

        // Song.addTag handles sorting and duplicates
        boolean ok = target.addTag(tag);

        if (ok) {
            System.out.println("Tag added.");
        } else {
            System.out.println("Tag NOT added (duplicate/invalid/no space).");
        }
    }

    /**
     * Displays the song with the highest rating.
     *
     * @param playlist playlist to inspect
     */
    private static void displayMostPopularSong(Playlist playlist) {

        //Ask playlist for best song (highest rating)
        Song best = playlist.findMostPopular();
        if (best == null) {
            System.out.println("No songs available.");
            return;
        }

        System.out.println("Most popular song:");
        System.out.println(best.format());
    }

    /**
     * Loads songs from a file and merges them into the current playlist.
     *
     * @param scanner scanner for input
     * @param playlist playlist to modify
     */
    private static void mergeSongsFromFile(Scanner scanner, Playlist playlist) {

        System.out.print("Enter filename to load songs from: ");
        // Read file name
        String file = scanner.nextLine().trim();
        if (file.isEmpty()) {
            System.out.println("No filename provided.");
            return;
        }

        // Read songs from file
        Song[] newSongs = SongFileUtils.readSongFile(file);
        if (newSongs == null || newSongs.length == 0) {
            System.out.println("No songs loaded from file.");
            return;
        }

        // Show the loaded songs (useful for checking what was read)
        System.out.println("Loaded songs:");
        for (int i = 0; i < newSongs.length; i++) {
            Song s = newSongs[i];
            if (s != null) {
                System.out.println(s.format());
                System.out.println("---------------------------");
            }
        }

        // Add them to the existing playlist
        playlist.addSongs(newSongs);
        System.out.println("Songs merged into playlist.");
    }

    /**
     * Prompts the user for song details and creates a Song object.
     * If title is blank, user cancels and we return null.
     *
     * @param scanner scanner for input
     * @return a Song, or null if user cancels
     */
    private static Song promptForSong(Scanner scanner) {
        System.out.print("Enter title (blank to cancel): ");
        String title = scanner.nextLine().trim();

        // Blank title means cancel
        if (title.isEmpty()) {
            return null;
        }

        System.out.print("Enter artist: ");
        String artist = scanner.nextLine().trim();

        System.out.print("Enter album: ");
        String album = scanner.nextLine().trim();

        // Read rating (keeps asking until user enters a number)
        double rating = readDouble(scanner);

        System.out.print("Enter genre: ");
        String genre = scanner.nextLine().trim();

        //Temporary array for tags (we may need to grow it)
        String[] buffer = new String[4];
        int count = 0; // number of tags stored in buffer

        while (true) {
            System.out.print("Enter tag (blank to finish): ");
            String tag = scanner.nextLine().trim();
            // Blank tag means stop entering tags
            if (tag.isEmpty()) {
                break;
            }

            // Ignore duplicates typed by the user
            if (containsIgnoreCase(buffer, count, tag)) {
                System.out.println("Duplicate tag (ignored).");
                continue;
            }

            // Ignore duplicates typed by the user
            if (count == buffer.length) {
                buffer = growStringArray(buffer);
            }
            // Store this tag
            buffer[count] = tag;
            count++;
        }

        // Create an exact-sized tags array
        String[] tags = new String[count];
        for (int i = 0; i < count; i++) {
            tags[i] = buffer[i];
        }

        // Create and return Song.
        // Song constructor will store tags internally and sort them using addTag().
        return new Song(title, artist, album, rating, genre, tags);
    }

    /**
     * Reads an integer safely from input. Re-prompts until valid.
     *
     * @param scanner scanner for input
     * @return parsed integer
     */
    private static int readInt(Scanner scanner) {
        while (true) {

            // Read full line (easier than scanner.nextInt() because of newline issues)
            String line = scanner.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.print("Invalid integer. Try again: ");
            }
        }
    }

    /**
     * Reads a double safely from input. Re-prompts until valid.
     *
     * @param scanner scanner for input
     * @return parsed double
     */
    private static double readDouble(Scanner scanner) {
        while (true) {
            System.out.print("Enter rating (number): ");
            String line = scanner.nextLine().trim();
            try {
                return Double.parseDouble(line);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Try again.");
            }
        }
    }

    /**
     * Doubles the size of a String array, copying elements manually.
     *
     * @param original original array
     * @return larger array
     */
    private static String[] growStringArray(String[] original) {

        // New length is double the current length
        int newLen = original.length * 2;

        // If original length was 0, ensure at least size 1
        if (newLen == 0) {
            newLen = 1;
        }

        // Create new array
        String[] bigger = new String[newLen];
        //Manual copy
        for (int i = 0; i < original.length; i++) {
            bigger[i] = original[i];
        }
        return bigger;
    }

    /**
     * Checks if a value exists in first 'used' entries of an array
     *
     * @param data array to check
     * @param used number of used slots
     * @param value search value
     * @return true if found
     */
    private static boolean containsIgnoreCase(String[] data, int used, String value) {
        // Loop only through the part of the array that is actually in use
        for (int i = 0; i < used; i++) {
            if (data[i] != null && data[i].equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }
}
