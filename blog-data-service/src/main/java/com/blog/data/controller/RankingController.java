package com.blog.data.controller;

import com.blog.data.service.RankingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/ranking")
public class RankingController {

    @Autowired
    private RankingService rankingService;

    @PostMapping
    public ResponseEntity<Boolean> addScore(
            @RequestParam String key,
            @RequestParam String member,
            @RequestParam double score) {
        boolean result = rankingService.addScore(key, member, score);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/increment")
    public ResponseEntity<Double> incrementScore(
            @RequestParam String key,
            @RequestParam String member,
            @RequestParam double delta) {
        double score = rankingService.incrementScore(key, member, delta);
        return ResponseEntity.ok(score);
    }

    @GetMapping("/score")
    public ResponseEntity<Double> getScore(
            @RequestParam String key,
            @RequestParam String member) {
        double score = rankingService.getScore(key, member);
        return ResponseEntity.ok(score);
    }

    @GetMapping("/rank")
    public ResponseEntity<Long> getRank(
            @RequestParam String key,
            @RequestParam String member) {
        long rank = rankingService.getRank(key, member);
        return ResponseEntity.ok(rank);
    }

    @GetMapping("/reverse-rank")
    public ResponseEntity<Long> getReverseRank(
            @RequestParam String key,
            @RequestParam String member) {
        long rank = rankingService.getReverseRank(key, member);
        return ResponseEntity.ok(rank);
    }

    @GetMapping("/range")
    public ResponseEntity<Set<Object>> getRange(
            @RequestParam String key,
            @RequestParam(defaultValue = "0") long start,
            @RequestParam(defaultValue = "9") long end) {
        Set<Object> range = rankingService.getRange(key, start, end);
        return ResponseEntity.ok(range);
    }

    @GetMapping("/reverse-range")
    public ResponseEntity<Set<Object>> getReverseRange(
            @RequestParam String key,
            @RequestParam(defaultValue = "0") long start,
            @RequestParam(defaultValue = "9") long end) {
        Set<Object> range = rankingService.getReverseRange(key, start, end);
        return ResponseEntity.ok(range);
    }

    @DeleteMapping
    public ResponseEntity<Long> removeMember(
            @RequestParam String key,
            @RequestParam String member) {
        long result = rankingService.removeMember(key, member);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Boolean> clearRanking(@RequestParam String key) {
        boolean result = rankingService.clearRanking(key);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/size")
    public ResponseEntity<Long> size(@RequestParam String key) {
        long size = rankingService.size(key);
        return ResponseEntity.ok(size);
    }
}
