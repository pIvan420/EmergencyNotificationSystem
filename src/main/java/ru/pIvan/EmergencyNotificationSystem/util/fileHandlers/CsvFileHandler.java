package ru.pIvan.EmergencyNotificationSystem.util.fileHandlers;

import org.springframework.web.multipart.MultipartFile;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.ICsvListReader;
import org.supercsv.prefs.CsvPreference;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CsvFileHandler implements FileHandler{
    @Override
    public List<String> process(MultipartFile file) throws IOException {
        List<String> fileRecords = new ArrayList<>();
        Reader reader = new InputStreamReader(file.getInputStream());
        ICsvListReader listReader = new CsvListReader(reader, CsvPreference.STANDARD_PREFERENCE);

        List<String> fileLine; //Строка таблицы
        while((fileLine = listReader.read()) != null) {
            //Преобразую fileLine к массиву, убирая пустые ячейки, и добавляю его в fileRecords
            fileRecords.addAll(Arrays
                    .stream(fileLine
                            .get(0)
                            .split(";"))
                    .filter(s -> !s.isEmpty())
                    .toList());
        }
        return fileRecords;
    }
}