package com.thanhhandsome.pythonmaster.controller;

import com.thanhhandsome.pythonmaster.dto.response.ApiResponse;
import com.thanhhandsome.pythonmaster.service.excel.ExcelDataImporterService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/data")
@RequiredArgsConstructor
@Tag(name = "Data Import", description = "Nạp dữ liệu từ Excel")
public class DataImportController {
    private final ExcelDataImporterService excelDataImporterService;

    @PostMapping("/import-excel")
    public ResponseEntity<ApiResponse<Map<String, Object>>> importExcel(
            @RequestParam(required = false) String filePath) {

        String pathToUse = (filePath != null && !filePath.isBlank())
                ? filePath
                : "src/main/Data/Data_khach_hang_Cleaned.xlsx";

        File file = new File(pathToUse);
        if (!file.exists()) {
            file = new File("c:/Users/DELL/IdeaProjects/Project/pythonmaster/src/main/Data/Data_khach_hang_Cleaned.xlsx");
        }

        int count = excelDataImporterService.importDataFromExcel(file.getAbsolutePath());
        Map<String, Object> data = Map.of(
                "importedRows", count,
                "filePath", file.getAbsolutePath()
        );

        return ResponseEntity.ok(ApiResponse.success("Đã nạp thành công " + count + " bản ghi từ Excel!", data));
    }
}
