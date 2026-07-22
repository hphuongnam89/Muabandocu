package com.cho2hand.marketplace.repository.listing;
import com.cho2hand.marketplace.entity.listing.Listing;
import java.time.Instant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
public interface ListingRepository extends JpaRepository<Listing, Long>, JpaSpecificationExecutor<Listing> {
    Page<Listing> findByListingStatusIdAndArchivedAtIsNullOrderByPublishedAtDescIdDesc(Long statusId, Pageable pageable);
    Page<Listing> findByListingStatusIdAndCategoryIdAndArchivedAtIsNullOrderByPublishedAtDescIdDesc(Long statusId, Long categoryId, Pageable pageable);
    Page<Listing> findBySellerUserIdAndArchivedAtIsNullOrderByPublishedAtDescIdDesc(Long sellerUserId, Pageable pageable);
    long countBySellerUserIdAndPublishedAtGreaterThanEqual(Long sellerUserId, Instant publishedAt);
}
