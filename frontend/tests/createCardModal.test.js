import {expect, test, describe, beforeEach, jest} from "@jest/globals";
import Api from "../src/Api"
import Cookies from "js-cookie"
import {createLocalVue, shallowMount} from "@vue/test-utils";
import VueLogger from "vuejs-logger"
import VueRouter from 'vue-router'
import {UserRole} from "../src/configs/User";
import EditCreateCardModal from "../src/components/marketplace/EditCreateCards";

jest.mock("../src/Api");
jest.mock("js-cookie");

const localVue = createLocalVue();
localVue.use(VueLogger, {isEnabled : false});
localVue.use(VueRouter);

/** White box testing ... */

describe("Testing the selection behaviour of the select section.", () => {

    let createCardModalWrapper;

    beforeEach(async () => {
        const mockApiResponse = {
            status: 200,
            data: {
                homeAddress: {
                    city: "CITY"
                },
            }
        }

        Api.getUser.mockImplementation( () => Promise.resolve(mockApiResponse) );
        Cookies.get.mockReturnValue(36);

        createCardModalWrapper = await shallowMount(EditCreateCardModal, {localVue});
        await createCardModalWrapper.vm.$nextTick();
        await createCardModalWrapper.setProps({currentModal: "create"});
        await createCardModalWrapper.vm.$nextTick();

    })

    test("Test that when you select ForSale it is stored in data", async () => {

        expect(createCardModalWrapper.find("#section-selection-default").exists()).toBe(true);
        expect(createCardModalWrapper.find("#for-sale-option").exists()).toBe(true);
        const forSaleOption = createCardModalWrapper.find("#for-sale-option");

        forSaleOption.setSelected();
        expect(createCardModalWrapper.vm.$data.sectionSelected).toBe("ForSale");
    })

    test("Test that when you select Exchange it is stored in data", async () => {

        expect(createCardModalWrapper.find("#section-selection-default").exists()).toBe(true);
        expect(createCardModalWrapper.find("#exchange-option").exists()).toBe(true);
        const exchangeOption = createCardModalWrapper.find("#exchange-option");

        exchangeOption.setSelected();
        expect(createCardModalWrapper.vm.$data.sectionSelected).toBe("Exchange");
    })

    test("Test that when you select Wanted it is stored in data", async () => {

        expect(createCardModalWrapper.find("#section-selection-default").exists()).toBe(true);
        expect(createCardModalWrapper.find("#wanted-option").exists()).toBe(true);
        const wantedOption = createCardModalWrapper.find("#wanted-option");

        wantedOption.setSelected();
        expect(createCardModalWrapper.vm.$data.sectionSelected).toBe("Wanted");
    })

    test("Test that when the form is submitted with no selection invalid feedback is provided.", async () => {

        expect(createCardModalWrapper.find("#section-selection-invalid-feedback").exists()).toBe(false);

        Api.addNewCard.mockImplementation( Promise.resolve( () => {} ) )
        await createCardModalWrapper.vm.createNewCard();
        await createCardModalWrapper.vm.$nextTick();

        expect(Api.addNewCard).toBeCalledTimes(0);
        expect(createCardModalWrapper.find("#section-selection-invalid-feedback").exists()).toBe(true);
    })

    test("Test that when the form is submitted with for sale selected, no invalid feedback is returned", async () => {

        expect(createCardModalWrapper.find("#section-selection-invalid-feedback").exists()).toBe(false);

        expect(createCardModalWrapper.find("#for-sale-option").exists()).toBe(true);
        const forSaleOption = createCardModalWrapper.find("#for-sale-option");
        forSaleOption.setSelected();

        Api.addNewCard.mockImplementation( Promise.resolve( () => {} ) )
        await createCardModalWrapper.vm.createNewCard();
        await createCardModalWrapper.vm.$nextTick();

        expect(createCardModalWrapper.find("#section-selection-invalid-feedback").exists()).toBe(false);
    })

    test("Test that when the form is submitted with for sale selected, no invalid feedback is returned", async () => {

        expect(createCardModalWrapper.find("#section-selection-invalid-feedback").exists()).toBe(false);

        expect(createCardModalWrapper.find("#exchange-option").exists()).toBe(true);
        const exchangeOption = createCardModalWrapper.find("#exchange-option");
        exchangeOption.setSelected();

        Api.addNewCard.mockImplementation( Promise.resolve( () => {} ) )
        await createCardModalWrapper.vm.createNewCard();
        await createCardModalWrapper.vm.$nextTick();

        expect(createCardModalWrapper.find("#section-selection-invalid-feedback").exists()).toBe(false);
    })

    test("Test that when the form is submitted with for sale selected, no invalid feedback is returned", async () => {

        expect(createCardModalWrapper.find("#section-selection-invalid-feedback").exists()).toBe(false);

        expect(createCardModalWrapper.find("#wanted-option").exists()).toBe(true);
        const wantedOption = createCardModalWrapper.find("#wanted-option");
        wantedOption.setSelected();

        Api.addNewCard.mockImplementation( Promise.resolve( () => {} ) )
        await createCardModalWrapper.vm.createNewCard();
        await createCardModalWrapper.vm.$nextTick();

        expect(createCardModalWrapper.find("#section-selection-invalid-feedback").exists()).toBe(false);
    })

} )

