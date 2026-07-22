package com.cho2hand.marketplace.service.impl.storage;

import com.cho2hand.marketplace.exception.MediaStorageException;
import com.cho2hand.marketplace.service.storage.StorageHealthService;
import com.cho2hand.marketplace.storage.MinioProperties;
import io.minio.BucketExistsArgs;
import io.minio.MinioClient;
import java.net.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class R2StorageHealthService implements StorageHealthService {
    private static final Logger log = LoggerFactory.getLogger(R2StorageHealthService.class);
    private final MinioClient minio;
    private final MinioProperties props;

    public R2StorageHealthService(MinioClient minio, MinioProperties props) {
        this.minio = minio;
        this.props = props;
    }

    @Override
    public void ensureReady() {
        try {
            if (!minio.bucketExists(BucketExistsArgs.builder().bucket(props.bucket()).build())) {
                log.error("storage_bucket_missing bucket={} endpointHost={}", props.bucket(), endpointHost());
                throw new MediaStorageException("Không tìm thấy bucket Cloudflare R2.", null);
            }
        } catch (MediaStorageException exception) {
            throw exception;
        } catch (Exception exception) {
            log.error("storage_health_failed bucket={} endpointHost={} errorType={} error={}",
                    props.bucket(), endpointHost(), exception.getClass().getSimpleName(), exception.getMessage());
            throw new MediaStorageException("Cloudflare R2 chưa sẵn sàng. Vui lòng kiểm tra cấu hình lưu trữ.", exception);
        }
    }

    private String endpointHost() {
        try {
            return URI.create(props.endpoint()).getHost();
        } catch (Exception ignored) {
            return "invalid-endpoint";
        }
    }
}
