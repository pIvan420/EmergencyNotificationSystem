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
import ru.pIvan.EmergencyNotificationSystem.util.fileHandlers.CsvFileHandler;
import ru.pIvan.EmergencyNotificationSystem.util.fileHandlers.FileHandler;
import ru.pIvan.EmergencyNotificationSystem.util.fileHandlers.XlsFileHandler;
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
    private static final FileHandler csvFileHandler = new CsvFileHandler();
    private static final FileHandler xlsFileHandler = new XlsFileHandler();

    @Autowired
    public FileHandlerService(NotifiedUserValidator notifyUserValidator) {
        this.notifyUserValidator = notifyUserValidator;
        this.phoneNumberUtil = PhoneNumberUtil.getInstance();
    }

    public ProcessingResult processFileToNotifyUsers(MultipartFile file) throws InvalidFileFormatException, IOException {
        List<String> rawFileRecords;
        String filename = file.getOriginalFilename();

        if(filename == null){
            throw new IllegalArgumentException("File name cannot be empty");
        } else if (filename.lastIndexOf(".") == -1) {
            throw new IllegalArgumentException("File name cannot be without extension");
        }

        String fileExtension = filename.substring(filename.lastIndexOf(".") + 1);

        FileHandler fileHandler = switch (fileExtension.toLowerCase()) {
            case "csv" -> csvFileHandler;
            case "xls" -> xlsFileHandler;
            default -> throw new InvalidFileFormatException(fileExtension);
        };

        rawFileRecords = fileHandler.process(file);

        return convertStringToNotifyUser(rawFileRecords);
    }

    private ProcessingResult convertStringToNotifyUser(List<String> records){
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
        return new ProcessingResult(correctPhoneNumbers, badPhoneNumbers);
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

    public class ProcessingResult {
        private final Set<NotifiedUser> correctPhoneNumbers;
        private final Set<BadPhoneNumber> badPhoneNumbers;

        public ProcessingResult(Set<NotifiedUser> correctPhoneNumbers, Set<BadPhoneNumber> badPhoneNumbers) {
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