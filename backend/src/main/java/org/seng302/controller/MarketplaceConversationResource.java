package org.seng302.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seng302.Authorization;
import org.seng302.exceptions.IllegalMessageContentException;
import org.seng302.model.*;
import org.seng302.model.repository.*;
import org.seng302.view.incoming.MarketplaceConversationMessagePayload;
import org.seng302.view.outgoing.MarketplaceConversationIdPayload;
import org.seng302.view.outgoing.ConversationPayload;
import org.seng302.view.outgoing.MessagePayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;
import java.util.Optional;

/**
 * Controller class for marketplace conversation. This class includes:
 * POST "/home/conversation/{conversationId}" endpoint used for creating a message in a conversation. If no conversation
 *                                       exists, conversation is created and its ID is returned.
 * GET "/home/conversation" endpoint used for retrieving all the conversations related to a given user. An empty array
 *                          is returned if no conversations exist for the user.
 * DELETE "/users/conversation/{conversationId} endpoint used to delete a marketplace conversation.
 */
@RestController
public class MarketplaceConversationResource {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MarketplaceCardRepository marketplaceCardRepository;

    @Autowired
    private MarketplaceConversationRepository marketplaceConversationRepository;

    @Autowired
    private MarketplaceConversationMessageRepository marketplaceConversationMessageRepository;

    // the name of the cookie used for authentication.
    private static final String COOKIE_AUTH = "JSESSIONID";
    // the error message to be logged when requested route does not exist.
    private static final String LOGGER_ERROR_REQUESTED_ROUTE = "Requested route does exist, but some part of the request is not acceptable";
    // the message to be returned when there is a 406 error.
    private static final String HTTP_NOT_ACCEPTABLE_MESSAGE = "The requested route does exist (so not a 404) but some part of the request is not acceptable, " +
            "for example trying to access a resource by an ID that does not exist.";

    /**
     * MarketplaceConversationResource constructor
     * @param userRepository The user repository
     * @param marketplaceCardRepository The marketplace card repository
     * @param marketplaceConversationRepository The marketplace conversation repository
     * @param marketplaceConversationMessageRepository The marketplace conversation message repository
     */
    public MarketplaceConversationResource(UserRepository userRepository,
                                           MarketplaceCardRepository marketplaceCardRepository,
                                           MarketplaceConversationRepository marketplaceConversationRepository,
                                           MarketplaceConversationMessageRepository marketplaceConversationMessageRepository
    ) {
        this.userRepository = userRepository;
        this.marketplaceCardRepository = marketplaceCardRepository;
        this.marketplaceConversationRepository = marketplaceConversationRepository;
        this.marketplaceConversationMessageRepository = marketplaceConversationMessageRepository;
    }

    private static final Logger logger = LogManager.getLogger(MarketplaceConversationResource.class.getName());
    private Conversation conversation;

    /**
     * Create a message in a marketplace conversation.
     * If the conversation does not exist, a conversation is created first.
     *
     * @param sessionToken The token used to identify the user.
     * @param conversationId The ID of the conversation that the message is being added to. Null if new conversation.
     * @param marketplaceConversationMessagePayload contains new message info.
     * @return marketplace conversation ID.
     */
    @PostMapping({"/home/conversation/{conversationId}", "/home/conversation"})
    public ResponseEntity<MarketplaceConversationIdPayload> createMarketplaceConversationMessage(
            @CookieValue(value = "JSESSIONID", required = false) String sessionToken,
            @RequestBody MarketplaceConversationMessagePayload marketplaceConversationMessagePayload,
            @PathVariable(required = false) Integer conversationId
    ) {

        //401
        User sender = Authorization.getUserVerifySession(sessionToken, userRepository);

        //400
        // check if receiver and marketplace card exists

        Integer receiverId = marketplaceConversationMessagePayload.getReceiverId();
        Optional<User> storedReceiver = userRepository.findById(receiverId);

        if (storedReceiver.isEmpty()) {
            logger.error("Invalid Conversation - invalid receiver id");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Conversation - invalid receiver id"
            );
        }

        Integer cardId = marketplaceConversationMessagePayload.getMarketplaceCardId();
        Optional<MarketplaceCard> storedCard = marketplaceCardRepository.findById(cardId);

