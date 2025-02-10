package com.dishcovery.project.controller;

import com.dishcovery.project.domain.*;
import com.dishcovery.project.service.AdminMemberService;
import com.dishcovery.project.service.AdminRecipeBoardService;
import com.dishcovery.project.util.PageMaker;
import com.dishcovery.project.util.Pagination;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping("/admin")
@Log4j
@PreAuthorize("hasRole('ROLE_ADMIN')") // 관리자 권한이 있는 사용자만 접근 가능
public class AdminController {

    private final AdminMemberService memberService;
    private final AdminRecipeBoardService recipeService;

    @Autowired
    public AdminController(AdminMemberService memberService,
        AdminRecipeBoardService recipeService) {
        this.memberService = memberService;
        this.recipeService = recipeService;
    }

    // 회원 목록 조회 (페이징 처리)
    @GetMapping("/members/list")
    public String getAllMembers(Model model,
        @RequestParam(value = "page", defaultValue = "1") int page,
        @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        log.info("getAllMembers() 호출");

        Pagination pagination = new Pagination(page, pageSize);
        List<MemberVO> memberList = memberService.getAllMembers(pagination);
        int totalCount = memberService.getTotalMemberCount(pagination); // 수정
        PageMaker pageMaker = new PageMaker(); // 수정

        model.addAttribute("memberList", memberList);
        model.addAttribute("pageMaker", pageMaker);
        return "admin/members/list"; // 회원 목록 페이지
    }

    // 회원 검색 및 필터링
    @GetMapping("/members/search")
    public String searchMembers(Model model,
        @RequestParam(value = "page", defaultValue = "1") int page,
        @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
        @RequestParam(value = "searchType", required = false) String searchType,
        @RequestParam(value = "keyword", required = false) String keyword) {
        log.info("searchMembers() 호출");

        Pagination pagination = new Pagination(page, pageSize);
        List<MemberVO> memberList = null;
        int totalCount = 0;

        if (searchType != null && keyword != null) {
            if ("email".equals(searchType)) {
                memberList = memberService.searchMembersByEmail(pagination, keyword);
            } else if ("name".equals(searchType)) {
                memberList = memberService.searchMembersByName(pagination, keyword);
            }
            totalCount = memberService.getSearchMemberCount(searchType, keyword); // 수정
        } else {
            memberList = memberService.getAllMembers(pagination);
            totalCount = memberService.getTotalMemberCount(pagination); // 수정
        }

        PageMaker pageMaker = new PageMaker(); // 수정

        model.addAttribute("memberList", memberList);
        model.addAttribute("pageMaker", pageMaker);
        model.addAttribute("searchType", searchType);
        model.addAttribute("keyword", keyword);
        return "admin/members/list"; // 회원 목록 페이지
    }

