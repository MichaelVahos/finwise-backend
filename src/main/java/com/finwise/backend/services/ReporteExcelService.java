package com.finwise.backend.services;

import com.finwise.backend.models.Transaccion;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ReporteExcelService {

    public byte[] exportarExcel(List<Transaccion> transacciones) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Transacciones");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Fecha");
        header.createCell(1).setCellValue("Descripción");
        header.createCell(2).setCellValue("Monto");
        header.createCell(3).setCellValue("Tipo");

        int fila = 1;
        for (Transaccion tx : transacciones) {
            Row row = sheet.createRow(fila++);
            row.createCell(0).setCellValue(tx.getFecha().toString());
            row.createCell(1).setCellValue(tx.getDescripcion());
            row.createCell(2).setCellValue(tx.getMonto());
            row.createCell(3).setCellValue(tx.getTipo().toString());
        }

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        workbook.write(output);
        workbook.close();

        return output.toByteArray();
    }
}