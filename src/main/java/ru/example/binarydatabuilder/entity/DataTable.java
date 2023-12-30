package ru.example.binarydatabuilder.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import static ru.example.binarydatabuilder.utils.Constant.RED_COLOR;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Table(name = "data_table")
@ToString
public class DataTable {

    private static long counter = 0;
    private static boolean initFlag = false;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "address")
    private String address;

    @Column(name = "command")
    private String command;

    public DataTable() {
        if (initFlag) {
            this.address = generateHexValue();
            this.command = RED_COLOR;
        }
        initFlag = true;
    }

    private String generateHexValue() {
        StringBuilder hexString = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            hexString.append(String.format("%04X", counter++));
            hexString.append("\n");
        }
        return hexString.toString();
    }
}
