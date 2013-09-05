/** 
 * Copyright (c) 2013, Kinvey, Inc. All rights reserved.
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

package com.kinvey.android;


import android.content.Context;
import android.util.Log;
import android.webkit.MimeTypeMap;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.BackOffPolicy;
import com.google.api.client.http.ExponentialBackOffPolicy;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonObjectParser;
import com.google.common.base.Preconditions;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import com.kinvey.android.callback.KinveyClientBuilderCallback;
import com.kinvey.android.callback.KinveyPingCallback;
import com.kinvey.android.callback.KinveyUserCallback;
import com.kinvey.android.push.AbstractPush;
import com.kinvey.android.push.GCMPush;
import com.kinvey.java.AbstractClient;
import com.kinvey.java.LinkedResources.LinkedGenericJson;
import com.kinvey.java.User;
import com.kinvey.java.auth.ClientUsers;
import com.kinvey.java.auth.Credential;
import com.kinvey.java.auth.CredentialManager;
import com.kinvey.java.auth.CredentialStore;
import com.kinvey.java.auth.KinveyAuthRequest;
import com.kinvey.java.core.KinveyClientRequestInitializer;
import com.kinvey.java.model.FileMetaData;

/**
 * This class is an implementation of a {@link com.kinvey.java.AbstractClient} with default settings for the Android operating
 * system.
 *
 * <p>
 * Functionality is provided through a series of factory methods, which return the various API service wrappers.
 * These factory methods are all synchronized, and access is thread-safe.  Once a service API has been retrieved,
 * it can be used to instantiate and execute asynchronous service calls.  The {@link Client.Builder} is not thread-safe.
 * </p>
 *
 * <p>
 * The calling class can pass in a callback instance. Upon completion of the service call-- either success or failure will
 * be invoked. The Callback mechanism is null-safe, so {@code null} can be passed if no callbacks are required.
 * </p>
 *
 * <p>
 * All callback methods are <i>null-safe</i>, the callback will be ignored if {@code null} is passed in.
 * </p>
 *
 * @author edwardf
 * @author m0rganic
 * @author mjsalinger
 * @since 2.0
 * @version $Id: $
 */
public class Client extends AbstractClient {

    /** global TAG used in Android logging **/
    public final static String TAG = "Kinvey - Client";

    final static Logger LOGGER = Logger.getLogger(Client.class.getSimpleName());

    private Context context = null;

    private ConcurrentHashMap<String, AsyncAppData> appDataInstanceCache;
    private ConcurrentHashMap<String, AsyncLinkedData> linkedDataInstanceCache;
    private AbstractPush pushProvider;
    private AsyncUserDiscovery userDiscovery;
    private AsyncFile file;
    private AsyncUserGroup userGroup;
    private ClientUsers clientUsers;
    private AsyncUser currentUser;
    private AsyncCustomEndpoints customEndpoints;

    /**
     * Protected constructor.  Public AbstractClient.Builder class is used to construct the AbstractClient, so this method shouldn't be
     * called directly.
     *
     * @param transport the transport
     * @param httpRequestInitializer standard request initializer
     * @param rootUrl root url to base all requests
     * @param servicePath standard service path for all requests
     * @param objectParser object parse used in all requests
     * @param kinveyRequestInitializer a {@link com.kinvey.java.core.KinveyClientRequestInitializer} object.
     * @param store a {@link com.kinvey.java.auth.CredentialStore} object.
     * @param requestPolicy a {@link BackOffPolicy} for retrying HTTP Requests
     */
    protected Client(HttpTransport transport, HttpRequestInitializer httpRequestInitializer, String rootUrl,
                     String servicePath, JsonObjectParser objectParser,
                     KinveyClientRequestInitializer kinveyRequestInitializer, CredentialStore store,
                     BackOffPolicy requestPolicy) {
        super(transport, httpRequestInitializer, rootUrl, servicePath, objectParser, kinveyRequestInitializer, store,
                requestPolicy);
    }

