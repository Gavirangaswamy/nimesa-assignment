package com.nimesa.nimesaorchestrator.entity;

import com.nimesa.nimesaorchestrator.enums.JobStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "Job")
@AllArgsConstructor
@NoArgsConstructor
public class Job {
    @Id
    private String id;
    @Enumerated(EnumType.STRING)
    private JobStatus status;
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public Job(String id, JobStatus status) {
        this.id = id;
        this.status = status;
    }
}