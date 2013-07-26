/** 
 * Copyright (c) 2013, Kinvey, Inc. All rights reserved.
 *
 * This software contains valuable confidential and proprietary information of
 * KINVEY, INC and is subject to applicable licensing agreements.
 * Unauthorized reproduction, transmission or distribution of this file and its
 * contents is a violation of applicable laws.
 * 
 */
package com.kinvey.java.auth;

import java.io.IOException;

/**
 * A mechanism to store, retrieve and purge credentials from memory and disk
 */
public interface CredentialStore {

    /**
     * @param userId a unique identifier for the stored credential
     * @return a credential object retrieved from storage otherwise {@code null} is returned
     * @throws IOException error in retrieving from low-level storage mechanism
     */
    Credential load (String userId) throws IOException;

    /**
     * @param userId a unique identifier to index the credential in storage
     * @param credential non-null credential to store
     * @throws IOException error in storing to low-level storage mechanism
     */
    void store (String userId, Credential credential) throws IOException;

    /**
     * @param userId the unique identifier to the credential to purge
     * @throws IOException error in purging the credential from low-level storage
     */
    void delete (String userId);

}
