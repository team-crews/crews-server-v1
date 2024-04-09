package com.server.crews.recruitment.application;

import com.server.crews.global.exception.CrewsException;
import com.server.crews.global.exception.ErrorCode;
import com.server.crews.recruitment.domain.NarrativeQuestion;
import com.server.crews.recruitment.domain.Recruitment;
import com.server.crews.recruitment.domain.Section;
import com.server.crews.recruitment.domain.SelectiveQuestion;
import com.server.crews.recruitment.dto.request.DeadlineUpdateRequest;
import com.server.crews.recruitment.dto.request.ProgressStateUpdateRequest;
import com.server.crews.recruitment.dto.request.RecruitmentSaveRequest;
import com.server.crews.recruitment.dto.response.RecruitmentDetailsResponse;
import com.server.crews.recruitment.repository.NarrativeQuestionRepository;
import com.server.crews.recruitment.repository.RecruitmentRepository;
import com.server.crews.recruitment.repository.SelectiveQuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RecruitmentService {
    private final RecruitmentRepository recruitmentRepository;
    private final NarrativeQuestionRepository narrativeQuestionRepository;
    private final SelectiveQuestionRepository selectiveQuestionRepository;

    @Transactional
    public void saveRecruitment(
            final Recruitment accessedRecruitment,
            final RecruitmentSaveRequest request) {
        accessedRecruitment.updateAll(request);
        recruitmentRepository.save(accessedRecruitment);
    }

    @Transactional
    public void updateProgressState(
            final Recruitment accessedRecruitment,
            final ProgressStateUpdateRequest request) {
        accessedRecruitment.updateProgress(request.progress());
        recruitmentRepository.save(accessedRecruitment);
    }

    public RecruitmentDetailsResponse getRecruitmentDetails(final Long recruitmentId) {
        Recruitment recruitment = recruitmentRepository.findWithSectionsById(recruitmentId)
                .orElseThrow(() -> new CrewsException(ErrorCode.RECRUITMENT_NOT_FOUND));
        List<Section> sections = recruitment.getSections();
        List<NarrativeQuestion> narrativeQuestions = narrativeQuestionRepository.findAllBySectionIn(sections);
        List<SelectiveQuestion> selectiveQuestions = selectiveQuestionRepository.findAllWithChoicesInSections(sections);

        Map<Section, List<NarrativeQuestion>> narrativeQuestionsBySection = narrativeQuestions.stream()
                .collect(groupingBy(NarrativeQuestion::getSection));
        Map<Section, List<SelectiveQuestion>> selectiveQuestionsBySection = selectiveQuestions.stream()
                .collect(groupingBy(SelectiveQuestion::getSection));
        return RecruitmentDetailsResponse.from(recruitment, narrativeQuestionsBySection, selectiveQuestionsBySection);
    }

    public void updateDeadline(
            final Recruitment accessedRecruitment, final DeadlineUpdateRequest request) {
        accessedRecruitment.updateDeadline(request.deadline());
        recruitmentRepository.save(accessedRecruitment);
    }
}
