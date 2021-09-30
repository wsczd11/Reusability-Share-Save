/**
 * Jest tests for HomeSales.vue.
 * @jest-environment jsdom
 */

import {test, expect, describe, beforeAll, beforeEach, jest, afterEach} from "@jest/globals"
import Api from "../../src/Api";
import Cookies from "js-cookie"
import {shallowMount} from "@vue/test-utils";
import HomeSales from "../../src/components/saleInsights/HomeSales";

let wrapper;
let $router;
let $route

jest.mock("../../src/Api");
jest.mock("js-cookie");


beforeEach(() => {

    $router = {
        push: jest.fn()
    }

    Date.now = jest.fn(() => new Date(Date.UTC(2021, 1, 14)).valueOf());

    $route = {

    }

    Cookies.get.mockImplementation(() => 4);
    Api.getBusiness.mockImplementation(() => Promise.resolve({data: {
            currencyCode: "asd",
            currencySymbol: "$"
        }}));
    Api.getSalesReport.mockImplementation(() => Promise.resolve({data: []}))
})


describe("Testing the loading component", () => {

    test("Testing that the loading component appears if loading ", async () => {
        wrapper = shallowMount(HomeSales, {
            mocks: {
                $router,
                $route
            }
        })
        await wrapper.setData({loading: true})
        const loading = await wrapper.find("#loading-dots");
        expect(loading.exists()).toBeTruthy();
    })

    test("Testing that the loading component is not visible when not loading", async () => {
        wrapper = shallowMount(HomeSales, {
            mocks: {
                $router,
                $route
            }
        })
        await wrapper.setData({loading: false})
        const loading = await wrapper.find("#loading-dots");
        expect(loading.exists()).toBeFalsy();
    })

})

describe("Testing the hide graph part.", () => {

    test("Testing that the no data to show message appears if not loading and hiding the graph", async () => {
        wrapper = shallowMount(HomeSales, {
            mocks: {
                $router,
                $route
            }
        })
        await wrapper.setData({loading: false})
        await wrapper.setData({hideGraph: true})
        const loading = await wrapper.find("#hide-graph");
        expect(loading.exists()).toBeTruthy();
    })

    test("Testing that the no data to show message does not appear when loading", async () => {
        wrapper = shallowMount(HomeSales, {
            mocks: {
                $router,
                $route
            }
        })
        await wrapper.setData({loading: true})
        await wrapper.setData({hideGraph: false})
        const loading = await wrapper.find("#hide-graph");
        expect(loading.exists()).toBeFalsy();
    })

    test("Testing that the no data to show message does not appear when not hiding the graph", async () => {
        wrapper = shallowMount(HomeSales, {
            mocks: {
                $router,
                $route
            }
        })
        await wrapper.setData({loading: false})
        await wrapper.setData({hideGraph: false})
        const loading = await wrapper.find("#hide-graph");
        expect(loading.exists()).toBeFalsy();
    })
});

describe("Testing the graph", () => {

    beforeEach(async () => {
        Api.getSalesReport.mockImplementation(() => Promise.resolve({data: [
                {totalRevenue: 2000, totalSales: 150}
            ]}))
        wrapper = shallowMount(HomeSales, {
            mocks: {
                $router,
                $route,
                Date
            }
        })

        await wrapper.vm.$nextTick();
        await wrapper.vm.$nextTick();
        await wrapper.vm.$nextTick();
    })

    test("Testing that the total sales is shown on screen", async () => {
        const totalSalesAmount = 150;
        const totalSales = await wrapper.find("#total-sales");
        expect(totalSales.exists()).toBeTruthy();
        expect(totalSales.text()).toStrictEqual(`Total Sales: ${totalSalesAmount}`);
        expect(wrapper.vm.$data.totalSales).toStrictEqual(totalSalesAmount);
    })

    test("Testing that the total revenue appears with the correct currency code and symbol", async () => {
        const totalRevenueAmount = 2000;
        const totalRevenue = await wrapper.find("#total-revenue");
        expect(totalRevenue.exists()).toBeTruthy();
        expect(totalRevenue.text()).toStrictEqual(`Total Revenue: $${totalRevenueAmount} asd`);
        expect(wrapper.vm.$data.totalRevenue).toStrictEqual(totalRevenueAmount);
    })

    test("Testing that the Fulle Sales Report button takes you to the sales page", async () => {
        const button = await wrapper.find("#go-to-sales");
        await button.trigger("click");
        expect($router.push).toHaveBeenCalledWith({path: `businessProfile/4/sales`})
    })

})

