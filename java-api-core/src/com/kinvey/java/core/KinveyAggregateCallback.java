/*
 * Copyright (c) 2013 Kinvey Inc.
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
package com.kinvey.java.core;

import com.kinvey.java.model.Aggregation;

/**
 * @author edwardf
 */
public abstract class KinveyAggregateCallback implements KinveyClientCallback<Aggregation.Result[]> {

    @Override
    public void onSuccess(Aggregation.Result[] result) {

        Aggregation response = new Aggregation(result);
        onSuccess(response);

    }


    public abstract void onFailure(Throwable error);

    public abstract void onSuccess(Aggregation response);

}
