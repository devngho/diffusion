# diffusion

확산 / diffusion

### 확산하는 생물들의 진영 싸움

‘확산’은 생물들을 소환, 운용해 전선을 형성해 승리하는 게임입니다.

<aside>
💡 게임은 계속해서 변형될 수 있습니다.

</aside>

## 기술

Kotlin, Gradle로 구현되며 core 파트와 유저 파트로 나뉩니다.

처음 papermc로 구현되며 차후 기타 방법으로 구현될 수 있습니다.

### coroutine

core부터 coroutine로 구현됩니다.

<aside>
⚠️ 코루틴 사용이 미숙한 관계로 문제가 있을 수 있습니다.

</aside>

## 게임

게임의 룰은 다음과 같습니다.

플레이어는 2인 이상이며 턴제입니다.

### 턴

1. 먼저 동물 생성, 카드 사용 등 액션을 취합니다.
2. 동물이 **확산**합니다.
3. (미구현) 기후 영향을 받습니다.
4. (미구현) 카드를 받습니다.

### 동물 생성

원하는 위치에 생성된 동물이 배치됩니다. 50코인이 필요합니다.

### 동물 확산

동물이 확산합니다. 동물은 상하좌우로 번식하며, 다른 동물이 있으면 power만큼 체력을 깎고 없으면 diffusion만큼 타일의 체력을 깎으며 타일 체력이 없으면 번식합니다.

### 기후 영향

동물의 속성에 따라 기후 영향을 받으며 기후가 변화합니다.

### 카드

받은 카드를 코인을 사용해 쓸 수 있습니다.

---

### 카드

- 구역 정리 - 30코인
- 환경 악화 - 20코인
- 환경 개선 - 20코인
- 지반 변화 - 30코인
- 평지 친화 - 30코인
- 털 - 30코인
- 더위 적응 - 30코인
- 고지 친화 - 20코인

### 동물

큰 동물과 작은 동물이 있습니다.

각각 3, 3과 1, 6 power와 diffusion 값을 갖습니다.

일정 코인으로 속성을 강화할 수 있습니다.

### 기후

빙기 1 2 3 - 온기 4 5 6 7 - 온난기 8 9 10

로 총 10단계로 나뉘며 동물의 diffusion, power 수치에 영향을 줍니다.

## 그래서 개발은?

[https://github.com/devngho/diffusion](https://github.com/devngho/diffusion)

[제목 없음](https://www.notion.so/ea23889d90174a9bbf8006859234b581)

### core

턴 처리

- [x]  턴 수신
- [x]  턴 처리-1
- [ ]  턴 처리-2
- [ ]  턴 처리-3
- [ ]  턴 처리-4

동물

- [x]  diffusion / power
- [x]  번식
- [ ]  속성
- [ ]  카드

기후

- [ ]  변동
- [ ]  동물 영향

카드

- [ ]  획득
- [ ]  사용

코인

- [ ]  획득
- [ ]  사용

### papermc

턴 처리

- [x]  턴 송수신
- [x]  턴 처리-1
- [ ]  턴 처리-2
- [ ]  턴 처리-3
- [ ]  턴 처리-4

표시

- [x]  동물 속성
- [x]  턴
- [ ]  카드
- [ ]  기후
- [ ]  코인

~~할일 왜 이렇게 많니~~
