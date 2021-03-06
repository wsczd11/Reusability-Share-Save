import {test, expect, describe, jest, beforeEach, afterEach} from "@jest/globals"
import listing from "../src/views/SaleListing"
import defaultImage from "./../public/default-image.jpg"
import {createLocalVue, shallowMount} from "@vue/test-utils";
import VueLogger from "vuejs-logger";
import Api from "./../src/Api";
import Cookies from "js-cookie"

const localVue = createLocalVue();
const resourcePath = "some.test.url.com/";
let wrapper;
localVue.use(VueLogger, {isEnabled: false})

jest.mock("../src/Api");
jest.mock("js-cookie");

let $router = {
    push: jest.fn(),
    back: jest.fn()
};

let response = {
    status: 200,
    data: {
        id: 6,
        inventoryItem: {
            id: 5,
            product: {
                id: "ARNOTTS-CSCOTCH",
                name: "Arnotts Chocolate Biscuits Scotch Fingers",
                description: "Traditional scotch finger biscuits half coated in milk chocolate.",
                manufacturer: "Arnotts",
                recommendedRetailPrice: 2.99,
                created: "2021-05-12T00:00",
                images: [
                    {isPrimary: false},
                    {isPrimary: false},
                    {isPrimary: true}
                ],
                business: {
                    id: 1,
                    administrators: [
                        {
                            id: 1,
                            firstName: "Alex",
                            lastName: "Doe",
                            middleName: "Joe",
                            nickname: "Johnny",
                            bio: "Biography",
                            email: "emailUSER@email.com",
                            created: "2021-02-12T00:00",
                            role: "USER",
                            businessesAdministered: [
                                null
                            ],
                            dateOfBirth: "2008-02-02",
                            phoneNumber: "0271317",
                            homeAddress: {
                                streetNumber: "129",
                                streetName: "Mastic Trail",
                                city: "Frank Sound Cayman Islands",
                                region: "Caribbean",
                                country: "North America",
                                postcode: "3442",
                                suburb: null
                            }
                        }
                    ],
                    primaryAdministratorId: 1,
                    name: "Brink Food",
                    description: "Description",
                    address: {
                        streetNumber: "86",
                        streetName: "High Street",
                        city: "Picton",
                        region: "Marlborough",
                        country: "New Zealand",
                        postcode: "7220",
                        suburb: null
                    },
                    businessType: "RETAIL_TRADE",
                    created: "2021-02-12T00:00",
                    currencySymbol: "$",
                    currencyCode: "NZD",
                },
                barcode: null
            },
            quantity: 13,
            pricePerItem: 2.99,
            totalPrice: 38.87,
            manufactured: "2021-01-12",
            sellBy: "2021-09-10",
            bestBefore: "2021-09-12",
            expires: "2021-11-12"
        },
        quantity: 13,
        price: 38.87,
        moreInfo: "Limited Stock.",
        created: "2021-05-12T00:00",
        closes: "2021-09-10T00:00",
        isBookmarked: false,
        totalBookmarks: 10
    }

}

const roleResponse = {

    status: 200,
    data: {
       role: "USER"
    }

}

beforeEach(() => {
    Api.getServerResourcePath.mockImplementation((stringPart) => resourcePath + stringPart);
    Api.getDetailForAListing.mockImplementation(() => Promise.resolve(response));
    Api.changeStatusOfAListing.mockResolvedValue(() => {});
})

afterEach(() => {
    wrapper.destroy();
});

