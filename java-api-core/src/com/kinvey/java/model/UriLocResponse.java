/** 
 * Copyright (c) 2013, Kinvey, Inc. All rights reserved.
 *
 * This software contains valuable confidential and proprietary information of
 * KINVEY, INC and is subject to applicable licensing agreements.
 * Unauthorized reproduction, transmission or distribution of this file and its
 * contents is a violation of applicable laws.
 * 
 */
package com.kinvey.java.model;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

/**
 * Generic blob service response object for upload and download url requests.
 *
 * @deprecated  with new file API
 */
public class UriLocResponse extends GenericJson {

    @Key("URI")
    private String blobTemporaryUri;

    public GenericUrl newGenericUrl() {
        return (blobTemporaryUri != null) ? new GenericUrl(blobTemporaryUri): null;
    }

    public String getBlobTemporaryUri() {
        return blobTemporaryUri;
    }
}
