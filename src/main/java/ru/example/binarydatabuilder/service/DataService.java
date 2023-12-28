package ru.example.binarydatabuilder.service;

import java.io.IOException;

public interface DataService {

    String convertStringToHexCode(String data);

    byte[] readHexFile(String filePath) throws IOException;

    void printHexDump(byte[] data);

    void editHexValue(byte[] data, String newValueHex);

    void saveHexFile(String filePath, byte[] data) throws IOException;
}