describe("Tests for getMainImage function.", () => {

    beforeAll(() => {
        Api.getUser.mockImplementation(() => Promise.resolve(roleResponse));
    })

    test("Testing that getMainImage returns default image if the index is below zero", async () => {
        wrapper = shallowMount(listing, {
            localVue,
            data() {
                return {
                    mainImageIndex: -1,
                    saleImages: [
                        {filename: "testing.png"}
                    ],
                    currencyCode: "NZD",
                    currencySymbol: "$"
                }
            },
            mocks: {
                $router
            },
        });

        expect(wrapper.vm.getMainImage()).toBe(defaultImage);
    });

    test("Testing that getMainImage returns the zero index image, when index is zero", async () => {
        wrapper = shallowMount(listing, {
            localVue,
            data() {
                return {
                    mainImageIndex: 0,
                    saleImages: [
                        {filename: "testing0.png"},
                        {filename: "testing1.png"}
                    ],
                    currencyCode: "NZD",
                    currencySymbol: "$"
                }
            },
            mocks: {
                $router
            },
        });

        expect(wrapper.vm.getMainImage()).toBe(resourcePath + "testing0.png");
    });

    test("Testing that getMainImage returns the 1st index image, when given index 1.", async () => {
        wrapper = shallowMount(listing, {
            localVue,
            data() {
                return {
                    mainImageIndex: 1,
                    saleImages: [
                        {filename: "testing0.png"},
                        {filename: "testing1.png"}
                    ],
                    currencyCode: "NZD",
                    currencySymbol: "$"
                }
            },
            mocks: {
                $router
            },
        });

        expect(wrapper.vm.getMainImage()).toBe(resourcePath + "testing1.png");
    });

    test("Testing that getMainImage returns the default image, when the index is equal to the length of saleImages", async () => {
        wrapper = shallowMount(listing, {
            localVue,
            data() {
                return {
                    mainImageIndex: 4,
                    saleImages: [
                        {filename: "testing0.png"},
                        {filename: "testing1.png"},
                        {filename: "testing2.png"},
                        {filename: "testing3.png"}
                    ],
                    currencyCode: "NZD",
                    currencySymbol: "$"
                }
            },
            mocks: {
                $router
            },
        });

        expect(wrapper.vm.getMainImage()).toBe(defaultImage);
    });

    test("Checking that it returns the default image, when the filename is not specified.", async () => {
        wrapper = shallowMount(listing, {
            localVue,
            data() {
                return {
                    mainImageIndex: 0,
                    saleImages: [
                        {},
                        {}
                    ],
                    currencyCode: "NZD",
                    currencySymbol: "$"
                }
            },
            mocks: {
                $router
            },
        });

        expect(wrapper.vm.getMainImage()).toBe(defaultImage);
    });

})

describe("Tests for getCarouselImage function", () => {

    beforeAll(() => {
        Api.getUser.mockImplementation(() => Promise.resolve(roleResponse));
    })

    test("Testing that getCarouselImage returns default image if the index is below zero", async () => {
        wrapper = shallowMount(listing, {
            localVue,
            data() {
                return {
                    saleImages: [
                        {thumbnailFilename: "testing.png"}
                    ],
                    currencyCode: "NZD",
                    currencySymbol: "$"
                }
            },
            mocks: {
                $router
            },
        });

        expect(wrapper.vm.getCarouselImage(-2)).toBe(defaultImage);
    });

    test("Testing that getCarouselImage returns the zero index image, when index is zero", async () => {
        wrapper = shallowMount(listing, {
            localVue,
            data() {
                return {
                    saleImages: [
                        {thumbnailFilename: "testing0.png"},
                        {thumbnailFilename: "testing1.png"}
                    ],
                    currencyCode: "NZD",
                    currencySymbol: "$"
                }
            },
            mocks: {
                $router
            },
        });

        expect(wrapper.vm.getCarouselImage(0)).toBe(resourcePath + "testing0.png");
    });

    test("Testing that getCarouselImage returns the 1st index image, when given index 1.", async () => {
        wrapper = shallowMount(listing, {
            localVue,
            data() {
                return {
                    saleImages: [
                        {thumbnailFilename: "testing0.png"},
                        {thumbnailFilename: "testing1.png"}
                    ],
                    currencyCode: "NZD",
                    currencySymbol: "$"
                }
            },
            mocks: {
                $router
            },
        });

        expect(wrapper.vm.getCarouselImage(1)).toBe(resourcePath + "testing1.png");
    });

    test("Testing that getCarouselImage returns the default image, when the index is equal to the length of saleImages", async () => {
        wrapper = shallowMount(listing, {
            localVue,
            data() {
                return {
                    saleImages: [
                        {thumbnailFilename: "testing0.png"},
                        {thumbnailFilename: "testing1.png"},
                        {thumbnailFilename: "testing2.png"},
                        {thumbnailFilename: "testing3.png"}
                    ],
                    currencyCode: "NZD",
                    currencySymbol: "$"
                }
            },
            mocks: {
                $router
            },
        });

        expect(wrapper.vm.getCarouselImage(4)).toBe(defaultImage);
    });

    test("Checking that it returns the default image, when the thumbnailFilename is not specified.", async () => {
        wrapper = shallowMount(listing, {
            localVue,
            data() {
                return {
                    saleImages: [
                        {},
                        {}
                    ],
                    currencyCode: "NZD",
                    currencySymbol: "$"
                }
            },
            mocks: {
                $router
            },
        });

        expect(wrapper.vm.getCarouselImage(0)).toBe(defaultImage);
    });

})

