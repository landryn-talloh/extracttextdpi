package com.example.extracttextdpi;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;





@Service
public class ImagePreprocessingService {

    static {
        // Charger la bibliothèque native OpenCV


        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public ImagePreprocessingService() {
    }

    /**
     * Prétraiter une image pour Tesseract et retourner le résultat en MultipartFile
     *
     * @param file Image en MultipartFile à traiter
     * @return MultipartFile de l'image traitée
     */
    public MultipartFile preprocessImage(MultipartFile file) throws IOException {
        // Charger l'image en mémoire en gris (les PNG sont aussi pris en charge)
        Mat image = Imgcodecs.imdecode(new MatOfByte(file.getBytes()), Imgcodecs.IMREAD_GRAYSCALE);

        if (image.empty()) {
            throw new IllegalArgumentException("Impossible de lire l'image.");
        }

        // Vérifier la résolution de l'image et augmenter la taille si nécessaire
        int targetWidth = 1500; // Résolution plus élevée pour les photos de téléphone
        int targetHeight = (int) ((double) targetWidth / image.width() * image.height()); // Maintenir le ratio
        Mat resizedImage = new Mat();
        Imgproc.resize(image, resizedImage, new Size(targetWidth, targetHeight));

        // Appliquer un ajustement de contraste et de luminosité si nécessaire (améliorer la lisibilité du texte)
        Mat adjustedImage = new Mat();
        resizedImage.convertTo(adjustedImage, -1, 1.1, 10); // Augmenter légèrement le contraste

        // Appliquer un seuil adaptatif pour binariser l'image (noir et blanc)
        Mat binaryImage = new Mat();
        Imgproc.adaptiveThreshold(adjustedImage, binaryImage, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 11, 2);

        // Réduire le bruit avec un flou léger si nécessaire (le flou peut parfois effacer des détails importants)
        Mat blurred = new Mat();
        Imgproc.GaussianBlur(binaryImage, blurred, new Size(3, 3), 0);

        // Encoder l'image traitée en mémoire (en format PNG)
        MatOfByte buffer = new MatOfByte();
        Imgcodecs.imencode(".png", blurred, buffer); // Sauvegarder l'image en PNG

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