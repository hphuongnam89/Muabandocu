package com.cho2hand.marketplace.service.listing;
import com.cho2hand.marketplace.dto.listing.*;
import org.springframework.data.domain.Page;import java.math.BigDecimal;
public interface ListingService { ListingResponse create(Long sellerId, CreateListingRequest request); ListingResponse get(Long id); ListingResponse update(Long sellerId, Long id, UpdateListingRequest request); void archive(Long sellerId, Long id); Page<ListingResponse> mine(Long sellerId,int page,int size); Page<ListingResponse> newest(String keyword,Long categoryId,Long conditionId,Long locationId,Long sellerUserId,BigDecimal minPrice,BigDecimal maxPrice,int page,int size); }
