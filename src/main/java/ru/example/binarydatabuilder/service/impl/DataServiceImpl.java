package ru.example.binarydatabuilder.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.example.binarydatabuilder.entity.DataTable;
import ru.example.binarydatabuilder.repository.DataTableRepository;
import ru.example.binarydatabuilder.service.DataService;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

@Service
@RequiredArgsConstructor
public class DataServiceImpl implements DataService {
    public static final String FILE_PATH = "C:\\Aleksandr\\Binary-Data-Builder\\src\\main\\resources\\DJAMBO.580";

    private final DataTableRepository dataTableRepository;

    @Override
    public String convertStringToHexCode(String data) {
        byte[] bytes = data.getBytes(Charset.forName("CP866"));
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(Integer.toHexString(b & 0xFF)).append(" ");
        }
        dataTableRepository.save(new DataTable());
        return sb.toString();
    }

    @Override
    public byte[] readHexFile()  {
        try (FileInputStream fileInputStream = new FileInputStream(FILE_PATH)) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;


            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }

            return byteArrayOutputStream.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void printHexDump(byte[] data) {
        for (int i = 0; i < data.length; i++) {
            System.out.printf("%02X ", data[i]);

            if ((i + 1) % 16 == 0) {
                System.out.println();
            }
        }
        System.out.println();
    }

    @Override
    public void editHexValue(byte[] data, String newValueHex) {
        String[] hexValues = newValueHex.split("\\s+");

        for (int i = 0; i < hexValues.length && i < data.length; i++) {
            int newValue = Integer.parseInt(hexValues[i], 16);
            data[i] = (byte) newValue;
        }
    }

    @Override
    public void saveHexFile(String filePath, byte[] data) throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
            fileOutputStream.write(data);
        }
    }
}
