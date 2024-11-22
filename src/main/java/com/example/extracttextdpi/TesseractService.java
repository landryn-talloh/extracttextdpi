package com.example.extracttextdpi;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.io.File;

public class TesseractService {

    private final Tesseract tesseract;

    public TesseractService() {
        tesseract = new Tesseract();
        tesseract.setDatapath("/usr/local/share/tessdata");//chemin serveur tessdata
        tesseract.setLanguage("eng+fra");
    }

    /**
     * Effectue l'OCR sur une image avec le DPI spécifié.
     *
     * @param imageFile Fichier image.
     * @param dpi       DPI de l'image.
     * @return Texte extrait de l'image.
     * @throws TesseractException Si une erreur survient pendant l'OCR.
     */
    public String performOCR(File imageFile, int dpi) throws TesseractException {
        tesseract.setTessVariable("user_defined_dpi", String.valueOf(dpi));
        return tesseract.doOCR(imageFile);
    }
}