describe("Testing the behaviour of prefilled input fields", () => {

    let createCardModal;

    beforeEach(async () => {
        // Mocking the get from the js-cookie library
        const mockApiResponse = {
            status: 200,
            data: {
                firstName: "FIRST_NAME",
                lastName: "LAST_NAME",
                role: UserRole.DEFAULTGLOBALAPPLICATIONADMIN
            }
        }
        // Mock the API Calls
        Cookies.get.mockReturnValue(36)
        Api.getUser.mockImplementation( () => Promise.resolve(mockApiResponse) );

        createCardModal = await shallowMount(EditCreateCardModal, {localVue})
        await createCardModal.vm.$nextTick();

    })

    test("Test that the name field is automatically displaying the correct full name", async () => {

        expect(createCardModal.vm.$data.userFullName).toBe("FIRST_NAME LAST_NAME")
        expect(createCardModal.find("#user-full-name").exists()).toBe(true);
        expect(createCardModal.find("#user-full-name").text()).toBe("FIRST_NAME LAST_NAME");

    })

    test("Test that the location field is correctly generated with only a city", async () => {

        const mockApiResponse = {
            status: 200,
            data: {
                homeAddress: {
                    city: "CITY"
                },
            }
        }

        Api.getUser.mockImplementation( () => Promise.resolve(mockApiResponse) );
        createCardModal = await shallowMount(EditCreateCardModal, {localVue})
        await createCardModal.vm.$nextTick();


        expect(createCardModal.vm.$data.userLocation).toBe("CITY");
        expect(createCardModal.find("#user-location").exists()).toBe(true)
        expect(createCardModal.find("#user-location").text()).toBe("CITY");
    })

    test("Test that the location is correctly generated with only a suburb", async () => {
        const mockApiResponse = {
            status: 200,
            data: {
                homeAddress: {
                    suburb: "SUBURB"
                },
            }
        }

        Api.getUser.mockImplementation( () => Promise.resolve(mockApiResponse) );
        createCardModal = await shallowMount(EditCreateCardModal, {localVue})
        await createCardModal.vm.$nextTick();

        expect(createCardModal.vm.$data.userLocation).toBe("SUBURB");
        expect(createCardModal.find("#user-location").exists()).toBe(true)
        expect(createCardModal.find("#user-location").text()).toBe("SUBURB");
    })

    test("Test that the location is correctly generated with no home address data.", async () => {
        const mockApiResponse = {
            status: 200,
            data: {
                homeAddress: {
                },
            }
        }

        Api.getUser.mockImplementation( () => Promise.resolve(mockApiResponse) );
        createCardModal = await shallowMount(EditCreateCardModal, {localVue})
        await createCardModal.vm.$nextTick();

        expect(createCardModal.vm.$data.userLocation).toBe("N/A");
        expect(createCardModal.find("#user-location").exists()).toBe(true)
        expect(createCardModal.find("#user-location").text()).toBe("N/A");
    })

    test("Test that the location is correctly generated with both city and suburb", async () => {
        const mockApiResponse = {
            status: 200,
            data: {
                homeAddress: {
                    city: "CITY",
                    suburb: "SUBURB"
                },
            }
        }

        Api.getUser.mockImplementation( () => Promise.resolve(mockApiResponse) );
        createCardModal = await shallowMount(EditCreateCardModal, {localVue})
        await createCardModal.vm.$nextTick();

        expect(createCardModal.vm.$data.userLocation).toBe("SUBURB, CITY");
        expect(createCardModal.find("#user-location").exists()).toBe(true)
        expect(createCardModal.find("#user-location").text()).toBe("SUBURB, CITY");
    })

})

describe( "Testing the title input field", () => {

    let createCardModalWrapper;

    beforeEach(async () => {
        const mockGetUserApiResponse = {
            status: 200,
            data: {
                firstName: "FIRST_NAME",
                lastName: "LAST_NAME",
                role: UserRole.DEFAULTGLOBALAPPLICATIONADMIN,
                homeAddress: {
                    city: "CITY",
                    suburb: "SUBURB"
                },
            }
        }

        Api.getUser.mockImplementation( () => Promise.resolve(mockGetUserApiResponse) );
        Api.addNewCard.mockImplementation( () => {} );
        Cookies.get.mockReturnValue(36);

        createCardModalWrapper = await shallowMount(EditCreateCardModal, {localVue});
        await createCardModalWrapper.vm.$nextTick();

    })

    test("Testing an empty title input field", async () => {

        // Checking all necessary elements are exist and do not.
        expect(createCardModalWrapper.find("#card-title-default").exists()).toBe(true);
        expect(createCardModalWrapper.find("#card-title-invalid-feedback").exists()).toBe(false);

        // Perform a submission
        await createCardModalWrapper.vm.createNewCard();
        await createCardModalWrapper.vm.$nextTick();

        // Ensure invalid feedback is provided.
        expect(Api.addNewCard).toBeCalledTimes(0);
        expect(createCardModalWrapper.find("#card-title-invalid-feedback").exists()).toBe(true);
        expect(createCardModalWrapper.find("#card-title-invalid-feedback").text()).toBe("The title must be between 1 and 50 in length.");
        expect(createCardModalWrapper.vm.$data.formError.titleError).toBe("The title must be between 1 and 50 in length.")
        expect(createCardModalWrapper.vm.$data.formErrorClasses.titleError).toBe("is-invalid")
    })

    test("Testing a single character string (A)", async () => {

        // Checking all necessary elements are exist and do not.
        expect(createCardModalWrapper.find("#card-title-default").exists()).toBe(true);
        expect(createCardModalWrapper.find("#card-title-invalid-feedback").exists()).toBe(false);

        // Set the title input value.
        createCardModalWrapper.find("#card-title-default").setValue("A");

        // Ensure that the data was updated.
        expect(createCardModalWrapper.vm.$data.title).toBe("A");

        // Perform a submission
        await createCardModalWrapper.vm.createNewCard();
        await createCardModalWrapper.vm.$nextTick();

        // Ensure invalid feedback is provided.
        expect(createCardModalWrapper.find("#card-title-invalid-feedback").exists()).toBe(false);
        expect(createCardModalWrapper.vm.$data.formError.titleError).toBe("");
        expect(createCardModalWrapper.vm.$data.formErrorClasses.titleError).toBe("")
    })

    test("Testing with 50 characters string (12345678912345678912345678912345678912345678912345)", async () => {

        // Checking all necessary elements are exist and do not.
        expect(createCardModalWrapper.find("#card-title-default").exists()).toBe(true);
        expect(createCardModalWrapper.find("#card-title-invalid-feedback").exists()).toBe(false);

        // Set the title input value.
        createCardModalWrapper.find("#card-title-default").setValue("12345678912345678912345678912345678912345678912345");

        // Ensure that the data was updated.
        expect(createCardModalWrapper.vm.$data.title).toBe("12345678912345678912345678912345678912345678912345");

        // Perform a submission
        await createCardModalWrapper.vm.createNewCard();
        await createCardModalWrapper.vm.$nextTick();

        // Ensure invalid feedback is provided.
        expect(createCardModalWrapper.find("#card-title-invalid-feedback").exists()).toBe(false);
        expect(createCardModalWrapper.vm.$data.formError.titleError).toBe("");
        expect(createCardModalWrapper.vm.$data.formErrorClasses.titleError).toBe("")
    })

    test("Testing with 51 characters string (123456789123456789123456789123456789123456789123456)", async () => {

        // Checking all necessary elements are exist and do not.
        expect(createCardModalWrapper.find("#card-title-default").exists()).toBe(true);
        expect(createCardModalWrapper.find("#card-title-invalid-feedback").exists()).toBe(false);

        // Set the title input value.
        createCardModalWrapper.find("#card-title-default").setValue("123456789123456789123456789123456789123456789123456");

        // Ensure that the data was updated.
        expect(createCardModalWrapper.vm.$data.title).toBe("123456789123456789123456789123456789123456789123456");

        // Perform a submission
        await createCardModalWrapper.vm.createNewCard();
        await createCardModalWrapper.vm.$nextTick();

        // Ensure invalid feedback is provided.
        expect(Api.addNewCard).toBeCalledTimes(0);
        expect(createCardModalWrapper.find("#card-title-invalid-feedback").exists()).toBe(true);
        expect(createCardModalWrapper.find("#card-title-invalid-feedback").text()).toBe("The title must be between 1 and 50 in length.");
        expect(createCardModalWrapper.vm.$data.formError.titleError).toBe("The title must be between 1 and 50 in length.")
        expect(createCardModalWrapper.vm.$data.formErrorClasses.titleError).toBe("is-invalid")
    })

    test("Testing the title can handle emojis ????", async () => {
        // Checking all necessary elements are exist and do not.
        expect(createCardModalWrapper.find("#card-title-default").exists()).toBe(true);
        expect(createCardModalWrapper.find("#card-title-invalid-feedback").exists()).toBe(false);

        // Set the title input value.
        createCardModalWrapper.find("#card-title-default").setValue("????");

        // Ensure that the data was updated.
        expect(createCardModalWrapper.vm.$data.title).toBe("????");

        // Perform a submission
        await createCardModalWrapper.vm.createNewCard();
        await createCardModalWrapper.vm.$nextTick();

        // Ensure invalid feedback is provided.
        expect(createCardModalWrapper.find("#card-title-invalid-feedback").exists()).toBe(false);
        expect(createCardModalWrapper.vm.$data.formError.titleError).toBe("");
        expect(createCardModalWrapper.vm.$data.formErrorClasses.titleError).toBe("")
    })
})

