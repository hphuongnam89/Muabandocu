package com.cho2hand.marketplace.service.impl.listing;

import static com.cho2hand.marketplace.specification.ListingSpecifications.*;

import com.cho2hand.marketplace.dto.listing.*;
import com.cho2hand.marketplace.entity.listing.Listing;
import com.cho2hand.marketplace.exception.*;
import com.cho2hand.marketplace.mapper.listing.ListingMapper;
import com.cho2hand.marketplace.repository.category.CategoryRepository;
import com.cho2hand.marketplace.repository.listing.*;
import com.cho2hand.marketplace.repository.location.LocationRepository;
import com.cho2hand.marketplace.repository.media.ListingImageRepository;
import com.cho2hand.marketplace.service.listing.ListingService;
import com.cho2hand.marketplace.service.security.CaptchaService;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ListingServiceImpl implements ListingService {
    private static final Logger log = LoggerFactory.getLogger(ListingServiceImpl.class);
    private static final int MONTHLY_LISTING_LIMIT = 3;
    private final ListingRepository listings;
    private final CategoryRepository categories;
    private final ItemConditionRepository conditions;
    private final LocationRepository locations;
    private final ListingStatusRepository statuses;
    private final ListingImageRepository images;
    private final ListingMapper mapper;
    private final CaptchaService captchaService;

    public ListingServiceImpl(ListingRepository listings, CategoryRepository categories, ItemConditionRepository conditions,
            LocationRepository locations, ListingStatusRepository statuses, ListingImageRepository images, ListingMapper mapper,
            CaptchaService captchaService) {
        this.listings = listings; this.categories = categories; this.conditions = conditions; this.locations = locations;
        this.statuses = statuses; this.images = images; this.mapper = mapper;
        this.captchaService = captchaService;
    }

    public ListingResponse create(Long seller, CreateListingRequest request) {
        captchaService.verify(request.captchaToken());
        enforceMonthlyQuota(seller);
        validate(request.categoryId(), request.conditionId(), request.locationId());
        var listing = new Listing();
        listing.setSellerUserId(seller); listing.setCategoryId(request.categoryId()); listing.setConditionId(request.conditionId());
        listing.setLocationId(request.locationId()); listing.setAddressDetail(request.addressDetail().trim()); listing.setTitle(request.title().trim());
        listing.setDescription(request.description().trim()); listing.setPriceAmount(request.priceAmount());
        listing.setCurrencyCode("VND"); listing.setListingStatusId(activeStatus().getId()); listing.setPublishedAt(Instant.now());
        return response(listings.save(listing));
    }

    @Transactional(readOnly = true)
    public ListingResponse get(Long id) {
        return response(listings.findById(id).filter(listing -> listing.getArchivedAt() == null)
                .orElseThrow(() -> new ListingNotFoundException(id)));
    }

    public ListingResponse update(Long seller, Long id, UpdateListingRequest request) {
        var listing = owned(seller, id);
        if (request.categoryId() != null) { validateCategory(request.categoryId()); listing.setCategoryId(request.categoryId()); }
        if (request.conditionId() != null) { validateCondition(request.conditionId()); listing.setConditionId(request.conditionId()); }
        if (request.locationId() != null) { validateLocation(request.locationId()); listing.setLocationId(request.locationId()); }
        if (request.title() != null) listing.setTitle(request.title().trim());
        if (request.description() != null) listing.setDescription(request.description().trim());
        if (request.priceAmount() != null) listing.setPriceAmount(request.priceAmount());
        if (request.addressDetail() != null) listing.setAddressDetail(request.addressDetail().trim());
        return response(listing);
    }

    public void archive(Long seller, Long id) { owned(seller, id).setArchivedAt(Instant.now()); }

    @Transactional(readOnly = true)
    public Page<ListingResponse> mine(Long seller, int page, int size) {
        return page(listings.findBySellerUserIdAndArchivedAtIsNullOrderByPublishedAtDescIdDesc(seller, PageRequest.of(page, size)));
    }

    @Transactional(readOnly = true)
    public Page<ListingResponse> newest(String keyword, Long categoryId, Long conditionId, Long locationId, Long sellerUserId,
            BigDecimal minPrice, BigDecimal maxPrice, int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("publishedAt"), Sort.Order.desc("id")));
        Specification<Listing> spec = Specification.where(active(activeStatus().getId())).and(keyword(keyword))
                .and(category(categoryId)).and(condition(conditionId)).and(locationIn(locationScope(locationId))).and(seller(sellerUserId)).and(price(minPrice, maxPrice));
        return page(listings.findAll(spec, pageable));
    }

    private Page<ListingResponse> page(Page<Listing> page) {
        return new PageImpl<>(responses(page.getContent()), page.getPageable(), page.getTotalElements());
    }

    private ListingResponse response(Listing listing) { return responses(List.of(listing)).getFirst(); }

    private List<ListingResponse> responses(List<Listing> listings) {
        if (listings.isEmpty()) return List.of();
        var listingIds = listings.stream().map(Listing::getId).toList();
        Map<Long, Long> covers = images.findCoverImagesByListingIds(listingIds).stream()
                .collect(java.util.stream.Collectors.toMap(image -> image.getId().getListingId(), image -> image.getId().getMediaId()));
        return listings.stream().map(listing -> mapper.toResponse(listing, coverUrl(listing.getId(), covers.get(listing.getId())))).toList();
    }

    private String coverUrl(Long listingId, Long mediaId) {
        return mediaId == null ? null : "/api/v1/listings/" + listingId + "/images/" + mediaId + "/content";
    }

    private Listing owned(Long seller, Long id) {
        var listing = listings.findById(id).orElseThrow(() -> new ListingNotFoundException(id));
        if (!seller.equals(listing.getSellerUserId())) throw new ListingAccessDeniedException();
        return listing;
    }

    private void validate(Long categoryId, Long conditionId, Long locationId) {
        validateCategory(categoryId); validateCondition(conditionId); validateLocation(locationId);
    }
    private void validateCategory(Long id) { if (categories.findByIdAndActiveTrue(id).filter(value -> value.isLeaf()).isEmpty()) throw new LookupValueNotFoundException("Leaf category", id.toString()); }
    private void validateCondition(Long id) { if (conditions.findByIdAndActiveTrue(id).isEmpty()) throw new LookupValueNotFoundException("Condition", id.toString()); }
    private void validateLocation(Long id) { if (locations.findByIdAndActiveTrue(id).filter(value -> value.getLevel() == 3).isEmpty()) throw new LookupValueNotFoundException("Ward", id.toString()); }
    private List<Long> locationScope(Long id) {
        if (id == null) return null;
        var location = locations.findByIdAndActiveTrue(id).orElseThrow(() -> new LookupValueNotFoundException("Location", id.toString()));
        if (location.getLevel() == 3) return List.of(id);
        var childIds = locations.findByParentLocationIdAndActiveTrue(id).stream().map(com.cho2hand.marketplace.entity.location.Location::getId).toList();
        return childIds.isEmpty() ? List.of(id) : childIds;
    }
    private com.cho2hand.marketplace.entity.listing.ListingStatus activeStatus() { return statuses.findByCodeAndActiveTrue("ACTIVE").orElseThrow(() -> new LookupValueNotFoundException("Listing status", "ACTIVE")); }
    private void enforceMonthlyQuota(Long seller) {
        var start = YearMonth.now(ZoneId.of("Asia/Ho_Chi_Minh")).atDay(1).atStartOfDay(ZoneId.of("Asia/Ho_Chi_Minh")).toInstant();
        var used = listings.countPublishedWithImagesThisMonth(seller, start);
        if (used >= MONTHLY_LISTING_LIMIT) {
            log.warn("monthly_listing_quota_exceeded sellerUserId={} used={} limit={}", seller, used, MONTHLY_LISTING_LIMIT);
            throw new QuotaExceededException("Bạn đã dùng hết 3 lượt đăng tin trong tháng này.");
        }
    }
}
