package com.denisfesenko;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class PerlinNoiseGenerator extends Application {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 800;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Create a canvas to draw on, and get its GraphicsContext.
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Create the application window with the canvas.
        StackPane root = new StackPane(canvas);
        Scene scene = new Scene(root, WIDTH, HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Dynamic Perlin Noise Generator");
        primaryStage.show();

        // Initialize the PerlinNoise object.
        PerlinNoise perlinNoise = new PerlinNoise();

        // Create an animation timer to update the canvas at a fixed interval.
        AnimationTimer animationTimer = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                // Update the canvas every 16 milliseconds (approximately 60 FPS).
                if (now - lastUpdate >= 16_000_000) {
                    // Generate a new Perlin noise image based on the current time.
                    double seconds = now / 1_000_000_000.0;
                    WritableImage perlinNoiseImage = generatePerlinNoiseImage(WIDTH, HEIGHT, perlinNoise, seconds);

                    // Draw the generated image on the canvas.
                    gc.drawImage(perlinNoiseImage, 0, 0);

                    // Update the last update timestamp.
                    lastUpdate = now;
                }
            }
        };

        // Start the animation timer.
        animationTimer.start();
    }

    /**
     * Generate a Perlin noise image of the specified size using the given PerlinNoise object and time value.
     *
     * @param width       The width of the image.
     * @param height      The height of the image.
     * @param perlinNoise The PerlinNoise object to use for generating noise values.
     * @param time        The time value to use for generating dynamic noise.
     * @return A WritableImage containing the generated Perlin noise.
     */
    private WritableImage generatePerlinNoiseImage(int width, int height, PerlinNoise perlinNoise, double time) {
        WritableImage image = new WritableImage(width, height);

        // Loop through each pixel of the image.
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // Calculate the noise value at the current pixel and time.
                // Adjust the scaling factors (80.0 and 2.0) to control the appearance of the noise.
                double noiseValue = perlinNoise.noise(x / 80.0 + time, y / 80.0 + time, time * 2.0);

                // Normalize the noise value to the range [0, 1] and clamp it to avoid values outside the range.
                double grayValue = Math.min(Math.max((noiseValue + 1) / 2, 0), 1);

                // Convert the normalized noise value to a grayscale color.
                Color color = new Color(grayValue, grayValue, grayValue, 1);
                // Set the pixel color in the image.
                image.getPixelWriter().setColor(x, y, color);
            }
        }

        // Return the generated Perlin noise image.
        return image;
    }
}