describe("Testing the description field", () => {

    let createCardModalWrapper;

    beforeEach(async () => {
        const mockGetUserApiResponse = {
            status: 200,
            data: {
                firstName: "FIRST_NAME",
                lastName: "LAST_NAME",
                role: UserRole.DEFAULTGLOBALAPPLICATIONADMIN,
                homeAddress: {
                    city: "CITY",
                    suburb: "SUBURB"
                },
            }
        }

        Api.getUser.mockImplementation( () => Promise.resolve(mockGetUserApiResponse) );
        Api.addNewCard.mockImplementation( () => {} );
        Cookies.get.mockReturnValue(36);

        createCardModalWrapper = await shallowMount(EditCreateCardModal, {localVue});
        await createCardModalWrapper.vm.$nextTick();

    })

    test("Testing an empty description input field", async () => {
        // Checking all necessary elements are exist and do not.
        expect(createCardModalWrapper.find("#card-description-default").exists()).toBe(true);
        expect(createCardModalWrapper.find("#card-description-invalid-feedback").exists()).toBe(false);

        // Perform a submission
        await createCardModalWrapper.vm.createNewCard();
        await createCardModalWrapper.vm.$nextTick();

        // Ensure invalid feedback is not provided.
        expect(createCardModalWrapper.find("#card-description-invalid-feedback").exists()).toBe(false);
        expect(createCardModalWrapper.vm.$data.formError.descriptionError).toBe("");
        expect(createCardModalWrapper.vm.$data.formErrorClasses.descriptionError).toBe("")
    })

    test("Testing a single character string (A)", async () => {
        // Checking all necessary elements are exist and do not.
        expect(createCardModalWrapper.find("#card-description-default").exists()).toBe(true);
        expect(createCardModalWrapper.find("#card-description-invalid-feedback").exists()).toBe(false);

        // Set the description input value.
        createCardModalWrapper.find("#card-title-default").setValue("A");

        // Ensure that the data was updated.
        expect(createCardModalWrapper.vm.$data.title).toBe("A");

        // Perform a submission
        await createCardModalWrapper.vm.createNewCard();
        await createCardModalWrapper.vm.$nextTick();

        // Ensure invalid feedback is not provided.
        expect(createCardModalWrapper.find("#card-description-invalid-feedback").exists()).toBe(false);
        expect(createCardModalWrapper.vm.$data.formError.descriptionError).toBe("");
        expect(createCardModalWrapper.vm.$data.formErrorClasses.descriptionError).toBe("")
    })

    test("Testing with 300 characters string", async () => {
        // Checking all necessary elements are exist and do not.
        expect(createCardModalWrapper.find("#card-description-default").exists()).toBe(true);
        expect(createCardModalWrapper.find("#card-description-invalid-feedback").exists()).toBe(false);

        // Set the description input value.
        createCardModalWrapper.find("#card-description-default").setValue("A".repeat(300));

        // Ensure that the data was updated.
        expect(createCardModalWrapper.vm.$data.description).toBe("A".repeat(300));

        // Perform a submission
        await createCardModalWrapper.vm.createNewCard();
        await createCardModalWrapper.vm.$nextTick();

        // Ensure invalid feedback is not provided.
        expect(createCardModalWrapper.find("#card-description-invalid-feedback").exists()).toBe(false);
        expect(createCardModalWrapper.vm.$data.formError.descriptionError).toBe("");
        expect(createCardModalWrapper.vm.$data.formErrorClasses.descriptionError).toBe("")
    })

    test("Testing with 301 characters string", async () => {
        // Checking all necessary elements are exist and do not.
        expect(createCardModalWrapper.find("#card-description-default").exists()).toBe(true);
        expect(createCardModalWrapper.find("#card-description-invalid-feedback").exists()).toBe(false);

        // Set the description input value.
        createCardModalWrapper.find("#card-description-default").setValue("A".repeat(301));

        // Ensure that the data was updated.
        expect(createCardModalWrapper.vm.$data.description).toBe("A".repeat(301));

        // Perform a submission
        await createCardModalWrapper.vm.createNewCard();
        await createCardModalWrapper.vm.$nextTick();

        // Ensure invalid feedback is provided.
        expect(Api.addNewCard).toBeCalledTimes(0);
        expect(createCardModalWrapper.find("#card-description-invalid-feedback").exists()).toBe(true);
        expect(createCardModalWrapper.find("#card-description-invalid-feedback").text()).toBe("The description length must be between 0 and 300 in length.");
        expect(createCardModalWrapper.vm.$data.formError.descriptionError).toBe("The description length must be between 0 and 300 in length.")
        expect(createCardModalWrapper.vm.$data.formErrorClasses.descriptionError).toBe("is-invalid")
    })

    test("Testing the description can handle emojis ????", async () => {
        // Checking all necessary elements are exist and do not.
        expect(createCardModalWrapper.find("#card-description-default").exists()).toBe(true);
        expect(createCardModalWrapper.find("#card-description-invalid-feedback").exists()).toBe(false);

        // Set the description input value.
        createCardModalWrapper.find("#card-description-default").setValue("????");

        // Ensure that the data was updated.
        expect(createCardModalWrapper.vm.$data.description).toBe("????");

        // Perform a submission
        await createCardModalWrapper.vm.createNewCard();
        await createCardModalWrapper.vm.$nextTick();

        // Ensure invalid feedback is provided.
        expect(createCardModalWrapper.find("#card-description-invalid-feedback").exists()).toBe(false);
        expect(createCardModalWrapper.vm.$data.formError.descriptionError).toBe("");
        expect(createCardModalWrapper.vm.$data.formErrorClasses.descriptionError).toBe("")
    })

})

