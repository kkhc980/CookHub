package com.dishcovery.project.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.dishcovery.project.domain.CustomUser;
import com.dishcovery.project.domain.ImageData;
import com.dishcovery.project.domain.RecipeBoardDTO;
import com.dishcovery.project.domain.RecipeBoardVO;
import com.dishcovery.project.domain.RecipeDetailVO;
import com.dishcovery.project.domain.RecipeRegisterRequest;
import com.dishcovery.project.domain.RecipeUpdateRequest;
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
    public String list(@RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                       @RequestParam(value = "pageSize", defaultValue = "8") int pageSize,
                       @RequestParam(value = "ingredientIds", required = false) String ingredientIdsStr,
                       @RequestParam(value = "typeId", defaultValue = "1") Integer typeId,
                       @RequestParam(value = "situationId", defaultValue = "1") Integer situationId,
                       @RequestParam(value = "methodId", defaultValue = "1") Integer methodId,
                       @RequestParam(value = "hashtag", required = false) String hashtag, // 추가
                       @RequestParam(value = "sort", defaultValue = "latest") String sort, Model model) {

        // Pagination 설정
        Pagination pagination = new Pagination(pageNum, pageSize);
        pagination.setIngredientIdsFromString(ingredientIdsStr);
        pagination.setTypeId(typeId != null ? typeId : 1);
        pagination.setSituationId(situationId != null ? situationId : 1);
        pagination.setMethodId(methodId != null ? methodId : 1);
        pagination.setHashtag(hashtag); // 추가
        pagination.setSort(sort);

        // RecipeBoard 목록 및 관련 데이터 가져오기
        Map<String, Object> result = recipeBoardService
                .getRecipeBoardListWithFilters(recipeBoardService.preprocessPagination(pagination));

        // 모델에 데이터 추가
        model.addAllAttributes(result);
        model.addAttribute("selectedTypeId", typeId);
        model.addAttribute("selectedSituationId", situationId);
        model.addAttribute("selectedMethodId", methodId);
        model.addAttribute("ingredientIdsStr", pagination.getIngredientIdsAsString());
        model.addAttribute("selectedPageNum", pageNum);
        model.addAttribute("selectedIngredientIds",
                ingredientIdsStr != null ? Arrays.asList(ingredientIdsStr.split(",")) : List.of("1"));
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
        model.addAttribute("pageContent", "recipeboard/register.jsp");

        // 공통 레이아웃 반환
        return "layout";
    }

    @PostMapping("/register")
    public String registerRecipe(@ModelAttribute @Valid RecipeRegisterRequest request,
                                 BindingResult bindingResult,
                                 Model model) {
        log.info("registerRecipe() called with request: " + request);
        if (bindingResult.hasErrors()) {
            log.warn("Validation errors: " + bindingResult.getAllErrors());
            model.addAttribute("errors", bindingResult.getAllErrors());
            // 필요한 attribute를 다시 model에 담아준다 (typesList, methodsList, situationsList, ingredientsList)
            model.addAttribute("typesList", recipeBoardService.getAllTypes());
            model.addAttribute("methodsList", recipeBoardService.getAllMethods());
            model.addAttribute("situationsList", recipeBoardService.getAllSituations());
            model.addAttribute("ingredientsList", recipeBoardService.getAllIngredients());
            return "recipeboard/register"; // 폼 페이지로 다시 이동
        }

        try {
            Integer currentUserId = getCurrentUserId(); // 현재 사용자 ID 가져오기
            if (currentUserId == null) {
                model.addAttribute("errorMessage", "로그인이 필요합니다.");
                return "redirect:/login"; // 로그인 페이지로 리다이렉트
            }
            request.setMemberId(currentUserId); // DTO에 memberId 설정
            recipeBoardService.createRecipe(request);
            log.info("Recipe created successfully.");
            return "redirect:/recipeboard/list"; // 성공 시 목록 페이지로 리다이렉트
        } catch (Exception e) {
            log.error("Error creating recipe: " + e.getMessage(), e);
            model.addAttribute("errorMessage", "레시피 등록에 실패했습니다.");
            return "recipeboard/register"; // 등록 폼으로 다시 이동
        }
    }
    @GetMapping("/detail/{recipeBoardId}")
    public String getRecipeDetail(@PathVariable int recipeBoardId, Model model, HttpServletRequest request) {
        log.info("getRecipeDetail() called with recipeBoardId: " + recipeBoardId);
        RecipeBoardDTO detail = recipeBoardService.getRecipeDetailById(recipeBoardId);

        if (detail == null) {
            log.warn("Recipe not found with id: " + recipeBoardId);
            return "redirect:/recipeboard/list";
        }

        // 클라이언트 IP 주소 가져오기
        String ipAddress = getClientIp(request);

        // 조회수 증가 처리 (IP 기반 하루 한 번 제한)
        recipeBoardService.increaseViewCountIfEligible(recipeBoardId, ipAddress);

        model.addAttribute("recipeBoard", detail);
        model.addAttribute("pageContent", "recipeboard/detail.jsp");

        // 공통 레이아웃 반환
        return "layout";
    }

    @GetMapping("/update/{recipeBoardId}")
    public String updateForm(@PathVariable int recipeBoardId, Model model) {
        log.info("updateForm() called with recipeBoardId: " + recipeBoardId);
        RecipeDetailVO recipeDetailVO = recipeBoardService.getRecipeDetailForUpdate(recipeBoardId);
        if (recipeDetailVO == null) {
            log.warn("Recipe not found with id: " + recipeBoardId);
            return "redirect:/recipeboard/list";
        }

        Integer currentUserId = getCurrentUserId();
        if (recipeDetailVO.getRecipeBoard().getMemberId() != currentUserId) {
            log.warn("Unauthorized access to recipe id: " + recipeBoardId + " by user: " + currentUserId);
            return "redirect:/recipeboard/list";
        }

        model.addAttribute("recipeBoard", recipeDetailVO.getRecipeBoard());
        model.addAttribute("typesList", recipeBoardService.getAllTypes());
        model.addAttribute("methodsList", recipeBoardService.getAllMethods());
        model.addAttribute("situationsList", recipeBoardService.getAllSituations());
        model.addAttribute("ingredientsList", recipeBoardService.getAllIngredients());
        model.addAttribute("ingredientDetails", recipeDetailVO.getIngredientDetails());  // ingredientDetails 추가
        model.addAttribute("steps", recipeDetailVO.getRecipeSteps()); // steps 추가
        model.addAttribute("recipeDetailVO", recipeDetailVO); // recipeDetailVO 추가

        model.addAttribute("pageContent", "recipeboard/update.jsp");
        return "layout";
    }

    @PostMapping("/update")
    public String updateRecipe(@ModelAttribute @Valid RecipeUpdateRequest request, BindingResult bindingResult, Model model) {
        log.info("updateRecipe() called with request: " + request);
        if (bindingResult.hasErrors()) {
            log.warn("Validation errors: " + bindingResult.getAllErrors());
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "recipeboard/update"; // 수정 폼으로 다시 이동
        }

        try {
            recipeBoardService.updateRecipe(request);
            log.info("Recipe updated successfully with id: " + request.getRecipeBoardId());
            return "redirect:/recipeboard/detail/" + request.getRecipeBoardId();
        } catch (Exception e) {
            log.error("Error updating recipe: " + e.getMessage(), e);
            model.addAttribute("errorMessage", "레시피 수정 중 오류가 발생했습니다.");
            return "recipeboard/update"; // 수정 폼으로 다시 이동
        }
    }

    @PostMapping("/delete/{recipeBoardId}")
    public String deleteRecipe(@PathVariable int recipeBoardId) {
        log.info("deleteRecipe() called with recipeBoardId: " + recipeBoardId);
        Integer currentUserId = getCurrentUserId();
        RecipeBoardVO existingBoard = recipeBoardService.getByRecipeBoardId(recipeBoardId);
        if (existingBoard == null || existingBoard.getMemberId() != currentUserId) {
            log.warn("Unauthorized access to delete recipe id: " + recipeBoardId + " by user: " + currentUserId);
            return "redirect:/recipeboard/list";
        }

        recipeBoardService.deleteRecipe(recipeBoardId);
        log.info("Recipe deleted successfully with id: " + recipeBoardId);
        return "redirect:/recipeboard/list";
    }

    @GetMapping("/thumbnail/{recipeBoardId}")
    public ResponseEntity<byte[]> getThumbnail(@PathVariable int recipeBoardId) {
        log.info("getThumbnail() called with recipeBoardId: " + recipeBoardId);
        Optional<ImageData> imageDataOptional = recipeBoardService.getThumbnailByRecipeBoardId(recipeBoardId);

        if (imageDataOptional.isEmpty()) {
            log.warn("Thumbnail not found for recipe id: " + recipeBoardId);
            return ResponseEntity.notFound().build();
        }

        ImageData imageData = imageDataOptional.get();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(imageData.getContentType())); // PNG, JPEG 자동 설정

        return ResponseEntity.ok().headers(headers).body(imageData.getData());
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    private Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof CustomUser) {
            CustomUser customUser = (CustomUser) authentication.getPrincipal();
            return customUser.getMemberVO().getMemberId(); // CustomUser에서 memberId를 가져옴
        }
        return null; // 인증되지 않은 경우 null 반환
    }

    @GetMapping("/project/upload/{year}/{month}/{day}/{filename:.+}") // 정규 표현식 추가
    @ResponseBody
    public ResponseEntity<Resource> getImage(@PathVariable String year, @PathVariable String month,
                                              @PathVariable String day, @PathVariable String filename) {

        // 파일 경로를 구성합니다.
        String filePath = "C:/uploads/" + year + "/" + month + "/" + day + "/" + filename; // 실제 경로로 변경

        // 파일을 읽어서 응답으로 반환합니다.
        try {
            Resource file = new FileSystemResource(filePath);
            if (file.exists()) {
                String contentType = determineContentType(filename);
                return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)) // 이미지 타입에 따라 변경
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