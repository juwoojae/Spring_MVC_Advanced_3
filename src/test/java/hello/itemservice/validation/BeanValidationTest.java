package hello.itemservice.validation;

import hello.itemservice.domain.item.Item;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import java.util.Set;


public class BeanValidationTest {

    @Test
    void beanValidation(){
        //검증기 생성
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Item item = new Item();

        item.setItemName(" ");//공백입력
        item.setPrice(0);
        item.setQuantity(10000);
        /**
         * 검증 대상 (item) 을 직접 검증기에 넣고 그 결과를 받는다. Set 에는 ConstraintViolation 이라는 검증 오류가 담긴다
         * 따라서 결과가 비어있으면 검증 오류라는 것이다
         */
        Set<ConstraintViolation<Item>> violations = validator.validate(item);
        for (ConstraintViolation<Item> violation : violations) {
            System.out.println("violation = " + violation);
            System.out.println("violation.getMessage() = " + violation.getMessage());
        }
    }

}
