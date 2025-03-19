package com.dishcovery.project.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.dishcovery.project.domain.CustomUser;
import com.dishcovery.project.domain.NoticeBoardVO;
import com.dishcovery.project.domain.RecipeBoardVO;
import com.dishcovery.project.service.NoticeBoardService;
import com.dishcovery.project.service.RecipeBoardService;
import com.dishcovery.project.service.RecipeRecommendationService;

import lombok.extern.log4j.Log4j;

@Controller
@RequestMapping("/")
@Log4j
public class MainController {

    @Autowired
    private NoticeBoardService noticeBoardService;
    
    @Autowired
    private RecipeBoardService recipeBoardService;
    
    @Autowired
    private RecipeRecommendationService recipeRecommendationService;

    @GetMapping("/main")
    public String mainPage(Model model) {
        List<NoticeBoardVO> latestNotices = noticeBoardService.getLatestNotices(5);
        List<RecipeBoardVO> topPosts = recipeBoardService.getTopPosts();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 👇 간결하게 호출
        List<Map<String, Object>> recommendedRecipes =
                recipeRecommendationService.getRecommendations(authentication);

        model.addAttribute("latestNotices", latestNotices);
        model.addAttribute("topPosts", topPosts);
        model.addAttribute("recommendedRecipes", recommendedRecipes);
        model.addAttribute("pageContent", "main.jsp");

        return "layout";
    }
}
