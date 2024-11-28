package com.example.extracttextdpi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@RestController
@RequestMapping("/api/ocr")
public class OCRController {

    private final ImagePreprocessingService preprocessingService;

    private final TesseractService tesseractService;


    public OCRController() {

        this.preprocessingService = new ImagePreprocessingService();
        this.tesseractService = new TesseractService();
    }

    @PostMapping("/extract-text")
    public ResponseEntity<TextExtract> extractTextFromImage(@RequestParam("file") MultipartFile multipartFile) {
        try {
            // traitement sur l image
            MultipartFile processedFile = preprocessingService.preprocessImage(multipartFile);
            // Convertir MultipartFile en File
            File tempFile = convertMultipartFileToFile(processedFile);

            // Extraire la résolution DPI
            int dpi = ImageDpiExtractor.extractDpi(tempFile);
            System.out.println("DPI extrait : " + dpi);

            // Effectuer l'OCR avec Tesseract
            String extractedText = tesseractService.performOCR(tempFile, dpi);
            var text = new TextExtract(extractedText);

            // Supprimer le fichier temporaire
            tempFile.delete();

            return ResponseEntity.ok(text);

       } catch (Exception e) {
            e.printStackTrace();
           //return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private File convertMultipartFileToFile(MultipartFile file) throws IOException {
        File tempFile = File.createTempFile("temp", file.getOriginalFilename());
        tempFile.deleteOnExit();

        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(file.getBytes());
        }

        return tempFile;
    }
}