# User Stories: DevOps Task API

## Tổng quan

DevOps Task API là REST API nhỏ dành cho người học hoặc nhóm phát triển muốn thực hành quy trình build, test và đóng gói ứng dụng bằng GitHub Actions và Docker. MVP cung cấp CRUD công việc, lưu dữ liệu bền vững trong Docker volume và sẵn sàng phát hành image lên Docker Hub.

## Actors

- **Người dùng API**: Tạo, xem, cập nhật và xóa công việc qua HTTP.
- **Lập trình viên**: Chạy ứng dụng, test và Docker trên máy local; gửi thay đổi qua pull request.
- **Người duy trì repository**: Cấu hình Docker Hub credentials và kiểm tra CI trước khi merge.

## Assumptions

- API không yêu cầu đăng nhập trong MVP.
- `title` dài tối đa 100 ký tự; `description` dài tối đa 500 ký tự.
- Trạng thái hợp lệ gồm `TODO`, `IN_PROGRESS`, `DONE`.
- GitHub repository và Docker Hub repository thuộc quyền quản lý của người dùng.

## User Stories

### Quản lý công việc

#### US-001: Tạo công việc

**Priority:** Must

**Story:** As a người dùng API, I want tạo một công việc, so that tôi có thể theo dõi việc cần làm.

**Acceptance Criteria:**

- Given request có `title` hợp lệ, when gọi `POST /api/tasks`, then API trả HTTP 201, header `Location` và task đã tạo.
- Nếu không cung cấp trạng thái, task nhận trạng thái `TODO`.
- Title rỗng hoặc dữ liệu vượt giới hạn trả HTTP 400 kèm lỗi theo field.

#### US-002: Xem công việc

**Priority:** Must

**Story:** As a người dùng API, I want xem danh sách hoặc một công việc, so that tôi biết trạng thái hiện tại.

**Acceptance Criteria:**

- `GET /api/tasks` trả HTTP 200 và một JSON array.
- `GET /api/tasks/{id}` trả task tương ứng hoặc HTTP 404 với JSON error nhất quán.

#### US-003: Cập nhật công việc

**Priority:** Must

**Story:** As a người dùng API, I want cập nhật nội dung và trạng thái công việc, so that dữ liệu phản ánh tiến độ mới nhất.

**Acceptance Criteria:**

- Request hợp lệ tới `PUT /api/tasks/{id}` trả task đã cập nhật.
- Trạng thái không hỗ trợ hoặc thiếu trạng thái trả HTTP 400.
- ID không tồn tại trả HTTP 404.

#### US-004: Xóa công việc

**Priority:** Must

**Story:** As a người dùng API, I want xóa công việc không còn cần thiết, so that danh sách luôn gọn và chính xác.

**Acceptance Criteria:**

- Xóa thành công trả HTTP 204.
- ID không tồn tại trả HTTP 404.

### Container và vận hành

#### US-005: Chạy ứng dụng bằng Docker

**Priority:** Must

**Story:** As a lập trình viên, I want chạy API bằng một lệnh Docker Compose, so that môi trường chạy có thể tái tạo dễ dàng.

**Acceptance Criteria:**

- `docker compose up --build -d` khởi động API trên cổng 8080.
- Container chạy bằng non-root user và đạt trạng thái healthy.
- Dữ liệu còn tồn tại sau khi container được tạo lại mà volume không bị xóa.

### Tích hợp và phát hành liên tục

#### US-006: Kiểm tra thay đổi tự động

**Priority:** Must

**Story:** As a lập trình viên, I want GitHub Actions tự động chạy test và Docker build, so that lỗi được phát hiện trước khi merge.

**Acceptance Criteria:**

- Pull request vào `main` và push branch đều chạy `./mvnw verify` và Docker build.
- Workflow thất bại nếu test hoặc Docker build thất bại.

#### US-007: Phát hành Docker image

**Priority:** Must

**Story:** As a người duy trì repository, I want publish image từ `main` và version tag, so that người dùng có thể pull bản build đã kiểm chứng.

**Acceptance Criteria:**

- Push `main` phát hành `latest` và `sha-*`; tag `v*` phát hành tag phiên bản.
- Image hỗ trợ `linux/amd64` và `linux/arm64`.
- Thiếu Docker Hub credentials không làm CI thất bại và phải hiển thị notice bỏ qua publish.

## Non-Functional Requirements

- Không lưu token, mật khẩu thật hoặc file `.env` trong Git.
- Health endpoint phải phản hồi tại `/actuator/health`.
- Tài liệu phải đủ để chạy local, Docker và cấu hình pipeline mà không cần đọc mã nguồn.

## Open Questions

- Không còn câu hỏi chặn MVP.