        if (storedCard.isEmpty()) {
            logger.error("Invalid Conversation - invalid card id ");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Conversation - invalid card id"
            );
        }


        // if conversationId is null, then create conversation
        if (conversationId == null) {

            //201
            conversation = createConversation(sender, storedReceiver.get(), storedCard.get());
            createMessage(conversation, sender, marketplaceConversationMessagePayload.getContent());
            return ResponseEntity.status(HttpStatus.CREATED).body(new MarketplaceConversationIdPayload(conversation.getId()));

        } else {

            // check if conversation exists if conversationId is not null
            Optional<Conversation> storedConversation = marketplaceConversationRepository.findConversationById(conversationId);
            if (storedConversation.isEmpty()) {
                //406
                logger.error("Invalid Conversation - conversation id does not exist.");
                throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Invalid Conversation - conversation id does not exist"
                );
            } else {
                //201
                createMessage(storedConversation.get(), sender, marketplaceConversationMessagePayload.getContent());
                return ResponseEntity.status(HttpStatus.CREATED).body(new MarketplaceConversationIdPayload(storedConversation.get().getId()));
            }
        }
    }

    /**
     * Creates a conversation.
     * @param sender The user sending the message.
     * @param receiver The user receiving the message.
     * @param card The card that the sender is messaging about.
     * @return conversation created.
     */
    private Conversation createConversation(User sender, User receiver, MarketplaceCard card) {
        try {
            conversation = new Conversation(sender, receiver, card);
            marketplaceConversationRepository.save(conversation);
            logger.info("Successful Conversation Creation - {}", conversation);
            return conversation;
        } catch (IllegalArgumentException e) {
            //400
            logger.error("Conversation Creation Failure - {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage()
            );
        }
    }

    /**
     * Creates a message.
     * @param conversation The conversation that the message relates to.
     * @param sender The user sending the message.
     * @param content The message content.
     */
    private void createMessage(Conversation conversation, User sender, String content) {
        try {
            Message message = new Message(conversation, sender, content);
            marketplaceConversationMessageRepository.save(message);
            logger.info("Successful Message Creation - {}", message);
        } catch (IllegalArgumentException | IllegalMessageContentException e) {
            //400
            logger.error("Message Creation Failure - {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage()
            );
        }

        if (conversation.getReceiver().getId() == sender.getId()) {
            conversation.setReadByInstigator(false);
            conversation.setReadByReceiver(true);
        } else if (conversation.getInstigator().getId() == sender.getId()) {
            conversation.setReadByReceiver(false);
            conversation.setReadByInstigator(true);
        }

        marketplaceConversationRepository.save(conversation);
    }

    /**
     * Retrieve all conversations related to a given user.
     * If there are no conversations, then an empty array is returned.
     * No user ID is provided in the URL as the JSESSIONID is used to determine which user is requesting their conversations.
     *
     * @param sessionToken The token used to identify the user.
     * @return Array of conversations belonging to the user.
     */
    @GetMapping("/home/conversation")
    public List<ConversationPayload> createMarketplaceConversationMessage(
            @CookieValue(value = "JSESSIONID", required = false) String sessionToken) {
        //401
        User currentUser = Authorization.getUserVerifySession(sessionToken, userRepository);

        List<Conversation> conversationList = marketplaceConversationRepository.findAllByInstigatorIdAndDeletedByInstigatorOrReceiverIdAndDeletedByReceiver_OrderByCreatedDesc(
                currentUser.getId(), false, currentUser.getId(), false
        );
        logger.info("Conversations retrieved user with ID {}", currentUser.getId());

        return toConversationPayloadList(conversationList);
    }

    /**
     * Retrieve all messages in a given conversation.
     * If there are no messages, then an empty array is returned.
     * No user ID is provided in the URL as the JSESSIONID is used to determine which user is requesting the messages for the conversation.
     *
     * @param sessionToken The token used to identify the user.
     * @return Array of messages belonging to the given conversation.
     */
    @GetMapping("/home/conversation/{conversationId}/messages")
    public List<MessagePayload> getMarketplaceConversationMessages(
            @CookieValue(value = "JSESSIONID", required = false) String sessionToken,
            @PathVariable Integer conversationId) {

        //401
        User currentUser = Authorization.getUserVerifySession(sessionToken, userRepository);
        Optional<Conversation> optionalConversation = marketplaceConversationRepository.findConversationById(conversationId);

        // 406
        if (optionalConversation.isEmpty()) {
            logger.error("Invalid Conversation ID");
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Invalid conversation ID");
        }

        Conversation currentConversation = optionalConversation.get();

        // DGAA/GAAs can access any messages / checks if user is receiver/instigator
        if (!Authorization.isGAAorDGAA(currentUser) && currentConversation.getReceiver().getId() != currentUser.getId() && currentConversation.getInstigator().getId() != currentUser.getId()) {
            logger.error("User does not have permission to perform action.");
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "The user does not have permission to perform the requested action"
            );
        }

        List<Message> messageList = marketplaceConversationMessageRepository.findAllByConversationId_OrderByCreatedDesc(conversationId);

        // Sets the current user to have seen all messages in conversation
        if (currentConversation.getInstigator().getId() == currentUser.getId()) {
            currentConversation.setReadByInstigator(true);
        } else if (currentConversation.getReceiver().getId() == currentUser.getId()) {
            currentConversation.setReadByReceiver(true);
        }
        marketplaceConversationRepository.save(currentConversation);

        return toMessagePayloadList(messageList);
    }

    /**
     * Converts a list of Conversation objects to a list of ConversationPayload objects to be sent to the frontend.
     *
     * @param conversationList A list of Conversation objects.
     * @return A list of ConversationPayload objects to be sent to the frontend.
     */
    public List<ConversationPayload> toConversationPayloadList(List<Conversation> conversationList) {
        List<ConversationPayload> conversationPayloadList = new ArrayList<>();
        for (Conversation unconvertedConversation: conversationList) {
            conversationPayloadList.add(unconvertedConversation.toConversationPayload());
        }
        return conversationPayloadList;
    }

    /**
     * This method is used to delete a notification.
     *
     * It will return a 401 error if the user is not logged in.
     * It will return a 406 error if the conversation does not exist.
     * It will return a 403 error if the user does not have permission to delete the conversation.
     * A GAA or DGAA can delete a conversation for other members.
     * A user can remove themself from a conversation. If there are no remaining members in a
     * conversation then it is deleted.
     *
     * @param sessionToken a token used for user authentication.
     * @param conversationId the id of the conversation to be deleted.
     */
    @Transactional
    @DeleteMapping("/users/conversation/{conversationId}")
    @ResponseStatus(code = HttpStatus.OK, reason = "Conversation successfully deleted")
    public void deleteConversation(
            @CookieValue(value = COOKIE_AUTH, required = false) String sessionToken,
            @PathVariable Integer conversationId
    ) {
        // checks to see if user is logged in. If they are not a 401 is returned.
        User currentUser = Authorization.getUserVerifySession(sessionToken, userRepository);

        Optional<Conversation> optionalConversation = marketplaceConversationRepository.findById(conversationId);
        // if no conversation is found with given id then return a 406.
        if (optionalConversation.isEmpty()) {
            logger.error(LOGGER_ERROR_REQUESTED_ROUTE);
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, HTTP_NOT_ACCEPTABLE_MESSAGE);
        }

        conversation = optionalConversation.get();

        if (currentUser == conversation.getInstigator()) {
            // the conversation has been deleted/left by the user.
            conversation.setDeletedByInstigator(true);
        } else if (currentUser == conversation.getReceiver()) {
            // the conversation has been deleted/left by the user.
            conversation.setDeletedByReceiver(true);
        } else if (Authorization.isGAAorDGAA(currentUser)) {
            // if the current user is a GAA or DGAA then they can delete the conversation and its associated messages.
            marketplaceConversationMessageRepository.deleteByConversation(conversation);
            marketplaceConversationRepository.deleteById(conversationId);
            logger.debug("Conversation and messages deleted");
            return; // need to return since conversation is deleted.
        } else {
            logger.error("Conversation Deletion Error - 403 [FORBIDDEN] - User doesn't have permissions to delete conversation");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid permissions to delete conversation");
        }

        if (conversation.hasNoMembers()) {
            // if there is no remaining members in the conversation then delete it and its associated messages.
            marketplaceConversationMessageRepository.deleteByConversation(conversation);
            marketplaceConversationRepository.deleteById(conversationId);
            logger.debug("Conversation and messages deleted");
        } else {
            // if a user removes themself from a conversation then update the members in the conversation.
            marketplaceConversationRepository.save(conversation);
            logger.debug("User removed from conversation");
        }
    }

    /**
     * Converts a list of Message objects to a list of MessagePayload objects to be sent to the frontend.
     *
     * @param messageList A list of Message objects.
     * @return A list of MessagePayload objects to be sent to the frontend.
     */
    public List<MessagePayload> toMessagePayloadList(List<Message> messageList) {
        List<MessagePayload> messagePayloadList = new ArrayList<>();
        for (Message message: messageList) {
            messagePayloadList.add(message.toMessagePayload());
        }
        return messagePayloadList;
    }

}