describe("Testing the keywords field", () => {

    let createCardModalWrapper;

    beforeEach(async () => {
        const mockGetUserApiResponse = {
            status: 200,
            data: {
                firstName: "FIRST_NAME",
                lastName: "LAST_NAME",
                role: UserRole.DEFAULTGLOBALAPPLICATIONADMIN,
                homeAddress: {
                    city: "CITY",
                    suburb: "SUBURB"
                },
            }
        }

        Api.getUser.mockImplementation( () => Promise.resolve(mockGetUserApiResponse) );
        Api.addNewCard.mockImplementation( () => {} );
        Cookies.get.mockReturnValue(36);

        createCardModalWrapper = await shallowMount(EditCreateCardModal, {localVue});
        await createCardModalWrapper.vm.$nextTick();
        await createCardModalWrapper.setProps({currentModal: "create"});

    })

    test("Testing an empty keywords input field", async () => {
        // Checking all necessary elements are exist and do not.
        expect(createCardModalWrapper.find("#card-keywords-create-default").exists()).toBe(true);
        expect(createCardModalWrapper.find("#card-keywords-invalid-feedback").exists()).toBe(false);

        // Perform a submission
        await createCardModalWrapper.vm.createNewCard();
        await createCardModalWrapper.vm.$nextTick();

        // Ensure invalid feedback is not provided.
        expect(createCardModalWrapper.find("#card-keywords-invalid-feedback").exists()).toBe(false);
        expect(createCardModalWrapper.vm.$data.formError.keywordsError).toBe("");
        expect(createCardModalWrapper.vm.$data.formErrorClasses.keywordsError).toBe("")
    })

    test("Testing a single character string (A)", async () => {
        // Checking all necessary elements are exist and do not.
        expect(createCardModalWrapper.find("#card-keywords-create-default").exists()).toBe(true);
        expect(createCardModalWrapper.find("#card-keywords-invalid-feedback").exists()).toBe(false);

        // Set the title input value.
        createCardModalWrapper.find("#card-keywords-create-default").setValue("A");
        await createCardModalWrapper.vm.$nextTick();

        // Ensure that the data was updated.
        expect(createCardModalWrapper.vm.$data.keywordsInput).toBe("#A");

        // Perform a submission
        await createCardModalWrapper.vm.createNewCard();
        await createCardModalWrapper.vm.$nextTick();

        // Ensure invalid feedback is not provided.
        expect(createCardModalWrapper.find("#card-keywords-invalid-feedback").exists()).toBe(true);
        expect(createCardModalWrapper.vm.$data.formError.keywordsError).toBe("All keywords need to be between 2 and 20 in length.");
        expect(createCardModalWrapper.vm.$data.formErrorClasses.keywordsError).toBe("is-invalid")
    })

    test("Testing a single character string (#)", async () => {
        // Checking all necessary elements are exist and do not.
        expect(createCardModalWrapper.find("#card-keywords-create-default").exists()).toBe(true);
        expect(createCardModalWrapper.find("#card-keywords-invalid-feedback").exists()).toBe(false);

        // Set the title input value.
        createCardModalWrapper.find("#card-keywords-create-default").setValue("#");
        await createCardModalWrapper.vm.$nextTick();

        // Ensure that the data was updated.
        expect(createCardModalWrapper.vm.$data.keywordsInput).toBe("#");

        // Perform a submission
        await createCardModalWrapper.vm.createNewCard();
        await createCardModalWrapper.vm.$nextTick();

        // Ensure invalid feedback is not provided.
        expect(createCardModalWrapper.find("#card-keywords-invalid-feedback").exists()).toBe(true);
        expect(createCardModalWrapper.vm.$data.formError.keywordsError).toBe("All keywords need to be between 2 and 20 in length.");
        expect(createCardModalWrapper.vm.$data.formErrorClasses.keywordsError).toBe("is-invalid")
    })

    test("Testing with 3 character string (123)", async () => {
        // Checking all necessary elements are exist and do not.
        expect(createCardModalWrapper.find("#card-keywords-create-default").exists()).toBe(true);
        expect(createCardModalWrapper.find("#card-keywords-invalid-feedback").exists()).toBe(false);

        // Set the title input value.
        createCardModalWrapper.find("#card-keywords-create-default").setValue("123");
        await createCardModalWrapper.vm.$nextTick();

        // Ensure that the data was updated.
        expect(createCardModalWrapper.vm.$data.keywordsInput).toBe("#"+"123");

        // Perform a submission
        await createCardModalWrapper.vm.createNewCard();
        await createCardModalWrapper.vm.$nextTick();

        // Ensure invalid feedback is not provided.
        expect(createCardModalWrapper.find("#card-keywords-invalid-feedback").exists()).toBe(false);
        expect(createCardModalWrapper.vm.$data.formError.keywordsError).toBe("");
        expect(createCardModalWrapper.vm.$data.formErrorClasses.keywordsError).toBe("")
    })

    test("Testing with 19 characters string", async () => {
        // Checking all necessary elements are exist and do not.
        expect(createCardModalWrapper.find("#card-keywords-create-default").exists()).toBe(true);
        expect(createCardModalWrapper.find("#card-keywords-invalid-feedback").exists()).toBe(false);

        // Set the title input value.
        createCardModalWrapper.find("#card-keywords-create-default").setValue("A".repeat(19));
        await createCardModalWrapper.vm.$nextTick();

        // Ensure that the data was updated.
        expect(createCardModalWrapper.vm.$data.keywordsInput).toBe("#"+"A".repeat(19));

        // Perform a submission
        await createCardModalWrapper.vm.createNewCard();
        await createCardModalWrapper.vm.$nextTick();

        // Ensure invalid feedback is not provided.
        expect(createCardModalWrapper.find("#card-keywords-invalid-feedback").exists()).toBe(false);
        expect(createCardModalWrapper.vm.$data.formError.keywordsError).toBe("");
        expect(createCardModalWrapper.vm.$data.formErrorClasses.keywordsError).toBe("")
    })

    test("Testing with 20 characters string", async () => {
        // Checking all necessary elements are exist and do not.
        expect(createCardModalWrapper.find("#card-keywords-create-default").exists()).toBe(true);
        expect(createCardModalWrapper.find("#card-keywords-invalid-feedback").exists()).toBe(false);

        // Set the title input value.
        createCardModalWrapper.find("#card-keywords-create-default").setValue("A".repeat(20));
        await createCardModalWrapper.vm.$nextTick();

        // Ensure that the data was updated. and the extra characters are removed!
        expect(createCardModalWrapper.vm.$data.keywordsInput).toBe("#"+"A".repeat(19));

        // Perform a submission
        await createCardModalWrapper.vm.createNewCard();
        await createCardModalWrapper.vm.$nextTick();

        // Ensure invalid feedback is not provided.
        expect(createCardModalWrapper.find("#card-keywords-invalid-feedback").exists()).toBe(false);
        expect(createCardModalWrapper.vm.$data.formError.keywordsError).toBe("");
        expect(createCardModalWrapper.vm.$data.formErrorClasses.keywordsError).toBe("")
    })

    test("Testing the keywords input can handle emojis ????????????", async () => {
        // Checking all necessary elements are exist and do not.
        expect(createCardModalWrapper.find("#card-keywords-create-default").exists()).toBe(true);
        expect(createCardModalWrapper.find("#card-keywords-invalid-feedback").exists()).toBe(false);

        // Set the title input value.
        createCardModalWrapper.find("#card-keywords-create-default").setValue("????????????");
        await createCardModalWrapper.vm.$nextTick();

        // Ensure that the data was updated.
        expect(createCardModalWrapper.vm.$data.keywordsInput).toBe("#????????????");

        // Perform a submission
        await createCardModalWrapper.vm.createNewCard();
        await createCardModalWrapper.vm.$nextTick();

        // Ensure invalid feedback is not provided.
        expect(createCardModalWrapper.find("#card-keywords-invalid-feedback").exists()).toBe(false);
        expect(createCardModalWrapper.vm.$data.formError.keywordsError).toBe("");
        expect(createCardModalWrapper.vm.$data.formErrorClasses.keywordsError).toBe("")
    })

    test("Testing the keywords input can handle emojis ????", async () => {
        // Checking all necessary elements are exist and do not.
        expect(createCardModalWrapper.find("#card-keywords-create-default").exists()).toBe(true);
        expect(createCardModalWrapper.find("#card-keywords-invalid-feedback").exists()).toBe(false);

        // Set the title input value.
        createCardModalWrapper.find("#card-keywords-create-default").setValue("????");
        await createCardModalWrapper.vm.$nextTick();

        // Ensure that the data was updated.
        expect(createCardModalWrapper.vm.$data.keywordsInput).toBe("#????");

        // Perform a submission
        await createCardModalWrapper.vm.createNewCard();
        await createCardModalWrapper.vm.$nextTick();

        // Ensure invalid feedback is not provided.
        expect(createCardModalWrapper.find("#card-keywords-invalid-feedback").exists()).toBe(false);
        expect(createCardModalWrapper.vm.$data.formError.keywordsError).toBe("");
        expect(createCardModalWrapper.vm.$data.formErrorClasses.keywordsError).toBe("")
    })

    test("Testing the keywords input can handle emojis ????????", async () => {
        // Checking all necessary elements are exist and do not.
        expect(createCardModalWrapper.find("#card-keywords-create-default").exists()).toBe(true);
        expect(createCardModalWrapper.find("#card-keywords-invalid-feedback").exists()).toBe(false);

        // Set the title input value.
        createCardModalWrapper.find("#card-keywords-create-default").setValue("????????");
        await createCardModalWrapper.vm.$nextTick();

        // Ensure that the data was updated.
        expect(createCardModalWrapper.vm.$data.keywordsInput).toBe("#????????");

        // Perform a submission
        await createCardModalWrapper.vm.createNewCard();
        await createCardModalWrapper.vm.$nextTick();

        // Ensure invalid feedback is not provided.
        expect(createCardModalWrapper.find("#card-keywords-invalid-feedback").exists()).toBe(false);
        expect(createCardModalWrapper.vm.$data.formError.keywordsError).toBe("");
        expect(createCardModalWrapper.vm.$data.formErrorClasses.keywordsError).toBe("")
    })

    test("Testing the keyword autocompletion", async () => {
        createCardModalWrapper.vm.$data.keywordsInput = "#Dri"

        let currentKeywordStartEnd = createCardModalWrapper.vm.getCurrentKeywordStartEnd()
        expect(currentKeywordStartEnd).toStrictEqual([0, 4])

        createCardModalWrapper.vm.updateKeyword("Drink")
        expect(createCardModalWrapper.vm.$data.keywordsInput).toBe("#Drink")

        await createCardModalWrapper.vm.$nextTick();
        expect(createCardModalWrapper.vm.$data.keywordsInput).toBe("#Drink")

        currentKeywordStartEnd = createCardModalWrapper.vm.getCurrentKeywordStartEnd()
        expect(currentKeywordStartEnd).toStrictEqual([0, 6])
    })

    test("Testing the keyword autocompletion overriding an existing key", async () => {
        createCardModalWrapper.vm.$data.keywordsInput = "#Coffee"

        let currentKeywordStartEnd = createCardModalWrapper.vm.getCurrentKeywordStartEnd()
        expect(currentKeywordStartEnd).toStrictEqual([0, 7])

        createCardModalWrapper.vm.updateKeyword("NotCoffee")
        expect(createCardModalWrapper.vm.$data.keywordsInput).toBe("#NotCoffee")

        await createCardModalWrapper.vm.$nextTick();
        expect(createCardModalWrapper.vm.$data.keywordsInput).toBe("#NotCoffee")

        currentKeywordStartEnd = createCardModalWrapper.vm.getCurrentKeywordStartEnd()
        expect(currentKeywordStartEnd).toStrictEqual([0, 10])
    })

    test("Testing the keywords autocompletion with multiple existing keywords", async () => {
        createCardModalWrapper.vm.$data.keywordsInput = "#Coffee #Drink #Tea"

        let currentKeywordStartEnd = createCardModalWrapper.vm.getCurrentKeywordStartEnd()
        expect(currentKeywordStartEnd).toStrictEqual([0, 7])

        createCardModalWrapper.vm.updateKeyword("NotCoffee")
        expect(createCardModalWrapper.vm.$data.keywordsInput).toBe("#NotCoffee #Drink #Tea")

        await createCardModalWrapper.vm.$nextTick();
        expect(createCardModalWrapper.vm.$data.keywordsInput).toBe("#NotCoffee #Drink #Tea")

        currentKeywordStartEnd = createCardModalWrapper.vm.getCurrentKeywordStartEnd()
        expect(currentKeywordStartEnd).toStrictEqual([0, 10])
    })

    test("Testing the keywords autocompletion with no existing keywords", async () => {
        createCardModalWrapper.vm.$data.keywordsInput = ""

        let currentKeywordStartEnd = createCardModalWrapper.vm.getCurrentKeywordStartEnd()
        expect(currentKeywordStartEnd).toBe(false)

        createCardModalWrapper.vm.updateKeyword("Anything")
        expect(createCardModalWrapper.vm.$data.keywordsInput).toBe("Anything")

        await createCardModalWrapper.vm.$nextTick();
        expect(createCardModalWrapper.vm.$data.keywordsInput).toBe("#Anything")

        currentKeywordStartEnd = createCardModalWrapper.vm.getCurrentKeywordStartEnd()
        expect(currentKeywordStartEnd).toStrictEqual([0, 9])
    })

})

