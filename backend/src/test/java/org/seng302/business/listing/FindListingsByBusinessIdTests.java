package org.seng302.business.listing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.seng302.address.Address;
import org.seng302.business.Business;
import org.seng302.business.BusinessType;
import org.seng302.business.inventoryItem.InventoryItem;
import org.seng302.business.product.Product;
import org.seng302.main.Main;
import org.seng302.user.Role;
import org.seng302.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * FindInventoryItemsByBusinessId test class - specifically for testing the pagination and ordering of the
 * findInventoryItemsByBusinessId method in InventoryItemsRepository.
 */
@DataJpaTest
@ContextConfiguration(classes = {Main.class})
@ActiveProfiles("test")
public class FindListingsByBusinessIdTests {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ListingRepository listingRepository;

    private Address address;

    private User user;

    private Business business;
    private Integer businessId;

    private Product product1;
    private Product product2;
    private Product product3;
    private Product product4;
    private Product product5;
    private List<Product> products;

    private InventoryItem inventoryItem1;
    private InventoryItem inventoryItem2;
    private InventoryItem inventoryItem3;
    private InventoryItem inventoryItem4;
    private InventoryItem inventoryItem5;
    private List<InventoryItem> inventoryItems;

    private Listing listing1;
    private Listing listing2;
    private Listing listing3;
    private Listing listing4;
    private Listing listing5;
    private List<Listing> listings;

