package org.seng302.view.incoming;

/**
 * The payload for when a user requests to receive a reset password email. Contains the email address to
 * send the reset email to and the client URL.
 */
public class UserForgotPasswordPayload {
    private String email;
    private String clientURL;

    public String getEmail() {
        return email;
    }

    public String getClientURL() {
        return clientURL;
    }
}
