package com.server.crews.recruitment.domain;

import com.server.crews.auth.domain.Administrator;
import com.server.crews.global.exception.CrewsException;
import com.server.crews.global.exception.ErrorCode;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Entity
@Table(name = "recruitment")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Recruitment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "recruitment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "progress", nullable = false)
    private Progress progress;

    @Column(name = "deadline", nullable = false)
    private LocalDateTime deadline;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publisher_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Administrator publisher;

    @CreatedDate
    @Column(name = "created_date", updatable = false, nullable = false)
    private LocalDateTime createdDate;

    public Recruitment(Long id, String code, String title, String description, LocalDateTime deadline,
                       Administrator publisher, List<Section> sections) {
        validateDeadline(deadline);
        this.id = id;
        this.code = code;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.publisher = publisher;
        this.progress = Progress.READY;
        addSections(sections);
    }

    private void validateDeadline(LocalDateTime deadline) {
        if (deadline.isBefore(LocalDateTime.now())) {
            throw new CrewsException(ErrorCode.INVALID_DEADLINE);
        }
        if (deadline.getMinute() != 0 || deadline.getSecond() != 0 || deadline.getNano() != 0) {
            throw new CrewsException(ErrorCode.INVALID_DEADLINE);
        }
    }

    public void addSections(List<Section> sections) {
        sections.forEach(section -> section.updateRecruitment(this));
        this.sections.addAll(sections);
    }

    public void start() {
        this.progress = Progress.IN_PROGRESS;
    }

    public void announce() {
        this.progress = Progress.ANNOUNCED;
    }

    public void close() {
        this.progress = Progress.COMPLETION;
    }

    public void updateDeadline(LocalDateTime deadline) {
        validateDeadline(deadline);
        this.deadline = deadline;
    }

    public boolean isAnnounced() {
        return this.progress == Progress.ANNOUNCED;
    }

    public boolean isStarted() {
        return this.progress != Progress.READY;
    }

    public boolean hasPassedDeadline() {
        return LocalDateTime.now().isAfter(deadline);
    }
}
