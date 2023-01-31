package nam.gor.stocktaking.infrastucture.dao.impl;

import lombok.RequiredArgsConstructor;
import nam.gor.stocktaking.domain.IdGenerator;
import nam.gor.stocktaking.domain.entities.Stock;
import nam.gor.stocktaking.infrastucture.dao.intrfc.StockDao;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;

@RequiredArgsConstructor
@Component
public class StockDaoAmazonS3Impl implements StockDao {

    private final IdGenerator idGen;
    private final S3Presigner resigned;


    @Value("${aws.s3.bucket}")
    private String bucket;

    @Override
    public Mono<Stock> generatePreSignedUrlForUpload() {
        return Mono.fromCallable(() -> {
            final String identifier = idGen.newId();
            final var putRequest = PutObjectRequest
                                                .builder()
                                                .key(identifier)
                                                .bucket(bucket)
                                                .contentType(MediaType.IMAGE_JPEG_VALUE)
                                                 .build();

            final var putPreSignedRequest = PutObjectPresignRequest
                                                            .builder()
                                                            .putObjectRequest(putRequest)
                                                            .signatureDuration(Duration.ofHours(1))
                                                            .build();
            final var preSignedUrl = resigned
                                             .presignPutObject(putPreSignedRequest)
                                             .url()
                                             .toString();
            return Stock
                    .builder()
                    .identifier(identifier)
                    .preSignedUrl(preSignedUrl)
                    .build();
        });
    }

    @Override
    public Mono<Stock> generatePreSignedUrlForVisualization(final String identifier) {
        return Mono.fromCallable(() -> {
            final var getRequest = GetObjectRequest
                                                 .builder()
                                                 .key(identifier)
                                                 .bucket(bucket)
                                                 .build();
            final var getPreSignedRequest = GetObjectPresignRequest
                                                                .builder()
                                                                .getObjectRequest(getRequest)
                                                        .signatureDuration(Duration.ofHours(1))
                                                                .build();
            final var preSignedUrl = resigned
                                           .presignGetObject(getPreSignedRequest)
                                           .url()
                                           .toString();
            return Stock
                    .builder()
                    .identifier(identifier)
                    .preSignedUrl(preSignedUrl)
                    .build();
        });
    }
}
