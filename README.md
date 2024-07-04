# Sucat
Suwon University Community Project

## 💡 Features
- 인증 서비스: JWT(Access Token, Refresh Token)
- 커뮤니티 서비스: 게시글 CRUD, 댓글/대댓글, 좋아요, 스크랩, 검색
- 친구 서비스: 친구 추가, 친구와 실시간 채팅
- 알림 서비스: 게시글, 대댓글, 채팅 알림
- 게임 서비스: 게임 플레이, 점수에 따른 개인 순위, 통합 점수에 따른 학과 순위

## ⚒️ Tech Stack

### Languages
- Java 17

### Frameworks
- Spring Boot 3.2.5
  - Spring Boot Starter Web
  - Spring Boot Starter WebSocket
  - Spring Boot Starter Validation
  - Spring Boot Starter Mail
  - Spring Boot Starter Security
  - Spring Boot Starter Data JPA

### Security
- JWT (io.jsonwebtoken)
- Auth0 Java JWT

### Database
- MySQL Connector 8.0.32

### Build Tools
- Gradle

### Utilities
- Lombok
- Spring Boot DevTools

### Testing
- Spring REST Docs (MockMvc)

### API Test/Documentation 
- Postman

## 🔍 Architecture

### ERD (24.07.04)
<img width="480" alt="스크린샷 2023-12-31 19 33 45" src="https://github.com/pp8817/JpaBoard/assets/71458064/55598ebf-81a1-49e3-9c63-ec064b7a31f2">

### System Architecture
