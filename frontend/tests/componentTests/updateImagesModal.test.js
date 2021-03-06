/**
 * @jest-environment jsdom
 */

import {shallowMount} from '@vue/test-utils';
import updateImagesModal from "../../src/components/UpdateImagesModal";
import Api from "../../src/Api";
import {describe, expect, jest, test} from "@jest/globals";
import Product from "../../src/configs/Product";
const endOfToday = require('date-fns/endOfToday');

jest.mock("../../src/Api");

const factory = (values = {}) => {
    return shallowMount(updateImagesModal, {
        data () {
            return {
                ...values
            }
        },
        propsData: {
            value: new Product({
                id: "VEGE",
                name:"Lettuce",
                description: "Green and fresh",
                manufacturer: "Pams",
                recommendedRetailPrice: 1.99,
                created: endOfToday(),
                images: [
                    {filename: "/fakeImage1.jpg", id: 1, isPrimary: true},
                    {filename: "/fakeImage2.png", id: 2, isPrimary: false}
                ]
            }),
            businessId: 1,
            location: "Product"
        }
    })
}

describe("Testing the set primary image, delete and upload image functionality", () => {

    test('Testing the component containing the delete button is not "visible" when no image is selected', () => {
        const wrapper = factory({
            selectedImage: null // no image is selected
        });
        expect(wrapper.find('.actionButtons').exists()).toBeFalsy();
    })

    test('Testing the component containing the delete button is "visible" when image is selected', () => {
        const wrapper = factory({
            selectedImage: 1 // id of selected image
        });
        expect(wrapper.find('.actionButtons').exists()).toBeTruthy();
    })

    test('Testing an error message is set when a user without permission to delete an image' +
        'tries to delete an image and the frontend receives a 403 error.', async () => {
        const $router = {
            push: jest.fn()
        };
        const wrapper = await factory({
            mocks: {
                $router
            }
        });

        const data = {
            response: {
                status: 403
            }
        }

        Api.deleteImage.mockImplementation(() => Promise.reject(data));

        await wrapper.vm.deleteSelectedImage();
        await wrapper.vm.$nextTick();

        expect(wrapper.vm.formErrorModalMessage).toBe("Sorry, you do not have permission to perform this action.");

    })

    test('Testing an error message is set when a user tries to delete an image' +
        'and the frontend receives a 406 error.', async () => {
        const $router = {
            push: jest.fn()
        };
        const wrapper = await factory({
            mocks: {
                $router
            }
        });

        const data = {
            response: {
                status: 406
            }
        }

        Api.deleteImage.mockImplementation(() => Promise.reject(data));

        await wrapper.vm.deleteSelectedImage();
        await wrapper.vm.$nextTick();

        expect(wrapper.vm.formErrorModalMessage).toBe("Sorry, something went wrong...");
    })

    test("Testing when a user deletes an image, it is removed from the list of images", async() => {
        const $router = {
            push: jest.fn()
        };
        const wrapper = await factory({
            mocks: {
                $router
            }
        });

        const data = {
            status: 200
        }

        jest.spyOn(document, 'getElementById').mockImplementation(() => {
            return {
                children: []
            };
        });

        Api.deleteImage.mockImplementation(() => Promise.resolve(data));

        wrapper.vm.$data.selectedImage = 2;

        await wrapper.vm.deleteSelectedImage();
        await wrapper.vm.$nextTick();

        expect(wrapper.vm.$data.currentData.data.images.length).toBe(1);
        expect(wrapper.vm.$data.currentData.data.images[0].id).toBe(1);
    });

    test("Testing when a user deletes the primary image, it is removed from the list of images and a new primary image is set", async() => {
        const $router = {
            push: jest.fn()
        };
        const wrapper = await factory({
            mocks: {
                $router
            }
        });

        const data = {
            status: 200
        }

        jest.spyOn(document, 'getElementById').mockImplementation(() => {
            return {
                children: []
            };
        });

        Api.deleteImage.mockImplementation(() => Promise.resolve(data));

        wrapper.vm.$data.selectedImage = 1;

        wrapper.vm.$data.primaryImage = 1;

        await wrapper.vm.deleteSelectedImage();
        await wrapper.vm.$nextTick();

        expect(wrapper.vm.$data.currentData.data.images.length).toBe(1);
        expect(wrapper.vm.$data.currentData.data.images[0].id).toBe(2);
        expect(wrapper.vm.$data.currentData.data.images[0].isPrimary).toBeTruthy();
    });

    test('Testing an error message is sent when a user without permission tries to ' +
        'set a primary image and the frontend receives a 403 error.', async () => {
        const $router = {
            push: jest.fn()
        };
        const wrapper = await factory({
            mocks: {
                $router
            }
        });

        const data = {
            response: {
                status: 403
            }
        }

        jest.spyOn(document, 'getElementById').mockImplementation(() => {
            return {
                children: []
            };
        });

        Api.setPrimaryImage.mockImplementation(() => Promise.reject(data));

        await wrapper.vm.setPrimarySelectedImage();
        await wrapper.vm.$nextTick();

        expect(wrapper.vm.formErrorModalMessage).toBe(
            "Sorry, you do not have permission to perform this action."
        );

    })

    test('Testing an error message is sent when a user tries to ' +
        'set a primary image and the frontend receives a 406 error.', async () => {
        const $router = {
            push: jest.fn()
        };
        const wrapper = await factory({
            mocks: {
                $router
            }
        });

        const data = {
            response: {
                status: 406
            }
        }

        Api.setPrimaryImage.mockImplementation(() => Promise.reject(data));

        await wrapper.vm.setPrimarySelectedImage();
        await wrapper.vm.$nextTick();

        expect(wrapper.vm.formErrorModalMessage).toBe("Sorry, something went wrong...");
    })

    test("Testing when a user sets the primary an image, it is removed from the list of images", async() => {
        const $router = {
            push: jest.fn()
        };
        jest.spyOn(document, 'getElementById').mockImplementation(() => {
            return {};
        });
        const wrapper = await factory({
            mocks: {
                $router
            }
        });

        const data = {
            status: 200
        }

        jest.spyOn(document, 'getElementById').mockImplementation(() => {
            return {
                children: []
            };
        });

        Api.setPrimaryImage.mockImplementation(() => Promise.resolve(data));

        wrapper.vm.$data.selectedImage = 2;
        wrapper.vm.$data.primaryImage = 1;

        await wrapper.vm.setPrimarySelectedImage();
        await wrapper.vm.$nextTick();

        expect(wrapper.vm.primaryImage).toBe(2);
        expect(wrapper.vm.$data.currentData.data.images[0].isPrimary).toBeFalsy();
        expect(wrapper.vm.$data.currentData.data.images[1].isPrimary).toBeTruthy();
    });

    test('Testing an error message is sent when a user tries to upload an invalid file (not an image).',
        async () => {
        const $router = {
            push: jest.fn()
        };
        const wrapper = await factory({
            mocks: {
                $router
            }
        });

        const data = {
            response: {
                status: 400
            }
        }

        Api.uploadImage.mockImplementation(() => Promise.reject(data));

        await wrapper.vm.uploadImage();
        await wrapper.vm.$nextTick();

        expect(wrapper.vm.formErrorModalMessage).toBe(
            "Sorry, the file you uploaded is not a valid image."
        );

    })

    test("Testing when a user uploads an image, it is added to the list of images", async() => {
        const $router = {
            push: jest.fn()
        };
        const wrapper = await factory({
            mocks: {
                $router
            }
        });

        const data = {
            status: 200,
            data: {
                id: 3,
                filename: "fakeFilename3.jpg",
                isPrimary: false,
                thumbnailFilename: "fakeThumbnail.jpg"
            }
        }

        Api.uploadImage.mockImplementation(() => Promise.resolve(data));

        wrapper.vm.$data.primaryImage = 1;

        await wrapper.vm.uploadImage();
        await wrapper.vm.$nextTick();

        expect(wrapper.vm.$data.currentData.data.images.length).toBe(3);
    });

    test("Testing when a user uploads an image when none exist, it is added to the list of images and sets it as the primary image", async() => {
        const wrapper = shallowMount(updateImagesModal, {
            propsData: {
                value: new Product({
                    id: "VEGE",
                    name:"Lettuce",
                    description: "Green and fresh",
                    manufacturer: "Pams",
                    recommendedRetailPrice: 1.99,
                    created: endOfToday(),
                    images: []
                }),
                businessId: 1,
                location: "Product"
            }
        });

        const data = {
            status: 200,
            data: {
                id: 3,
                filename: "fakeFilename3.jpg",
                isPrimary: false,
                thumbnailFilename: "fakeThumbnail.jpg"
            }
        }

        Api.uploadImage.mockImplementation(() => Promise.resolve(data));

        wrapper.vm.$data.primaryImage = null;
        wrapper.vm.$data.currentData.data.images = [];

        await wrapper.vm.uploadImage();
        await wrapper.vm.$nextTick();

        expect(wrapper.vm.$data.currentData.data.images.length).toBe(1);
        expect(wrapper.vm.$data.currentData.data.images[0].id).toBe(3);
        expect(wrapper.vm.$data.currentData.data.images[0].isPrimary).toBeTruthy();
        expect(wrapper.vm.$data.primaryImage).toBe(3);

    });
})

