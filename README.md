# OldMarket

OldMarket la website mua ban do cu C2C duoc xay dung theo huong don gian, minh bach va tap trung vao trai nghiem nguoi mua.

Khac voi cac san thuong mai co tin VIP hoac ban luot day tin, OldMarket uu tien tin moi theo thoi gian dang. Nguoi ban dang tin nhanh, nguoi mua tim do cu de dang, va uy tin cong dong duoc xay dung qua chat, giao dich, danh gia va Trust Score.

## Muc tieu san pham

- Giao dien toi gian, de dung tren desktop va mobile.
- Tin moi luon hien thi truoc.
- Khong co tin VIP, khong ban luot day tin.
- Dang tin nhanh voi nhieu anh san pham.
- Chat truc tiep theo tung tin dang.
- Co co che bao cao tin xau, luu tin, theo doi nguoi ban va danh gia sau giao dich.
- Kien truc ro rang de tiep tuc mo rong thanh san pham production.

## Demo local

Ung dung chay qua Docker Compose tai:

```bash
http://localhost:8088
```

Health check backend duoc proxy qua frontend:

```bash
http://localhost:8088/actuator/health
```

## Tech stack

Backend:

- Java 21
- Spring Boot 3.5
- Spring Security
- JWT
- Spring Data JPA
- Hibernate
- Flyway
- MySQL
- MinIO
- Maven

Frontend:

- React
- Vite
- React Router
- TailwindCSS
- Nginx

Infrastructure:

- Docker Compose
- MySQL 8.4
- MinIO object storage
- GitHub Actions

## Chuc nang da co

Nguoi dung:

- Dang ky
- Dang nhap
- Quen mat khau
- Doi mat khau
- Cap nhat ten hien thi
- Xem ho so ca nhan

Tin dang:

- Tao tin dang
- Chinh sua tin
- An tin
- Upload nhieu anh
- Xoa va them anh khi sua tin
- Hien thi anh dai dien
- Tim kiem theo tu khoa
- Loc theo danh muc, tinh trang, khu vuc va gia
- Tin moi sap xep truoc

Tuong tac:

- Chat theo tung tin dang
- Hien thi ten tin dang trong phong chat
- Binh luan tin
- Luu tin
- Theo doi nguoi ban
- Xem Trust Score nguoi ban

Giao dich va danh gia:

- Nguoi ban danh dau da ban
- Nguoi mua xac nhan da nhan hang
- Danh gia nguoi ban mot lan sau giao dich

Quan tri va an toan:

- Bao cao tin xau
- Admin xem danh sach bao cao
- Admin bo qua bao cao
- Admin an tin vi pham
- Rate limiting co ban
- Xu ly loi API thong nhat bang ProblemDetail

## Kien truc tong quan

```text
OldMarket
├── frontend/                 React + Vite + Nginx
├── src/main/java/            Spring Boot backend
│   └── com/cho2hand/marketplace
│       ├── config/           Cau hinh Spring Security, CORS, MinIO
│       ├── controller/       REST API layer
│       ├── dto/              Request/response DTO
│       ├── entity/           JPA entities
│       ├── exception/        Custom exception + API error handler
│       ├── mapper/           Entity/DTO mapping
│       ├── repository/       Spring Data JPA repositories
│       ├── security/         JWT provider/filter, rate limit
│       ├── service/          Service interfaces
│       ├── service/impl/     Business logic
│       └── specification/    Dynamic listing filters
├── src/main/resources/db/    Flyway migrations
├── compose.yaml              Local fullstack runtime
└── .github/workflows/        CI build/test pipeline
```

## Backend package structure

```text
controller/      Nhan request REST, validate input co ban, tra response DTO
service/         Dinh nghia use case
service/impl/    Xu ly nghiep vu va transaction
repository/      Truy cap MySQL qua JPA
entity/          Mapping bang database
dto/             Request/response contract
mapper/          Chuyen doi entity <-> DTO
config/          Cau hinh ung dung
security/        JWT, authentication filter, password encoder, rate limit
exception/       Loi nghiep vu va handler tap trung
specification/   Dieu kien loc dong cho listing
```

## Database

Database dung MySQL va Flyway migration.

Nhom bang chinh:

- `users`
- `roles`, `user_roles`
- `categories`
- `locations`
- `listings`
- `listing_statuses`
- `media_assets`
- `listing_images`
- `conversations`
- `messages`
- `comments`
- `seller_follows`
- `saved_listings`
- `listing_transactions`
- `seller_reviews`
- `notifications`
- `listing_reports`
- `report_reasons`

Seed MVP co san:

- Category: Dien thoai, Laptop, Ghe, Quan ao
- Location: Ho Chi Minh, Ha Noi va mot so quan/huyen
- Lookup values cho tinh trang, status, report reason

