package ru.example.binarydatabuilder.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record DataRequest(
        @Schema(defaultValue = "Руслан Джамбаев")
        String name,
        @Schema(defaultValue = "Группа ЭС-19-1бз")
        String group,
        @Schema(defaultValue = "Зачетная книжка 19-ЭТФз-636")
        String recordBook
) {
}