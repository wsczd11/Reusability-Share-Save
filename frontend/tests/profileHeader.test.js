/**
 * Jest tests for ProfileHeader.vue.
 * @jest-environment jsdom
 */

import {describe, expect, jest, test} from "@jest/globals";
import {createLocalVue, shallowMount} from "@vue/test-utils";
import ProfileHeader from "../src/components/ProfileHeader";
import VueRouter from 'vue-router';
import Search from "../src/views/Search";

describe("Testing the search type functionality", () => {

    describe("Testing the changeSearchType method", () => {

        test('Testing changeSearchType sets the search type to the input', () => {
            const $route = { query: 'User'};
            const profileHeaderWrapper = shallowMount(ProfileHeader, {
                mocks: {
                    $route
                }
            });
            profileHeaderWrapper.vm.searchType = 'User';

            profileHeaderWrapper.vm.changeSearchType('Business');
            expect(profileHeaderWrapper.vm.searchType).toEqual('Business');

            profileHeaderWrapper.vm.changeSearchType('User');
            expect(profileHeaderWrapper.vm.searchType).toEqual('User');
        });

        test('Testing changeSearchType is called with User when the User radio button is clicked', () => {
            const $route = { query: 'User'};
            const profileHeaderWrapper = shallowMount(ProfileHeader, {
                mocks: {
                    $route
                }
            });
            profileHeaderWrapper.vm.searchType = 'Business';
            expect(profileHeaderWrapper.vm.searchType).toEqual('Business');

            let radioButton = profileHeaderWrapper.find('#user-radio-button');
            radioButton.trigger('click');

            expect(profileHeaderWrapper.vm.searchType).toEqual('User');
        });

        test('Testing changeSearchType is called with Business when the Business radio button is clicked', () => {
            const $route = { query: 'User'};
            const profileHeaderWrapper = shallowMount(ProfileHeader, {
                mocks: {
                    $route
                }
            });
            profileHeaderWrapper.vm.searchType = 'User';
            expect(profileHeaderWrapper.vm.searchType).toEqual('User');

            let radioButton = profileHeaderWrapper.find('#business-radio-button');
            radioButton.trigger('click');

            expect(profileHeaderWrapper.vm.searchType).toEqual('Business');
        });

    });

    describe("Testing the placeholder value setting", () => {

        test('Testing placeholder returns the correct value when the search type is User', () => {
            const $route = { query: 'User'};
            const profileHeaderWrapper = shallowMount(ProfileHeader, {
                mocks: {
                    $route
                }
            });
            profileHeaderWrapper.vm.searchType = 'User';
            expect(profileHeaderWrapper.vm.placeholder).toEqual('Search all users');
        });

        test('Testing placeholder returns the correct value when the search type is Business', () => {
            const $route = { query: 'User'};
            const profileHeaderWrapper = shallowMount(ProfileHeader, {
                mocks: {
                    $route
                }
            });
            profileHeaderWrapper.vm.searchType = 'Business';
            expect(profileHeaderWrapper.vm.placeholder).toEqual('Search all businesses');
        });

    });

    describe("Testing the business type dropdown", () => {

        test('Testing business type dropdown is not visible when the search type is User', () => {
            const $route = { query: 'User'};
            const profileHeaderWrapper = shallowMount(ProfileHeader, {
                mocks: {
                    $route
                }
            });
            profileHeaderWrapper.vm.searchType = 'User';

            profileHeaderWrapper.vm.$nextTick().then(() => {
                expect(profileHeaderWrapper.find('#business-type-combo-box-container').exists()).toBe(false);
            });
        });

        test('Testing business type dropdown is visible when the search type is Business', () => {
            const $route = { query: 'User'};
            const profileHeaderWrapper = shallowMount(ProfileHeader, {
                mocks: {
                    $route
                }
            });
            profileHeaderWrapper.vm.searchType = 'Business';

            profileHeaderWrapper.vm.$nextTick().then(() => {
                expect(profileHeaderWrapper.find('#business-type-combo-box').exists()).toBe(true);
            });
        });

        test('Testing that when Any is selected in the dropdown yet, selectedBusinessType is not populated', () => {
            const $route = { query: 'User'};
            const profileHeaderWrapper = shallowMount(ProfileHeader, {
                mocks: {
                    $route
                }
            });
            profileHeaderWrapper.vm.searchType = 'Business';

            profileHeaderWrapper.vm.$nextTick().then(() => {
                const dropdownOption = profileHeaderWrapper.find("#default-option");
                dropdownOption.setSelected();

                expect(profileHeaderWrapper.vm.selectedBusinessType).toEqual('Any');
            });
        });

        test('Testing that when Accommodation and Food Services is selected in the dropdown, selectedBusinessType is set to it', () => {
            const $route = { query: 'User'};
            const profileHeaderWrapper = shallowMount(ProfileHeader, {
                mocks: {
                    $route
                }
            });
            profileHeaderWrapper.vm.searchType = 'Business';

            profileHeaderWrapper.vm.$nextTick().then(() => {
                const dropdownOption = profileHeaderWrapper.find("#accommodation-and-food-services");
                dropdownOption.setSelected();

                expect(profileHeaderWrapper.vm.selectedBusinessType).toEqual('Accommodation and Food Services');
            });
        });

        test('Testing that when Retail Trade is selected in the dropdown, selectedBusinessType is set to it', () => {
            const $route = { query: 'User'};
            const profileHeaderWrapper = shallowMount(ProfileHeader, {
                mocks: {
                    $route
                }
            });
            profileHeaderWrapper.vm.searchType = 'Business';

            profileHeaderWrapper.vm.$nextTick().then(() => {
                const dropdownOption = profileHeaderWrapper.find("#retail-trade");
                dropdownOption.setSelected();

                expect(profileHeaderWrapper.vm.selectedBusinessType).toEqual('Retail Trade');
            });
        });

        test('Testing that when Charitable Organisation is selected in the dropdown, selectedBusinessType is set to it', () => {
            const $route = { query: 'User'};
            const profileHeaderWrapper = shallowMount(ProfileHeader, {
                mocks: {
                    $route
                }
            });
            profileHeaderWrapper.vm.searchType = 'Business';

            profileHeaderWrapper.vm.$nextTick().then(() => {
                const dropdownOption = profileHeaderWrapper.find("#charitable-organisation");
                dropdownOption.setSelected();

                expect(profileHeaderWrapper.vm.selectedBusinessType).toEqual('Charitable Organisation');
            });
        });

        test('Testing that when Non Profit Organisation is selected in the dropdown, selectedBusinessType is set to it', () => {
            const $route = { query: 'User'};
            const profileHeaderWrapper = shallowMount(ProfileHeader, {
                mocks: {
                    $route
                }
            });
            profileHeaderWrapper.vm.searchType = 'Business';

            profileHeaderWrapper.vm.$nextTick().then(() => {
                const dropdownOption = profileHeaderWrapper.find("#non-profit-organisation");
                dropdownOption.setSelected();

                expect(profileHeaderWrapper.vm.selectedBusinessType).toEqual('Non Profit Organisation');
            });
        });

    });

    describe("Testing the URL populates correctly when searching for users", () => {

        let profileHeaderWrapper;
        let router;

        beforeAll(() => {
            const localVue = createLocalVue();
            localVue.use(VueRouter)

            const routes = [{path: '/profile/:id', component: ProfileHeader, name: 'Profile'},{path: '/search', component: Search, name: 'Search'}]
            router = new VueRouter({
                routes
            })
            router.push({
                name: 'Profile',
                params: {id: '1'}
            })
            profileHeaderWrapper = shallowMount(ProfileHeader, {
                localVue,
                router
            });
        });

        test('Testing that pressing enter when the search type is User populates the URL correctly', () => {
            profileHeaderWrapper.vm.searchType = 'User';

            let inputQuery = 'User Search Enter Test';
            let expectedQuery = 'User%20Search%20Enter%20Test';
            profileHeaderWrapper.vm.$refs.searchInput.value = inputQuery;

            profileHeaderWrapper.vm.$nextTick().then(() => {
                let searchBar = profileHeaderWrapper.find('#search-bar');
                searchBar.trigger('keydown.enter');

                expect(router.currentRoute.name).toBe('Search')
                expect(router.currentRoute.fullPath).toBe(`/search?type=User&searchQuery=${expectedQuery}&orderBy=fullNameASC&page=1&pageSize=5`)
            });
        });


        test('Testing that clicking the search button when the search type is User populates the URL correctly', () => {

            profileHeaderWrapper.vm.searchType = 'User';

            let inputQuery = 'User Search Click Test';
            let expectedQuery = 'User%20Search%20Click%20Test';
            profileHeaderWrapper.vm.$refs.searchInput.value = inputQuery;

            profileHeaderWrapper.vm.$nextTick().then(() => {
                let searchButton = profileHeaderWrapper.find('#search-button');
                searchButton.trigger('click');

                expect(router.currentRoute.name).toBe('Search')
                expect(router.currentRoute.fullPath).toBe(`/search?type=User&searchQuery=${expectedQuery}&orderBy=fullNameASC&page=1&pageSize=5`)
            });
        });

    });

    describe("Testing the URL populates correctly when searching for businesses", () => {

        let profileHeaderWrapper;
        let router;

        beforeAll(() => {
            const localVue = createLocalVue();
            localVue.use(VueRouter)

            const routes = [{path: '/profile/:id', component: ProfileHeader, name: 'Profile'},{path: '/search', component: Search, name: 'Search'}]
            router = new VueRouter({
                routes
            })
            router.push({
                name: 'Profile',
                params: {id: '1'}
            })
            profileHeaderWrapper = shallowMount(ProfileHeader, {
                localVue,
                router
            });
        });

        describe("Testing URL population when business type is default", () => {

            test('Testing that pressing enter when the search type is Business populates the URL correctly', () => {

                profileHeaderWrapper.vm.searchType = 'Business';

                let inputQuery = 'Business Search Enter Test';
                let expectedQuery = 'Business%20Search%20Enter%20Test';
                profileHeaderWrapper.vm.$refs.searchInput.value = inputQuery;

                profileHeaderWrapper.vm.$nextTick().then(() => {
                    let searchBar = profileHeaderWrapper.find('#search-bar');
                    searchBar.trigger('keydown.enter');

                    let businessType = "Any";

                    expect(router.currentRoute.name).toBe('Search')
                    expect(router.currentRoute.fullPath).toBe(`/search?type=Business&searchQuery=${expectedQuery}&businessType=${businessType}&orderBy=nameASC&page=1&pageSize=5`);
                });
            });

            test('Testing that clicking the search button when the search type is Business populates the URL correctly', () => {

                profileHeaderWrapper.vm.searchType = 'Business';

                let inputQuery = 'Business Search Click Test';
                let expectedQuery = 'Business%20Search%20Click%20Test';
                profileHeaderWrapper.vm.$refs.searchInput.value = inputQuery;

                profileHeaderWrapper.vm.$nextTick().then(() => {
                    let searchButton = profileHeaderWrapper.find('#search-button');
                    searchButton.trigger('click');

                    let businessType = "Any";

                    expect(router.currentRoute.name).toBe('Search')
                    expect(router.currentRoute.fullPath).toBe(`/search?type=Business&searchQuery=${expectedQuery}&businessType=${businessType}&orderBy=nameASC&page=1&pageSize=5`);
                });
            });
        });

        describe("Testing URL population when business type is Accommodation and Food Services", () => {

            test('Testing that pressing enter when the search type is Business populates the URL correctly', () => {

                profileHeaderWrapper.vm.searchType = 'Business';

                let inputQuery = 'Business Search Enter Test';
                let expectedQuery = 'Business%20Search%20Enter%20Test';
                profileHeaderWrapper.vm.$refs.searchInput.value = inputQuery;

                profileHeaderWrapper.vm.$nextTick().then(() => {
                    let dropdownOption = profileHeaderWrapper.find("#accommodation-and-food-services");
                    dropdownOption.setSelected();

                    let searchBar = profileHeaderWrapper.find('#search-bar');
                    searchBar.trigger('keydown.enter');

                    let businessType = "Accommodation%20and%20Food%20Services";

                    expect(router.currentRoute.name).toBe('Search')
                    expect(router.currentRoute.fullPath).toBe(`/search?type=Business&searchQuery=${expectedQuery}&businessType=${businessType}&orderBy=nameASC&page=1&pageSize=5`);
                });
            });

            test('Testing that clicking the search button when the search type is Business populates the URL correctly', () => {

                profileHeaderWrapper.vm.searchType = 'Business';

                let inputQuery = 'Business Search Click Test';
                let expectedQuery = 'Business%20Search%20Click%20Test';
                profileHeaderWrapper.vm.$refs.searchInput.value = inputQuery;

                profileHeaderWrapper.vm.$nextTick().then(() => {
                    let dropdownOption = profileHeaderWrapper.find("#accommodation-and-food-services");
                    dropdownOption.setSelected();

                    let searchButton = profileHeaderWrapper.find('#search-button');
                    searchButton.trigger('click');

                    let businessType = "Accommodation%20and%20Food%20Services";

                    expect(router.currentRoute.name).toBe('Search')
                    expect(router.currentRoute.fullPath).toBe(`/search?type=Business&searchQuery=${expectedQuery}&businessType=${businessType}&orderBy=nameASC&page=1&pageSize=5`);
                });
            });
        });

        describe("Testing URL population when business type is Retail Trade", () => {

            test('Testing that pressing enter when the search type is Business populates the URL correctly', () => {

                profileHeaderWrapper.vm.searchType = 'Business';

                let inputQuery = 'Business Search Enter Test';
                let expectedQuery = 'Business%20Search%20Enter%20Test';
                profileHeaderWrapper.vm.$refs.searchInput.value = inputQuery;

                profileHeaderWrapper.vm.$nextTick().then(() => {
                    let dropdownOption = profileHeaderWrapper.find("#retail-trade");
                    dropdownOption.setSelected();

                    let searchBar = profileHeaderWrapper.find('#search-bar');
                    searchBar.trigger('keydown.enter');

                    let businessType = "Retail%20Trade";

                    expect(router.currentRoute.name).toBe('Search')
                    expect(router.currentRoute.fullPath).toBe(`/search?type=Business&searchQuery=${expectedQuery}&businessType=${businessType}&orderBy=nameASC&page=1&pageSize=5`);
                });
            });

            test('Testing that clicking the search button when the search type is Business populates the URL correctly', () => {

                profileHeaderWrapper.vm.searchType = 'Business';

                let inputQuery = 'Business Search Click Test';
                let expectedQuery = 'Business%20Search%20Click%20Test';
                profileHeaderWrapper.vm.$refs.searchInput.value = inputQuery;

                profileHeaderWrapper.vm.$nextTick().then(() => {
                    let dropdownOption = profileHeaderWrapper.find("#retail-trade");
                    dropdownOption.setSelected();

                    let searchButton = profileHeaderWrapper.find('#search-button');
                    searchButton.trigger('click');

                    let businessType = "Retail%20Trade";

                    expect(router.currentRoute.name).toBe('Search')
                    expect(router.currentRoute.fullPath).toBe(`/search?type=Business&searchQuery=${expectedQuery}&businessType=${businessType}&orderBy=nameASC&page=1&pageSize=5`);
                });
            });
        });

        describe("Testing URL population when business type is Charitable Organisation", () => {

            test('Testing that pressing enter when the search type is Business populates the URL correctly', () => {

                profileHeaderWrapper.vm.searchType = 'Business';

                let inputQuery = 'Business Search Enter Test';
                let expectedQuery = 'Business%20Search%20Enter%20Test';
                profileHeaderWrapper.vm.$refs.searchInput.value = inputQuery;

                profileHeaderWrapper.vm.$nextTick().then(() => {
                    let dropdownOption = profileHeaderWrapper.find("#charitable-organisation");
                    dropdownOption.setSelected();

                    let searchBar = profileHeaderWrapper.find('#search-bar');
                    searchBar.trigger('keydown.enter');

                    let businessType = "Charitable%20Organisation";

                    expect(router.currentRoute.name).toBe('Search')
                    expect(router.currentRoute.fullPath).toBe(`/search?type=Business&searchQuery=${expectedQuery}&businessType=${businessType}&orderBy=nameASC&page=1&pageSize=5`);
                });
            });

            test('Testing that clicking the search button when the search type is Business populates the URL correctly', () => {

                profileHeaderWrapper.vm.searchType = 'Business';

                let inputQuery = 'Business Search Click Test';
                let expectedQuery = 'Business%20Search%20Click%20Test';
                profileHeaderWrapper.vm.$refs.searchInput.value = inputQuery;

                profileHeaderWrapper.vm.$nextTick().then(() => {
                    let dropdownOption = profileHeaderWrapper.find("#charitable-organisation");
                    dropdownOption.setSelected();

                    let searchButton = profileHeaderWrapper.find('#search-button');
                    searchButton.trigger('click');

                    let businessType = "Charitable%20Organisation";

                    expect(router.currentRoute.name).toBe('Search')
                    expect(router.currentRoute.fullPath).toBe(`/search?type=Business&searchQuery=${expectedQuery}&businessType=${businessType}&orderBy=nameASC&page=1&pageSize=5`);
                });
            });
        });

        describe("Testing URL population when business type is Non Profit Organisation", () => {

            test('Testing that pressing enter when the search type is Business populates the URL correctly', () => {

                profileHeaderWrapper.vm.searchType = 'Business';

                let inputQuery = 'Business Search Enter Test';
                let expectedQuery = 'Business%20Search%20Enter%20Test';
                profileHeaderWrapper.vm.$refs.searchInput.value = inputQuery;

                profileHeaderWrapper.vm.$nextTick().then(() => {
                    let dropdownOption = profileHeaderWrapper.find("#non-profit-organisation");
                    dropdownOption.setSelected();

                    let searchBar = profileHeaderWrapper.find('#search-bar');
                    searchBar.trigger('keydown.enter');

                    let businessType = "Non%20Profit%20Organisation";

                    expect(router.currentRoute.name).toBe('Search')
                    expect(router.currentRoute.fullPath).toBe(`/search?type=Business&searchQuery=${expectedQuery}&businessType=${businessType}&orderBy=nameASC&page=1&pageSize=5`);
                });
            });

            test('Testing that clicking the search button when the search type is Business populates the URL correctly', () => {

                profileHeaderWrapper.vm.searchType = 'Business';

                let inputQuery = 'Business Search Click Test';
                let expectedQuery = 'Business%20Search%20Click%20Test';
                profileHeaderWrapper.vm.$refs.searchInput.value = inputQuery;

                profileHeaderWrapper.vm.$nextTick().then(() => {
                    let dropdownOption = profileHeaderWrapper.find("#non-profit-organisation");
                    dropdownOption.setSelected();

                    let searchButton = profileHeaderWrapper.find('#search-button');
                    searchButton.trigger('click');

                    let businessType = "Non%20Profit%20Organisation";

                    expect(router.currentRoute.name).toBe('Search')
                    expect(router.currentRoute.fullPath).toBe(`/search?type=Business&searchQuery=${expectedQuery}&businessType=${businessType}&orderBy=nameASC&page=1&pageSize=5`);
                });
            });
        });
    });

    describe("Search triggering edge cases", () => {

        let profileHeaderWrapper;
        let router;

        beforeAll(() => {
            const localVue = createLocalVue();
            localVue.use(VueRouter)

            const routes = [{path: '/profile/:id', component: ProfileHeader, name: 'Profile'},{path: '/search', component: Search, name: 'Search'}]
            router = new VueRouter({
                routes
            })
            router.push({
                name: 'Profile',
                params: {id: '1'}
            })
            profileHeaderWrapper = shallowMount(ProfileHeader, {
                localVue,
                router
            });
        });

        test('Testing that clicking the search button when the search type is not Business or User does not trigger a router push', () => {

            profileHeaderWrapper.vm.searchType = 'Other';

            profileHeaderWrapper.vm.$nextTick().then(() => {
                let searchButton = profileHeaderWrapper.find('#search-button');
                searchButton.trigger('click');

                expect(router.currentRoute.name).toBe('Profile')
            });
        });

        test('Testing that pressing enter when the search type is not Business or User does not trigger a router push', () => {

            profileHeaderWrapper.vm.searchType = 'Other';

            profileHeaderWrapper.vm.$nextTick().then(() => {
                let searchBar = profileHeaderWrapper.find('#search-bar');
                searchBar.trigger('keydown.enter');

                expect(router.currentRoute.name).toBe('Profile')
            });
        });

        test('Testing that pressing a key other than enter does not trigger a router push', () => {

            profileHeaderWrapper.vm.searchType = 'User';

            profileHeaderWrapper.vm.$nextTick().then(() => {
                let searchBar = profileHeaderWrapper.find('#search-bar');
                searchBar.trigger('keydown.escape');

                expect(router.currentRoute.name).toBe('Profile')
            });
        });
    });

    describe("Search using enter triggering current route and target route comparison", () => {

        let profileHeaderWrapper;
        let $router;
        let $route;

        beforeAll(() => {
            $router = {
                push: jest.fn()
            };
            $route = {
                name: "Search",
                query: {
                    type: 'Business',
                    searchQuery: 'Hello World',
                    businessType: 'Retail Trade'
                }
            };
            profileHeaderWrapper = shallowMount(ProfileHeader, {
                mocks: {
                    $router,
                    $route
                }
            });
        });

        test('Testing pressing enter when searchType is Business, route businessType equals selectedBusinessType' +
            'and current route equals target route then an event is not emitted and router.push is not called.', () => {
            profileHeaderWrapper.vm.searchType = 'Business';
            profileHeaderWrapper.vm.selectedBusinessType = 'Retail Trade';

            profileHeaderWrapper.vm.$refs.searchInput.value = 'Hello World';

            profileHeaderWrapper.vm.$nextTick().then(() => {
                let searchBar = profileHeaderWrapper.find('#search-bar');
                searchBar.trigger('keydown.enter');

                expect(profileHeaderWrapper.emitted().requestUsers).toBeFalsy();
                expect(profileHeaderWrapper.emitted().requestBusinesses).toBeFalsy();
                expect($router.push).toHaveBeenCalledTimes(0); // should have not been called
            });
        });

        test('Testing pressing enter when searchType is User and current route equals target route then an event is not emitted and router.push is not called.', () => {
            profileHeaderWrapper.vm.searchType = 'User';
            profileHeaderWrapper.vm.$route.query.type = 'User';

            profileHeaderWrapper.vm.$refs.searchInput.value = 'Hello World';

            profileHeaderWrapper.vm.$nextTick().then(() => {
                let searchBar = profileHeaderWrapper.find('#search-bar');
                searchBar.trigger('keydown.enter');

                expect(profileHeaderWrapper.emitted().requestUsers).toBeFalsy();
                expect(profileHeaderWrapper.emitted().requestBusinesses).toBeFalsy();
                expect($router.push).toHaveBeenCalledTimes(0); // should have not been called
            });
        });
    });

    describe("Search using enter triggering current route and target route comparison", () => {

        let profileHeaderWrapper;
        let $router;
        let $route;

        beforeAll(() => {
            $router = {
                push: jest.fn()
            };
            $route = {
                name: "Search",
                query: {
                    type: 'Business',
                    searchQuery: 'Hello World',
                    businessType: 'Retail Trade',
                    pageSize: '5'
                }
            };
            profileHeaderWrapper = shallowMount(ProfileHeader, {
                mocks: {
                    $router,
                    $route
                }
            });
        });

        test('Testing clicking the search button when searchType is Business, route businessType equals selectedBusinessType' +
            'and current route equals target route then an event is not emitted and router.push is not called.', () => {
            profileHeaderWrapper.vm.searchType = 'Business';
            profileHeaderWrapper.vm.selectedBusinessType = 'Retail Trade';

            profileHeaderWrapper.vm.$refs.searchInput.value = 'Hello World';

            profileHeaderWrapper.vm.$nextTick().then(() => {
                let searchButton = profileHeaderWrapper.find('#search-button');
                searchButton.trigger('click');

                expect(profileHeaderWrapper.emitted().requestUsers).toBeFalsy();
                expect(profileHeaderWrapper.emitted().requestBusinesses).toBeFalsy();
                expect($router.push).toHaveBeenCalledTimes(0); // should have not been called
            });
        });

        test('Testing clicking the search button when searchType is User and current route equals target route then an event is not emitted and router.push is not called.', () => {
            profileHeaderWrapper.vm.searchType = 'User';
            profileHeaderWrapper.vm.$route.query.type = 'User';

            profileHeaderWrapper.vm.$refs.searchInput.value = 'Hello World';

            profileHeaderWrapper.vm.$nextTick().then(() => {
                let searchButton = profileHeaderWrapper.find('#search-button');
                searchButton.trigger('click');

                expect(profileHeaderWrapper.emitted().requestUsers).toBeFalsy();
                expect(profileHeaderWrapper.emitted().requestBusinesses).toBeFalsy();
                expect($router.push).toHaveBeenCalledTimes(0); // should have not been called
            });
        });
    });

});