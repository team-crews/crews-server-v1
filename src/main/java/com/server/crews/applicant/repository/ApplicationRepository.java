package com.server.crews.applicant.repository;

import com.server.crews.applicant.domain.Application;
import com.server.crews.recruitment.domain.Recruitment;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ApplicationRepository extends JpaRepository<Application, Long>, ApplicationDslRepository {
    @Query("""
            select count(application) from Application application
            join application.applicant applicant
            join applicant.recruitment recruitment
            where recruitment = :recruitment
            """)
    int countAllByRecruitment(@Param("recruitment") Recruitment recruitment);

    @Query("""
            select a from Application a
            where a.applicant.id = :applicantId
            """)
    Optional<Application> findByApplicantId(@Param("applicantId") Long applicantId);
}
