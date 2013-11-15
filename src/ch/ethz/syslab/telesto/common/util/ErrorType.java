package ch.ethz.syslab.telesto.common.util;

import ch.ethz.syslab.telesto.common.model.ClientMode;

public enum ErrorType {
    /**
     * A typically unexpected error
     */
    INTERNAL_ERROR,
    /**
     * A packet that could not be handled was received
     */
    UNEXPECTED_PACKET,
    /**
     * An error during network interaction
     */
    IO_ERROR,
    /**
     * A unique constraint was violated
     */
    UNIQUE_CONSTRAINT,
    /**
     * The specified <code>queue_name</code> is not unique
     */
    QUEUE_NAME_NOT_UNIQUE,
    /**
     * The specified client does not exist
     */
    CLIENT_NOT_EXISTING,
    /**
     * The specified <code>client_name</code> is not unique
     */
    CLIENT_NAME_NOT_UNIQUE,
    /**
     * The specified queue does not exist
     */
    QUEUE_NOT_EXISTING,
    /**
     * No queues contains messages for the client
     */
    NO_ACTIVE_QUEUES_EXISTING,
    /**
     * There exist absolutely no queues
     */
    NO_QUEUES_EXISTING,
    /**
     * There are no messages in the specified queue. (None at all)
     */
    NO_MESSAGES_IN_QUEUE,
    /**
     * A receive query did not yield any messages
     */
    NO_MESSAGES_RETRIEVED,
    /**
     * A required parameter in a packet is missing
     */
    REQUIRED_PARAMETER_MISSING,
    /**
     * The {@link ClientMode} prohibits the use of the called service
     */
    CLIENT_MODE_PERMISSION_VIOLATION;
}
