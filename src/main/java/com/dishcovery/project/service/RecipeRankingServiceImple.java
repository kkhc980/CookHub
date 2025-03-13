package com.dishcovery.project.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dishcovery.project.domain.RecipeRankingVO;
import com.dishcovery.project.persistence.RecipeRankingMapper;
import com.dishcovery.project.persistence.RecipeViewStatsMapper;

import lombok.extern.log4j.Log4j;

@Service
@Log4j
public class RecipeRankingServiceImple implements RecipeRankingService {

    @Autowired
    private RecipeRankingMapper rankingMapper;
    
    @Autowired
    private RecipeViewStatsMapper viewStatsMapper;

    /**
     * 랭킹 데이터를 가져오는 구현 메서드
     * @param rankType 랭킹 유형 (DAILY, WEEKLY, MONTHLY)
     * @return 랭킹 데이터 리스트
     */
    @Override
    public List<RecipeRankingVO> getRankings(String rankType) {
        List<RecipeRankingVO> rankings = rankingMapper.getRankings(rankType);

        // ✅ 로그 추가하여 데이터 확인
        log.info("✅ getRankings() called for type: " + rankType);
        log.info("✅ Retrieved " + rankings.size() + " rankings");

        if (rankings.isEmpty()) {
            log.warn("❌ No data returned from rankingMapper.getRankings()");
        }

        return rankings;
    }
    
    @Override
    public List<RecipeRankingVO> getTotalRankings(String rankType) {
        return rankingMapper.getTotalRankings(rankType);
    }
    @Override
    @Transactional
    public void updateRankings(String type) {
        // 기존 랭킹 데이터 삭제
        rankingMapper.deleteRankingsByType(type);

        // 조회수 데이터 가져오기
        List<RecipeRankingVO> rankings = viewStatsMapper.getViewStatsByType(type);

        // 랭킹 순위 부여
        for (int i = 0; i < rankings.size(); i++) {
            rankings.get(i).setRankPosition(i + 1); // 순위 부여 (1부터 시작)
            rankings.get(i).setRankType(type);     // 랭킹 유형 설정
        }

        // 랭킹 데이터 삽입
        rankings.forEach(ranking -> rankingMapper.insertRankings(ranking));
    }
}