    /**
     * <p>Setter for the field <code>context</code>.</p>
     *
     * @param context a {@link android.content.Context} object.
     */
    public void setContext(Context context) {
        if (context != null) {
            this.context = context.getApplicationContext();
        }
    }

    /**
     * AppData factory method
     * <p>
     * Returns an instance of {@link com.kinvey.java.AppData} for the supplied collection.  A new instance is created for each collection, but
     * only one instance of {@link AsyncAppData} is created per collection.  The method is Generic and takes an instance of a
     * {@link com.google.api.client.json.GenericJson} entity type that is used for fetching/saving of {@link com.kinvey.java.AppData}.
     * </p>
     * <p>
     * This method is thread-safe.
     * </p>
     * <p>
     *     Sample Usage:
     * <pre>
     * {@code
        AppData<myEntity> myAppData = kinveyClient.appData("entityCollection", myEntity.class);
     }
     * </pre>
     * </p>
     *
     * @param collectionName The name of the collection
     * @param myClass The class that defines the entity of type {@link com.google.api.client.json.GenericJson} used
     *                for saving and fetching of data
     * @param <T> Generic of type {@link com.google.api.client.json.GenericJson} of same type as myClass
     * @return Instance of {@link com.kinvey.java.AppData} for the defined collection
     */
    public <T> AsyncAppData<T> appData(String collectionName, Class<T> myClass) {
        synchronized (lock) {
            Preconditions.checkNotNull(collectionName, "collectionName must not be null");
            if (appDataInstanceCache == null) {
                appDataInstanceCache = new ConcurrentHashMap<String, AsyncAppData>();
            }
            if (!appDataInstanceCache.containsKey(collectionName)) {
                Log.v(Client.TAG, "adding new instance of AppData, new collection name");
                appDataInstanceCache.put(collectionName, new AsyncAppData(collectionName, myClass, this));
            }
            if(appDataInstanceCache.containsKey(collectionName) && !appDataInstanceCache.get(collectionName).getCurrentClass().equals(myClass)){
                Log.v(Client.TAG, "adding new instance of AppData, class doesn't match");
                appDataInstanceCache.put(collectionName, new AsyncAppData(collectionName, myClass, this));   
            }

            return appDataInstanceCache.get(collectionName);
        }
    }

    /**
     * LinkedData factory method
     * <p>
     * Returns an instance of {@link AsyncLinkedData} for the supplied collection.  A new instance is created for each collection, but
     * only one instance of LinkedData is created per collection.  The method is Generic and takes an instance of a
     * {@link LinkedGenericJson} entity type that is used for fetching/saving of {@link AsyncLinkedData}.
     * </p>
     * <p>
     * This method is thread-safe.
     * </p>
     * <p>
     *     Sample Usage:
     * <pre>
     * {@code
     LinkedData<myEntity> myAppData = kinveyClient.linkedData("entityCollection", myEntity.class);
    }
     * </pre>
     * </p>
     *
     * @param collectionName The name of the collection
     * @param myClass The class that defines the entity of type {@link LinkedGenericJson} used for saving and fetching of data
     * @param <T> Generic of type {@link com.google.api.client.json.GenericJson} of same type as myClass
     * @return Instance of {@link AsyncLinkedData} for the defined collection
     */
    public <T extends LinkedGenericJson> AsyncLinkedData<T> linkedData(String collectionName, Class<T> myClass) {
        synchronized (lock) {
            Preconditions.checkNotNull(collectionName, "collectionName must not be null");
            if (linkedDataInstanceCache == null) {
                linkedDataInstanceCache = new ConcurrentHashMap<String, AsyncLinkedData>();
            }
            if (!linkedDataInstanceCache.containsKey(collectionName)) {
                linkedDataInstanceCache.put(collectionName, new AsyncLinkedData(collectionName, myClass, this));
            }
            return linkedDataInstanceCache.get(collectionName);
        }
    }


