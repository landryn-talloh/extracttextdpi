package com.example.extracttextdpi;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;






public class ImagePreprocessingService {

    static {
        // Charger la bibliothèque native OpenCV


       System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public ImagePreprocessingService() {
    }

    /**
     * Prétraiter une image pour Tesseract et retourner le résultat en MultipartFile
     * @param file Image en MultipartFile à traiter
     * @return MultipartFile de l'image traitée
     */
    public MultipartFile preprocessImage(MultipartFile file) throws IOException {
        // Charger l'image en mémoire
        Mat image = Imgcodecs.imdecode(new MatOfByte(file.getBytes()), Imgcodecs.IMREAD_GRAYSCALE);

        if (image.empty()) {
            throw new IllegalArgumentException("Impossible de lire l'image.");
        }

        // Améliorer le contraste (égalisation d'histogramme)
        Mat equalized = new Mat();
        Imgproc.equalizeHist(image, equalized);

        // Réduire le bruit avec un flou gaussien
        Mat blurred = new Mat();
        Imgproc.GaussianBlur(equalized, blurred, new Size(3, 3), 0);

        // Encoder l'image traitée en mémoire
        MatOfByte buffer = new MatOfByte();
        Imgcodecs.imencode(".jpg", blurred, buffer);

        // Convertir le résultat en MultipartFile
        ByteArrayInputStream inputStream = new ByteArrayInputStream(buffer.toArray());
        return new MockMultipartFile(
                file.getName(),                 // Nom du champ original
                file.getOriginalFilename(),     // Nom original du fichier
                file.getContentType(),          // Type MIME
                inputStream                     // Contenu
        );
    }
}
