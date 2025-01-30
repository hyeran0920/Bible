package com.library.bible.cart.controller;

import com.library.bible.cart.model.Cart;
import com.library.bible.cart.service.ICartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/carts")
public class CartController {

    private final ICartService cartService;

    public CartController(ICartService cartService) {
        this.cartService = cartService;
    }

    // 전체 장바구니 목록 조회
    @GetMapping("/list")
    public ResponseEntity<List<Cart>> getAllCarts(@RequestParam("memId") int memId) {
        List<Cart> cartList = cartService.getAllCarts(memId);
        return ResponseEntity.ok(cartList);
    }
    /*
    @GetMapping("/user/cart")
	public ResponseEntity<List<Cart>> getUserCart(@AuthenticationPrincipal UserDetails userDetails) {
	    int memId = Integer.parseInt(userDetails.getUsername()); // JWT에서 사용자 ID 추출
	    List<Cart> cartList = cartService.getAllCarts(memId);
	    return ResponseEntity.ok(cartList);
	}
    */
    
    //특정 장바구니 조회 (Cart ID)
    @GetMapping("/{cartId}")
    public ResponseEntity<Cart> getCart(@PathVariable int cartId) {
        Cart cart = cartService.getCart(cartId);
        return ResponseEntity.ok(cart);
    }

    // 장바구니에 책 추가
    @PostMapping("/add")
    public ResponseEntity<String> addCart(@RequestBody Map<String, Integer> request) {
        int bookId = request.get("bookId");
        int memId = request.get("memId");
        int bookCount = request.get("bookCount");

        cartService.addCart(bookId, memId, bookCount);
        return ResponseEntity.ok("장바구니에 추가되었습니다.");
    }

    // 장바구니 수량 업데이트
    @PutMapping("/update")
    public ResponseEntity<String> updateCart(@RequestBody Map<String, Integer> request) {
        int cartId = request.get("cartId");
        int newCount = request.get("newCount");

        cartService.updateCart(cartId, newCount);
        return ResponseEntity.ok("장바구니가 업데이트되었습니다.");
    }

    // 장바구니 항목 삭제
    @DeleteMapping("/delete/{cartId}")
    public ResponseEntity<String> deleteCart(@PathVariable int cartId) {
        cartService.deleteCart(cartId);
        return ResponseEntity.ok("장바구니에서 삭제되었습니다.");
    }

    // 선택한 장바구니 항목의 가격 조회
    @PostMapping("/prices")
    public ResponseEntity<List<Integer>> getSelectedCartPrices(@RequestBody List<Integer> cartIds) {
        List<Integer> prices = cartService.getSelectedCartPrices(cartIds);
        return ResponseEntity.ok(prices);
    }
}
