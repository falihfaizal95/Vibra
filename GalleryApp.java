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
import java.util.Collections;
import java.net.http.HttpResponse.BodyHandlers;
import java.io.InputStream;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import java.util.Random;

import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.concurrent.Task;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.layout.HBox;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.RowConstraints;
import javafx.scene.Node;
import javafx.scene.control.Alert;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
/**
 * Represents an iTunes Gallery App.
 */

public class GalleryApp extends Application {


    /** HTTP client. */
    public static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_2)           // uses HTTP protocol version 2 where possible
        .followRedirects(HttpClient.Redirect.NORMAL)  // always redirects, except from HTTPS to HTTP
        .build();                                     // builds and returns a HttpClient object

    /** Google {@code Gson} object for parsing JSON-formatted strings. */
    public static Gson GSON = new GsonBuilder()
        .setPrettyPrinting()                          // enable nice output when printing
        .create();                                    // builds and returns a Gson object

    private Stage stage;
    private Scene scene;
    private VBox root;

    private Button playPauseButton;
    private Button getImagesButton;
    private TextField searchTermField;
    private ComboBox<String> mediaTypeComboBox;
    private Label messageLabel;
    private ProgressBar progressBar;
    private Label statusBarLabel;
    private VBox mainContentPlaceholder;

    private Timeline playTimeline;
    private Random random = new Random();
    private List<Image> images;
    /**
     * Constructs a {@code GalleryApp} object}.
     */

    public GalleryApp() {
        this.stage = null;
        this.scene = null;
        this.root = new VBox();
    } // GalleryApp

    private boolean isPlaying = false;

    /** {@inheritDoc} */
    @Override
    public void init() {
        // feel free to modify this method
        System.out.println("init() called");
    } // init

    /** {@inheritDoc} */
    @Override
    public void start(Stage stage) {
        this.stage = stage;
        this.scene = new Scene(this.root);
        this.stage.setOnCloseRequest(event -> Platform.exit());
        this.stage.setTitle("GalleryApp!");
        this.stage.setScene(this.scene);
        this.stage.sizeToScene();
        playPauseButton = new Button("Play");
        getImagesButton = new Button("Get Images");
        playPauseButton.setOnAction(event -> togglePlayPause());
        getImagesButton.setOnAction(event -> {
            try {
                String searchTerm = searchTermField.getText().trim();
                String mediaType = mediaTypeComboBox.getValue().toLowerCase();
                if (mediaType.equals("all")) {
                    for (String type : mediaTypeComboBox.getItems()) {
                        if (!type.equals("all")) {
                            searchImagesForMediaType(searchTerm, type.toLowerCase());
                        }
                    }
                } else {
                    searchImagesForMediaType(searchTerm, mediaType);
                }
            } catch (Exception e) {
                e.printStackTrace();
                messageLabel.setText("Last attempt to get images failed...");
                Alert alert = new Alert(Alert.AlertType.ERROR, "URI: " + searchTermField.getText() +
                                        "\nException: " + e.toString(), ButtonType.OK);
                alert.showAndWait();
            }
        });
        Label searchLabel = new Label("Search:");
        searchTermField = new TextField("jack johnson");
        searchTermField.setPromptText("Enter search term");
        mediaTypeComboBox = new ComboBox<>();
        mediaTypeComboBox.getItems().addAll("movie", "music", "podcast", "musicVideo",
                                            "audiobook", "shortFilm", "tvShow", "software",
                                            "ebook", "all");
        mediaTypeComboBox.setValue("music");
        HBox searchBar = new HBox();
        searchBar.setSpacing(7);
        searchBar.getChildren().addAll(playPauseButton, searchLabel, searchTermField,
                                       mediaTypeComboBox, getImagesButton);
        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(300);
        statusBarLabel = new Label("Images Provided by iTunes Search API");
        HBox statusBar = new HBox();
        HBox.setHgrow(progressBar, Priority.ALWAYS);
        statusBar.getChildren().addAll(progressBar, statusBarLabel);
        mainContentPlaceholder  = new VBox();
        mainContentPlaceholder.setMinSize(500, 500);
        mainContentPlaceholder.setStyle("-fx-border-color: black");
        VBox buttonsAndMessage = new VBox();
        messageLabel = new Label("Type in a term, select a media type, then click the button.");
        VBox messageBox = new VBox();
        messageBox.getChildren().add(messageLabel);
        root.getChildren().addAll(searchBar, messageBox, mainContentPlaceholder, statusBar);
        Platform.runLater(() -> this.stage.setResizable(false));
        this.stage.show();
    } // start

    /**this method just extends the width of the search bar.
     *
     */
    private void searchTerm() {
        searchTermField.setPrefWidth(200);
    }

    /** {@inheritDoc} */
    @Override
    public void stop() {
        // feel free to modify this method
        System.out.println("stop() called");
    } // stop

    /**  This method toggles the play pause button when it is clicked.
     *
     *
     */
    private void togglePlayPause() {
        isPlaying = !isPlaying;
        playPauseButton.setText(isPlaying ? "Pause" : "Play");

        if (isPlaying) {
            playPauseButton.setPrefWidth(60);
        } else {
            playPauseButton.setPrefWidth(Button.USE_COMPUTED_SIZE);
        }

        if (!isPlaying) {
            startPlayMode();
        } else {
            stopPlayMode();
        }
    } //togglePlayPause

    /**this method changes the maincontent box when it is cllicked.
     *and randomizes the image every 2 seconds
     *
     */
    private void startPlayMode() {
        playTimeline = new Timeline(new KeyFrame(Duration.seconds(2),
                                                 event -> replaceRandomImage()));
        playTimeline.setCycleCount(Timeline.INDEFINITE);
        playTimeline.play();
    }

    /**this method will stop the app if there is no value in the play timeline.
     *
     *
     */
    private void stopPlayMode() {
        if (playTimeline != null) {
            playTimeline.stop();
        }
    }

    /**this method randomizes the image every 20 seconds.
     *out of the every 20 images and finds a unique image.
     *
     */
    private void replaceRandomImage() {
        if (images == null || images.isEmpty()) {
            return;
        }

        int totalImages = images.size();
        int maxImages = 20;
        int imagesToDisplay = Math.min(totalImages, maxImages);

        List<Integer> displayedIndexes = new ArrayList<>();
        for (Node node : mainContentPlaceholder.getChildren()) {
            if (node instanceof GridPane) {
                GridPane gridPane = (GridPane) node;
                for (Node childNode : gridPane.getChildren()) {
                    if (childNode instanceof ImageView) {
                        ImageView imageView = (ImageView) childNode;
                        Image currentImage = imageView.getImage();
                        int currentIndex = images.indexOf(currentImage);
                        if (currentIndex != -1) {
                            displayedIndexes.add(currentIndex);
                        }
                    }
                }
            }
        }

        int randomIndex;
        do {
            randomIndex = random.nextInt(totalImages);
        } while (displayedIndexes.contains(randomIndex));

        Image replacementImage = images.get(randomIndex);

        for (Node node : mainContentPlaceholder.getChildren()) {
            if (node instanceof GridPane) {
                GridPane gridPane = (GridPane) node;
                for (Node childNode : gridPane.getChildren()) {
                    if (childNode instanceof ImageView) {
                        ImageView imageViewToReplace = (ImageView) childNode;
                        Image currentImage = imageViewToReplace.getImage();
                        int currentIndex = images.indexOf(currentImage);
                        if (currentIndex != -1) {
                            if (currentIndex == randomIndex) {
                                imageViewToReplace.setImage(replacementImage);
                                displayedIndexes.add(randomIndex);
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    public static void main(String [] args) {
        launch(args);
    } //main

    /**This method searches the UI for images.
     *
     *
     */
    public void searchImages() throws IOException, InterruptedException {
        playPauseButton.setDisable(true);
        getImagesButton.setDisable(true);
        String searchTerm = searchTermField.getText().replaceAll(" ", "+");
        String mediaType = mediaTypeComboBox.getValue().replaceAll(" ", "+");
        if (searchTerm.isEmpty()) {
            messageLabel.setText("please enter a search term");
            playPauseButton.setDisable(false);
            getImagesButton.setDisable(false);
            return;
        }
        progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
        messageLabel.setText("Getting Images...");
        String url = "https://itunes.apple.com/search?term=" + searchTerm +
            "&limit=200&media=" + mediaType;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();
        Task<List<String>> task = new Task<>() {
                @Override
                protected List<String> call() throws Exception {
                    try {
                        HttpResponse<String> response =
                            HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
                        String responseBody = response.body();
                        List<String> imageUrls = parseSearchResults(responseBody);
                        isPlaying = false;
                        return imageUrls;
                    } catch (Exception e) {
                        handleDownloadError(e);
                        return null;
                    }
                }

                @Override
                protected void succeeded() {
                    List<String> imageUrls = getValue();
                    if (imageUrls != null) {
                        Platform.runLater(() -> {
                            playPauseButton.setDisable(false);
                            getImagesButton.setDisable(false);
                            messageLabel.setText("Query URI: " + url);
                            if (imageUrls.isEmpty()) {
                                handleDownloadError(new IllegalArgumentException("No results"));
                            } else {
                                images = downloadImages(imageUrls);
                                if (images.isEmpty()) {
                                    handleDownloadError(new IllegalArgumentException( "Failed"));
                                } else {
                                    displayImages(images, url);
                                }
                            }
                        });
                    }
                }
            };
        new Thread(task).start();
    }

    /**This method splits the url part when it receives it from the API.
     *it also starts it at 0.
     *
     *@return returns the imageUrls
     *@param responseBody returns the parameter of parseSearch
     */
    private List <String> parseSearchResults(String responseBody) {
        List<String> imageURLs = new ArrayList<>();
        String[] parts = responseBody.split("\"artworkUrl100\":\"");
        for (int i = 1; i < parts.length; i++) {
            String[] urlPart = parts[i].split("\"", 2);
            imageURLs.add(urlPart[0]);
        }
        return imageURLs;
    }

    /**This methods downloads images from the API URL link.
     *for itunes and also provides error boxes.
     *
     *@param imageUrls is the parameter for the links
     *@return imageUrls from the API.
     */
    private List<Image> downloadImages(List<String> imageUrls) {
        List<Image> images = new ArrayList<>();
        List<String> downloadedUrls = new ArrayList<>();

        progressBar.setProgress(0);
        progressBar.setProgress(1);

        for (int i = 0; i < imageUrls.size(); i++) {
            try {
                String imageUrl = imageUrls.get(i);
                if (!downloadedUrls.contains(imageUrl)) {
                    Image image = new Image(imageUrl);
                    images.add(image);
                    downloadedUrls.add(imageUrl);
                }
            } catch (Exception e) {
                System.err.println("Failed to load image: " + imageUrls.get(i));
                e.printStackTrace();
            }
            final double progress = (i + 1) / (double) imageUrls.size();
            Platform.runLater(() -> progressBar.setProgress(progress));
        }
        return images;
    }

    /**This method displays the images on the main content when the user searches.
 * It will return errors if there's any sort of null input.
 *
 * @param images   List of images to display
 * @param queryUrl Query URL for display
 */
    private void displayImages(List<Image> images, String queryUrl) {
    // Clear existing content
        mainContentPlaceholder.getChildren().clear();

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(0));
        gridPane.setHgap(0);
        gridPane.setVgap(0);

        for (int i = 0; i < 5; i++) {
            ColumnConstraints colConst = new ColumnConstraints();
            colConst.setPercentWidth(100.0 / 5);
            gridPane.getColumnConstraints().add(colConst);
        }

        for (int i = 0; i < 4; i++) {
            RowConstraints rowConst = new RowConstraints();
            rowConst.setPercentHeight(100.0 / 4);
            gridPane.getRowConstraints().add(rowConst);
        }

        int totalImages = images.size();
        int maxImages = 20; // Maximum number of images to display
        int imagesToDisplay = Math.min(totalImages, maxImages);

        for (int i = 0; i < imagesToDisplay; i++) {
            ImageView imageView = new ImageView(images.get(i));
            imageView.setFitWidth(100);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);
            imageView.setCache(true);

            gridPane.getRowConstraints().get(i / 5)
                .setPrefHeight(mainContentPlaceholder.getHeight() / 4);
            gridPane .getColumnConstraints()
                .get(i % 5).setPrefWidth(mainContentPlaceholder.getWidth() / 5);
            gridPane.add(imageView, i % 5, i / 5);
        }

        mainContentPlaceholder.getChildren().add(gridPane);
        messageLabel.setText(queryUrl);
    }

    /** this method handles any download error.
     *
     *@param exception
     */
    private void handleDownloadError(Exception exception) {
        playPauseButton.setDisable(false);
        getImagesButton.setDisable(false);
        progressBar.setProgress(1);
        messageLabel.setText("Last attempt to get images failed..");

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("error");
        alert.setHeaderText(null);
        alert.setContentText("URI: " + searchTermField.getText() +
                             "\nException: " + exception.toString());
        alert.showAndWait();
    }

    /**this method searches for images based on the media type of the desired input.
     *
     *
     *@param searchTerm it searches the term for which dropdown element.
     *@param mediaType it goes and checks for the desired media type.
     */
    public void searchImagesForMediaType(String searchTerm, String mediaType) {
        try {
            String url = "https://itunes.apple.com/search?term=" +
                URLEncoder.encode(searchTerm, StandardCharsets.UTF_8) +
                "&limit=200&media=" + mediaType;
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

            HttpResponse<String> response = HTTP_CLIENT.send(request, BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new IOException(response.toString());
            }

            String responseBody = response.body();
            List<String> imageUrls = parseSearchResults(responseBody);

            Platform.runLater(() -> {
                try {
                    images = downloadImages(imageUrls);
                    displayImages(images, url);
                } catch (Exception e) {
                    handleDownloadError(e);
                }
            });
        } catch (Exception e) {
            handleDownloadError(e);
        }
    }

} // GalleryApp
