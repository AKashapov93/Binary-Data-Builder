package ru.example.binarydatabuilder.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "data_table")
@ToString
public class DataTable {

    private static long counter = 0;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "address")
    private String address;

    @Column(name = "command")
    private String command;

    @Setter(AccessLevel.NONE)
    @Column(name = "marker")
    private String marker;

    @Column(name = "mnemonic_and_operand")
    private String mnemonicAndOperand;

    @Column(name = "comment")
    private String comment;

    public DataTable(String command, String mnemonicAndOperand, String comment) {
        this.command = command;
        this.mnemonicAndOperand = mnemonicAndOperand;
        this.comment = comment;
        this.marker = null;
        this.address = generateHexValue();
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