    /**
     * File factory method
     * <p>
     * Returns an instance of {@link com.kinvey.java.File} for uploading and downloading of files.  Only one instance is created for each
     * instance of the Kinvey client.
     * </p>
     * <p>
     * This method is thread-safe.
     * </p>
     * <p>
     *     Sample Usage:
     * <pre>
     * {@code
     File myFile = kinveyClient.file();
     }
     * </pre>
     * </p>
     *
     * @return Instance of {@link com.kinvey.java.File} for the defined collection
     */
    @Override
    public AsyncFile file() {
        synchronized (lock) {
            if (file == null) {
                file = new AsyncFile(this);
            }
            return file;
        }
    }

    /**
     * Custom Endpoints factory method
     *<p>
     * Returns the instance of {@link com.kinvey.java.CustomEndpoints} used for executing RPC requests.  Only one instance
     * of Custom Endpoints is created for each instance of the Kinvey Client.
     *</p>
     * <p>
     * This method is thread-safe.
     * </p>
     * <p>
     *     Sample Usage:
     * <pre>
     {@code
      CustomEndpoints myCustomEndpoints = kinveyClient.customEndpoints();
     }
     * </pre>
     * </p>
     *
     * @return Instance of {@link com.kinvey.java.UserDiscovery} for the defined collection
     */
    @Override
    public AsyncCustomEndpoints customEndpoints(){
        synchronized (lock){
            if (customEndpoints == null){
                customEndpoints = new AsyncCustomEndpoints(this);
            }
            return customEndpoints;
        }
    }

    /**
     * UserDiscovery factory method
     * <p>
     * Returns the instance of {@link com.kinvey.java.UserDiscovery} used for searching for users. Only one instance of
     * UserDiscovery is created for each instance of the Kinvey Client.
     * </p>
     * <p>
     * This method is thread-safe.
     * </p>
     * <p>
     *     Sample Usage:
     * <pre>
     {@code
     UserDiscovery myUserDiscovery = kinveyClient.userDiscovery();
     }
     * </pre>
     * </p>
     *
     * @return Instance of {@link com.kinvey.java.UserDiscovery} for the defined collection
     */
    @Override
    public AsyncUserDiscovery userDiscovery() {
        synchronized (lock) {
            if (userDiscovery == null) {
                userDiscovery = new AsyncUserDiscovery(this,
                        (KinveyClientRequestInitializer) this.getKinveyRequestInitializer());
            }
            return userDiscovery;
        }
    }

    /**
     * UserGroup factory method
     * <p>
     * Returns the instance of {@link com.kinvey.java.UserGroup} used for managing user groups. Only one instance of
     * UserGroup is created for each instance of the Kinvey Client.
     * </p>
     * <p>
     * This method is thread-safe.
     * </p>
     * <p>
     *     Sample Usage:
     * <pre>
     {@code
     UserGroup myUserGroup = kinveyClient.userGroup();
     }
     * </pre>
     * </p>
     *
     * @return Instance of {@link com.kinvey.java.UserGroup} for the defined collection
     */
    @Override
    public AsyncUserGroup userGroup() {
        synchronized (lock) {
            if (userGroup == null) {
                userGroup = new AsyncUserGroup(this,
                        (KinveyClientRequestInitializer) this.getKinveyRequestInitializer());
            }
            return userGroup;
        }

    }

    /** {@inheritDoc} */
    @Override
    protected ClientUsers getClientUsers() {
        synchronized (lock) {
            if (this.clientUsers == null) {
                this.clientUsers = AndroidClientUsers.getClientUsers(this.context);
            }
            return this.clientUsers;
        }

    }


