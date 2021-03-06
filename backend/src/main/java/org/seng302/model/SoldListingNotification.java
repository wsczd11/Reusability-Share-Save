package org.seng302.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.seng302.exceptions.IllegalSoldListingNotificationArgumentException;
import org.seng302.view.outgoing.SoldListingNotificationPayload;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Class for sold listing notifications.
 * Sold listing notifications are associated with a business and a sold listing.
 */
@NoArgsConstructor
@Data
@Entity
public class SoldListingNotification {

    @Id // this field (attribute) is the table primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // autoincrement the ID
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "business_id", nullable = false)
    private Integer businessId;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "sold_listing_id", referencedColumnName = "id")
    private SoldListing soldListing;

    @Column(name = "sold_listing_id", nullable = false, insertable = false, updatable = false)
    private Integer soldListingId;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "created", nullable = false)
    private LocalDateTime created;

    // Values need for validation.
    private static final Integer DESCRIPTION_MIN_LENGTH = 10;
    private static final Integer DESCRIPTION_MAX_LENGTH = 600;

    /**
     * Constructor for SoldListingNotification.
     * created is generated upon construction.
     *
     * @param businessId the id of the business the notification is for
     * @param soldListing the sold listing this notification refers to
     * @param description the message of the notification
     */
    public SoldListingNotification(Integer businessId, SoldListing soldListing, String description) throws IllegalSoldListingNotificationArgumentException {
        if (!Objects.equals(businessId, soldListing.getBusiness().getId())) {
            throw new IllegalSoldListingNotificationArgumentException("Invalid business ID");
        }
        if (!(isValidDescription(description))) {
            throw new IllegalSoldListingNotificationArgumentException("Invalid description");
        }

        this.businessId = businessId;
        this.soldListing = soldListing;
        this.soldListingId = soldListing.getId();
        this.description = description;
        this.created = LocalDateTime.now();
    }

    /**
     * Override the toString method for debugging purposes.
     * @return a string representing the SoldListingNotification.
     */
    @Override
    public String toString() {
        return "{\"id\":" + id + "," +
                "\"businessId\":" + businessId + "," +
                "\"soldListing\":" + soldListing + "," +
                "\"description\":\"" + description + "\"," +
                "\"created\":\"" + created + "\"}";
    }

    /**
     * Create a new payload that will be sent to the frontend in a GET request
     * @return a payload representing the sold listing notification
     */
    public SoldListingNotificationPayload toSoldListingNotificationPayload() throws Exception {
        return new SoldListingNotificationPayload(id, soldListing.toSoldListingPayload(), description, created);
    }

    /*---------------------------------------------------Validation---------------------------------------------------*/

    /**
     * Checks to see whether a sold listing notification's description is valid based on its constraints
     * This method can be updated in the future if there is additional constraints.
     * @param description The description to be checked.
     * @return true when the description is valid
     */
    private boolean isValidDescription(String description) {
        return (description.length() >= DESCRIPTION_MIN_LENGTH) &&
                (description.length() <= DESCRIPTION_MAX_LENGTH);
    }

}
