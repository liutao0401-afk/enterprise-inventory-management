package com.eims.service;

import java.util.Map;

public interface ImportService {
    Map<String, Object> importMaterials(String filePath);
    Map<String, Object> importSuppliers(String filePath);
    Map<String, Object> importRequirements(String filePath);
}
