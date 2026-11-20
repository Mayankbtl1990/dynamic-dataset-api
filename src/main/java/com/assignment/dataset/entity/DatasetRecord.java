package com.assignment.dataset.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "dataset_records")
@Data
public class DatasetRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String datasetName;

    @Column(columnDefinition = "TEXT")
    private String recordData;
}