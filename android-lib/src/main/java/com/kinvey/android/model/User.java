package com.kinvey.android.model;

import com.kinvey.android.AsyncClientRequest;
import com.kinvey.java.core.KinveyClientCallback;
import com.kinvey.java.dto.BaseUser;
import com.kinvey.java.store.BaseUserStore;

import java.io.IOException;

/**
 * Created by yuliya on 06/07/17.
 */

public class User extends BaseUser {

    public void update(KinveyClientCallback callback) {
        new Update(callback).execute();
    }

    private static class Update<T extends User> extends AsyncClientRequest<T> {

        private Update(KinveyClientCallback<T> callback) {
            super(callback);
        }

        @Override
        protected T executeAsync() throws IOException {
            return BaseUserStore.update();
        }
    }
}