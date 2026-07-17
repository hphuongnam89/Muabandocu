package com.cho2hand.marketplace.controller.listing;
import com.cho2hand.marketplace.dto.listing.*;
import com.cho2hand.marketplace.service.listing.ListingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/listings") public class ListingController {
 private final ListingService service; public ListingController(ListingService s){service=s;}
 @GetMapping public Page<ListingResponse> newest(@RequestParam(required=false) @Size(max=120) String keyword,@RequestParam(required=false) @Positive Long categoryId,@RequestParam(required=false) @Positive Long conditionId,@RequestParam(required=false) @Positive Long locationId,@RequestParam(required=false) @Positive Long sellerUserId,@RequestParam(required=false) @DecimalMin("0") java.math.BigDecimal minPrice,@RequestParam(required=false) @DecimalMin("0") java.math.BigDecimal maxPrice,@RequestParam(defaultValue="0") @PositiveOrZero int page,@RequestParam(defaultValue="20") @Min(1) @Max(100) int size){return service.newest(keyword,categoryId,conditionId,locationId,sellerUserId,minPrice,maxPrice,page,size);}
 @GetMapping("/mine") public Page<ListingResponse> mine(@AuthenticationPrincipal Long userId,@RequestParam(defaultValue="0") @PositiveOrZero int page,@RequestParam(defaultValue="20") @Min(1) @Max(100) int size){return service.mine(userId,page,size);}
 @GetMapping("/{id}") public ListingResponse get(@PathVariable @Positive Long id){return service.get(id);}
 @PostMapping @ResponseStatus(HttpStatus.CREATED) public ListingResponse create(@AuthenticationPrincipal Long userId,@Valid @RequestBody CreateListingRequest request){return service.create(userId,request);}
 @PatchMapping("/{id}") public ListingResponse update(@AuthenticationPrincipal Long userId,@PathVariable @Positive Long id,@Valid @RequestBody UpdateListingRequest request){return service.update(userId,id,request);}
 @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void archive(@AuthenticationPrincipal Long userId,@PathVariable @Positive Long id){service.archive(userId,id);}
}
