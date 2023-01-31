package nam.gor.stocktaking.infrastucture.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

@Configuration
public class AmazonS3Config {

    @Value("${aws.region}")
    private String region;

    @Value("${aws.s3.endpoint}")
    private String endpoint;

    @Bean
    public S3Presigner s3Presigner() {
        return S3Presigner
                .builder()
                .credentialsProvider(DefaultCredentialsProvider.create())
                .region(Region.of(region))
                .endpointOverride(URI.create(endpoint))
                .build();
    }
}
