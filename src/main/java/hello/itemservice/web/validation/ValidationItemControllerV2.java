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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/validation/v2/items")
@RequiredArgsConstructor
public class ValidationItemControllerV2 {

    private final ItemRepository itemRepository;

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "validation/v2/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v2/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item", new Item());
        return "validation/v2/addForm";
    }

    /**
     * @param bindingResult : V1 에서 errors 를 담당한다
     * BindingResult
     * 스프링이 제공하는 검증 오류를 보관하는 객체이다. 검증 오류가 발생하면 여기에 보관하면 된다.
     *
     * BindingResult 가 없으면 -> 400 오류가 발생하면서 컨트롤러가 호출되지 않고, 오류 페이지로 이동한다
     * BindingResult 가 있으면 -> @modelAttribute 의 객체에 타입 오류 등으로 바인딩이 실패하는 경우
     *    오류 정보(FieldError) 를 BindingResult에 담아서 컨트롤러를 정상 호출한다
     *
     *
     */
//    @PostMapping("/add") //BindingResult 의 위치는 @ModelAttribute 의 바로 뒤에 와야 한다. 이 객체가 타당한지 판단해야 한다
    public String addItemV1(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes) {


        if (!StringUtils.hasText(item.getItemName())) {
            bindingResult.addError(new FieldError("item", "itemName", "상품 이름은 필수입니다."));
        }
        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            bindingResult.addError(new FieldError("item", "price", item.getPrice(), false, null, null, "가격은 1,000 ~ 1,000,000 까지 허용합니다."));
        }
        if (item.getQuantity() == null || item.getQuantity() >= 9999) {
            bindingResult.addError(new FieldError("item", "quantity", item.getQuantity(), false, null, null, "수량은 최대 9,999 까지 허용합니다"));
        }

        //특정 필드가 아님 복합 룰 검증
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                bindingResult.addError(new ObjectError("item", "가격 * 수량의 합은 10,000원 이상이여야 합니다. 현재값 = " + resultPrice));
            }
        }


        //오류를 가지고 있다면
        if (bindingResult.hasErrors()) {
            log.info("errors = {} ", bindingResult);
            return "validation/v2/addForm";
        }
        //성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }
    /**
     * 오류가 발생한 객체 이름, 오류 필드, 사용자가 입력한 값(거절된 값) 타입 오류 같은 바인딩 실패인지, 검증 실패인지 구분하는 값
     * 메세지 코드, 메세지에서 사용하는 인자, 기본 오류 메세지
     * FieldError(String objectName, String field, @Nullable Object rejectedValue, boolean bindingFailure,
     * 			            @Nullable String[] codes, @Nullable Object[] arguments, @Nullable String defaultMessage)
     * 사용자의 입력 데이터가 컨트롤러에 @ModelAttribute 에 바인딩 되는 시점에 오류가 발생하면, 모델 객체에 사용자 입력 값을
     * 유지하기 어렵다. 별도로 보관하기위해서 FieldError 가 파라메터로 받아서 유지한다
     *
     */
    //@PostMapping("/add")
    public String addItemV2(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        if (!StringUtils.hasText(item.getItemName())) {
            bindingResult.addError(new FieldError("item", "itemName", item.getItemName(), false, null, null, "상품 이름은 필수입니다."));
        }
        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            bindingResult.addError(new FieldError("item", "price", item.getPrice(), false, null, null, "가격은 1,000 ~ 1,000,000 까지 허용합니다."));
        }
        if (item.getQuantity() == null || item.getQuantity() >= 9999) {
            bindingResult.addError(new FieldError("item", "quantity", "수량은 최대 9,999 까지 허용합니다"));
        }

        //특정 필드가 아님 복합 룰 검증
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                bindingResult.addError(new ObjectError("item", "가격 * 수량의 합은 10,000원 이상이여야 합니다. 현재값 = " + resultPrice));
            }
        }

        //오류를 가지고 있다면
        if (bindingResult.hasErrors()) {
            log.info("errors = {} ", bindingResult);
            return "validation/v2/addForm";
        }
        //성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }
    //@PostMapping("/add")
    public String addItemV3(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        log.info("objectName={}", bindingResult.getObjectName());
        log.info("target={}", bindingResult.getTarget());

        if (!StringUtils.hasText(item.getItemName())) {
            bindingResult.addError(new FieldError("item", "itemName", item.getItemName(), false, new String[]{"required.item.itemName"},null,null));
        }
        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            bindingResult.addError(new FieldError("item",  "price", item.getPrice(), false, new String[]{"range.item.price"}, new Object[]{1000, 1000000}, null));
        }
        if (item.getQuantity() == null || item.getQuantity() >= 9999) {
            bindingResult.addError(new FieldError("item", "quantity",item.getQuantity(),false,new String[]{"max.item.quantity"}, new Object[]{9999}, null));
        }

        //특정 필드가 아님 복합 룰 검증
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                bindingResult.addError(new ObjectError("item",new String[]{"totalPriceMin"},new Object[]{10000,resultPrice} ,null));
            }
        }

        //오류를 가지고 있다면
        if (bindingResult.hasErrors()) {
            log.info("errors = {} ", bindingResult);
            return "validation/v2/addForm";
        }
        //성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);

        return "redirect:/validation/v2/items/{itemId}";
    }

    /**
     * BindingResult 가 제공하는 rejectValue(), reject() 를 사용하면 FieldError, ObjectError 의 인스턴스를 생성하고 생성자 그 수많은 파라메터를 안넣어도 된다
     * FieldError, ObjectError 는  String[] codes 에 오류 코드를 다 추가해줘야하지만 rejectValue,reject 는 MessageCodesResolver 를 제공하므로
     * 오류코드를 알아서 생성후 추가해준다
     */
    @PostMapping("/add")
    public String addItemV4(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        //Object 의 Name 은 이미 bindingResult 가 알고 있다
        log.info("objectName={}", bindingResult.getObjectName());
        log.info("target={}", bindingResult.getTarget());

        if (!StringUtils.hasText(item.getItemName())) {
            bindingResult.rejectValue("itemName", "required");
        }
        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            bindingResult.rejectValue("price","range", new Object[]{1000,1000000},null);
        }
        if (item.getQuantity() == null || item.getQuantity() >= 9999) {
            bindingResult.rejectValue("quantity","max",new Object[]{9999},null);
        }

        //특정 필드가 아님 복합 룰 검증
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                bindingResult.reject("totalPrinceMin", new Object[]{10000, resultPrice}, null);
            }
        }

        //오류를 가지고 있다면
        if (bindingResult.hasErrors()) {
            log.info("errors = {} ", bindingResult);
            return "validation/v2/addForm";
        }
        //성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);

        return "redirect:/validation/v2/items/{itemId}";
    }
    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v2/editForm";
    }


    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @ModelAttribute Item item) {
        itemRepository.update(itemId, item);
        return "redirect:/validation/v2/items/{itemId}";
    }

}

