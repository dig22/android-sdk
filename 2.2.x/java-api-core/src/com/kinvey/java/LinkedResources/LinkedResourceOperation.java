/*
 * Copyright (c) 2013, Kinvey, Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.kinvey.java.LinkedResources;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import com.kinvey.java.AppData;
import com.kinvey.java.AppDataOperation;
import com.kinvey.java.LinkedData;
import com.kinvey.java.Query;
import com.kinvey.java.cache.AbstractKinveyCachedClientRequest;
import com.kinvey.java.cache.Cache;
import com.kinvey.java.cache.CachePolicy;
import com.kinvey.java.core.AbstractKinveyClientRequest;
import com.kinvey.java.core.AbstractKinveyJsonClientRequest;
import com.kinvey.java.model.AggregateEntity;
import com.kinvey.java.model.KinveyDeleteResponse;
import com.kinvey.java.query.MongoQueryFilter;

/**
 * Builder for advanced App Data requests
 *
 * @author edwardf
 * @since 2.0.2
 */
public class LinkedResourceOperation extends AppDataOperation{



}