package org.seng302.business;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.seng302.model.Address;
import org.seng302.model.repository.AddressRepository;
import org.seng302.controller.BusinessResource;
import org.seng302.Main;
import org.seng302.model.Business;
import org.seng302.model.repository.BusinessRepository;
import org.seng302.model.enums.BusinessType;
import org.seng302.model.enums.Role;
import org.seng302.model.User;
import org.seng302.model.repository.UserRepository;
import org.seng302.view.outgoing.AddressPayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.http.Cookie;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

/**
 * BusinessResource test class
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = {Main.class})
@ActiveProfiles("test")
class BusinessResourceIntegrationTests {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BusinessRepository businessRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private AddressRepository addressRepository;

    private String payloadJson;

    private MockHttpServletResponse response;

    private Integer id;

    private String sessionToken;

    private String expectedJson;

    private String expectedUserJson;

    private User user;

    private User gAA;

    private User anotherUser;

    private Business business;

    private Address address;

    private final String expectedBusinessJson = "{\"id\":%d," +
            "\"administrators\":%s," +
            "\"primaryAdministratorId\":%d," +
            "\"name\":\"%s\"," +
            "\"description\":\"%s\"," +
            "\"address\":%s," +
            "\"businessType\":\"%s\"," +
            "\"created\":\"%s\","+
            "\"currencySymbol\":\"%s\","+
            "\"currencyCode\":\"%s\"," +
            "\"businessImages\":[]" +
            "}";

    private final String expectedAdministratorJson = "[{\"id\":%d," +
            "\"firstName\":\"%s\"," +
            "\"lastName\":\"%s\"," +
            "\"middleName\":\"%s\"," +
            "\"nickname\":\"%s\"," +
            "\"bio\":\"%s\"," +
            "\"email\":\"%s\"," +
            "\"created\":\"%s\"," +
            "\"role\":\"%s\"," +
            "\"businessesAdministered\":[]," +
            "\"images\":%s," +
            "\"dateOfBirth\":\"%s\"," +
            "\"phoneNumber\":\"%s\"," +
            "\"homeAddress\":{\"streetNumber\":\"%s\",\"streetName\":\"%s\",\"suburb\":\"%s\",\"city\":\"%s\",\"region\":\"%s\",\"country\":\"%s\",\"postcode\":\"%s\"}}]";
    private User dGAA;

    @BeforeAll
    void setup() throws Exception {
        address = new Address(
                "3/24",
                "Ilam Road",
                "Christchurch",
                "Canterbury",
                "New Zealand",
                "90210",
                "Ilam"
        );
        user = new User("testfirst",
                "testlast",
                "testmiddle",
                "testnick",
                "testbiography",
                "testemail@email.com",
                LocalDate.of(2020, 2, 2).minusYears(13),
                "0271316",
                address,
                "Testpassword123!",
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
                LocalDateTime.of(LocalDate.of(2021, 2, 2), LocalTime.of(0, 0, 0)),
                user,
                "$",
                "NZD"
        );
        business.setId(2);
        anotherUser = new User(
                "John",
                "Doe",
                "S",
                "Generic",
                "Biography",
                "email@email.com",
                LocalDate.of(2020, 2, 2).minusYears(13),
                "0271316",
                address,
                "Password123!",
                LocalDateTime.of(LocalDate.of(2021, 2, 2),
                        LocalTime.of(0, 0)),
                Role.USER);
        anotherUser.setId(3);
        anotherUser.setSessionUUID(User.generateSessionUUID());
        dGAA = new User(
                "John",
                "Doe",
                "S",
                "Generic",
                "Biography",
                "email@email.com",
                LocalDate.of(2020, 2, 2).minusYears(13),
                "0271316",
                address,
                "Password123!",
                LocalDateTime.of(LocalDate.of(2021, 2, 2),
                        LocalTime.of(0, 0)),
                Role.DEFAULTGLOBALAPPLICATIONADMIN);
        dGAA.setId(2);
        dGAA.setSessionUUID(User.generateSessionUUID());
        gAA = new User(
                "John",
                "Doe",
                "S",
                "Generic",
                "Biography",
                "gaa@email.com",
                LocalDate.of(2020, 2, 2).minusYears(13),
                "0271316",
                address,
                "Password123!",
                LocalDateTime.of(LocalDate.of(2021, 2, 2),
                        LocalTime.of(0, 0)),
                Role.GLOBALAPPLICATIONADMIN);
        gAA.setId(4);
        gAA.setSessionUUID(User.generateSessionUUID());
        this.mvc = MockMvcBuilders.standaloneSetup(
                new BusinessResource(businessRepository, userRepository, addressRepository)
        ).build();
    }

    /**
     * test when business has been create, current user has been add to business's administrators
     * @throws Exception thrown by MockMvc
     */
    @Test
    void setAdministratorComplete() throws Exception {
        // given
        Business newBusiness = new Business(
                user.getId(),
                "Lumbridge General Store",
                "A one-stop shop for all your adventuring needs",
                new Address(
                        "2/24",
                        "Ilam Road",
                        "Christchurch",
                        "Canterbury",
                        "New Zealand",
                        "90210",
                        "Ilam"
                ),
                BusinessType.ACCOMMODATION_AND_FOOD_SERVICES,
                LocalDateTime.now(),
                user,
                "$",
                "NZD"
        );
        newBusiness.setId(3);
        newBusiness.addAdministrators(user);

        payloadJson = "{" +
                "\"primaryAdministratorId\": " + user.getId() + "," +
                "\"name\": \"Lumbridge General Stores\"," +
                "\"description\": \"A one-stop shop for all your adventuring needs\"," +
                "\"address\": {" +
                "\"streetNumber\": \"2/24\"," +
                "\"streetName\": \"Ilam Road\"," +
                "\"city\": \"Christchurch\"," +
                "\"region\": \"Canterbury\"," +
                "\"country\": \"New Zealand\"," +
                "\"postcode\": \"90210\"," +
                "\"suburb\": \"Ilam\"" +
                "}," +
                "\"businessType\": \"Accommodation and Food Services\"" +
                "}";
        sessionToken = user.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);

        // when
        when(businessRepository.save(any(Business.class))).thenReturn(newBusiness);
        when(userRepository.findBySessionUUID(sessionToken)).thenReturn(Optional.ofNullable(user));
        response = mvc.perform(post("/businesses").cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON).content(payloadJson)).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(newBusiness.getAdministrators().get(0)).isEqualTo(user);
    }

    /**
     * test when business has been create, current user has been add to business's administrators
     * @throws Exception thrown by MockMvc
     */
    @Test
    void setPrimaryAdministratorComplete() throws Exception {
        // given
        Business newBusiness = new Business(
                user.getId(),
                "Lumbridge General Store",
                "A one-stop shop for all your adventuring needs",
                new Address(
                        "2/24",
                        "Ilam Road",
                        "Christchurch",
                        "Canterbury",
                        "New Zealand",
                        "90210",
                        "Ilam"
                ),
                BusinessType.ACCOMMODATION_AND_FOOD_SERVICES,
                LocalDateTime.now(),
                user,
                "$",
                "NZD"
        );
        newBusiness.setId(3);
        newBusiness.addAdministrators(user);

        payloadJson = "{" +
                "\"primaryAdministratorId\": " + user.getId() + "," +
                "\"name\": \"Lumbridge General Stores\"," +
                "\"description\": \"A one-stop shop for all your adventuring needs\"," +
                "\"address\": {" +
                "\"streetNumber\": \"2/24\"," +
                "\"streetName\": \"Ilam Road\"," +
                "\"city\": \"Christchurch\"," +
                "\"region\": \"Canterbury\"," +
                "\"country\": \"New Zealand\"," +
                "\"postcode\": \"90210\"," +
                "\"suburb\": \"Ilam\"" +
                "}," +
                "\"businessType\": \"Accommodation and Food Services\"" +
                "}";
        sessionToken = user.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);

        // when
        when(businessRepository.save(any(Business.class))).thenReturn(newBusiness);
        when(userRepository.findBySessionUUID(sessionToken)).thenReturn(Optional.ofNullable(user));
        response = mvc.perform(post("/businesses").cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON).content(payloadJson)).andReturn().getResponse();

        // then

        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(newBusiness.getPrimaryAdministratorId()).isEqualTo(user.getId());
    }