    /**
     * Creates and inserts all entities needed for testing.
     * Any exception.
     */
    @BeforeEach
    public void setup() throws Exception {
        // given
        address = new Address(
                "3/24",
                "Ilam Road",
                "Christchurch",
                "Canterbury",
                "New Zealand",
                "90210"
        );
        entityManager.persist(address);
        entityManager.flush();
        user = new User(
                "first",
                "last",
                "middle",
                "nick",
                "bio",
                "test@example.com",
                LocalDate.of(2021, Month.JANUARY, 1).minusYears(13),
                "123456789",
                address,
                "Password123!",
                LocalDateTime.of(LocalDate.of(2021, Month.JANUARY, 1), LocalTime.of(0, 0)),
                Role.USER
        );
        user = entityManager.persist(user);
        entityManager.flush();
        business = new Business(
                user.getId(),
                "example name",
                "some text",
                address,
                BusinessType.RETAIL_TRADE,
                LocalDateTime.now(),
                user
        );
        business = entityManager.persist(business);
        businessId = business.getId();
        entityManager.flush();

        product1 = new Product(
                "APPLE",
                business,
                "Apple",
                "A Description",
                "Manufacturer",
                21.00,
                LocalDateTime.of(LocalDate.of(2021, 1, 1),
                        LocalTime.of(0, 0))
        );
        product2 = new Product(
                "APP-LE",
                business,
                "Beans",
                "Description",
                "A Manufacturer",
                20.00,
                LocalDateTime.of(LocalDate.of(2020, 1, 1),
                        LocalTime.of(0, 0))
        );
        product3 = new Product(
                "APP-LE3",
                business,
                "Beans",
                "Description",
                "A Manufacturer",
                11.00,
                LocalDateTime.of(LocalDate.of(2021, 1, 1),
                        LocalTime.of(0, 0))
        );
        product4 = new Product(
                "DUCT",
                business,
                "Duct-Tape",
                "Brand new Description",
                "A New Manufacturer",
                10.00,
                LocalDateTime.of(LocalDate.of(2021, 2, 1),
                        LocalTime.of(0, 0))
        );
        product5 = new Product(
                "PROD",
                business,
                "Product",
                "New Description",
                "New Manufacturer",
                10.00,
                LocalDateTime.of(LocalDate.of(2021, 2, 1),
                        LocalTime.of(1, 0))
        );

        products = List.of(product1, product2, product3, product4, product5);
        for (Product product : products) {
            entityManager.persist(product);
        }
        entityManager.flush();

        inventoryItem1 = new InventoryItem(product1,
                "APPLE",
                45,
                6.5,
                21.99,
                LocalDate.of(2020, 04, 25),
                LocalDate.of(2021, 04, 25),
                LocalDate.of(2021, 04, 25),
                LocalDate.of(2021, 12, 25));

        inventoryItem2 = new InventoryItem(product2,
                "APP-LE",
                6,
                17.44,
                27.00,
                LocalDate.of(2021, 04, 25),
                LocalDate.of(2022, 04, 25),
                LocalDate.of(2022, 04, 25),
                LocalDate.of(2022, 12, 25));

        inventoryItem3 = new InventoryItem(product3,
                "APP-LE3",
                40,
                4.99,
                32.23,
                LocalDate.of(2021, 04, 25),
                LocalDate.of(2023, 04, 25),
                LocalDate.of(2023, 04, 25),
                LocalDate.of(2024, 12, 25));

        inventoryItem4 = new InventoryItem(product4,
                "DUCT",
                13,
                6.5,
                29.99,
                LocalDate.of(2021, 04, 25),
                LocalDate.of(2023, 04, 25),
                LocalDate.of(2023, 04, 25),
                LocalDate.of(2023, 12, 25));

        inventoryItem5 = new InventoryItem(product5,
                "PROD",
                40,
                6.5,
                21.99,
                LocalDate.of(2020, 05, 25),
                LocalDate.of(2022, 04, 25),
                LocalDate.of(2023, 04, 25),
                LocalDate.of(2023, 12, 25));

        inventoryItems = List.of(inventoryItem1, inventoryItem2, inventoryItem3, inventoryItem4, inventoryItem5);
        for (InventoryItem inventoryItem : inventoryItems) {
            entityManager.persist(inventoryItem);
        }

        entityManager.flush();
        listing1 = new Listing(inventoryItem1,
                5,
                12.0,
                "",
                LocalDateTime.of(LocalDate.of(2020, 8, 1), LocalTime.of(0,0,0)),
                LocalDateTime.of(LocalDate.of(2022, 10, 1), LocalTime.of(0,0,0)));

        listing2 = new Listing(inventoryItem2,
                4,
                1.2,
                "",
                LocalDateTime.of(LocalDate.of(2020, 10, 1), LocalTime.of(0,0,0)),
                LocalDateTime.of(LocalDate.of(2022, 4, 1), LocalTime.of(0,0,0)));

        listing3 = new Listing(inventoryItem3,
                3,
                1.50,
                "",
                LocalDateTime.of(LocalDate.of(2021, 3, 1), LocalTime.of(0,0,0)),
                LocalDateTime.of(LocalDate.of(2022, 3, 1), LocalTime.of(0,0,0)));

        listing4 = new Listing(inventoryItem4,
                2,
                11.20,
                "",
                LocalDateTime.of(LocalDate.of(2021, 1, 1), LocalTime.of(0,0,0)),
                LocalDateTime.of(LocalDate.of(2021, 12, 1), LocalTime.of(0,0,0)));

        listing5 = new Listing(inventoryItem5,
                1,
                15.20,
                "",
                LocalDateTime.of(LocalDate.of(2020, 1, 1), LocalTime.of(0,0,0)),
                LocalDateTime.of(LocalDate.of(2023, 10, 1), LocalTime.of(0,0,0)));

        listings = List.of(listing1, listing2, listing3, listing4, listing5);
        for (Listing listing : listings) {
            entityManager.persist(listing);
        }
        entityManager.flush();
    }

    // -------------------------------------- order by quantity tests ----------------------------------------------

    /**
     * Tests that the findListingByBusinessId functionality will order products by quantity
     * in ascending order i.e. from lowest to largest
     */
    @Test
    public void whenFindListingByBusinessIdTests_thenReturnQuantityOrderedListingsAscending() {
        // given

        int pageNo = 0;
        int pageSize = 5;
        Sort sortBy = Sort.by(Sort.Order.asc("quantity").ignoreCase())
                .and(Sort.by(Sort.Order.asc("id")));
        Pageable pageable = PageRequest.of(pageNo, pageSize, sortBy);
        ArrayList<Integer> orderedQuantities = new ArrayList<>();
        orderedQuantities.add(1);
        orderedQuantities.add(2);
        orderedQuantities.add(3);
        orderedQuantities.add(4);
        orderedQuantities.add(5);
        ArrayList<String> orderedProdIds = new ArrayList<>();
        orderedProdIds.add("PROD");
        orderedProdIds.add("DUCT");
        orderedProdIds.add("APP-LE3");
        orderedProdIds.add("APP-LE");
        orderedProdIds.add("APPLE");

        // when
        Page<Listing> listingsPage = listingRepository.findListingsByBusinessId(businessId, pageable);

        // then
        for (int i = 0; i < listingsPage.getContent().size(); i++) {
            assertThat(listingsPage.getContent().get(i).getQuantity()).isEqualTo(orderedQuantities.get(i));
            assertThat(listingsPage.getContent().get(i).getInventoryItem().getProductId()).isEqualTo(orderedProdIds.get(i));
        }
    }

