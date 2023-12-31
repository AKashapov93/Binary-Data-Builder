package ru.example.binarydatabuilder.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.example.binarydatabuilder.dto.DataRequest;
import ru.example.binarydatabuilder.entity.DataTable;
import ru.example.binarydatabuilder.repository.DataTableRepository;
import ru.example.binarydatabuilder.service.DataService;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static ru.example.binarydatabuilder.utils.Constant.*;

@Service
@RequiredArgsConstructor
public class DataServiceImpl implements DataService {
    public static final String FILE_PATH = "C://Aleksandr//Binary-Data-Builder//src//main//resources//хуета.580";

    private final DataTableRepository dataTableRepository;

    @Override
    public void createTableAndWriteToFile(final DataRequest dataRequest)  {
        //  buildTable(dataRequest);

        try {
            buildFile(dataRequest);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public String convertStringToHexCode(String data) {
        byte[] bytes = data.getBytes(Charset.forName("CP866"));
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(Integer.toHexString(b & 0xFF)).append(" ");
        }
        return sb.toString();
    }

    @Override
    public byte[] readHexFile() {
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
    public void saveHexFile(byte[] data) throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(FILE_PATH)) {
            fileOutputStream.write(data);

        }
    }
public void  buildFile( final  DataRequest dataRequest) throws IOException {
    String[] strings = {dataRequest.name(), dataRequest.group(), dataRequest.recordBook()};
    StringBuilder builder = new StringBuilder();

    for (int i = 0; i < 3; i++) {
        String[] hexStrings = convertStringToHexCode(strings[i]).split(" ");
        int b = 39 - strings[i].length();
        String[] spase = buildWhitespace(b);
        String[] result = Stream.concat(Arrays.stream(hexStrings), Arrays.stream(spase))
                .toArray(String[]::new);
        String color = (i == 0) ? RED_COLOR_COMMAND : ((i == 1) ? GREEN_COLOR_COMMAND : BLUE_COLOR_COMMAND);
        for (String s :result) {
            builder.append(color).append(" ").append(COMMAND_TEMPLATE.formatted(s)).append(" ");
        }
        builder.append("76");
    }
    String result = builder.toString().trim();
    String result1 = result.replace("\n" , " ");
    System.out.println(result1);
 byte[] data = readHexFile();
 editHexValue(data, result1);
 saveHexFile(data);
}
public String[] buildWhitespace(int i){
        String[] strings = new String[i];
    for (int j = 0; j <i ; j++) {
        strings[j] = "20";
    }
    return strings;
}
    private void buildTable(final DataRequest dataRequest) {
        String[] strings = {dataRequest.name(), dataRequest.group(), dataRequest.recordBook()};
        IntStream.range(0, strings.length)
                .forEach(index -> {
                    String[] hexStrings = convertStringToHexCode(strings[index]).split(" ");
                    Arrays.stream(hexStrings)
                            .forEach(hexString -> {
                                DataTable colorDataTable = chooseDataTableColor(index);
                                dataTableRepository.save(colorDataTable);
                                dataTableRepository.save(new DataTable(
                                        COMMAND_TEMPLATE.formatted(hexString),
                                        MNEMONIC_AND_OPERAND_TEMPLATE.formatted(hexString),
                                        COMMENT_TEMPLATE.formatted(
                                                hexString,
                                                hexString,
                                                hexString.equals("20") ? COMMENT_SPACE : convertHexCodeToString(hexString),
                                                hexString
                                        )));

                            });
                });
    }

    public DataTable chooseDataTableColor(int index) {
        switch (index) {
            case (0) -> {
                return new DataTable(RED_COLOR_COMMAND, MNEMONIC_AND_OPERAND_RED, COMMENT_RED);
            }
            case (1) -> {
                return new DataTable(GREEN_COLOR_COMMAND, MNEMONIC_AND_OPERAND_GREEN, COMMENT_GREEN);
            }
            case (2) -> {
                return new DataTable(BLUE_COLOR_COMMAND, MNEMONIC_AND_OPERAND_BLUE, COMMENT_BLUE);
            }
        }
        return null;
    }

    public String convertHexCodeToString(String hexCode) {
        String[] hexValues = hexCode.split(" ");
        byte[] bytes = new byte[hexValues.length];

        for (int i = 0; i < hexValues.length; i++) {
            int intValue = Integer.parseInt(hexValues[i], 16);
            bytes[i] = (byte) intValue;
        }

        return new String(bytes, Charset.forName("CP866"));
    }
}