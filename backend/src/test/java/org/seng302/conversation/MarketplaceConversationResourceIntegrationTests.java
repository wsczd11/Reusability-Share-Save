package org.seng302.conversation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.seng302.Main;
import org.seng302.controller.MarketplaceConversationResource;
import org.seng302.model.*;
import org.seng302.model.enums.Role;
import org.seng302.model.enums.Section;
import org.seng302.model.repository.MarketplaceCardRepository;
import org.seng302.model.repository.MarketplaceConversationMessageRepository;
import org.seng302.model.repository.MarketplaceConversationRepository;
import org.seng302.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

/**
 * MarketplaceConversationResource test class
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = {Main.class})
@ActiveProfiles("test")
class MarketplaceConversationResourceIntegrationTests {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private MarketplaceCardRepository marketplaceCardRepository;

    @MockBean
    private MarketplaceConversationRepository marketplaceConversationRepository;

    @MockBean
    private MarketplaceConversationMessageRepository marketplaceConversationMessageRepository;

    private MockHttpServletResponse response;

    private User instigator;
    private User receiver;
    private User anotherUser;
    private User dgaa;
    private User gaa;

    private MarketplaceCard marketplaceCard;

    private Conversation conversation;
    private Message message;
    private String content;

    private String payloadJson;

    private final String messagePayloadJson = "{\"senderId\":%d," +
            "\"receiverId\":%d," +
            "\"marketplaceCardId\":%d," +
            "\"content\":\"%s\"," +
            "\"created\":\"%s\"}";


    // Conversation Deletion
    private User outsideUser;
    private Conversation conversationDelete;
    private MarketplaceCard marketplaceCardDelete;
    private Message messageDelete1;
    private Message messageDelete2;

    @BeforeEach
    public void setup() throws Exception {
        Address address = new Address(
                "3/24",
                "Ilam Road",
                "Christchurch",
                "Canterbury",
                "New Zealand",
                "90210",
                "Ilam"
        );

        instigator = new User(
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
        instigator.setId(1);
        instigator.setSessionUUID(User.generateSessionUUID());

        receiver = new User(
                "Abby",
                "Wyatt",
                "W",
                "Abby",
                "bio",
                "Abby@example.com",
                LocalDate.of(2020, Month.JANUARY, 1).minusYears(13),
                "1234567555",
                address,
                "Password123!",
                LocalDateTime.of(LocalDate.of(2020, Month.JANUARY, 1), LocalTime.of(0, 0)),
                Role.USER
        );
        receiver.setId(2);
        receiver.setSessionUUID(User.generateSessionUUID());

        anotherUser = new User(
                "John",
                "Jacobs",
                "A",
                "Jay",
                "bio",
                "john@example.com",
                LocalDate.of(2020, Month.JANUARY, 1).minusYears(13),
                "123456789",
                address,
                "Password123!",
                LocalDateTime.of(LocalDate.of(2020, Month.JANUARY, 1), LocalTime.of(0, 0)),
                Role.USER
        );
        anotherUser.setId(3);
        anotherUser.setSessionUUID(User.generateSessionUUID());

        dgaa = new User(
                "Admin",
                "Jacobs",
                "A",
                "admin",
                "bio",
                "admin@example.com",
                LocalDate.of(2020, Month.JANUARY, 1).minusYears(13),
                "1234567555",
                address,
                "Password123!",
                LocalDateTime.of(LocalDate.of(2020, Month.JANUARY, 1), LocalTime.of(0, 0)),
                Role.DEFAULTGLOBALAPPLICATIONADMIN
        );
        dgaa.setId(4);
        dgaa.setSessionUUID(User.generateSessionUUID());

        gaa = new User(
                "AnotherAdmin",
                "Jacobs",
                "A",
                "secondAdmin",
                "bio",
                "admin2@example.com",
                LocalDate.of(2020, Month.JANUARY, 1).minusYears(13),
                "1234567555",
                address,
                "Password123!",
                LocalDateTime.of(LocalDate.of(2020, Month.JANUARY, 1), LocalTime.of(0, 0)),
                Role.GLOBALAPPLICATIONADMIN
        );
        gaa.setId(5);
        gaa.setSessionUUID(User.generateSessionUUID());

        outsideUser = new User(
                "Alex",
                "Doe",
                "S",
                "Generic",
                "Biography",
                "test@email.com",
                LocalDate.of(2000, 2, 2),
                "0271316",
                address,
                "Password123!",
                LocalDateTime.of(LocalDate.of(2000, 2, 2),
                        LocalTime.of(0, 0)),
                Role.USER);
        outsideUser.setId(6);
        outsideUser.setSessionUUID(User.generateSessionUUID());

        marketplaceCard = new MarketplaceCard(
                instigator.getId(),
                instigator,
                Section.FORSALE,
                LocalDateTime.of(LocalDate.of(2021, Month.JANUARY, 1), LocalTime.of(0, 0)),
                "Hayley's Birthday",
                "Come join Hayley and help her celebrate her birthday!"
        );
        marketplaceCard.setId(1);

        marketplaceCardDelete = new MarketplaceCard(
                receiver.getId(),
                receiver,
                Section.FORSALE,
                LocalDateTime.now(),
                "Royal Gala Apples For Sale",
                "Fresh, wanting $3 a kg."
        );
        marketplaceCardDelete.setId(2);

        conversation = new Conversation(instigator, receiver, marketplaceCard);
        conversation.setId(1);
        conversation.setCreated(LocalDateTime.of(2021, 6, 1, 0, 0));

        conversationDelete = new Conversation(instigator, receiver, marketplaceCardDelete);
        conversationDelete.setId(2);

        content = "Hi Hayley, I want to buy some baked goods :)";

        message = new Message(conversation, instigator, content);
        message.setId(1);

        messageDelete1 = new Message(conversationDelete, instigator, "Can I please have 5kg?");
        messageDelete1.setId(2);

        messageDelete2 = new Message(conversationDelete, receiver, "You sure can!");
        messageDelete2.setId(3);

        this.mvc = MockMvcBuilders.standaloneSetup(
                   new MarketplaceConversationResource(userRepository, marketplaceCardRepository, marketplaceConversationRepository, marketplaceConversationMessageRepository))
                   .build();
    }

    // ---------------------------------------- Tests for GET /home/conversation ---------------------------------------

    /**
     * Tests that an UNAUTHORIZED status is received when no cookie is provided with the GET request.
     *
     * @throws Exception thrown if there's an error with the mock mvc methods.
     */
    @Test
    void cannotRetrieveConversationsWithNoCookie() throws Exception {
        // When
        response = mvc.perform(get("/home/conversation")).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    /**
     * Tests that an UNAUTHORIZED status is received when an invalid JSESSIONID is provided with the GET request.
     *
     * @throws Exception thrown if there's an error with the mock mvc methods.
     */
    @Test
    void cannotRetrieveConversationsWithInvalidCookie() throws Exception {
        // When
        response = mvc.perform(get("/home/conversation")
                        .cookie(new Cookie("JSESSIONID", "0"))).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    /**
     * Tests that an OK status and an empty list are received when trying to retrieve conversations and the user has no
     * conversations associated with them.
     *
     * @throws Exception thrown if there's an error with the mock mvc methods.
     */
    @Test
    void canRetrieveEmptyListWhenNoAssociatedConversationsExist() throws Exception {
        // Given
        given(userRepository.findBySessionUUID(instigator.getSessionUUID())).willReturn(Optional.ofNullable(instigator));

        // When
        when(marketplaceConversationRepository.findAllByInstigatorIdAndDeletedByInstigatorOrReceiverIdAndDeletedByReceiver_OrderByCreatedDesc(instigator.getId(), false, instigator.getId(), false)).thenReturn(List.of());
        response = mvc.perform(get("/home/conversation")
                .cookie(new Cookie("JSESSIONID", instigator.getSessionUUID()))).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("[]");
    }

    /**
     * Tests that an OK status and a list of conversations are received when trying to retrieve conversations and the user has a
     * conversation associated with them.
     *
     * @throws Exception thrown if there's an error with the mock mvc methods.
     */
    @Test
    void canRetrieveListOfConversationsWhenAssociatedConversationsExist() throws Exception {
        // Given
        given(userRepository.findBySessionUUID(instigator.getSessionUUID())).willReturn(Optional.ofNullable(instigator));

        // When
        when(marketplaceConversationRepository.findAllByInstigatorIdAndDeletedByInstigatorOrReceiverIdAndDeletedByReceiver_OrderByCreatedDesc(instigator.getId(), false, instigator.getId(), false)).thenReturn(List.of(conversation));
        response = mvc.perform(get("/home/conversation")
                .cookie(new Cookie("JSESSIONID", instigator.getSessionUUID()))).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("[" + conversation.toConversationPayload().toString() + "]");
    }

    // --------------------------------- Tests for POST /home/conversation/{conversationId} ----------------------------

    /**
     * Tests that a UNAUTHORIZED (401) status is received when sending a valid marketplace conversation message
     * to the /home/conversation/{conversationId} API endpoint when the user is not logged in.
     * Conversation already exists and message does not exist.
     *
     * @throws Exception thrown if there is an error when checking that the users is logged in.
     */
    @Test
    void givenNoCookie_WhenCreateMessage_ThenReceiveUnauthorizedStatus() throws Exception {
        // given
        given(userRepository.findById(instigator.getId())).willReturn(Optional.ofNullable(instigator));
        String nonExistingSessionUUID = User.generateSessionUUID();
        given(userRepository.findBySessionUUID(nonExistingSessionUUID)).willReturn(Optional.empty());
        payloadJson = String.format(messagePayloadJson, instigator.getId(), receiver.getId(),
                marketplaceCard.getId(), content, LocalDateTime.of(LocalDate.of(2021, 2, 2),
                        LocalTime.of(0, 0)));

        // when
        response = mvc.perform(post(String.format("/home/conversation/%d", conversation.getId()))
                        .cookie(new Cookie("JSESSIONID", nonExistingSessionUUID))
                        .contentType(MediaType.APPLICATION_JSON).content(payloadJson))
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    /**
     * Tests that a BAD_REQUEST (400) status is received when sending a marketplace conversation message
     * to the /home/conversation/{conversationId} API endpoint and the receiver id does not correspond
     * to an existing user.
     *
     * @throws Exception thrown if there is an error when checking that the receiving user exists.
     */
    @Test
    void givenValidCookieAndNonExistentReceiverId_WhenCreateMessage_ThenReceiveBadRequestStatus() throws Exception {
        // given
        given(userRepository.findBySessionUUID(instigator.getSessionUUID())).willReturn(Optional.ofNullable(instigator));
        payloadJson = String.format(messagePayloadJson, instigator.getId(), receiver.getId(),
                marketplaceCard.getId(), content, LocalDateTime.of(LocalDate.of(2021, 2, 2),
                        LocalTime.of(0, 0)));
        given(marketplaceConversationMessageRepository.findMessageById(message.getId())).willReturn(Optional.ofNullable(message));

        Integer nonExistentUserId = 1000;
        given(userRepository.findById(nonExistentUserId)).willReturn(Optional.empty());

        // when
        response = mvc.perform(post(String.format("/home/conversation/%d", conversation.getId()))
                        .cookie(new Cookie("JSESSIONID", instigator.getSessionUUID()))
                        .contentType(MediaType.APPLICATION_JSON).content(payloadJson))
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getErrorMessage()).isEqualTo("Invalid Conversation - invalid receiver id");
    }

    /**
     * Tests that a BAD_REQUEST (400) status is received when sending a marketplace conversation message
     * to the /home/conversation/{conversationId} API endpoint and the receiver id does not correspond
     * to an existing marketplace card.
     *
     * @throws Exception thrown if there is an error when checking that the card exists.
     */
    @Test
    void givenValidCookieAndValidReceiverIdAndInvalidMarketplaceCardId_WhenCreateMessage_ThenReceiveBadRequestStatus() throws Exception {
        // given
        given(userRepository.findBySessionUUID(instigator.getSessionUUID())).willReturn(Optional.ofNullable(instigator));
        given(userRepository.findById(instigator.getId())).willReturn(Optional.ofNullable(instigator));
        payloadJson = String.format(messagePayloadJson, instigator.getId(), receiver.getId(),
                marketplaceCard.getId(), content, LocalDateTime.of(LocalDate.of(2021, 2, 2),
                        LocalTime.of(0, 0)));
        given(marketplaceConversationMessageRepository.findMessageById(message.getId())).willReturn(Optional.ofNullable(message));
        given(userRepository.findById(receiver.getId())).willReturn(Optional.ofNullable(receiver));
        given(marketplaceCardRepository.findById(marketplaceCard.getId())).willReturn(Optional.empty());

        // when
        response = mvc.perform(post(String.format("/home/conversation/%d", conversation.getId()))
                        .cookie(new Cookie("JSESSIONID", instigator.getSessionUUID()))
                        .contentType(MediaType.APPLICATION_JSON).content(payloadJson))
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getErrorMessage()).isEqualTo("Invalid Conversation - invalid card id");
    }

    /**
     * Tests that a NOT_ACCEPTABLE (406) status is received when sending a marketplace conversation message
     * to the /home/conversation/{conversationId} API endpoint and the conversation id does not correspond
     * to an existing marketplace conversation, and the receiver and card exist.
     *
     * @throws Exception thrown if there is an error when checking that the conversation exists.
     */
    @Test
    void givenValidCookieAndValidReceiverIdAndValidCardIdAndNonExistentConversationId_WhenCreateMessage_ThenReceiveBadRequestStatus() throws Exception {
        // given
        given(userRepository.findBySessionUUID(instigator.getSessionUUID())).willReturn(Optional.ofNullable(instigator));
        given(userRepository.findById(instigator.getId())).willReturn(Optional.ofNullable(instigator));
        payloadJson = String.format(messagePayloadJson, instigator.getId(), receiver.getId(),
                marketplaceCard.getId(), content, LocalDateTime.of(LocalDate.of(2021, 2, 2),
                        LocalTime.of(0, 0)));
        given(marketplaceConversationMessageRepository.findMessageById(message.getId())).willReturn(Optional.empty());
        given(userRepository.findById(receiver.getId())).willReturn(Optional.ofNullable(receiver));
        given(marketplaceCardRepository.findById(marketplaceCard.getId())).willReturn(Optional.ofNullable(marketplaceCard));
        given(marketplaceConversationRepository.findConversationById(conversation.getId())).willReturn(Optional.empty());

        // when
        response = mvc.perform(post(String.format("/home/conversation/%d", conversation.getId()))
                        .cookie(new Cookie("JSESSIONID", instigator.getSessionUUID()))
                        .contentType(MediaType.APPLICATION_JSON).content(payloadJson))
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_ACCEPTABLE.value());
        assertThat(response.getErrorMessage()).isEqualTo("Invalid Conversation - conversation id does not exist");
    }


    /**
     * Tests that a CREATED (201) status is received when sending a marketplace conversation message
     * to the /home/conversation/{conversationId} API endpoint and the receiver id corresponds
     * to an existing marketplace conversation, the card id corresponds to an existing marketplace card and the
     * conversation id is not provided (and thus the conversation is created).
     *
     * @throws Exception thrown if there is an error when checking that the conversation exists.
     */
    @Test
    void givenValidDataAndConversationIdIsNotProvided_WhenCreateMessage_ThenReceiveCreatedStatus() throws Exception {
        // given
        given(userRepository.findBySessionUUID(instigator.getSessionUUID())).willReturn(Optional.ofNullable(instigator));
        given(userRepository.findById(instigator.getId())).willReturn(Optional.ofNullable(instigator));
        payloadJson = String.format(messagePayloadJson, instigator.getId(), receiver.getId(),
                marketplaceCard.getId(), content, LocalDateTime.of(LocalDate.of(2021, 2, 2),
                        LocalTime.of(0, 0)));
        given(userRepository.findById(receiver.getId())).willReturn(Optional.ofNullable(receiver));
        given(marketplaceCardRepository.findById(marketplaceCard.getId())).willReturn(Optional.ofNullable(marketplaceCard));
        given(marketplaceConversationRepository.findById(conversation.getId())).willReturn(Optional.empty());

        // when
        response = mvc.perform(post("/home/conversation")
                        .cookie(new Cookie("JSESSIONID", instigator.getSessionUUID()))
                        .contentType(MediaType.APPLICATION_JSON).content(payloadJson))
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
    }

    /**
     * Tests that a CREATED (201) status is received when sending a marketplace conversation message
     * to the /home/conversation/{conversationId} API endpoint and the receiver id corresponds
     * to an existing marketplace conversation, the card id corresponds to an existing marketplace card and the
     * conversation id is an existing conversation.
     *
     * @throws Exception thrown if there is an error when checking that the conversation exists.
     */
    @Test
    void givenValidDataAndConversationIdExists_WhenCreateMessage_ThenReceiveCreatedStatus() throws Exception {
        // given
        given(userRepository.findBySessionUUID(instigator.getSessionUUID())).willReturn(Optional.ofNullable(instigator));
        given(userRepository.findById(instigator.getId())).willReturn(Optional.ofNullable(instigator));
        payloadJson = String.format(messagePayloadJson, instigator.getId(), receiver.getId(),
                marketplaceCard.getId(), content, LocalDateTime.of(LocalDate.of(2021, 2, 2),
                        LocalTime.of(0, 0)));
        given(userRepository.findById(receiver.getId())).willReturn(Optional.ofNullable(receiver));
        given(marketplaceCardRepository.findById(marketplaceCard.getId())).willReturn(Optional.ofNullable(marketplaceCard));
        given(marketplaceConversationRepository.findConversationById(conversation.getId())).willReturn(Optional.ofNullable(conversation));

        // when
        response = mvc.perform(post(String.format("/home/conversation/%d", conversation.getId()))
                        .cookie(new Cookie("JSESSIONID", instigator.getSessionUUID()))
                        .contentType(MediaType.APPLICATION_JSON).content(payloadJson))
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
    }

    /**
     * Tests that the read by instigator/receiver are set correctly when sending a marketplace conversation message
     * to the /home/conversation/{conversationId} API endpoint as the instigator.
     *
     * @throws Exception exception
     */
    @Test
    void testReadBy_WhenInstigatorCreateMessage() throws Exception {
        // given
        given(userRepository.findBySessionUUID(instigator.getSessionUUID())).willReturn(Optional.ofNullable(instigator));
        given(userRepository.findById(instigator.getId())).willReturn(Optional.ofNullable(instigator));

        payloadJson = String.format(messagePayloadJson, instigator.getId(), receiver.getId(),
                marketplaceCard.getId(), content, LocalDateTime.of(LocalDate.of(2021, 2, 2),
                        LocalTime.of(0, 0)));

        given(userRepository.findById(receiver.getId())).willReturn(Optional.ofNullable(receiver));

        given(marketplaceCardRepository.findById(marketplaceCard.getId())).willReturn(Optional.ofNullable(marketplaceCard));
        given(marketplaceConversationRepository.findConversationById(conversation.getId())).willReturn(Optional.ofNullable(conversation));

        // when
        response = mvc.perform(post(String.format("/home/conversation/%d", conversation.getId()))
                .cookie(new Cookie("JSESSIONID", instigator.getSessionUUID()))
                .contentType(MediaType.APPLICATION_JSON).content(payloadJson))
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(conversation.getReadByReceiver()).isFalse();
        assertThat(conversation.getReadByInstigator()).isTrue();
    }

    /**
     * Tests that the read by instigator/receiver are set correctly when sending a marketplace conversation message
     * to the /home/conversation/{conversationId} API endpoint as the receiver.
     *
     * @throws Exception exception
     */
    @Test
    void testReadBy_WhenReceiverCreateMessage() throws Exception {
        // given
        given(userRepository.findById(instigator.getId())).willReturn(Optional.ofNullable(instigator));

        payloadJson = String.format(messagePayloadJson, instigator.getId(), receiver.getId(),
                marketplaceCard.getId(), content, LocalDateTime.of(LocalDate.of(2021, 2, 2),
                        LocalTime.of(0, 0)));

        given(userRepository.findBySessionUUID(receiver.getSessionUUID())).willReturn(Optional.ofNullable(receiver));
        given(userRepository.findById(receiver.getId())).willReturn(Optional.ofNullable(receiver));

        given(marketplaceCardRepository.findById(marketplaceCard.getId())).willReturn(Optional.ofNullable(marketplaceCard));
        given(marketplaceConversationRepository.findConversationById(conversation.getId())).willReturn(Optional.ofNullable(conversation));

        // when
        response = mvc.perform(post(String.format("/home/conversation/%d", conversation.getId()))
                .cookie(new Cookie("JSESSIONID", receiver.getSessionUUID()))
                .contentType(MediaType.APPLICATION_JSON).content(payloadJson))
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(conversation.getReadByReceiver()).isTrue();
        assertThat(conversation.getReadByInstigator()).isFalse();
    }

    /**
     * Tests that an OK status and a list of messages are received when trying to retrieve messages from an existing conversation.
     *
     * @throws Exception thrown if there's an error with the mock mvc methods.
     */
    @Test
    void canRetrieveListOfMessagesWhenConversationExists() throws Exception {
        // Given
        given(userRepository.findBySessionUUID(instigator.getSessionUUID())).willReturn(Optional.ofNullable(instigator));

        // When
        when(marketplaceConversationRepository.findConversationById(1)).thenReturn(Optional.of(conversation));
        when(marketplaceConversationMessageRepository.findAllByConversationId_OrderByCreatedDesc(1)).thenReturn(List.of(message));
        response = mvc.perform(get("/home/conversation/" + conversation.getId() + "/messages")
                .cookie(new Cookie("JSESSIONID", instigator.getSessionUUID()))).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("[" + message.toMessagePayload().toString() + "]");
    }

    /**
     * Tests the read by receiver/instigator is set correctly when receiver views the conversation
     *
     * @throws Exception thrown if there's an error with the mock mvc methods.
     */
    @Test
    void testReadBy_WhenReceiverGetConversation() throws Exception {
        // Given
        given(userRepository.findBySessionUUID(instigator.getSessionUUID())).willReturn(Optional.ofNullable(instigator));

        conversation.setReadByInstigator(false);
        conversation.setReadByReceiver(false);

        // When
        when(marketplaceConversationRepository.findConversationById(1)).thenReturn(Optional.of(conversation));
        when(marketplaceConversationMessageRepository.findAllByConversationId_OrderByCreatedDesc(1)).thenReturn(List.of(message));
        response = mvc.perform(get("/home/conversation/" + conversation.getId() + "/messages")
                .cookie(new Cookie("JSESSIONID", instigator.getSessionUUID()))).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("[" + message.toMessagePayload().toString() + "]");

        assertThat(conversation.getReadByInstigator()).isTrue();
        assertThat(conversation.getReadByReceiver()).isFalse();
    }

    /**
     * Tests the read by receiver/instigator is set correctly when instigator views the conversation
     *
     * @throws Exception thrown if there's an error with the mock mvc methods.
     */
    @Test
    void testReadBy_WhenInstigatorGetConversation() throws Exception {
        // Given
        given(userRepository.findBySessionUUID(receiver.getSessionUUID())).willReturn(Optional.ofNullable(receiver));

        conversation.setReadByInstigator(false);
        conversation.setReadByReceiver(false);

        // When
        when(marketplaceConversationRepository.findConversationById(1)).thenReturn(Optional.of(conversation));
        when(marketplaceConversationMessageRepository.findAllByConversationId_OrderByCreatedDesc(1)).thenReturn(List.of(message));
        response = mvc.perform(get("/home/conversation/" + conversation.getId() + "/messages")
                .cookie(new Cookie("JSESSIONID", receiver.getSessionUUID()))).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("[" + message.toMessagePayload().toString() + "]");

        assertThat(conversation.getReadByInstigator()).isFalse();
        assertThat(conversation.getReadByReceiver()).isTrue();
    }

    /**
     * Tests that an UNAUTHORIZED status is received when an invalid JSESSIONID is provided with the GET request for messages.
     *
     * @throws Exception thrown if there's an error with the mock mvc methods.
     */
    @Test
    void cannotRetrieveMessagesWithInvalidCookie() throws Exception {

        // When
        when(marketplaceConversationRepository.findConversationById(1)).thenReturn(Optional.of(conversation));
        when(marketplaceConversationMessageRepository.findAllByConversationId_OrderByCreatedDesc(1)).thenReturn(List.of(message));
        response = mvc.perform(get("/home/conversation/" + conversation.getId() + "/messages")
                .cookie(new Cookie("JSESSIONID", instigator.getSessionUUID()))).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    /**
     * Tests that a NOT FOUND status is received when getting messages for a conversation that does not exist.
     *
     * @throws Exception thrown if there's an error with the mock mvc methods.
     */
    @Test
    void cannotRetrieveMessagesWithInvalidConversationId() throws Exception {
        // Given
        given(userRepository.findBySessionUUID(instigator.getSessionUUID())).willReturn(Optional.ofNullable(instigator));

        // When
        response = mvc.perform(get("/home/conversation/" + conversation.getId() + "/messages")
                .cookie(new Cookie("JSESSIONID", instigator.getSessionUUID()))).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_ACCEPTABLE.value());
    }

    /**
     * Tests that a FORBIDDEN status is received when trying to access messages of a conversation that the user is not a part of.
     *
     * @throws Exception thrown if there's an error with the mock mvc methods.
     */
    @Test
    void cannotRetrieveListOfMessagesWhenNotMyConversation() throws Exception {
        // Given
        given(userRepository.findBySessionUUID(anotherUser.getSessionUUID())).willReturn(Optional.ofNullable(anotherUser));

        // When
        when(marketplaceConversationRepository.findConversationById(1)).thenReturn(Optional.of(conversation));
        when(marketplaceConversationMessageRepository.findAllByConversationId_OrderByCreatedDesc(1)).thenReturn(List.of(message));
        response = mvc.perform(get("/home/conversation/" + conversation.getId() + "/messages")
                .cookie(new Cookie("JSESSIONID", anotherUser.getSessionUUID()))).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
    }

    /**
     * Tests that an OK status and a list of messages are received when trying to retrieve messages from a conversation
     * the user is not a part of when acting as a DGAA. (DGAAs can view all messages)
     *
     * @throws Exception thrown if there's an error with the mock mvc methods.
     */
    @Test
    void canRetrieveListOfMessagesWhenNotMyConversationWhileActingAsDGAA() throws Exception {
        // Given
        given(userRepository.findBySessionUUID(dgaa.getSessionUUID())).willReturn(Optional.ofNullable(dgaa));

        // When
        when(marketplaceConversationRepository.findConversationById(1)).thenReturn(Optional.of(conversation));
        when(marketplaceConversationMessageRepository.findAllByConversationId_OrderByCreatedDesc(1)).thenReturn(List.of(message));
        response = mvc.perform(get("/home/conversation/" + conversation.getId() + "/messages")
                .cookie(new Cookie("JSESSIONID", dgaa.getSessionUUID()))).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("[" + message.toMessagePayload().toString() + "]");
    }

    /**
     * Tests that an OK status and a list of messages are received when trying to retrieve messages from a conversation
     * the user is not a part of when acting as a GAA. (GAAs can view all messages)
     *
     * @throws Exception thrown if there's an error with the mock mvc methods.
     */
    @Test
    void canRetrieveListOfMessagesWhenNotMyConversationWhileActingAsGAA() throws Exception {
        // Given
        given(userRepository.findBySessionUUID(gaa.getSessionUUID())).willReturn(Optional.ofNullable(gaa));

        // When
        when(marketplaceConversationRepository.findConversationById(1)).thenReturn(Optional.of(conversation));
        when(marketplaceConversationMessageRepository.findAllByConversationId_OrderByCreatedDesc(1)).thenReturn(List.of(message));
        response = mvc.perform(get("/home/conversation/" + conversation.getId() + "/messages")
                .cookie(new Cookie("JSESSIONID", gaa.getSessionUUID()))).andReturn().getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("[" + message.toMessagePayload().toString() + "]");
    }


    // ------------------------------ Tests for DELETE /users/conversation/{conversationId} ----------------------------

    /**
     * Test that an UNAUTHORIZED status is received when a non-logged in user tries to delete a conversation.
     * @throws Exception thrown if there is an error when deleting a conversation.
     */
    @Test
    void canNotDeleteConversationWhenUserNotLoggedIn() throws Exception {
        // Given
        given(userRepository.findBySessionUUID(instigator.getSessionUUID())).willReturn(Optional.empty());

        // When
        response = mvc.perform(delete(String.format("/users/conversation/%d", conversationDelete.getId())))
                .andReturn()
                .getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(response.getErrorMessage()).isEqualTo("Access token is missing or invalid");
    }

    /**
     * Test that an NOT_ACCEPTABLE status is received when the conversation does not exist.
     * @throws Exception thrown if there is an error when deleting a conversation.
     */
    @Test
    void canNotDeleteConversationWhenItDoesNotExist() throws Exception {
        // Given
        given(userRepository.findBySessionUUID(instigator.getSessionUUID())).willReturn(Optional.ofNullable(instigator));
        given(marketplaceConversationRepository.findById(conversationDelete.getId()))
                .willReturn(Optional.empty());

        // When
        response = mvc.perform(delete(String.format("/users/conversation/%d", conversationDelete.getId()))
                .cookie(new Cookie("JSESSIONID", instigator.getSessionUUID())))
                .andReturn()
                .getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_ACCEPTABLE.value());
        assertThat(response.getErrorMessage()).isEqualTo("The requested route does exist (so not a 404) but some part of the request is not acceptable, " +
                "for example trying to access a resource by an ID that does not exist.");
    }

    /**
     * Test that a FORBIDDEN status is received when the conversation exists but the user is trying to
     * delete a conversation they are not a member of and they are not a GAA or DGAA.
     * @throws Exception thrown if there is an error when deleting a conversation.
     */
    @Test
    void canNotDeleteConversationWhenItExistsButNotAMemberGAAOrDGAA() throws Exception {
        // Given
        given(userRepository.findBySessionUUID(outsideUser.getSessionUUID())).willReturn(Optional.ofNullable(outsideUser));
        given(marketplaceConversationRepository.findById(conversationDelete.getId()))
                .willReturn(Optional.ofNullable(conversationDelete));

        // When
        response = mvc.perform(delete(String.format("/users/conversation/%d", conversationDelete.getId()))
                .cookie(new Cookie("JSESSIONID", outsideUser.getSessionUUID())))
                .andReturn()
                .getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
        assertThat(response.getErrorMessage()).isEqualTo("Invalid permissions to delete conversation");
    }

    /**
     * Test that an OK status is received when the conversation exists and a DGAA tries to
     * delete a conversation for other users.
     * @throws Exception thrown if there is an error when deleting a conversation.
     */
    @Test
    void canDeleteConversationWhenItExistsAndCurrentUserIsADGAA() throws Exception {
        // Given
        given(userRepository.findBySessionUUID(dgaa.getSessionUUID())).willReturn(Optional.ofNullable(dgaa));
        given(marketplaceConversationRepository.findById(conversationDelete.getId()))
                .willReturn(Optional.ofNullable(conversationDelete));

        // When
        response = mvc.perform(delete(String.format("/users/conversation/%d", conversationDelete.getId()))
                .cookie(new Cookie("JSESSIONID", dgaa.getSessionUUID())))
                .andReturn()
                .getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    /**
     * Test that an OK status is received when the conversation exists and the instigator tries to
     * delete a conversation when the receiver has already removed themself from the conversation.
     * @throws Exception thrown if there is an error when deleting a conversation.
     */
    @Test
    void canDeleteConversationWhenReceiverHasAlreadyRemovedThemself() throws Exception {
        // Given
        given(userRepository.findBySessionUUID(instigator.getSessionUUID())).willReturn(Optional.ofNullable(instigator));
        Conversation conversationWithReceiverRemoved = new Conversation(
                conversationDelete.getInstigator(),
                conversationDelete.getReceiver(),
                conversationDelete.getMarketplaceCard());
        conversationWithReceiverRemoved.setId(3);
        conversationWithReceiverRemoved.setDeletedByReceiver(true);
        given(marketplaceConversationRepository.findById(conversationWithReceiverRemoved.getId()))
                .willReturn(Optional.of(conversationWithReceiverRemoved));

        // When
        response = mvc.perform(delete(String.format("/users/conversation/%d", conversationWithReceiverRemoved.getId()))
                .cookie(new Cookie("JSESSIONID", instigator.getSessionUUID())))
                .andReturn()
                .getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    /**
     * Test that an OK status is received when the conversation exists and the instigator tries to
     * remove themself from the conversation.
     * @throws Exception thrown if there is an error when deleting a conversation.
     */
    @Test
    void canRemoveInstigatorFromConversation() throws Exception {
        // Given
        given(userRepository.findBySessionUUID(instigator.getSessionUUID())).willReturn(Optional.ofNullable(instigator));
        given(marketplaceConversationRepository.findById(conversationDelete.getId()))
                .willReturn(Optional.ofNullable(conversationDelete));

        // When
        response = mvc.perform(delete(String.format("/users/conversation/%d", conversationDelete.getId()))
                .cookie(new Cookie("JSESSIONID", instigator.getSessionUUID())))
                .andReturn()
                .getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    /**
     * Test that an OK status is received when the conversation exists and the receiver tries to
     * remove themself from the conversation.
     * @throws Exception thrown if there is an error when deleting a conversation.
     */
    @Test
    void canRemoveReceiverFromConversation() throws Exception {
        // Given
        given(userRepository.findBySessionUUID(receiver.getSessionUUID())).willReturn(Optional.ofNullable(receiver));
        given(marketplaceConversationRepository.findById(conversationDelete.getId()))
                .willReturn(Optional.ofNullable(conversationDelete));

        // When
        response = mvc.perform(delete(String.format("/users/conversation/%d", conversationDelete.getId()))
                .cookie(new Cookie("JSESSIONID", receiver.getSessionUUID())))
                .andReturn()
                .getResponse();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

}