    /**
     * Tests that the findListingByBusinessId functionality will order products by quantity
     * in descending order i.e. from largest to lowest
     */
    @Test
    public void whenFindListingByBusinessIdTests_thenReturnQuantityOrderedListingsDescending() {
        // given

        int pageNo = 0;
        int pageSize = 5;
        Sort sortBy = Sort.by(Sort.Order.desc("quantity").ignoreCase())
                .and(Sort.by(Sort.Order.asc("id")));
        Pageable pageable = PageRequest.of(pageNo, pageSize, sortBy);
        ArrayList<Integer> orderedQuantities = new ArrayList<>();
        orderedQuantities.add(5);
        orderedQuantities.add(4);
        orderedQuantities.add(3);
        orderedQuantities.add(2);
        orderedQuantities.add(1);
        ArrayList<String> orderedProdIds = new ArrayList<>();
        orderedProdIds.add("APPLE");
        orderedProdIds.add("APP-LE");
        orderedProdIds.add("APP-LE3");
        orderedProdIds.add("DUCT");
        orderedProdIds.add("PROD");

        // when
        Page<Listing> listingsPage = listingRepository.findListingsByBusinessId(businessId, pageable);

        // then
        for (int i = 0; i < listingsPage.getContent().size(); i++) {
            assertThat(listingsPage.getContent().get(i).getQuantity()).isEqualTo(orderedQuantities.get(i));
            assertThat(listingsPage.getContent().get(i).getInventoryItem().getProductId()).isEqualTo(orderedProdIds.get(i));
        }
    }

    /**
     * Tests that the findListingByBusinessId functionality will order products by price
     * in descending order i.e. from lowest to largest
     */
    @Test
    public void whenFindListingByBusinessIdTests_thenReturnPriceOrderedListingsAscending() {
        // given

        int pageNo = 0;
        int pageSize = 5;
        Sort sortBy = Sort.by(Sort.Order.asc("price").ignoreCase())
                .and(Sort.by(Sort.Order.asc("id")));
        Pageable pageable = PageRequest.of(pageNo, pageSize, sortBy);
        ArrayList<Double> orderedPrices = new ArrayList<>();
        orderedPrices.add(1.2);
        orderedPrices.add(1.5);
        orderedPrices.add(11.2);
        orderedPrices.add(12.0);
        orderedPrices.add(15.2);
        ArrayList<String> orderedProdIds = new ArrayList<>();
        orderedProdIds.add("APP-LE");
        orderedProdIds.add("APP-LE3");
        orderedProdIds.add("DUCT");
        orderedProdIds.add("APPLE");
        orderedProdIds.add("PROD");

        // when
        Page<Listing> listingsPage = listingRepository.findListingsByBusinessId(businessId, pageable);

        // then
        for (int i = 0; i < listingsPage.getContent().size(); i++) {
            assertThat(listingsPage.getContent().get(i).getPrice()).isEqualTo(orderedPrices.get(i));
            assertThat(listingsPage.getContent().get(i).getInventoryItem().getProductId()).isEqualTo(orderedProdIds.get(i));
        }
    }

    /**
     * Tests that the findListingByBusinessId functionality will order products by price
     * in descending order i.e. from largest to lowest
     */
    @Test
    public void whenFindListingByBusinessIdTests_thenReturnPriceOrderedListingsDescending() {
        // given

        int pageNo = 0;
        int pageSize = 5;
        Sort sortBy = Sort.by(Sort.Order.desc("price").ignoreCase())
                .and(Sort.by(Sort.Order.asc("id")));
        Pageable pageable = PageRequest.of(pageNo, pageSize, sortBy);
        ArrayList<Double> orderedPrices = new ArrayList<>();
        orderedPrices.add(15.2);
        orderedPrices.add(12.0);
        orderedPrices.add(11.2);
        orderedPrices.add(1.5);
        orderedPrices.add(1.2);
        ArrayList<String> orderedProdIds = new ArrayList<>();
        orderedProdIds.add("PROD");
        orderedProdIds.add("APPLE");
        orderedProdIds.add("DUCT");
        orderedProdIds.add("APP-LE3");
        orderedProdIds.add("APP-LE");

        // when
        Page<Listing> listingsPage = listingRepository.findListingsByBusinessId(businessId, pageable);

        // then
        for (int i = 0; i < listingsPage.getContent().size(); i++) {
            assertThat(listingsPage.getContent().get(i).getPrice()).isEqualTo(orderedPrices.get(i));
            assertThat(listingsPage.getContent().get(i).getInventoryItem().getProductId()).isEqualTo(orderedProdIds.get(i));
        }
    }

