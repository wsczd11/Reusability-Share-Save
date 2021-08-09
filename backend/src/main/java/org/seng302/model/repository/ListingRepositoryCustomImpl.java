package org.seng302.model.repository;

import org.seng302.model.Listing;
import org.seng302.model.enums.BusinessType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.query.QueryUtils;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Custom Implementation of Listing Repository for searching
 */
public class ListingRepositoryCustomImpl implements ListingRepositoryCustom {

    @Autowired
    private EntityManager entityManager;

    /**
     * Search for listings by product name and optional filters.
     *
     * @param names        A list of product names.
     * @param pageable     A pageable object containing the requested page number, the number of results in a page and a sort object.
     * @param businessType The type of a business to search for. (Optional)
     * @param minimumPrice Lower end of prices to include in search. (Optional)
     * @param maximumPrice Higher end of prices to include in search. (Optional)
     * @param fromDate     Earlier end of close dates to include in search. (Optional)
     * @param toDate       Later end of close dates to include in search. (Optional)
     * @return A Page object containing all matching listing results.
     *
     * Preconditions:  A non-null list of names to search for product names.
     *                 A non-null pageable object.
     * Postconditions: A page object containing all matching listing results.
     */
    @Override
    public Page<Listing> findAllListingsByProductName(
            List<String> names, Pageable pageable,
            BusinessType businessType,
            Double minimumPrice, Double maximumPrice,
            LocalDateTime fromDate, LocalDateTime toDate
    ) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Listing> query = criteriaBuilder.createQuery(Listing.class);
        Root<Listing> listing = query.from(Listing.class);

        Path<String> namePath = listing.get("inventoryItem").get("product").get("name");

        ArrayList<Predicate> predicates = getNamePredicates(names, namePath, criteriaBuilder);

