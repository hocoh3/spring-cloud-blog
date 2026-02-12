package com.blog.data.service;

import java.util.List;
import java.util.Set;

public interface RankingService {

    boolean addScore(String key, Object member, double score);

    double incrementScore(String key, Object member, double delta);

    double getScore(String key, Object member);

    long getRank(String key, Object member);

    long getReverseRank(String key, Object member);

    Set<Object> getRange(String key, long start, long end);

    Set<Object> getReverseRange(String key, long start, long end);

    Set<Object> getRangeWithScores(String key, long start, long end);

    Set<Object> getReverseRangeWithScores(String key, long start, long end);

    long removeMember(String key, Object member);

    boolean clearRanking(String key);

    long size(String key);
}
