--liquibase formatted sql

--changeset pruglo-ve:20231231-1 failOnError:true
--comment: Create data_table table.
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:0 select count(*) from information_schema.tables where table_name = 'data_table';
create table if not exists data_table
(
    id                      BIGSERIAL           PRIMARY KEY,
    address                 varchar(255),
    command                 varchar(255),
    marker                  varchar(255),
    mnemonic_and_operand    varchar(255),
    comment                 varchar(255)
);