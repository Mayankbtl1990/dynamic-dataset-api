package com.assignment.dataset.controller;

import com.assignment.dataset.entity.DatasetRecord;
import com.assignment.dataset.repository.DatasetRecordRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dataset")
public class DatasetController {

    @Autowired
    private DatasetRecordRepository repository;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private com.assignment.dataset.service.DatasetService datasetService;

    @PostMapping("/{datasetName}/record")
    public ResponseEntity<Map<String, Object>> insertRecord(
            @PathVariable String datasetName,
            @RequestBody Map<String, Object> requestBody) {
        
        try {
            String jsonString = objectMapper.writeValueAsString(requestBody);

            DatasetRecord record = new DatasetRecord();
            record.setDatasetName(datasetName);
            record.setRecordData(jsonString);

            DatasetRecord savedRecord = repository.save(record);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Record added successfully");
            response.put("dataset", datasetName);
            response.put("recordId", savedRecord.getId());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to save record: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    @GetMapping("/{datasetName}/query")
    public ResponseEntity<Map<String, Object>> queryDataset(
            @PathVariable String datasetName,
            @RequestParam(required = false) String groupBy,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "asc") String order) {

        try {
            if (groupBy != null && !groupBy.isEmpty()) {
                Map<String, Object> result = datasetService.processGroupBy(datasetName, groupBy);
                return ResponseEntity.ok(result);
            } else if (sortBy != null && !sortBy.isEmpty()) {
                Map<String, Object> result = datasetService.processSortBy(datasetName, sortBy, order);
                return ResponseEntity.ok(result);
            } else {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Please provide either 'groupBy' or 'sortBy' query parameter.");
                return ResponseEntity.badRequest().body(error);
            }
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Query execution failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}