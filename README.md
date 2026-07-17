# OldMarket

Marketplace C2C đồ cũ, ưu tiên tin mới và trải nghiệm người mua. Không có tin VIP hoặc lượt đẩy tin.

## Chạy bằng Docker

```bash
docker compose up --build
```

Mở `http://localhost:8088`. Compose chạy MySQL, MinIO, Spring Boot và React/Nginx. Flyway tự áp dụng migration; health endpoint là `http://localhost:8088/actuator/health` qua proxy frontend.

## Biến môi trường backend

`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET` (Base64 tối thiểu 256 bit), `MINIO_ENDPOINT`, `MINIO_ACCESS_KEY`, `MINIO_SECRET_KEY`, `MINIO_BUCKET`.

Docker Compose đã có giá trị phát triển mặc định. Khi deploy, thay toàn bộ mật khẩu và JWT secret bằng secret của môi trường triển khai.

## Kiểm thử và build

```bash
mvn test
cd frontend && npm ci && npm run build
git diff --check
```

GitHub Actions chạy ba kiểm tra này cho backend/frontend trên mỗi push và pull request.

## Luồng chính

- Đăng ký/đăng nhập JWT, reset và đổi mật khẩu.
- Tìm kiếm/lọc tin; đăng, sửa, ẩn tin; nhiều ảnh MinIO.
- Chat theo tin, thông báo, follow, lưu tin và Trust Score.
- Người bán đánh dấu đã bán → người mua xác nhận nhận hàng → đánh giá một lần.
- Báo cáo tin; admin bỏ qua hoặc ẩn tin, với trạng thái xử lý lưu lại.

## Giới hạn hiện tại

Rate limit chạy trong bộ nhớ của một backend container. Khi triển khai nhiều replica, thay bằng Redis. Email/SMS xác thực, KYC và backup tự động chưa được tích hợp vì cần hạ tầng ngoài.
