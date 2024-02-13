package com.nimesa.nimesaorchestrator.service;

import java.util.List;


public interface DiscoveryService {

    String discoverServices(List<String> services);


    String getJobResult(String jobId);

    List<String> getDiscoveryResult(String service);

    String getS3BucketObjects(String bucketName);

    long getS3BucketObjectCount(String bucketName);

    List<String> getS3BucketObjectlike(String bucketName, String pattern);
}
