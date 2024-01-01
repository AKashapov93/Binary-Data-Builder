package ru.example.binarydatabuilder.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.example.binarydatabuilder.entity.DataTable;

@Repository
public interface DataTableRepository extends JpaRepository<DataTable, Long> {
}
