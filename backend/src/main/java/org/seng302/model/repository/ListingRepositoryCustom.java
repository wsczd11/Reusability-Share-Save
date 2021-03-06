package org.seng302.model.repository;

import org.seng302.exceptions.FailedToDeleteListingException;
import org.seng302.model.Listing;
import org.seng302.model.enums.BusinessType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface ListingRepositoryCustom {

    /**
     * Finds all listings with matching product names
     * @param names list of product names
     * @param pageable Pageable
     * @param businessTypes types of businesses
     * @param minimumPrice minimum price for price range
     * @param maximumPrice maximum price for price range
     * @param fromDate from date for close date range
     * @param toDate to date for close date range
     * @param barcode a barcode to match listings to
     * @return returns a Page of listings
     */
    Page<Listing> findAllListingsByProductName(
            List<String> names, Pageable pageable,
            List<BusinessType> businessTypes, Double minimumPrice, Double maximumPrice, LocalDateTime fromDate, LocalDateTime toDate, String barcode
    );

    /**
     * Finds all listings with matching locations
     * @param locations list of locations
     * @param pageable Pageable
     * @param businessTypes types of businesses
     * @param minimumPrice minimum price for price range
     * @param maximumPrice maximum price for price range
     * @param fromDate from date for close date range
     * @param toDate to date for close date range
     * @param barcode a barcode to match listings to
     * @return returns a Page of listings
     */
    Page<Listing> findAllListingsByLocation(
            List<String> locations, Pageable pageable,
            List<BusinessType> businessTypes, Double minimumPrice, Double maximumPrice, LocalDateTime fromDate, LocalDateTime toDate, String barcode
    );

    /**
     * Finds all listings with matching business names
     * @param names list of business names
     * @param pageable Pageable
     * @param businessTypes types of businesses
     * @param minimumPrice minimum price for price range
     * @param maximumPrice maximum price for price range
     * @param fromDate from date for close date range
     * @param toDate to date for close date range
     * @param barcode a barcode to match listings to
     * @return returns a Page of listings
     */
    Page<Listing> findAllListingsByBusinessName(
            List<String> names, Pageable pageable,
            List<BusinessType> businessTypes, Double minimumPrice, Double maximumPrice, LocalDateTime fromDate, LocalDateTime toDate, String barcode
    );


    /**
     * Given a listing id attempt to delete it. And created a notification for all bookmarked users.
     *
     * @param id This is the id of the listing to be deleted if exists.
     * @return Returns true if succeeds.
     * @throws FailedToDeleteListingException Thrown when something goes wrong. The message will contain the details.
     */
    Boolean deleteListing(Integer id) throws FailedToDeleteListingException;

}
