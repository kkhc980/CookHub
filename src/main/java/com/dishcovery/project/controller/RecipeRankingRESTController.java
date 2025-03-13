package com.dishcovery.project.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dishcovery.project.domain.RecipeBoardVO;
import com.dishcovery.project.domain.RecipeRankingVO;
import com.dishcovery.project.service.RecipeBoardService;
import com.dishcovery.project.service.RecipeRankingService;

import lombok.extern.log4j.Log4j;

@RequestMapping("/rankingboard")
@RestController
@Log4j
public class RecipeRankingRESTController {

    @Autowired
    private RecipeRankingService rankingService;

    @Autowired
    private RecipeBoardService recipeBoardService;

    /**
     * 특정 유형의 랭킹 데이터를 JSON 형식으로 반환
     * @param type 랭킹 유형 (DAILY, WEEKLY, MONTHLY, TOTAL_DAILY, TOTAL_WEEKLY, TOTAL_MONTHLY)
     * @return JSON 데이터
     */
    @GetMapping("/rankings")
    public ResponseEntity<List<Map<String, Object>>> getRankings(@RequestParam(value = "type", required = false, defaultValue = "DAILY") String type) {
        log.info("Fetching rankings for type: " + type);

        List<RecipeRankingVO> rankings;

        if (type.startsWith("TOTAL_")) {
            rankings = rankingService.getTotalRankings(type.replace("TOTAL_", "").toLowerCase());
        } else {
            rankings = rankingService.getRankings(type.toUpperCase());
        }

        List<Map<String, Object>> result = rankings.stream().map(rank -> {
            log.debug("Processing rank: " + rank.getRankPosition() + ", RecipeBoard ID: " + rank.getRecipeBoardId());

            if (rank.getRecipeBoardId() == null || rank.getRecipeBoardId() == 0) {  
                log.warn("❌ RecipeBoard ID is null or 0 for rank position: " + rank.getRankPosition());
                return null;
            }

            RecipeBoardVO recipe = recipeBoardService.getByRecipeBoardId(rank.getRecipeBoardId());

            if (recipe == null) {
                log.warn("❌ RecipeBoard not found for ID: " + rank.getRecipeBoardId());
                return null;
            } else {
                log.debug("✅ Found RecipeBoard: " + recipe.getRecipeBoardTitle() + " (ID: " + recipe.getRecipeBoardId() + ")");
            }

            Map<String, Object> map = new HashMap<>();
            map.put("recipeBoardId", recipe.getRecipeBoardId());
            map.put("recipeBoardTitle", recipe.getRecipeBoardTitle());
            map.put("thumbnailPath", recipe.getThumbnailPath());
            map.put("recipeBoardViewCount", recipe.getViewCount());

            if (type.startsWith("TOTAL_")) {
                map.put("totalScore", Optional.ofNullable(rank.getTotalScore()).orElse(0.0));
            } else {
                map.put("viewCount", rank.getViewCount());
            }

            return map;
        }).filter(Objects::nonNull).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }



}
