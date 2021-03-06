package org.seng302.view.outgoing;

import org.seng302.model.enums.NotificationType;

import java.time.LocalDateTime;

/**
 * Payload for the MarketCardNotification.
 */
public class MarketCardNotificationPayload {

    private Integer id;
    private String description;
    private String created;
    private MarketplaceCardPayload marketplaceCardPayload;
    private String notificationType;

    /**
     * @param id                     notification id
     * @param description            description
     * @param created                time notification been created
     * @param marketplaceCardPayload marketplace Card Payload
     */
    public MarketCardNotificationPayload(Integer id,
                                         String description,
                                         LocalDateTime created,
                                         MarketplaceCardPayload marketplaceCardPayload) {
        this.id = id;
        this.description = description;
        this.created = created.toString();
        this.marketplaceCardPayload = marketplaceCardPayload;
        this.notificationType = NotificationType.MARKETPLACE.toString();
    }

    public Integer getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getCreated() {
        return created;
    }

    public MarketplaceCardPayload getMarketplaceCardPayload() {
        return marketplaceCardPayload;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public void setMarketplaceCardPayload(MarketplaceCardPayload marketplaceCardPayload) {
        this.marketplaceCardPayload = marketplaceCardPayload;
    }

    /**
     * Override the toString method for debugging purposes.
     * @return a string representing the MarketCardNotificationPayload.
     */
    @Override
    public String toString() {
        return "{\"id\":" + id +
                ",\"description\":\"" + description + "\"" +
                ",\"created\":\"" + created + "\"" +
                ",\"marketplaceCardPayload\":" + marketplaceCardPayload.toString() +
                ",\"notificationType\":\"" + notificationType +
                "\"}";
    }
}