describe("Tests for getVisibleImages function", () => {

    beforeAll(() => {
        Api.getUser.mockImplementation(() => Promise.resolve(roleResponse));
    })

    test("Testing that for index -3, and 3 images we get the correct values", async () => {
        wrapper = shallowMount(listing, {
            localVue,
            data() {
                return {
                    carouselStartIndex: -3,
                    carouselNumImages: 3,
                    saleImages: [
                        {},
                        {},
                        {}
                    ],
                    currencyCode: "NZD",
                    currencySymbol: "$"
                }
            },
            mocks: {
                $router
            },
        });

        expect(wrapper.vm.getVisibleImages()).toStrictEqual([0, 1, 2])
    })

    test("Testing that for index 0, and 3 images we get the correct values", async () => {
        wrapper = shallowMount(listing, {
            localVue,
            data() {
                return {
                    carouselStartIndex: 0,
                    carouselNumImages: 3,
                    saleImages: [
                        {},
                        {},
                        {}
                    ],
                    currencyCode: "NZD",
                    currencySymbol: "$"
                }
            },
            mocks: {
                $router
            },
        });

        expect(wrapper.vm.getVisibleImages()).toStrictEqual([0, 1, 2])
    })

    test("Testing that for index 1, and 3 images we get the correct values", async () => {
        wrapper = shallowMount(listing, {
            localVue,
            data() {
                return {
                    carouselStartIndex: 1,
                    carouselNumImages: 3,
                    saleImages: [
                        {},
                        {},
                        {}
                    ],
                    currencyCode: "NZD",
                    currencySymbol: "$"
                }
            },
            mocks: {
                $router
            },
        });

        expect(wrapper.vm.getVisibleImages()).toStrictEqual([1, 2, 0])
    })

    test("Testing that for index larger then number of images we get the correct values", async () => {
        wrapper = shallowMount(listing, {
            localVue,
            data() {
                return {
                    carouselStartIndex: 7,
                    carouselNumImages: 3,
                    saleImages: [
                        {},
                        {},
                        {}
                    ],
                    currencyCode: "NZD",
                    currencySymbol: "$"
                }
            },
            mocks: {
                $router
            },
        });

        expect(wrapper.vm.getVisibleImages()).toStrictEqual([1, 2, 0])
    })

    test("Testing that for an equal number of images to the index we get the correct values", async () => {
        wrapper = shallowMount(listing, {
            localVue,
            data() {
                return {
                    carouselStartIndex: 5,
                    carouselNumImages: 5,
                    saleImages: [
                        {},
                        {},
                        {}
                    ],
                    currencyCode: "NZD",
                    currencySymbol: "$"
                }
            },
            mocks: {
                $router
            },
        });

        expect(wrapper.vm.getVisibleImages()).toStrictEqual([2, 0, 1, 2, 0])
    })

    test("Testing for a zero number of carousel images we get an empty array", async () => {
        wrapper = shallowMount(listing, {
            localVue,
            data() {
                return {
                    carouselStartIndex: 3,
                    carouselNumImages: 0,
                    saleImages: [
                        {},
                        {},
                        {},
                        {},
                        {}
                    ],
                    currencyCode: "NZD",
                    currencySymbol: "$"
                }
            },
            mocks: {
                $router
            },
        });

        expect(wrapper.vm.getVisibleImages()).toStrictEqual([])
    })
})

