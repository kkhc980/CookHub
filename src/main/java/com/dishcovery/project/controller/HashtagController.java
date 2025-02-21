package com.dishcovery.project.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.dishcovery.project.service.HashtagService;

@RestController
@RequestMapping("/autocomplete")
public class HashtagController {

    @Autowired
    private HashtagService hashtagService;

    @GetMapping
    @ResponseBody
    public List<String> autocomplete(@RequestParam(value = "q", required = false) String query) {
        if (query == null || query.isBlank()) {
            return List.of(); // 빈 리스트 반환
        }
        return hashtagService.getHashtags(query);
    }
}