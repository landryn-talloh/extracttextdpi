package com.example.extracttextdpi;
import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;

import java.io.File;

public class ImageDpiExtractor {

    /**
     * Extrait la résolution DPI d'une image.
     *
     * @param imageFile Fichier image.
     * @return La résolution DPI (valeur par défaut : 72 si non trouvée).
     */
    public static int extractDpi(File imageFile) {
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(imageFile);
            ExifIFD0Directory directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);

            if (directory != null) {
                Integer xDpi = directory.getInteger(ExifIFD0Directory.TAG_X_RESOLUTION);
                Integer yDpi = directory.getInteger(ExifIFD0Directory.TAG_Y_RESOLUTION);

                // Retourner le DPI moyen ou une valeur disponible
                if (xDpi != null && yDpi != null) {
                    return (xDpi + yDpi) / 2;
                } else if (xDpi != null) {
                    return xDpi;
                } else if (yDpi != null) {
                    return yDpi;
                }
            }

            // Retourner une valeur par défaut si le DPI n'est pas trouvé
            return 72;
        } catch (Exception e) {
            e.printStackTrace();
            return 72;
        }
    }
}