// Mock the Date.now()
describe("Testing generate dates", () => {

    afterEach( async () => {
        wrapper = shallowMount(HomeSales, {
            mocks: {
                $router,
                $route,
                Date
            }
        })

        expect(await wrapper.vm.generateDates()).toStrictEqual({
            "fromDate": "2021-10-24T00:00:00.000Z",
            "toDate": "2021-10-30T00:00:00.000Z"
        });
    })

    test("Testing that the generated dates are the current week when today is the middle of the week", async () => {
        Date.now = jest.fn(() => new Date(Date.UTC(2021, 9, 30)).valueOf());
    })

    test("Testing that the generated dates are the current week when today is the start of the week", async () => {
        Date.now = jest.fn(() => new Date(Date.UTC(2021, 9, 27)).valueOf());
    })

    test("Testing that the generated dates are the current week when today is the end of the week", async () => {
        Date.now = jest.fn(() => new Date(Date.UTC(2021, 9, 30)).valueOf());})
})

describe("Testing the build graph function",() => {

    test("Testing that if the user is acting as a user. The hide graph message appears", async () => {
        Cookies.get.mockImplementation(() => {
            return undefined;
        });
        wrapper = shallowMount(HomeSales, {
            mocks: {
                $router,
                $route,
                Date
            }
        })
        expect(Cookies.get("actAs")).toStrictEqual(undefined);
        await wrapper.vm.$nextTick();
        await wrapper.vm.$nextTick();
        await wrapper.vm.$nextTick();
        const hideGraph = await wrapper.find("#hide-graph");
        expect(hideGraph.exists()).toBeTruthy();
    })

    test("Testing that the correct total are calculated and currency is set correctly.", async () => {

    })

    test("Testing that if an error is thrown from getBusiness handleGraphError is called", async () => {})

    test("Testing that if an error is thrown from getSalesReport handleGraphError is called", async () => {})

    test("Testing a 401 from getBusiness", async () => {})

    test("Testing a 403 from getBusiness", async () => {})

    test("Testing a 500 from getBusiness", async () => {})

    test("Testing a timeout from getBusiness", async () => {})

    test("Testing a 401 from getSalesReport", async () => {})

    test("Testing a 403 from getSalesReport", async () => {})

    test("Testing a 500 from getSalesReport", async () => {})

    test("Testing a timeout from getSalesReport", async () => {})

})

describe("Testing the handleGraphError function", () => {

    test("Testing error with timeout", async () => {})

    test("Testing error with 401", async () => {})

    test("Testing error with 403", async () => {})

    test("Testing error with 500", async () => {})

})

describe("Testing isActingAsUser function", () => {

    test("Testing with a number for actAs", async () => {})

    test("Testing with a string for actAs", async () => {})

    test("Testing with a undefined for actAs", async () => {})

    test("Testing with a null for actAs", async () => {})
})

describe("Testing goToSales", () => {

    test("Testing with a number for actAs, that the router.push is called", async () => {})

    test("Testing with a number for actAs, that the router.push is called", async () => {})

    test("Testing with a string for actAs, that the router.push is called", async () => {})

    test("Testing with a undefined for actAs, that the router.push is called", async () => {})

    test("Testing with a null for actAs, that the router.push is called", async () => {})
})