package ru.pIvan.EmergencyNotificationSystem.util.fileHandlers;

import org.apache.poi.ss.usermodel.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class XlsFileHandler implements FileHandler{
    @Override
    public List<String> process(MultipartFile file) throws IOException {
        List<String> fileRecords = new ArrayList<>();
        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        for(Row row: sheet){
            for(Cell cell: row){
                String fileRecord = switch(cell.getCellType()){
                    case STRING -> cell.getStringCellValue();
                    case NUMERIC -> Long.toString((long) cell.getNumericCellValue());
                    default -> null;
                };
                if(fileRecord != null) fileRecords.add(fileRecord);
            }
        }

        workbook.close();

        return fileRecords;
    }
}