//--------------------------------------------------/businesses--------------------------------------------------

    /**
     * Tests that an CREATED(201) status is received when sending a create payload to the /businesses API endpoint
     * that contains business name, description, address, businessType and a create cookie belongs to an user.
     */
    @Test
    void canCreateWhenDataValidAndCookieExists() throws Exception {
        // given
        payloadJson = "{" +
                "\"primaryAdministratorId\": " + user.getId() + "," +
                "\"name\": \"Lumbridge General Stores\"," +
                "\"description\": \"A one-stop shop for all your adventuring needs\"," +
                "\"address\": {" +
                "\"streetNumber\": \"16/24\"," +
                "\"streetName\": \"Ilam Road\"," +
                "\"city\": \"Christchurch\"," +
                "\"region\": \"Canterbury\"," +
                "\"country\": \"New Zealand\"," +
                "\"postcode\": \"90211\"," +
                "\"suburb\": \"Ilam\"" +
                "}," +
                "\"businessType\": \"Accommodation and Food Services\"" +
                "}";
        sessionToken = user.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);

        // when
        when(businessRepository.save(any(Business.class))).thenReturn(business);
        when(userRepository.findBySessionUUID(sessionToken)).thenReturn(Optional.ofNullable(user));
        response = mvc.perform(post("/businesses").cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON).content(payloadJson)).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
    }

    /**
     * Tests that an BAD_REQUEST(400) status is received when sending a payload to the /businesses API endpoint
     * that contains business name(empty), description, address, businessType.
     */
    @Test
    void canNotCreateWhenNameEmpty() throws Exception {
        // given
        payloadJson = "{" +
                "\"primaryAdministratorId\": " + user.getId() + "," +
                "\"name\": \"\"," +
                "\"description\": \"A one-stop shop for all your adventuring needs\"," +
                "\"address\": {" +
                "\"streetNumber\": \"2/24\"," +
                "\"streetName\": \"Ilam Road\"," +
                "\"city\": \"Christchurch\"," +
                "\"region\": \"Canterbury\"," +
                "\"country\": \"New Zealand\"," +
                "\"postcode\": \"90210\"," +
                "\"suburb\": \"Ilam\"" +
                "}," +
                "\"businessType\": \"Accommodation and Food Services\"" +
                "}";
        sessionToken = user.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);

        // when
        when(userRepository.findBySessionUUID(sessionToken)).thenReturn(Optional.ofNullable(user));
        response = mvc.perform(post("/businesses").cookie(cookie).
                contentType(MediaType.APPLICATION_JSON).content(payloadJson)).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getErrorMessage()).isEqualTo("Invalid business name");
    }

    /**
     * Tests that an BAD_REQUEST(400) status is received when sending a payload to the /businesses API endpoint
     * that contains business name(only space), description, address, businessType.
     */
    @Test
    void canNotCreateWhenNameOnlySpace() throws Exception {
        // given
        payloadJson = "{" +
                "\"primaryAdministratorId\": " + user.getId() + "," +
                "\"name\": \"   \"," +
                "\"description\": \"A one-stop shop for all your adventuring needs\"," +
                "\"address\": {" +
                "\"streetNumber\": \"2/24\"," +
                "\"streetName\": \"Ilam Road\"," +
                "\"city\": \"Christchurch\"," +
                "\"region\": \"Canterbury\"," +
                "\"country\": \"New Zealand\"," +
                "\"postcode\": \"90210\"," +
                "\"suburb\": \"Ilam\"" +
                "}," +
                "\"businessType\": \"Accommodation and Food Services\"" +
                "}";
        sessionToken = user.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);

        // when
        when(userRepository.findBySessionUUID(sessionToken)).thenReturn(Optional.ofNullable(user));
        response = mvc.perform(post("/businesses").cookie(cookie).
                contentType(MediaType.APPLICATION_JSON).content(payloadJson)).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getErrorMessage()).isEqualTo("Invalid business name");
    }

    /**
     * Tests that an BAD_REQUEST(400) status is received when sending a payload to the /businesses API endpoint
     * that contains business name(length = 101), description, address, businessType.
     */
    @Test
    void canNotCreateWhenNameLengthLargerThan100() throws Exception {
        // given
        String aName = "a".repeat(101);
        payloadJson = "{" +
                "\"primaryAdministratorId\": " + user.getId() + "," +
                "\"name\": \"" + aName + "\"," +
                "\"description\": \"A one-stop shop for all your adventuring needs\"," +
                "\"address\": {" +
                "\"streetNumber\": \"2/24\"," +
                "\"streetName\": \"Ilam Road\"," +
                "\"city\": \"Christchurch\"," +
                "\"region\": \"Canterbury\"," +
                "\"country\": \"New Zealand\"," +
                "\"postcode\": \"90210\"," +
                "\"suburb\": \"Ilam\"" +
                "}," +
                "\"businessType\": \"Accommodation and Food Services\"" +
                "}";
        sessionToken = user.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);

        // when
        when(userRepository.findBySessionUUID(sessionToken)).thenReturn(Optional.ofNullable(user));
        response = mvc.perform(post("/businesses").cookie(cookie).
                contentType(MediaType.APPLICATION_JSON).content(payloadJson)).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getErrorMessage()).isEqualTo("Invalid business name");
    }

    /**
     * Tests that an BAD_REQUEST(400) status is received when sending a payload to the /businesses API endpoint
     * that contains business name, description(length = 601), address, businessType.
     */
    @Test
    void canNotCreateWhenDescriptionLengthLargerThan600() throws Exception {
        // given
        String aDescription = "a".repeat(601);
        payloadJson = "{" +
                "\"primaryAdministratorId\": " + user.getId() + "," +
                "\"name\": \"Lumbridge General Store\",\n" +
                "\"description\": \"" + aDescription + "\"," +
                "\"address\": {" +
                "\"streetNumber\": \"2/24\"," +
                "\"streetName\": \"Ilam Road\"," +
                "\"city\": \"Christchurch\"," +
                "\"region\": \"Canterbury\"," +
                "\"country\": \"New Zealand\"," +
                "\"postcode\": \"90210\"," +
                "\"suburb\": \"Ilam\"" +
                "}," +
                "\"businessType\": \"Accommodation and Food Services\"" +
                "}";
        sessionToken = user.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);

        // when
        when(userRepository.findBySessionUUID(sessionToken)).thenReturn(Optional.ofNullable(user));
        response = mvc.perform(post("/businesses").cookie(cookie).
                contentType(MediaType.APPLICATION_JSON).content(payloadJson)).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getErrorMessage()).isEqualTo("Invalid business description");
    }

    /**
     * Tests that an BAD_REQUEST(400) status is received when sending a payload to the /businesses API endpoint
     * that contains business name, description, address(length = 256), businessType.
     */
    @Test
    void canNotCreateWhenAddressLengthLargerThan255() throws Exception {
        // given
        String aString = "a".repeat(256);
        payloadJson = "{" +
                "\"primaryAdministratorId\": " + user.getId() + "," +
                "\"name\": \"Lumbridge General Store\",\n" +
                "\"description\": \"A one-stop shop for all your adventuring needs\"," +
                "\"address\": {" +
                "\"streetNumber\": \"" + aString + "\"," +
                "\"streetName\": \"" + aString + "\"," +
                "\"city\": \"" + aString + "\"," +
                "\"region\": \"" + aString + "\"," +
                "\"country\": \"" + aString + "\"," +
                "\"postcode\": \"9\"," +
                "\"suburb\": \"Ilam\"" +
                "}," +
                "\"businessType\": \"Accommodation and Food Services\"" +
                "}";
        sessionToken = user.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);

        // when
        when(userRepository.findBySessionUUID(sessionToken)).thenReturn(Optional.ofNullable(user));
        response = mvc.perform(post("/businesses").cookie(cookie).
                contentType(MediaType.APPLICATION_JSON).content(payloadJson)).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getErrorMessage()).isEqualTo("Invalid business address");
    }

    /**
     * Tests that an BAD_REQUEST(400) status is received when sending a payload to the /businesses API endpoint
     * that contains business name, description, address(country = ""), businessType.
     */
    @Test
    void canNotCreateWhenAddressContainAnEmptyCountry() throws Exception {
        // given
        payloadJson = "{" +
                "\"primaryAdministratorId\": " + user.getId() + "," +
                "\"name\": \"Lumbridge General Store\"," +
                "\"description\": \"A one-stop shop for all your adventuring needs\"," +
                "\"address\": {" +
                "\"streetNumber\": \"2/24\"," +
                "\"streetName\": \"Ilam Road\"," +
                "\"city\": \"Christchurch\"," +
                "\"region\": \"Canterbury\"," +
                "\"country\": \"\"," +
                "\"postcode\": \"90210\"," +
                "\"suburb\": \"Ilam\"" +
                "}," +
                "\"businessType\": \"Accommodation and Food Services\"" +
                "}";
        sessionToken = user.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);

        // when
        when(userRepository.findBySessionUUID(sessionToken)).thenReturn(Optional.ofNullable(user));
        response = mvc.perform(post("/businesses").cookie(cookie).
                contentType(MediaType.APPLICATION_JSON).content(payloadJson)).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getErrorMessage()).isEqualTo("Invalid business address");
    }

    /**
     * Tests that an BAD_REQUEST(400) status is received when sending a payload to the /businesses API endpoint
     * that contains business name, description, address, businessType(not exist).
     */
    @Test
    void canNotCreateWhenBusinessTypeIsNotExist() throws Exception {
        // given
        payloadJson = "{" +
                "\"primaryAdministratorId\": " + user.getId() + "," +
                "\"name\": \"Lumbridge General Store\",\n" +
                "\"description\": \"A one-stop shop for all your adventuring needs\"," +
                "\"address\": {" +
                "\"streetNumber\": \"2/24\"," +
                "\"streetName\": \"Ilam Road\"," +
                "\"city\": \"Christchurch\"," +
                "\"region\": \"Canterbury\"," +
                "\"country\": \"New Zealand\"," +
                "\"postcode\": \"90210\"," +
                "\"suburb\": \"Ilam\"" +
                "}," +
                "\"businessType\": \"example\"" +
                "}";
        sessionToken = user.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);

        // when
        when(userRepository.findBySessionUUID(sessionToken)).thenReturn(Optional.ofNullable(user));
        response = mvc.perform(post("/businesses").cookie(cookie).
                contentType(MediaType.APPLICATION_JSON).content(payloadJson)).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getErrorMessage()).isEqualTo("Invalid business type");
    }

    /**
     * Tests that an UNAUTHORIZED(401) status is received when sending a create payload to the /businesses API endpoint
     * that contains business name, description, address, businessType but a wrong cookie.
     */
    @Test
    void canNotCreateWhenDataValidAndCookieNotExists() throws Exception {
        // given
        payloadJson = "{" +
                "\"primaryAdministratorId\": " + user.getId() + "," +
                "\"name\": \"Lumbridge General Store\"," +
                "\"description\": \"A one-stop shop for all your adventuring needs\"," +
                "\"address\": {" +
                "\"streetNumber\": \"2/24\"," +
                "\"streetName\": \"Ilam Road\"," +
                "\"city\": \"Christchurch\"," +
                "\"region\": \"Canterbury\"," +
                "\"country\": \"New Zealand\"," +
                "\"postcode\": \"90210\"," +
                "\"suburb\": \"Ilam\"" +
                "}," +
                "\"businessType\": \"Accommodation and Food Services\"" +
                "}";

        // when
        when(userRepository.findBySessionUUID(sessionToken)).thenReturn(Optional.empty());
        response = mvc.perform(post("/businesses").contentType(MediaType.APPLICATION_JSON)
                .content(payloadJson)).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    /**
     * Tests that an Forbidden(403) status is received when sending a create payload to the /businesses API endpoint
     * that contains business name, description, address, businessType but a wrong primaryAdministratorId.
     */
    @Test
    void canNotCreateWhenDataValidAndPrimaryAdministratorIdDifferent() throws Exception {
        // given
        payloadJson = "{" +
                "\"primaryAdministratorId\": " + (user.getId()+1) + "," +
                "\"name\": \"Lumbridge General Store\"," +
                "\"description\": \"A one-stop shop for all your adventuring needs\"," +
                "\"address\": {" +
                "\"streetNumber\": \"2/24\"," +
                "\"streetName\": \"Ilam Road\"," +
                "\"city\": \"Christchurch\"," +
                "\"region\": \"Canterbury\"," +
                "\"country\": \"New Zealand\"," +
                "\"postcode\": \"90210\"," +
                "\"suburb\": \"Ilam\"" +
                "}," +
                "\"businessType\": \"Accommodation and Food Services\"" +
                "}";
        sessionToken = user.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);

        // when
        when(userRepository.findBySessionUUID(sessionToken)).thenReturn(Optional.ofNullable(user));
        response = mvc.perform(post("/businesses").cookie(cookie).
                contentType(MediaType.APPLICATION_JSON).content(payloadJson)).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

//--------------------------------------------------/businesses/{id}--------------------------------------------------

    /**
     * Tests that a OK(200) status is received when the user id in the /businesses/{id} API endpoint does exist, and
     * primary administrator id will be display(As current user is administrator of this business).
     */
    @Test
    void administratorCanRetrieveBusinessWhenBusinessDoesExist() throws Exception {
        // given
        id = business.getId();
        expectedJson = "{" +
                "\"id\":" + id + "," +
                "\"administrators\":" + "[{\"id\":1," +
                "\"firstName\":\"testfirst\"," +
                "\"lastName\":\"testlast\"," +
                "\"middleName\":\"testmiddle\"," +
                "\"nickname\":\"testnick\"," +
                "\"bio\":\"testbiography\"," +
                "\"email\":\"testemail@email.com\"," +
                "\"created\":\"2021-02-02T00:00\"," +
                "\"role\":\"USER\"," +
                "\"businessesAdministered\":[]," +
                "\"images\":[]," +
                "\"dateOfBirth\":\"2007-02-02\"," +
                "\"phoneNumber\":\"0271316\"," +
                "\"homeAddress\":{" +
                "\"streetNumber\":\"3/24\"," +
                "\"streetName\":\"Ilam Road\"," +
                "\"suburb\":\"Ilam\"," +
                "\"city\":\"Christchurch\"," +
                "\"region\":\"Canterbury\"," +
                "\"country\":\"New Zealand\"," +
                "\"postcode\":\"90210\"" +
                "}" +
                "}]" + "," +
                "\"primaryAdministratorId\":" + business.getPrimaryAdministratorId() + "," +
                "\"name\":\"" + business.getName() + "\"," +
                "\"description\":\"" + business.getDescription() + "\"," +
                "\"address\":{" +
                "\"streetNumber\":\"" + address.getStreetNumber() + "\"," +
                "\"streetName\":\"" + address.getStreetName() + "\"," +
                "\"suburb\":\"" + address.getSuburb() + "\"," +
                "\"city\":\"" + address.getCity() + "\"," +
                "\"region\":\"" + address.getRegion() + "\"," +
                "\"country\":\"" + address.getCountry() + "\"," +
                "\"postcode\":\"" + address.getPostcode() + "\"" +
                "}," +
                "\"businessType\":\"" + business.getBusinessType() + "\"," +
                "\"created\":\"" + business.getCreated() + "\"," +
                "\"currencySymbol\":\"" + business.getCurrencySymbol() + "\"," +
                "\"currencyCode\":\"" + business.getCurrencyCode() + "\"," +
                "\"businessImages\":[]}";
        sessionToken = user.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);

        // when
        when(userRepository.findBySessionUUID(sessionToken)).thenReturn(Optional.ofNullable(user));
        when(businessRepository.findBusinessById(business.getId())).thenReturn(Optional.ofNullable(business));
        response = mvc.perform(get(String.format("/businesses/%d", id)).cookie(cookie)).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(expectedJson);
    }

    /**
     * Tests that a OK(200) status is received when the user id in the /businesses/{id} API endpoint does exist, and
     * primary administrator id will be display(As current user is not administrator of this business and not a DGAA).
     */
    @Test
    void nonAdministratorCanRetrieveBusinessWhenBusinessDoesExist() throws Exception {
        // given
        id = business.getId();
        expectedJson = "{" +
                "\"id\":" + id + "," +
                "\"administrators\":" + "[{\"id\":1," +
                "\"firstName\":\"testfirst\"," +
                "\"lastName\":\"testlast\"," +
                "\"middleName\":\"testmiddle\"," +
                "\"nickname\":\"testnick\"," +
                "\"bio\":\"testbiography\"," +
                "\"email\":\"testemail@email.com\"," +
                "\"created\":\"2021-02-02T00:00\"," +
                "\"role\":\"USER\"," +
                "\"businessesAdministered\":[]," +
                "\"images\":[]," +
                "\"dateOfBirth\":\"2007-02-02\"," +
                "\"phoneNumber\":\"0271316\"," +
                "\"homeAddress\":{" +
                "\"streetNumber\":\"3/24\"," +
                "\"streetName\":\"Ilam Road\"," +
                "\"suburb\":\"Ilam\"," +
                "\"city\":\"Christchurch\"," +
                "\"region\":\"Canterbury\"," +
                "\"country\":\"New Zealand\"," +
                "\"postcode\":\"90210\"" +
                "}" +
                "}]" + "," +
                "\"primaryAdministratorId\":null," +
                "\"name\":\"" + business.getName() + "\"," +
                "\"description\":\"" + business.getDescription() + "\"," +
                "\"address\":{" +
                "\"streetNumber\":\"" + address.getStreetNumber() + "\"," +
                "\"streetName\":\"" + address.getStreetName() + "\"," +
                "\"suburb\":\"" + address.getSuburb() + "\"," +
                "\"city\":\"" + address.getCity() + "\"," +
                "\"region\":\"" + address.getRegion() + "\"," +
                "\"country\":\"" + address.getCountry() + "\"," +
                "\"postcode\":\"" + address.getPostcode() + "\"" +
                "}," +
                "\"businessType\":\"" + business.getBusinessType() + "\"," +
                "\"created\":\"" + business.getCreated() + "\"," +
                "\"currencySymbol\":\"" + business.getCurrencySymbol() + "\"," +
                "\"currencyCode\":\"" + business.getCurrencyCode() + "\"," +
                "\"businessImages\":[]}";
        sessionToken = anotherUser.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);

        // when
        when(userRepository.findBySessionUUID(sessionToken)).thenReturn(Optional.ofNullable(anotherUser));
        when(businessRepository.findBusinessById(business.getId())).thenReturn(Optional.ofNullable(business));
        response = mvc.perform(get(String.format("/businesses/%d", id)).cookie(cookie)).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(expectedJson);
    }

    /**
     * Tests that a OK(200) status is received when the user id in the /businesses/{id} API endpoint does exist, and
     * primary administrator id will be display(As current user is DGAA).
     */
    @Test
    void DGAACanRetrieveBusinessWhenBusinessDoesExist() throws Exception {
        User user = new User("testfirst",
                "testlast",
                "testmiddle",
                "testnick",
                "testbiography",
                "testemail@email.com",
                LocalDate.of(2020, 2, 2).minusYears(13),
                "0271316",
                address,
                "Testpassword123!",
                LocalDateTime.of(LocalDate.of(2021, 2, 2),
                        LocalTime.of(0, 0)),
                Role.DEFAULTGLOBALAPPLICATIONADMIN);
        user.setId(1);
        user.setSessionUUID(User.generateSessionUUID());

        // given
        id = business.getId();
        expectedJson = "{" +
                "\"id\":" + id + "," +
                "\"administrators\":" + "[{\"id\":1," +
                "\"firstName\":\"testfirst\"," +
                "\"lastName\":\"testlast\"," +
                "\"middleName\":\"testmiddle\"," +
                "\"nickname\":\"testnick\"," +
                "\"bio\":\"testbiography\"," +
                "\"email\":\"testemail@email.com\"," +
                "\"created\":\"2021-02-02T00:00\"," +
                "\"role\":\"USER\"," +
                "\"businessesAdministered\":[]," +
                "\"images\":[]," +
                "\"dateOfBirth\":\"2007-02-02\"," +
                "\"phoneNumber\":\"0271316\"," +
                "\"homeAddress\":{" +
                "\"streetNumber\":\"3/24\"," +
                "\"streetName\":\"Ilam Road\"," +
                "\"suburb\":\"Ilam\"," +
                "\"city\":\"Christchurch\"," +
                "\"region\":\"Canterbury\"," +
                "\"country\":\"New Zealand\"," +
                "\"postcode\":\"90210\"" +
                "}" +
                "}]" + "," +
                "\"primaryAdministratorId\":" + business.getPrimaryAdministratorId() + "," +
                "\"name\":\"" + business.getName() + "\"," +
                "\"description\":\"" + business.getDescription() + "\"," +
                "\"address\":{" +
                "\"streetNumber\":\"" + address.getStreetNumber() + "\"," +
                "\"streetName\":\"" + address.getStreetName() + "\"," +
                "\"suburb\":\"" + address.getSuburb() + "\"," +
                "\"city\":\"" + address.getCity() + "\"," +
                "\"region\":\"" + address.getRegion() + "\"," +
                "\"country\":\"" + address.getCountry() + "\"," +
                "\"postcode\":\"" + address.getPostcode() + "\"" +
                "}," +
                "\"businessType\":\"" + business.getBusinessType() + "\"," +
                "\"created\":\"" + business.getCreated() + "\"," +
                "\"currencySymbol\":\"" + business.getCurrencySymbol() + "\"," +
                "\"currencyCode\":\"" + business.getCurrencyCode() + "\"," +
                "\"businessImages\":[]}";

        sessionToken = user.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);

        // when
        when(userRepository.findBySessionUUID(sessionToken)).thenReturn(Optional.ofNullable(user));
        when(businessRepository.findBusinessById(business.getId())).thenReturn(Optional.ofNullable(business));
        response = mvc.perform(get(String.format("/businesses/%d", id)).cookie(cookie)).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(expectedJson);
    }

    /**
     * Tests that a UNAUTHORIZED(401) status is received when cookie wrong
     */
    @Test
    void canNotRetrieveBusinessWhenCookieNotExist() throws Exception {
        // given
        String nonExistingSessionUUID = User.generateSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", nonExistingSessionUUID);
        expectedJson = "";

        // when
        when(userRepository.findBySessionUUID(nonExistingSessionUUID)).thenReturn(Optional.empty());
        when(businessRepository.findBusinessById(business.getId())).thenReturn(Optional.ofNullable(business));
        response = mvc.perform(
                get(String.format("/businesses/%d", business.getId())).cookie(cookie)
        ).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(response.getContentAsString()).isEqualTo(expectedJson);
    }

    /**
     * Tests that a NOT_ACCEPTABLE(406) status is received when the user id in the /businesses/{id} API endpoint does exist
     */
    @Test
    void canNotRetrieveBusinessWhenBusinessDoesNotExist() throws Exception {
        // given
        int nonExistentBusinessId = 0;
        sessionToken = user.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);
        expectedJson = "";

        // when
        when(userRepository.findBySessionUUID(sessionToken)).thenReturn(Optional.ofNullable(user));
        when(businessRepository.findBusinessById(nonExistentBusinessId)).thenReturn(Optional.empty());
        response = mvc.perform(
                get(String.format("/businesses/%d", nonExistentBusinessId)).cookie(cookie)
        ).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_ACCEPTABLE.value());
        assertThat(response.getContentAsString()).isEqualTo(expectedJson);
    }

