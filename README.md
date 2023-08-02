# 스프링 MVC 1편
#### 인프런 강의: [스프링 MVC 1편 - 백엔드 웹 개발 핵심 기술](https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-mvc-1)


## JSP
* /WEB-INF
  - 이 경로안에 JSP가 있으면 외부에서 직접 JSP를 호출할 수 없다. 항상 컨트롤러를 통해서 JSP를 호출하도록 만들 수 있다.

* redirect vs forward
  - 리다이렉트는 실제 클라이언트(웹 브라우저)에 응답이 나갔다가, 클라이언트가 redirect 경로로 다시 요청한다.
  - 따라서 클라이언트가 인지할 수 있고, URL 경로도 실제로 변경된다.
  - 반면에 포워드는 서버 내부에서 일어나는 호출이기 때문에 클라이언트가 전혀 인지하지 못한다.

* <%= request.getAttribute("member")%>
  - 모델에 저장한 member 객체를 꺼낼 수 있지만, 너무 복잡해진다.
  - JSP는 ${} 문법을 제공하는데, 이 문법을 사용하면 request의 attribute에 담긴 데이터를 편리하게 조회할 수 있다.

- MVC 덕분에 컨트롤러 로직과 뷰 로직을 확실하게 분리한 것을 확인할 수 있다.
- 향후 화면에 수정이 발생하면 뷰 로직만 변경하면 된다.


## MVC 패턴 한계
- MVC 패턴을 적용한 덕분에 컨트롤러의 역할과 뷰를 렌더링 하는 역할을 명확하게 구분할 수 있다.
- 특히 뷰는 화면을 그리는 역할에 충실한 덕분에, 코드가 깔끔하고 직관적이다. 단순하게 모델에서 필요한 데이터를 꺼내고, 화면을 만들면 된다.
- 그런데 컨트롤러는 딱 봐도 중복이 많고, 필요하지 않는 코드들도 많이 보인다.

- 공통 처리가 어렵다.
  - 기능이 복잡해질수록 컨트롤러에서 공통으로 처리해야 하는 부분이 점점 더 많이 증가할 것이다.
  - 단순히 '공통 기능'을 메서드로 뽑으면 될 것 같지만, 결과적으로 해당 메서드를 항상 호출해야 하고, 실수로 호출하지 않으면 문제가 될 것이다. 그리고 호출하는 것 자체도 중복이다.

- 공통 처리 해결 방법
  - 이 문제를 해결하려면 '컨트롤러 호출 전'에 먼저 공통 기능을 처리해야 한다.
  - 소위 수문장 역할을 하는 기능이 필요하다.
  - 프론트 컨트롤러(Front Controller) 패턴을 도입하면 이런 문제를 깔끔하게 해결할 수 있다. (입구를 하나로!)
  - 스프링 MVC의 핵심도 바로 이 프론트 컨트롤러에 있다.


## FrontController 패턴 특징
- 프론트 컨트롤러 서블릿 하나로 클라이언트의 요청을 받음
- 프론트 컨트롤러가 요청에 맞는 컨트롤러를 찾아서 호출
- 입구를 하나로!
- 공통 처리 가능
- 프론트 컨트롤러를 제외한 나머지 컨트롤러는 서블릿을 사용하지 않아도 됨

- 스프링 웹 MVC와 프론트 컨트롤러
  - 스프링 웹 MVC의 핵심도 바로 FrontController
  - 스프링 웹 MVC의 DispatcherServlet이 FrontController 패턴으로 구현되어 있음


## DispacherServlet 서블릿 등록
- DispacherServlet 도 부모 클래스에서 HttpServlet 을 상속 받아서 사용하고, 서블릿으로 동작한다.
- DispatcherServlet -> FrameworkServlet -> HttpServletBean -> HttpServlet
- 스프링 부트는 DispacherServlet 을 서블릿으로 자동으로 등록하면서 모든 경로( urlPatterns="/" )에 대해서 매핑한다.
  - 참고: 더 자세한 경로가 우선순위가 높다. 그래서 기존에 등록한 서블릿도 함께 동작한다.

- 요청흐름:
  - 서블릿이 호출되면 HttpServlet 이 제공하는 serivce() 가 호출된다.
  - 스프링 MVC는 DispatcherServlet 의 부모인 FrameworkServlet 에서 service() 를 오버라이드 해두었다.
  - FrameworkServlet.service() 를 시작으로 여러 메서드가 호출되면서 DispacherServlet.doDispatch() 가 호출된다.