describe("Tests for boundIndex function", () => {

    beforeAll(() => {
        Api.getUser.mockImplementation(() => Promise.resolve(roleResponse));
    })

    test("Testing boundIndex(), with negative random index", async () => {
        wrapper = shallowMount(listing, {localVue});
        expect(wrapper.vm.boundIndex(-5, 5)).toStrictEqual(0);
    });

    test("Testing boundIndex(), with -1 index", async () => {
        wrapper = shallowMount(listing, {localVue});
        expect(wrapper.vm.boundIndex(-1, 4)).toStrictEqual(3);
    });

    test("Testing boundIndex(), with 0 index", async () => {
        wrapper = shallowMount(listing, {localVue});
        expect(wrapper.vm.boundIndex(0, 5)).toStrictEqual(0);
    });

    test("Testing boundIndex(), with 1 index", async () => {
        wrapper = shallowMount(listing, {localVue});
        expect(wrapper.vm.boundIndex(1, 7)).toStrictEqual(1);
    });

    test("Testing boundIndex(), with n-1 index", async () => {
        wrapper = shallowMount(listing, {localVue});
        expect(wrapper.vm.boundIndex(17, 18)).toStrictEqual(17);
    });

    test("Testing boundIndex(), with n index", async () => {
        wrapper = shallowMount(listing, {localVue});
        expect(wrapper.vm.boundIndex(30, 30)).toStrictEqual(0);
    });

    test("Testing boundIndex(), with n+1 index", async () => {
        wrapper = shallowMount(listing, {localVue});
        expect(wrapper.vm.boundIndex(6, 5)).toStrictEqual(1);
    });

    test("Testing boundIndex(), with n+13 index", async () => {
        wrapper = shallowMount(listing, {localVue});
        expect(wrapper.vm.boundIndex(18, 5)).toStrictEqual(3);
    });
})

describe("Tests for nextImage function", () => {

    beforeAll(() => {
        Api.getUser.mockImplementation(() => Promise.resolve(roleResponse));
    })

    test("Testing that we get the next image at 0 index in 5 n array", async () => {
        wrapper = shallowMount(listing, {
            localVue,
            data() {
                return {
                    carouselStartIndex: 0,
                    saleImages: [
                        {},
                        {},
                        {},
                        {},
                        {},
                    ]
                }
            }
        })
        expect(wrapper.vm.$data.carouselStartIndex).toStrictEqual(0);
        wrapper.vm.nextImage();
        expect(wrapper.vm.$data.carouselStartIndex).toStrictEqual(1);
    })

    test("Testing that we get the next image at 4 index in 5 n array", async () => {
        wrapper = shallowMount(listing, {
            localVue,
            data() {
                return {
                    carouselStartIndex: 4,
                    saleImages: [
                        {},
                        {},
                        {},
                        {},
                        {},
                    ]
                }
            },
            mocks: {
                $router
            },
        })
        expect(wrapper.vm.$data.carouselStartIndex).toStrictEqual(4);
        wrapper.vm.nextImage();
        expect(wrapper.vm.$data.carouselStartIndex).toStrictEqual(0);
    })

    test("Testing that we get the next image at 5 index in 5 n array", async () => {
        wrapper = shallowMount(listing, {
            localVue,
            data() {
                return {
                    carouselStartIndex: 5,
                    saleImages: [
                        {},
                        {},
                        {},
                        {},
                        {},
                    ]
                }
            },
            mocks: {
                $router
            },
        })
        expect(wrapper.vm.$data.carouselStartIndex).toStrictEqual(5);
        wrapper.vm.nextImage();
        expect(wrapper.vm.$data.carouselStartIndex).toStrictEqual(1);
    })

    test("Testing that we get the next image at 6 index in 5 n array", async () => {
        wrapper = shallowMount(listing, {
            localVue,
            data() {
                return {
                    carouselStartIndex: 6,
                    saleImages: [
                        {},
                        {},
                        {},
                        {},
                        {},
                    ]
                }
            },
            mocks: {
                $router
            },
        })
        expect(wrapper.vm.$data.carouselStartIndex).toStrictEqual(6);
        wrapper.vm.nextImage();
        expect(wrapper.vm.$data.carouselStartIndex).toStrictEqual(2);
    })
})

