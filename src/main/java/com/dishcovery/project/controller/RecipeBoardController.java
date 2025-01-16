package com.dishcovery.project.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.dishcovery.project.domain.RecipeBoardVO;
import com.dishcovery.project.domain.RecipeDetailVO;
import com.dishcovery.project.service.RecipeBoardService;
import com.dishcovery.project.util.Pagination;

import lombok.extern.log4j.Log4j;

@Controller
@RequestMapping("/recipeboard")
@Log4j
public class RecipeBoardController {

    @Autowired
    RecipeBoardService recipeBoardService;

    @GetMapping("/list")
    public String list(
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "4") int pageSize,
            @RequestParam(value = "ingredientIds", required = false) String ingredientIdsStr,
            @RequestParam(value = "typeId", defaultValue = "1") Integer typeId,
            @RequestParam(value = "situationId", defaultValue = "1") Integer situationId,
            @RequestParam(value = "methodId", defaultValue = "1") Integer methodId,
            @RequestParam(value = "hashtag", required = false) String hashtag, // 추가
            Model model) {
    	 
        // Pagination 설정
        Pagination pagination = new Pagination(pageNum, pageSize);
        pagination.setIngredientIdsFromString(ingredientIdsStr);
        pagination.setTypeId(typeId != null ? typeId : 1);
        pagination.setSituationId(situationId != null ? situationId : 1);
        pagination.setMethodId(methodId != null ? methodId : 1);
        pagination.setHashtag(hashtag); // 추가

        // RecipeBoard 목록 및 관련 데이터 가져오기
        Map<String, Object> result = recipeBoardService.getRecipeBoardListWithFilters(
                recipeBoardService.preprocessPagination(pagination));
        
        // 모델에 데이터 추가
        model.addAllAttributes(result);
        model.addAttribute("selectedTypeId", typeId);
        model.addAttribute("selectedSituationId", situationId);
        model.addAttribute("selectedMethodId", methodId);
        model.addAttribute("ingredientIdsStr", pagination.getIngredientIdsAsString());
        model.addAttribute("selectedPageNum", pageNum);
        model.addAttribute("selectedIngredientIds", ingredientIdsStr != null ? Arrays.asList(ingredientIdsStr.split(",")) : List.of("1"));
        model.addAttribute("searchHashtag", hashtag); // 추가

        // 공통 레이아웃에 포함될 페이지 설정
        model.addAttribute("pageContent", "recipeboard/list.jsp");

        // 공통 레이아웃 반환
        return "layout";
    }


    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("typesList", recipeBoardService.getAllTypes());
        model.addAttribute("methodsList", recipeBoardService.getAllMethods());
        model.addAttribute("situationsList", recipeBoardService.getAllSituations());
        model.addAttribute("ingredientsList", recipeBoardService.getAllIngredients());
        return "recipeboard/register";
    }

    @PostMapping("/register")
    public String registerRecipe(
            RecipeBoardVO recipeBoard,
            @RequestParam(value = "ingredientIds", required = false) List<Integer> ingredientIds,
            @RequestParam(value = "hashtags", required = false) String hashtags,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail) {
    	log.info("POST 요청 도달 - Recipe 등록");
    	recipeBoardService.createRecipe(recipeBoard, ingredientIds, hashtags, thumbnail);
        return "redirect:/recipeboard/list";
    }

    @GetMapping("/detail/{recipeBoardId}")
    public String getRecipeDetail(@PathVariable int recipeBoardId, Model model, HttpServletRequest request) {
        RecipeDetailVO detail = recipeBoardService.getRecipeDetailById(recipeBoardId);

        if (detail == null) {
            return "redirect:/recipeboard/list";
        }
        
        // 클라이언트 IP 주소 가져오기
        String ipAddress = getClientIp(request);

        // 조회수 증가 처리 (IP 기반 하루 한 번 제한)
        recipeBoardService.increaseViewCountIfEligible((int) recipeBoardId, ipAddress);

        model.addAttribute("recipeBoard", detail.getRecipeBoard());
        model.addAttribute("typeName", detail.getTypeName());
        model.addAttribute("methodName", detail.getMethodName());
        model.addAttribute("situationName", detail.getSituationName());
        model.addAttribute("ingredients", detail.getIngredients());
        model.addAttribute("hashtags", detail.getHashtags());
        return "recipeboard/detail";
    }

    private String getClientIp(HttpServletRequest request) {
        // 1. 프록시나 로드 밸런서에서 전달된 헤더 확인
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            // X-Forwarded-For 헤더에 여러 IP가 포함될 수 있으므로, 첫 번째 IP를 가져옵니다
            return ip.split(",")[0].trim();
        }

        // 2. 직접 연결된 클라이언트의 IP 가져오기
        ip = request.getRemoteAddr();

        // 3. IPv6 로컬 호스트 주소를 IPv4로 변환 (테스트용)
        if ("0:0:0:0:0:0:0:1".equals(ip)) {
            ip = "192.168.0.139"; // 개발 PC의 실제 IP로 대체 (테스트용)
        }

        return ip;
    }
    
    @GetMapping("/update/{recipeBoardId}")
    public String updateForm(@PathVariable int recipeBoardId, Model model) {
        RecipeBoardVO recipeBoard = recipeBoardService.getByRecipeBoardId(recipeBoardId);
        if (recipeBoard == null) {
            return "redirect:/recipeboard/list";
        }

        model.addAttribute("recipeBoard", recipeBoard);
        model.addAttribute("selectedIngredientIds", recipeBoardService.getSelectedIngredientIdsByRecipeBoardId(recipeBoardId));
        model.addAttribute("typesList", recipeBoardService.getAllTypes());
        model.addAttribute("methodsList", recipeBoardService.getAllMethods());
        model.addAttribute("situationsList", recipeBoardService.getAllSituations());
        model.addAttribute("ingredientsList", recipeBoardService.getAllIngredients());
        model.addAttribute("hashtags", recipeBoardService.getHashtagsByRecipeBoardId(recipeBoardId));

        return "recipeboard/update";
    }

    @PostMapping("/update")
    public String updateRecipe(RecipeBoardVO recipeBoard,
                               @RequestParam(value = "ingredientIds", required = false) List<Integer> ingredientIds,
                               @RequestParam(value = "hashtags", required = false) String hashtags,
                               @RequestPart(value = "thumbnail", required = true) MultipartFile thumbnail) {
        try {
        	recipeBoardService.updateRecipe(recipeBoard, ingredientIds, hashtags, thumbnail);
            return "redirect:/recipeboard/detail/" + recipeBoard.getRecipeBoardId();
        } catch (IllegalArgumentException e) {
            log.error("Error updating recipe: " + e.getMessage());
            return "redirect:/recipeboard/update/" + recipeBoard.getRecipeBoardId() + "?error=" + e.getMessage();
        }
    }

    @PostMapping("/delete/{recipeBoardId}")
    public String deleteRecipe(@PathVariable int recipeBoardId) {
        recipeBoardService.deleteRecipe(recipeBoardId);
        return "redirect:/recipeboard/list";
    }

    @GetMapping("/thumbnail/{recipeBoardId}")
    public ResponseEntity<?> getThumbnail(@PathVariable int recipeBoardId) {
        return recipeBoardService.getThumbnailByRecipeBoardId(recipeBoardId)
                .map(resource -> ResponseEntity.ok(resource))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @GetMapping("/csrf-debug")
    @ResponseBody
    public Map<String, String> debugCsrf(HttpServletRequest request) {
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        Map<String, String> csrfInfo = new HashMap<>();
        if (csrfToken != null) {
            csrfInfo.put("token", csrfToken.getToken());
            csrfInfo.put("headerName", csrfToken.getHeaderName());
            csrfInfo.put("parameterName", csrfToken.getParameterName());
        } else {
            csrfInfo.put("error", "CSRF 토큰을 생성할 수 없습니다.");
        }
        return csrfInfo;
    }
}
