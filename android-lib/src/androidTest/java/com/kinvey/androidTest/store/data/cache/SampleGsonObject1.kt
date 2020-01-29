package com.kinvey.androidTest.store.data.cache

import com.google.api.client.json.GenericJson
import com.google.api.client.util.Key

/**
 * Created by Prots on 1/27/16.
 */
data class SampleGsonObject1(
    @Key("_id")
    var id: String? = null,
    @Key("title")
    var title: String? = null
) : GenericJson()