# DevOps Task API

REST API quản lý công việc dùng để thực hành một quy trình DevOps nhỏ nhưng hoàn chỉnh: viết test, đóng gói Docker, kiểm tra bằng GitHub Actions và phát hành image lên Docker Hub.

## Công nghệ

- Java 21, Spring Boot 3.5 và Maven Wrapper
- Spring Web, Validation, Data JPA, Actuator
- H2 file database
- Docker, Docker Compose và GitHub Actions

## Chạy bằng Maven

Yêu cầu Java 21 trở lên:

```bash
./mvnw spring-boot:run
```

API chạy tại `http://localhost:8080`. Dữ liệu local được ghi vào thư mục `data/` và không được commit lên Git.

Kiểm tra health:

```bash
curl http://localhost:8080/actuator/health
```

## Chạy bằng Docker Compose

```bash
docker compose up --build -d
docker compose ps
```

Compose tạo named volume `devops-task-api_task-data`. Vì vậy dữ liệu vẫn tồn tại khi chạy lại container:

```bash
docker compose down
docker compose up -d
```

Chỉ dùng `docker compose down -v` khi muốn xóa cả database.

## REST API

| Phương thức | Endpoint | Chức năng |
| --- | --- | --- |
| `POST` | `/api/tasks` | Tạo công việc |
| `GET` | `/api/tasks` | Lấy danh sách |
| `GET` | `/api/tasks/{id}` | Lấy một công việc |
| `PUT` | `/api/tasks/{id}` | Cập nhật toàn bộ công việc |
| `DELETE` | `/api/tasks/{id}` | Xóa công việc |

Tạo task mới (nếu không gửi `status`, giá trị mặc định là `TODO`):

```bash
curl -i -X POST http://localhost:8080/api/tasks \
  -H 'Content-Type: application/json' \
  -d '{"title":"Build pipeline","description":"Run tests and build image"}'
```

Cập nhật task:

```bash
curl -X PUT http://localhost:8080/api/tasks/1 \
  -H 'Content-Type: application/json' \
  -d '{"title":"Publish image","description":"Push to Docker Hub","status":"DONE"}'
```

`status` nhận một trong ba giá trị: `TODO`, `IN_PROGRESS`, `DONE`.

## Kiểm thử

```bash
./mvnw verify
docker compose config
docker compose build
```

## GitHub Actions và Docker Hub

Workflow `.github/workflows/ci-cd.yml` thực hiện:

- Mọi pull request vào `main` và mọi branch được push: chạy test Maven và build Docker image.
- Push vào `main`: publish `latest` và `sha-*`.
- Push tag `v*`, ví dụ `v1.0.0`: publish tag cùng tên.
- Image phát hành hỗ trợ `linux/amd64` và `linux/arm64`.

Để bật publish, tạo trong **Settings → Secrets and variables → Actions**:

- Repository variable `DOCKERHUB_USERNAME`: tên tài khoản Docker Hub.
- Repository secret `DOCKERHUB_TOKEN`: access token của Docker Hub, không dùng mật khẩu trực tiếp.

Nếu thiếu một trong hai giá trị, job publish được bỏ qua với notice; test và Docker build vẫn chạy bình thường.

Nên bảo vệ nhánh `main` trong **Settings → Branches** bằng rule yêu cầu pull request và check `Test and build` thành công trước khi merge.

## Biến môi trường

Sao chép `.env.example` thành `.env` nếu muốn đổi cấu hình Compose. Không commit `.env` hoặc bất kỳ token nào lên repository.

| Biến | Mặc định | Ý nghĩa |
| --- | --- | --- |
| `APP_PORT` | `8080` | Cổng public trên máy host |
| `DB_USERNAME` | `sa` | Tài khoản H2 |
| `DB_PASSWORD` | rỗng | Mật khẩu H2 cho demo local |
| `IMAGE_NAME` | `devops-task-api` | Tên Docker image |
| `IMAGE_TAG` | `local` | Docker image tag |

## Quy trình Git đề xuất

```bash
git switch -c feature/my-change
git push -u origin feature/my-change
```

Sau đó mở pull request về `main`, chờ CI thành công và review trước khi merge.
