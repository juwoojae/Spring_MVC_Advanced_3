package hello.itemservice.web.validation;

import ch.qos.logback.core.util.StringUtil;
import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/validation/v3/items")
@RequiredArgsConstructor
public class ValidationItemControllerV3 {

    private final ItemRepository itemRepository;

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "validation/v3/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v3/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item", new Item());
        return "validation/v3/addForm";
    }

    /**
     * 1. LocalValidatorFactoryBean 은 글로벌 Validator 로 등록한다. 이 Validator 는 @NotNull 같은 에노테이션을 보고 검증을 수행한다
     * 이렇게 글로벌 Validator 가 적용되어 있기 때문에 @Validated , @Valid 를 적용해서 검증 오류가 발생하면, FieldError, ObjectError 를
     * 생성해서 BindingResult 에 담아 준다
     * 2. Bean - Validation 에러 코드
     * 오류 코드가 애노테이션 이름으로 자동으로 등록 된다. 마치 (typeMismatch 와 유사)
     * 이 오류 코드를 기반으로 MessageCodesResolver 를 통해 4가지 메세지 코드가 생성된다
     * 이것또한 errors 메세지로 등록해서 사용할수있다 {0} : 필드명, {1}.{2} ... 에노테이션마다 다르지만 , 대부분 range
     */
    @PostMapping("/add")
    public String addItem(@Validated @ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        //특정 필드가 아님 복합 룰 검증
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();  //가격 * 수량의 합은 10000원 이상이다
            if (resultPrice < 10000) {
                bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }

        //오류를 가지고 있다면
        if (bindingResult.hasErrors()) {
            log.info("errors = {} ", bindingResult); // 그 오류들을 log 로 찍어보자
            return "validation/v3/addForm";
        }
        //성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v3/items/{itemId}";
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v3/editForm";
    }


    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @ModelAttribute Item item) {
        itemRepository.update(itemId, item);
        return "redirect:/validation/v3/items/{itemId}";
    }

}

