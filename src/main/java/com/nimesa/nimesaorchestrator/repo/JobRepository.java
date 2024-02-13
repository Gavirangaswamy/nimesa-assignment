package com.nimesa.nimesaorchestrator.repo;

import com.nimesa.nimesaorchestrator.entity.Job;
import com.nimesa.nimesaorchestrator.entity.S3ObjectEntity;
import com.nimesa.nimesaorchestrator.entity.ServiceDiscoveryResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, String> {
}

