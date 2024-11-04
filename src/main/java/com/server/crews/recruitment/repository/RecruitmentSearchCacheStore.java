package com.server.crews.recruitment.repository;

import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Range;
import org.springframework.data.domain.Range.Bound;
import org.springframework.data.redis.connection.Limit;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RecruitmentSearchCacheStore {

    private static final String ZSET_KEY = "recruitment_titles";
    private static final String SEARCH_DELIMITER = "\uFFFF";
    private static final int DEFAULT_SCORE = 0;

    private final RedisTemplate<String, String> redisTemplate;

    public void saveRecruitmentTitle(String title) {
        redisTemplate.opsForZSet().add(ZSET_KEY, title, DEFAULT_SCORE);
    }

    public List<String> findRecruitmentTitlesByPrefix(String prefix, int limit) {
        String rangeStart = prefix;
        String rangeEnd = prefix + SEARCH_DELIMITER;

        Range<String> range = Range.of(Bound.inclusive(rangeStart), Bound.exclusive(rangeEnd));
        Limit limitCount = Limit.limit().count(limit * 2);
        Set<String> allMatches = redisTemplate.opsForZSet()
                .rangeByLex(ZSET_KEY, range, limitCount);

        return allMatches.stream()
                .filter(title -> title.startsWith(prefix))
                .limit(limit)
                .toList();
    }
}
