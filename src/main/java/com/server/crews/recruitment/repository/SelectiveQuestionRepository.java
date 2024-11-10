package com.server.crews.recruitment.repository;

import com.server.crews.recruitment.domain.Section;
import com.server.crews.recruitment.domain.SelectiveQuestion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SelectiveQuestionRepository extends JpaRepository<SelectiveQuestion, Long> {
    @Query("""
            select s from SelectiveQuestion s
            left join fetch s.choices
            where s.section in :sections
            """)
    List<SelectiveQuestion> findAllWithChoicesInSections(@Param("sections") List<Section> sections);
}
