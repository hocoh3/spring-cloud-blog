package com.blog.data.service.impl;

import com.blog.data.service.RankingService;
import com.blog.data.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class RankingServiceImpl implements RankingService {

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public boolean addScore(String key, Object member, double score) {
        return redisUtil.zAdd(key, member, score);
    }

    @Override
    public double incrementScore(String key, Object member, double delta) {
        redisUtil.zAdd(key, member, delta);
        return redisUtil.zScore(key, member);
    }

    @Override
    public double getScore(String key, Object member) {
        return redisUtil.zScore(key, member);
    }

    @Override
    public long getRank(String key, Object member) {
        Long rank = redisUtil.zRank(key, member);
        return rank != null ? rank : -1;
    }

    @Override
    public long getReverseRank(String key, Object member) {
        Long rank = redisUtil.zReverseRank(key, member);
        return rank != null ? rank : -1;
    }

    @Override
    public Set<Object> getRange(String key, long start, long end) {
        return redisUtil.zRange(key, start, end);
    }

    @Override
    public Set<Object> getReverseRange(String key, long start, long end) {
        return redisUtil.zReverseRange(key, start, end);
    }

    @Override
    public Set<Object> getRangeWithScores(String key, long start, long end) {
        return redisUtil.zRange(key, start, end);
    }

    @Override
    public Set<Object> getReverseRangeWithScores(String key, long start, long end) {
        return redisUtil.zReverseRange(key, start, end);
    }

    @Override
    public long removeMember(String key, Object member) {
        return redisUtil.zRemove(key, member);
    }

    @Override
    public boolean clearRanking(String key) {
        return redisUtil.delete(key);
    }

    @Override
    public long size(String key) {
        return redisUtil.zSize(key);
    }
}
