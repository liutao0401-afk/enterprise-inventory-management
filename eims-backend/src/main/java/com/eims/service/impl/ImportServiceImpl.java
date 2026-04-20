package com.eims.service.impl;

import com.eims.entity.Material;
import com.eims.entity.Supplier;
import com.eims.entity.Requirement;
import com.eims.repository.MaterialRepository;
import com.eims.repository.SupplierRepository;
import com.eims.repository.RequirementRepository;
import com.eims.service.ImportService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileInputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
public class ImportServiceImpl implements ImportService {

    @Autowired
    private MaterialRepository materialRepository;
    @Autowired
    private SupplierRepository supplierRepository;
    @Autowired
    private RequirementRepository requirementRepository;

    @Override
    @Transactional
    public Map<String, Object> importMaterials(String filePath) {
        Map<String, Object> result = new HashMap<>();
        int success = 0, fail = 0;
        List<String> errors = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                try {
                    Row row = sheet.getRow(i);
                    if (row == null) continue;

                    Material material = new Material();
                    material.setCode(getCellValueAsString(row.getCell(0)));
                    material.setName(getCellValueAsString(row.getCell(1)));
                    material.setUnit(getCellValueAsString(row.getCell(2)));
                    material.setSpec(getCellValueAsString(row.getCell(3)));

                    if (row.getCell(4) != null) {
                        material.setSafetyStock(new BigDecimal(getCellValueAsString(row.getCell(4))));
                    }
                    if (row.getCell(5) != null) {
                        material.setMinStock(new BigDecimal(getCellValueAsString(row.getCell(5))));
                    }
                    if (row.getCell(6) != null) {
                        material.setPrice(new BigDecimal(getCellValueAsString(row.getCell(6))));
                    }

                    material.setStatus("ACTIVE");
                    material.setCreateTime(LocalDateTime.now());

                    materialRepository.insert(material);
                    success++;
                } catch (Exception e) {
                    fail++;
                    errors.add("第" + (i + 1) + "行: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("导入物资失败", e);
            throw new RuntimeException("导入文件失败: " + e.getMessage());
        }

        result.put("success", success);
        result.put("fail", fail);
        result.put("errors", errors);
        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> importSuppliers(String filePath) {
        Map<String, Object> result = new HashMap<>();
        int success = 0, fail = 0;
        List<String> errors = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                try {
                    Row row = sheet.getRow(i);
                    if (row == null) continue;

                    Supplier supplier = new Supplier();
                    supplier.setCode(getCellValueAsString(row.getCell(0)));
                    supplier.setName(getCellValueAsString(row.getCell(1)));
                    supplier.setContact(getCellValueAsString(row.getCell(2)));
                    supplier.setPhone(getCellValueAsString(row.getCell(3)));
                    supplier.setEmail(getCellValueAsString(row.getCell(4)));
                    supplier.setAddress(getCellValueAsString(row.getCell(5)));
                    supplier.setMainProduct(getCellValueAsString(row.getCell(6)));
                    supplier.setLevel(getCellValueAsString(row.getCell(7)));
                    supplier.setStatus("ACTIVE");
                    supplier.setCreateTime(LocalDateTime.now());

                    supplierRepository.insert(supplier);
                    success++;
                } catch (Exception e) {
                    fail++;
                    errors.add("第" + (i + 1) + "行: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("导入供应商失败", e);
            throw new RuntimeException("导入文件失败: " + e.getMessage());
        }

        result.put("success", success);
        result.put("fail", fail);
        result.put("errors", errors);
        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> importRequirements(String filePath) {
        Map<String, Object> result = new HashMap<>();
        int success = 0, fail = 0;
        List<String> errors = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                try {
                    Row row = sheet.getRow(i);
                    if (row == null) continue;

                    Requirement requirement = new Requirement();
                    requirement.setCode(generateRequirementCode());
                    requirement.setPurpose(getCellValueAsString(row.getCell(0)));
                    requirement.setQuantity(new BigDecimal(getCellValueAsString(row.getCell(1))));

                    if (row.getCell(2) != null) {
                        String dateStr = getCellValueAsString(row.getCell(2));
                        requirement.setExpectedDate(LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                    }

                    requirement.setDeptName(getCellValueAsString(row.getCell(3)));
                    requirement.setStatus("PENDING");
                    requirement.setCreateTime(LocalDateTime.now());

                    requirementRepository.insert(requirement);
                    success++;
                } catch (Exception e) {
                    fail++;
                    errors.add("第" + (i + 1) + "行: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("导入需求失败", e);
            throw new RuntimeException("导入文件失败: " + e.getMessage());
        }

        result.put("success", success);
        result.put("fail", fail);
        result.put("errors", errors);
        return result;
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            default -> "";
        };
    }

    private String generateRequirementCode() {
        String prefix = "PR-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        Long count = requirementRepository.selectCount(null) + 1;
        return prefix + "-" + String.format("%03d", count);
    }
}
