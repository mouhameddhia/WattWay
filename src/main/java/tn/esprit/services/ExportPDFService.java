package tn.esprit.services;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import tn.esprit.entities.Mechanic;

import java.util.List;

public class ExportPDFService {

    private MechanicServices mechanicServices = new MechanicServices();

    public void exportMechanicsToPDF(String outputFilePath) throws Exception {
        // Retrieve list of mechanics.
        List<Mechanic> mechanics = mechanicServices.returnList();

        // Create a new PDF document.
        PDDocument document = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);

        // Set up a content stream.
        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        // Define margins and table parameters.
        float margin = 50;
        float yStart = PDRectangle.A4.getHeight() - margin;
        float tableBottomY = margin;
        // Define column widths. Adjust as needed.
        float[] colWidths = {50, 200, 150, 100};
        float tableWidth = 0;
        for (float w : colWidths) {
            tableWidth += w;
        }
        float rowHeight = 20;
        float yPosition = yStart;

        // --------------------------
        // Draw Header Row Background
        // --------------------------
        // Set header background color (e.g., primary color: #00bba8).
        contentStream.setNonStrokingColor(0, 187, 168);
        float nextX = margin;
        // Draw each header cell's background.
        String[] headers = {"ID", "Name", "Speciality", "Cars Repaired"};
        for (int i = 0; i < headers.length; i++) {
            contentStream.addRect(nextX, yPosition - rowHeight, colWidths[i], rowHeight);
            contentStream.fill();
            nextX += colWidths[i];
        }

        // --------------------------
        // Write Header Text
        // --------------------------
        // Set text color to white.
        contentStream.setNonStrokingColor(255, 255, 255);
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
        nextX = margin;
        for (int i = 0; i < headers.length; i++) {
            contentStream.beginText();
            // A little padding inside the cell.
            contentStream.newLineAtOffset(nextX + 2, yPosition - 15);
            contentStream.showText(headers[i]);
            contentStream.endText();
            nextX += colWidths[i];
        }

        // Draw a horizontal line below the header in white.
        contentStream.setStrokingColor(255, 255, 255);
        yPosition -= rowHeight;
        contentStream.moveTo(margin, yPosition);
        contentStream.lineTo(margin + tableWidth, yPosition);
        contentStream.stroke();

        // --------------------------
        // Write Data Rows
        // --------------------------
        // Reset text color to black (or choose a contrasting color).
        contentStream.setNonStrokingColor(0, 0, 0);
        contentStream.setFont(PDType1Font.HELVETICA, 12);

        // Optionally, you can alternate row background colors.
        int rowCount = 0;
        for (Mechanic mechanic : mechanics) {
            // Check if we need a new page.
            if (yPosition - rowHeight < tableBottomY) {
                contentStream.close();
                page = new PDPage(PDRectangle.A4);
                document.addPage(page);
                contentStream = new PDPageContentStream(document, page);
                yPosition = yStart;
            }

            nextX = margin;

            // Optional: fill row background for even rows.
            if (rowCount % 2 == 0) {
                // Light gray background for even rows.
                contentStream.setNonStrokingColor(230, 230, 230);
                contentStream.addRect(margin, yPosition - rowHeight, tableWidth, rowHeight);
                contentStream.fill();
                // Reset text color to black.
                contentStream.setNonStrokingColor(0, 0, 0);
            }

            // Write row text.
            String[] row = {
                    String.valueOf(mechanic.getIdMechanic()),
                    mechanic.getNameMechanic(),
                    mechanic.getSpecialityMechanic().toString(),
                    String.valueOf(mechanic.getCarsRepaired())
            };

            for (int i = 0; i < row.length; i++) {
                contentStream.beginText();
                contentStream.newLineAtOffset(nextX + 2, yPosition - 15);
                contentStream.showText(row[i]);
                contentStream.endText();
                nextX += colWidths[i];
            }

            // Draw a horizontal line after the row.
            yPosition -= rowHeight;
            contentStream.moveTo(margin, yPosition);
            contentStream.lineTo(margin + tableWidth, yPosition);
            contentStream.stroke();

            rowCount++;
        }

        contentStream.close();
        document.save(outputFilePath);
        document.close();
    }
}
