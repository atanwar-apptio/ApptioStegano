package org.example;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RestController
@RequestMapping("/api/steganography")
public class SteganographyController {

    @PostMapping(value = "/embed", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> embedText(@RequestParam("image") MultipartFile imageFile) {
        try {
            BufferedImage originalImage = ImageIO.read(imageFile.getInputStream());
            String filename = imageFile.getOriginalFilename();

            BufferedImage processedImage = SteganographyUtil.embedText(originalImage, filename);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(processedImage, "png", baos);
            byte[] imageBytes = baos.toByteArray();

            return ResponseEntity
                    .ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(imageBytes);

        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Error processing image: " + e.getMessage());
        }
    }

    @PostMapping(value = "/extract", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> extractText(@RequestParam("image") MultipartFile imageFile) {
        try {
            BufferedImage image = ImageIO.read(imageFile.getInputStream());
            String extractedText = SteganographyUtil.extractText(image);

            return ResponseEntity.ok(extractedText);

        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Error processing image: " + e.getMessage());
        }
    }
}