describe("Tests for previousImage function", () => {

    beforeAll(() => {
        Api.getUser.mockImplementation(() => Promise.resolve(roleResponse));
    })

    test("Testing that we get the previous image at 0 index in 5 n array", async () => {
        wrapper = shallowMount(listing, {
            localVue,
            data() {
                return {
                    carouselStartIndex: 0,
                    saleImages: [
                        {},
                        {},
                        {},
                        {},
                        {},
                    ]
                }
            },
            mocks: {
                $router
            },
        })
        expect(wrapper.vm.$data.carouselStartIndex).toStrictEqual(0);

        wrapper.vm.previousImage();
        expect(wrapper.vm.$data.carouselStartIndex).toStrictEqual(4);
    })

    test("Testing that we get the previous image at 4 index in 5 n array", async () => {
        wrapper = shallowMount(listing, {
            localVue,
            data() {
                return {
                    carouselStartIndex: 4,
                    saleImages: [
                        {},
                        {},
                        {},
                        {},
                        {},
                    ]
                }
            },
            mocks: {
                $router
            },
        })
        expect(wrapper.vm.$data.carouselStartIndex).toStrictEqual(4);
        wrapper.vm.previousImage();
        expect(wrapper.vm.$data.carouselStartIndex).toStrictEqual(3);
    })

    test("Testing that we get the previous image at 5 index in 5 n array", async () => {
        wrapper = shallowMount(listing, {
            localVue,
            data() {
                return {
                    carouselStartIndex: 5,
                    saleImages: [
                        {},
                        {},
                        {},
                        {},
                        {},
                    ]
                }
            },
            mocks: {
                $router
            },
        })
        expect(wrapper.vm.$data.carouselStartIndex).toStrictEqual(5);
        wrapper.vm.previousImage();
        expect(wrapper.vm.$data.carouselStartIndex).toStrictEqual(4);
    })

    test("Testing that we get the previous image at 6 index in 5 n array", async () => {
        wrapper = shallowMount(listing, {
            localVue,
            data() {
                return {
                    carouselStartIndex: 6,
                    saleImages: [
                        {},
                        {},
                        {},
                        {},
                        {},
                    ]
                }
            },
            mocks: {
                $router
            },
        })
        expect(wrapper.vm.$data.carouselStartIndex).toStrictEqual(6);
        wrapper.vm.previousImage();
        expect(wrapper.vm.$data.carouselStartIndex).toStrictEqual(0);
    })
})


