package com.assignment.dataset.service;

import com.assignment.dataset.entity.DatasetRecord;
import com.assignment.dataset.repository.DatasetRecordRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class DatasetService {

    @Autowired
    private DatasetRecordRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    public Map<String, Object> processGroupBy(String datasetName, String groupByField) {
        List<DatasetRecord> records = repository.findByDatasetName(datasetName);
        Map<String, List<Map<String, Object>>> groupedMap = new LinkedHashMap<>();

        for (DatasetRecord record : records) {
            try {
                Map<String, Object> jsonMap = objectMapper.readValue(
                        record.getRecordData(),
                        new TypeReference<Map<String, Object>>() {}
                );
                jsonMap.put("id", record.getId());

                Object groupValueObj = findValueIgnoreCase(jsonMap, groupByField);
                String groupKey = (groupValueObj != null) ? groupValueObj.toString() : "UNKNOWN";

                groupedMap.computeIfAbsent(groupKey, k -> new ArrayList<>()).add(jsonMap);
            } catch (Exception ignored) {
            }
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("groupedRecords", groupedMap);
        return response;
    }

    public Map<String, Object> processSortBy(String datasetName, String sortByField, String order) {
        List<DatasetRecord> records = repository.findByDatasetName(datasetName);
        List<Map<String, Object>> parsedList = new ArrayList<>();

        for (DatasetRecord record : records) {
            try {
                Map<String, Object> jsonMap = objectMapper.readValue(
                        record.getRecordData(),
                        new TypeReference<Map<String, Object>>() {}
                );
                jsonMap.put("id", record.getId());
                parsedList.add(jsonMap);
            } catch (Exception ignored) {
            }
        }

        parsedList.sort((map1, map2) -> {
            Object val1 = findValueIgnoreCase(map1, sortByField);
            Object val2 = findValueIgnoreCase(map2, sortByField);

            if (val1 == null && val2 == null) {
                return 0;
            }
            if (val1 == null) {
                return 1;
            }
            if (val2 == null) {
                return -1;
            }

            int comparison;
            if (val1 instanceof Number && val2 instanceof Number) {
                comparison = Double.compare(((Number) val1).doubleValue(), ((Number) val2).doubleValue());
            } else {
                comparison = val1.toString().compareToIgnoreCase(val2.toString());
            }

            return "desc".equalsIgnoreCase(order) ? -comparison : comparison;
        });

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("sortedRecords", parsedList);
        return response;
    }

    private Object findValueIgnoreCase(Map<String, Object> map, String targetKey) {
        if (map == null || targetKey == null) {
            return null;
        }
        if (map.containsKey(targetKey)) {
            return map.get(targetKey);
        }
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getKey() != null && entry.getKey().equalsIgnoreCase(targetKey)) {
                return entry.getValue();
            }
        }
        return null;
    }
}