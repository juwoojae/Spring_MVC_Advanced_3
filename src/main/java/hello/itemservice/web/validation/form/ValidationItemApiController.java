package hello.itemservice.web.validation.form;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Spring MVC 1 정리
 * 1. @ModelAttribute 는 HTTP 요청 파라미터(URL 쿼리 스트링, Post Form)를 다룰 때 사용한다.
 * 2. @RequestBody 는 HTTP Body 의 데이터를 객체로 변환할때 사용한다. 주로 API JSON 요청을 다룰때 사용한다
 *
 * Spring MVC 의 HTTPMessageConverter 는 HTTP 요청/응답 의 body 를 자바객체 <-> json/xml/문자열 등으로 변환해주는 컴포넌트 이다.
 * 즉, json을 우리가 사용할 자바의 객체 데이터로 가공하는 용도
 *
 * Request
 * 1. 클라이언트가 JSON 같은 데이터를 Body 데이터를 보낸다(Content-Type = application/json)
 * 2. 컨트롤러 메서드에 @RequestBody Item item 같은 파라미터가 있으면,
 * 3. MappingJackson2HttpMessageConverter 같은 컨버터가 동작하여 json -> Item 객체 변환
 *
 * Response
 * 1. 컨트롤러가 객체(Item) 을 리턴하고, @ResponseBody 가 붙어 있으면,
 * 2. 메세지 컴버터가 객체 -> json 으로 변환
 * 3. http 응답 body 에서 json 의 문자열을 씀
 *
 * API 의 경우 3가지를 생각해야 한다.
 * 성공 요청 : 성공
 * 실패 요청 : JSON 을 객체로 생성하는 것 자체가 실패함
 * 검증 오류 요청: JSON 을 객체로 생성하는 것은 성공했고, 검증에서 실패함
 *
 * 실패 요청의 경우 (Type Conversion)
 * HttpMessageConverter 에서 요청 JSON을 ItemSaveForm 객체로 생성하는데 실패한다.
 * 이 경우는 ItemSaveForm 객체를 만들지 못하기 때문에 컨트롤러 자체가 호출되지 않고 그 전에 예외가 발생한다. 물
 * 론 Validator도 실행되지 않는다.
 *
 * 검증 오류의 요청
 *  ObjectError 와 FieldError 를 반환한다. 스프링이 이 객
 * 체를 JSON으로 변환해서 클라이언트에 전달
 *
 * @ModelAttribute vs @RequestBody
 *
 * @ModelAttribute 는 필드 단위로 정교하게 바인딩이 적용된다. 특정 필드가 바인딩 되지 않아도 나머지 필드
 * 는 정상 바인딩 되고, Validator를 사용한 검증도 적용할 수 있다.
 * @RequestBody 는 HttpMessageConverter 단계에서 JSON 데이터를 객체로 변경하지 못하면 이후 단계 자
 * 체가 진행되지 않고 예외가 발생한다. 컨트롤러도 호출되지 않고, Validator도 적용할 수 없다.
 */
@Slf4j
@RestController
@RequestMapping("/validation/api/items")
public class ValidationItemApiController {

    @PostMapping("/add")
    public Object addItem(@RequestBody @Validated ItemSaveForm form, BindingResult bindingResult) {
        log.info("API 컨트롤러 호출");

        if (bindingResult.hasErrors()) {
            log.info("검증 오류 발생 errors = {} ", bindingResult);
            return bindingResult.getAllErrors();
        }

        log.info("성공 로직 실행");
        return form;
    }
}
