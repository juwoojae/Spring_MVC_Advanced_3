package hello.itemservice.validation;

import org.junit.jupiter.api.Test;
import org.springframework.validation.DefaultMessageCodesResolver;
import org.springframework.validation.MessageCodesResolver;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * DefaultMessageCodesResolver 의 기본 메세지 생성 규칙
 * 객체 오류
 * 1. : code + "." + object name
 * 2. : code
 *
 * 예) 오류 코드: required, object name: item
 * 1. : required.item
 * 2. : required
 *
 * 필드 오류
 * 1. : code + "." + object name + "." + field
 * 2. : code + "." + field
 * 3. : code + "." + field type
 * 4. : code
 *
 * 예) 오류 코드: typeMismatch, object name "user", field "age", field type: int
 * 1. "typeMismatch.user.age"
 * 2. "typeMismatch.age"
 * 3. "typeMismatch.int"
 * rejectValue(), reject() 는 내부에서 MessageCodesResolver 를 사용한다
 */
public class MessageCodesResolverTest {

    MessageCodesResolver codesResolver = new DefaultMessageCodesResolver();

    //Object 오류
    @Test
    void messageCodesResolverObject() {
        String[] messageCodes = codesResolver.resolveMessageCodes("required", "item");
        assertThat(messageCodes).containsExactly("required.item", "required");
    }
    //Field 오류 테스트
    @Test
    void messageCodesResolverField(){
        String[] messageCodes = codesResolver.resolveMessageCodes("required", "item", "itemName", String.class);
        assertThat(messageCodes).containsExactly(
                "required.item.itemName",
                "required.itemName",
                "required.java.lang.String",
                "required"
        );
    }
}
