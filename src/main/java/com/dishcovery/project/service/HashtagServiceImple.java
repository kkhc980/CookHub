package com.dishcovery.project.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dishcovery.project.persistence.HashtagMapper;

@Service
public class HashtagServiceImple implements HashtagService {

    @Autowired
    private HashtagMapper hashtagMapper;

    @Override
    public List<String> getHashtags(String query) {
        return hashtagMapper.findHashtags(query);
    }
}