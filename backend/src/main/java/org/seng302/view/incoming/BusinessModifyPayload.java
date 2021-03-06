package org.seng302.view.incoming;

import org.seng302.model.enums.BusinessType;
import org.seng302.view.outgoing.AddressPayload;

/**
 * Describes the payload returned by the modification request for a business.
 */
public class BusinessModifyPayload {

    private Integer primaryAdministratorId;
    private String name;
    private String description;
    private AddressPayload address;
    private String businessType;
    private String currencySymbol;
    private String currencyCode;

    public Integer getPrimaryAdministratorId() {
        return primaryAdministratorId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public AddressPayload getAddress() {
        return address;
    }

    public BusinessType getBusinessType() {
        return businessTypeTranslate(businessType);
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    @Override
    public String toString() {
        return  "{" +
                "\"primaryAdministratorId\":" + primaryAdministratorId + "," +
                "\"name\":\"" + name + "\"," +
                "\"description\":\"" + description + "\"," +
                "\"address\":" + address + "," +
                "\"businessType\":\"" + businessType + "\"," +
                "\"currencySymbol\":\"" + currencySymbol + "\"," +
                "\"currencyCode\":\"" + currencyCode + "\"" +
                "}";
    }

    /**
     * check if a string is a business type, if so make the string to businessType object, if not, return null
     * @param string a string about business type
     * @return when string is business type return BusinessType object, if not return null
     */
    private BusinessType businessTypeTranslate(String string){
        BusinessType translatedType = null;
        if (string == null) {
            return null;
        }else {
            string = string.toUpperCase();
        }

        switch (string) {
            case "ACCOMMODATION AND FOOD SERVICES":
                translatedType = BusinessType.ACCOMMODATION_AND_FOOD_SERVICES;
                break;
            case "RETAIL TRADE":
                translatedType = BusinessType.RETAIL_TRADE;
                break;
            case "CHARITABLE ORGANISATION":
                translatedType = BusinessType.CHARITABLE_ORGANISATION;
                break;
            case "NON PROFIT ORGANISATION":
                translatedType = BusinessType.NON_PROFIT_ORGANISATION;
                break;
        }
        return translatedType;
    }
}