    /**
     * User factory method
     * <p>
     * Returns the instance of {@link com.kinvey.java.User} that contains the current active user.  If no active user context
     * has been established, the {@link com.kinvey.java.User} object returned will be instantiated and empty.
     * </p>
     * <p>
     * This method is thread-safe.
     * </p>
     * <p>
     *     Sample Usage:
     * <pre>
     * {@code
     User currentUser = kinveyClient.currentUser();
     }
     * </pre>
     * </p>
     *
     * @return Instance of {@link com.kinvey.java.User} for the defined collection
     */
    @Override
    public AsyncUser user() {
        synchronized (lock) {
            if (getCurrentUser() == null) {
                String appKey = ((KinveyClientRequestInitializer) getKinveyRequestInitializer()).getAppKey();
                String appSecret = ((KinveyClientRequestInitializer) getKinveyRequestInitializer()).getAppSecret();
                setCurrentUser(new AsyncUser(this, new KinveyAuthRequest.Builder(getRequestFactory().getTransport(), getJsonFactory(),
                        getBaseUrl(), appKey, appSecret, null)));
            }
            return (AsyncUser) getCurrentUser();
        }
    }


    /**
     * Push factory method
     * <p>
     * Returns the instance of {@link AbstractPush} used for configuring Push. Only one instance of
     * {@link AbstractPush} is created for each instance of the Kinvey Client.
     * </p>
     * <p>
     * This method is thread-safe.
     * </p>
     * <p>
     *     Sample Usage:
     * <pre>
     {@code
        AbstractPush myPush = kinveyClient.push();
     }
     * </pre>
     * </p>
     *
     * @return Instance of {@link AbstractPush} for the defined collection
     */
    public AbstractPush push() {
        synchronized (lock) {
            //NOTE:  pushProvider is defined as a GCMPush in the ClientBuilder#build() method, if the user has set it in the property file.
            //ONCE Urban Airship has been officially deprecated we can remove the below lines completely (or create GCMPush inline here)
            if (pushProvider == null) {
                pushProvider = new GCMPush(this, true, "");
            }
            return pushProvider;
        }
    }

    /**
     * Asynchronous Ping service method
     * <p>
     * Performs an authenticated ping against the configured Kinvey backend.
     * </p>
     * <p>
     * Sample Usage:
     * <pre>
     {@code
        kinveyClient.ping(new KinveyPingCallback() {
            onSuccess(Boolean result) { ... }
            onFailure(Throwable error) { ... }
        }
     }
     * </pre>
     * </p>
     *
     * @param callback object of type {@link KinveyPingCallback} to be notified when request completes
     */
    public void ping(KinveyPingCallback callback) {
        new Ping(callback).execute();
    }


    private class Ping extends AsyncClientRequest<Boolean> {
        private Ping(KinveyPingCallback callback) {
            super(callback);
        }

        @Override
        protected Boolean executeAsync() throws IOException {
            return Client.this.pingBlocking();
        }
    }

    /**
     * Get a reference to the Application Context used to create this instance of the Client
     * @return {@code null} or the live Application Context
     */
    public Context getContext(){
        return this.context;
    }

    /**
     * Create a client for interacting with Kinvey's services from an Android Activity.
     * <pre>
     * {@code
     * Client myClient =  new Client.Builder(appKey, appSecret, getContext()).build();
     * }
     * <pre/>
     * All features of the library are be accessed through an instance of a client.
     * <p/>
     * It is recommended to maintain a single instance of a {@code Client} while developing with Kinvey, either in an
     * Activity, a Service, or an Application.
     * <p/>
     * This Builder class is not thread-safe.
     */
    public static class Builder extends AbstractClient.Builder {

        private Context context = null;
        private KinveyUserCallback retrieveUserCallback = null;
        //GCM Push Fields
        private String GCM_SenderID = "";
        private boolean GCM_Enabled = false;
        private boolean GCM_InProduction = true;


        /**
         * Use this constructor to create a AbstractClient.Builder, which can be used to build a Kinvey AbstractClient with defaults
         * set for the Android Operating System.
         * <p>
         * This constructor does NOT support push notification functionality.
         * If push notifications are necessary, use a properties file and the overloaded constructor.
         * </p>
         *
         * @param appKey Your Kinvey Application Key
         * @param appSecret Your Kinvey Application Secret
         * @param context Your Android Application Context
         */
        public Builder(String appKey, String appSecret, Context context) {
            super(AndroidHttp.newCompatibleTransport(), AndroidJson.newCompatibleJsonFactory(), null
                    , new KinveyClientRequestInitializer(appKey, appSecret, new KinveyHeaders(context)));
            this.context = context.getApplicationContext();
            this.setRequestBackoffPolicy(new ExponentialBackOffPolicy());
            try {
                this.setCredentialStore(new AndroidCredentialStore(this.context));
            } catch (Exception ex) {
                //TODO Add handling
            }
        }


