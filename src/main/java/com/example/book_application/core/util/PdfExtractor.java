package com.example.book_application.core.util;


import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;

public class PdfExtractor {

    public static String extractTextFromPdf(String filePath) throws IOException {
        File file = new File(filePath);
        try (PDDocument document = PDDocument.load(file)) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true); // Ensures words appear in correct order

            StringBuilder extractedText = new StringBuilder();
            int totalPages = document.getNumberOfPages();

            for (int page = 1; page <= totalPages; page++) {
                stripper.setStartPage(page);
                stripper.setEndPage(page);
                extractedText.append("=== Page ").append(page).append(" ===\n"); // ✅ Add page separator
                extractedText.append(stripper.getText(document));
                extractedText.append("\n\n"); // ✅ Ensure paragraph breaks
            }

            return extractedText.toString();
        }
    }
}