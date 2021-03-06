export const BusinessTypes = [
    'Accommodation and Food Services' ,
    'Retail Trade',
    'Charitable Organisation',
    'Non Profit Organisation'
]

/**
 * This method converts a business type that is stored in the backend into the format that is used
 * in the frontend. It is assumed that the business type received from the backend will be of a valid type and will
 * therefore, always return a valid business type in the frontend.
 *
 * @param type a business type as stored in the backend.
 * @return {string} the frontend format of business type.
 */
export function convertToFrontendBusinessType(type) {
    if (type === "ACCOMMODATION_AND_FOOD_SERVICES") { return BusinessTypes[0]; }
    if (type === "RETAIL_TRADE") { return BusinessTypes[1]; }
    if (type === "CHARITABLE_ORGANISATION") { return BusinessTypes[2]; }
    return BusinessTypes[3];
}

export default class Business {

    // This is a config for the business requirement details
    static config = {
        businessName: {
            name: "Name",
            minLength: 1,
            maxLength: 100,
            regex: /^[a-zA-Z0-9À-ÖØ-öø-įĴ-őŔ-žǍ-ǰǴ-ǵǸ-țȞ-ȟȤ-ȳɃɆ-ɏḀ-ẞƀ-ƓƗ-ƚƝ-ơƤ-ƥƫ-ưƲ-ƶẠ-ỿ '#,.&()-]+$/,
            regexMessage: "Must only contain alphanumeric characters, numbers, spaces, or '#,.&()[]-]+$",
        },
        description: {
            name: "Description",
            minLength: 0,
            maxLength: 600
        },
        businessType: {
            name: "Business type",
        },
        businessAddress: {
            name: "Business address",
            minLength: 0,
            maxLength: 255,
            regex: /^[a-zA-Z0-9À-ÖØ-öø-įĴ-őŔ-žǍ-ǰǴ-ǵǸ-țȞ-ȟȤ-ȳɃɆ-ɏḀ-ẞƀ-ƓƗ-ƚƝ-ơƤ-ƥƫ-ưƲ-ƶẠ-ỿ '#,.&()-]+$/,
            regexMessage: "Must only contain alphanumeric characters, numbers, spaces, or '#,.&()[]-]+$",
        },
        streetNumber: {
            name: "Street number",
            minLength: 0,
            maxLength: 255
        },
        streetName: {
            name: "Street name",
            minLength: 0,
            maxLength: 255
        },
        suburb: {
            name: "Suburb",
            minLength: 0,
            maxLength: 255
        },
        city: {
            name: "City",
            minLength: 0,
            maxLength: 255,
        },
        postcode: {
            name: "Postcode",
            minLength: 0,
            maxLength: 255
        },
        region: {
            name: "Region",
            minLength: 0,
            maxLength: 255
        },
        country: {
            name: "Country",
            minLength: 1,
            maxLength: 255,
            regexMessage: "Must be alphanumeric (spaces, -, ' optional)",
            regex: /^[a-zA-ZÀ-ÖØ-öø-įĴ-őŔ-žǍ-ǰǴ-ǵǸ-țȞ-ȟȤ-ȳɃɆ-ɏḀ-ẞƀ-ƓƗ-ƚƝ-ơƤ-ƥƫ-ưƲ-ƶẠ-ỿ '-]+$/
        },
        currencySymbol: {
            name: "Currency symbol"
        },
        currencyCode: {
            name: "Currency code"
        },
    };

    constructor({primaryAdministratorId, name, description, address, businessType, currencySymbol, currencyCode}) {
        this.data = {
            primaryAdministratorId,
            name,
            description,
            address,
            businessType,
            currencySymbol,
            currencyCode
        }

    }

}