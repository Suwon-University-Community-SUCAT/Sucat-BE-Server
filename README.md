# Sucat
Suwon University Community Project

## 💡 Features
- 인증 서비스: 대학교 메일을 사용한 자체 회원가입/로그인 서비스
  - 대학교 메일에 대한 이메일 인증 과정 구현
- 커뮤니티 서비스: 게시글 CRUD, 댓글/대댓글, 게시물 좋아요, 게시물 스크랩, 검색
- 친구 서비스: 친구 관계 형성, 친구 요청, 친구 수락/취소
- 채팅 서비스: 친구 관계인 사용자와 채팅 서비스
- 알림 서비스: 게시물, 대댓글, 채팅 알림 전송 서비스
- 게임 서비스: 게임 플레이, 점수에 따른 개인 순위, 통합 점수에 따른 학과 순위

## ⚒️ Tech Stack

### Languages
- Java 17

### Frameworks
- Spring Boot 3.2.5
  - Spring Boot Web
  - Spring Boot WebSocket
  - Spring Boot Validation
  - Spring Boot Mail
  - Spring Boot Security
  - Spring Boot Data JPA

### Security
- Spring Security
- JWT

### Database
- MySQL Connector 8.0.32
- Redis

### Test (예정)
- Mockito
- JUnit(+ AssertJ)

### Deploy
- AWS
  - EC2
  - RDS
  - S3 (적용 예정) 

### Build Tools
- Gradle

### Utilities
- Lombok
- Spring Boot DevTools

### API Test/Documentation 
- Postman

## 🔍 Architecture

## Commit Message Convention
<img width="721" alt="commit Message" src="https://github.com/pp8817/Sucat/assets/71458064/623550c0-c44e-418e-a801-f70af1a7ac3b">

### ERD (24.09.01)
![image](https://github.com/user-attachments/assets/adf7da68-358a-4de0-a762-f55791bedcfe)

### System Architecture