describe("Test data population", () =>{
    beforeEach(() => {
        // given
        Api.getUser.mockImplementation(() => Promise.resolve(roleResponse));
        wrapper = shallowMount(listing, {
            localVue,
        })

        // when
        wrapper.vm.populateData(response.data)
    })

    test("Test product info been populated", async () => {
        // then
        expect(wrapper.vm.$data.productName).toStrictEqual(response.data.inventoryItem.product.name);
        expect(wrapper.vm.$data.productId).toStrictEqual(response.data.inventoryItem.product.id);
        expect(wrapper.vm.$data.price).toStrictEqual(response.data.price);
        expect(wrapper.vm.$data.barcode).toStrictEqual(response.data.inventoryItem.product.barcode);
        expect(wrapper.vm.$data.description).toStrictEqual(response.data.inventoryItem.product.description);
        expect(wrapper.vm.$data.quantity).toStrictEqual(response.data.quantity);
    })

    test("Test image info been populated", async () => {
        // then
        expect(wrapper.vm.$data.saleImages).toStrictEqual(response.data.inventoryItem.product.images);
        expect(wrapper.vm.$data.carouselNumImages).toStrictEqual(response.data.inventoryItem.product.images.length);
        expect(wrapper.vm.$data.mainImageIndex).toStrictEqual(2);
        expect(wrapper.vm.$data.carouselStartIndex).toStrictEqual(2);
    })

    test("Test date info been populated", async () => {
        // then
        expect(wrapper.vm.$data.startDate).toStrictEqual("12th May 2021");
        expect(wrapper.vm.$data.closeData).toStrictEqual("10th Sep 2021");
        expect(wrapper.vm.$data.manufactured).toStrictEqual(response.data.inventoryItem.manufactured);
        expect(wrapper.vm.$data.sellBy).toStrictEqual(response.data.inventoryItem.sellBy);
        expect(wrapper.vm.$data.bestBefore).toStrictEqual(response.data.inventoryItem.bestBefore);
        expect(wrapper.vm.$data.expires).toStrictEqual(response.data.inventoryItem.expires);
        expect(wrapper.vm.$data.manufacturer).toStrictEqual(response.data.inventoryItem.product.manufacturer);
    })

    test("Test business info been populated", async () => {
        // then
        expect(wrapper.vm.$data.businessName).toStrictEqual(response.data.inventoryItem.product.business.name);
        expect(wrapper.vm.$data.businessAddress).toStrictEqual(response.data.inventoryItem.product.business.address);
    })

    test("Test address info been populated", async () => {
        // then
        expect(wrapper.vm.$data.businessAddressLine1).toStrictEqual("86 High Street");
        expect(wrapper.vm.$data.businessAddressLine2).toBeNull();
        expect(wrapper.vm.$data.businessAddressLine3).toStrictEqual("Picton, 7220");
        expect(wrapper.vm.$data.businessAddressLine4).toStrictEqual("Marlborough, New Zealand");
    })

    test("Test bookmark info been populated", async () => {
        // then
        expect(wrapper.vm.$data.listingId).toStrictEqual(response.data.id);
        expect(wrapper.vm.$data.isBookmarked).toStrictEqual(response.data.isBookmarked);
        expect(wrapper.vm.$data.totalBookmarks).toStrictEqual(10);
    })
})

describe("Test bookmark display counter and icon", () => {
    beforeEach(() => {
        // given
        Api.getUser.mockImplementation(() => Promise.resolve(roleResponse));
        wrapper = shallowMount(listing, {
            localVue
        });
    })

    test("Test user can add a bookmark, if user not mark current listing.", async function () {
        expect(wrapper.find("#bookmarks").text()).toBe("10")

        // when
        await wrapper.find("#bookmarks").trigger('click');
        await wrapper.vm.$nextTick()

        // then
        expect(wrapper.find("#bookmarks").text()).toBe("11")
    });

    test("Test user can remove a bookmark, if user marked current listing.", async function () {
        await wrapper.find("#bookmarks").trigger('click');
        await wrapper.vm.$nextTick()
        expect(wrapper.find("#bookmarks").text()).toBe("11")

        // when
        await wrapper.find("#bookmarks").trigger('click');
        await wrapper.vm.$nextTick()

        // then
        expect(wrapper.find("#bookmarks").text()).toBe("10")
    });

    test("Test when user add a bookmark, the icon change .", async function () {
        expect(wrapper.find("#un-marked").exists()).toBeTruthy()

        // when
        await wrapper.find("#bookmarks").trigger('click');
        await wrapper.vm.$nextTick()

        // then
        expect(wrapper.find("#marked").exists()).toBeTruthy()
    });

    test("Test when user remove a bookmark, the icon change.", async function () {
        await wrapper.find("#bookmarks").trigger('click');
        await wrapper.vm.$nextTick()
        expect(wrapper.find("#marked").exists()).toBeTruthy()

        // when
        await wrapper.find("#bookmarks").trigger('click');
        await wrapper.vm.$nextTick()

        // then
        expect(wrapper.find("#un-marked").exists()).toBeTruthy()
    });
})

describe("Testing the 'Go to Business Profile' button", () => {

    let $router;
    let $route;
    let goToButton;

    beforeEach(() => {
        $router = {
            push: jest.fn()
        };
        $route = {
            params: {
                businessId: 2,
                listingId: 11
            }
        };
        Api.getUser.mockImplementation(() => Promise.resolve(roleResponse));
        wrapper = shallowMount(listing, {
            mocks: {
                $router,
                $route
            }
        });
        goToButton = wrapper.find("#go-to-button");
    });

    test("Test that user is redirected to the business profile page when the 'Go to Business Profile' button is clicked.", async () => {
        const businessId = 2;

        await goToButton.trigger("click");

        expect($router.push).toHaveBeenCalledWith({ path: `/businessProfile/${businessId}`});
    });
});

