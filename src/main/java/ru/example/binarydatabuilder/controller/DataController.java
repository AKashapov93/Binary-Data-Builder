package ru.example.binarydatabuilder.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.example.binarydatabuilder.dto.DataRequest;
import ru.example.binarydatabuilder.service.DataService;


@RestController
@RequiredArgsConstructor
public class DataController {

    private final DataService dataService;

    @PostMapping("/data")
    public ResponseEntity<String> acceptData(@RequestBody DataRequest dataRequest) {
        return ResponseEntity.ok(dataService.createTableAndWriteToFile(dataRequest));
    }

    @GetMapping("/data")
    public ResponseEntity<Void> printFile() {
        dataService.printHexDump(dataService.readHexFile());
        return ResponseEntity.noContent().build();

    }
}
