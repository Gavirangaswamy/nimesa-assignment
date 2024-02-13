package com.nimesa.nimesaorchestrator.service.impl;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.nimesa.nimesaorchestrator.entity.Job;
import com.nimesa.nimesaorchestrator.entity.S3ObjectEntity;
import com.nimesa.nimesaorchestrator.entity.ServiceDiscoveryResultEntity;
import com.nimesa.nimesaorchestrator.enums.JobStatus;
import com.nimesa.nimesaorchestrator.repo.JobRepository;
import com.nimesa.nimesaorchestrator.repo.S3ObjectRepository;
import com.nimesa.nimesaorchestrator.repo.ServiceDiscoveryResultRepository;
import com.nimesa.nimesaorchestrator.service.DiscoveryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Service
public class DiscoveryServiceImpl implements DiscoveryService {
    private static final Logger log = LoggerFactory.getLogger(DiscoveryServiceImpl.class);
    private final AmazonEC2 ec2Client;
    private final AmazonS3 s3Client;
    private final JobRepository jobRepository;
    private final ServiceDiscoveryResultRepository serviceDiscoveryResultRepository;
    private final S3ObjectRepository s3ObjectRepository;
    private final ExecutorService executorService;

    @Autowired
    public DiscoveryServiceImpl(AmazonEC2 ec2Client, AmazonS3 s3Client, JobRepository jobRepository,
                                ServiceDiscoveryResultRepository serviceDiscoveryResultRepository,
                                S3ObjectRepository s3ObjectRepository, ExecutorService executorService) {
        this.ec2Client = ec2Client;
        this.s3Client = s3Client;
        this.jobRepository = jobRepository;
        this.serviceDiscoveryResultRepository = serviceDiscoveryResultRepository;
        this.s3ObjectRepository = s3ObjectRepository;
        this.executorService = executorService;
    }

    @Override
    public String discoverServices(List<String> services) {
        try {
            String jobId = UUID.randomUUID().toString();
            Job job = new Job(jobId, JobStatus.IN_PROGRESS);
            jobRepository.save(job);
            services.forEach(service -> executorService.submit(() -> discoverService(service, jobId)));
            return jobId;
        } catch (Exception e) {
            log.error("Error during discoverServices", e);
            throw new RuntimeException("Error during discoverServices", e);
        }
    }

    public void discoverService(String service, String jobId) {
        Job jobEntity = jobRepository.findById(jobId).orElse(null);
        if (jobEntity != null) {
            if ("s3".equalsIgnoreCase(service)) {
                s3Client.listBuckets().stream()
                        .map(Bucket::getName)
                        .forEach(bucket -> serviceDiscoveryResultRepository.save(
                                new ServiceDiscoveryResultEntity(service, bucket, jobEntity)));
            } else if ("ec2".equalsIgnoreCase(service)) {
                ec2Client.describeInstances().getReservations().stream()
                        .flatMap(reservation -> reservation.getInstances().stream())
                        .map(Instance::getInstanceId)
                        .forEach(instanceId -> serviceDiscoveryResultRepository.save(
                                new ServiceDiscoveryResultEntity(service, instanceId, jobEntity)));
            }
            jobEntity.setStatus(JobStatus.COMPLETED);
            jobRepository.save(jobEntity);
        }
    }

    @Override
    public String getJobResult(String jobId) {
        try {
            return jobRepository.findById(jobId)
                    .map(Job::getStatus)
                    .map(JobStatus::toString)
                    .orElse("JobId not found");
        } catch (Exception e) {
            log.error("Error during getJobResult", e);
            throw new RuntimeException("Error during getJobResult", e);
        }
    }

    @Override
    public List<String> getDiscoveryResult(String service) {
        try {
            return serviceDiscoveryResultRepository.findAllByServiceName(service)
                    .stream()
                    .map(ServiceDiscoveryResultEntity::getResult)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error during getDiscoveryResult", e);
            throw new RuntimeException("Error during getDiscoveryResult", e);
        }
    }

    @Override
    public String getS3BucketObjects(String bucketName) {
        try {
            String jobId = UUID.randomUUID().toString();
            Job job = new Job(jobId, JobStatus.IN_PROGRESS);
            jobRepository.save(job);

            executorService.submit(() -> {
                ObjectListing objectListing = s3Client.listObjects(bucketName);
                saveS3Objects(bucketName, jobId, objectListing.getObjectSummaries());
                while (objectListing.isTruncated()) {
                    objectListing = s3Client.listNextBatchOfObjects(objectListing);
                    saveS3Objects(bucketName, jobId, objectListing.getObjectSummaries());
                }
                Optional<Job> byId = jobRepository.findById(jobId);
                if (byId.isPresent()) {
                    Job job1 = byId.get();
                    job1.setStatus(JobStatus.COMPLETED);
                    jobRepository.save(job1);
                }
            });

            return jobId;
        } catch (Exception e) {
            log.error("Error during getS3BucketObjects", e);
            throw new RuntimeException("Error during getS3BucketObjects", e);
        }
    }

    private void saveS3Objects(String bucketName, String jobId, List<S3ObjectSummary> objectSummaries) {
        try {
            Job jobEntity = jobRepository.findById(jobId).orElse(null);
            if (jobEntity != null) {
                List<S3ObjectEntity> s3ObjectEntities = objectSummaries.stream()
                        .map(S3ObjectSummary::getKey)
                        .map(key -> new S3ObjectEntity(bucketName, key, jobEntity))
                        .collect(Collectors.toList());
                s3ObjectRepository.saveAll(s3ObjectEntities);
            }
        } catch (Exception e) {
            log.error("Error during saveS3Objects", e);
            throw new RuntimeException("Error during saveS3Objects", e);
        }
    }

    @Override
    public long getS3BucketObjectCount(String bucketName) {
        try {
            return s3ObjectRepository.countByBucketName(bucketName);
        } catch (Exception e) {
            log.error("Error during getS3BucketObjectCount", e);
            throw new RuntimeException("Error during getS3BucketObjectCount", e);
        }
    }

    @Override
    public List<String> getS3BucketObjectlike(String bucketName, String pattern) {
        try {
            return s3ObjectRepository.findByBucketNameAndObjectNameContaining(bucketName, pattern)
                    .stream()
                    .map(S3ObjectEntity::getObjectName)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error during getS3BucketObjectlike", e);
            throw new RuntimeException("Error during getS3BucketObjectlike", e);
        }

    }
}