describe("Testing required fields", () => {

    let createCardWrapper;

    beforeEach( async () => {
        // Mock all the API calls needed.
        const getUserMockResponse = {
            status: 200,
            data: {
                role: UserRole.DEFAULTGLOBALAPPLICATIONADMIN,
                firstName: "FIRST",
                lastName: "LAST",
                homeAddress: {
                    city: "CITY",
                    suburb: "SUBURB"
                }
            }
        }
        Api.getUser.mockImplementation( () => Promise.resolve(getUserMockResponse))
        Cookies.get.mockReturnValue(36);
        Api.addNewCard.mockImplementation( () => Promise.resolve( {status: 201} ) )

        // Shallow mount the component
        createCardWrapper = await shallowMount(EditCreateCardModal, {localVue});
        await createCardWrapper.vm.$nextTick();
        await createCardWrapper.setProps({currentModal: "create"});
        await createCardWrapper.vm.$nextTick();

    } )

    test("Submitting the create new card form with minimum required fields", async () => {
        // Set the section & verify it is there.
        expect(createCardWrapper.find("#section-selection-default").exists()).toBe(true)
        expect(createCardWrapper.find("#for-sale-option").exists()).toBe(true);
        createCardWrapper.find("#for-sale-option").setSelected();
        expect(createCardWrapper.vm.$data.sectionSelected).toBe("ForSale");

        // Verify that the creator Id is set.
        expect(createCardWrapper.vm.$data.creatorId).toBe(36);

        // Set and verify the title.
        expect(createCardWrapper.find("#card-title-default").exists()).toBe(true);
        createCardWrapper.find("#card-title-default").setValue("A Title");
        expect(createCardWrapper.find("#card-title-default").element.value).toBe("A Title");
        expect(createCardWrapper.vm.$data.title).toBe("A Title");

        // Attempt to create the card.
        await createCardWrapper.vm.createNewCard();
        expect(createCardWrapper.vm.creatorId).toBe(36);
        expect(Api.addNewCard).toBeCalledTimes(1);
    })

    describe("Assorted method tests", () => {

        test("Testing isCreatorIdInvalid method when there is no UserID cookie", async () => {
            let $router = {
                push: jest.fn()
            };

            let createCardModalWrapper = shallowMount(EditCreateCardModal, {
                mocks: {
                    $router
                }
            });

            createCardModalWrapper.vm.submitAttempted = true;

            Cookies.get.mockReturnValue("");

            Api.signOut.mockImplementation( () => Promise.resolve() );

            let returned = await createCardModalWrapper.vm.isCreatorIdInvalid();

            expect(returned).toBeTruthy();
            expect($router.push).toHaveBeenCalledWith({"name": "Login"});
        });

        test("Testing isCreatorIdInvalid method when the user is an admin and the creator ID is empty", async () => {
            let $router = {
                push: jest.fn()
            };

            let createCardModalWrapper = shallowMount(EditCreateCardModal, {
                mocks: {
                    $router
                }
            });

            createCardModalWrapper.vm.submitAttempted = true;
            createCardModalWrapper.vm.userRole = UserRole.GLOBALAPPLICATIONADMIN;
            createCardModalWrapper.vm.creatorId = "";

            let returned = await createCardModalWrapper.vm.isCreatorIdInvalid();

            expect(returned).toBeTruthy();
            expect(createCardModalWrapper.vm.formErrorClasses.creatorIdError).toBe("is-invalid");
            expect(createCardModalWrapper.vm.formError.creatorIdError).toBe("This field is required.");
            expect($router.push).toHaveBeenCalledTimes(0);
        });

        test("Testing isCardDataValid method when all data is valid except for user ID", async () => {
            let $router = {
                push: jest.fn()
            };

            let createCardModalWrapper = shallowMount(EditCreateCardModal, {
                mocks: {
                    $router
                }
            });

            createCardModalWrapper.vm.submitAttempted = true;
            createCardModalWrapper.vm.sectionSelected = 'ForSale';
            createCardModalWrapper.vm.title = 'Card';
            createCardModalWrapper.vm.description = 'Desc';

            Cookies.get.mockReturnValue("");

            Api.signOut.mockImplementation( () => Promise.resolve() );

            let returned = await createCardModalWrapper.vm.isCardDataValid();

            expect(returned).toBeFalsy();
        });

        test("Testing createNewCard when 201 response is received from Api", async () => {
            let $router = {
                go: jest.fn()
            };

            let createCardModalWrapper = shallowMount(EditCreateCardModal, {
                mocks: {
                    $router
                }
            });

            createCardModalWrapper.vm.submitAttempted = true;
            createCardModalWrapper.vm.sectionSelected = 'ForSale';
            createCardModalWrapper.vm.title = 'Card';
            createCardModalWrapper.vm.description = 'Desc';

            Cookies.get.mockReturnValue(36);

            let mockResponse = {
                status: 201
            }

            Api.addNewCard.mockImplementation( () => Promise.resolve(mockResponse) );

            await createCardModalWrapper.vm.createNewCard();

            expect(createCardModalWrapper.emitted("new-card-created")).toBeTruthy();
        });

        test("Testing createNewCard when a 400 response is received from Api", async () => {
            let createCardModalWrapper = shallowMount(EditCreateCardModal, {});

            createCardModalWrapper.vm.submitAttempted = true;
            createCardModalWrapper.vm.sectionSelected = 'ForSale';
            createCardModalWrapper.vm.title = 'Card';
            createCardModalWrapper.vm.description = 'Desc';

            Cookies.get.mockReturnValue(36);

            let mockResponse = {
                response: {
                    status: 400,
                    data: {
                        message: "test"
                    }
                }
            }

            Api.addNewCard.mockImplementation( () => Promise.reject(mockResponse) );

            await createCardModalWrapper.vm.createNewCard();
            await createCardModalWrapper.vm.$nextTick();

            expect(createCardModalWrapper.vm.modalError).toBe('Error: test');
        });

        test("Testing createNewCard when a 401 response is received from Api", async () => {
            let createCardModalWrapper = shallowMount(EditCreateCardModal, {});

            createCardModalWrapper.vm.submitAttempted = true;
            createCardModalWrapper.vm.sectionSelected = 'ForSale';
            createCardModalWrapper.vm.title = 'Card';
            createCardModalWrapper.vm.description = 'Desc';

            Cookies.get.mockReturnValue(36);

            let mockResponse = {
                response: {
                    status: 401,
                    data: {
                        message: "test"
                    }
                }
            };

            Api.addNewCard.mockImplementation( () => Promise.reject(mockResponse) );

            await createCardModalWrapper.vm.createNewCard();
            await createCardModalWrapper.vm.$nextTick();

            expect(createCardModalWrapper.vm.modalError).toBe('401: Access token missing');
        });

        test("Testing createNewCard when a 403 response is received from Api", async () => {
            let createCardModalWrapper = shallowMount(EditCreateCardModal, {});

            createCardModalWrapper.vm.submitAttempted = true;
            createCardModalWrapper.vm.sectionSelected = 'ForSale';
            createCardModalWrapper.vm.title = 'Card';
            createCardModalWrapper.vm.description = 'Desc';

            Cookies.get.mockReturnValue(36);

            let mockResponse = {
                response: {
                    status: 403,
                    data: {
                        message: "test"
                    }
                }
            };

            Api.addNewCard.mockImplementation( () => Promise.reject(mockResponse) );

            await createCardModalWrapper.vm.createNewCard();
            await createCardModalWrapper.vm.$nextTick();

            expect(createCardModalWrapper.vm.modalError).toBe('403: Cannot create card for another user if not GAA or DGAA.');
        });

        test("Testing createNewCard when a different error response is received from Api", async () => {
            let createCardModalWrapper = shallowMount(EditCreateCardModal, {});

            createCardModalWrapper.vm.submitAttempted = true;
            createCardModalWrapper.vm.sectionSelected = 'ForSale';
            createCardModalWrapper.vm.title = 'Card';
            createCardModalWrapper.vm.description = 'Desc';

            Cookies.get.mockReturnValue(36);

            let mockResponse = {
                response: {
                    status: 404,
                    data: {
                        message: "test"
                    }
                }
            };

            Api.addNewCard.mockImplementation( () => Promise.reject(mockResponse) );

            await createCardModalWrapper.vm.createNewCard();
            await createCardModalWrapper.vm.$nextTick();

            expect(createCardModalWrapper.vm.modalError).toBe('404: SOMETHING WENT WRONG');
        });

        test("Testing createNewCard when an error request is received from Api", async () => {
            let createCardModalWrapper = shallowMount(EditCreateCardModal, {});

            createCardModalWrapper.vm.submitAttempted = true;
            createCardModalWrapper.vm.sectionSelected = 'ForSale';
            createCardModalWrapper.vm.title = 'Card';
            createCardModalWrapper.vm.description = 'Desc';

            Cookies.get.mockReturnValue(36);

            let mockResponse = {
                request: {
                }
            };

            Api.addNewCard.mockImplementation( () => Promise.reject(mockResponse) );

            await createCardModalWrapper.vm.createNewCard();
            await createCardModalWrapper.vm.$nextTick();

            expect(createCardModalWrapper.vm.modalError).toBe('Server Timeout');
        });

        test("Testing createNewCard when a different error is received from Api", async () => {
            let createCardModalWrapper = shallowMount(EditCreateCardModal, {});

            createCardModalWrapper.vm.submitAttempted = true;
            createCardModalWrapper.vm.sectionSelected = 'ForSale';
            createCardModalWrapper.vm.title = 'Card';
            createCardModalWrapper.vm.description = 'Desc';

            Cookies.get.mockReturnValue(36);

            let mockResponse = {};

            Api.addNewCard.mockImplementation( () => Promise.reject(mockResponse) );

            await createCardModalWrapper.vm.createNewCard();
            await createCardModalWrapper.vm.$nextTick();

            expect(createCardModalWrapper.vm.modalError).toBe('Unexpected error occurred.');
        });

        test("Testing addKeywordPrefix when the keyword isn't a string", () => {
            let createCardModalWrapper = shallowMount(EditCreateCardModal, {});

            try {
                createCardModalWrapper.vm.addKeywordPrefix(2);
            } catch (error) {
                expect(error.message).toBe("keyword must be string!")
            }
        });

        test("Testing enforceKeywordMaxLength when the keyword isn't a string", () => {
            let createCardModalWrapper = shallowMount(EditCreateCardModal, {});

            try {
                createCardModalWrapper.vm.enforceKeywordMaxLength(2);
            } catch (error) {
                expect(error.message).toBe("keyword must be string!")
            }
        });

        test("Testing convertSection when the section is WANTED", () => {
            let createCardModalWrapper = shallowMount(EditCreateCardModal, {});

            createCardModalWrapper.vm.convertSection('WANTED');

            expect(createCardModalWrapper.vm.sectionSelected).toBe('Wanted');
        });

        test("Testing convertSection when the section is EXCHANGE", () => {
            let createCardModalWrapper = shallowMount(EditCreateCardModal, {});

            createCardModalWrapper.vm.convertSection('EXCHANGE');

            expect(createCardModalWrapper.vm.sectionSelected).toBe('Exchange');
        });

        test("Testing getCurrentData when a 400 response is received from Api", async () => {
            let $router = {
                push: jest.fn()
            };

            let createCardModalWrapper = shallowMount(EditCreateCardModal, {
                mocks: {
                    $router
                }
            });

            let mockResponse = {
                response: {
                    status: 400
                }
            };

            Api.getDetailForACard.mockImplementation( () => Promise.reject(mockResponse) );

            await createCardModalWrapper.vm.getCurrentData();

            expect($router.push).toHaveBeenCalledWith({"path": "/pageDoesNotExist"});
        });

        test("Testing getCurrentData when a 401 response is received from Api", async () => {
            let $router = {
                push: jest.fn()
            };

            let createCardModalWrapper = shallowMount(EditCreateCardModal, {
                mocks: {
                    $router
                }
            });

            let mockResponse = {
                response: {
                    status: 401
                }
            };

            Api.getDetailForACard.mockImplementation( () => Promise.reject(mockResponse) );

            await createCardModalWrapper.vm.getCurrentData();

            expect($router.push).toHaveBeenCalledWith({"path": "/invalidtoken"});
        });

        test("Testing getCurrentData when a 406 response is received from Api", async () => {
            let $router = {
                push: jest.fn()
            };

            let createCardModalWrapper = shallowMount(EditCreateCardModal, {
                mocks: {
                    $router
                }
            });

            let mockResponse = {
                response: {
                    status: 406
                }
            };

            Api.getDetailForACard.mockImplementation( () => Promise.reject(mockResponse) );

            await createCardModalWrapper.vm.getCurrentData();

            expect($router.push).toHaveBeenCalledWith({"path": "/noCard"});
        });

        test("Testing getCurrentData when another error is received from Api", async () => {
            let $router = {
                push: jest.fn()
            };

            let createCardModalWrapper = shallowMount(EditCreateCardModal, {
                mocks: {
                    $router
                }
            });

            let mockResponse = {};

            Api.getDetailForACard.mockImplementation( () => Promise.reject(mockResponse) );

            await createCardModalWrapper.vm.getCurrentData();

            expect($router.push).toHaveBeenCalledWith({"path": "/noCard"});
        });

        test("Testing resetData", () => {
            let createCardModalWrapper = shallowMount(EditCreateCardModal, {});

            createCardModalWrapper.vm.resetData();

            expect(createCardModalWrapper.vm.id).toBeNull();
            expect(createCardModalWrapper.vm.submitAttempted).toBeFalsy();
            expect(createCardModalWrapper.vm.description).toBe("");
            expect(createCardModalWrapper.vm.title).toBe("");
            expect(createCardModalWrapper.vm.sectionSelected).toBe("");
            expect(createCardModalWrapper.vm.keywordsInput).toBe("");
        });
    });

})
