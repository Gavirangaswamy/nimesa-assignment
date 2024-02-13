package com.nimesa.nimesaorchestrator.controller;

import com.nimesa.nimesaorchestrator.service.DiscoveryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Tag(name = "AWS Service Discovery API", description = "Operations pertaining to AWS service discovery")
public class DiscoveryController {

    @Autowired
    private DiscoveryService discoveryService;
    @Operation(summary = "Discover AWS services", description = "Discovers specified AWS services and stores their details")
    @ApiResponse(responseCode = "201", description = "Discovery started successfully",
            content = { @Content(mediaType = "application/json",
                    schema = @Schema(implementation = String.class)) })
    @PostMapping("/DiscoverServices")
    public ResponseEntity<String> discoverServices(@RequestBody List<String> services){
        String resultId = discoveryService.discoverServices(services);
        URI location = URI.create("/DiscoverServices/" + resultId);
        return ResponseEntity.created(location).body(resultId);
    }
    @Operation(summary = "Get job result", description = "Fetches the result of a specific job by its ID")
    @ApiResponse(responseCode = "200", description = "Job found successfully",
            content = { @Content(mediaType = "application/json",
                    schema = @Schema(implementation = String.class)) })
    @GetMapping("/GetJobResult")
    public ResponseEntity<String> getJobResult(@RequestParam String jobId){
        String result = discoveryService.getJobResult(jobId);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Get discovery result", description = "Fetches the discovery result for a specific AWS service")
    @ApiResponse(responseCode = "200", description = "Discovery result found successfully",
            content = { @Content(mediaType = "application/json") })
    @GetMapping("/GetDiscoveryResult")
    public ResponseEntity<List<String>> getDiscoveryResult(@RequestParam String service){
        List<String> result = discoveryService.getDiscoveryResult(service);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Discover S3 bucket objects", description = "Discovers and stores all filenames in a specified S3 bucket")
    @ApiResponse(responseCode = "201", description = "Discovery started successfully",
            content = { @Content(mediaType = "application/json",
                    schema = @Schema(implementation = String.class)) })
    @PostMapping("/GetS3BucketObjects")
    public ResponseEntity<String> getS3BucketObjects(@RequestParam String bucketName){
        String result = discoveryService.getS3BucketObjects(bucketName);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Get S3 bucket object count", description = "Fetches the count of objects within a specified S3 bucket from the database")
    @ApiResponse(responseCode = "200", description = "Count found successfully",
            content = { @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Integer.class)) })
    @GetMapping("/GetS3BucketObjectCount")
    public ResponseEntity<Integer> getS3BucketObjectCount(@RequestParam String bucketName){
        int count = (int) discoveryService.getS3BucketObjectCount(bucketName);
        return ResponseEntity.ok(count);
    }

    @Operation(summary = "Get S3 bucket objects by pattern", description = "Fetches filenames within a specified S3 bucket that match a given pattern from the database")
    @ApiResponse(responseCode = "200", description = "Filenames found successfully",
            content = { @Content(mediaType = "application/json",
                    schema = @Schema(implementation = List.class)) })
    @GetMapping("/GetS3BucketObjectlike")
    public ResponseEntity<List<String>> getS3BucketObjectlike(@RequestParam String bucketName, @RequestParam String pattern){
        List<String> result = discoveryService.getS3BucketObjectlike(bucketName, pattern);
        return ResponseEntity.ok(result);
    }
}