        /**
         * Use this constructor to create a Client.Builder, which can be used to build a Kinvey Client with defaults
         * set for the Android Operating System.
         * <p>
         * This constructor requires a  properties file, containing configuration for your Kinvey Client.
         * Save this file within your Android project, at:  assets/kinvey.properties
         * </p>
         * <p>
         * This constructor provides support for push notifications.
         * </p>
         * <p>
         * <a href="http://devcenter.kinvey.com/android/guides/getting-started#InitializeClient">Kinvey Guide for initializing Client with a properties file.</a>
         * </p>
         *
         * @param context - Your Android Application Context
         *
         */
        public Builder(Context context) {
            super(AndroidHttp.newCompatibleTransport(), AndroidJson.newCompatibleJsonFactory(), null);

            try {
                final InputStream in = context.getAssets().open("kinvey.properties");//context.getClassLoader().getResourceAsStream(getAndroidPropertyFile());

                super.getProps().load(in);
            } catch (IOException e) {
                Log.w(TAG, "Couldn't load properties, trying another load approach.  Ensure there is a file:  myProject/assets/kinvey.properties which contains: app.key and app.secret.");
                super.loadPropertiesFromDisk(getAndroidPropertyFile());
            } catch (NullPointerException ex){
                Log.e(TAG, "Builder cannot find properties file at assets/kinvey.properties.  Ensure this file exists, containing app.key and app.secret!");
                Log.e(TAG, "If you are using push notification or offline storage you must configure your client to load from properties, see our guides for instructions.");
                throw new RuntimeException("Builder cannot find properties file at assets/kinvey.properties.  Ensure this file exists, containing app.key and app.secret!");
            }

            if (super.getString(Option.BASE_URL) != null) {
                this.setBaseUrl(super.getString(Option.BASE_URL));
            }

            if (super.getString(Option.PORT) != null){
                this.setBaseUrl(String.format("%s:%s", super.getBaseUrl(), super.getString(Option.PORT)));
            }

            if (super.getString(Option.GCM_PUSH_ENABLED) != null){
                this.GCM_Enabled = Boolean.parseBoolean(super.getString(Option.GCM_PUSH_ENABLED));
            }

            if (super.getString(Option.GCM_PROD_MODE) != null){
                this.GCM_InProduction = Boolean.parseBoolean(super.getString(Option.GCM_PROD_MODE));
            }

            if (super.getString(Option.GCM_SENDER_ID) != null){
                this.GCM_SenderID = super.getString(Option.GCM_SENDER_ID);
            }

            String appKey = Preconditions.checkNotNull(super.getString(Option.APP_KEY), "appKey must not be null");
            String appSecret = Preconditions.checkNotNull(super.getString(Option.APP_SECRET), "appSecret must not be null");

            KinveyClientRequestInitializer initializer = new KinveyClientRequestInitializer(appKey, appSecret, new KinveyHeaders(context));
            this.setKinveyClientRequestInitializer(initializer);

            this.context = context.getApplicationContext();
            this.setRequestBackoffPolicy(new ExponentialBackOffPolicy());
            try {
                this.setCredentialStore(new AndroidCredentialStore(this.context));
            } catch (AndroidCredentialStoreException ex) {
                Log.e(TAG, "Credential store was in a corrupted state and had to be rebuilt", ex);
            } catch (IOException ex) {
                Log.e(TAG, "Credential store failed to load", ex);
            }
        }


