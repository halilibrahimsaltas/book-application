package com.example.book_application.core.util;


import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;

public class PdfExtractor {

    public static String extractTextFromPdf(String filePath) throws IOException {
        File file = new File(filePath);
        try (PDDocument document = PDDocument.load(file)) {
            return new PDFTextStripper().getText(document);
        }
    }
}
