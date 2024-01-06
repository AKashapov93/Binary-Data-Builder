package ru.example.binarydatabuilder.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;
import org.springframework.stereotype.Service;
import ru.example.binarydatabuilder.dto.DataRequest;
import ru.example.binarydatabuilder.dto.NumberOfSpaces;
import ru.example.binarydatabuilder.entity.DataTable;
import ru.example.binarydatabuilder.repository.DataTableRepository;
import ru.example.binarydatabuilder.service.DataService;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static ru.example.binarydatabuilder.utils.Constant.*;

@Service
@RequiredArgsConstructor
public class DataServiceImpl implements DataService {

    public static final String FILE_PATH = "C://Aleksandr//Binary-Data-Builder//src//main//resources//Александр.580";

    //Создавай каждый раз чистый файл иначе будут ошибки!!!!
    private final DataTableRepository dataTableRepository;
    private int counter = 0;

    @Override
    public String createTableAndWriteToFile(final DataRequest dataRequest) {

        try {
            buildFile(dataRequest);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return buildTable(dataRequest);
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

    public void buildFile(final DataRequest dataRequest) throws IOException {
        String[] strings = {dataRequest.name(), dataRequest.group(), dataRequest.recordBook()};
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < 3; i++) {
            String[] hexStrings = convertStringToHexCode(strings[i]).split(" ");
            int b = 39 - strings[i].length();
            String[] spase = buildWhitespace(b);
            String[] result = Stream.concat(Arrays.stream(hexStrings), Arrays.stream(spase))
                    .toArray(String[]::new);
            String[] result1 = new String[result.length - 1];
            System.arraycopy(result, 0, result1, 0, result1.length);
            String color = (i == 0) ? RED_COLOR_COMMAND : ((i == 1) ? GREEN_COLOR_COMMAND : BLUE_COLOR_COMMAND);
            for (String s : result1) {
                builder.append(color).append(" ").append(COMMAND_TEMPLATE.formatted(s)).append(" ");
            }
            builder.append("76");
        }
        String result = builder.toString().trim();
        String result1 = result.replace("\n", " ");
        String result2 = result1.toUpperCase();
        System.out.println(result2);
        byte[] data = readHexFile();
        editHexValue(data, result2);
        saveHexFile(data);
    }

    public String[] buildWhitespace(int i) {
        String[] strings = new String[i + 1];
        String[] strings1 = new String[1];
        counter++;
        if (counter < 3) {

            for (int j = 0; j < i + 1; j++) {
                strings[j] = "20";
            }
            strings[i] = "0";
            return strings;

        } else {
            strings1[0] = "76";
            return strings1;
        }
    }

    private String buildTable(final DataRequest dataRequest) {
        String[] strings = {dataRequest.name(), dataRequest.group(), dataRequest.recordBook()};
        List<String> lines = new ArrayList<>();
        IntStream.range(0, strings.length)
                .forEach(index -> {
                    String currentLine = strings[index].trim();
                    lines.add(String.valueOf(39 - currentLine.length()));
                    String[] hexStrings = convertStringToHexCode(currentLine).split(" ");
                    Arrays.stream(hexStrings)
                            .forEach(hexString -> {
                                DataTable colorDataTable = chooseDataTableColor(index);
                                dataTableRepository.save(colorDataTable);
                                dataTableRepository.save(new DataTable(null,
                                        COMMAND_TEMPLATE.formatted(hexString.toUpperCase()),
                                        MNEMONIC_AND_OPERAND_TEMPLATE.formatted(hexString.toUpperCase()),
                                        hexString.equals("20") ? COMMENT_SPACE : COMMENT_TEMPLATE.formatted(
                                                hexString.toUpperCase(),
                                                hexString.toUpperCase(),
                                                convertHexCodeToString(hexString),
                                                hexString.toUpperCase()
                                        )));

                            });
                    if (index != strings.length - 1) {
                        IntStream.range(currentLine.length(), 39).forEach(value -> {
                            dataTableRepository.save(new DataTable(null, null, null, null));
                            dataTableRepository.save(new DataTable(null, null, null, null));
                        });
                    } else {
                        dataTableRepository.save(new DataTable("not empty", "76", "HLT", "Остановка процессора"));
                    }
                });
        NumberOfSpaces numberOfSpaces = new NumberOfSpaces(lines.get(0), lines.get(1));
        return createWordDocument(numberOfSpaces, dataRequest.name().trim().replace(" ", "_"));
    }

    public DataTable chooseDataTableColor(int index) {
        switch (index) {
            case (0) -> {
                return new DataTable(null, RED_COLOR_COMMAND, MNEMONIC_AND_OPERAND_RED, COMMENT_RED);
            }
            case (1) -> {
                return new DataTable(null, GREEN_COLOR_COMMAND, MNEMONIC_AND_OPERAND_GREEN, COMMENT_GREEN);
            }
            case (2) -> {
                return new DataTable(null, BLUE_COLOR_COMMAND, MNEMONIC_AND_OPERAND_BLUE, COMMENT_BLUE);
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

    private String createWordDocument(NumberOfSpaces numberOfSpaces, String fileName) {
        String fullWordFilePath = WORD_FILE_PATH + fileName + ".docx";
        try (FileOutputStream fos = new FileOutputStream(fullWordFilePath);
             XWPFDocument document = new XWPFDocument()) {

            XWPFTable table = document.createTable();
            table.setWidth(8052);
            table.setWidthType(TableWidthType.DXA);

            List<DataTable> data = dataTableRepository.findAll();

            table.removeRow(0);
            XWPFTableRow headerRow = table.createRow();
            for (int i = 0; i < COLUMN_NAMES.length; i++) {
                String columnName = COLUMN_NAMES[i];
                XWPFTableCell cell = headerRow.getCell(i);
                if (cell == null) {
                    cell = headerRow.createCell();
                }
                cell.setText(columnName);
                cell.setWidth(COLUMN_WIDTHS[i]);
                cell.setWidthType(TableWidthType.DXA);
                cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            }

            boolean skipRows = false;
            int countLine = 0;

            for (DataTable row : data) {
                if (row.getCommand() == null && row.getMnemonicAndOperand() == null && row.getComment() == null) {
                    if (!skipRows) {
                        XWPFTableRow tableRow = table.createRow();
                        for (int i = 0; i < 5; i++) {
                            CTTcPr tcPr = tableRow.getCell(i).getCTTc().addNewTcPr();
                            if (i == 0) {
                                tcPr.addNewHMerge().setVal(STMerge.RESTART);
                            } else {
                                tcPr.addNewHMerge().setVal(STMerge.CONTINUE);
                            }
                        }
                        XWPFTableCell cell = tableRow.getCell(0);
                        cell.setText(String.format(REPLACE_SPACES,
                                countLine == 0 ? numberOfSpaces.firstLine()
                                        : numberOfSpaces.secondLine()));
                        skipRows = true;
                        countLine++;
                    }
                } else {
                    if (skipRows) skipRows = false;
                    XWPFTableRow tableRow = table.createRow();
                    processCellWithLineBreaks(tableRow.getCell(0), row.getAddress());
                    processCellWithLineBreaks(tableRow.getCell(1), row.getCommand());
                    processCellWithLineBreaks(tableRow.getCell(3), row.getMnemonicAndOperand());
                    processCellWithLineBreaks(tableRow.getCell(4), row.getComment());
                }

            }

            for (XWPFTableRow tableRow : table.getRows()) {
                if (nonNull(tableRow.getCell(1))) {
                    XWPFTableCell cell = tableRow.getCell(1);
                    cell.getParagraphs().forEach(paragraph -> paragraph.setAlignment(ParagraphAlignment.CENTER));
                }
            }

            document.write(fos);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return fullWordFilePath;
    }

    private void processCellWithLineBreaks(XWPFTableCell cell, String content) {
        if (isNull(content)) return;
        String[] lines = content.split("\n");
        XWPFParagraph paragraph = cell.addParagraph();
        for (String line : lines) {
            XWPFRun run = paragraph.createRun();
            run.setFontFamily("Arimo");
            run.setFontSize(10);
            run.setText(line);
            run.addBreak();
        }
    }
}