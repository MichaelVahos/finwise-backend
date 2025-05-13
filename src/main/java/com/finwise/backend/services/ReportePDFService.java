package com.finwise.backend.services;

import com.finwise.backend.models.Transaccion;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class ReportePDFService {

    public byte[] generarReporteMensual(List<Transaccion> transacciones) throws DocumentException {
        Document document = new Document();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);
        document.open();

        // Título
        Font fontTitulo = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        document.add(new Paragraph("Reporte Mensual de Transacciones", fontTitulo));
        document.add(Chunk.NEWLINE);

        // Tabla
        PdfPTable table = new PdfPTable(4); // Fecha, Descripción, Monto, Tipo
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        table.addCell(new PdfPCell(new Phrase("Fecha", headerFont)));
        table.addCell(new PdfPCell(new Phrase("Descripción", headerFont)));
        table.addCell(new PdfPCell(new Phrase("Monto", headerFont)));
        table.addCell(new PdfPCell(new Phrase("Tipo", headerFont)));

        double totalIngresos = 0;
        double totalGastos = 0;

        for (Transaccion tx : transacciones) {
            table.addCell(tx.getFecha().toString());
            table.addCell(tx.getDescripcion());
            table.addCell("$" + tx.getMonto());
            table.addCell(tx.getTipo().name());

            if (tx.getTipo().name().equals("INGRESO")) {
                totalIngresos += tx.getMonto();
            } else {
                totalGastos += tx.getMonto();
            }
        }

        document.add(table);

        // Totales
        document.add(new Paragraph("Total Ingresos: $" + totalIngresos));
        document.add(new Paragraph("Total Gastos: $" + totalGastos));
        document.add(new Paragraph("Balance Final: $" + (totalIngresos - totalGastos)));

        document.close();
        return baos.toByteArray();
    }
}
