# Todo List Backend

Backend cho ứng dụng Todo List, được xây dựng bằng Spring Boot. Project hỗ trợ đăng ký, đăng nhập bằng JWT và quản lý công việc của người dùng.

Repository: https://github.com/PhamNguyenTienManh/todo_list_backend.git

## Công nghệ sử dụng

- Java 21
- Spring Boot 3.4.2
- Spring Web
- Spring Data JPA
- Spring Security
- MySQL
- Lombok
- MapStruct
- Maven

## Yêu cầu môi trường

Cần cài đặt trước:

- JDK 21
- MySQL Server
- Git


## Clone project

```bash
git clone https://github.com/PhamNguyenTienManh/todo_list_backend.git
cd todo_list_backend
```

## Cấu hình database

Tạo database MySQL:

```sql
CREATE DATABASE todo_db;
```

Sau đó cấu hình lại file `src/main/resources/application.yaml` theo MySQL trên máy của bạn:

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/todo_db
    username: root
    password: your_mysql_password
    driver-class-name: com.mysql.cj.jdbc.Driver

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
```

Trong đó:

- `url`: đường dẫn tới database MySQL.
- `username`: tài khoản MySQL.
- `password`: mật khẩu MySQL.
- `ddl-auto: update`: tự động cập nhật bảng khi chạy ứng dụng.

## Cấu hình JWT

Trong file `src/main/resources/application.yaml`, cấu hình khóa ký token:

```yaml
jwt:
  signerKey: your_secret_key
  valid-duration: 1200
```

Trong đó:

- `signerKey`: khóa bí mật dùng để ký JWT.
- `valid-duration`: thời gian sống của token, tính bằng giây.

## Chạy project

Trên Windows:

```bash
.\mvnw.cmd spring-boot:run
```

Trên macOS/Linux:

```bash
./mvnw spring-boot:run
```

Ứng dụng mặc định chạy tại:

```text
http://localhost:8080
```

## Chạy test

Trên Windows:

```bash
.\mvnw.cmd test
```

Trên macOS/Linux:

```bash
./mvnw test
```

## Build project

Trên Windows:

```bash
.\mvnw.cmd clean package
```

Trên macOS/Linux:

```bash
./mvnw clean package
```
