import {mount} from "@vue/test-utils";
import ResetPassword from "../src/views/ResetPassword";
import {beforeEach, describe, expect, jest, test} from "@jest/globals";
import Api from '../src/Api';

let wrapper;
let $route;
let $router;

jest.mock("../src/Api");

beforeEach(() => {
    $router = {
        push: jest.fn()
    };
    $route = {
        query: {
            token: "123token"
        }
    };
    wrapper = mount(ResetPassword, {
        attachTo: document.body,
        mocks: {
            $router,
            $route
        }
    });
})

describe("Testing the password field", () => {

    test("Testing that inputting a value into the input field updates the value", async () => {
        const password = await wrapper.find("#password");
        expect(password.element.value).toStrictEqual("");
        expect(wrapper.vm.$data.password).toStrictEqual("");

        await password.setValue("password");
        await password.trigger("input");
        await wrapper.vm.$nextTick();
        expect(password.element.value).toStrictEqual("password");
        expect(wrapper.vm.$data.password).toStrictEqual("password");
    })

    const toggleShowPassword = async (password) => {
        const showPasswordIcon = wrapper.find("#show-password");

        expect(password.element.type).toStrictEqual("password");

        showPasswordIcon.trigger("click");
        await wrapper.vm.$nextTick();
        expect(password.element.type).toStrictEqual("text");

        showPasswordIcon.trigger("click");
        await wrapper.vm.$nextTick();
        expect(password.element.type).toStrictEqual("password");

    }

    test("Testing that pressing the show password icon changes the input type for password", async () => {
        const password = await wrapper.find("#password");
        await toggleShowPassword(password);
    })

    test("Testing that pressing the show password icon changes the input type confirm password", async () => {
        const password = await wrapper.find("#confirm-password");
        await toggleShowPassword(password);
    })
})

describe("Testing the dynamic criteria", () => {

    const getDynamicCriteria = async (wrapper) => {
        return {
            lowerCase: await wrapper.find("#lower-case-criteria"),
            upperCase: await wrapper.find("#upper-case-criteria"),
            number: await wrapper.find("#number-criteria"),
            symbol: await wrapper.find("#symbol-criteria"),
            length: await wrapper.find("#length-criteria")
        }
    }

    const redClassList = ["small", "text-red"];
    const normalClassList = ["small"]

    const expectCriteriaClassList = async (criteria, classList) => {
        expect(criteria.lowerCase.classes()).toEqual(classList)
        expect(criteria.upperCase.classes()).toEqual(classList)
        expect(criteria.number.classes()).toEqual(classList)
        expect(criteria.symbol.classes()).toEqual(classList)
        expect(criteria.length.classes()).toEqual(classList)
    }

    const setup = async (criteria) => {
        await expectCriteriaClassList(criteria, normalClassList);
        await wrapper.find("#password").trigger("focus")
        await wrapper.vm.$nextTick();
    }

    test("Testing that on focus all fields turn red", async () => {
        const criteria = await getDynamicCriteria(wrapper);
        await setup(criteria);
        await expectCriteriaClassList(criteria, redClassList);
    })

    const expectChangeInput = async (criteria, classList, input) => {
        await setup(criteria);
        await wrapper.find("#password").setValue(input);
        await wrapper.vm.$nextTick();
        expect(classList).toEqual(normalClassList)
    }

    test("Testing that when we input a lower case character the lower case criteria is no longer red", async () => {
        const criteria = await getDynamicCriteria(wrapper);
        await expectChangeInput(criteria, criteria.lowerCase.classes(), "a")
    })

    test("Testing that when we input a upper case character the upper case criteria is no longer red", async () => {
        const criteria = await getDynamicCriteria(wrapper);
        await expectChangeInput(criteria, criteria.upperCase.classes(), "A")
    })

    test("Testing that when we input a number the number criteria is no longer red", async () => {
        const criteria = await getDynamicCriteria(wrapper);
        await expectChangeInput(criteria, criteria.number.classes(), "1")
    })

    test("Testing that when we input a symbol the symbol criteria is no longer red", async () => {
        const criteria = await getDynamicCriteria(wrapper);
        await expectChangeInput(criteria, criteria.symbol.classes(), "@")
    })

    test("Testing that when we input 8 numbers the length criteria is no longer red", async () => {
        const criteria = await getDynamicCriteria(wrapper);
        await expectChangeInput(criteria, criteria.length.classes(), "12345678")
    })

})

describe("Testing the changePassword message", () => {

    test("Testing that the changePassword method updates the resetSuccess variable when a 200 response is received.", async () => {

        const response = {
            status: 200
        }
        Api.resetPassword.mockImplementation(() => Promise.resolve(response))

        wrapper.vm.$data.password = "TestPassword123!"
        wrapper.vm.$data.confirmPassword = "TestPassword123!";

        wrapper.vm.changePassword();
        await wrapper.vm.$nextTick()
        await wrapper.vm.$nextTick()

        expect(wrapper.vm.$data.resetSuccess).toBeTruthy();

    });

    test("Testing that the changePassword method pushes to the timeout page when no response is received.", async () => {

        const data = {
            request: true
        }
        Api.resetPassword.mockImplementation(() => Promise.reject(data))

        wrapper.vm.$data.password = "TestPassword123!"
        wrapper.vm.$data.confirmPassword = "TestPassword123!";

        wrapper.vm.changePassword();
        await wrapper.vm.$nextTick()
        await wrapper.vm.$nextTick()

        expect($router.push).toHaveBeenCalledWith({ path: `/timeout`});

    });

    test("Testing that the changePassword method sets the password error message when a 400 status is received", async () => {

        const data = {
            response: {
                status: 400
            }
        }
        Api.resetPassword.mockImplementation(() => Promise.reject(data))

        wrapper.vm.$data.password = "TestPassword123!"
        wrapper.vm.$data.confirmPassword = "TestPassword123!";

        wrapper.vm.changePassword();
        await wrapper.vm.$nextTick()
        await wrapper.vm.$nextTick()


        expect(wrapper.vm.$data.passwordErrorMsg).toEqual("Invalid password: Please check criteria.")

    });

    test("Testing that the changePassword method sets the invalidToken to true when a 406 status is received", async () => {

        const data = {
            response: {
                status: 406
            }
        }
        Api.resetPassword.mockImplementation(() => Promise.reject(data))

        wrapper.vm.$data.password = "TestPassword123!"
        wrapper.vm.$data.confirmPassword = "TestPassword123!";

        wrapper.vm.changePassword();
        await wrapper.vm.$nextTick()
        await wrapper.vm.$nextTick()


        expect(wrapper.vm.$data.invalidToken).toBeTruthy();

    });

    test("Testing that the changePassword method pushes to a timeout route when receiving a 500 error", async () => {

        const data = {
            response: {
                status: 500
            }
        }
        Api.resetPassword.mockImplementation(() => Promise.reject(data))

        wrapper.vm.$data.password = "TestPassword123!"
        wrapper.vm.$data.confirmPassword = "TestPassword123!";

        wrapper.vm.changePassword();
        await wrapper.vm.$nextTick()
        await wrapper.vm.$nextTick()


        expect($router.push).toHaveBeenCalledWith({ path: `/timeout`});

    });

    test("BackToLogin calls $router.push to the login route.", async () => {

        wrapper.vm.backToLogin();
        await wrapper.vm.$nextTick()
        await wrapper.vm.$nextTick()
        expect($router.push).toHaveBeenCalledWith({name: "Login"});
    })
});
