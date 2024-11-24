<div align="center">

<!-- logo -->
<img src="https://github.com/user-attachments/assets/f4f50d50-cbc4-434f-9424-0e5a6fa87fa1" width="400"/>

### Sucat Back-end Reamd.me ✅

<br/> [<img src="https://img.shields.io/badge/프로젝트 기간-2024.5.10~2024.11.20-green?style=flat&logo=&logoColor=white" />]()

</div> 

**목차**
- 프로젝트 소개
- 프로젝트 팀원
- 프로젝트 프로토 타입
- 프로젝트 API 설계
- 사용한 기술 스택
- 프로젝트 아키텍쳐
- ERD
- 커밋 컨벤션
- 기술적 이슈와 해결 과정

## 📝 소개

<br />

## 💁‍♂️ 프로젝트 팀원
|Backend 팀장|Backend 팀원|Backend 팀원|
|:---:|:---:|:---:|
| ![](https://github.com/pp8817.png?size=120) | ![]() | ![]()|
|[박상민](https://github.com/yewon-Noh)|[정아름](https://github.com/armddi)|[김지현](https://github.com/Jihyeon02)|


### 프로토타입
<img src="https://github.com/user-attachments/assets/3530555f-c4ac-41c5-9925-f89fbd64bce8"><br/>
<img src="https://github.com/user-attachments/assets/26b0e4f1-9465-4301-804e-1985cc9aac49">
<img src="https://github.com/user-attachments/assets/81a658e0-ae39-4d65-8a0b-cf1ca0d8dd3c">
<br />

## 🗂️ APIs
작성한 API는 아래에서 확인할 수 있습니다.

👉🏻 [API 바로보기](https://documenter.getpostman.com/view/28413167/2sA3rwNEYL)


<br />

## ⚙ 기술 스택
> skills 폴더에 있는 아이콘을 이용할 수 있습니다.
### Back-end
<div>
<img src="https://github.com/yewon-Noh/readme-template/blob/main/skills/Java.png?raw=true" width="80">
<img src="https://github.com/yewon-Noh/readme-template/blob/main/skills/SpringBoot.png?raw=true" width="80">
<img src="https://github.com/yewon-Noh/readme-template/blob/main/skills/SpringSecurity.png?raw=true" width="80">
<img src="https://github.com/yewon-Noh/readme-template/blob/main/skills/SpringDataJPA.png?raw=true" width="80">
<img src="https://github.com/yewon-Noh/readme-template/blob/main/skills/Mysql.png?raw=true" width="80">
<img src="https://github.com/yewon-Noh/readme-template/blob/main/skills/Redis.png?raw=true" width="80">
<img src="https://github.com/yewon-Noh/readme-template/blob/main/skills/JWT.png?raw=true" width="80">


</div>

### Infra
<div>
<img src="https://img.shields.io/badge/amazonaws-232F3E?style=for-the-badge&logo=amazonaws&logoColor=white"><br/>
- AWS EC2, RDS, S3, Route 53, ELB ...
</div>

### Tools
<div>
<img src="https://github.com/yewon-Noh/readme-template/blob/main/skills/Github.png?raw=true" width="80">
<img src="https://github.com/yewon-Noh/readme-template/blob/main/skills/Notion.png?raw=true" width="80">
<img src="https://github.com/yewon-Noh/readme-template/blob/main/skills/Discord.png?raw=true" width="80">
<img src="https://github.com/yewon-Noh/readme-template/blob/main/skills/Figma.png?raw=true" width="80">
<img src="https://github.com/yewon-Noh/readme-template/blob/main/skills/Postman.png?raw=true" width="80">
</div>

<br />

## 🛠️ 프로젝트 아키텍쳐

### ERD
<img src="https://github.com/user-attachments/assets/1f6be81e-fe32-46df-b835-5595f938492b">
<br/>

### 커밋 컨벤션

**기본 구조**

```
type: subject

body
```

**type 종류**
```
feat: 새로운 기능을 추가할 경우
fix: 버그를 고친 경우
refactor: 프로덕션 코드 리팩토링의 경우
comment: 필요한 주석 추가 및 수정의 경우
docs: 문서를 수정한 경우
test: 테스트 추가, 테스트 리팩토링의 경우
chore: 빌드 태스트 업데이트, 패키지 매니저를 설정한 경우
rename: 파일 혹은 폴더명을 수정하거나 옮기는 경우
remove: 파일을 삭제하는 경우
style: 코드 포맷 변경, 코드 수정이 없는 경우
!BREAKING CHANGE!: 커다란 API 변경의 경우
```

**커밋 예시**
```
== ex1
feat: 로그인 기능 구현

Email 중복확인 api 개발

== ex2
fix: 사용자 정보 누락 버그 해결

사용자 서비스 코드 수정
```

<br />

## 🤔 기술적 이슈와 해결 과정
- JWT Token 인증 문제
    - [💥 [Error] JWT Token 인증 문제](https://velog.io/@pp8817/Error-JWT-Token-인증-문제)
- 닉네임 중복 검사 기능 구현 중 발생한 문제
    - [💥 아! Get 요청시에는 Message body를 사용하면 안되는구나!!](https://velog.io/@pp8817/아-Get-요청시에는-body에-데이터를-담아서-전송을-못하는구나)
- Circular Dependency 문제
    - [[시행착오] 빈 간의 순환 참조(Circular Dependency) 문제](https://velog.io/@pp8817/시행착오-빈-간의-순환-참조Circular-Dependency-문제)
- 회원과 채팅방의 연관관계 문제
    - [[시행착오] 회원과 채팅방의 연관관계는 N:M일까? 1:N일까?](https://velog.io/@pp8817/시행착오-회원과-채팅방-연관관계-설정)
- AWS로 배포하기
    - [[시행착오] AWS 프리티어 비용 과금 이슈, 해결 방법](https://velog.io/@pp8817/Project-AWS-프리티어-비용-과금-이슈-해결-방법)

<br />