    /**
     * Tests that the findListingByBusinessId functionality will order products by created
     * in Ascending order i.e. from oldest to newest
     */
    @Test
    public void whenFindListingByBusinessIdTests_thenReturnCreatedOrderedListingsAscending() {
        // given

        int pageNo = 0;
        int pageSize = 5;
        Sort sortBy = Sort.by(Sort.Order.asc("created").ignoreCase())
                .and(Sort.by(Sort.Order.asc("id")));
        Pageable pageable = PageRequest.of(pageNo, pageSize, sortBy);
        ArrayList<LocalDateTime> orderedPrices = new ArrayList<>();
        orderedPrices.add(LocalDateTime.of(LocalDate.of(2020, 1, 1), LocalTime.of(0,0,0)));
        orderedPrices.add(LocalDateTime.of(LocalDate.of(2020, 8, 1), LocalTime.of(0,0,0)));
        orderedPrices.add(LocalDateTime.of(LocalDate.of(2020, 10, 1), LocalTime.of(0,0,0)));
        orderedPrices.add(LocalDateTime.of(LocalDate.of(2021, 1, 1), LocalTime.of(0,0,0)));
        orderedPrices.add(LocalDateTime.of(LocalDate.of(2021, 3, 1), LocalTime.of(0,0,0)));
        ArrayList<String> orderedProdIds = new ArrayList<>();
        orderedProdIds.add("PROD");
        orderedProdIds.add("APPLE");
        orderedProdIds.add("APP-LE");
        orderedProdIds.add("DUCT");
        orderedProdIds.add("APP-LE3");


        // when
        Page<Listing> listingsPage = listingRepository.findListingsByBusinessId(businessId, pageable);

        // then
        for (int i = 0; i < listingsPage.getContent().size(); i++) {
            assertThat(listingsPage.getContent().get(i).getCreated()).isEqualTo(orderedPrices.get(i));
            assertThat(listingsPage.getContent().get(i).getInventoryItem().getProductId()).isEqualTo(orderedProdIds.get(i));
        }
    }

    /**
     * Tests that the findListingByBusinessId functionality will order products by created
     * in descending order i.e. from newest to oldest
     */
    @Test
    public void whenFindListingByBusinessIdTests_thenReturnCreatedOrderedListingsDescending() {
        // given

        int pageNo = 0;
        int pageSize = 5;
        Sort sortBy = Sort.by(Sort.Order.desc("created").ignoreCase())
                .and(Sort.by(Sort.Order.asc("id")));
        Pageable pageable = PageRequest.of(pageNo, pageSize, sortBy);
        ArrayList<LocalDateTime> orderedPrices = new ArrayList<>();
        orderedPrices.add(LocalDateTime.of(LocalDate.of(2021, 3, 1), LocalTime.of(0,0,0)));
        orderedPrices.add(LocalDateTime.of(LocalDate.of(2021, 1, 1), LocalTime.of(0,0,0)));
        orderedPrices.add(LocalDateTime.of(LocalDate.of(2020, 10, 1), LocalTime.of(0,0,0)));
        orderedPrices.add(LocalDateTime.of(LocalDate.of(2020, 8, 1), LocalTime.of(0,0,0)));
        orderedPrices.add(LocalDateTime.of(LocalDate.of(2020, 1, 1), LocalTime.of(0,0,0)));
        ArrayList<String> orderedProdIds = new ArrayList<>();
        orderedProdIds.add("APP-LE3");
        orderedProdIds.add("DUCT");
        orderedProdIds.add("APP-LE");
        orderedProdIds.add("APPLE");
        orderedProdIds.add("PROD");

        // when
        Page<Listing> listingsPage = listingRepository.findListingsByBusinessId(businessId, pageable);

        // then
        for (int i = 0; i < listingsPage.getContent().size(); i++) {
            assertThat(listingsPage.getContent().get(i).getCreated()).isEqualTo(orderedPrices.get(i));
            assertThat(listingsPage.getContent().get(i).getInventoryItem().getProductId()).isEqualTo(orderedProdIds.get(i));
        }
    }