        /**
         * @return an instantiated Kinvey Android Client,
         * which contains factory methods for accessing various functionality.
         */
        @Override
        public Client build() {
            final Client client = new Client(getTransport(),
                    getHttpRequestInitializer(), getBaseUrl(),
                    getServicePath(), getObjectParser(), getKinveyClientRequestInitializer(), getCredentialStore(),
                    getRequestBackoffPolicy());
            client.setContext(context);
            client.clientUsers = AndroidClientUsers.getClientUsers(context);
            try {
                Credential credential = retrieveUserFromCredentialStore(client);
                if (credential != null) {
                    loginWithCredential(client, credential);
                }

            } catch (AndroidCredentialStoreException ex) {
                Log.e(TAG, "Credential store was in a corrupted state and had to be rebuilt", ex);
                client.setCurrentUser(null);
            } catch (IOException ex) {
                Log.e(TAG, "Credential store failed to load", ex);
                client.setCurrentUser(null);
            }

            //GCM explicitely enabled
            if (this.GCM_Enabled ==  true){
                client.pushProvider = new GCMPush(client, this.GCM_InProduction, this.GCM_SenderID);
            }

            return client;
        }

        /**
         * Asynchronous Client build method
         *
         * <p>
         *
         * </p>
         *
         * @param buildCallback Instance of {@link: KinveyClientBuilderCallback}
         */
        public void build(KinveyClientBuilderCallback buildCallback) {
            new Build(buildCallback).execute(AsyncClientRequest.ExecutorType.KINVEYSERIAL);
        }

        /**
         * Sets a callback to be called after a client is intialized and User attributes is being retrieved.
         *
         * <p>
         * When a client is intialized after an initial login, the user's credentials are cached locally and used for the
         * initialization of the client.  As part of the initialization process, a background thread is spawned to retrieve
         * up-to-date user attributes.  This optional callback is called when the retrieval process is complete and passes
         * an instance of the logged in user.
         * </p>
         * <p>Sample Usage:
         * <pre>
         * {@code
            Client myClient = Client.Builder(this)
                    .setRetrieveUserCallback(new KinveyUserCallback() {
                public void onFailure(Throwable t) {
                    CharSequence text = "Error retrieving user attributes.";
                    Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                }

                public void onSuccess(User u) {
                    CharSequence text = "Retrieved up-to-date data for " + u.getUserName() + ".";
                    Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                }
            }).build();
         }
         * </pre>
         * ></p>
         *
         * @param callback
         * @return
         */
        public Client.Builder setRetrieveUserCallback(KinveyUserCallback callback) {
            this.retrieveUserCallback = callback;
            return this;
        }

        public Client.Builder enableGCM(boolean gcmEnabled){
            this.GCM_Enabled = gcmEnabled;
            return this;
        }

        public Client.Builder setSenderIDs(String senderID){
            this.GCM_SenderID = senderID;
            return this;
        }

        public Client.Builder setGcmInProduction(boolean inProduction){
            this.GCM_InProduction = inProduction;
            return this;
        }

        private Credential retrieveUserFromCredentialStore(Client client)
                throws AndroidCredentialStoreException, IOException {
            Credential credential = null;
            if (!client.user().isUserLoggedIn()) {
                String userID = client.getClientUsers().getCurrentUser();
                if (userID != null && !userID.equals("")) {
                    AndroidCredentialStore store = new AndroidCredentialStore(context);
                    CredentialManager manager = new CredentialManager(store);
                    credential = manager.loadCredential(userID);
                }
            }
            return credential;
        }

        private void loginWithCredential(final Client client, Credential credential) {
            getKinveyClientRequestInitializer().setCredential(credential);
            try {
                client.user().login(credential).execute();
            } catch (IOException ex) {
                Log.e(TAG, "Could not retrieve user Credentials");
            }

            client.user().retrieveMetadata(new KinveyUserCallback() {
                @Override
                public void onSuccess(User result) {
                    client.setCurrentUser(result);
                    if (retrieveUserCallback != null){
                        retrieveUserCallback.onSuccess(result);
                    }
                }

                @Override
                public void onFailure(Throwable error) {
                    if (retrieveUserCallback != null){
                        retrieveUserCallback.onFailure(error);
                    }
                }
            });
        }

