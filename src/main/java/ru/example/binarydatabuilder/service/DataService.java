package ru.example.binarydatabuilder.service;

import ru.example.binarydatabuilder.dto.DataRequest;

import java.io.IOException;

public interface DataService {

    String createTableAndWriteToFile(DataRequest dataRequest);

    String convertStringToHexCode(String data);

    byte[] readHexFile();

    void printHexDump(byte[] data);

    void editHexValue(byte[] data, String newValueHex);

    void saveHexFile(byte[] data) throws IOException;
}
