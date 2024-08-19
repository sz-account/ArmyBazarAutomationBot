package org.example;

import com.google.gson.Gson;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

public class Main {

    static final String DIRECTORY_PATH = "listings";

    public static void main(String[] args) throws IOException {

        List<Listing> listings = new LinkedList<>();

        // Get list of listings
        getListOfListings(listings);
        String[] credentials = readCredentials();

        System.out.println("Number of listings found: " + listings.size());

        // Crate session manager object and login to the site
        SessionManager sessionManager = new SessionManager();
        sessionManager.logIn(credentials[0], credentials[1]);

        sessionManager.uploadListings(listings);
    }

    public static String[] readCredentials() throws IOException {

        String[] credentials = new String[2];
        BufferedReader br = new BufferedReader(new FileReader("credentials.txt"));
        credentials[0] = br.readLine(); // Read username from the first line
        credentials[1] = br.readLine(); // Read password from the second line

        return credentials;
    }

    private static void getListOfListings(List<Listing> listings) throws IOException {

        Listing listing;
        Gson gson = new Gson();

        File[] files = searchForDirectories();

        for (File directory : files) {
            listing = readDataFromJason(directory.getName(), gson);
            listing.photos = getListOfPhotos(directory);
            listings.add(listing);
        }
    }

    private static File[] getListOfPhotos(File directory) throws IOException {

        // File filter looking for .jpg
        FilenameFilter jpgFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".jpg");
            }
        };

        return directory.listFiles(jpgFilter);
    }

    public static Listing readDataFromJason(String filePath, Gson gson) throws IOException {

        Path path = Paths.get(DIRECTORY_PATH + "/" + filePath + "/data.json");
        String json = new String(Files.readAllBytes(path));

        return gson.fromJson(json, Listing.class);
    }

    private static File[] searchForDirectories() {



        File directory = new File(DIRECTORY_PATH);

        // Check if the specified path is a directory
        if (directory.isDirectory()) {

            // Get a list of subdirectories (folders) in the specified directory
            File[] subdirectories = directory.listFiles(File::isDirectory);

            if (subdirectories != null) {
                return subdirectories;
            } else {
                System.out.println("No subdirectories found.");
            }

        } else {
            System.out.println("Invalid directory path: " + DIRECTORY_PATH);
        }

        return null;
    }

}