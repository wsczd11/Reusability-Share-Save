package org.seng302.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.seng302.view.outgoing.SoldListingPayload;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seng302.exceptions.IllegalSoldListingArgumentException;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Class for Sold listings
 * This stores information about sales that have been completed in the past.
 * May be used for analysis in the future. This means we don't want to change the details
 * i.e. modify the records.
 */
@Data
@NoArgsConstructor
@Entity
public class SoldListing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business", nullable = false)
    private Business business;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer", nullable = false)
    private User customer;

    @Column(name = "saleDate", nullable = false)
    private LocalDateTime saleDate;

    @Column(name = "listingDate", nullable = false)
    private LocalDateTime listingDate;

    @JoinColumn(name = "productId", nullable = false)
    private String productId;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "bookmarks", nullable = false)
    private Integer bookmarks;

    private static final Logger logger = LogManager.getLogger(SoldListing.class.getName());

    public SoldListing(Business business, User customer, LocalDateTime listingDate, String productId, Integer quantity, Double price, Integer bookmarks) throws IllegalSoldListingArgumentException {
        if (business != null) {
            this.business = business;
        } else {
            logger.error("Sold Listing Creation Error - Business is null");
            throw new IllegalSoldListingArgumentException("Invalid business");
        }
        if (customer != null) {
            this.customer = customer;
        } else {
            logger.error("Sold Listing Creation Error - Customer is null");
            throw new IllegalSoldListingArgumentException("Invalid customer");
        }
        if (listingDate != null && listingDate.isBefore(LocalDateTime.now())) {
            this.listingDate = listingDate;
        } else {
            logger.error("Sold Listing Creation Error - listingDate is null or after saleDate");
            throw new IllegalSoldListingArgumentException("Invalid listingDate");
        }
        if (productId != null && !productId.equals("")) {
            this.productId = productId;
        } else {
            logger.error("Sold Listing Creation Error - productId is null");
            throw new IllegalSoldListingArgumentException("Invalid productId");
        }
        if (quantity != null && quantity >= 1) {
            this.quantity = quantity;
        } else {
            logger.error("Sold Listing Creation Error - quantity is null or less than 1");
            throw new IllegalSoldListingArgumentException("Invalid quantity");
        }
        if (price != null && price > 0) {
            this.price = price;
        } else {
            logger.error("Sold Listing Creation Error - price is null or less than 0");
            throw new IllegalSoldListingArgumentException("Invalid price");
        }
        if (bookmarks != null && bookmarks >= 0) {
            this.bookmarks = bookmarks;
        } else {
            logger.error("Sold Listing Creation Error - bookmarks is null or less than 0");
            throw new IllegalSoldListingArgumentException("Invalid bookmarks");
        }

        this.saleDate = LocalDateTime.now();
    }

    /**
     * Converts a SoldListing into its payload representation
     * @return SoldListingPayload
     * @throws Exception an exception potentially thrown by the conversion of the user to a payload
     */
    public SoldListingPayload toSoldListingPayload() throws Exception {
        return new SoldListingPayload(id, saleDate.toString(), listingDate.toString(), productId, quantity, price, bookmarks, customer.toUserPayloadSecure());
    }

    /**
     * Overridden for debugging and testing purposes.
     * @return The JSON format of the sold listing
     */
    @SneakyThrows
    @Override
    public String toString() {
        return "{\"id\":" + id + "," +
                "\"saleDate\":\"" + saleDate + "\"," +
                "\"listingDate\":\"" + listingDate + "\"," +
                "\"productId\":\"" + productId + "\"," +
                "\"quantity\":" + quantity + "," +
                "\"price\":" + price + "," +
                "\"bookmarks\":" + bookmarks +
                ",\"customer\":" + customer.toUserPayloadSecure() + "}";
    }
}
