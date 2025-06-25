# 🤖 AI 기반 테스트 케이스 문서 생성 서비스

> GitHub 태그 기반 소스 코드 변경 분석과 RAG를 활용한 지능형 테스트 케이스 자동 생성 플랫폼

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/)
[![Spring AI](https://img.shields.io/badge/Spring%20AI-1.0.0-blue.svg)](https://spring.io/projects/spring-ai)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-PGVector-blue.svg)](https://github.com/pgvector/pgvector)

## 📋 프로젝트 개요

이 프로젝트는 **Spring AI**와 **PGVector RAG**를 활용하여 GitHub 리포지토리의 소스 코드 변경 사항을 분석하고, 자동으로 전문적인 테스트 케이스 문서를 생성하는 AI 기반 서비스입니다.

### 🎯 핵심 가치

**🏢 비즈니스 가치**
- **회사**: AI 기반 신규 기능 도입으로 고객 유치 및 만족도 증대
- **개발팀**: QA 업무 효율성 극대화 및 테스트 품질 향상
- **고객**: 일상 속 개발 문제를 AI와 데이터 기반으로 해결

**💡 주요 특장점**
- GitHub 태그 기반 **스마트 변경 추적**
- **2단계 LLM 분석**: 소스 이해 → 테스트 케이스 생성
- **RAG 컨텍스트 향상**: 화면 정보와 소스 코드 연결
- **실무 적용 가능**: 실제 QA 팀에서 바로 사용 가능한 문서 형식

## 🛠 기술 스택

### Core Framework
- **Backend**: Spring Boot 3.4.4
- **Language**: Java 21
- **Build**: Gradle

### AI/ML Stack
- **LLM**: Anthropic Claude 3.5 Sonnet
- **Embedding**: OpenAI text-embedding-3-small
- **Framework**: Spring AI 1.0.0
- **Vector DB**: PostgreSQL + PGVector extension

### External Integration
- **GitHub API**: 소스 코드 변경 추적
- **OpenAI API**: 임베딩.
- **Claude API**: 소스 분석 및 문서 생성



## 📖 API 사용법

### 테스트 케이스 생성 API

```http
GET /test5?owner={github_owner}&repo={repository_name}&selectTag={tag_name}
```

**Parameters:**
| Parameter | Type | Description | Example |
|-----------|------|-------------|---------|
| `owner` | String | GitHub 리포지토리 소유자 | `mycompany` |
| `repo` | String | 리포지토리 이름 | `myproject` |
| `selectTag` | String | 분석할 태그명 | `v1.2.0` |

**Example Request:**
```bash
curl "http://localhost:8080/test5?owner=mycompany&repo=myproject&selectTag=v1.2.0"
```

**Response:**
```json
{
  "testCaseDocument": "# 테스트 케이스 문서\n\n## 1. 코드 개요\n...",
  "analysisMetadata": {
    "filesAnalyzed": 5,
    "ragContextUsed": true,
    "similarityScores": [0.85, 0.72, 0.68]
  }
}
```

## 🔄 아키텍처

### 처리 흐름 다이어그램

<img width="872" alt="Image" src="https://github.com/user-attachments/assets/5eb0b3ce-15dc-46c6-8307-a91c42d03e0d" />


### 시스템 아키텍처

![Image](https://github.com/user-attachments/assets/e30f0427-3e1c-4913-b95e-aa4d21dbc71a)

### 주요 컴포넌트

#### 🔍 **CodeAnalyzeImpl** - 핵심 분석 엔진
```java
@Service
public class CodeAnalyzeImpl implements CodeAnalyze {
    // 1. 소스 코드 수집: GitHub API를 통해 태그 간 변경된 파일들을 수집
    // 2. 1차 분석: Claude LLM에게 소스 코드의 기능과 목적을 분석 요청
    // 3. RAG 검색: 분석 결과를 기반으로 PGVector에서 관련 화면 정보 검색
    // 4. 테스트 케이스 생성: 소스 코드와 RAG 컨텍스트를 결합하여 최종 문서 생성
}
```

#### 🐙 **GithubFileService** - GitHub 연동 서비스
- GitHub API를 통한 태그 정보 조회
- 태그 간 변경 사항 비교 (git compare)
- 변경된 파일의 base64 인코딩된 내용 다운로드

#### 🔎 **VectorSearchService** - RAG 검색 엔진
- LangChain 호환 PGVector 스키마 지원
- 코사인 유사도 기반 벡터 검색 (임계값: 0.4)
- 유사도 점수와 함께 관련 문서 반환

## 📊 RAG 시스템 세부사항

### 🎯 RAG 워크플로우

1. **소스 코드 분석**: Claude가 변경된 코드의 기능과 목적을 분석
2. **임베딩 변환**: OpenAI embedding 모델로 분석 결과를 벡터화
3. **유사도 검색**: PGVector에서 코사인 유사도 기반 관련 문서 검색
4. **컨텍스트 결합**: 소스 분석 결과 + RAG 검색 결과를 통합
5. **문서 생성**: 향상된 컨텍스트로 전문적인 테스트 케이스 문서 생성

## 📝 생성되는 테스트 케이스 문서 형식

시스템이 생성하는 테스트 케이스 문서는 다음 구조를 따릅니다:

```markdown
# 1. 코드 개요
- 제공된 코드의 주요 목적과 기능 설명
- 핵심 클래스/메소드와 그 역할
- 사용된 주요 기술과 패턴

# 2. 테스트 전략
- 테스트 접근 방식 설명
- 테스트 범위 및 우선순위

# 3. 테스트 케이스
| ID | 테스트 항목 | 초기 조건 | 테스트 단계 | 예상 결과 | 테스트 유형 | 우선순위 |
|---|------------|---------|-----------|----------|-----------|---------|
| TC001 | ... | ... | ... | ... | 단위/통합/기능 | 상/중/하 |

# 4. 엣지 케이스 및 예외 상황
# 5. 잠재적 문제점 및 개선사항
# 6. 결론 및 요약
```


### 기타 테스트 API (개발/디버깅용)
- `GET /test1`: GitHub 태그 목록 조회
- `GET /test2`: 태그 간 변경 사항 비교
- `GET /test3`: LLM 기반 변경 파일 분석
- `GET /test4`: 변경된 소스 코드 정보 조회

## 📈 향후 개발 계획

### 🎯 단기 목표
- [ ] 멀티 컬렉션 지원 벡터 스토어 팩토리 구현
- [ ] 비동기 처리 최적화 (Mono/Flux 활용)
- [ ] 디렉토리 기반 일괄 소스 분석 지원
- [ ] 테스트 케이스 템플릿 커스터마이징

### 🚀 장기 비전
- [ ] 다양한 LLM 모델 지원 (GPT-4, Gemini 등)
- [ ] 테스트 케이스 실행 결과 피드백 루프
- [ ] 서비스별 맞춤형 문서 생성 프롬프트 템플릿
- [ ] 레포지토리 검색기능 api