describe("Testing buy listing functionality", () => {

    let $router;
    let $route;

    beforeEach(() => {
        $router = {
            push: jest.fn()
        };
        $route = {
            params: {
                businessId: 2,
                listingId: 11
            }
        };
        Api.getUser.mockImplementation(() => Promise.resolve(roleResponse));
        wrapper = shallowMount(listing, {
            mocks: {
                $router,
                $route
            }
        });

    });

    test("Test v-if button variable is set correctly when acting as a user", (() => {
        wrapper.vm.$data.actingBusinessId = undefined;
        wrapper.vm.populateData(response.data);

        wrapper.vm.$nextTick();

        expect(wrapper.vm.$data.canBuy).toBeTruthy();
    }));

    test("Test v-if button variable is set correctly when acting as business", (() => {
        wrapper.vm.$data.actingBusinessId = 25;
        wrapper.vm.populateData(response.data);

        wrapper.vm.$nextTick();

        expect(wrapper.vm.$data.canBuy).toBeFalsy();
    }));
});

describe("Testing the getBarcodeImage method", () => {

    let $router;
    let $route;

    beforeEach(() => {
        $router = {
            push: jest.fn()
        };
        $route = {
            params: {
                businessId: 2,
                listingId: 11
            }
        };
        Api.getUser.mockImplementation(() => Promise.resolve(roleResponse));
        wrapper = shallowMount(listing, {
            mocks: {
                $router,
                $route
            }
        });
    });

    test("Test correct url is returned when this.barcode is UPC-A", () => {
        const upca = "036000291452";
        const expected_url = "https://bwipjs-api.metafloor.com/?bcid=upca&text=" + upca;
        wrapper.vm.$data.barcode = upca;
        expect(wrapper.vm.getBarcodeImage()).toEqual(expected_url);
    });

    test("Test correct url is returned when this.barcode is EAN13", () => {
        const ean13 = "9400547002634";
        const expected_url = "https://bwipjs-api.metafloor.com/?bcid=ean13&text=" + ean13;
        wrapper.vm.$data.barcode = ean13;
        expect(wrapper.vm.getBarcodeImage()).toEqual(expected_url);
    });

    test("Test correct empty url is returned when this.barcode is not UPC-A or EAN13", () => {
        const invalid_barcode = "123456";
        const expected_url = "";
        wrapper.vm.$data.barcode = invalid_barcode;
        expect(wrapper.vm.getBarcodeImage()).toEqual(expected_url);
    });
});

