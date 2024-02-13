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
@Table(name = "ServiceDiscoveryResult")
@AllArgsConstructor
@NoArgsConstructor
public class ServiceDiscoveryResultEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String serviceName;
    private String result;
    @ManyToOne
    @JoinColumn(name = "jobId")
    private Job job;
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public ServiceDiscoveryResultEntity(String serviceName, String result, Job job) {
        this.serviceName = serviceName;
        this.result = result;
        this.job = job;
    }
}