## SpringMVC 구조
- 동작 순서
  1. 핸들러 조회: 핸들러 매핑을 통해 요청 URL에 매핑된 핸들러(컨트롤러)를 조회한다.
  2. 핸들러 어댑터 조회: 핸들러를 실행할 수 있는 핸들러 어댑터를 조회한다.
  3. 핸들러 어댑터 실행: 핸들러 어댑터를 실행한다.
  4. 핸들러 실행: 핸들러 어댑터가 실제 핸들러를 실행한다.
  5. ModelAndView 반환: 핸들러 어댑터는 핸들러가 반환하는 정보를 ModelAndView로 변환해서 반환한다.
  6. viewResolver 호출: 뷰 리졸버를 찾고 실행한다.
    - JSP의 경우: InternalResourceViewResolver 가 자동 등록되고, 사용된다.
  7. View반환: 뷰리졸버는 뷰의 논리이름을 물리이름으로 바꾸고,렌더링 역할을 담당하는 뷰객체를 반환한다.
    - JSP의 경우 InternalResourceView(JstlView) 를 반환하는데, 내부에 forward() 로직이 있다.
  8. 뷰렌더링: 뷰를 통해서 뷰를 렌더링한다.

- 스프링 MVC의 큰 강점은 DispatcherServlet 코드의 변경 없이, 원하는 기능을 변경하거나 확장할 수 있다는 점이다.
- 지금까지 설명한 대부분을 확장 가능할 수 있게 인터페이스로 제공한다.


## Slf4j 로깅
- @Slf4j : 롬복 사용 가능
- LEVEL: TRACE > DEBUG > INFO > WARN > ERROR

- 로그 사용시 장점
    1. 쓰레드 정보, 클래스 이름 같은 부가 정보를 함께 볼 수 있고, 출력 모양을 조정할 수 있다.
    2. 로그 레벨에 따라 개발 서버에서는 모든 로그를 출력하고, 운영서버에서는 출력하지 않는 등 로그를 상황에 맞게 조절할 수 있다.
    3. 시스템 아웃 콘솔에만 출력하는 것이 아니라, 파일이나 네트워크 등, 로그를 별도의 위치에 남길 수 있다. 특히 파일로 남길 때는 일별, 특정 용량에 따라 로그를 분할하는 것도 가능하다.
    4. 성능도 일반 System.out보다 좋다. (내부 버퍼링, 멀티 쓰레드 등등) 그래서 실무에서는 꼭 로그를 사용해야 한다.


## Spring MVC
- @Controller :
  - 스프링이 자동으로 스프링 빈으로 등록한다. (내부에 @Component 애노테이션이 있어서 컴포넌트 스캔의 대상이 됨)
  - 스프링 MVC에서 애노테이션 기반 컨트롤러로 인식한다.

- @RequestMapping :
  - 요청 정보를 매핑한다.
  - 해당 URL이 호출되면 이 메서드가 호출된다.
  - 애노테이션을 기반으로 동작하기 때문에, 메서드의 이름은 임의로 지으면 된다.
  - @RequestMapping -> @GetMapping, @PostMapping
  - class 위에 아래 코드를 적어서 경로 중복 해결.
  - @RequestMapping("/springmvc/v2/members")

- ModelAndView :
  - 모델과 뷰 정보를 담아서 반환하면 된다.

- @RequestParam("username") :
  - Param 이름을 지정하여 가져올 수 있다.


## 매핑 정보
- @Controller : 반환 값이 String 이면 뷰 이름으로 인식된다. 그래서 뷰를 찾고 뷰가 랜더링 된다.
- @RestController : 반환 값으로 뷰를 찾는 것이 아니라, HTTP 메시지 바디에 바로 입력한다. 따라서 실행 결과로 ok 메세지를 받을 수 있다.

- @RequestMapping("/hello-basic") :
  - /hello-basic URL 호출이 오면 이 메서드가 실행되도록 매핑한다.
  - 대부분의 속성을 배열[] 로 제공하므로 다중 설정이 가능하다. {"/hello-basic", "/hello-go"}


## HTTP 메서드
- @RequestMapping 에 method 속성으로 HTTP 메서드를 지정하지 않으면 HTTP 메서드와 무관하게 호출된다.
- 모두 허용 GET, HEAD, POST, PUT, PATCH, DELETE

- @RequestMapping(value = "/mapping-get-v1", method = RequestMethod.GET)
- @GetMapping("/test")


## PathVariable
```java
public String mappingPath(@PathVariable String userId)
```


## MultiValueMap
- MAP과 유사한데, 하나의 키에 여러 값을 받을 수 있다.
- HTTP header, HTTP 쿼리 파라미터와 같이 하나의 키에 여러 값을 받을 때 사용한다.
- keyA=value1&keyA=value2

  ```java
  MultiValueMap<String, String> map = new LinkedMultiValueMap();
  map.add("keyA", "value1");
  map.add("keyA", "value2");
  //[value1,value2]
  List<String> values = map.get("keyA");
  ```


