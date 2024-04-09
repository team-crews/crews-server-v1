package com.server.crews.recruitment.repository;

import com.server.crews.recruitment.domain.Recruitment;

import java.util.Optional;

public interface RecruitmentDslRepository {
    Optional<Recruitment> findDetailsById(Long id);
}
