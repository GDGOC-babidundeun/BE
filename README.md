## 🌳 Git 브랜치 규칙

### Branch Strategy
main
<br>>ㅤdevelop
ㅤ<br>ㅤ>>ㅤfeature/~
ㅤ<br>ㅤ>>ㅤfeature/~
ㅤ<br>ㅤ>>ㅤfeature/~

### Branch Rule
main
- 배포 가능한 코드
- 직접 Push 금지
- develop에서 Merge
develop
- 개발 통합 브랜치
- feature 브랜치만 Merge
feature
- 기능 단위 개발
ex) feature/fe-menu, feature/be-order, feature/be-payment

### Workflow
develop Pull
<br>↓
<br>feature 생성
<br>↓
<br>개발
<br>↓
<br>Commit
<br>↓
<br>Push
<br>↓
<br>PR 생성
<br>↓
<br>Review
<br>↓
<br>develop Merge
<br>↓
<br>feature 삭제

### Commit Convention
feat: 기능 추가
<br>fix: 버그 수정
<br>refactor: 리팩토링
<br>docs: 문서 수정
<br>style: 코드 스타일
<br>test: 테스트
<br>chore: 설정 변경

<br><br>

## 🚨 프로젝트 규칙

### 개발 규칙
- 모든 기능은 feature/* 브랜치에서 개발한다.
- main 브랜치 직접 Push 금지
- develop 브랜치 직접 Push 금지 (PR을 통해 Merge)
- 기능 개발 전 최신 develop을 Pull한다.
- Merge는 Squash and Merge를 사용한다.
  
### 완료 기준 (Definition of Done)
기능 구현 완료
API 연동 완료
예외 처리 완료
로컬 테스트 완료
PR 생성 및 리뷰 완료
develop 브랜치에 Merge 완료

<br><br>

## 🗄️ 로컬 DB 실행

### 준비

- Docker Desktop 또는 Docker Engine
- Java 21

### 실행 방법

```bash
cp .env.example .env
docker compose up -d
./gradlew bootRun
```

MySQL은 기본적으로 `localhost:3306`에서 실행되며, DB 이름은 `babi_order`입니다.
개인별 접속 정보와 Toss Secret Key는 `.env`에서 변경하고 `.env`는 Git에 커밋하지 않습니다.

### 종료

```bash
docker compose down
```

DB 데이터까지 초기화해야 할 때만 다음 명령을 사용합니다.

```bash
docker compose down -v
```
