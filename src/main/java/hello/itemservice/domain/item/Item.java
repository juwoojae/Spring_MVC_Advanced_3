package hello.itemservice.domain.item;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.ScriptAssert;

/**
 * jakarta.validation 으로 시작하면 특정 구현에 관계 없이 제공되는 표준 인터페이스이고,
 * org.hibernate.validator 로 시작하면 하이버네이트 validator 구현체를 사용할때만 제공되는 검증기능
 *
 * <검증 순서>
 * 1. @ModelAttribute 에 의해서 파라메터 바인딩이 일어난다. 이때 각각 필드에 타입 변환(Type Conversion) 이 일어나는데 , 성골할경우
 * 다음으로
 * 실패할 경우에 typeMismatch 로 FieldError 추가
 * 2. Validator 적용
 *
 * 변환에 성공한 필드만 BeanValidation 적용
 *
 * Bean Validation - groups
 * 모델 객체를 등록할때/수정할때를 다르게 검증한다
 * 방법 2가지
 * Validation 의 groups 기능 사용하기
 * item 을 직접 사용하지 않고. itemSaveForm, ItemUpdateForm 같은 폼 전송을 위한 별도의 모델 객체를 만들어서 사용한다.
 * */
@Data
//@ScriptAssert(lang = "javascript", script = "_this.price * _this.quantity >=10000")
public class Item {

    // @NotNull(groups = UpdateCheck.class)
    private Long id;

    // @NotBlank(groups = {SaveCheck.class, UpdateCheck.class}) //빈값 + 공백만 있는 경우를 허용하지 않는다
    private String itemName;

    // @NotNull(groups = {SaveCheck.class, UpdateCheck.class}) //null 을 허용하지 않는다
    //@Range(min = 1000, max = 1000000, groups = {SaveCheck.class, UpdateCheck.class}) //100 ~ 1000000 범위 안의 값이어야 한다
    private Integer price;

    //@NotNull(groups = {SaveCheck.class, UpdateCheck.class})@Max(value = 9999, groups = {SaveCheck.class}) //최대 9999 까지만 허용한다
    private Integer quantity;

    public Item() {
    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
