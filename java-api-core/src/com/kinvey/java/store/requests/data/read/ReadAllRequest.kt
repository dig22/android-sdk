/*
 *  Copyright (c) 2016, Kinvey, Inc. All rights reserved.
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

package com.kinvey.java.store.requests.data.read

import com.google.api.client.json.GenericJson
import com.kinvey.java.cache.ICache
import com.kinvey.java.model.KinveyReadResponse
import com.kinvey.java.network.NetworkManager
import com.kinvey.java.store.ReadPolicy

import java.io.IOException
import java.util.Arrays

/**
 * Created by Prots on 2/15/16.
 */
class ReadAllRequest<T : GenericJson>(cache: ICache<T>?, readPolicy: ReadPolicy?, networkManager: NetworkManager<T>?)
    : AbstractReadRequest<T>(cache, readPolicy, networkManager) {

    override val cached: KinveyReadResponse<T>?
        get() {
            val response = KinveyReadResponse<T>()
            response.result = cache?.get()
            if (cache?.isAddCount == true) {
                response.count = response.result?.size
            }
            return response
        }

    override val network: KinveyReadResponse<T>?
        @Throws(IOException::class)
        get() = networkData?.getBlocking()?.execute()
}
