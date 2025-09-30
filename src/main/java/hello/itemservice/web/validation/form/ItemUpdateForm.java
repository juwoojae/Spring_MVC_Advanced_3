package hello.itemservice.web.validation.form;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

@Data
public class ItemUpdateForm {

    @NotNull
    private Long id;

    @NotBlank
    private String ItemName;

    @NotNull
    @Range(min = 1000, max = 1000000)
    private Integer price;

    @NotNull
    private Integer quantity;  //수정 폼에서 수량은 자유롭게 등록할수 있다

}