        /**
         * The default kinvey settings filename {@code assets/kinvey.properties}
         *
         * @return {@code assets/kinvey.properties}
         */
        protected static String getAndroidPropertyFile() {
            return "assets/kinvey.properties";
        }

        private class Build extends AsyncClientRequest<Client> {

            private Build(KinveyClientBuilderCallback builderCallback) {
                super(builderCallback);
            }

            @Override
            protected Client executeAsync() {
                return Client.Builder.this.build();
            }
        }
    }


    @Override
    public MimeTypeFinder getMimeTypeFinder() {
        return new MimeTypeFinder() {
            @Override
            public void getMimeType(FileMetaData meta, InputStream stream) {

                String mimetype = null;
                try{
                    mimetype = URLConnection.guessContentTypeFromStream(stream);
                    System.out.println("Kinvey - Client - File | mimetype from stream found as: " + mimetype);
                } catch (Exception e) {
                    System.out.println("Kinvey - Client - File | content stream mimetype is unreadable, defaulting");
                }

                if (mimetype == null){
                    getMimeType(meta);
                }else{
                    meta.setMimetype(mimetype);
                }



                stream.mark(0x100000 * 10);  //10MB mark limit
                int numBytes = 0;
                try{
                    while (stream.read() != -1){
                        numBytes++;
                    }

                }catch (Exception e){
                    Log.i(TAG, "error reading input stream to get size, setting it to 0") ;
                    numBytes = 0;
                }
                try{
                    stream.reset();
                }catch(Exception e){
                    Log.i(TAG, "error resetting stream!") ;

                }

                Log.i(TAG, "size is: " + numBytes) ;



                meta.setSize(numBytes);
            }

            @Override
            public void getMimeType(FileMetaData meta, File file) {
                if (file == null || file.getName() == null || meta == null) {
                    Log.v(Client.TAG, "cannot calculate mimetype without a file or filename!");
                    meta.setMimetype("application/octet-stream");
                    super.getMimeType(meta, file);
                    return;
                }

                if (meta.getMimetype()!= null && meta.getMimetype().length() > 0){
                    return;
                }

                //check metadata file name first
                //check file's file name
                //check stream                          );

                String mimetype;
                String fileExt = "";

                if (meta.getFileName() != null && meta.getFileName().length() > 0 && meta.getFileName().lastIndexOf(".") > 0){
                    fileExt = meta.getFileName().substring(meta.getFileName().lastIndexOf('.'), meta.getFileName().length());
                }

                if (file.getName() != null && file.getName().lastIndexOf(".") > 0){
                    if (fileExt.length() == 0){
                        fileExt = file.getName().substring(file.getName().lastIndexOf('.'), file.getName().length());
                    }
                }

                System.out.print("Async File file extension: " + fileExt);

                //did we get it from file extension? if not, attempt to get it from file contents
                if (fileExt.length() > 0){
                    mimetype = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExt);
                }else{
                    mimetype = "application/octet-stream";
                }

                meta.setMimetype(mimetype);
                meta.setSize(file.length());            }

            @Override
            public void getMimeType(FileMetaData metaData) {
                Log.i(TAG, "******************");
                Log.i(TAG, "getting mime type! from metadata");
                Log.i(TAG, "******************");


                if (metaData.getMimetype()!= null && metaData.getMimetype().length() > 0){
                    return;
                }
                String mimetype = null;

                if(metaData.getFileName()!= null){
                    int dotIndex = metaData.getFileName().lastIndexOf(".");
                    if (dotIndex > 0){
                        mimetype = MimeTypeMap.getSingleton().getMimeTypeFromExtension(metaData.getFileName().substring(dotIndex, metaData.getFileName().length()));
                    }
                }

                if (mimetype == null){
                    mimetype = "application/octet-stream";
                }
                metaData.setMimetype(mimetype);

            }
        };
    }



}