    // 회원 상태 변경 (활성/비활성 토글)
    @PostMapping("/members/status/update")
    public ResponseEntity<String> updateMemberStatus(
            @RequestParam int memberId,
            @RequestParam int authStatus) {
        log.info("updateMemberStatus() 호출 memberId: " + memberId + ", authStatus: " + authStatus);
        try {
            boolean success = memberService.updateMemberAuthStatus(memberId, authStatus);
            if (success) {
                return new ResponseEntity<>("회원 상태가 업데이트 되었습니다.", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("회원 상태 업데이트에 실패했습니다.", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            log.error("회원 상태 업데이트에 실패했습니다.", e);
            return new ResponseEntity<>("회원 상태 업데이트에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    // 회원 삭제 (탈퇴 처리)
    @PostMapping("/members/delete/{memberId}")
    public String deleteMember(@PathVariable int memberId,
        @RequestParam(value = "permanent", defaultValue = "false") boolean permanent,
        RedirectAttributes redirectAttributes) {
        log.info("deleteMember() 호출 memberId: " + memberId + ", permanent: " + permanent);
        try {
            boolean success = memberService.deleteMember(memberId);
            if (success) {
                redirectAttributes.addFlashAttribute("message", "회원 삭제가 완료되었습니다.");
            } else {
                redirectAttributes.addFlashAttribute("message", "회원 삭제에 실패했습니다.");
            }
        } catch (Exception e) {
            log.error("회원 삭제에 실패했습니다.", e);
            redirectAttributes.addFlashAttribute("message", "회원 삭제 중 오류가 발생했습니다.");
        }
        return "redirect:/admin/members/list"; // 회원 목록 페이지로 리다이렉트
    }



    // 레시피 관련 기능 (AdminRecipeBoardService 사용)
    @GetMapping("/recipeboard")
    public String recipeList(
        @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
        @RequestParam(value = "typeId", required = false) Integer typeId,
        @RequestParam(value = "situationId", required = false) Integer situationId,
        @RequestParam(value = "methodId", required = false) Integer methodId,
        @RequestParam(value = "ingredientIds", required = false) String ingredientIds,
        @RequestParam(value = "hashtag", required = false) String hashtag,
        Model model,
        RedirectAttributes redirectAttributes) {
        log.info("recipeList() 호출");

        Pagination pagination = new Pagination(pageNum, 10); // 페이지 크기 10으로 고정
        pagination.setTypeId(typeId);
        pagination.setSituationId(situationId);
        pagination.setMethodId(methodId);
        if (ingredientIds != null) { // ingredientIds가 null이 아닐 때만 split
            pagination.setIngredientIdsFromString(ingredientIds);
        }
        pagination.setHashtag(hashtag);
        //pagination.setDefaultValues(); // 이 메서드의 역할이 명확하지 않음

        Map<String, Object> result = recipeService.findAllRecipesWithFilters(pagination, typeId, situationId, methodId, ingredientIds, hashtag); // 필터 적용

        model.addAllAttributes(result);
        model.addAttribute("allTypes", recipeService.getAllTypes());
        model.addAttribute("allSituations", recipeService.getAllSituations());
        model.addAttribute("allIngredients", recipeService.getAllIngredients());
        model.addAttribute("allMethods", recipeService.getAllMethods());

        // 필터 값 유지를 위해 Model 에 attribute 추가
        model.addAttribute("selectedTypeId", typeId);
        model.addAttribute("selectedSituationId", situationId);
        model.addAttribute("selectedMethodId", methodId);
        model.addAttribute("ingredientIdsStr", ingredientIds);
        model.addAttribute("hashtag", hashtag);

        return "admin/recipeboard/list";
    }
 // 게시글 상세 조회
    @GetMapping("/recipeboard/{id}")
    public String recipeDetail(@PathVariable int id, Model model) {
        log.info("recipeDetail() 호출 id: " + id);

        // 레시피 기본 정보
        RecipeBoardVO recipe = recipeService.findRecipeById(id);
        model.addAttribute("recipe", recipe);

        // 타입, 방법, 상황 이름 가져오기
        TypesVO type = recipeService.getTypeById(recipe.getTypeId()); 
        MethodsVO method = recipeService.getMethodById(recipe.getMethodId());
        SituationsVO situation = recipeService.getSituationById(recipe.getSituationId()); 
       
       
        model.addAttribute("typeName", type.getTypeName());
        model.addAttribute("methodName", method.getMethodName());
        model.addAttribute("situationName", situation.getSituationName());

        // 재료 상세 정보
        List<RecipeIngredientsDetailVO> ingredientDetails = recipeService.getRecipeIngredientsDetailsByRecipeId(id);
        model.addAttribute("ingredientDetails", ingredientDetails);

        // 레시피 단계별 정보
        List<RecipeBoardStepVO> steps = recipeService.getRecipeBoardStepsByRecipeBoardId(id);
        model.addAttribute("steps", steps);

    
        Set<Integer> selectedIngredientIds = recipeService.getSelectedIngredientIdsByRecipeBoardId(id);
        model.addAttribute("selectedIngredientIds", selectedIngredientIds);

        // 해쉬태그 이름 목록
        List<String> hashtagNames = recipeService.getHashtagNamesByRecipeBoardId(id);
        model.addAttribute("hashtagNames", hashtagNames);
        
        // 재료 목록
        List<IngredientsVO> ingredients = recipeService.getIngredientsByRecipeId(id);
        model.addAttribute("ingredients", ingredients);

        return "admin/recipeboard/detail";
    }

    // 게시글 수정 폼
    @GetMapping("/recipeboard/{id}/edit")
    public String editRecipeForm(@PathVariable int id, Model model) {
        log.info("editRecipeForm() 호출 id: " + id);
        RecipeBoardVO recipe = recipeService.findRecipeById(id);
        model.addAttribute("recipe", recipe);
        model.addAttribute("allTypes", recipeService.getAllTypes());
        model.addAttribute("allSituations", recipeService.getAllSituations());
        model.addAttribute("allIngredients", recipeService.getAllIngredients());
        model.addAttribute("allMethods", recipeService.getAllMethods());
        // 재료 상세 정보
        List<RecipeIngredientsDetailVO> ingredientDetails = recipeService.getRecipeIngredientsDetailsByRecipeId(
                id);
        model.addAttribute("ingredientDetails", ingredientDetails);
        // 레시피 단계별 정보
        List<RecipeBoardStepVO> steps = recipeService.getRecipeBoardStepsByRecipeBoardId(id);
        model.addAttribute("steps", steps);
        //선택된 재료 ID
        Set<Integer> selectedIngredientIds = recipeService.getSelectedIngredientIdsByRecipeBoardId(
                id);
        model.addAttribute("selectedIngredientIds", selectedIngredientIds);
        // 해쉬태그
        List<String> hashtagNames = recipeService.getHashtagNamesByRecipeBoardId(id);
        model.addAttribute("hashtagNames", hashtagNames);
        return "admin/recipeboard/edit";
    }

    // 게시글 수정 처리
    @PostMapping("/recipeboard/{id}/update")
    public String updateRecipe(@PathVariable int id,
                               @ModelAttribute RecipeBoardVO recipe,
                               @RequestParam(value = "ingredientIds", required = false) List<Integer> ingredientIds,
                               @RequestParam(value = "hashtags", required = false) String hashtags,
                               @RequestParam(value = "thumbnail", required = false) MultipartFile thumbnail,
                               @RequestParam(value = "steps", required = false) List<RecipeBoardStepVO> steps,
                               @RequestParam(value = "deleteStepIds", required = false) List<Integer> deleteStepIds,
                               @RequestParam(value = "ingredientDetails", required = false) List<RecipeIngredientsDetailVO> ingredientDetails,
                               RedirectAttributes redirectAttributes) {
        log.info("updateRecipe() 호출 id: " + id + ", recipe: " + recipe);
        try {
            recipeService.updateRecipe(id, recipe, ingredientIds, hashtags, thumbnail, steps,
                    deleteStepIds, ingredientDetails);
            redirectAttributes.addFlashAttribute("message", "레시피가 성공적으로 업데이트되었습니다.");
        } catch (IOException e) {
            log.error("레시피 업데이트 실패: " + e.getMessage(), e);
            redirectAttributes.addFlashAttribute("message", "레시피 업데이트에 실패했습니다: " + e.getMessage());
        } catch (Exception e) {
            log.error("레시피 업데이트 실패: " + e.getMessage(), e);
            redirectAttributes.addFlashAttribute("message", "레시피 업데이트에 실패했습니다.");
        }
        return "redirect:/admin/recipeboard";
    }

    // 게시글 삭제
    @PostMapping("/recipeboard/{id}/delete")
    public String deleteRecipe(@PathVariable int id, RedirectAttributes redirectAttributes) {
        log.info("deleteRecipe() 호출 id: " + id);
        try {
            recipeService.deleteRecipe(id);
            redirectAttributes.addFlashAttribute("message", "레시피가 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            log.error("레시피 삭제 실패: " + e.getMessage(), e);
            redirectAttributes.addFlashAttribute("message", "레시피 삭제에 실패했습니다.");
        }
        return "redirect:/admin/recipeboard";
    }

    // 게시글 등록 폼
    @GetMapping("/recipeboard/register")
    public String registerRecipeForm(Model model) {
        log.info("registerRecipeForm() 호출");
        model.addAttribute("allTypes", recipeService.getAllTypes());
        model.addAttribute("allSituations", recipeService.getAllSituations());
        model.addAttribute("allIngredients", recipeService.getAllIngredients());
        model.addAttribute("allMethods", recipeService.getAllMethods());
        return "admin/recipeboard/register";
    }

    // 게시글 등록 처리
    @PostMapping("/recipeboard/register")
    public String registerRecipe(
            @ModelAttribute RecipeBoardVO recipeBoard,
            @RequestParam(value = "ingredientIds", required = false) List<Integer> ingredientIds,
            @RequestParam(value = "hashtags", required = false) String hashtags,
            @RequestParam(value = "thumbnail", required = false) MultipartFile thumbnail,
            @RequestParam(value = "steps", required = false) List<RecipeBoardStepVO> steps,
            @RequestParam(value = "ingredientDetails", required = false) List<RecipeIngredientsDetailVO> ingredientDetails,
            RedirectAttributes redirectAttributes) {
        log.info("registerRecipe() 호출 recipe: " + recipeBoard);

        try {
            recipeService.createRecipe(recipeBoard, ingredientIds, hashtags, thumbnail, steps,
                    ingredientDetails);
            redirectAttributes.addFlashAttribute("message", "레시피가 성공적으로 등록되었습니다.");
        } catch (IOException e) {
            log.error("레시피 등록 실패: " + e.getMessage(), e);
            redirectAttributes.addFlashAttribute("message", "레시피 등록에 실패했습니다: " + e.getMessage());
        } catch (Exception e) {
            log.error("레시피 등록 실패: " + e.getMessage(), e);
            redirectAttributes.addFlashAttribute("message", "레시피 등록에 실패했습니다.");
        }

        return "redirect:/admin/recipeboard";
    }
}