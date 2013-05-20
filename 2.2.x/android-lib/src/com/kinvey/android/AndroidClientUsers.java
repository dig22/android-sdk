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
package com.kinvey.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import com.google.common.base.Preconditions;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.logging.Logger;

import com.kinvey.java.auth.ClientUsers;

/**
 * @author mjsalinger
 * @since 2.0
 */
class AndroidClientUsers implements ClientUsers {
    private HashMap<String,String> userList;
    private String activeUser;
    private static AndroidClientUsers _instance;
    SharedPreferences userPreferences;
    Context appContext;

    private enum PersistData {
        USERLIST,
        ACTIVEUSER,
        BOTH
    }

    private AndroidClientUsers(Context context) {
        appContext = context.getApplicationContext();
        userPreferences = appContext.getSharedPreferences(
                appContext.getPackageName(), Context.MODE_PRIVATE);
        retrieveUsers();
        if (userList == null) {
            userList = new HashMap<String, String>();
        }
        activeUser = userPreferences.getString("activeUser","");
        if (activeUser == null) {
            activeUser = "";
        }
    }

    private void persistData(PersistData type) {
        SharedPreferences.Editor editor = userPreferences.edit();

        switch(type) {
            case USERLIST:
                persistUsers();
                break;
            case ACTIVEUSER:

                editor.putString("activeUser",activeUser);
                break;
            case BOTH:

                editor.putString("activeUser", activeUser);
                persistUsers();
                break;
            default:
                throw new IllegalArgumentException("Illegal PersistData argument");
        }
        editor.commit();

    }

    private synchronized void persistUsers() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new PersistUsers().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new PersistUsers().execute();
        }
    }

    static AndroidClientUsers getClientUsers(Context context) {
        if (_instance == null) {
            _instance = new AndroidClientUsers(context);
        }
        return _instance;
    }

    /** {@inheritDoc} */
    @Override
    public void addUser(String userID, String type) {
        userList.put(userID, type);
        persistData(PersistData.USERLIST);
    }

    /** {@inheritDoc} */
    @Override
    public void removeUser(String userID) {
        if(userID.equals(getCurrentUser())) {
            setCurrentUser(null);
        }
        userList.remove(userID);
        persistData(PersistData.BOTH);
    }

    /** {@inheritDoc} */
    @Override
    public void switchUser(String userID) {
        Preconditions.checkState(userList.containsKey(userID), "userID %s was not in the credential store", userID);
        activeUser = userID;
        persistData(PersistData.ACTIVEUSER);
    }

    /** {@inheritDoc} */
    @Override
    public void setCurrentUser(String userID) {
        Preconditions.checkState(userList.containsKey(userID), "userID %s was not in the credential store", userID);
        activeUser = userID;
        persistData(PersistData.ACTIVEUSER);
    }

    /** {@inheritDoc} */
    @Override
    public String getCurrentUser() {
        return activeUser;
    }

    /** {@inheritDoc} */
    @Override
    public String getCurrentUserType() {
        String userType = userList.get(activeUser);
        return userType == null ? "" : userType;
    }

    private void retrieveUsers() {
        FileInputStream fIn = null;
        ObjectInputStream in = null;

        try {
            fIn = appContext.openFileInput("kinveyUsers.bin");
            in = new ObjectInputStream(fIn);
            userList = (HashMap<String,String>) in.readObject();
        } catch (FileNotFoundException e) {
            //ignore we're probably initializing it
        } catch (Exception e) {
            // trap all exceptions and log
            // not propagating this exception
            Log.e(Client.TAG, "Failed to initialize kinveyUsers.bin", e);
        } finally {
            try {
                if (fIn != null) {
                    fIn.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException io) {
                Log.e("AndroidClientUsers", "Failed to clean up resources while reading kinveyUser.bin", io);
            }
        }
    }

    private class PersistUsers extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                FileOutputStream fStream = appContext.openFileOutput("kinveyUsers.bin", Context.MODE_PRIVATE);
                ObjectOutputStream oStream = new ObjectOutputStream(fStream);

                oStream.writeObject(userList);
                oStream.flush();
                fStream.getFD().sync();
                oStream.close();

                Log.i(Client.TAG,"Serialization success");
            } catch (IOException e) {
                Log.e(Client.TAG, e.getMessage());
            }

            return null;
        }
    }
}
