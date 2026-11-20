package com.assignment.dataset;

import com.assignment.dataset.entity.DatasetRecord;
import com.assignment.dataset.repository.DatasetRecordRepository;
import com.assignment.dataset.service.DatasetService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DatasetServiceTest {

    @Mock
    private DatasetRecordRepository repository;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private DatasetService datasetService;

    @Test
    void shouldGroupRecordsByDepartment() {
        DatasetRecord r1 = new DatasetRecord();
        r1.setId(1L);
        r1.setDatasetName("employee_dataset");
        r1.setRecordData("{\"name\":\"John Doe\",\"age\":30,\"department\":\"Engineering\"}");

        DatasetRecord r2 = new DatasetRecord();
        r2.setId(2L);
        r2.setDatasetName("employee_dataset");
        r2.setRecordData("{\"name\":\"Alice Brown\",\"age\":28,\"department\":\"Marketing\"}");

        when(repository.findByDatasetName("employee_dataset")).thenReturn(List.of(r1, r2));

        Map<String, Object> result = datasetService.processGroupBy("employee_dataset", "department");

        assertNotNull(result.get("groupedRecords"));
        Map<?, ?> grouped = (Map<?, ?>) result.get("groupedRecords");
        assertEquals(2, grouped.size());
        assertNotNull(grouped.get("Engineering"));
        assertNotNull(grouped.get("Marketing"));
    }
}