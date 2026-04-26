package com.nhantic.trelloapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.auth.StsAssumeRoleCredentialsProvider;
import software.amazon.awssdk.services.sts.model.AssumeRoleRequest;

@Configuration
public class S3Config {
    private final String REGION = Region.AP_SOUTHEAST_1.toString();

    @Value("${AWS_S3_ROLE_ARN}")
    private String ROLE_ARN;

    @Value("${AWS_S3_ROLE_SESSION}")
    private String ROLE_SESSION;

    @Bean
    public AwsCredentialsProvider awsCredentialsProvider() {
        // Assume một IAM Role cụ thể qua STS, dùng credentials hiện tại làm base
        StsClient stsClient = StsClient.builder()
                .region(Region.of(REGION))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();

        AssumeRoleRequest assumeRoleRequest = AssumeRoleRequest.builder()
                .roleArn(ROLE_ARN)
                .roleSessionName(ROLE_SESSION)
                .durationSeconds(3600) // Token có hiệu lực 1 giờ
                .build();

        return StsAssumeRoleCredentialsProvider.builder()
                .stsClient(stsClient)
                .refreshRequest(assumeRoleRequest)
                .build();
    }

    @Bean
    public
    S3Client s3Client(AwsCredentialsProvider credentialsProvider) {
        return S3Client.builder()
                .region(Region.of(REGION))
                .credentialsProvider(credentialsProvider)
                .build();
    }

    @Bean
    public S3Presigner s3Presigner(AwsCredentialsProvider credentialsProvider) {
        return S3Presigner.builder()
                .region(Region.of(REGION))
                .credentialsProvider(credentialsProvider)
                .build();
    }
}
