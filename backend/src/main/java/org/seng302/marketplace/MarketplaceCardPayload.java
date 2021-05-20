package org.seng302.marketplace;

import org.seng302.user.UserPayloadSecure;

import java.time.LocalDateTime;
import java.util.List;

public class MarketplaceCardPayload {

    private Integer id;
    private UserPayloadSecure creator;
    private String section;
    private String created;
    private String displayPeriodEnd;
    private String title;
    private String description;
    private List<KeywordPayload> keywords;

    public MarketplaceCardPayload(Integer id,
                                  UserPayloadSecure creator,
                                  Section section,
                                  LocalDateTime created,
                                  LocalDateTime displayPeriodEnd,
                                  String title,
                                  String description,
                                  List<KeywordPayload> keywords) {
        this.id = id;
        this.creator = creator;
        this.section = section.toString();
        this.created = created.toString();
        this.displayPeriodEnd = displayPeriodEnd.toString();
        this.title = title;
        this.description = description;
        this.keywords = keywords;
    }

    public Integer getId() {
        return id;
    }

    public UserPayloadSecure getCreator() {
        return creator;
    }

    public String getSection() {
        return section;
    }

    public String getCreated() {
        return created;
    }

    public String getDisplayPeriodEnd() {
        return displayPeriodEnd;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<KeywordPayload> getKeywords() { return keywords; }
}