    /**
     * Tests that the findListingByBusinessId functionality will order products by closes
     * in ascending order i.e. from newest to oldest
     */
    @Test
    public void whenFindListingByBusinessIdTests_thenReturnClosesOrderedListingsAscending() {
        // given

        int pageNo = 0;
        int pageSize = 5;
        Sort sortBy = Sort.by(Sort.Order.asc("closes").ignoreCase())
                .and(Sort.by(Sort.Order.asc("id")));
        Pageable pageable = PageRequest.of(pageNo, pageSize, sortBy);
        ArrayList<LocalDateTime> orderedPrices = new ArrayList<>();
        orderedPrices.add(LocalDateTime.of(LocalDate.of(2021, 12, 1), LocalTime.of(0,0,0)));
        orderedPrices.add(LocalDateTime.of(LocalDate.of(2022, 3, 1), LocalTime.of(0,0,0)));
        orderedPrices.add(LocalDateTime.of(LocalDate.of(2022, 4, 1), LocalTime.of(0,0,0)));
        orderedPrices.add(LocalDateTime.of(LocalDate.of(2022, 10, 1), LocalTime.of(0,0,0)));
        orderedPrices.add(LocalDateTime.of(LocalDate.of(2023, 10, 1), LocalTime.of(0,0,0)));
        ArrayList<String> orderedProdIds = new ArrayList<>();
        orderedProdIds.add("DUCT");
        orderedProdIds.add("APP-LE3");
        orderedProdIds.add("APP-LE");
        orderedProdIds.add("APPLE");
        orderedProdIds.add("PROD");

        // when
        Page<Listing> listingsPage = listingRepository.findListingsByBusinessId(businessId, pageable);

        // then
        for (int i = 0; i < listingsPage.getContent().size(); i++) {
            assertThat(listingsPage.getContent().get(i).getCloses()).isEqualTo(orderedPrices.get(i));
            assertThat(listingsPage.getContent().get(i).getInventoryItem().getProductId()).isEqualTo(orderedProdIds.get(i));
        }
    }

    /**
     * Tests that the findListingByBusinessId functionality will order products by closes
     * in descending order i.e. from newest to oldest
     */
    @Test
    public void whenFindListingByBusinessIdTests_thenReturnClosesOrderedListingsDescending() {
        // given

        int pageNo = 0;
        int pageSize = 5;
        Sort sortBy = Sort.by(Sort.Order.desc("closes").ignoreCase())
                .and(Sort.by(Sort.Order.asc("id")));
        Pageable pageable = PageRequest.of(pageNo, pageSize, sortBy);
        ArrayList<LocalDateTime> orderedPrices = new ArrayList<>();
        orderedPrices.add(LocalDateTime.of(LocalDate.of(2023, 10, 1), LocalTime.of(0,0,0)));
        orderedPrices.add(LocalDateTime.of(LocalDate.of(2022, 10, 1), LocalTime.of(0,0,0)));
        orderedPrices.add(LocalDateTime.of(LocalDate.of(2022, 4, 1), LocalTime.of(0,0,0)));
        orderedPrices.add(LocalDateTime.of(LocalDate.of(2022, 3, 1), LocalTime.of(0,0,0)));
        orderedPrices.add(LocalDateTime.of(LocalDate.of(2021, 12, 1), LocalTime.of(0,0,0)));
        ArrayList<String> orderedProdIds = new ArrayList<>();
        orderedProdIds.add("PROD");
        orderedProdIds.add("APPLE");
        orderedProdIds.add("APP-LE");
        orderedProdIds.add("APP-LE3");
        orderedProdIds.add("DUCT");

        // when
        Page<Listing> listingsPage = listingRepository.findListingsByBusinessId(businessId, pageable);

        // then
        for (int i = 0; i < listingsPage.getContent().size(); i++) {
            assertThat(listingsPage.getContent().get(i).getCloses()).isEqualTo(orderedPrices.get(i));
            assertThat(listingsPage.getContent().get(i).getInventoryItem().getProductId()).isEqualTo(orderedProdIds.get(i));
        }
    }
}

