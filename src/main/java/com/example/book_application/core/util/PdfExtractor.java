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
            stripper.setSortByPosition(true); // Metni pozisyona göre sırala
            stripper.setAddMoreFormatting(true); // Daha fazla formatlama ekle
            stripper.setLineSeparator("\n"); // Satır ayırıcıyı belirle
            stripper.setParagraphStart("\n"); // Paragraf başlangıcını belirle
            stripper.setParagraphEnd("\n"); // Paragraf sonunu belirle
            stripper.setWordSeparator(" "); // Kelime ayırıcıyı belirle
            stripper.setSpacingTolerance(0.5f); // Boşluk toleransını ayarla
            stripper.setAverageCharTolerance(0.3f); // Karakter toleransını ayarla

            StringBuilder extractedText = new StringBuilder();
            int totalPages = document.getNumberOfPages();

            for (int page = 1; page <= totalPages; page++) {
                stripper.setStartPage(page);
                stripper.setEndPage(page);
                
                // Sayfadaki metni al
                String pageText = stripper.getText(document);
                
                // Metni temizle ve formatla
                pageText = pageText
                    // Birden fazla boş satırı tek satıra indir
                    .replaceAll("(?m)^\\s+$", "")
                    .replaceAll("\n{3,}", "\n\n")
                    // Tire ile bölünmüş kelimeleri birleştir
                    .replaceAll("(\\w+)-\\s*\n\\s*(\\w+)", "$1$2")
                    // Cümle ortasındaki gereksiz satır sonlarını kaldır
                    .replaceAll("(?<![\\.\\!\\?])\\s*\n(?!\\s*[A-Z])", " ")
                    .trim();

                extractedText.append("=== Page ").append(page).append(" ===\n\n");
                extractedText.append(pageText);
                extractedText.append("\n\n");
            }

            return extractedText.toString()
                // Son temizlik
                .replaceAll("\n{3,}", "\n\n")
                .trim();
        }
    }
}