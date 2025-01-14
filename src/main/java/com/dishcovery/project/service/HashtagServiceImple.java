package com.dishcovery.project.service;

import com.dishcovery.project.persistence.HashtagMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HashtagServiceImple implements HashtagService {

    @Autowired
    private HashtagMapper hashtagMapper;

    @Override
    public List<String> getHashtags(String query) {
        return hashtagMapper.findHashtags(query);
    }
}