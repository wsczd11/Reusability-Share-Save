package org.seng302.steps;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.seng302.model.Address;
import org.seng302.model.Business;
import org.seng302.model.repository.BusinessRepository;
import org.seng302.model.enums.BusinessType;
import org.seng302.model.InventoryItem;
import org.seng302.model.repository.InventoryItemRepository;
import org.seng302.controller.InventoryItemResource;
import org.seng302.model.Product;
import org.seng302.model.repository.ProductRepository;
import org.seng302.model.enums.Role;
import org.seng302.model.User;
import org.seng302.model.repository.UserRepository;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.http.Cookie;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

/**
 * Modify inventory item step definitions class
 */
public class ModifyInventoryItemStepDefs {

    private MockMvc mvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private BusinessRepository businessRepository;

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private InventoryItemRepository inventoryItemRepository;

    private User user;

    private Business business;

    private Product product;

    private Product newProduct;

    private InventoryItem inventoryItem;

    private final String inventoryItemPayloadJson = "{" +
            "\"productId\":\"%s\"," +
            "\"quantity\":%d," +
            "\"pricePerItem\":%f," +
            "\"totalPrice\":%f," +
            "\"manufactured\":\"%s\"," +
            "\"sellBy\":\"%s\"," +
            "\"bestBefore\":\"%s\"," +
            "\"expires\":\"%s\"" +
            "}";

    private String payloadJson;

    private MockHttpServletResponse response;

    @Before
    public void createMockMvc() {
        inventoryItemRepository = mock(InventoryItemRepository.class);
        productRepository = mock(ProductRepository.class);
        businessRepository = mock(BusinessRepository.class);
        userRepository = mock(UserRepository.class);
        this.mvc = MockMvcBuilders.standaloneSetup(new InventoryItemResource(inventoryItemRepository, productRepository, businessRepository, userRepository)).build();
    }

    @Given("I am logged in as an administrator of a business.")
    public void i_am_logged_in_as_an_administrator_of_a_business() throws Exception {
        Address address = new Address(
                "3/24",
                "Ilam Road",
                "Christchurch",
                "Canterbury",
                "New Zealand",
                "90210",
                "Ilam"
        );

        user = new User(
                "John",
                "Doe",
                "S",
                "Generic",
                "Biography",
                "email@email.com",
                LocalDate.of(2000, 2, 2),
                "0271316",
                address,
                "Password123!",
                LocalDateTime.of(LocalDate.of(2021, 2, 2),
                        LocalTime.of(0, 0)),
                Role.USER);
        user.setId(1);
        user.setSessionUUID(User.generateSessionUUID());

        business = new Business(
                user.getId(),
                "name",
                "some text",
                address,
                BusinessType.ACCOMMODATION_AND_FOOD_SERVICES,
                LocalDateTime.of(LocalDate.of(2021, 2, 2), LocalTime.of(0, 0)),
                user,
                "$",
                "NZD"
        );
        business.setId(1);
        product = new Product(
                "WATT-420-BEANS",
                business,
                "Beans",
                "Description",
                "Manufacturer",
                20.99,
                ""
        );
        user.setBusinessesAdministeredObjects(List.of(business));
    }

    @Given("I have an inventory item with Product Id {string}, quantity {int}, price per item {string}, total price {string}, manufactured {string}, sell by {string}, best before {string} and expires {string}.")
    public void i_have_an_inventory_item_with_name_product_id_quantity_price_per_item_total_price_manufactured_sell_by_best_before_and_expires(String productId, Integer quantity, String pricePerItem, String totalPrice, String manufactured, String sellBy, String bestBefore, String expires) throws Exception {
        inventoryItem = new InventoryItem(
                product,
                productId,
                quantity,
                Double.valueOf(pricePerItem),
                Double.valueOf(totalPrice),
                LocalDate.parse(manufactured),
                LocalDate.parse(sellBy),
                LocalDate.parse(bestBefore),
                LocalDate.parse(expires)
        );
    }

    @Given("I have an product with Product Id {string}, name {string}, description {string}, manufacturer {string}, recommendPrice {string} and barcode {string}.")
    public void i_have_an_product_with_product_id_name_description_manufacturer_recommend_price_and_created(String productId, String name, String description, String manufacturer, String recommendPrice, String barcode) throws Exception {
        newProduct = new Product(
                productId,
                business,
                name,
                description,
                manufacturer,
                Double.valueOf(recommendPrice),
                barcode
        );
    }