//-----------------------------------------/businesses/{id}/makeAdministrator-----------------------------------------

    /**
     * Tests that an OK(200) status is received when sending a non-administrator(for this business) userId payload to
     * the /businesses/{id}/makeAdministrator API endpoint. And current session token is for an administrator of this
     * business.
     * @throws Exception thrown by MockMvc
     */
    @Test
    void anBusinessAdministratorCanMakeUserBecomeAdministrator() throws Exception {
        // given
        User anotherUser = new User(
                "John",
                "Doe",
                "S",
                "Generic",
                "Biography",
                "email@email.com",
                LocalDate.of(2020, 2, 2).minusYears(13),
                "0271316",
                address,
                "Password123!",
                LocalDateTime.of(LocalDate.of(2021, 2, 2),
                        LocalTime.of(0, 0)),
                Role.USER);
        anotherUser.setId(3);
        anotherUser.setSessionUUID(User.generateSessionUUID());
        User user = new User("testfirst",
                "testlast",
                "testmiddle",
                "testnick",
                "testbiography",
                "testemail@email.com",
                LocalDate.of(2020, 2, 2).minusYears(13),
                "0271316",
                address,
                "Testpassword123!",
                LocalDateTime.of(LocalDate.of(2021, 2, 2),
                        LocalTime.of(0, 0)),
                Role.USER);
        user.setId(1);
        user.setSessionUUID(User.generateSessionUUID());
        Business business = new Business(
                user.getId(),
                "name",
                "some text",
                address,
                BusinessType.ACCOMMODATION_AND_FOOD_SERVICES,
                LocalDateTime.of(LocalDate.of(2021, 2, 2), LocalTime.of(0, 0, 0)),
                user,
                "$",
                "NZD"
        );
        business.setId(2);

        id = business.getId();
        expectedJson = "{" +
                "\"userId\":" + anotherUser.getId() +
                "}";
        sessionToken = user.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);

        //add business to user object()
        List<Business> businessesAdministeredObjects = user.getBusinessesAdministeredObjects();
        businessesAdministeredObjects.add(business);
        user.setBusinessesAdministeredObjects(businessesAdministeredObjects);

        // when
        when(userRepository.findBySessionUUID(sessionToken)).thenReturn(Optional.ofNullable(user));
        when(userRepository.findById(anotherUser.getId())).thenReturn(Optional.ofNullable(anotherUser));
        when(businessRepository.findBusinessById(business.getId())).thenReturn(Optional.ofNullable(business));

        response = mvc.perform(put(String.format("/businesses/%d/makeAdministrator", id)).cookie(cookie)
                .content(expectedJson).contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        // then
        System.out.println(response.getErrorMessage());
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    /**
     * Tests that an OK(200) status is received when sending a non-administrator(for this business) userId payload to
     * the /businesses/{id}/makeAdministrator API endpoint. And current session token is for a DGAA.
     * @throws Exception thrown by MockMvc
     */
    @Test
    void aDGAACanMakeUserBecomeAdministrator() throws Exception {
        User anotherUser = new User(
                "John",
                "Doe",
                "S",
                "Generic",
                "Biography",
                "email@email.com",
                LocalDate.of(2020, 2, 2).minusYears(13),
                "0271316",
                address,
                "Password123!",
                LocalDateTime.of(LocalDate.of(2021, 2, 2),
                        LocalTime.of(0, 0)),
                Role.USER);
        anotherUser.setId(3);
        anotherUser.setSessionUUID(User.generateSessionUUID());
        User user = new User("testfirst",
                "testlast",
                "testmiddle",
                "testnick",
                "testbiography",
                "testemail@email.com",
                LocalDate.of(2020, 2, 2).minusYears(13),
                "0271316",
                address,
                "Testpassword123!",
                LocalDateTime.of(LocalDate.of(2021, 2, 2),
                        LocalTime.of(0, 0)),
                Role.USER);
        user.setId(1);
        user.setSessionUUID(User.generateSessionUUID());
        Business business = new Business(
                user.getId(),
                "name",
                "some text",
                address,
                BusinessType.ACCOMMODATION_AND_FOOD_SERVICES,
                LocalDateTime.of(LocalDate.of(2021, 2, 2), LocalTime.of(0, 0, 0)),
                user,
                "$",
                "NZD"
        );
        business.setId(2);
        // given
        id = business.getId();
        expectedJson = "{" +
                "\"userId\":" + anotherUser.getId() +
                "}";
        sessionToken = user.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);

        user.setRole(Role.DEFAULTGLOBALAPPLICATIONADMIN);

        // when
        when(userRepository.findBySessionUUID(sessionToken)).thenReturn(Optional.ofNullable(user));
        when(userRepository.findById(anotherUser.getId())).thenReturn(Optional.ofNullable(anotherUser));
        when(businessRepository.findBusinessById(business.getId())).thenReturn(Optional.ofNullable(business));

        response = mvc.perform(put(String.format("/businesses/%d/makeAdministrator", id)).cookie(cookie)
                .content(expectedJson).contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    /**
     * Tests that an BAD_REQUEST(400) status is received when sending a not exist userId payload to the
     * /businesses/{id}/makeAdministrator API endpoint. And current session token is for an administrator of this
     * business.
     * @throws Exception thrown by MockMvc
     */
    @Test
    void anBusinessAdministratorCanNotMakeANotExistUserBecomeAdministrator() throws Exception {
        User anotherUser = new User(
                "John",
                "Doe",
                "S",
                "Generic",
                "Biography",
                "email@email.com",
                LocalDate.of(2020, 2, 2).minusYears(13),
                "0271316",
                address,
                "Password123!",
                LocalDateTime.of(LocalDate.of(2021, 2, 2),
                        LocalTime.of(0, 0)),
                Role.USER);
        anotherUser.setId(3);
        anotherUser.setSessionUUID(User.generateSessionUUID());
        User user = new User("testfirst",
                "testlast",
                "testmiddle",
                "testnick",
                "testbiography",
                "testemail@email.com",
                LocalDate.of(2020, 2, 2).minusYears(13),
                "0271316",
                address,
                "Testpassword123!",
                LocalDateTime.of(LocalDate.of(2021, 2, 2),
                        LocalTime.of(0, 0)),
                Role.USER);
        user.setId(1);
        user.setSessionUUID(User.generateSessionUUID());
        Business business = new Business(
                user.getId(),
                "name",
                "some text",
                address,
                BusinessType.ACCOMMODATION_AND_FOOD_SERVICES,
                LocalDateTime.of(LocalDate.of(2021, 2, 2), LocalTime.of(0, 0, 0)),
                user,
                "$",
                "NZD"
        );
        business.setId(2);
        // given
        id = business.getId();
        expectedJson = "{" +
                "\"userId\":0" +
                "}";
        sessionToken = user.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);

        //add business to user object()
        List<Business> businessesAdministeredObjects = user.getBusinessesAdministeredObjects();
        businessesAdministeredObjects.add(business);
        user.setBusinessesAdministeredObjects(businessesAdministeredObjects);

        // when
        when(userRepository.findBySessionUUID(sessionToken)).thenReturn(Optional.ofNullable(user));
        when(userRepository.findById(anotherUser.getId())).thenReturn(Optional.ofNullable(anotherUser));
        when(businessRepository.findBusinessById(business.getId())).thenReturn(Optional.ofNullable(business));

        response = mvc.perform(put(String.format("/businesses/%d/makeAdministrator", id)).cookie(cookie)
                .content(expectedJson).contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Tests that an BAD_REQUEST(400) status is received when sending a administrator(for this business) userId payload
     * to the /businesses/{id}/makeAdministrator API endpoint. And current session token is for an administrator of
     * this business.
     * @throws Exception thrown by MockMvc
     */
    @Test
    void anBusinessAdministratorCanNotMakeOtherAdministratorBecomeAdministrator() throws Exception {
        User anotherUser = new User(
                "John",
                "Doe",
                "S",
                "Generic",
                "Biography",
                "email@email.com",
                LocalDate.of(2020, 2, 2).minusYears(13),
                "0271316",
                address,
                "Password123!",
                LocalDateTime.of(LocalDate.of(2021, 2, 2),
                        LocalTime.of(0, 0)),
                Role.USER);
        anotherUser.setId(3);
        anotherUser.setSessionUUID(User.generateSessionUUID());
        User user = new User("testfirst",
                "testlast",
                "testmiddle",
                "testnick",
                "testbiography",
                "testemail@email.com",
                LocalDate.of(2020, 2, 2).minusYears(13),
                "0271316",
                address,
                "Testpassword123!",
                LocalDateTime.of(LocalDate.of(2021, 2, 2),
                        LocalTime.of(0, 0)),
                Role.USER);
        user.setId(1);
        user.setSessionUUID(User.generateSessionUUID());
        Business business = new Business(
                user.getId(),
                "name",
                "some text",
                address,
                BusinessType.ACCOMMODATION_AND_FOOD_SERVICES,
                LocalDateTime.of(LocalDate.of(2021, 2, 2), LocalTime.of(0, 0, 0)),
                user,
                "$",
                "NZD"
        );
        business.setId(2);
        // given
        id = business.getId();
        expectedJson = "{" +
                "\"userId\":" + anotherUser.getId() +
                "}";
        sessionToken = user.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);

        //add otherUser to administrator of business
        business.addAdministrators(anotherUser);

        //add business to user and otherUser
        List<Business> businessesAdministeredObjects = user.getBusinessesAdministeredObjects();
        businessesAdministeredObjects.add(business);
        user.setBusinessesAdministeredObjects(businessesAdministeredObjects);
        anotherUser.setBusinessesAdministeredObjects(businessesAdministeredObjects);

        // when
        when(userRepository.findBySessionUUID(sessionToken)).thenReturn(Optional.ofNullable(user));
        when(userRepository.findById(anotherUser.getId())).thenReturn(Optional.ofNullable(anotherUser));
        when(businessRepository.findBusinessById(business.getId())).thenReturn(Optional.ofNullable(business));

        response = mvc.perform(put(String.format("/businesses/%d/makeAdministrator", id)).cookie(cookie)
                .content(expectedJson).contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Tests that an UNAUTHORIZED(401) status is received when sending a non-administrator(for this business) userId
     * payload to the /businesses/{id}/makeAdministrator API endpoint. But session token is missing.
     * @throws Exception thrown by MockMvc
     */
    @Test
    void whenSessionTokenMissing_MakingUserBecomeAdministratorNotWork() throws Exception {
        User anotherUser = new User(
                "John",
                "Doe",
                "S",
                "Generic",
                "Biography",
                "email@email.com",
                LocalDate.of(2020, 2, 2).minusYears(13),
                "0271316",
                address,
                "Password123!",
                LocalDateTime.of(LocalDate.of(2021, 2, 2),
                        LocalTime.of(0, 0)),
                Role.USER);
        anotherUser.setId(3);
        anotherUser.setSessionUUID(User.generateSessionUUID());
        User user = new User("testfirst",
                "testlast",
                "testmiddle",
                "testnick",
                "testbiography",
                "testemail@email.com",
                LocalDate.of(2020, 2, 2).minusYears(13),
                "0271316",
                address,
                "Testpassword123!",
                LocalDateTime.of(LocalDate.of(2021, 2, 2),
                        LocalTime.of(0, 0)),
                Role.USER);
        user.setId(1);
        user.setSessionUUID(User.generateSessionUUID());
        Business business = new Business(
                user.getId(),
                "name",
                "some text",
                address,
                BusinessType.ACCOMMODATION_AND_FOOD_SERVICES,
                LocalDateTime.of(LocalDate.of(2021, 2, 2), LocalTime.of(0, 0, 0)),
                user,
                "$",
                "NZD"
        );
        business.setId(2);
        // given
        id = business.getId();
        expectedJson = "{" +
                "\"userId\":" + anotherUser.getId() +
                "}";
        sessionToken = user.getSessionUUID();

        //add business to user object()
        List<Business> businessesAdministeredObjects = user.getBusinessesAdministeredObjects();
        businessesAdministeredObjects.add(business);
        user.setBusinessesAdministeredObjects(businessesAdministeredObjects);

        // when
        when(userRepository.findBySessionUUID(sessionToken)).thenReturn(Optional.ofNullable(user));
        when(userRepository.findById(anotherUser.getId())).thenReturn(Optional.ofNullable(anotherUser));
        when(businessRepository.findBusinessById(business.getId())).thenReturn(Optional.ofNullable(business));

        response = mvc.perform(put(String.format("/businesses/%d/makeAdministrator", id))
                .content(expectedJson).contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    /**
     * Tests that an FORBIDDEN(403) status is received when sending a userId payload to the
     * /businesses/{id}/makeAdministrator API endpoint. But current session token is for an normal user.
     * @throws Exception thrown by MockMvc
     */
    @Test
    void aNormalUserCanNotMakeUserBecomeAdministrator() throws Exception {
        User anotherUser = new User(
                "John",
                "Doe",
                "S",
                "Generic",
                "Biography",
                "email@email.com",
                LocalDate.of(2020, 2, 2).minusYears(13),
                "0271316",
                address,
                "Password123!",
                LocalDateTime.of(LocalDate.of(2021, 2, 2),
                        LocalTime.of(0, 0)),
                Role.USER);
        anotherUser.setId(3);
        anotherUser.setSessionUUID(User.generateSessionUUID());
        User user = new User("testfirst",
                "testlast",
                "testmiddle",
                "testnick",
                "testbiography",
                "testemail@email.com",
                LocalDate.of(2020, 2, 2).minusYears(13),
                "0271316",
                address,
                "Testpassword123!",
                LocalDateTime.of(LocalDate.of(2021, 2, 2),
                        LocalTime.of(0, 0)),
                Role.USER);
        user.setId(1);
        user.setSessionUUID(User.generateSessionUUID());
        Business business = new Business(
                user.getId(),
                "name",
                "some text",
                address,
                BusinessType.ACCOMMODATION_AND_FOOD_SERVICES,
                LocalDateTime.of(LocalDate.of(2021, 2, 2), LocalTime.of(0, 0, 0)),
                user,
                "$",
                "NZD"
        );
        business.setId(2);
        // given
        id = business.getId();
        expectedJson = "{" +
                "\"userId\":" + anotherUser.getId() +
                "}";
        sessionToken = anotherUser.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);

        //delete 'user' in 'business'
        business.setAdministrators(new ArrayList<>());

        // when
        when(userRepository.findBySessionUUID(sessionToken)).thenReturn(Optional.ofNullable(anotherUser));
        when(userRepository.findById(user.getId())).thenReturn(Optional.ofNullable(user));
        when(businessRepository.findBusinessById(business.getId())).thenReturn(Optional.ofNullable(business));

        response = mvc.perform(put(String.format("/businesses/%d/makeAdministrator", id)).cookie(cookie)
                .content(expectedJson).contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
        assertThat(business.getAdministrators().size()).isZero();
    }

    /**
     * Tests that an FORBIDDEN(403) status is received when a business administrator that is not the primary
     * administrator requests to make a user a business administrator.
     * @throws Exception thrown by MockMvc
     */
    @Test
    void aNonPrimaryAdministratorCannotMakeUserBecomeAdministrator() throws Exception {
        User anotherUser = new User(
                "John",
                "Doe",
                "S",
                "Generic",
                "Biography",
                "email@email.com",
                LocalDate.of(2020, 2, 2).minusYears(13),
                "0271316",
                address,
                "Password123!",
                LocalDateTime.of(LocalDate.of(2021, 2, 2),
                        LocalTime.of(0, 0)),
                Role.USER);
        anotherUser.setId(3);
        anotherUser.setSessionUUID(User.generateSessionUUID());
        User user = new User("testfirst",
                "testlast",
                "testmiddle",
                "testnick",
                "testbiography",
                "testemail@email.com",
                LocalDate.of(2020, 2, 2).minusYears(13),
                "0271316",
                address,
                "Testpassword123!",
                LocalDateTime.of(LocalDate.of(2021, 2, 2),
                        LocalTime.of(0, 0)),
                Role.USER);
        user.setId(1);
        user.setSessionUUID(User.generateSessionUUID());
        User aThirdUser = new User("newfirst",
                "newlast",
                "newmiddle",
                "newnick",
                "newbiography",
                "newemail@email.com",
                LocalDate.of(2020, 3, 2).minusYears(13),
                "02799999",
                address,
                "Testpassword123!",
                LocalDateTime.of(LocalDate.of(2021, 2, 2),
                        LocalTime.of(0, 0)),
                Role.USER);
        aThirdUser.setId(5);
        aThirdUser.setSessionUUID(User.generateSessionUUID());
        Business business = new Business(
                user.getId(),
                "name",
                "some text",
                address,
                BusinessType.ACCOMMODATION_AND_FOOD_SERVICES,
                LocalDateTime.of(LocalDate.of(2021, 2, 2), LocalTime.of(0, 0, 0)),
                user,
                "$",
                "NZD"
        );
        business.setId(2);
        // given
        id = business.getId();
        expectedJson = "{" +
                "\"userId\":" + aThirdUser.getId() +
                "}";
        sessionToken = anotherUser.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);

        // Add business to user's and anotherUser's administrated businesses lists
        List<Business> businessesAdministeredObjects = user.getBusinessesAdministeredObjects();
        businessesAdministeredObjects.add(business);
        user.setBusinessesAdministeredObjects(businessesAdministeredObjects);
        anotherUser.setBusinessesAdministeredObjects(businessesAdministeredObjects);
        business.addAdministrators(anotherUser);

        // when
        when(userRepository.findBySessionUUID(sessionToken)).thenReturn(Optional.ofNullable(anotherUser));
        when(userRepository.findById(aThirdUser.getId())).thenReturn(Optional.ofNullable(aThirdUser));
        when(businessRepository.findBusinessById(business.getId())).thenReturn(Optional.ofNullable(business));

        response = mvc.perform(put(String.format("/businesses/%d/makeAdministrator", id)).cookie(cookie)
                .content(expectedJson).contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
        assertThat(response.getErrorMessage()).isEqualTo("Current user is not DGAA or a primary administrator of this business");
    }

    /**
     * Tests that an NOT_ACCEPTABLE(406) status is received when sending a non-administrator(for this business) userId payload to
     * the /businesses/{id}/makeAdministrator API endpoint. And current session token is for an administrator of this
     * business. But given business not exist.
     * @throws Exception thrown by MockMvc
     */
    @Test
    void canNotMakeUserBecomeAdministratorWhenBusinessNotExist() throws Exception {
        User anotherUser = new User(
                "John",
                "Doe",
                "S",
                "Generic",
                "Biography",
                "email@email.com",
                LocalDate.of(2020, 2, 2).minusYears(13),
                "0271316",
                address,
                "Password123!",
                LocalDateTime.of(LocalDate.of(2021, 2, 2),
                        LocalTime.of(0, 0)),
                Role.USER);
        anotherUser.setId(3);
        anotherUser.setSessionUUID(User.generateSessionUUID());
        User user = new User("testfirst",
                "testlast",
                "testmiddle",
                "testnick",
                "testbiography",
                "testemail@email.com",
                LocalDate.of(2020, 2, 2).minusYears(13),
                "0271316",
                address,
                "Testpassword123!",
                LocalDateTime.of(LocalDate.of(2021, 2, 2),
                        LocalTime.of(0, 0)),
                Role.USER);
        user.setId(1);
        user.setSessionUUID(User.generateSessionUUID());
        Business business = new Business(
                user.getId(),
                "name",
                "some text",
                address,
                BusinessType.ACCOMMODATION_AND_FOOD_SERVICES,
                LocalDateTime.of(LocalDate.of(2021, 2, 2), LocalTime.of(0, 0, 0)),
                user,
                "$",
                "NZD"
        );
        business.setId(2);
        // given
        id = 0;
        expectedJson = "{" +
                "\"userId\":" + anotherUser.getId() +
                "}";
        sessionToken = user.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);

        //add business to user object()
        List<Business> businessesAdministeredObjects = user.getBusinessesAdministeredObjects();
        businessesAdministeredObjects.add(business);
        user.setBusinessesAdministeredObjects(businessesAdministeredObjects);

        // when
        when(userRepository.findBySessionUUID(sessionToken)).thenReturn(Optional.ofNullable(user));
        when(userRepository.findById(anotherUser.getId())).thenReturn(Optional.ofNullable(anotherUser));
        when(businessRepository.findBusinessById(business.getId())).thenReturn(Optional.ofNullable(business));

        response = mvc.perform(put(String.format("/businesses/%d/makeAdministrator", id)).cookie(cookie)
                .content(expectedJson).contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_ACCEPTABLE.value());
    }

//-----------------------------------------/businesses/{id}/removeAdministrator-----------------------------------------

    /**
     * Tests that an OK(200) status is received when sending a non-administrator(for this business) userId payload to
     * the /businesses/{id}/removeAdministrator API endpoint. And current session token is for an administrator of this
     * business.
     * @throws Exception thrown by MockMvc
     */
    @Test
    void aBusinessAdministratorCanRemoveUserBecomeAdministrator() throws Exception {
        User anotherUser = new User(
                "John",
                "Doe",
                "S",
                "Generic",
                "Biography",
                "email@email.com",
                LocalDate.of(2020, 2, 2).minusYears(13),
                "0271316",
                address,
                "Password123!",
                LocalDateTime.of(LocalDate.of(2021, 2, 2),
                        LocalTime.of(0, 0)),
                Role.USER);
        anotherUser.setId(3);
        anotherUser.setSessionUUID(User.generateSessionUUID());
        User user = new User("testfirst",
                "testlast",
                "testmiddle",
                "testnick",
                "testbiography",
                "testemail@email.com",
                LocalDate.of(2020, 2, 2).minusYears(13),
                "0271316",
                address,
                "Testpassword123!",
                LocalDateTime.of(LocalDate.of(2021, 2, 2),
                        LocalTime.of(0, 0)),
                Role.USER);
        user.setId(1);
        user.setSessionUUID(User.generateSessionUUID());
        Business business = new Business(
                user.getId(),
                "name",
                "some text",
                address,
                BusinessType.ACCOMMODATION_AND_FOOD_SERVICES,
                LocalDateTime.of(LocalDate.of(2021, 2, 2), LocalTime.of(0, 0, 0)),
                user,
                "$",
                "NZD"
        );
        business.setId(2);
        // given
        id = business.getId();
        expectedJson = "{" +
                "\"userId\":" + anotherUser.getId() +
                "}";
        sessionToken = user.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);

        //add business to user and anotherUser
        List<Business> businessesAdministeredObjects = user.getBusinessesAdministeredObjects();
        businessesAdministeredObjects.add(business);
        user.setBusinessesAdministeredObjects(businessesAdministeredObjects);
        anotherUser.setBusinessesAdministeredObjects(businessesAdministeredObjects);
        //add user and anotherUser to business
        business.addAdministrators(anotherUser);

        // when
        when(userRepository.findBySessionUUID(sessionToken)).thenReturn(Optional.ofNullable(user));
        when(userRepository.findById(anotherUser.getId())).thenReturn(Optional.ofNullable(anotherUser));
        when(businessRepository.findBusinessById(business.getId())).thenReturn(Optional.ofNullable(business));

        response = mvc.perform(put(String.format("/businesses/%d/removeAdministrator", id)).cookie(cookie)
                .content(expectedJson).contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    /**
     * Tests that an OK(200) status is received when sending a non-administrator(for this business) userId payload to
     * the /businesses/{id}/removeAdministrator API endpoint. And current session token is for a DGAA.
     * @throws Exception thrown by MockMvc
     */
    @Test
    void aDGAACanRemoveUserBecomeAdministrator() throws Exception {
        User anotherUser = new User(
                "John",
                "Doe",
                "S",
                "Generic",
                "Biography",
                "email@email.com",
                LocalDate.of(2020, 2, 2).minusYears(13),
                "0271316",
                address,
                "Password123!",
                LocalDateTime.of(LocalDate.of(2021, 2, 2),
                        LocalTime.of(0, 0)),
                Role.USER);
        anotherUser.setId(3);
        anotherUser.setSessionUUID(User.generateSessionUUID());
        User user = new User("testfirst",
                "testlast",
                "testmiddle",
                "testnick",
                "testbiography",
                "testemail@email.com",
                LocalDate.of(2020, 2, 2).minusYears(13),
                "0271316",
                address,
                "Testpassword123!",
                LocalDateTime.of(LocalDate.of(2021, 2, 2),
                        LocalTime.of(0, 0)),
                Role.USER);
        user.setId(1);
        user.setSessionUUID(User.generateSessionUUID());
        Business business = new Business(
                user.getId(),
                "name",
                "some text",
                address,
                BusinessType.ACCOMMODATION_AND_FOOD_SERVICES,
                LocalDateTime.of(LocalDate.of(2021, 2, 2), LocalTime.of(0, 0, 0)),
                user,
                "$",
                "NZD"
        );
        business.setId(2);
        // given
        id = business.getId();
        expectedJson = "{" +
                "\"userId\":" + anotherUser.getId() +
                "}";
        sessionToken = user.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);

        user.setRole(Role.DEFAULTGLOBALAPPLICATIONADMIN);
        //add business to user and anotherUser
        List<Business> businessesAdministeredObjects = user.getBusinessesAdministeredObjects();
        businessesAdministeredObjects.add(business);
        anotherUser.setBusinessesAdministeredObjects(businessesAdministeredObjects);
        //add user and anotherUser to business
        business.addAdministrators(anotherUser);

        // when
        when(userRepository.findBySessionUUID(sessionToken)).thenReturn(Optional.ofNullable(user));
        when(userRepository.findById(anotherUser.getId())).thenReturn(Optional.ofNullable(anotherUser));
        when(businessRepository.findBusinessById(business.getId())).thenReturn(Optional.ofNullable(business));

        response = mvc.perform(put(String.format("/businesses/%d/removeAdministrator", id)).cookie(cookie)
                .content(expectedJson).contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        // then
        System.out.println(response.getErrorMessage());
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    /**
     * Tests that an BAD_REQUEST(400) status is received when sending a not exist userId payload to the
     * /businesses/{id}/removeAdministrator API endpoint. And current session token is for an administrator of this
     * business.
     * @throws Exception thrown by MockMvc
     */
    @Test
    void anBusinessAdministratorCanNotRemoveANotExistUserBecomeAdministrator() throws Exception {
        User anotherUser = new User(
                "John",
                "Doe",
                "S",
                "Generic",
                "Biography",
                "email@email.com",
                LocalDate.of(2020, 2, 2).minusYears(13),
                "0271316",
                address,
                "Password123!",
                LocalDateTime.of(LocalDate.of(2021, 2, 2),
                        LocalTime.of(0, 0)),
                Role.USER);
        anotherUser.setId(3);
        anotherUser.setSessionUUID(User.generateSessionUUID());
        User user = new User("testfirst",
                "testlast",
                "testmiddle",
                "testnick",
                "testbiography",
                "testemail@email.com",
                LocalDate.of(2020, 2, 2).minusYears(13),
                "0271316",
                address,
                "Testpassword123!",
                LocalDateTime.of(LocalDate.of(2021, 2, 2),
                        LocalTime.of(0, 0)),
                Role.USER);
        user.setId(1);
        user.setSessionUUID(User.generateSessionUUID());
        Business business = new Business(
                user.getId(),
                "name",
                "some text",
                address,
                BusinessType.ACCOMMODATION_AND_FOOD_SERVICES,
                LocalDateTime.of(LocalDate.of(2021, 2, 2), LocalTime.of(0, 0, 0)),
                user,
                "$",
                "NZD"
        );
        business.setId(2);
        // given
        id = business.getId();
        expectedJson = "{" +
                "\"userId\":0" +
                "}";
        sessionToken = user.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);

        //add business to user object()
        List<Business> businessesAdministeredObjects = user.getBusinessesAdministeredObjects();
        businessesAdministeredObjects.add(business);
        user.setBusinessesAdministeredObjects(businessesAdministeredObjects);

        // when
        when(userRepository.findBySessionUUID(sessionToken)).thenReturn(Optional.ofNullable(user));
        when(userRepository.findById(anotherUser.getId())).thenReturn(Optional.ofNullable(anotherUser));
        when(businessRepository.findBusinessById(business.getId())).thenReturn(Optional.ofNullable(business));

        response = mvc.perform(put(String.format("/businesses/%d/removeAdministrator", id)).cookie(cookie)
                .content(expectedJson).contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Tests that an BAD_REQUEST(400) status is received when sending a administrator(for this business) userId payload
     * to the /businesses/{id}/removeAdministrator API endpoint. And current session token is for an administrator of
     * this business.
     * @throws Exception thrown by MockMvc
     */
    @Test
    void anBusinessAdministratorCanNotRemoveOtherAdministratorBecomeAdministrator() throws Exception {
        User anotherUser = new User(
                "John",
                "Doe",
                "S",
                "Generic",
                "Biography",
                "email@email.com",
                LocalDate.of(2020, 2, 2).minusYears(13),
                "0271316",
                address,
                "Password123!",
                LocalDateTime.of(LocalDate.of(2021, 2, 2),
                        LocalTime.of(0, 0)),
                Role.USER);
        anotherUser.setId(3);
        anotherUser.setSessionUUID(User.generateSessionUUID());
        User user = new User("testfirst",
                "testlast",
                "testmiddle",
                "testnick",
                "testbiography",
                "testemail@email.com",
                LocalDate.of(2020, 2, 2).minusYears(13),
                "0271316",
                address,
                "Testpassword123!",
                LocalDateTime.of(LocalDate.of(2021, 2, 2),
                        LocalTime.of(0, 0)),
                Role.USER);
        user.setId(1);
        user.setSessionUUID(User.generateSessionUUID());
        Business business = new Business(
                user.getId(),
                "name",
                "some text",
                address,
                BusinessType.ACCOMMODATION_AND_FOOD_SERVICES,
                LocalDateTime.of(LocalDate.of(2021, 2, 2), LocalTime.of(0, 0, 0)),
                user,
                "$",
                "NZD"
        );
        business.setId(2);
        // given
        id = business.getId();
        expectedJson = "{" +
                "\"userId\":" + anotherUser.getId() +
                "}";
        sessionToken = user.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);

        //add business to user and otherUser
        List<Business> businessesAdministeredObjects = user.getBusinessesAdministeredObjects();
        businessesAdministeredObjects.add(business);
        user.setBusinessesAdministeredObjects(businessesAdministeredObjects);
        anotherUser.setBusinessesAdministeredObjects(businessesAdministeredObjects);

        // when
        when(userRepository.findBySessionUUID(sessionToken)).thenReturn(Optional.ofNullable(user));
        when(userRepository.findById(anotherUser.getId())).thenReturn(Optional.ofNullable(anotherUser));
        when(businessRepository.findBusinessById(business.getId())).thenReturn(Optional.ofNullable(business));

        response = mvc.perform(put(String.format("/businesses/%d/removeAdministrator", id)).cookie(cookie)
                .content(expectedJson).contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Tests that an UNAUTHORIZED(401) status is received when sending a non-administrator(for this business) userId
     * payload to the /businesses/{id}/removeAdministrator API endpoint. But session token is missing.
     * @throws Exception thrown by MockMvc
     */
    @Test
    void whenSessionTokenMissing_RemovingUserBecomeAdministratorNotWork() throws Exception {
        User anotherUser = new User(
                "John",
                "Doe",
                "S",
                "Generic",
                "Biography",
                "email@email.com",
                LocalDate.of(2020, 2, 2).minusYears(13),
                "0271316",
                address,
                "Password123!",
                LocalDateTime.of(LocalDate.of(2021, 2, 2),
                        LocalTime.of(0, 0)),
                Role.USER);
        anotherUser.setId(3);
        anotherUser.setSessionUUID(User.generateSessionUUID());
        User user = new User("testfirst",
                "testlast",
                "testmiddle",
                "testnick",
                "testbiography",
                "testemail@email.com",
                LocalDate.of(2020, 2, 2).minusYears(13),
                "0271316",
                address,
                "Testpassword123!",
                LocalDateTime.of(LocalDate.of(2021, 2, 2),
                        LocalTime.of(0, 0)),
                Role.USER);
        user.setId(1);
        user.setSessionUUID(User.generateSessionUUID());
        Business business = new Business(
                user.getId(),
                "name",
                "some text",
                address,
                BusinessType.ACCOMMODATION_AND_FOOD_SERVICES,
                LocalDateTime.of(LocalDate.of(2021, 2, 2), LocalTime.of(0, 0, 0)),
                user,
                "$",
                "NZD"
        );
        business.setId(2);
        // given
        id = business.getId();
        expectedJson = "{" +
                "\"userId\":" + anotherUser.getId() +
                "}";
        sessionToken = user.getSessionUUID();

        //add business to user object()
        List<Business> businessesAdministeredObjects = user.getBusinessesAdministeredObjects();
        businessesAdministeredObjects.add(business);
        user.setBusinessesAdministeredObjects(businessesAdministeredObjects);

        // when
        when(userRepository.findBySessionUUID(sessionToken)).thenReturn(Optional.ofNullable(user));
        when(userRepository.findById(anotherUser.getId())).thenReturn(Optional.ofNullable(anotherUser));
        when(businessRepository.findBusinessById(business.getId())).thenReturn(Optional.ofNullable(business));

        response = mvc.perform(put(String.format("/businesses/%d/removeAdministrator", id))
                .content(expectedJson).contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    /**
     * Tests that an FORBIDDEN(403) status is received when sending a userId payload to the
     * /businesses/{id}/removeAdministrator API endpoint. But current session token is for a normal user.
     * @throws Exception thrown by MockMvc
     */
    @Test
    void aNormalUserCanNotRemoveUserBecomeAdministrator() throws Exception {
        User anotherUser = new User(
                "John",
                "Doe",
                "S",
                "Generic",
                "Biography",
                "email@email.com",
                LocalDate.of(2020, 2, 2).minusYears(13),
                "0271316",
                address,
                "Password123!",
                LocalDateTime.of(LocalDate.of(2021, 2, 2),
                        LocalTime.of(0, 0)),
                Role.USER);
        anotherUser.setId(3);
        anotherUser.setSessionUUID(User.generateSessionUUID());
        User user = new User("testfirst",
                "testlast",
                "testmiddle",
                "testnick",
                "testbiography",
                "testemail@email.com",
                LocalDate.of(2020, 2, 2).minusYears(13),
                "0271316",
                address,
                "Testpassword123!",
                LocalDateTime.of(LocalDate.of(2021, 2, 2),
                        LocalTime.of(0, 0)),
                Role.USER);
        user.setId(1);
        user.setSessionUUID(User.generateSessionUUID());
        Business business = new Business(
                user.getId(),
                "name",
                "some text",
                address,
                BusinessType.ACCOMMODATION_AND_FOOD_SERVICES,
                LocalDateTime.of(LocalDate.of(2021, 2, 2), LocalTime.of(0, 0, 0)),
                user,
                "$",
                "NZD"
        );
        business.setId(2);
        // given
        id = business.getId();
        expectedJson = "{" +
                "\"userId\":" + anotherUser.getId() +
                "}";
        sessionToken = anotherUser.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);

        //delete 'user' in 'business'
        business.setAdministrators(new ArrayList<>());

        // when
        when(userRepository.findBySessionUUID(sessionToken)).thenReturn(Optional.ofNullable(anotherUser));
        when(userRepository.findById(user.getId())).thenReturn(Optional.ofNullable(user));
        when(businessRepository.findBusinessById(business.getId())).thenReturn(Optional.ofNullable(business));

        response = mvc.perform(put(String.format("/businesses/%d/removeAdministrator", id)).cookie(cookie)
                .content(expectedJson).contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
        assertThat(business.getAdministrators().size()).isZero();
    }

    /**
     * Tests that an FORBIDDEN(403) status is received when a business administrator that is not the primary business
     * administrator requests to remove another administrator.
     * Testing /businesses/{id}/removeAdministrator API endpoint.
     * @throws Exception thrown by MockMvc
     */
    @Test
    void aNonPrimaryAdminCannotRemoveAnotherAdministrator() throws Exception {
        User anotherUser = new User(
                "John",
                "Doe",
                "S",
                "Generic",
                "Biography",
                "email@email.com",
                LocalDate.of(2020, 2, 2).minusYears(13),
                "0271316",
                address,
                "Password123!",
                LocalDateTime.of(LocalDate.of(2021, 2, 2),
                        LocalTime.of(0, 0)),
                Role.USER);
        anotherUser.setId(3);
        anotherUser.setSessionUUID(User.generateSessionUUID());
        User user = new User("testfirst",
                "testlast",
                "testmiddle",
                "testnick",
                "testbiography",
                "testemail@email.com",
                LocalDate.of(2020, 2, 2).minusYears(13),
                "0271316",
                address,
                "Testpassword123!",
                LocalDateTime.of(LocalDate.of(2021, 2, 2),
                        LocalTime.of(0, 0)),
                Role.USER);
        user.setId(1);
        user.setSessionUUID(User.generateSessionUUID());
        User aThirdUser = new User("newfirst",
                "newlast",
                "newmiddle",
                "newnick",
                "newbiography",
                "newemail@email.com",
                LocalDate.of(2020, 3, 2).minusYears(13),
                "02799999",
                address,
                "Testpassword123!",
                LocalDateTime.of(LocalDate.of(2021, 2, 2),
                        LocalTime.of(0, 0)),
                Role.USER);
        aThirdUser.setId(5);
        aThirdUser.setSessionUUID(User.generateSessionUUID());
        Business business = new Business(
                user.getId(),
                "name",
                "some text",
                address,
                BusinessType.ACCOMMODATION_AND_FOOD_SERVICES,
                LocalDateTime.of(LocalDate.of(2021, 2, 2), LocalTime.of(0, 0, 0)),
                user,
                "$",
                "NZD"
        );
        business.setId(2);
        // given
        id = business.getId();
        expectedJson = "{" +
                "\"userId\":" + aThirdUser.getId() +
                "}";
        sessionToken = anotherUser.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);

        // Add business to user and anotherUser
        List<Business> businessesAdministeredObjects = user.getBusinessesAdministeredObjects();
        businessesAdministeredObjects.add(business);
        anotherUser.setBusinessesAdministeredObjects(businessesAdministeredObjects);
        aThirdUser.setBusinessesAdministeredObjects(businessesAdministeredObjects);
        // Add anotherUser and a third user to the business's administrators
        business.addAdministrators(anotherUser);
        business.addAdministrators(aThirdUser);

        // when
        when(userRepository.findBySessionUUID(sessionToken)).thenReturn(Optional.ofNullable(anotherUser));
        when(userRepository.findById(aThirdUser.getId())).thenReturn(Optional.ofNullable(aThirdUser));
        when(businessRepository.findBusinessById(business.getId())).thenReturn(Optional.ofNullable(business));

        response = mvc.perform(put(String.format("/businesses/%d/removeAdministrator", id)).cookie(cookie)
                .content(expectedJson).contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
        assertThat(response.getErrorMessage()).isEqualTo("Current user is not DGAA or a primary administrator of this business");
    }

    /**
     * Tests that an FORBIDDEN(403) status is received when a business administrator that is not the primary business
     * administrator requests to remove the primary administrator.
     * Testing /businesses/{id}/removeAdministrator API endpoint.
     * @throws Exception thrown by MockMvc
     */
    @Test
    void aNonPrimaryAdminCannotRemoveAPrimaryAdministrator() throws Exception {
        User anotherUser = new User(
                "John",
                "Doe",
                "S",
                "Generic",
                "Biography",
                "email@email.com",
                LocalDate.of(2020, 2, 2).minusYears(13),
                "0271316",
                address,
                "Password123!",
                LocalDateTime.of(LocalDate.of(2021, 2, 2),
                        LocalTime.of(0, 0)),
                Role.USER);
        anotherUser.setId(3);
        anotherUser.setSessionUUID(User.generateSessionUUID());
        User user = new User("testfirst",
                "testlast",
                "testmiddle",
                "testnick",
                "testbiography",
                "testemail@email.com",
                LocalDate.of(2020, 2, 2).minusYears(13),
                "0271316",
                address,
                "Testpassword123!",
                LocalDateTime.of(LocalDate.of(2021, 2, 2),
                        LocalTime.of(0, 0)),
                Role.USER);
        user.setId(1);
        user.setSessionUUID(User.generateSessionUUID());
        Business business = new Business(
                user.getId(),
                "name",
                "some text",
                address,
                BusinessType.ACCOMMODATION_AND_FOOD_SERVICES,
                LocalDateTime.of(LocalDate.of(2021, 2, 2), LocalTime.of(0, 0, 0)),
                user,
                "$",
                "NZD"
        );
        business.setId(2);
        // given
        id = business.getId();
        expectedJson = "{" +
                "\"userId\":" + user.getId() +
                "}";
        sessionToken = anotherUser.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);

        // Add business to user and anotherUser
        List<Business> businessesAdministeredObjects = user.getBusinessesAdministeredObjects();
        businessesAdministeredObjects.add(business);
        anotherUser.setBusinessesAdministeredObjects(businessesAdministeredObjects);
        // Add anotherUser to the business's administrators
        business.addAdministrators(anotherUser);

        // when
        when(userRepository.findBySessionUUID(sessionToken)).thenReturn(Optional.ofNullable(anotherUser));
        when(userRepository.findById(user.getId())).thenReturn(Optional.ofNullable(user));
        when(businessRepository.findBusinessById(business.getId())).thenReturn(Optional.ofNullable(business));

        response = mvc.perform(put(String.format("/businesses/%d/removeAdministrator", id)).cookie(cookie)
                .content(expectedJson).contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
        assertThat(response.getErrorMessage()).isEqualTo("Current user is not DGAA or a primary administrator of this business");
    }

    /**
     * Tests that an FORBIDDEN(403) status is received when sending a userId payload to the
     * /businesses/{id}/removeAdministrator API endpoint. But select user is it's self.
     * @throws Exception thrown by MockMvc
     */
    @Test
    void aAdministratorCanNotRemoveItsSelf() throws Exception {
        User user = new User("testfirst",
                "testlast",
                "testmiddle",
                "testnick",
                "testbiography",
                "testemail@email.com",
                LocalDate.of(2020, 2, 2).minusYears(13),
                "0271316",
                address,
                "Testpassword123!",
                LocalDateTime.of(LocalDate.of(2021, 2, 2),
                        LocalTime.of(0, 0)),
                Role.USER);
        user.setId(1);
        user.setSessionUUID(User.generateSessionUUID());
        Business business = new Business(
                user.getId(),
                "name",
                "some text",
                address,
                BusinessType.ACCOMMODATION_AND_FOOD_SERVICES,
                LocalDateTime.of(LocalDate.of(2021, 2, 2), LocalTime.of(0, 0, 0)),
                user,
                "$",
                "NZD"
        );
        business.setId(2);
        // given
        id = business.getId();
        expectedJson = "{" +
                "\"userId\":" + user.getId() +
                "}";
        sessionToken = anotherUser.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);

        //delete 'user' in 'business'

        // when
        when(userRepository.findBySessionUUID(sessionToken)).thenReturn(Optional.ofNullable(user));
        when(userRepository.findById(user.getId())).thenReturn(Optional.ofNullable(user));
        when(businessRepository.findBusinessById(business.getId())).thenReturn(Optional.ofNullable(business));

        response = mvc.perform(put(String.format("/businesses/%d/removeAdministrator", id)).cookie(cookie)
                .content(expectedJson).contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
        assertThat(business.getAdministrators().size()).isEqualTo(1);
    }

    /**
     * Tests that an NOT_ACCEPTABLE(406) status is received when sending a non-administrator(for this business) userId payload to
     * the /businesses/{id}/removeAdministrator API endpoint. And current session token is for an administrator of this
     * business. But given business not exist.
     * @throws Exception thrown by MockMvc
     */
    @Test
    void CanNotRemoveUserBecomeAdministratorWhenBusinessNotExist() throws Exception {
        User anotherUser = new User(
                "John",
                "Doe",
                "S",
                "Generic",
                "Biography",
                "email@email.com",
                LocalDate.of(2020, 2, 2).minusYears(13),
                "0271316",
                address,
                "Password123!",
                LocalDateTime.of(LocalDate.of(2021, 2, 2),
                        LocalTime.of(0, 0)),
                Role.USER);
        anotherUser.setId(3);
        anotherUser.setSessionUUID(User.generateSessionUUID());
        User user = new User("testfirst",
                "testlast",
                "testmiddle",
                "testnick",
                "testbiography",
                "testemail@email.com",
                LocalDate.of(2020, 2, 2).minusYears(13),
                "0271316",
                address,
                "Testpassword123!",
                LocalDateTime.of(LocalDate.of(2021, 2, 2),
                        LocalTime.of(0, 0)),
                Role.USER);
        user.setId(1);
        user.setSessionUUID(User.generateSessionUUID());
        Business business = new Business(
                user.getId(),
                "name",
                "some text",
                address,
                BusinessType.ACCOMMODATION_AND_FOOD_SERVICES,
                LocalDateTime.of(LocalDate.of(2021, 2, 2), LocalTime.of(0, 0, 0)),
                user,
                "$",
                "NZD"
        );
        business.setId(2);
        // given
        id = 0;
        expectedJson = "{" +
                "\"userId\":" + anotherUser.getId() +
                "}";
        sessionToken = user.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);

        //add business to user object()
        List<Business> businessesAdministeredObjects = user.getBusinessesAdministeredObjects();
        businessesAdministeredObjects.add(business);
        user.setBusinessesAdministeredObjects(businessesAdministeredObjects);

        // when
        when(userRepository.findBySessionUUID(sessionToken)).thenReturn(Optional.ofNullable(user));
        when(userRepository.findById(anotherUser.getId())).thenReturn(Optional.ofNullable(anotherUser));
        when(businessRepository.findBusinessById(business.getId())).thenReturn(Optional.ofNullable(business));

        response = mvc.perform(put(String.format("/businesses/%d/removeAdministrator", id)).cookie(cookie)
                .content(expectedJson).contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_ACCEPTABLE.value());
    }

    /* ----------------------------------------------/buisnesses/search---------------------------------------------- */

    /**
     * Tests that an OK status is received when searching for a business using the /businesses/search API endpoint
     * and that the JSON response is equal to the business searched for. The business is searched for using the
     * business name.
     * Test specifically for when the user searching for a business is a DGAA.
     */
    @Test
    void canSearchBusinessesByNameWhenBusinessExistsWithDgaaCookieTest() throws Exception {
        // given
        String searchQuery = "NAME";
        List<String> names = List.of(searchQuery);

        expectedUserJson = String.format(expectedAdministratorJson, user.getId(), user.getFirstName(), user.getLastName(),
                user.getMiddleName(), user.getNickname(), user.getBio(), user.getEmail(), user.getCreated(), user.getRole(),
                "[]", user.getDateOfBirth(), user.getPhoneNumber(), address.getStreetNumber(), address.getStreetName(), address.getSuburb(),
                address.getCity(), address.getRegion(), address.getCountry(), address.getPostcode());
        expectedJson = "[" + String.format(expectedBusinessJson, business.getId(), expectedUserJson, business.getPrimaryAdministratorId(),
                business.getName(), business.getDescription(), business.getAddress(), business.getBusinessType(), business.getCreated(),
                business.getCurrencySymbol(), business.getCurrencyCode()) + "]";

        // when
        List<Business> list = List.of(business);
        Page<Business> pagedResponse = new PageImpl<>(list);
        Sort sort = Sort.by(Sort.Order.asc("name").ignoreCase());
        Pageable paging = PageRequest.of(0, 5, sort);

        when(businessRepository.findAllBusinessesByNames(names, paging)).thenReturn(pagedResponse);
        when(userRepository.findBySessionUUID(dGAA.getSessionUUID())).thenReturn(Optional.ofNullable(dGAA));

        response = mvc.perform(get("/businesses/search").param("searchQuery", searchQuery)
                .cookie(new Cookie("JSESSIONID", dGAA.getSessionUUID()))).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(expectedJson);
    }

    /**
     * Tests that an OK status is received when searching for a business using the /businesses/search API endpoint
     * and that the JSON response is equal to the business searched for. The business is searched for using the
     * business name.
     * Test specifically for when the order by, page and page size params provided are valid.
     */
    @Test
    void canSearchBusinessesWhenBusinessExistsWithValidOrderByAndPageAndPageSizeParamsTest() throws Exception {
        // given
        String searchQuery = "NAME";
        List<String> names = List.of(searchQuery);

        expectedUserJson = String.format(expectedAdministratorJson, user.getId(), user.getFirstName(), user.getLastName(),
                user.getMiddleName(), user.getNickname(), user.getBio(), user.getEmail(), user.getCreated(), user.getRole(),
                "[]", user.getDateOfBirth(), user.getPhoneNumber(), address.getStreetNumber(), address.getStreetName(), address.getSuburb(),
                address.getCity(), address.getRegion(), address.getCountry(), address.getPostcode());
        expectedJson = "[" + String.format(expectedBusinessJson, business.getId(), expectedUserJson, business.getPrimaryAdministratorId(),
                business.getName(), business.getDescription(), business.getAddress(), business.getBusinessType(), business.getCreated(),
                business.getCurrencySymbol(), business.getCurrencyCode()) + "]";


        // when
        List<Business> list = List.of(business);
        Page<Business> pagedResponse = new PageImpl<>(list);
        Sort sort = Sort.by(Sort.Order.asc("name").ignoreCase());
        Pageable paging = PageRequest.of(0, 1, sort);

        when(businessRepository.findAllBusinessesByNames(names, paging)).thenReturn(pagedResponse);
        when(userRepository.findBySessionUUID(dGAA.getSessionUUID())).thenReturn(Optional.ofNullable(dGAA));

        response = mvc.perform(get("/businesses/search").param("searchQuery", searchQuery)
                            .param("orderBy", "nameASC")
                            .param("page", "0")
                            .param("pageSize", "1")
                            .cookie(new Cookie("JSESSIONID", dGAA.getSessionUUID()))).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(expectedJson);
    }

    /**
     * Tests that an OK status is received when searching for a business using the /businesses/search API endpoint
     * and that the JSON response is equal to the business searched for. The business is searched for using the
     * business name.
     * Test specifically for when the user searching for a business is a USER.
     */
    @Test
    void canSearchBusinessesByNameWhenBusinessExistsWithUserCookieTest() throws Exception {
        // given
        String searchQuery = "NAME";
        List<String> names = List.of(searchQuery);

        expectedUserJson = String.format(expectedAdministratorJson, user.getId(), user.getFirstName(), user.getLastName(),
                user.getMiddleName(), user.getNickname(), user.getBio(), user.getEmail(), user.getCreated(), user.getRole(),
                "[]", user.getDateOfBirth(), user.getPhoneNumber(), address.getStreetNumber(), address.getStreetName(), address.getSuburb(),
                address.getCity(), address.getRegion(), address.getCountry(), address.getPostcode());
        expectedJson = "[" + String.format(expectedBusinessJson, business.getId(), expectedUserJson, business.getPrimaryAdministratorId(),
                business.getName(), business.getDescription(), business.getAddress(), business.getBusinessType(), business.getCreated(),
                business.getCurrencySymbol(), business.getCurrencyCode()) + "]";

        // when
        List<Business> list = List.of(business);
        Page<Business> pagedResponse = new PageImpl<>(list);
        Sort sort = Sort.by(Sort.Order.asc("name").ignoreCase());
        Pageable paging = PageRequest.of(0, 5, sort);

        when(businessRepository.findAllBusinessesByNames(names, paging)).thenReturn(pagedResponse);
        when(userRepository.findBySessionUUID(user.getSessionUUID())).thenReturn(Optional.ofNullable(user));

        response = mvc.perform(get("/businesses/search").param("searchQuery", searchQuery)
                .cookie(new Cookie("JSESSIONID", user.getSessionUUID()))).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(expectedJson);
    }

    /**
     * Tests for an OK status but an empty response is received when searching for a business that does not exist using
     * the /businesses/search API endpoint. The business (that does not exist by the name searched for) is searched for
     * using the business name.
     */
    @Test
    void emptySearchBusinessesByNameWhenBusinessDoesntExistTest() throws Exception {
        // given
        String searchQuery = "BUSINESS";
        List<String> names = List.of(searchQuery);
        expectedJson = "[]";

        // when
        List<Business> list = List.of();
        Page<Business> pagedResponse = new PageImpl<>(list);
        Sort sort = Sort.by(Sort.Order.asc("name").ignoreCase());
        Pageable paging = PageRequest.of(0, 5, sort);

        when(userRepository.findBySessionUUID(dGAA.getSessionUUID())).thenReturn(Optional.ofNullable(dGAA));
        when(businessRepository.findAllBusinessesByNames(names, paging)).thenReturn(pagedResponse);

        response = mvc.perform(get("/businesses/search").param("searchQuery", searchQuery)
                .cookie(new Cookie("JSESSIONID", dGAA.getSessionUUID()))).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(expectedJson);
    }

    /**
     * Tests that a BAD_REQUEST status is received when searching for a business using the /businesses/search API endpoint
     * when the order by param is invalid.
     * Test specifically for when the order by param provided is invalid.
     */
    @Test
    void cantSearchBusinessesWithInvalidOrderByParam() throws Exception {
        // given
        String searchQuery = "NAME";
        expectedJson = "";

        // when
        when(userRepository.findBySessionUUID(dGAA.getSessionUUID())).thenReturn(Optional.ofNullable(dGAA));

        response = mvc.perform(get("/users/search").param("searchQuery", searchQuery)
                            .param("orderBy", "a")
                            .param("page", "0")
                            .param("pageSize", "1")
                            .cookie(new Cookie("JSESSIONID", dGAA.getSessionUUID()))).andReturn().getResponse();
        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).isEqualTo(expectedJson);
    }

    /**
     * Tests that a BAD_REQUEST status is received when searching for a business using the /businesses/search API endpoint
     * when the page param is invalid.
     * Test specifically for when the page param provided is invalid.
     */
    @Test
    void cantSearchBusinessesWithInvalidPageParam() throws Exception {
        // given
        String searchQuery = "NAME";
        expectedJson = "";

        // when
        when(userRepository.findBySessionUUID(dGAA.getSessionUUID())).thenReturn(Optional.ofNullable(dGAA));

        response = mvc.perform(get("/businesses/search").param("searchQuery", searchQuery)
                            .param("orderBy", "nameASC")
                            .param("page", "a")
                            .param("pageSize", "1")
                            .cookie(new Cookie("JSESSIONID", dGAA.getSessionUUID()))).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).isEqualTo(expectedJson);
    }

    /**
     * Tests that a BAD_REQUEST status is received when searching for a business using the /businesses/search API endpoint
     * when the page size param is invalid.
     * Test specifically for when the page size param provided is invalid.
     */
    @Test
    void cantSearchBusinessesWithInvalidPageSizeParam() throws Exception {
        // given
        String searchQuery = "NAME";
        expectedJson = "";

        // when
        when(userRepository.findBySessionUUID(dGAA.getSessionUUID())).thenReturn(Optional.ofNullable(dGAA));

        response = mvc.perform(get("/businesses/search").param("searchQuery", searchQuery)
                .param("orderBy", "nameASC")
                .param("page", "0")
                .param("pageSize", "a")
                .cookie(new Cookie("JSESSIONID", dGAA.getSessionUUID()))).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).isEqualTo(expectedJson);
    }

    /**
     * Tests that an UNAUTHORIZED status is received when searching for a business using the /businesses/search API endpoint
     * when the cookie contains a non-existing ID.
     */
    @Test
    void cantSearchBusinessesWithNonExistingIdCookie() throws Exception {
        // given
        String searchQuery = "NAME";
        expectedJson = "";

        // when
        when(userRepository.findBySessionUUID("0")).thenReturn(Optional.empty());

        response = mvc.perform(get("/businesses/search").param("searchQuery", searchQuery).cookie(
                new Cookie("JSESSIONID", "0"))).andReturn().getResponse();


        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(response.getContentAsString()).isEqualTo(expectedJson);
    }

    /**
     * Tests that an UNAUTHORIZED status is received when searching for a business using the /businesses/search API endpoint
     * when there is no cookie.
     */
    @Test
    void cantSearchBusinessesWithNoCookie() throws Exception {
        // given
        String searchQuery = "NAME";
        expectedJson = "";

        // when
        response = mvc.perform(get("/businesses/search").param("searchQuery", searchQuery)).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(response.getContentAsString()).isEqualTo(expectedJson);
    }

    /**
     * Tests that an OK status is received when searching for businesses using the /businesses/search API endpoint
     * and that the JSON response is equal to the businesses searched for. The businesses are searched for using business
     * type.
     * Test specifically for when searching only by business type.
     */
    @Test
    void canSearchBusinessesByTypeWhenBusinessExistsTest() throws Exception {
        // given
        String businessType = "ACCOMMODATION_AND_FOOD_SERVICES";
        BusinessType convertedBusinessType = BusinessType.ACCOMMODATION_AND_FOOD_SERVICES;

        expectedUserJson = String.format(expectedAdministratorJson, user.getId(), user.getFirstName(), user.getLastName(),
                user.getMiddleName(), user.getNickname(), user.getBio(), user.getEmail(), user.getCreated(), user.getRole(),
                "[]", user.getDateOfBirth(), user.getPhoneNumber(), address.getStreetNumber(), address.getStreetName(), address.getSuburb(),
                address.getCity(), address.getRegion(), address.getCountry(), address.getPostcode());
        expectedJson = "[" + String.format(expectedBusinessJson, business.getId(), expectedUserJson, business.getPrimaryAdministratorId(),
                business.getName(), business.getDescription(), business.getAddress(), business.getBusinessType(), business.getCreated(),
                business.getCurrencySymbol(), business.getCurrencyCode()) + "]";

        // when
        List<Business> list = List.of(business);
        Page<Business> pagedResponse = new PageImpl<>(list);
        Sort sort = Sort.by(Sort.Order.asc("name").ignoreCase());
        Pageable paging = PageRequest.of(0, 5, sort);

        when(businessRepository.findBusinessesByBusinessType(convertedBusinessType, paging)).thenReturn(pagedResponse);
        when(userRepository.findBySessionUUID(dGAA.getSessionUUID())).thenReturn(Optional.ofNullable(dGAA));

        response = mvc.perform(get("/businesses/search").param("businessType", businessType)
                .cookie(new Cookie("JSESSIONID", dGAA.getSessionUUID()))).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(expectedJson);
    }

    /**
     * Tests that an OK status is received when searching for a business using the /businesses/search API endpoint
     * and that the JSON response is equal to the business searched for. The business is searched for using business
     * type and business name.
     * Test specifically for when searching by business type and business name.
     */
    @Test
    void canSearchBusinessesByNameAndTypeWhenBusinessExistsTest() throws Exception {
        // given
        String businessType = "ACCOMMODATION_AND_FOOD_SERVICES";
        BusinessType convertedBusinessType = BusinessType.ACCOMMODATION_AND_FOOD_SERVICES;
        String searchQuery = "NAME";
        List<String> names = List.of(searchQuery);

        expectedUserJson = String.format(expectedAdministratorJson, user.getId(), user.getFirstName(), user.getLastName(),
                user.getMiddleName(), user.getNickname(), user.getBio(), user.getEmail(), user.getCreated(), user.getRole(),
                "[]", user.getDateOfBirth(), user.getPhoneNumber(), address.getStreetNumber(), address.getStreetName(), address.getSuburb(),
                address.getCity(), address.getRegion(), address.getCountry(), address.getPostcode());
        expectedJson = "[" + String.format(expectedBusinessJson, business.getId(), expectedUserJson, business.getPrimaryAdministratorId(),
                business.getName(), business.getDescription(), business.getAddress(), business.getBusinessType(), business.getCreated(),
                business.getCurrencySymbol(), business.getCurrencyCode()) + "]";

        // when
        List<Business> list = List.of(business);
        Page<Business> pagedResponse = new PageImpl<>(list);
        Sort sort = Sort.by(Sort.Order.asc("name").ignoreCase());
        Pageable paging = PageRequest.of(0, 5, sort);

        when(businessRepository.findAllBusinessesByNamesAndType(names, convertedBusinessType, paging)).thenReturn(pagedResponse);
        when(userRepository.findBySessionUUID(dGAA.getSessionUUID())).thenReturn(Optional.ofNullable(dGAA));

        response = mvc.perform(get("/businesses/search").param("searchQuery", searchQuery)
                .param("businessType", businessType)
                .cookie(new Cookie("JSESSIONID", dGAA.getSessionUUID()))).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(expectedJson);
    }

    /**
     * Testing that when changing all the fields of a business then all the changes occur. And that we
     * receive a OK status message back.
     */
    @Test
    void canUpdateTheBusinessWithAllFieldsBusinessModify() throws Exception {
        AddressPayload newAddress = new AddressPayload("123", "new", "new", "new", "NZ", "123", "subur");
        Address newAddressObj = new Address("123", "new", "new", "new", "NZ", "123", "subur");
        payloadJson = "{" +
                "\"primaryAdministratorId\":" + 13 + "," +
                "\"name\":\"" + "new" + "\"," +
                "\"description\":\"" + "new" + "\"," +
                "\"address\":" + newAddress + "," +
                "\"businessType\":\"" + "ACCOMMODATION AND FOOD SERVICES" + "\"," +
                "\"currencySymbol\":\"" + "$" + "\"," +
                "\"currencyCode\":\"" + "NZD" + "\"" +
                "}";
        Business someBusiness = new Business(
                user.getId(),
                "name",
                "some text",
                address,
                BusinessType.RETAIL_TRADE,
                LocalDateTime.of(LocalDate.of(2021, 2, 2), LocalTime.of(0, 0, 0)),
                anotherUser,
                "#",
                "BED"
        );
        Business newBusiness = new Business(
                13,
                "new",
                "new",
                newAddressObj,
                BusinessType.ACCOMMODATION_AND_FOOD_SERVICES,
                LocalDateTime.of(LocalDate.of(2021, 2, 2), LocalTime.of(0, 0, 0)),
                user,
                "$",
                "NZD");

        sessionToken = dGAA.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);
        when(userRepository.findBySessionUUID(dGAA.getSessionUUID())).thenReturn(Optional.ofNullable(dGAA));
        when(userRepository.findById(13)).thenReturn(Optional.ofNullable(user));
        when(businessRepository.findBusinessById(someBusiness.getId())).thenReturn(Optional.ofNullable(someBusiness));

        response = mvc.perform(put(String.format("/businesses/%d", someBusiness.getId())).cookie(cookie)
                .content(payloadJson).contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(someBusiness).hasToString(newBusiness.toString());
    }

    /**
     * Testing that an UNAUTHROIZED is returned when failing to provide a session token.
     */
    @Test
    void receivingUnauthorizedWhenNotProvidingASessionTokenModifyBusinessBusinessModify() throws Exception {
        AddressPayload newAddress = new AddressPayload("123", "new", "new", "new", "NZ", "123", "subur");
        Address newAddressObj = new Address("123", "new", "new", "new", "NZ", "123", "subur");
        payloadJson = "{" +
                "\"primaryAdministratorId\":" + 13 + "," +
                "\"name\":\"" + "new" + "\"," +
                "\"description\":\"" + "new" + "\"," +
                "\"address\":" + newAddress + "," +
                "\"businessType\":\"" + "ACCOMMODATION AND FOOD SERVICES" + "\"," +
                "\"currencySymbol\":\"" + "$" + "\"," +
                "\"currencyCode\":\"" + "NZD" + "\"" +
                "}";
        Business someBusiness = new Business(
                user.getId(),
                "name",
                "some text",
                address,
                BusinessType.RETAIL_TRADE,
                LocalDateTime.of(LocalDate.of(2021, 2, 2), LocalTime.of(0, 0, 0)),
                anotherUser,
                "#",
                "BED"
        );

        sessionToken = dGAA.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);
        when(userRepository.findBySessionUUID(dGAA.getSessionUUID())).thenReturn(Optional.ofNullable(dGAA));
        when(userRepository.findById(13)).thenReturn(Optional.ofNullable(user));
        when(businessRepository.findBusinessById(someBusiness.getId())).thenReturn(Optional.ofNullable(someBusiness));

        response = mvc.perform(put(String.format("/businesses/%d", someBusiness.getId()))
                .content(payloadJson).contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(someBusiness).hasToString(someBusiness.toString());
    }

    /**
     * Testing that we receive an UNAUTHORIZED when providing an invalid session token. And the business
     * is not modified.
     */
    @Test
    void receivingAuthorizedWhentProvidingAInvalidSessionTokenModifyBusinessBusinessModify() throws Exception {
        AddressPayload newAddress = new AddressPayload("123", "new", "new", "new", "NZ", "123", "subur");
        Address newAddressObj = new Address("123", "new", "new", "new", "NZ", "123", "subur");
        payloadJson = "{" +
                "\"primaryAdministratorId\":" + 13 + "," +
                "\"name\":\"" + "new" + "\"," +
                "\"description\":\"" + "new" + "\"," +
                "\"address\":" + newAddress + "," +
                "\"businessType\":\"" + "ACCOMMODATION AND FOOD SERVICES" + "\"," +
                "\"currencySymbol\":\"" + "$" + "\"," +
                "\"currencyCode\":\"" + "NZD" + "\"" +
                "}";
        Business someBusiness = new Business(
                user.getId(),
                "name",
                "some text",
                address,
                BusinessType.RETAIL_TRADE,
                LocalDateTime.of(LocalDate.of(2021, 2, 2), LocalTime.of(0, 0, 0)),
                anotherUser,
                "#",
                "BED"
        );

        sessionToken = "ASNDAJSNDKJANSKJDNAKSJNKDJN DONT MAKE ME AN ACTUAL SESSION!";
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);
        when(userRepository.findBySessionUUID(dGAA.getSessionUUID())).thenReturn(Optional.ofNullable(dGAA));
        when(userRepository.findById(13)).thenReturn(Optional.ofNullable(user));
        when(businessRepository.findBusinessById(someBusiness.getId())).thenReturn(Optional.ofNullable(someBusiness));

        response = mvc.perform(put(String.format("/businesses/%d", someBusiness.getId())).cookie(cookie)
                .content(payloadJson).contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(someBusiness).hasToString(someBusiness.toString());
    }

    /**
     * Testing that a NOT_ACCEPTABLE is returned when we provide a invalud business id. And that the business is not modified.
     */
    @Test
    void receivingANotAcceptableWhenProvidingInvalidBusinessIdBusinessModify() throws Exception {
        AddressPayload newAddress = new AddressPayload("123", "new", "new", "new", "NZ", "123", "subur");
        Address newAddressObj = new Address("123", "new", "new", "new", "NZ", "123", "subur");
        payloadJson = "{" +
                "\"primaryAdministratorId\":" + 13 + "," +
                "\"name\":\"" + "new" + "\"," +
                "\"description\":\"" + "new" + "\"," +
                "\"address\":" + newAddress + "," +
                "\"businessType\":\"" + "ACCOMMODATION AND FOOD SERVICES" + "\"," +
                "\"currencySymbol\":\"" + "$" + "\"," +
                "\"currencyCode\":\"" + "NZD" + "\"" +
                "}";
        Business someBusiness = new Business(
                user.getId(),
                "name",
                "some text",
                address,
                BusinessType.RETAIL_TRADE,
                LocalDateTime.of(LocalDate.of(2021, 2, 2), LocalTime.of(0, 0, 0)),
                anotherUser,
                "#",
                "BED"
        );
        Business newBusiness = new Business(
                13,
                "new",
                "new",
                newAddressObj,
                BusinessType.ACCOMMODATION_AND_FOOD_SERVICES,
                LocalDateTime.of(LocalDate.of(2021, 2, 2), LocalTime.of(0, 0, 0)),
                user,
                "$",
                "NZD");

        sessionToken = dGAA.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);
        when(userRepository.findBySessionUUID(dGAA.getSessionUUID())).thenReturn(Optional.ofNullable(dGAA));
        when(userRepository.findById(13)).thenReturn(Optional.ofNullable(user));
        when(businessRepository.findBusinessById(someBusiness.getId())).thenReturn(Optional.ofNullable(someBusiness));

        response = mvc.perform(put(String.format("/businesses/%d", 12354665)).cookie(cookie)
                .content(payloadJson).contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_ACCEPTABLE.value());
        assertThat(someBusiness).hasToString(someBusiness.toString());
    }

    /**
     * Testing that a GAA can modify a business that is not there own. And returning an OK status.
     */
    @Test
    void updatingBusinessAsGaaBusinessModify() throws Exception {
        AddressPayload newAddress = new AddressPayload("123", "new", "new", "new", "NZ", "123", "subur");
        Address newAddressObj = new Address("123", "new", "new", "new", "NZ", "123", "subur");
        payloadJson = "{" +
                "\"primaryAdministratorId\":" + 13 + "," +
                "\"name\":\"" + "new" + "\"," +
                "\"description\":\"" + "new" + "\"," +
                "\"address\":" + newAddress + "," +
                "\"businessType\":\"" + "ACCOMMODATION AND FOOD SERVICES" + "\"," +
                "\"currencySymbol\":\"" + "$" + "\"," +
                "\"currencyCode\":\"" + "NZD" + "\"" +
                "}";
        Business someBusiness = new Business(
                user.getId(),
                "name",
                "some text",
                address,
                BusinessType.RETAIL_TRADE,
                LocalDateTime.of(LocalDate.of(2021, 2, 2), LocalTime.of(0, 0, 0)),
                anotherUser,
                "#",
                "BED"
        );
        Business newBusiness = new Business(
                13,
                "new",
                "new",
                newAddressObj,
                BusinessType.ACCOMMODATION_AND_FOOD_SERVICES,
                LocalDateTime.of(LocalDate.of(2021, 2, 2), LocalTime.of(0, 0, 0)),
                user,
                "$",
                "NZD");

        sessionToken = gAA.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);
        when(userRepository.findBySessionUUID(gAA.getSessionUUID())).thenReturn(Optional.ofNullable(gAA));
        when(userRepository.findById(13)).thenReturn(Optional.ofNullable(user));
        when(businessRepository.findBusinessById(someBusiness.getId())).thenReturn(Optional.ofNullable(someBusiness));

        response = mvc.perform(put(String.format("/businesses/%d", someBusiness.getId())).cookie(cookie)
                .content(payloadJson).contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(someBusiness).hasToString(newBusiness.toString());
    }

    /**
     * Testing that a business can be modified by a DGAA and returns an OK status.
     */
    @Test
    void updatingBusinessAsDgaaBusinessModify() throws Exception {
        AddressPayload newAddress = new AddressPayload("123", "new", "new", "new", "NZ", "123", "subur");
        Address newAddressObj = new Address("123", "new", "new", "new", "NZ", "123", "subur");
        payloadJson = "{" +
                "\"primaryAdministratorId\":" + 13 + "," +
                "\"name\":\"" + "new" + "\"," +
                "\"description\":\"" + "new" + "\"," +
                "\"address\":" + newAddress + "," +
                "\"businessType\":\"" + "ACCOMMODATION AND FOOD SERVICES" + "\"," +
                "\"currencySymbol\":\"" + "$" + "\"," +
                "\"currencyCode\":\"" + "NZD" + "\"" +
                "}";
        Business someBusiness = new Business(
                user.getId(),
                "name",
                "some text",
                address,
                BusinessType.RETAIL_TRADE,
                LocalDateTime.of(LocalDate.of(2021, 2, 2), LocalTime.of(0, 0, 0)),
                anotherUser,
                "#",
                "BED"
        );
        Business newBusiness = new Business(
                13,
                "new",
                "new",
                newAddressObj,
                BusinessType.ACCOMMODATION_AND_FOOD_SERVICES,
                LocalDateTime.of(LocalDate.of(2021, 2, 2), LocalTime.of(0, 0, 0)),
                user,
                "$",
                "NZD");

        sessionToken = dGAA.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);
        when(userRepository.findBySessionUUID(dGAA.getSessionUUID())).thenReturn(Optional.ofNullable(dGAA));
        when(userRepository.findById(13)).thenReturn(Optional.ofNullable(user));
        when(businessRepository.findBusinessById(someBusiness.getId())).thenReturn(Optional.ofNullable(someBusiness));

        response = mvc.perform(put(String.format("/businesses/%d", someBusiness.getId())).cookie(cookie)
                .content(payloadJson).contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(someBusiness).hasToString(newBusiness.toString());
    }

    /**
     * Testing that a non admin cannot modify a business as they do not have permissions and that it
     * returns FORBIDDEN.
     */
    @Test
    void updatingBusinessAsNonAdminUserBusinessModify() throws Exception {
        AddressPayload newAddress = new AddressPayload("123", "new", "new", "new", "NZ", "123", "subur");
        Address newAddressObj = new Address("123", "new", "new", "new", "NZ", "123", "subur");
        payloadJson = "{" +
                "\"primaryAdministratorId\":" + 13 + "," +
                "\"name\":\"" + "new" + "\"," +
                "\"description\":\"" + "new" + "\"," +
                "\"address\":" + newAddress + "," +
                "\"businessType\":\"" + "ACCOMMODATION AND FOOD SERVICES" + "\"," +
                "\"currencySymbol\":\"" + "$" + "\"," +
                "\"currencyCode\":\"" + "NZD" + "\"" +
                "}";
        Business someBusiness = new Business(
                user.getId(),
                "name",
                "some text",
                address,
                BusinessType.RETAIL_TRADE,
                LocalDateTime.of(LocalDate.of(2021, 2, 2), LocalTime.of(0, 0, 0)),
                anotherUser,
                "#",
                "BED"
        );

        sessionToken = user.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);
        when(userRepository.findBySessionUUID(user.getSessionUUID())).thenReturn(Optional.ofNullable(user));
        when(userRepository.findById(13)).thenReturn(Optional.ofNullable(dGAA));
        when(businessRepository.findBusinessById(someBusiness.getId())).thenReturn(Optional.ofNullable(someBusiness));

        response = mvc.perform(put(String.format("/businesses/%d", someBusiness.getId())).cookie(cookie)
                .content(payloadJson).contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
        assertThat(someBusiness).hasToString(someBusiness.toString());
    }

    /**
     * Testing that an admin can modify all aspects of the business (not including the primary id). And returns
     * OK status.
     */
    @Test
    void updatingBusinessAsRegularAdminOfBusinessBusinessModify() throws Exception {
        AddressPayload newAddress = new AddressPayload("123", "new", "new", "new", "NZ", "123", "subur");
        Address newAddressObj = new Address("123", "new", "new", "new", "NZ", "123", "subur");
        payloadJson = "{" +
                "\"name\":\"" + "new" + "\"," +
                "\"description\":\"" + "new" + "\"," +
                "\"address\":" + newAddress + "," +
                "\"businessType\":\"" + "ACCOMMODATION AND FOOD SERVICES" + "\"," +
                "\"currencySymbol\":\"" + "$" + "\"," +
                "\"currencyCode\":\"" + "NZD" + "\"" +
                "}";
        Business someBusiness = new Business(
                dGAA.getId(),
                "name",
                "some text",
                address,
                BusinessType.RETAIL_TRADE,
                LocalDateTime.of(LocalDate.of(2021, 2, 2), LocalTime.of(0, 0, 0)),
                dGAA,
                "#",
                "BED"
        );
        someBusiness.addAdministrators(anotherUser);
        Business newBusiness = new Business(
                dGAA.getId(),
                "new",
                "new",
                newAddressObj,
                BusinessType.ACCOMMODATION_AND_FOOD_SERVICES,
                LocalDateTime.of(LocalDate.of(2021, 2, 2), LocalTime.of(0, 0, 0)),
                user,
                "$",
                "NZD");

        sessionToken = anotherUser.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);
        when(userRepository.findBySessionUUID(anotherUser.getSessionUUID())).thenReturn(Optional.ofNullable(anotherUser));
        when(userRepository.findById(13)).thenReturn(Optional.ofNullable(user));
        when(businessRepository.findBusinessById(someBusiness.getId())).thenReturn(Optional.ofNullable(someBusiness));

        response = mvc.perform(put(String.format("/businesses/%d", someBusiness.getId())).cookie(cookie)
                .content(payloadJson).contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(someBusiness).hasToString(newBusiness.toString());
    }

    /**
     * Testing that a business can be updated by the primary admin of the business (all fields). And
     * that a OK status is returned.
     */
    @Test
    void updatingBusinessAsPrimaryAdminBusinessModify() throws Exception {
        AddressPayload newAddress = new AddressPayload("123", "new", "new", "new", "NZ", "123", "subur");
        Address newAddressObj = new Address("123", "new", "new", "new", "NZ", "123", "subur");
        payloadJson = "{" +
                "\"primaryAdministratorId\":" + 13 + "," +
                "\"name\":\"" + "new" + "\"," +
                "\"description\":\"" + "new" + "\"," +
                "\"address\":" + newAddress + "," +
                "\"businessType\":\"" + "ACCOMMODATION AND FOOD SERVICES" + "\"," +
                "\"currencySymbol\":\"" + "$" + "\"," +
                "\"currencyCode\":\"" + "NZD" + "\"" +
                "}";
        Business someBusiness = new Business(
                user.getId(),
                "name",
                "some text",
                address,
                BusinessType.RETAIL_TRADE,
                LocalDateTime.of(LocalDate.of(2021, 2, 2), LocalTime.of(0, 0, 0)),
                user,
                "#",
                "BED"
        );
        Business newBusiness = new Business(
                13,
                "new",
                "new",
                newAddressObj,
                BusinessType.ACCOMMODATION_AND_FOOD_SERVICES,
                LocalDateTime.of(LocalDate.of(2021, 2, 2), LocalTime.of(0, 0, 0)),
                anotherUser,
                "$",
                "NZD");

        sessionToken = user.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);
        when(userRepository.findBySessionUUID(user.getSessionUUID())).thenReturn(Optional.ofNullable(user));
        when(userRepository.findById(13)).thenReturn(Optional.ofNullable(anotherUser));
        when(businessRepository.findBusinessById(someBusiness.getId())).thenReturn(Optional.ofNullable(someBusiness));

        response = mvc.perform(put(String.format("/businesses/%d", someBusiness.getId())).cookie(cookie)
                .content(payloadJson).contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(someBusiness).hasToString(newBusiness.toString());
    }

    /**
     * Testing that the primary admin id can be updated by the GAA. And that a OK status
     * is returned.
     */
    @Test
    void updatingBusinessPrimaryAdminIdAsGaaBusinessModify() throws Exception {
        AddressPayload newAddress = new AddressPayload("123", "new", "new", "new", "NZ", "123", "subur");
        Address newAddressObj = new Address("123", "new", "new", "new", "NZ", "123", "subur");
        payloadJson = "{" +
                "\"primaryAdministratorId\":" + 13 + "," +
                "\"name\":\"" + "new" + "\"," +
                "\"description\":\"" + "new" + "\"," +
                "\"address\":" + newAddress + "," +
                "\"businessType\":\"" + "ACCOMMODATION AND FOOD SERVICES" + "\"," +
                "\"currencySymbol\":\"" + "$" + "\"," +
                "\"currencyCode\":\"" + "NZD" + "\"" +
                "}";
        Business someBusiness = new Business(
                user.getId(),
                "name",
                "some text",
                address,
                BusinessType.RETAIL_TRADE,
                LocalDateTime.of(LocalDate.of(2021, 2, 2), LocalTime.of(0, 0, 0)),
                user,
                "#",
                "BED"
        );
        Business newBusiness = new Business(
                13,
                "new",
                "new",
                newAddressObj,
                BusinessType.ACCOMMODATION_AND_FOOD_SERVICES,
                LocalDateTime.of(LocalDate.of(2021, 2, 2), LocalTime.of(0, 0, 0)),
                anotherUser,
                "$",
                "NZD");

        sessionToken = gAA.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);
        when(userRepository.findBySessionUUID(gAA.getSessionUUID())).thenReturn(Optional.ofNullable(gAA));
        when(userRepository.findById(13)).thenReturn(Optional.ofNullable(anotherUser));
        when(businessRepository.findBusinessById(someBusiness.getId())).thenReturn(Optional.ofNullable(someBusiness));

        response = mvc.perform(put(String.format("/businesses/%d", someBusiness.getId())).cookie(cookie)
                .content(payloadJson).contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(someBusiness.getPrimaryAdministratorId()).isEqualTo(newBusiness.getPrimaryAdministratorId());
    }

    /**
     * Testing that the DGAA can change the primary admin id of a business. And that a OK status is returned.
     */
    @Test
    void updatingBusinessPrimaryAdminIdAsDgaaBusinessModify() throws Exception {
        AddressPayload newAddress = new AddressPayload("123", "new", "new", "new", "NZ", "123", "subur");
        Address newAddressObj = new Address("123", "new", "new", "new", "NZ", "123", "subur");
        payloadJson = "{" +
                "\"primaryAdministratorId\":" + 13 + "," +
                "\"name\":\"" + "new" + "\"," +
                "\"description\":\"" + "new" + "\"," +
                "\"address\":" + newAddress + "," +
                "\"businessType\":\"" + "ACCOMMODATION AND FOOD SERVICES" + "\"," +
                "\"currencySymbol\":\"" + "$" + "\"," +
                "\"currencyCode\":\"" + "NZD" + "\"" +
                "}";
        Business someBusiness = new Business(
                user.getId(),
                "name",
                "some text",
                address,
                BusinessType.RETAIL_TRADE,
                LocalDateTime.of(LocalDate.of(2021, 2, 2), LocalTime.of(0, 0, 0)),
                user,
                "#",
                "BED"
        );
        Business newBusiness = new Business(
                13,
                "new",
                "new",
                newAddressObj,
                BusinessType.ACCOMMODATION_AND_FOOD_SERVICES,
                LocalDateTime.of(LocalDate.of(2021, 2, 2), LocalTime.of(0, 0, 0)),
                anotherUser,
                "$",
                "NZD");

        sessionToken = dGAA.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);
        when(userRepository.findBySessionUUID(dGAA.getSessionUUID())).thenReturn(Optional.ofNullable(dGAA));
        when(userRepository.findById(13)).thenReturn(Optional.ofNullable(anotherUser));
        when(businessRepository.findBusinessById(someBusiness.getId())).thenReturn(Optional.ofNullable(someBusiness));

        response = mvc.perform(put(String.format("/businesses/%d", someBusiness.getId())).cookie(cookie)
                .content(payloadJson).contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(someBusiness.getPrimaryAdministratorId()).isEqualTo(newBusiness.getPrimaryAdministratorId());
    }

    /**
     * Testing that a non-admin user cannot change the primary admin id of a business. And returns a
     * FORBIDDEN error.
     */
    @Test
    void updatingBusinessPrimaryAdminIdAsUserAndFailingBusinessModify() throws Exception {
        AddressPayload newAddress = new AddressPayload("123", "new", "new", "new", "NZ", "123", "subur");
        Address newAddressObj = new Address("123", "new", "new", "new", "NZ", "123", "subur");
        payloadJson = "{" +
                "\"primaryAdministratorId\":" + 13 + "," +
                "\"name\":\"" + "new" + "\"," +
                "\"description\":\"" + "new" + "\"," +
                "\"address\":" + newAddress + "," +
                "\"businessType\":\"" + "ACCOMMODATION AND FOOD SERVICES" + "\"," +
                "\"currencySymbol\":\"" + "$" + "\"," +
                "\"currencyCode\":\"" + "NZD" + "\"" +
                "}";
        Business someBusiness = new Business(
                user.getId(),
                "name",
                "some text",
                address,
                BusinessType.RETAIL_TRADE,
                LocalDateTime.of(LocalDate.of(2021, 2, 2), LocalTime.of(0, 0, 0)),
                user,
                "#",
                "BED"
        );

        sessionToken = anotherUser.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);
        when(userRepository.findBySessionUUID(anotherUser.getSessionUUID())).thenReturn(Optional.ofNullable(anotherUser));
        when(userRepository.findById(13)).thenReturn(Optional.ofNullable(anotherUser));
        when(businessRepository.findBusinessById(someBusiness.getId())).thenReturn(Optional.ofNullable(someBusiness));

        response = mvc.perform(put(String.format("/businesses/%d", someBusiness.getId())).cookie(cookie)
                .content(payloadJson).contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
        assertThat(someBusiness.getPrimaryAdministratorId()).isEqualTo(someBusiness.getPrimaryAdministratorId());
    }

    /**
     * Testing that a regular admin cannot change the primary admin of a business. And that a FORBIDDEN is returned.
     */
    @Test
    void updatingBusinessPrimaryAdminIdAsAdminAndFailingBusinessModify() throws Exception {
        AddressPayload newAddress = new AddressPayload("123", "new", "new", "new", "NZ", "123", "subur");
        Address newAddressObj = new Address("123", "new", "new", "new", "NZ", "123", "subur");
        payloadJson = "{" +
                "\"primaryAdministratorId\":" + 13 + "," +
                "\"name\":\"" + "new" + "\"," +
                "\"description\":\"" + "new" + "\"," +
                "\"address\":" + newAddress + "," +
                "\"businessType\":\"" + "ACCOMMODATION AND FOOD SERVICES" + "\"," +
                "\"currencySymbol\":\"" + "$" + "\"," +
                "\"currencyCode\":\"" + "NZD" + "\"" +
                "}";
        Business someBusiness = new Business(
                user.getId(),
                "name",
                "some text",
                address,
                BusinessType.RETAIL_TRADE,
                LocalDateTime.of(LocalDate.of(2021, 2, 2), LocalTime.of(0, 0, 0)),
                user,
                "#",
                "BED"
        );
        someBusiness.addAdministrators(anotherUser);

        sessionToken = anotherUser.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);
        when(userRepository.findBySessionUUID(anotherUser.getSessionUUID())).thenReturn(Optional.ofNullable(anotherUser));
        when(userRepository.findById(13)).thenReturn(Optional.ofNullable(dGAA));
        when(businessRepository.findBusinessById(someBusiness.getId())).thenReturn(Optional.of(someBusiness));

        response = mvc.perform(put(String.format("/businesses/%d", someBusiness.getId())).cookie(cookie)
                .content(payloadJson).contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
        assertThat(someBusiness).hasToString(someBusiness.toString());
    }

    /**
     * Testing the primary admin can change the primary admin id of a business. And returns an OK status.
     */
    @Test
    void updatingBusinessPrimaryAdminIdAsPrimaryAdminBusinessModify() throws Exception {
        AddressPayload newAddress = new AddressPayload("123", "new", "new", "new", "NZ", "123", "subur");
        Address oldAddress = new Address("1", "old", "old", "old", "old", "old", "old");
        Address newAddressObj = new Address("123", "new", "new", "new", "NZ", "123", "subur");
        payloadJson = "{" +
                "\"primaryAdministratorId\":" + 13 + "," +
                "\"name\":\"" + "new" + "\"," +
                "\"description\":\"" + "new" + "\"," +
                "\"address\":" + newAddress + "," +
                "\"businessType\":\"" + "ACCOMMODATION AND FOOD SERVICES" + "\"," +
                "\"currencySymbol\":\"" + "$" + "\"," +
                "\"currencyCode\":\"" + "NZD" + "\"" +
                "}";
        Business someBusiness = new Business(
                user.getId(),
                "name",
                "some text",
                oldAddress,
                BusinessType.RETAIL_TRADE,
                LocalDateTime.of(LocalDate.of(2021, 2, 2), LocalTime.of(0, 0, 0)),
                user,
                "#",
                "BED"
        );
        Business newBusiness = new Business(
                13,
                "new",
                "new",
                newAddressObj,
                BusinessType.ACCOMMODATION_AND_FOOD_SERVICES,
                LocalDateTime.of(LocalDate.of(2021, 2, 2), LocalTime.of(0, 0, 0)),
                anotherUser,
                "$",
                "NZD");

        sessionToken = user.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);
        when(userRepository.findBySessionUUID(user.getSessionUUID())).thenReturn(Optional.ofNullable(user));
        when(userRepository.findById(13)).thenReturn(Optional.ofNullable(anotherUser));
        when(businessRepository.findBusinessById(someBusiness.getId())).thenReturn(Optional.ofNullable(someBusiness));

        response = mvc.perform(put(String.format("/businesses/%d", someBusiness.getId())).cookie(cookie)
                .content(payloadJson).contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(someBusiness.getPrimaryAdministratorId()).isEqualTo(newBusiness.getPrimaryAdministratorId());
    }

    /**
     * Testing that a business can be modified without the primary id being mentioned.
     */
    @Test
    void updatingBusinessWithNullPrimaryIdBusinessModify() throws Exception {
        AddressPayload newAddress = new AddressPayload("123", "new", "new", "new", "NZ", "123", "subur");
        Address newAddressObj = new Address("123", "new", "new", "new", "NZ", "123", "subur");
        payloadJson = "{" +
                "\"name\":\"" + "new" + "\"," +
                "\"description\":\"" + "new" + "\"," +
                "\"address\":" + newAddress + "," +
                "\"businessType\":\"" + "ACCOMMODATION AND FOOD SERVICES" + "\"," +
                "\"currencySymbol\":\"" + "$" + "\"," +
                "\"currencyCode\":\"" + "NZD" + "\"" +
                "}";
        Business someBusiness = new Business(
                user.getId(),
                "name",
                "some text",
                address,
                BusinessType.RETAIL_TRADE,
                LocalDateTime.of(LocalDate.of(2021, 2, 2), LocalTime.of(0, 0, 0)),
                user,
                "#",
                "BED"
        );
        Business newBusiness = new Business(
                user.getId(),
                "new",
                "new",
                newAddressObj,
                BusinessType.ACCOMMODATION_AND_FOOD_SERVICES,
                LocalDateTime.of(LocalDate.of(2021, 2, 2), LocalTime.of(0, 0, 0)),
                user,
                "$",
                "NZD");

        sessionToken = user.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);
        when(userRepository.findBySessionUUID(user.getSessionUUID())).thenReturn(Optional.ofNullable(user));
        when(userRepository.findById(13)).thenReturn(Optional.ofNullable(anotherUser));
        when(businessRepository.findBusinessById(someBusiness.getId())).thenReturn(Optional.ofNullable(someBusiness));

        response = mvc.perform(put(String.format("/businesses/%d", someBusiness.getId())).cookie(cookie)
                .content(payloadJson).contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(someBusiness).hasToString(newBusiness.toString());
    }

    /**
     * Testing that the name is updated when we include it in the payload. And that a OK status is returned.
     */
    @Test
    void updatingBusinessWithNewNameBusinessModify() throws Exception {
        AddressPayload newAddress = new AddressPayload("123", "new", "new", "new", "NZ", "123", "subur");
        Address newAddressObj = new Address("123", "new", "new", "new", "NZ", "123", "subur");
        payloadJson = "{" +
                "\"primaryAdministratorId\":" + 13 + "," +
                "\"name\":\"" + "new" + "\"," +
                "\"description\":\"" + "new" + "\"," +
                "\"address\":" + newAddress + "," +
                "\"businessType\":\"" + "ACCOMMODATION AND FOOD SERVICES" + "\"," +
                "\"currencySymbol\":\"" + "$" + "\"," +
                "\"currencyCode\":\"" + "NZD" + "\"" +
                "}";
        Business someBusiness = new Business(
                user.getId(),
                "name",
                "some text",
                address,
                BusinessType.RETAIL_TRADE,
                LocalDateTime.of(LocalDate.of(2021, 2, 2), LocalTime.of(0, 0, 0)),
                user,
                "#",
                "BED"
        );
        Business newBusiness = new Business(
                13,
                "new",
                "new",
                newAddressObj,
                BusinessType.ACCOMMODATION_AND_FOOD_SERVICES,
                LocalDateTime.of(LocalDate.of(2021, 2, 2), LocalTime.of(0, 0, 0)),
                anotherUser,
                "$",
                "NZD");

        sessionToken = user.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);
        when(userRepository.findBySessionUUID(user.getSessionUUID())).thenReturn(Optional.ofNullable(user));
        when(userRepository.findById(13)).thenReturn(Optional.ofNullable(anotherUser));
        when(businessRepository.findBusinessById(someBusiness.getId())).thenReturn(Optional.ofNullable(someBusiness));

        response = mvc.perform(put(String.format("/businesses/%d", someBusiness.getId())).cookie(cookie)
                .content(payloadJson).contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(someBusiness.getName()).hasToString(newBusiness.getName());
    }

    /**
     * Testing that a name must be provided in the modify payload. And that it returns a BAD_REQUEST.
     */
    @Test
    void updatingBusinessWithNullNameAndFailingAsItIsRequiredBusinessModify() throws Exception {
        AddressPayload newAddress = new AddressPayload("123", "new", "new", "new", "NZ", "123", "subur");
        Address newAddressObj = new Address("123", "new", "new", "new", "NZ", "123", "subur");
        payloadJson = "{" +
                "\"primaryAdministratorId\":" + 13 + "," +
                "\"description\":\"" + "new" + "\"," +
                "\"address\":" + newAddress + "," +
                "\"businessType\":\"" + "ACCOMMODATION AND FOOD SERVICES" + "\"," +
                "\"currencySymbol\":\"" + "$" + "\"," +
                "\"currencyCode\":\"" + "NZD" + "\"" +
                "}";
        Business someBusiness = new Business(
                user.getId(),
                "name",
                "some text",
                address,
                BusinessType.RETAIL_TRADE,
                LocalDateTime.of(LocalDate.of(2021, 2, 2), LocalTime.of(0, 0, 0)),
                user,
                "#",
                "BED"
        );

        sessionToken = user.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);
        when(userRepository.findBySessionUUID(user.getSessionUUID())).thenReturn(Optional.ofNullable(user));
        when(userRepository.findById(13)).thenReturn(Optional.ofNullable(anotherUser));
        when(businessRepository.findBusinessById(someBusiness.getId())).thenReturn(Optional.ofNullable(someBusiness));

        response = mvc.perform(put(String.format("/businesses/%d", someBusiness.getId())).cookie(cookie)
                .content(payloadJson).contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(someBusiness).hasToString(someBusiness.toString());
    }

    /**
     * Updating the business with a new description. Expecting an OK stats.
     */
    @Test
    void updatingBusinessWithNewDescriptionBusinessModify() throws Exception {
        AddressPayload newAddress = new AddressPayload("123", "new", "new", "new", "NZ", "123", "subur");
        Address newAddressObj = new Address("123", "new", "new", "new", "NZ", "123", "subur");
        payloadJson = "{" +
                "\"primaryAdministratorId\":" + 13 + "," +
                "\"name\":\"" + "new" + "\"," +
                "\"description\":\"" + "new" + "\"," +
                "\"address\":" + newAddress + "," +
                "\"businessType\":\"" + "ACCOMMODATION AND FOOD SERVICES" + "\"," +
                "\"currencySymbol\":\"" + "$" + "\"," +
                "\"currencyCode\":\"" + "NZD" + "\"" +
                "}";
        Business someBusiness = new Business(
                user.getId(),
                "name",
                "some text",
                address,
                BusinessType.RETAIL_TRADE,
                LocalDateTime.of(LocalDate.of(2021, 2, 2), LocalTime.of(0, 0, 0)),
                user,
                "#",
                "BED"
        );
        Business newBusiness = new Business(
                13,
                "new",
                "new",
                newAddressObj,
                BusinessType.ACCOMMODATION_AND_FOOD_SERVICES,
                LocalDateTime.of(LocalDate.of(2021, 2, 2), LocalTime.of(0, 0, 0)),
                anotherUser,
                "$",
                "NZD");

        sessionToken = user.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);
        when(userRepository.findBySessionUUID(user.getSessionUUID())).thenReturn(Optional.ofNullable(user));
        when(userRepository.findById(13)).thenReturn(Optional.ofNullable(anotherUser));
        when(businessRepository.findBusinessById(someBusiness.getId())).thenReturn(Optional.ofNullable(someBusiness));

        response = mvc.perform(put(String.format("/businesses/%d", someBusiness.getId())).cookie(cookie)
                .content(payloadJson).contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(someBusiness.getDescription()).hasToString(newBusiness.getDescription());
    }

    /**
     * Testing with a null description that it still updates the business. And that it returns an OK status.
     */
    @Test
    void updatingBusinessWithNullDescriptionBusinessModify() throws Exception {
        AddressPayload newAddress = new AddressPayload("123", "new", "new", "new", "NZ", "123", "subur");
        Address newAddressObj = new Address("123", "new", "new", "new", "NZ", "123", "subur");
        payloadJson = "{" +
                "\"primaryAdministratorId\":" + 13 + "," +
                "\"name\":\"" + "new" + "\"," +
                "\"address\":" + newAddress + "," +
                "\"businessType\":\"" + "ACCOMMODATION AND FOOD SERVICES" + "\"," +
                "\"currencySymbol\":\"" + "$" + "\"," +
                "\"currencyCode\":\"" + "NZD" + "\"" +
                "}";
        Business someBusiness = new Business(
                user.getId(),
                "name",
                "some text",
                address,
                BusinessType.RETAIL_TRADE,
                LocalDateTime.of(LocalDate.of(2021, 2, 2), LocalTime.of(0, 0, 0)),
                user,
                "#",
                "BED"
        );
        Business newBusiness = new Business(
                13,
                "new",
                "some text",
                newAddressObj,
                BusinessType.ACCOMMODATION_AND_FOOD_SERVICES,
                LocalDateTime.of(LocalDate.of(2021, 2, 2), LocalTime.of(0, 0, 0)),
                anotherUser,
                "$",
                "NZD");

        sessionToken = user.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);
        when(userRepository.findBySessionUUID(user.getSessionUUID())).thenReturn(Optional.ofNullable(user));
        when(userRepository.findById(13)).thenReturn(Optional.ofNullable(anotherUser));
        when(businessRepository.findBusinessById(someBusiness.getId())).thenReturn(Optional.ofNullable(someBusiness));

        response = mvc.perform(put(String.format("/businesses/%d", someBusiness.getId())).cookie(cookie)
                .content(payloadJson).contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(someBusiness).hasToString(newBusiness.toString());
    }

    /**
     * Testing that the address changes when we include it in the payload. And that it reuturns OK status.
     */
    @Test
    void updatingBusinessWithNewAddressBusinessModify() throws Exception {
        AddressPayload newAddress = new AddressPayload("123", "new", "new", "new", "NZ", "123", "subur");
        Address oldAddress = new Address("1", "old", "old", "old", "old", "old", "old");
        Address newAddressObj = new Address("123", "new", "new", "new", "NZ", "123", "subur");
        payloadJson = "{" +
                "\"primaryAdministratorId\":" + 13 + "," +
                "\"name\":\"" + "new" + "\"," +
                "\"description\":\"" + "new" + "\"," +
                "\"address\":" + newAddress + "," +
                "\"businessType\":\"" + "ACCOMMODATION AND FOOD SERVICES" + "\"," +
                "\"currencySymbol\":\"" + "$" + "\"," +
                "\"currencyCode\":\"" + "NZD" + "\"" +
                "}";
        Business someBusiness = new Business(
                user.getId(),
                "name",
                "some text",
                oldAddress,
                BusinessType.RETAIL_TRADE,
                LocalDateTime.of(LocalDate.of(2021, 2, 2), LocalTime.of(0, 0, 0)),
                user,
                "#",
                "BED"
        );
        Business newBusiness = new Business(
                13,
                "new",
                "new",
                newAddressObj,
                BusinessType.ACCOMMODATION_AND_FOOD_SERVICES,
                LocalDateTime.of(LocalDate.of(2021, 2, 2), LocalTime.of(0, 0, 0)),
                anotherUser,
                "$",
                "NZD");

        sessionToken = user.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);
        when(userRepository.findBySessionUUID(user.getSessionUUID())).thenReturn(Optional.ofNullable(user));
        when(userRepository.findById(13)).thenReturn(Optional.ofNullable(anotherUser));
        when(businessRepository.findBusinessById(someBusiness.getId())).thenReturn(Optional.ofNullable(someBusiness));

        response = mvc.perform(put(String.format("/businesses/%d", someBusiness.getId())).cookie(cookie)
                .content(payloadJson).contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(someBusiness.getAddress()).isEqualTo(newBusiness.getAddress());
    }

    /**
     * Testing that you require the address in the modify payload. And that it returns a BAD_REQUEST status.
     */
    @Test
    void updatingBusinessWithNullAddressAndFailingAsItIsRequiredBusinessModify() throws Exception {
        payloadJson = "{" +
                "\"primaryAdministratorId\":" + 13 + "," +
                "\"name\":\"" + "new" + "\"," +
                "\"description\":\"" + "new" + "\"," +
                "\"businessType\":\"" + "ACCOMMODATION AND FOOD SERVICES" + "\"," +
                "\"currencySymbol\":\"" + "$" + "\"," +
                "\"currencyCode\":\"" + "NZD" + "\"" +
                "}";
        Business someBusiness = new Business(
                user.getId(),
                "name",
                "some text",
                address,
                BusinessType.RETAIL_TRADE,
                LocalDateTime.of(LocalDate.of(2021, 2, 2), LocalTime.of(0, 0, 0)),
                user,
                "#",
                "BED"
        );


        sessionToken = user.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);
        when(userRepository.findBySessionUUID(user.getSessionUUID())).thenReturn(Optional.ofNullable(user));
        when(userRepository.findById(13)).thenReturn(Optional.ofNullable(anotherUser));
        when(businessRepository.findBusinessById(someBusiness.getId())).thenReturn(Optional.ofNullable(someBusiness));

        response = mvc.perform(put(String.format("/businesses/%d", someBusiness.getId())).cookie(cookie)
                .content(payloadJson).contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(someBusiness).hasToString(someBusiness.toString());
    }

    /**
     * When updating a business with a null business type it will fail to update and return a BAD_REQUEST status.
     */
    @Test
    void updatingBusinessWithNullBusinessTypeAndFailingAsItIsRequiredBusinessModify() throws Exception {
        AddressPayload newAddress = new AddressPayload("123", "new", "new", "new", "NZ", "123", "subur");
        Address newAddressObj = new Address("123", "new", "new", "new", "NZ", "123", "subur");
        payloadJson = "{" +
                "\"primaryAdministratorId\":" + 13 + "," +
                "\"name\":\"" + "new" + "\"," +
                "\"description\":\"" + "new" + "\"," +
                "\"address\":" + newAddress + "," +
                "\"currencySymbol\":\"" + "$" + "\"," +
                "\"currencyCode\":\"" + "NZD" + "\"" +
                "}";
        Business someBusiness = new Business(
                user.getId(),
                "name",
                "some text",
                address,
                BusinessType.RETAIL_TRADE,
                LocalDateTime.of(LocalDate.of(2021, 2, 2), LocalTime.of(0, 0, 0)),
                user,
                "#",
                "BED"
        );

        sessionToken = user.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);
        when(userRepository.findBySessionUUID(user.getSessionUUID())).thenReturn(Optional.ofNullable(user));
        when(userRepository.findById(13)).thenReturn(Optional.ofNullable(anotherUser));
        when(businessRepository.findBusinessById(someBusiness.getId())).thenReturn(Optional.ofNullable(someBusiness));

        response = mvc.perform(put(String.format("/businesses/%d", someBusiness.getId())).cookie(cookie)
                .content(payloadJson).contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(someBusiness).hasToString(someBusiness.toString());
    }

    /**
     * Testing that we can update the business type. And that it returns an OK status.
     */
    @Test
    void updatingBusinessWithNewBusinessTypeBusinessModify() throws Exception {
        AddressPayload newAddress = new AddressPayload("123", "new", "new", "new", "NZ", "123", "subur");
        Address newAddressObj = new Address("123", "new", "new", "new", "NZ", "123", "subur");
        payloadJson = "{" +
                "\"primaryAdministratorId\":" + 13 + "," +
                "\"name\":\"" + "new" + "\"," +
                "\"description\":\"" + "new" + "\"," +
                "\"address\":" + newAddress + "," +
                "\"businessType\":\"" + "ACCOMMODATION AND FOOD SERVICES" + "\"," +
                "\"currencySymbol\":\"" + "$" + "\"," +
                "\"currencyCode\":\"" + "NZD" + "\"" +
                "}";
        Business someBusiness = new Business(
                user.getId(),
                "name",
                "some text",
                address,
                BusinessType.RETAIL_TRADE,
                LocalDateTime.of(LocalDate.of(2021, 2, 2), LocalTime.of(0, 0, 0)),
                user,
                "#",
                "BED"
        );
        Business newBusiness = new Business(
                13,
                "new",
                "new",
                newAddressObj,
                BusinessType.ACCOMMODATION_AND_FOOD_SERVICES,
                LocalDateTime.of(LocalDate.of(2021, 2, 2), LocalTime.of(0, 0, 0)),
                anotherUser,
                "$",
                "NZD");

        sessionToken = user.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);
        when(userRepository.findBySessionUUID(user.getSessionUUID())).thenReturn(Optional.ofNullable(user));
        when(userRepository.findById(13)).thenReturn(Optional.ofNullable(anotherUser));
        when(businessRepository.findBusinessById(someBusiness.getId())).thenReturn(Optional.ofNullable(someBusiness));

        response = mvc.perform(put(String.format("/businesses/%d", someBusiness.getId())).cookie(cookie)
                .content(payloadJson).contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(someBusiness.getBusinessType()).isEqualTo(newBusiness.getBusinessType());
    }

    /**
     * Testing that providing a invalid business type does not update the business and returns a BAD_REQUEST error code.
     */
    @Test
    void updatingBusinessWithInvalidBusinessTypeBusinessModify() throws Exception {
        AddressPayload newAddress = new AddressPayload("123", "new", "new", "new", "NZ", "123", "subur");
        Address newAddressObj = new Address("123", "new", "new", "new", "NZ", "123", "subur");
        payloadJson = "{" +
                "\"primaryAdministratorId\":" + 13 + "," +
                "\"name\":\"" + "new" + "\"," +
                "\"description\":\"" + "new" + "\"," +
                "\"address\":" + newAddress + "," +
                "\"businessType\":\"" + "ACCOMMODATION AND SOMETHING NOT REAL!" + "\"," +
                "\"currencySymbol\":\"" + "$" + "\"," +
                "\"currencyCode\":\"" + "NZD" + "\"" +
                "}";
        Business someBusiness = new Business(
                user.getId(),
                "name",
                "some text",
                address,
                BusinessType.RETAIL_TRADE,
                LocalDateTime.of(LocalDate.of(2021, 2, 2), LocalTime.of(0, 0, 0)),
                user,
                "#",
                "BED"
        );

        sessionToken = user.getSessionUUID();
        Cookie cookie = new Cookie("JSESSIONID", sessionToken);
        when(userRepository.findBySessionUUID(user.getSessionUUID())).thenReturn(Optional.ofNullable(user));
        when(userRepository.findById(13)).thenReturn(Optional.ofNullable(anotherUser));
        when(businessRepository.findBusinessById(someBusiness.getId())).thenReturn(Optional.ofNullable(someBusiness));

        response = mvc.perform(put(String.format("/businesses/%d", someBusiness.getId())).cookie(cookie)
                .content(payloadJson).contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(someBusiness).hasToString(someBusiness.toString());
    }
}
