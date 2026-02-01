## Http-Server-Without-Spring

### 요구사항 확인
- Java 기반 Socket 서버 : 구현 완료 - `HttpServer`
- `ServerSocket`을 이용한 소켓 바인딩 : 구현 완료 - `HttpServer`
- `ExecutorService` 기반 스레드 풀로 요청 처리 : 구현 완료 - `HttpServer`
- HTTP Request 파싱 : 구현 완료 - `HttpRequestParser`
    - Request Line (Method / Path / Query)
    - Header (필요 최소)
    - Body (POST 1개 이상)
- URL 라우팅 : 구현 완료 - `Router`
- Controller 역할 클래스 1개 : 구현 완료 - `MenuController`
- GET / POST API 1~2개 : 구현 완료 - `GET 메뉴 조회` / `POST 메뉴 추가`
- 요청 파라미터 파싱 : 구현 완료 - `HttpRequestParser`
- Validation (null / 타입 / 범위 체크 등) : 구현 완료 - `MenuController`
- 예외 처리 : 구현 완료
- HTTP Response 생성 : 구현 완료 - `HttpResponse`
    - Status Code
    - Header
    - Body

### 1. 구현하면서 가장 귀찮았던 책임 3가지
(1) HTTP 파싱
```
HTTP 요청에서 Request Line, Headers, Body를 직접 파싱하고 처리하는 로직 구현이 번거로웠습니다.

Request Line에서 HTTP Method, Path, Query String을 직접 분리해야 했고,
Headers는 콜론(:)을 기준으로 key-value 형태로 파싱해야 했습니다.

또한, Query String URL 디코딩, \r\n을 기준으로 Header와 Body를 구분하는 로직까지 고려해야 했습니다.

HTTP 파싱을 직접 구현하면서 Spring이 @RequestParam, @RequestBody 등을 통해
자동으로 수행해주던 HTTP 요청 처리 작업이 생각보다 복잡하고 큰 책임이 요구된다는 것을 느낄 수 있었습니다.
```

(2) JSON 파싱
```
JSON을 직접 파싱하는 것이 번거로웠습니다.

중괄호와 따옴표를 제거하고, 쉼표로 필드를 분리한 뒤, 콜론을 기준으로 key-value를 나누는 단순한 로직만 처리했음에도 코드 구현이 복잡하고 가독성이 떨어졌습니다.

이 경험을 통해 @RequestBody 하나로 객체에 자동 매핑해주는 편리함과
Jackson이 복잡한 JSON 구조를 안정적으로 변환해주는 역할의 중요성을 느낄 수 있었습니다.
```

(3) 요청 처리
```
Socket에서 InputStream을 읽어 요청을 파싱하고, Router를 통해 적절한 Controller를 찾아 메소드를 호출하고,
반환된 결과를 다시 OutputStream으로 전송하는 로직을 모두 직접 구현해야 했습니다.

또한, 이 과정에서 try-catch로 예외를 처리해야 했기 때문에 비즈니스 로직에 집중하기 어려웠습니다.

이 경험을 통해 Spring의 DispatcherServlet과 @ExceptionHandler, AOP 기반 예외 처리 구조가
Spring을 사용할 때 비즈니스 로직에만 집중할 수 있게 해준다는 것을 느낄 수 있었습니다.
```


### 2. 이 책임을 컨트롤러가 직접 가지면 생기는 문제
```
컨트롤러가 HTTP 파싱, JSON 파싱, 요청 처리 책임을 직접 가지게 되면

- 단일 책임 원칙(SRP)을 위반하게 됩니다.
- 모든 컨트롤러 메소드마다 HTTP 파싱/응답 생성하는 로직이 반복되어 코드 중복이 발생합니다.
- HTTP 스펙이 변경되면 모든 컨트롤러 메소드를 수정해야 되어 유지보수가 어려워집니다.
```

### 3. Spring MVC가 이 책임을 어디서 처리하는지
(1) HTTP 파싱 : Servlet Container, DispatcherServlet
```
HTTP 파싱은 Servlet Container, DispatcherServlet에서 처리합니다.

HTTP 요청은 Servlet Container가 파싱하여 HttpServletRequest / HttpServletResponse 객체로 변환합니다.
DispatcherServlet은 파싱된 HttpServletRequest를 전달받아 Spring MVC 흐름을 처리합니다.
```

(2) JSON 파싱 : HttpMessageConverter (Jackson)
```
JSON 파싱은 HttpMessageConverter에서 처리합니다.

@RequestBody가 선언됐을 때 DispatcherServlet은 요청의 Content-Type을 확인하고,
적절한 HttpMessageConverter를 선택합니다.

application/json인 경우, MappingJackson2HttpMessageConverter에서
Jackson ObjectMapper를 통해 JSON을 객체로 변환합니다.
```

(3) 요청 처리 : DispatcherServler, HandlerMapping, HandlerAdapter
```
요청 처리는 DispatcherServlet, HandlerMapping, HandlerAdapter에서 처리합니다.

먼저, Front Controller인 DispatcherServlet가 요청을 가로챈 뒤
HandlerMapping을 통해 요청 URL에 매핑된 Controller와 Handler Method를 찾고,
HandlerAdapter를 통해 해당 Controller 메소드를 실행합니다.
메소드 실행 결과는 HttpMessageConverter를 통해 JSON으로 변환되며,
DispatcherServlet을 거쳐 응답이 반환됩니다.
```