## 요청 파라미터 vs HTTP 메시지 바디
- @RequestParam , @ModelAttribute : 요청 파라미터를 조회하는 기능
- @RequestBody : HTTP 메시지 바디를 직접 조회하는 기능
- @ResponseBody : 응답 결과를 HTTP 메시지 바디에 직접 담아서 전달할 수 있다. 물론 이 경우에도 view를 사용하지 않는다.


* 스프링은 @ModelAttribute , @RequestParam 과 같은 해당 애노테이션을 생략시 다음과 같은 규칙을 적용한다.
  - String , int , Integer 같은 단순 타입 = @RequestParam
  - 나머지 = @ModelAttribute (argument resolver 로 지정해둔 타입 외)
  - @RequestBody 는 생략 불가능! @ModelAttribute 가 적용되어 버린다.


- @RequestBody 요청
  - JSON 요청 -> HTTP 메시지 컨버터 -> 객체
- @ResponseBody 응답
  - 객체 -> HTTP 메시지 컨버터 -> JSON 응답


## @RestController
- @Controller 대신에 @RestController 애노테이션을 사용하면, 해당 컨트롤러에 모두 @ResponseBody 가 적용되는 효과가 있다. 
- 따라서 뷰 템플릿을 사용하는 것이 아니라, HTTP 메시지 바디에 직접 데이터를 입력한다. 
- 이름 그대로 Rest API(HTTP API)를 만들 때 사용하는 컨트롤러이다.
- 참고로 @ResponseBody 는 클래스 레벨에 두면 전체 메서드에 적용되는데, @RestController 에노테이션 안에 @ResponseBody 가 적용되어 있다.


## @ResponseBody 사용 원리
* @ResponseBody 를 사용
    - HTTP의 BODY에 문자 내용을 직접 반환
    - viewResolver 대신에 HttpMessageConverter 가 동작 
    - 기본 문자처리: StringHttpMessageConverter
    - 기본 객체처리: MappingJackson2HttpMessageConverter

- 참고: 응답의 경우 클라이언트의 HTTP Accept 해더와 서버의 컨트롤러 반환 타입 정보 둘을 조합해서 HttpMessageConverter 가 선택된다.

- 스프링 MVC는 다음의 경우에 HTTP 메시지 컨버터를 적용한다. 
  - HTTP 요청: @RequestBody , HttpEntity(RequestEntity)
  - HTTP 응답: @ResponseBody , HttpEntity(ResponseEntity)


## 스프링 부트 기본 메시지 컨버터
- 작동 순서: 
  - 0 = ByteArrayHttpMessageConverter (클래스 타입: byte[] , 미디어타입: */*)
  - 1 = StringHttpMessageConverter (클래스 타입: String , 미디어타입: */*)
  - 2 = MappingJackson2HttpMessageConverter (클래스 타입: 객체 또는 HashMap , 미디어타입 application/json 관련)

- 스프링 부트는 다양한 메시지 컨버터를 제공하는데, 대상 '클래스 타입'과 '미디어 타입' 둘을 체크해서 사용여부를 결정한다.
- 만약 만족하지 않으면 다음 메시지 컨버터로 우선순위가 넘어간다.

## RequestMappingHandlerAdapter 동작 방식
* ArgumentResolver
- 애노테이션 기반의 컨트롤러는 매우 다양한 파라미터를 사용할 수 있었다.
    - HttpServletRequest, Model 은 물론이고, @RequestParam, @ModelAttribute 같은 애노테이션 그리고 @RequestBody, HttpEntity 같은 HTTP 메시지를 처리하는 부분까지 매우 큰 유연함을 보여주었다.

- 이렇게 파라미터를 유연하게 처리할 수 있는 이유가 바로 'ArgumentResolver' 덕분!
- 애노테이션 기반 컨트롤러를 처리하는 'RequestMappingHandlerAdapter' 는 바로 이 'ArgumentResolver' 를 호출해서 컨트롤러(핸들러)가 필요로 하는 다양한 파라미터의 값(객체)을 생성한다.
- 그리고 이렇게 파리미터의 값이 모두 준비되면 컨트롤러를 호출하면서 값을 넘겨준다.


## HTTP 메시지 컨버터
- HTTP 메시지 컨버터를 사용하는 @RequestBody 도 컨트롤러가 필요로 하는 '파라미터의 값'에 사용된다. @ResponseBody 의 경우도 '컨트롤러의 반환 값'을 이용한다.
- 요청의 경우:
  - @RequestBody 를 처리하는 ArgumentResolver 가 있고, HttpEntity 를 처리하는 ArgumentResolver 가 있다.
  - 이 ArgumentResolver 들이 HTTP 메시지 컨버터를 사용해서 필요한 객체를 생성하는 것이다.
- 응답의 경우:
  - @ResponseBody 와 HttpEntity 를 처리하는 'ReturnValueHandler' 가 있다.
  - 그리고 여기에서 HTTP 메시지 컨버터를 호출해서 응답 결과를 만든다.

