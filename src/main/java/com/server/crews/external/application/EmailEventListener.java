package com.server.crews.external.application;

import com.server.crews.applicant.domain.Application;
import com.server.crews.applicant.event.OutcomeDeterminedEvent;
import com.server.crews.recruitment.domain.Recruitment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailEventListener {
    private final EmailService emailService;

    @TransactionalEventListener(value = OutcomeDeterminedEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void sendEmailToApplicants(final OutcomeDeterminedEvent event) {
        Recruitment recruitment = event.recruitment();
        List<Application> applications = event.applications();

        applications.forEach(applicant -> emailService.send(applicant, recruitment));
    }
}
