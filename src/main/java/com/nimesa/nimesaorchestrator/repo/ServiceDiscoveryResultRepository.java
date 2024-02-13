package com.nimesa.nimesaorchestrator.repo;

import com.nimesa.nimesaorchestrator.entity.ServiceDiscoveryResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceDiscoveryResultRepository extends JpaRepository<ServiceDiscoveryResultEntity, Integer> {
    List<ServiceDiscoveryResultEntity> findAllByServiceName(String serviceName);
}