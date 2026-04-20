package com.eims.service;

import com.eims.config.AiConfig;
import com.eims.entity.Requirement;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Service
public class AiDuplicateCheckService {

    private final AiConfig aiConfig;

    public AiDuplicateCheckService(AiConfig aiConfig) {
        this.aiConfig = aiConfig;
    }

    @Data
    public static class DuplicateCheckRequest {
        private Long materialId;
        private String materialName;
        private String spec;
        private String purpose;
        private BigDecimal quantity;
        private List<Requirement> historyRequirements;
    }

    @Data
    public static class DuplicateCheckResult {
        private boolean isDuplicate;
        private double similarityScore;
        private List<SimilarItem> similarItems;
        private String reason;

        @Data
        public static class SimilarItem {
            private Long id;
            private String code;
            private String materialName;
            private BigDecimal quantity;
            private String status;
            private String reason;
        }
    }

    /**
     * 使用AI检测重复采购
     */
    public DuplicateCheckResult checkWithAI(DuplicateCheckRequest request) {
        if (!aiConfig.isEnabled()) {
            return checkWithLogic(request);
        }

        try {
            // 调用AI API
            String prompt = buildPrompt(request);
            String aiResponse = callAiApi(prompt);
            return parseAiResponse(aiResponse, request);
        } catch (Exception e) {
            log.error("AI检测失败，回退到逻辑检测", e);
            return checkWithLogic(request);
        }
    }

    /**
     * 使用系统逻辑检测重复采购
     */
    public DuplicateCheckResult checkWithLogic(DuplicateCheckRequest request) {
        DuplicateCheckResult result = new DuplicateCheckResult();
        result.setSimilarItems(new ArrayList<>());

        if (request.getHistoryRequirements() == null || request.getHistoryRequirements().isEmpty()) {
            result.setDuplicate(false);
            result.setReason("30天内无相似采购记录");
            result.setSimilarityScore(0);
            return result;
        }

        // 精确匹配：物资ID相同
        List<DuplicateCheckResult.SimilarItem> exactMatches = new ArrayList<>();
        // 模糊匹配：名称相似
        List<DuplicateCheckResult.SimilarItem> fuzzyMatches = new ArrayList<>();

        for (Requirement req : request.getHistoryRequirements()) {
            if (req.getMaterialId().equals(request.getMaterialId())) {
                // 精确匹配
                DuplicateCheckResult.SimilarItem item = new DuplicateCheckResult.SimilarItem();
                item.setId(req.getId());
                item.setCode(req.getCode());
                item.setMaterialName(req.getMaterialName());
                item.setQuantity(req.getQuantity());
                item.setStatus(req.getStatus());
                item.setReason("物资编码完全相同");
                exactMatches.add(item);
            } else if (isFuzzyMatch(request.getMaterialName(), req.getMaterialName())) {
                // 模糊匹配
                DuplicateCheckResult.SimilarItem item = new DuplicateCheckResult.SimilarItem();
                item.setId(req.getId());
                item.setCode(req.getCode());
                item.setMaterialName(req.getMaterialName());
                item.setQuantity(req.getQuantity());
                item.setStatus(req.getStatus());
                item.setReason("物资名称相似");
                fuzzyMatches.add(item);
            }
        }

        // 合并结果
        List<DuplicateCheckResult.SimilarItem> allMatches = new ArrayList<>();
        allMatches.addAll(exactMatches);
        allMatches.addAll(fuzzyMatches);
        result.setSimilarItems(allMatches);

        if (!exactMatches.isEmpty()) {
            result.setDuplicate(true);
            result.setSimilarityScore(1.0);
            result.setReason("发现" + exactMatches.size() + "条完全相同的采购记录");
        } else if (!fuzzyMatches.isEmpty()) {
            result.setDuplicate(true);
            result.setSimilarityScore(0.7);
            result.setReason("发现" + fuzzyMatches.size() + "条相似的采购记录");
        } else {
            result.setDuplicate(false);
            result.setSimilarityScore(0);
            result.setReason("未发现重复采购");
        }

        return result;
    }

