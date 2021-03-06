/**
 * Summary. Contains the definition for the conversation repository interface.
 *
 * Description. Contains the definition for the conversation repository, which defines the functions
 * used to perform actions with the data.
 *
 * @link   team-400/src/main/java/org/seng302/model/repository
 * @file   This file defines the MarketplaceConversationRepository interface.
 * @author team-400
 * @since  31.08.2021
 */

package org.seng302.model.repository;

import org.seng302.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource
public interface MarketplaceConversationRepository extends JpaRepository<Conversation, Integer> {

    /**
     * Search to see if a conversation exists.
     * @param conversationId the id of the conversation to search for.
     * @return conversation object if exists
     */
    Optional<Conversation> findConversationById(
            Integer conversationId
    );


    /**
     * Return all conversations associated with the provided user IDs given they have not deleted the conversation.
     * @param instigatorId The user ID of the instigator.
     * @param deletedByInstigator Keeps track of whether a conversation has been deleted by an instigator.
     * @param receiverId The user ID of the receiver.
     * @param deletedByReceiver Keeps track of whether a conversation has been deleted by a receiver.
     * @return A list of Conversation objects if any exist, otherwise an empty list.
     */
    List<Conversation> findAllByInstigatorIdAndDeletedByInstigatorOrReceiverIdAndDeletedByReceiver_OrderByCreatedDesc(
            Integer instigatorId, boolean deletedByInstigator, Integer receiverId, boolean deletedByReceiver
    );
}
