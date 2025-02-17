package com.dishcovery.project.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

import com.dishcovery.project.domain.CustomUser;
import com.dishcovery.project.domain.ImageData;
import com.dishcovery.project.domain.RecipeBoardStepVO;
import com.dishcovery.project.domain.RecipeBoardVO;
import com.dishcovery.project.domain.RecipeDetailVO;
import com.dishcovery.project.domain.RecipeIngredientsDetailVO;
import com.dishcovery.project.service.RecipeBoardService;
import com.dishcovery.project.util.FileUploadUtil;
import com.dishcovery.project.util.Pagination;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

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
            @RequestParam(value = "pageSize", defaultValue = "8") int pageSize,
            @RequestParam(value = "ingredientIds", required = false) String ingredientIdsStr,
            @RequestParam(value = "typeId", defaultValue = "1") Integer typeId,
            @RequestParam(value = "situationId", defaultValue = "1") Integer situationId,
            @RequestParam(value = "methodId", defaultValue = "1") Integer methodId,
            @RequestParam(value = "hashtag", required = false) String hashtag, // 추가
            @RequestParam(value = "sort", defaultValue = "latest") String sort,
            Model model) {
    	 
        // Pagination 설정
        Pagination pagination = new Pagination(pageNum, pageSize);
        pagination.setIngredientIdsFromString(ingredientIdsStr);
        pagination.setTypeId(typeId != null ? typeId : 1);
        pagination.setSituationId(situationId != null ? situationId : 1);
        pagination.setMethodId(methodId != null ? methodId : 1);
        pagination.setHashtag(hashtag); // 추가
        pagination.setSort(sort);

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
        model.addAttribute("selectedSort", sort);

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
        // 공통 레이아웃에 포함될 페이지 설정
        model.addAttribute("pageContent", "recipeboard/register.jsp");

        // 공통 레이아웃 반환
        return "layout";
    }

    @PostMapping("/register")
    public String registerRecipe(
        RecipeBoardVO recipeBoard,
        @RequestParam(value = "ingredientIds", required = false) List<Integer> ingredientIds,
        @RequestParam(value = "hashtags", required = false) String hashtags,
        @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail,
        @RequestParam(value = "stepDescription", required = false) List<String> stepDescriptions,
        @RequestPart(value = "stepImage", required = false) List<MultipartFile> stepImages,
        @RequestParam(value = "servings", required = false) String servings,
        @RequestParam(value = "time", required = false) String time,
        @RequestParam(value = "difficulty", required = false) String difficulty,
        @RequestParam(value = "stepOrder", required = false) List<Integer> stepOrders,
        @RequestParam(value = "ingredientName", required = false) List<String> ingredientNames,
        @RequestParam(value = "ingredientAmount", required = false) List<String> ingredientAmounts,
        @RequestParam(value = "ingredientUnit", required = false) List<String> ingredientUnits,
        @RequestParam(value = "ingredientNote", required = false) List<String> ingredientNotes
    ) throws IOException {

    	 Integer currentUserId = getCurrentUserId();
    	 log.info("Current User ID: " + currentUserId);	
    	 if (currentUserId != null) {
    	        recipeBoard.setMemberId(currentUserId);
    	        log.info("Setting recipeBoard.memberId to: " + currentUserId);
    	    }
        List<RecipeIngredientsDetailVO> ingredientDetails = new ArrayList<>();
        if (ingredientNames != null) {
            for (int i = 0; i < ingredientNames.size(); i++) {
                RecipeIngredientsDetailVO detail = new RecipeIngredientsDetailVO();
                detail.setIngredientName(ingredientNames.get(i));
                  if(ingredientAmounts != null && i < ingredientAmounts.size()){
                          detail.setIngredientAmount(ingredientAmounts.get(i));
                      }
                  if(ingredientUnits != null && i < ingredientUnits.size()){
                         detail.setIngredientUnit(ingredientUnits.get(i));
                      }
                   if(ingredientNotes != null && i < ingredientNotes.size()){
                        detail.setIngredientNote(ingredientNotes.get(i));
                     }
                ingredientDetails.add(detail);
                log.info("Ingredient Detail: " + detail);
            }
        }

        List<RecipeBoardStepVO> steps = new ArrayList<>();
        if (stepDescriptions != null && !stepDescriptions.isEmpty()) {
            for (int i = 0; i < stepDescriptions.size(); i++) {
                RecipeBoardStepVO step = new RecipeBoardStepVO();
                if (stepOrders != null && i < stepOrders.size()) {
                    Integer order = stepOrders.get(i);
                    step.setStepOrder(order == null ? i + 1 : order);
                } else {
                    step.setStepOrder(i + 1);
                }
                step.setStepDescription(stepDescriptions.get(i));
                if (stepImages != null && i < stepImages.size() && stepImages.get(i) != null && !stepImages.get(i).isEmpty()) {
                    // 파일 경로는 실제 저장되는 경로로 변경해야 합니다.
                    String stepImageUrl = FileUploadUtil.saveFile("C:/uploads", stepImages.get(i));
                    step.setStepImageUrl(stepImageUrl);
                } else {
                    step.setStepImageUrl(null);
                }
                steps.add(step);
                log.info("Step info in controller: " + step);
            }
        }

        log.info("RecipeBoard value before createRecipe method called: " + recipeBoard);
        if (servings != null) {
            recipeBoard.setServings(servings);
        }
        if (time != null) {
            recipeBoard.setTime(time);
        }
        if (difficulty != null) {
            recipeBoard.setDifficulty(difficulty);
        }
       
       
        
        recipeBoardService.createRecipe(recipeBoard, ingredientIds, hashtags, thumbnail, steps, ingredientDetails);
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
        model.addAttribute("steps", detail.getRecipeSteps()); 
        model.addAttribute("ingredientDetails", recipeBoardService.getRecipeIngredientsDetailsByRecipeId(recipeBoardId));
        // 공통 레이아웃에 포함될 페이지 설정
        model.addAttribute("pageContent", "recipeboard/detail.jsp");

        // 공통 레이아웃 반환
        return "layout";
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

        Integer currentUserId = getCurrentUserId();
        if (recipeBoard.getMemberId() != currentUserId) {
            return "redirect:/recipeboard/list";
        }
        
        model.addAttribute("recipeBoard", recipeBoard);
		model.addAttribute("selectedIngredientIds",
				recipeBoardService.getSelectedIngredientIdsByRecipeBoardId(recipeBoardId));
		model.addAttribute("typesList", recipeBoardService.getAllTypes());
		model.addAttribute("methodsList", recipeBoardService.getAllMethods());
		model.addAttribute("situationsList", recipeBoardService.getAllSituations());
		model.addAttribute("ingredientsList", recipeBoardService.getAllIngredients());
		model.addAttribute("hashtags", recipeBoardService.getHashtagsByRecipeBoardId(recipeBoardId));
		model.addAttribute("ingredientDetails",
				recipeBoardService.getRecipeIngredientsDetailsByRecipeId(recipeBoardId));
		  List<RecipeBoardStepVO> steps = recipeBoardService.getRecipeBoardStepsByBoardId(recipeBoardId);
		    String contextPath = "/project"; // 실제 contextPath로 변경
		    String uploadPath = contextPath + "/recipeboard/project/upload"; // URL 생성

		    for (RecipeBoardStepVO step : steps) {
		        if (step.getStepImageUrl() != null && !step.getStepImageUrl().startsWith("http")) {
		            step.setStepImageUrl(uploadPath + "/2025/02/10/" + step.getStepImageUrl()); 
		        }
		    }
		    model.addAttribute("steps",  recipeBoardService.getRecipeBoardStepsByBoardId(recipeBoardId)); // 스텝 정보 추가
		    model.addAttribute("ingredientDetails",
		            recipeBoardService.getRecipeIngredientsDetailsByRecipeId(recipeBoardId));
		return "recipeboard/update";
	}
    

    @PostMapping("/update")
    public String updateRecipe(
          RecipeBoardVO recipeBoard,
            @RequestParam(value = "ingredientIds", required = false) List<Integer> ingredientIds,
            @RequestParam(value = "hashtags", required = false) String hashtags,
            @RequestPart(value = "thumbnail", required = true) MultipartFile thumbnail,
             @RequestParam(value = "stepDescription", required = false) List<String> stepDescriptions,
            @RequestParam(value = "stepImage", required = false)  List<MultipartFile> stepImages,
             @RequestParam(value = "servings", required = false) String servings,
            @RequestParam(value = "time", required = false) String time,
             @RequestParam(value = "difficulty", required = false) String difficulty,
             @RequestParam(value = "stepOrder", required = false) List<Integer> stepOrders,
            @RequestParam(value = "deleteStepIds", required = false) List<Integer> deleteStepIds,
            @RequestParam(value="recipeIngredients", required = false, defaultValue = "[]") String recipeIngredientsJson
    ) throws IOException {
        try {
        	Integer currentUserId = getCurrentUserId();
            RecipeBoardVO existingBoard = recipeBoardService.getByRecipeBoardId(recipeBoard.getRecipeBoardId());

            if (existingBoard == null || existingBoard.getMemberId() != currentUserId) { // 기본형 비교
                return "redirect:/recipeboard/list";
            }
            ObjectMapper mapper = new ObjectMapper();
            List<RecipeIngredientsDetailVO> ingredientDetails = new ArrayList<>();
            if(recipeIngredientsJson != null && !recipeIngredientsJson.trim().isEmpty()){
                   ingredientDetails = mapper.readValue(recipeIngredientsJson, new TypeReference<List<RecipeIngredientsDetailVO>>(){});
            }

            List<RecipeBoardStepVO> steps = new ArrayList<>();
            if (stepDescriptions != null && !stepDescriptions.isEmpty()) {
                for (int i = 0; i < stepDescriptions.size(); i++) {
                    RecipeBoardStepVO step = new RecipeBoardStepVO();
                    if (stepOrders != null && i < stepOrders.size()) {
                        Integer order = stepOrders.get(i);
                        step.setStepOrder(order == null ? i + 1 : order);
                    } else {
                        step.setStepOrder(i + 1);
                    }
                    step.setStepDescription(stepDescriptions.get(i));
                    if (stepImages != null && i < stepImages.size() && stepImages.get(i) != null && !stepImages.get(i).isEmpty()) {
                        String stepImageUrl = FileUploadUtil.saveFile("C:/uploads", stepImages.get(i));
						step.setStepImageUrl(stepImageUrl);
                    } else {
                        step.setStepImageUrl(null);
                    }
                    steps.add(step);
                    log.info("step info in controller: " + step);
                }
            }

           log.info("RecipeBoard value before updateRecipe method called: " + recipeBoard);
                if(servings != null){
                     recipeBoard.setServings(servings);
               }
               if(time != null){
                    recipeBoard.setTime(time);
                }
              if(difficulty != null){
                   recipeBoard.setDifficulty(difficulty);
               }
            recipeBoardService.updateRecipe(0, recipeBoard, ingredientIds, hashtags, thumbnail, steps, deleteStepIds, ingredientDetails);
            return "redirect:/recipeboard/detail/" + recipeBoard.getRecipeBoardId();
        } catch (IllegalArgumentException e) {
            log.error("Error updating recipe: " + e.getMessage());
            return "redirect:/recipeboard/update/" + recipeBoard.getRecipeBoardId() + "?error=" + e.getMessage();
        }
    }

    @PostMapping("/delete/{recipeBoardId}")
    public String deleteRecipe(@PathVariable int recipeBoardId) {
    	Integer currentUserId = getCurrentUserId();
    	RecipeBoardVO existingBoard = recipeBoardService.getByRecipeBoardId(recipeBoardId);
    	if (existingBoard == null || existingBoard.getMemberId() != currentUserId) { // 기본형 비교
    	        return "redirect:/recipeboard/list";
    	    }
    	
        recipeBoardService.deleteRecipe(recipeBoardId);
        return "redirect:/recipeboard/list";
    }

    @GetMapping("/thumbnail/{recipeBoardId}")
    public ResponseEntity<byte[]> getThumbnail(@PathVariable int recipeBoardId) {
        Optional<ImageData> imageDataOptional = recipeBoardService.getThumbnailByRecipeBoardId(recipeBoardId);

        if (imageDataOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        ImageData imageData = imageDataOptional.get();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(imageData.getContentType())); // PNG, JPEG 자동 설정

        return ResponseEntity.ok()
                .headers(headers)
                .body(imageData.getData());
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
    
    private Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof CustomUser) {
            CustomUser customUser = (CustomUser) authentication.getPrincipal();
            return customUser.getMemberVO().getMemberId(); // CustomUser에서 memberId를 가져옴
        }
        return null; // 인증되지 않은 경우 null 반환
    }
    @GetMapping("/project/upload/{year}/{month}/{day}/{filename:.+}") // 정규 표현식 추가
    @ResponseBody
    public ResponseEntity<Resource> getImage(
            @PathVariable String year,
            @PathVariable String month,
            @PathVariable String day,
            @PathVariable String filename) {

        // 파일 경로를 구성합니다.
        String filePath = "C:/uploads/" + year + "/" + month + "/" + day + "/" + filename; // 실제 경로로 변경

        // 파일을 읽어서 응답으로 반환합니다.
        try {
            Resource file = new FileSystemResource(filePath);
            if (file.exists()) {
                String contentType = determineContentType(filename);
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType)) // 이미지 타입에 따라 변경
                        .body(file);
            } else {
                return ResponseEntity.notFound().build(); // 파일이 없으면 404 반환
            }
        } catch (Exception e) {
            // 오류 처리
            log.error("Error serving image: " + filePath, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private String determineContentType(String filename) {
        if (filename.toLowerCase().endsWith(".jpg") || filename.toLowerCase().endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (filename.toLowerCase().endsWith(".png")) {
            return "image/png";
        } else if (filename.toLowerCase().endsWith(".gif")) {
            return "image/gif";
        } else {
            return "application/octet-stream"; // 기본적으로 바이너리 파일로 처리
        }
    }
}
