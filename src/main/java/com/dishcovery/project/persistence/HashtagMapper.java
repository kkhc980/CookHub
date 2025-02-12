package com.dishcovery.project.persistence;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface HashtagMapper {
    List<String> findHashtags(String query);
}