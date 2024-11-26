## Kafka&Redis를 활용한 선착순 쿠폰 발급 이벤트
유혜린
- 여기어때 기술블로그의 글을 읽고 직접 구현해본 코드입니다.
- 출처 : https://techblog.gccompany.co.kr/redis-kafka%EB%A5%BC-%ED%99%9C%EC%9A%A9%ED%95%9C-%EC%84%A0%EC%B0%A9%EC%88%9C-%EC%BF%A0%ED%8F%B0-%EC%9D%B4%EB%B2%A4%ED%8A%B8-%EA%B0%9C%EB%B0%9C%EA%B8%B0-feat-%EB%84%A4%EA%B3%A0%EC%99%95-ec6682e39731

### 선착순 쿠폰 발급 이벤트 요구사항
1. 이벤트 기간 동안, 매일 특정 시간 오픈하며 총지급 수량을 한정한다.
2. 쿠폰의 지급 수량은 당일 정해진 양을 초과해서는 안된다.
3. 쿠폰은 1인당 1장만 지급한다.

### Monolithic 구조 & RDB에 의존한 쿠폰 수량 체크 방식의 문제점
- 동시성 이슈로 인하여 선착순 쿠폰이 초과 지급될 위험이 존재한다.
- Monolithic 한 시스템 구조로 인해 쿠폰 시스템 장애 발생 시, 서비스 전체에 장애 전파 가능성이 존재한다.

### Redis를 활용하는 이유
- Redis가 단일 스레드 기반 커맨드를 순차적으로 실행하고 결과를 전달하기 때문에 동시성 이슈를 해결할 수 있다.
- 다양한 데이터 타입과 커맨드를 제공해 주기 때문에 요구사항을 만족시킬 수 있다.
- 1인당 1장만 지급하는 요구사항을 만족시키기 위해 Redis의 SET 자료구조를 사용한다.
- (참고) Redis 명령어
  - `SCARD` : Set Size 반환.
  - `SADD` : Set에 데이터 추가.
  - `SISMEMBER` : Set에 특정 문자열이 존재하는지 여부 조회.
- 단순히 SCARD 커맨드로 총 수량 체크하고, SADD 커맨드로 지급 처리하는 방식은 트래픽이 몰릴 경우 동시성 이슈를 야기하며 초과 지급이 발생하는 케이스가 존재한다.
- 이를 해결하기 위해 Redis의 Transaction을 활용하여 여러 커맨드 묶음에 대해 순차 처리를 보장한다. (단, Cluster Redis 환경에서는 Transaction이 지원되지 않고 Standalone Redis에서만 Transaction 지원 가능하다)
- `MULTI` 로 트랜잭션을 시작하고 `EXEC`로 트랜잭션을 한번에 실행시킨다. (SCARD+SADD) 조합 실행.
- Redis Transaction의 경우 Rollback 불가능한 케이스가 존재하므로, 이부분은 별도로 Rollback로직 구현 필요합니다.

### Kafka를 활용하는 이유
- 실제 쿠폰을 지급하는 DB Insert 쿼리 실행에 트래픽이 몰리며 Write DB에 부하가 발생할 수 있다.
- Write DB의 경우 이벤트 뿐 아니라 다른 서비스에서도 사용할 수 있으며, 이벤트 트래픽의 영향으로 장애 전파가 될 수 있는 문제가 존재한다.
- 이를 해결하기 위해 EDA기반 Kafka를 활용하여 쿠폰 Kafka 애플리케이션에서 쿠폰 지급 처리하도록 변경한다.
- 쿠폰 Kafka 애플리케이션이 `TimeAttackCouponIssue` 토픽을 Consume 하여 쿠폰 지급하도록 처리한다.

---
### 프로젝트 실행
kafka, redis 로컬 실행을 위한 준비
1. zookeeper 설치
- `brew install zookeeper`
2. kafka 설치
- `brew install kafka`
3. redis 설치
- `brew install redis`
4. 실행
- `brew services start zookeeper`
- `brew services start kafka`
- `brew services start redis`

### Ngrinder 성능 테스트
ngrinder download : https://github.com/naver/ngrinder/releases 
(해당 파일은 현 프로젝트 아래 ngrinder 디렉토리에 존재)
1. `java -Djava.io.tmpdir=${NGRINDER_HOME}/lib -jar nginder-controller-3.5.9-p1.war --port 7070`
2. `localhost:7070` 접속하여 ID,PW `admin`으로 접속
3. ngrinder-agent 디렉토리로 이동하여 `./run_agent.sh`로 에이전트 실행
4. Groovy script 작성하여 테스트를 진행.
5. 참고
   - Vuser per agent : 동시에 요청을 날리는 사용자 수
   - Process/ Thread : 하나의 에이전트에서 생성할 프로세스, 스레드 개수
   - Run Count : 스레드 당 테스트 코드 수행 횟수