describe("Testing getQueryForParams() function", () => {

    test('Testing when location is User, query will use id as a userId and image type is USER_IMAGE',
        async () => {

        const wrapper = await factory();
        await wrapper.setProps({location : "User", id: 1});

        let query = await wrapper.vm.getQueryForParams();
        expect(query).toEqual("?uncheckedImageType=USER_IMAGE&userId=1")
    })

    test('Testing when location is Business, query will use id as a businessId and image type is BUSINESS_IMAGE',
        async () => {

        const wrapper = await factory();
        await wrapper.setProps({location : "Business", id: 1});

        let query = await wrapper.vm.getQueryForParams();
        expect(query).toEqual("?uncheckedImageType=BUSINESS_IMAGE&businessId=1")
    })

    test('Testing when location is Product, query will use id as a businessId and image type is PRODUCT_IMAGE',
        async () => {

            const wrapper = await factory();
            await wrapper.setProps({location : "Product", id: 1});

            let query = await wrapper.vm.getQueryForParams();
            expect(query).toEqual("?uncheckedImageType=PRODUCT_IMAGE&businessId=1&productId=VEGE")
        })

    test('Testing when location is not one of "Product", "Business", "User", query will return "" and a error will be print in console',
        async () => {

            const wrapper = await factory();
            await wrapper.setProps({location : "", id: 1});
            const consoleSpy = jest.spyOn(console, 'log');

            let query = await wrapper.vm.getQueryForParams();
            expect(query).toEqual("");
            expect(consoleSpy).toHaveBeenCalledWith('Location error!');
     })
})

describe("Testing setSelected() function", () => {

    test('Testing setSelected() sets selected image to null when selectedImage equals id',
        async () => {
            const wrapper = await factory();
            const id = 1;
            wrapper.vm.$data.selectedImage = id;

            wrapper.vm.setSelected(id);
            expect(wrapper.vm.$data.selectedImage).toBeNull();
        })

    test('Testing setSelected() sets selected image to id when selectedImage does not equal id',
        async () => {
            const wrapper = await factory();
            const id = 1;
            wrapper.vm.$data.selectedImage = 2;

            wrapper.vm.setSelected(id);
            expect(wrapper.vm.$data.selectedImage).toEqual(id);
        })
})

