package ru.pIvan.EmergencyNotificationSystem.services;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.multipart.MultipartFile;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.ICsvListReader;
import org.supercsv.prefs.CsvPreference;
import ru.pIvan.EmergencyNotificationSystem.models.NotifiedUser;
import ru.pIvan.EmergencyNotificationSystem.util.validator.InvalidFileFormatException;
import ru.pIvan.EmergencyNotificationSystem.util.validator.NotifiedUserValidator;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;

@Service
public class FileHandlerService {

    private final NotifiedUserValidator notifyUserValidator;
    private final PhoneNumberUtil phoneNumberUtil;

    @Autowired
    public FileHandlerService(NotifiedUserValidator notifyUserValidator) {
        this.notifyUserValidator = notifyUserValidator;
        this.phoneNumberUtil = PhoneNumberUtil.getInstance();
    }

    public NotificationResult processFileToNotifyUsers(MultipartFile file) throws InvalidFileFormatException, IOException {
        List<String> records;
        String fileName = file.getOriginalFilename();
        if (fileName != null) {

            String extension = fileName.substring(fileName.lastIndexOf(".") + 1); // Получение расширения файла

            records = switch (extension.toLowerCase()) {
                case "csv" -> csvFileProcessor(file);
                case "xls" -> xlsFileProcessor(file);
                default -> throw new InvalidFileFormatException("Unsupported file extension: " + extension);
            };

            return convertStringToNotifyUser(records);
        }
        else{
            throw new IllegalArgumentException("File name cannot be empty");
        }
    }
    private NotificationResult convertStringToNotifyUser(List<String> records){
        Set<NotifiedUser> correctPhoneNumbers = new HashSet<>();
        Set<BadPhoneNumber> badPhoneNumbers = new HashSet<>();
        for(String record: records){

            // try/catch и классы PhoneNumber проводят валидацию и форматирование номера
            try {
                Phonenumber.PhoneNumber phoneNumber = phoneNumberUtil.parse(record, "RU");
                String formattedPhoneNumber = phoneNumberUtil
                        .format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164); // Форматирование телефона к виду +79876543210
                if(!phoneNumberUtil.isValidNumber(phoneNumber)){
                    throw new IllegalArgumentException();
                }


                NotifiedUser notifyUser = new NotifiedUser(formattedPhoneNumber);
                Errors errors = new BeanPropertyBindingResult(notifyUser, "notifyUser");
                notifyUserValidator.validate(notifyUser, errors);
                if (errors.hasErrors()) {
                    badPhoneNumbers.add(new BadPhoneNumber(record, errors.getFieldErrors()));
                }
                else {
                    correctPhoneNumbers.add(notifyUser);
                }
            } catch (NumberParseException e) {
                badPhoneNumbers.add(new BadPhoneNumber(record,
                        "Could not convert this string to a phone number"));
            } catch (IllegalArgumentException e) {
                badPhoneNumbers.add(new BadPhoneNumber(record,
                        "Invalid phone number format"));
            }
        }
        return new NotificationResult(correctPhoneNumbers, badPhoneNumbers);
    }

    private List<String> csvFileProcessor(MultipartFile file) throws IOException {
        //считываю данные из файла и преобразую ячейки таблицы к строкам
        List<String> records = new ArrayList<>();
        Reader reader = new InputStreamReader(file.getInputStream());
        ICsvListReader listReader = new CsvListReader(reader, CsvPreference.STANDARD_PREFERENCE);

        List<String> record;
        while((record = listReader.read()) != null) {
            //Из строки достаю НЕ пустые ячейки
            records.addAll(Arrays
                    .stream(record
                            .get(0)
                            .split(";"))
                    .filter(s -> !s.isEmpty())
                    .toList());
        }
        return records;
    }

    private List<String> xlsFileProcessor(MultipartFile file) throws IOException {
        List<String> records = new ArrayList<>();
        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        for(Row row: sheet){
            for(Cell cell: row){
                String record = switch(cell.getCellType()){
                    case STRING -> cell.getStringCellValue();
                    case NUMERIC -> Long.toString((long) cell.getNumericCellValue());
                    default -> null;
                };
                if(record != null) records.add(record);
            }
        }
        workbook.close();
        return records;
    }

    public class BadPhoneNumber{
        private final String phoneNumber;
        private final StringBuilder errorMsg;

        public BadPhoneNumber(String phoneNumber, List<FieldError> errors ) {
            this.phoneNumber = phoneNumber;
            this.errorMsg = new StringBuilder();
            for(FieldError error: errors){
                errorMsg.append(error.getDefaultMessage());
            }
        }

        public BadPhoneNumber(String phoneNumber, String error ) {
            this.phoneNumber = phoneNumber;
            this.errorMsg = new StringBuilder();
            errorMsg.append(error);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BadPhoneNumber that = (BadPhoneNumber) o;
            return Objects.equals(phoneNumber, that.phoneNumber);
        }

        @Override
        public int hashCode() {
            return Objects.hash(phoneNumber);
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public String getErrorMsg() {
            return errorMsg.toString();
        }
    }

    public class NotificationResult {
        private final Set<NotifiedUser> correctPhoneNumbers;
        private final Set<BadPhoneNumber> badPhoneNumbers;

        public NotificationResult(Set<NotifiedUser> correctPhoneNumbers, Set<BadPhoneNumber> badPhoneNumbers) {
            this.correctPhoneNumbers = correctPhoneNumbers;
            this.badPhoneNumbers = badPhoneNumbers;
        }

        public Set<NotifiedUser> getCorrectPhoneNumbers() {
            return correctPhoneNumbers;
        }

        public Set<BadPhoneNumber> getBadPhoneNumbers() {
            return badPhoneNumbers;
        }
    }
}