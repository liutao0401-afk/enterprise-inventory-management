package com.eims.controller;

import com.eims.dto.ApiResponse;
import com.eims.service.ImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Map;

@RestController
@RequestMapping("/import")
public class ImportController {

    @Autowired
    private ImportService importService;

    @PostMapping("/materials")
    public ApiResponse<Map<String, Object>> importMaterials(@RequestParam("file") MultipartFile file) {
        try {
            File tempFile = File.createTempFile("import_", ".xlsx");
            file.transferTo(tempFile);
            Map<String, Object> result = importService.importMaterials(tempFile.getAbsolutePath());
            tempFile.delete();
            return ApiResponse.success("导入完成", result);
        } catch (Exception e) {
            return ApiResponse.error("导入失败: " + e.getMessage());
        }
    }

    @PostMapping("/suppliers")
    public ApiResponse<Map<String, Object>> importSuppliers(@RequestParam("file") MultipartFile file) {
        try {
            File tempFile = File.createTempFile("import_", ".xlsx");
            file.transferTo(tempFile);
            Map<String, Object> result = importService.importSuppliers(tempFile.getAbsolutePath());
            tempFile.delete();
            return ApiResponse.success("导入完成", result);
        } catch (Exception e) {
            return ApiResponse.error("导入失败: " + e.getMessage());
        }
    }

    @PostMapping("/requirements")
    public ApiResponse<Map<String, Object>> importRequirements(@RequestParam("file") MultipartFile file) {
        try {
            File tempFile = File.createTempFile("import_", ".xlsx");
            file.transferTo(tempFile);
            Map<String, Object> result = importService.importRequirements(tempFile.getAbsolutePath());
            tempFile.delete();
            return ApiResponse.success("导入完成", result);
        } catch (Exception e) {
            return ApiResponse.error("导入失败: " + e.getMessage());
        }
    }
}
