package cs1302.gallery;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.net.http.HttpResponse.BodyHandlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Console version of the Gallery App to demonstrate functionality.
 */
public class GalleryAppConsole {

    /** HTTP client. */
    public static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_2)
        .followRedirects(HttpClient.Redirect.NORMAL)
        .build();

    /** Google {@code Gson} object for parsing JSON-formatted strings. */
    public static Gson GSON = new GsonBuilder()
        .setPrettyPrinting()
        .create();

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("🎨 Gallery App - Console Version");
        System.out.println("=================================");
        System.out.println("This is a console demonstration of the Gallery App functionality.");
        System.out.println("The full GUI version requires JavaFX which has compatibility issues on macOS.");
        System.out.println();

        while (true) {
            System.out.println("\nOptions:");
            System.out.println("1. Search for media");
            System.out.println("2. View available media types");
            System.out.println("3. Exit");
            System.out.print("Enter your choice (1-3): ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    searchMedia();
                    break;
                case "2":
                    showMediaTypes();
                    break;
                case "3":
                    System.out.println("Goodbye! 👋");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void searchMedia() {
        System.out.println("\n--- Media Search ---");
        
        System.out.print("Enter search term (e.g., 'Taylor Swift', 'Avengers'): ");
        String searchTerm = scanner.nextLine().trim();
        
        if (searchTerm.isEmpty()) {
            System.out.println("Search term cannot be empty.");
            return;
        }

        System.out.println("\nAvailable media types:");
        showMediaTypes();
        
        System.out.print("Enter media type (or 'all' for all types): ");
        String mediaType = scanner.nextLine().trim().toLowerCase();

        System.out.println("\n🔍 Searching for: " + searchTerm + " in " + mediaType + "...");
        
        try {
            if (mediaType.equals("all")) {
                searchAllMediaTypes(searchTerm);
            } else {
                searchSingleMediaType(searchTerm, mediaType);
            }
        } catch (Exception e) {
            System.out.println("❌ Error during search: " + e.getMessage());
        }
    }

    private static void searchSingleMediaType(String searchTerm, String mediaType) throws IOException, InterruptedException {
        String url = "https://itunes.apple.com/search?term=" +
            URLEncoder.encode(searchTerm, StandardCharsets.UTF_8) +
            "&limit=10&media=" + mediaType;
        
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .build();

        HttpResponse<String> response = HTTP_CLIENT.send(request, BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("HTTP Error: " + response.statusCode());
        }

        String responseBody = response.body();
        List<String> imageUrls = parseSearchResults(responseBody);

        System.out.println("\n✅ Search completed!");
        System.out.println("📊 Results found: " + imageUrls.size() + " items");
        System.out.println("🔗 Query URL: " + url);
        
        if (!imageUrls.isEmpty()) {
            System.out.println("\n📸 Image URLs found:");
            for (int i = 0; i < Math.min(imageUrls.size(), 5); i++) {
                System.out.println((i + 1) + ". " + imageUrls.get(i));
            }
            if (imageUrls.size() > 5) {
                System.out.println("... and " + (imageUrls.size() - 5) + " more");
            }
        } else {
            System.out.println("❌ No results found for this search.");
        }
    }

    private static void searchAllMediaTypes(String searchTerm) throws IOException, InterruptedException {
        String[] mediaTypes = {"music", "movie", "podcast", "musicVideo", "audiobook", 
                              "shortFilm", "tvShow", "software", "ebook"};
        
        System.out.println("\n🔍 Searching across all media types...");
        
        for (String mediaType : mediaTypes) {
            try {
                System.out.print("Searching " + mediaType + "... ");
                searchSingleMediaType(searchTerm, mediaType);
                System.out.println("✅ Found results in " + mediaType);
            } catch (Exception e) {
                System.out.println("❌ Error in " + mediaType + ": " + e.getMessage());
            }
        }
    }

    private static void showMediaTypes() {
        System.out.println("\n📋 Available Media Types:");
        System.out.println("• music - Music tracks and albums");
        System.out.println("• movie - Movies and films");
        System.out.println("• podcast - Podcast episodes");
        System.out.println("• musicVideo - Music videos");
        System.out.println("• audiobook - Audiobooks");
        System.out.println("• shortFilm - Short films");
        System.out.println("• tvShow - TV shows");
        System.out.println("• software - Software applications");
        System.out.println("• ebook - E-books");
        System.out.println("• all - Search all media types");
    }

    private static List<String> parseSearchResults(String responseBody) {
        List<String> imageURLs = new ArrayList<>();
        String[] parts = responseBody.split("\"artworkUrl100\":\"");
        for (int i = 1; i < parts.length; i++) {
            String[] urlPart = parts[i].split("\"", 2);
            imageURLs.add(urlPart[0]);
        }
        return imageURLs;
    }
}