    @Given("New Product Id {string}, new quantity {int}, new price per item {string}, new total price {string}, new manufactured {string}, new sell by {string}, new best before {string} and new expires {string}.")
    public void i_modified_this_inventory_with_new_name_new_product_id_new_quantity_new_price_per_item_new_total_price_new_manufactured_new_sell_by_new_best_before_and_new_expires(String productId, Integer quantity, String pricePerItem, String totalPrice, String manufactured, String sellBy, String bestBefore, String expires) throws Exception {
        InventoryItem newInventoryItem = new InventoryItem(newProduct,
                productId,
                1,
                Double.valueOf(pricePerItem),
                Double.valueOf(totalPrice),
                LocalDate.parse(manufactured),
                LocalDate.parse(sellBy),
                LocalDate.parse(bestBefore),
                LocalDate.parse(expires));
        newInventoryItem.setQuantity(quantity);
        payloadJson = String.format(inventoryItemPayloadJson, newInventoryItem.getProductId(), newInventoryItem.getQuantity(),
                newInventoryItem.getPricePerItem(), newInventoryItem.getTotalPrice(), newInventoryItem.getManufactured(),
                newInventoryItem.getSellBy(), newInventoryItem.getBestBefore(), newInventoryItem.getExpires());
        given(userRepository.findBySessionUUID(user.getSessionUUID())).willReturn(Optional.ofNullable(user));
        given(businessRepository.findBusinessById(business.getId())).willReturn(Optional.ofNullable(business));
        given(productRepository.findProductByIdAndBusinessId(newProduct.getProductId(), business.getId()))
                .willReturn(Optional.ofNullable(newProduct));
        given(inventoryItemRepository.findInventoryItemById(inventoryItem.getId()))
                .willReturn(Optional.ofNullable(inventoryItem));
        given(inventoryItemRepository.save(any(InventoryItem.class))).willReturn(newInventoryItem);
    }

    @Given("New Product Id {string}, new quantity {int}, new price per item {string}, new total price {string}, new manufactured {string}, new sell by {string}, new best before {string} and no expires.")
    public void i_modified_this_inventory_with_new_name_new_product_id_new_quantity_new_price_per_item_new_total_price_new_manufactured_new_sell_by_new_best_before_and_no_expires(String productId, Integer quantity, String pricePerItem, String totalPrice, String manufactured, String sellBy, String bestBefore) throws Exception {
        InventoryItem newInventoryItem = new InventoryItem(newProduct,
                productId,
                quantity,
                Double.valueOf(pricePerItem),
                Double.valueOf(totalPrice),
                LocalDate.parse(manufactured),
                LocalDate.parse(sellBy),
                LocalDate.parse(bestBefore),
                LocalDate.parse(bestBefore));
        newInventoryItem.setExpires(null);
        payloadJson = String.format(inventoryItemPayloadJson, newInventoryItem.getProductId(), newInventoryItem.getQuantity(),
                newInventoryItem.getPricePerItem(), newInventoryItem.getTotalPrice(), newInventoryItem.getManufactured(),
                newInventoryItem.getSellBy(), newInventoryItem.getBestBefore(), newInventoryItem.getExpires());
        given(userRepository.findBySessionUUID(user.getSessionUUID())).willReturn(Optional.ofNullable(user));
        given(businessRepository.findBusinessById(business.getId())).willReturn(Optional.ofNullable(business));
        given(productRepository.findProductByIdAndBusinessId(newProduct.getProductId(), business.getId()))
                .willReturn(Optional.ofNullable(newProduct));
        given(inventoryItemRepository.findInventoryItemById(inventoryItem.getId()))
                .willReturn(Optional.ofNullable(inventoryItem));
        given(inventoryItemRepository.save(any(InventoryItem.class))).willReturn(newInventoryItem);
    }

    @When("I modified the inventory.")
    public void i_modified_the_inventory() throws Exception {
        response = mvc.perform(put(String.format("/businesses/%d/inventory/%d", business.getId(), inventoryItem.getId()))
                .contentType(MediaType.APPLICATION_JSON).content(payloadJson)
                .cookie(new Cookie("JSESSIONID", user.getSessionUUID())))
                .andReturn().getResponse();
    }

    @Then("The inventory is successfully modified.")
    public void the_inventory_is_successfully_modified() {
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Then("The inventory is fail to modified.")
    public void the_inventory_is_fail_to_modified() {
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
