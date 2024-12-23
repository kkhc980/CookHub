package com.dishcovery.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.dishcovery.project.domain.RecipeBoardVO;
import com.dishcovery.project.service.RecipeBoardService;
import lombok.extern.log4j.Log4j;

@Controller
@RequestMapping(value = "/recipeboard")
@Log4j
public class RecipeBoardController {

    @Autowired
    private RecipeBoardService recipeBoardService;

    // 레시피 게시판 목록
    @GetMapping("/list")
    public String getRecipeboardList(Model model) {
        model.addAttribute("recipeboardList", recipeBoardService.getBoardList());
        return "/recipeboard/list";
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("typesList", recipeBoardService.getTypes());
        log.info("recipeBoardService.getTypes()");
        model.addAttribute("methodsList", recipeBoardService.getMethods());
        log.info("recipeBoardService.getMethods()");
        model.addAttribute("ingredientsList", recipeBoardService.getIngredients());
        log.info("recipeBoardService.getIngredients()");
        model.addAttribute("situationsList", recipeBoardService.getSituations());
        log.info("recipeBoardService.getSituations()");
        return "/recipeboard/register";
    }

    @PostMapping("/register")
    public String registerPOST(@RequestParam(value = "recipeBoardTitle", required = true) String recipeBoardTitle,
                                @RequestParam(value = "recipeBoardContent", required = true) String recipeBoardContent,
                                @RequestParam(value = "memberId", required = true) int memberId,
                                @RequestParam(value = "ingredientList", required = false) String ingredientListStr,
                                @RequestParam(value = "methodList", required = false) String methodListStr,
                                @RequestParam(value = "situationList", required = false) String situationListStr,
                                @RequestParam(value = "typeList", required = false) String typeListStr) {

        log.info("registerPOST()");


        RecipeBoardVO recipeBoardVO = new RecipeBoardVO();
        recipeBoardVO.setRecipeBoardTitle(recipeBoardTitle);
        recipeBoardVO.setRecipeBoardContent(recipeBoardContent);
        recipeBoardVO.setMemberId(memberId);
        recipeBoardVO.setIngredientListStr(ingredientListStr);
        recipeBoardVO.setMethodListStr(methodListStr);
        recipeBoardVO.setSituationListStr(situationListStr);
        recipeBoardVO.setTypeListStr(typeListStr);


        log.info("recipeBoardVO" + recipeBoardVO.toString());

        int result = recipeBoardService.createRecipeBoard(recipeBoardVO);

        log.info(result + "행등록");
        return "redirect:/recipeboard/list";
    }
    @GetMapping("/detail")
    public void detail(Integer recipeBoardId) {
    }

    @GetMapping("/modify")
    public void modifyGET(Model model, int recipeBoardId) {
        log.info("modifyGET()");
        RecipeBoardVO recipeBoardVO = recipeBoardService.getRecipeBoardsById(recipeBoardId);
        model.addAttribute("recipeBoardVO", recipeBoardVO);
    }

    @PostMapping("/modify")
    public String modifyPOST(RecipeBoardVO recipeBoardVO) {
        log.info("modifyPOST()");
        int result = recipeBoardService.updateRecipeBoard(recipeBoardVO);
        log.info(result + "행수정");
        return "redirect:/board/list";
    }

    @PostMapping("/delete")
    public String delete(int recipeBoardId) {
        log.info("delete()");
        int result = recipeBoardService.deleteRecipeBoard(recipeBoardId);
        log.info(result + "행 삭제");
        return "redirect:/board/list";
    }

}