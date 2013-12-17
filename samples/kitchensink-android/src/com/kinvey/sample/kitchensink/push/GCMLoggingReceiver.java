/** 
 * Copyright (c) 2013 Kinvey Inc.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 * 
 */
package com.kinvey.sample.kitchensink.push;


import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.kinvey.android.Client;
import com.kinvey.android.push.KinveyGCMService;
import com.kinvey.sample.kitchensink.R;


public class GCMLoggingReceiver extends KinveyGCMService {
    @Override
    public void onMessage(String message) {
        Log.i(Client.TAG, "GCM - onMessage: " + message);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.icon_small)
                        .setContentTitle("My notification")
                        .setContentText("Hello World!");

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(1, mBuilder.build());


    }

    @Override
    public void onError(String error) {
        Log.i(Client.TAG, "GCM - onError: " + error);
    }

    @Override
    public void onDelete(int deleteCount) {
        Log.i(Client.TAG, "GCM - onDelete, message deleted count: " + deleteCount);
    }

    @Override
    public void onRegistered(String gcmID) {
        Log.i(Client.TAG, "GCM - onRegister, new gcmID is: " + gcmID);

    }

    @Override
    public void onUnregistered(String oldID) {
        Log.i(Client.TAG, "GCM - onUnregister");
    }






}
