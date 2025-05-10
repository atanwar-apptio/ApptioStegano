package org.example;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

public class SteganographyUtil {

    public static BufferedImage embedText(BufferedImage image, String text) {
        int[] pixels = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());

        // Convert text to binary
        StringBuilder binaryText = new StringBuilder();
        for (char c : text.toCharArray()) {
            String binary = String.format("%8s", Integer.toBinaryString(c)).replace(' ', '0');
            binaryText.append(binary);
        }

        // Add terminator
        binaryText.append("00000000");

        // Embed binary text into image
        int textIndex = 0;
        for (int i = 0; i < pixels.length && textIndex < binaryText.length(); i++) {
            int pixel = pixels[i];
            int red = (pixel >> 16) & 0xff;
            int green = (pixel >> 8) & 0xff;
            int blue = pixel & 0xff;

            // Modify least significant bit of blue channel
            if (textIndex < binaryText.length()) {
                blue = (blue & 0xFE) | (binaryText.charAt(textIndex) == '1' ? 1 : 0);
                textIndex++;
            }

            pixels[i] = (pixel & 0xFFFFFF00) | blue;
        }

        BufferedImage outputImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        outputImage.setRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
        return outputImage;
    }

    public static String extractText(BufferedImage image) {
        StringBuilder binaryText = new StringBuilder();
        int[] pixels = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());

        // Extract binary text from image
        for (int pixel : pixels) {
            int blue = pixel & 0xff;
            binaryText.append(blue & 1);
        }

        // Convert binary to text
        StringBuilder text = new StringBuilder();
        String binaryString = binaryText.toString();
        for (int i = 0; i < binaryString.length() - 7; i += 8) {
            String chunk = binaryString.substring(i, i + 8);
            int charCode = Integer.parseInt(chunk, 2);
            if (charCode == 0) break; // terminator found
            text.append((char) charCode);
        }

        return text.toString();
    }
}