describe("Test delete button", () => {

    beforeAll(() => {
        Api.getUser.mockImplementation(() => Promise.resolve(roleResponse));
    })

    test("Test delete button appears when user is acting as a business", async () => {
        Cookies.get.mockReturnValue(1);
        wrapper = shallowMount(listing, {
            localVue,
        });
        await wrapper.vm.$nextTick();

        expect(wrapper.find(".delete-button").exists()).toBe(true);
    })

    test("Test delete button doesn't appear when user is acting as a user", async () => {
        Cookies.get.mockReturnValueOnce(1);
        Cookies.get.mockReturnValueOnce(undefined);
        wrapper = shallowMount(listing, {
            localVue,
        });
        await wrapper.vm.$nextTick();

        expect(wrapper.find(".delete-button").exists()).toBe(false);
    })

    test("Test delete button doesn't appear when user is acting as a different business", async () => {
        Cookies.get.mockReturnValueOnce("1");
        Cookies.get.mockReturnValueOnce("2");
        wrapper = shallowMount(listing, {
            localVue,
        });
        await wrapper.vm.$nextTick();

        expect(wrapper.find(".delete-button").exists()).toBe(false);
    })

    test("Test delete button doesn't appear when ActAs cookie is not a valid number", async () => {
        Cookies.get.mockReturnValueOnce("1");
        Cookies.get.mockReturnValueOnce("Not a Number");
        wrapper = shallowMount(listing, {
            localVue,
        });
        await wrapper.vm.$nextTick();

        expect(wrapper.find(".delete-button").exists()).toBe(false);
    })

    describe("Test delete button functionality", () => {
        let apiResponse;

        beforeEach(async () => {
            Cookies.get.mockReturnValue(1)
            wrapper = shallowMount(listing, {
                localVue,
                mocks: {
                    $router
                }
            });
            await wrapper.vm.$nextTick();
        })

        test("Test a successful delete", async () => {
            apiResponse = {
                status: 200,
            }
            Api.deleteListing.mockImplementation(() => Promise.resolve(apiResponse));

            wrapper.vm.deleteListing(1);
            await wrapper.vm.$nextTick();

            expect($router.back).toBeCalled();
        })

        test("Test a 401 response when deleting", async () => {
            apiResponse = {
                response: {
                    status: 401
                }
            }
            Api.deleteListing.mockImplementation(() => Promise.reject(apiResponse));

            wrapper.vm.deleteListing(1);
            await wrapper.vm.$nextTick();

            expect($router.push).toBeCalled();
            expect($router.push).toBeCalledWith({name: "InvalidToken"});
        })

        test("Test a 406 response when deleting", async () => {
            apiResponse = {
                response: {
                    status: 406
                }
            }
            Api.deleteListing.mockImplementation(() => Promise.reject(apiResponse));

            wrapper.vm.deleteListing(1);
            await wrapper.vm.$nextTick();

            expect($router.push).toBeCalled();
            expect($router.push).toBeCalledWith({name: "NoListing"});
        })

        test("Test a 403 response when deleting makes delete button disappear and removes actingAs", async () => {
            apiResponse = {
                response: {
                    status: 403
                }
            }
            Api.deleteListing.mockImplementation(() => Promise.reject(apiResponse));

            expect(wrapper.find(".delete-button").exists()).toBeTruthy();

            wrapper.vm.deleteListing(1);
            await wrapper.vm.$nextTick();
            await wrapper.vm.$nextTick();

            expect(wrapper.vm.$data.actingBusinessId).toBe(null);
            expect(wrapper.find(".delete-button").exists()).toBeFalsy();
        })
    })
})

describe("Testing the getRole method", () => {

    test("Test successful response for a GAA user", async () => {
        let APIResponseUser = {
            status: 200,
            data: {
                role: "GLOBALAPPLICATIONADMIN"
            }
        }
        Api.getUser.mockImplementation(() => Promise.resolve(APIResponseUser))

        wrapper = shallowMount(listing, {
            localVue,
            mocks: {
                $router
            }
        });
        await wrapper.vm.$nextTick()

        expect(wrapper.vm.$data.isAdmin).toBeTruthy();
    })

    test("Test successful response for a DGAA user", async () => {
        let APIResponseUser = {
            status: 200,
            data: {
                role: "DEFAULTGLOBALAPPLICATIONADMIN"
            }
        }
        Api.getUser.mockImplementation(() => Promise.resolve(APIResponseUser))

        wrapper = shallowMount(listing, {
            localVue,
            mocks: {
                $router
            }
        });
        await wrapper.vm.$nextTick()

        expect(wrapper.vm.$data.isAdmin).toBeTruthy();
    })

    test("Test successful response for a normal user", async () => {
        let APIResponseUser = {
            status: 200,
            data: {
                role: "USER"
            }
        }
        Api.getUser.mockImplementation(() => Promise.resolve(APIResponseUser))

        wrapper = shallowMount(listing, {
            localVue,
            mocks: {
                $router
            }
        });
        await wrapper.vm.$nextTick()

        expect(wrapper.vm.$data.isAdmin).toBeFalsy();
    });

    test("Test 401 response for a user", async () => {
        let APIResponseUser = {
            response: {
                status: 401
            }
        }
        Api.getUser.mockImplementation(() => Promise.reject(APIResponseUser))

        wrapper = shallowMount(listing, {
            localVue,
            mocks: {
                $router
            }
        });
        await wrapper.vm.$nextTick()

        expect($router.push).toBeCalled()
        expect($router.push).toBeCalledWith({name: 'InvalidToken'})
    });
});
