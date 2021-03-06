/**
 * Copyright (c) 2014, Kinvey, Inc. All rights reserved.
 *
 * This software is licensed to you under the Kinvey terms of service located at
 * http://www.kinvey.com/terms-of-use. By downloading, accessing and/or using this
 * software, you hereby accept such terms of service  (and any agreement referenced
 * therein) and agree that you have read, understand and agree to be bound by such
 * terms of service and are of legal age to agree to such terms with Kinvey.
 *
 * This software contains valuable confidential and proprietary information of
 * KINVEY, INC and is subject to applicable licensing agreements.
 * Unauthorized reproduction, transmission or distribution of this file and its
 * contents is a violation of applicable laws.
 *
 */
package com.kinvey.java.core

import com.google.api.client.http.HttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.testing.http.MockHttpTransport
import com.google.api.client.testing.json.MockJsonFactory
import com.kinvey.java.testing.MockKinveyJsonClient.Builder
import junit.framework.TestCase
import org.junit.Test

/**
 *
 * @author m0rganic
 */
class AbstractKinveyJsonClientTest : TestCase() {
    @Test
    fun testAbstractKinveyJsonRequestBuilderWithBaseUrl() {
        val transport: HttpTransport = MockHttpTransport()
        val factory: JsonFactory = MockJsonFactory()
        val baseUrl = "https://baas.kinvey.com/"
        val buildUnderTest = Builder()

        // with no trailing "/" on baseUrl


        buildUnderTest.setBaseUrl("https://baas.kinvey.com")
        assertEquals(baseUrl, buildUnderTest.baseUrl)
        // with no trailing "/" on serviceUrl


        buildUnderTest.setServiceUrl("test")
        assertEquals("test/", buildUnderTest.servicePath)
        // with only "/" on serviceUrl


        buildUnderTest.setServiceUrl("/")
        assertEquals("", buildUnderTest.servicePath)
    }
}