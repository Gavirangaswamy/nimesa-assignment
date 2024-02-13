package com.nimesa.nimesaorchestrator.repo;

import com.nimesa.nimesaorchestrator.entity.S3ObjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface S3ObjectRepository extends JpaRepository<S3ObjectEntity, Integer> {
    @Query("SELECT COUNT(s) FROM S3ObjectEntity s WHERE s.bucketName = :bucketName")
    long countByBucketName(@Param("bucketName") String bucketName);

    @Query("SELECT s FROM S3ObjectEntity s WHERE s.bucketName = :bucketName AND s.objectName LIKE %:pattern%")
    List<S3ObjectEntity> findByBucketNameAndObjectNameContaining(@Param("bucketName") String bucketName, @Param("pattern") String pattern);
}
