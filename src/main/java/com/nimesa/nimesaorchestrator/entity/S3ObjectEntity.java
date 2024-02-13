package com.nimesa.nimesaorchestrator.entity;

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
@Table(name = "S3Object")
@AllArgsConstructor
@NoArgsConstructor
public class S3ObjectEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String bucketName;
    private String objectName;
    @ManyToOne
    @JoinColumn(name = "jobId")
    private Job job;
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public S3ObjectEntity(String bucketName, String objectName, Job job) {
        this.bucketName = bucketName;
        this.objectName = objectName;
        this.job = job;
    }
}