    private String buildPrompt(DuplicateCheckRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append("你是企业物资管理专家。判断以下采购需求是否重复。\n\n");
        sb.append("新需求：\n");
        sb.append("- 物资名称：").append(request.getMaterialName() != null ? request.getMaterialName() : "").append("\n");
        sb.append("- 规格型号：").append(request.getSpec() != null ? request.getSpec() : "").append("\n");
        sb.append("- 采购用途：").append(request.getPurpose() != null ? request.getPurpose() : "").append("\n");
        sb.append("- 需求数量：").append(request.getQuantity()).append("\n\n");

        sb.append("30天内的历史需求：\n");
        if (request.getHistoryRequirements() != null) {
            for (int i = 0; i < request.getHistoryRequirements().size(); i++) {
                Requirement r = request.getHistoryRequirements().get(i);
                sb.append(i + 1).append(". ");
                sb.append("名称：").append(r.getMaterialName() != null ? r.getMaterialName() : "");
                sb.append("，数量：").append(r.getQuantity());
                sb.append("，状态：").append(r.getStatus());
                sb.append("\n");
            }
        }

        sb.append("\n请以JSON格式返回检测结果：\n");
        sb.append("{\n");
        sb.append("  \"is_duplicate\": true/false,\n");
        sb.append("  \"similarity_score\": 0.0-1.0,\n");
        sb.append("  \"similar_items\": [{\"name\": \"xxx\", \"reason\": \"原因\"}],\n");
        sb.append("  \"reason\": \"判断理由\"\n");
        sb.append("}");

        return sb.toString();
    }

    private String callAiApi(String prompt) {
        // TODO: 实现实际的AI API调用
        // 这里返回模拟结果，实际使用时替换为真实的API调用
        log.info("调用AI API，Prompt: {}", prompt);
        return "{\"is_duplicate\": false, \"similarity_score\": 0, \"similar_items\": [], \"reason\": \"未发现重复\"}";
    }

    private DuplicateCheckResult parseAiResponse(String aiResponse, DuplicateCheckRequest request) {
        DuplicateCheckResult result = new DuplicateCheckResult();
        result.setSimilarItems(new ArrayList<>());

        try {
            // 简单的JSON解析，实际使用时可用Jackson
            if (aiResponse.contains("\"is_duplicate\": true")) {
                result.setDuplicate(true);
            } else {
                result.setDuplicate(false);
            }

            if (aiResponse.contains("\"similarity_score\":")) {
                String score = aiResponse.substring(aiResponse.indexOf("\"similarity_score\":") + 18);
                score = score.substring(0, score.indexOf(",")).trim();
                result.setSimilarityScore(Double.parseDouble(score));
            }

            if (aiResponse.contains("\"reason\":")) {
                String reason = aiResponse.substring(aiResponse.indexOf("\"reason\":") + 10);
                if (reason.startsWith("\"")) {
                    reason = reason.substring(1, reason.indexOf("\"", 1));
                }
                result.setReason(reason);
            }
        } catch (Exception e) {
            log.error("解析AI响应失败", e);
            return checkWithLogic(request);
        }

        return result;
    }

    /**
     * 判断两个名称是否相似（简单的模糊匹配）
     */
    private boolean isFuzzyMatch(String name1, String name2) {
        if (name1 == null || name2 == null) return false;

        name1 = name1.toLowerCase().replaceAll("[^a-z0-9]", "");
        name2 = name2.toLowerCase().replaceAll("[^a-z0-9]", "");

        // 计算相似度
        int distance = levenshteinDistance(name1, name2);
        int maxLen = Math.max(name1.length(), name2.length());
        double similarity = 1.0 - (double) distance / maxLen;

        return similarity >= 0.7;
    }

    /**
     * 计算编辑距离
     */
    private int levenshteinDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];

        for (int i = 0; i <= s1.length(); i++) dp[i][0] = i;
        for (int j = 0; j <= s2.length(); j++) dp[0][j] = j;

        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(dp[i - 1][j - 1], Math.min(dp[i - 1][j], dp[i][j - 1]));
                }
            }
        }

        return dp[s1.length()][s2.length()];
    }
}