## REST API chinh

Auth:

```text
POST /api/v1/auth/register
POST /api/v1/auth/login
POST /api/v1/auth/password-reset-requests
POST /api/v1/auth/password-resets
POST /api/v1/auth/password-changes
```

Users:

```text
GET   /api/v1/users/me
GET   /api/v1/users/{id}
PATCH /api/v1/users/{id}
```

Listings:

```text
GET    /api/v1/listings
GET    /api/v1/listings/{id}
GET    /api/v1/listings/mine
POST   /api/v1/listings
PATCH  /api/v1/listings/{id}
DELETE /api/v1/listings/{id}
```

Images:

```text
GET    /api/v1/listings/{listingId}/images
GET    /api/v1/listings/{listingId}/images/{mediaId}/content
POST   /api/v1/listings/{listingId}/images
DELETE /api/v1/listings/{listingId}/images/{mediaId}
```

Chat:

```text
POST /api/v1/listings/{listingId}/conversations
GET  /api/v1/conversations
GET  /api/v1/conversations/{id}/messages
POST /api/v1/conversations/{id}/messages
```

Trust, save, follow, transaction:

```text
GET    /api/v1/sellers/{sellerId}/trust-score
POST   /api/v1/sellers/{sellerId}/follow
DELETE /api/v1/sellers/{sellerId}/follow
GET    /api/v1/saved-listings/ids
POST   /api/v1/listings/{listingId}/saved
DELETE /api/v1/listings/{listingId}/saved
POST   /api/v1/listings/{listingId}/transactions
POST   /api/v1/transactions/{id}/confirm
POST   /api/v1/transactions/{id}/reviews
```

Report/Admin:

```text
POST  /api/v1/listings/{listingId}/reports
GET   /api/v1/admin/reports
PATCH /api/v1/admin/reports/{id}/dismiss
PATCH /api/v1/admin/reports/{id}/archive
PATCH /api/v1/admin/listings/{id}/archive
```

## Chay du an bang Docker

Yeu cau:

- Docker Desktop
- Docker Compose

Lenh chay:

```bash
docker compose up --build
```

Sau khi container san sang:

```bash
Frontend: http://localhost:8088
Backend:  http://localhost:8088/api/v1
Health:   http://localhost:8088/actuator/health
MinIO:    http://localhost:9001
```

Dung ung dung:

```bash
docker compose down
```

Dung va xoa volume local:

```bash
docker compose down -v
```

## Chay rieng backend

Can MySQL va MinIO dang chay, sau do cau hinh bien moi truong:

```bash
DB_URL=jdbc:mysql://localhost:3306/oldmarket
DB_USERNAME=oldmarket
DB_PASSWORD=oldmarket
JWT_SECRET=<base64-256-bit-secret>
MINIO_ENDPOINT=http://localhost:9000
MINIO_ACCESS_KEY=minioadmin
MINIO_SECRET_KEY=minioadmin
MINIO_BUCKET=oldmarket
```

Chay backend:

```bash
mvn spring-boot:run
```

## Chay rieng frontend

```bash
cd frontend
npm ci
npm run dev
```

Build production:

```bash
npm run build
```

## Kiem thu

Backend:

```bash
mvn test
```

Frontend:

```bash
cd frontend
npm ci
npm run build
```

Kiem tra whitespace/diff:

```bash
git diff --check
```

GitHub Actions dang chay cac buoc build/test co ban tren push va pull request.

## Bao mat

Dang co:

- JWT authentication
- BCrypt password hashing
- Stateless session
- Role-based admin endpoint
- Centralized exception handling
- Rate limiting in-memory
- Validation bang Jakarta Validation

Can thay doi khi deploy production:

- Doi toan bo password mac dinh trong `compose.yaml`
- Doi `JWT_SECRET` bang secret rieng tung moi truong
- Bat HTTPS qua reverse proxy
- Cau hinh backup MySQL va MinIO
- Dung Redis hoac API gateway cho distributed rate limiting neu chay nhieu replica

## Gioi han hien tai

- Elasticsearch chua duoc bat; search hien dung MySQL specification.
- Rate limit dang in-memory, phu hop local/single container.
- Chua co email/SMS that cho reset password.
- Chua co KYC, moderation nang cao hoac payment.
- Frontend dang o muc web app co ban, chua tach component theo design system day du.

## Roadmap

- Tach frontend thanh cac component/module ro hon.
- Them Elasticsearch cho search tieng Viet tot hon.
- Them Redis cho cache/rate limit/session support.
- Them email provider cho reset password.
- Them dashboard admin day du.
- Them test E2E cho luong dang tin, chat va giao dich.
- Them deploy pipeline len VPS/cloud.

## License

Du an duoc xay dung cho muc dich hoc tap, portfolio va phat trien san pham mau.