        return getListings(pageable, businessType, minimumPrice, maximumPrice, fromDate, toDate, criteriaBuilder, query, listing, predicates);
    }

    /**
     * Search for listings by location and optional filters.
     *
     * @param locations    A list of locations
     * @param pageable     A pageable object containing the requested page number, the number of results in a page and a sort object.
     * @param businessType The type of a business to search for. (Optional)
     * @param minimumPrice Lower end of prices to include in search. (Optional)
     * @param maximumPrice Higher end of prices to include in search. (Optional)
     * @param fromDate     Earlier end of close dates to include in search. (Optional)
     * @param toDate       Later end of close dates to include in search. (Optional)
     * @return A Page object containing all matching listing results.
     *
     * Preconditions:  A non-null list of locations to search for.
     *                 A non-null pageable object.
     * Postconditions: A page object containing all matching listing results.
     */
    @Override
    public Page<Listing> findAllListingsByLocation(List<String> locations, Pageable pageable, BusinessType businessType, Double minimumPrice, Double maximumPrice, LocalDateTime fromDate, LocalDateTime toDate) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Listing> query = criteriaBuilder.createQuery(Listing.class);

        Root<Listing> listing = query.from(Listing.class);

        Path<String> addressPath = listing.get("inventoryItem").get("product").get("business").get("address");

        ArrayList<Predicate> predicates = new ArrayList<>();
        for (String location : locations) {
            if (location.startsWith("\"") && location.endsWith("\"")) {
                location = location.replaceAll("^\"+|\"+$", ""); // Remove quotations.
                predicates.add(criteriaBuilder.equal(addressPath.get("suburb"), location));
                predicates.add(criteriaBuilder.equal(addressPath.get("region"), location));
                predicates.add(criteriaBuilder.equal(addressPath.get("city"), location));
                predicates.add(criteriaBuilder.equal(addressPath.get("country"), location));
            } else {
                predicates.add(criteriaBuilder.like(criteriaBuilder.upper(addressPath.get("suburb")), "%" + location.toUpperCase() + "%"));
                predicates.add(criteriaBuilder.like(criteriaBuilder.upper(addressPath.get("region")), "%" + location.toUpperCase() + "%"));
                predicates.add(criteriaBuilder.like(criteriaBuilder.upper(addressPath.get("city")), "%" + location.toUpperCase() + "%"));
                predicates.add(criteriaBuilder.like(criteriaBuilder.upper(addressPath.get("country")), "%" + location.toUpperCase() + "%"));
            }
        }
        return getListings(pageable, businessType, minimumPrice, maximumPrice, fromDate, toDate, criteriaBuilder, query, listing, predicates);
    }

    /**
     * Search for listings by business name and optional filters.
     *
     * @param names        A list of business names.
     * @param pageable     A pageable object containing the requested page number, the number of results in a page and a sort object.
     * @param businessType The type of a business to search for. (Optional)
     * @param minimumPrice Lower end of prices to include in search. (Optional)
     * @param maximumPrice Higher end of prices to include in search. (Optional)
     * @param fromDate     Earlier end of close dates to include in search. (Optional)
     * @param toDate       Later end of close dates to include in search. (Optional)
     * @return A Page object containing all matching listing results.
     *
     * Preconditions:  A non-null list of names to search for businesses.
     *                 A non-null pageable object.
     * Postconditions: A page object containing all matching listing results.
     */
    @Override
    public Page<Listing> findAllListingsByBusinessName(List<String> names, Pageable pageable, BusinessType businessType, Double minimumPrice, Double maximumPrice, LocalDateTime fromDate, LocalDateTime toDate) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Listing> query = criteriaBuilder.createQuery(Listing.class);

        Root<Listing> listing = query.from(Listing.class);

        Path<String> businessNamePath = listing.get("inventoryItem").get("product").get("business").get("name");

        ArrayList<Predicate> predicates = getNamePredicates(names, businessNamePath, criteriaBuilder);

        return getListings(pageable, businessType, minimumPrice, maximumPrice, fromDate, toDate, criteriaBuilder, query, listing, predicates);
    }

    /**
     * Gets Predicates that contain only one field (ie Product name).
     *
     * @param names list of names to search
     * @param path path to required field
     * @param criteriaBuilder CriteriaBuilder object
     * @return ArrayList of Predicates
     *
     * Preconditions: path is valid
     *                names is not empty
     * Postconditions: List of predicates
     */
    private ArrayList<Predicate> getNamePredicates(List<String> names, Path<String> path, CriteriaBuilder criteriaBuilder) {
        ArrayList<Predicate> predicates = new ArrayList<>();
        for (String name : names) {
            if (name.startsWith("\"") && name.endsWith("\"")) {
                name = name.replaceAll("^\"+|\"+$", ""); // Remove quotations.
                predicates.add(criteriaBuilder.equal(path, name));
            } else {
                predicates.add(criteriaBuilder.like(criteriaBuilder.upper(path), "%" + name.toUpperCase() + "%"));
            }
        }
        return predicates;
    }

    /**
     * Gets the page of Listings from predicates and applies the optional filters.
     *
     * @param pageable Pageable for Pagination/Sorting
     * @param businessType Type of Business
     * @param minimumPrice Lower end of price range
     * @param maximumPrice Higher end of price range
     * @param fromDate Earlier date of close date range
     * @param toDate Later date of close date range
     * @param criteriaBuilder Criteria builder
     * @param query Query for Listings location
     * @param listing Root for listing location
     * @param predicates Predicates from searchQuery
     * @return A Page of Listings that apply to filters (can be empty)
     *
     * Preconditions:  predicates contains at least one Predicate
     *                 pageable is a valid Pageable
     *                 query and listing are for the same place
     * Postconditions: A matching Page of Listings
     */
    private Page<Listing> getListings(Pageable pageable, BusinessType businessType, Double minimumPrice, Double maximumPrice, LocalDateTime fromDate, LocalDateTime toDate, CriteriaBuilder criteriaBuilder, CriteriaQuery<Listing> query, Root<Listing> listing, ArrayList<Predicate> predicates) {

        // Optional filters
        ArrayList<Predicate> predicateList = new ArrayList<>();
        if (businessType != null) {
            // where businessType = type
            Predicate predicateForBusinessType = criteriaBuilder.equal(listing.get("inventoryItem").get("product").get("business").get("businessType"), businessType);
            predicateList.add(predicateForBusinessType);
        }
        if (minimumPrice != null) {
            Predicate predicateForMinimumPrice = criteriaBuilder.greaterThanOrEqualTo(listing.get("price"), minimumPrice);
            predicateList.add(predicateForMinimumPrice);
        }
        if (maximumPrice != null) {
            Predicate predicateForMaximumPrice = criteriaBuilder.lessThanOrEqualTo(listing.get("price"), maximumPrice);
            predicateList.add(predicateForMaximumPrice);
        }
        if (fromDate != null) {
            Predicate predicateForFromDate = criteriaBuilder.greaterThanOrEqualTo(
                    listing.get("closes").as(LocalDateTime.class), fromDate
            );
            predicateList.add(predicateForFromDate);
        }
        if (toDate != null) {
            Predicate predicateForToDate = criteriaBuilder.lessThanOrEqualTo(
                    listing.get("closes").as(LocalDateTime.class), toDate
            );
            predicateList.add(predicateForToDate);
        }

        predicateList.add(criteriaBuilder.or(predicates.toArray(new Predicate[predicates.size()])));

        // the where clause of the query
        query.where(criteriaBuilder.and(predicateList.toArray(new Predicate[predicateList.size()])));

        // the order by clause of the query
        query.orderBy(QueryUtils.toOrders(pageable.getSort(), listing, criteriaBuilder));

        // this query fetches the listings as per the page limit
        List<Listing> listings = entityManager.createQuery(query).setFirstResult((int) pageable.getOffset()).setMaxResults(pageable.getPageSize()).getResultList();

        // create a count query used to display "Showing 1-5 of x results"
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<Listing> listingRootCount = countQuery.from(Listing.class);
        countQuery.select(criteriaBuilder.count(listingRootCount)).where(criteriaBuilder.or(predicates.toArray(new Predicate[predicates.size()])));

        // fetches the count of all listings as per given criteria
        Long count = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(listings, pageable, count);
    }
}