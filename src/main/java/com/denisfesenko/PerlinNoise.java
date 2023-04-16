package com.denisfesenko;

import java.util.Random;

public class PerlinNoise {

    // The permutation array, used to generate pseudo-random gradients.
    private static final int[] p = new int[512];

    // Initialize the permutation array with a shuffled sequence of integers from 0 to 255.
    static {
        int[] permutation = new int[256];
        Random random = new Random();
        for (int i = 0; i < 256; i++) {
            permutation[i] = i;
        }
        for (int i = 0; i < 256; i++) {
            int j = random.nextInt(256 - i) + i;
            int temp = permutation[i];
            permutation[i] = permutation[j];
            permutation[j] = temp;
        }
        // Copy the shuffled permutation array twice into the p array.
        System.arraycopy(permutation, 0, p, 0, 256);
        System.arraycopy(permutation, 0, p, 256, 256);
    }

    /**
     * Calculate the Perlin noise value at the given (x, y, t) coordinates.
     *
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     * @param t The time or z-coordinate.
     * @return The Perlin noise value at the given coordinates.
     */
    public double noise(double x, double y, double t) {
        // Calculate the integer coordinates of the unit cube containing the input point.
        int X = (int) Math.floor(x) & 255;
        int Y = (int) Math.floor(y) & 255;
        int Z = (int) Math.floor(t) & 255;

        // Calculate the relative position of the input point within the unit cube.
        x -= Math.floor(x);
        y -= Math.floor(y);
        t -= Math.floor(t);

        // Compute the fade curves for each coordinate.
        double u = fade(x);
        double v = fade(y);
        double w = fade(t);

        // Calculate the 3D hash values for each corner of the unit cube.
        int A = p[X] + Y;
        int AA = p[A] + Z;
        int AB = p[A + 1] + Z;
        int B = p[X + 1] + Y;
        int BA = p[B] + Z;
        int BB = p[B + 1] + Z;

        // Interpolate the gradient values from each corner and return the final Perlin noise value.
        return lerp(w, lerp(v, lerp(u, grad(p[AA], x, y, t),
                                grad(p[BA], x - 1, y, t)),
                        lerp(u, grad(p[AB], x, y - 1, t),
                                grad(p[BB], x - 1, y - 1, t))),
                lerp(v, lerp(u, grad(p[AA + 1], x, y, t - 1),
                                grad(p[BA + 1], x - 1, y, t - 1)),
                        lerp(u, grad(p[AB + 1], x, y - 1, t - 1),
                                grad(p[BB + 1], x - 1, y - 1, t - 1))));
    }

    /**
     * Compute the fade curve value for the input t.
     *
     * @param t The input value.
     * @return The fade curve value
     * for the input t.
     */
    private static double fade(double t) {
        // 6t^5 - 15t^4 + 10t^3 is the quintic (5th degree) polynomial used for the fade curve.
        return t * t * t * (t * (t * 6 - 15) + 10);
    }

    /**
     * Perform linear interpolation between a and b using the input t.
     *
     * @param t The interpolation factor.
     * @param a The first value.
     * @param b The second value.
     * @return The interpolated value between a and b.
     */
    private static double lerp(double t, double a, double b) {
        // Compute the interpolated value: a + t * (b - a).
        return a + t * (b - a);
    }

    /**
     * Compute the dot product of the gradient vector and the relative position vector.
     *
     * @param hash The hash value used to select the gradient vector.
     * @param x    The x-coordinate of the relative position vector.
     * @param y    The y-coordinate of the relative position vector.
     * @param t    The time or z-coordinate of the relative position vector.
     * @return The dot product of the gradient vector and the relative position vector.
     */
    private static double grad(int hash, double x, double y, double t) {
        // The hash value determines which gradient vector to use.
        int h = hash & 15;

        // Select the x and y components of the gradient vector based on the hash value.
        double u = h < 8 ? x : y;
        double v = h < 4 ? y : h == 12 || h == 14 ? x : t;

        // Compute the dot product of the gradient vector and the relative position vector.
        return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
    }
}