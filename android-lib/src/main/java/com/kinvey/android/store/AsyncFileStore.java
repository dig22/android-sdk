package com.kinvey.android.store;

import com.kinvey.android.AsyncClientRequest;
import com.kinvey.android.async.AsyncRequest;
import com.kinvey.android.callback.KinveyDeleteCallback;
import com.kinvey.java.Query;
import com.kinvey.java.cache.ICacheManager;
import com.kinvey.java.core.DownloaderProgressListener;
import com.kinvey.java.core.KinveyClientCallback;
import com.kinvey.java.core.UploaderProgressListener;
import com.kinvey.java.model.FileMetaData;
import com.kinvey.java.network.NetworkFileManager;
import com.kinvey.java.store.FileStore;
import com.kinvey.java.store.StoreType;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Created by Prots on 2/22/16.
 */
public class AsyncFileStore extends FileStore {




    private enum FileMethods{
        UPLOAD_FILE,
        UPLOAD_FILE_METADATA,
        UPLOAD_STREAM_METADATA,
        UPLOAD_STREAM_FILENAME,
        DELETE_ID,
        DOWNLOAD_METADATA,
        DOWNLOAD_QUERY,
        DOWNLOAD_FILENAME
    }

    private static HashMap<FileMethods, Method> asyncMethods =
            new HashMap<FileMethods, Method>();

    static {
        try {
            //UPLOAD METHODS
            asyncMethods.put(FileMethods.UPLOAD_FILE,
                    FileStore.class.getDeclaredMethod("upload", File.class, UploaderProgressListener.class));
            asyncMethods.put(FileMethods.UPLOAD_FILE_METADATA,
                    FileStore.class.getDeclaredMethod("upload", File.class,
                            FileMetaData.class,
                            UploaderProgressListener.class));
            asyncMethods.put(FileMethods.UPLOAD_STREAM_METADATA,
                    FileStore.class.getDeclaredMethod("upload", InputStream.class,
                            FileMetaData.class,
                            UploaderProgressListener.class));
            asyncMethods.put(FileMethods.UPLOAD_STREAM_FILENAME,
                    FileStore.class.getDeclaredMethod("upload", String.class,
                            InputStream.class,
                            UploaderProgressListener.class));

            //DELETE METHODS

            asyncMethods.put(FileMethods.DELETE_ID,
                    FileStore.class.getDeclaredMethod("download", String.class));

            //DOWNLOAD METHODS
            asyncMethods.put(FileMethods.DOWNLOAD_FILENAME,
                    FileStore.class.getDeclaredMethod("download", String.class, String.class, DownloaderProgressListener.class));

            asyncMethods.put(FileMethods.DOWNLOAD_METADATA,
                    FileStore.class.getDeclaredMethod("download", FileMetaData.class, OutputStream.class, DownloaderProgressListener.class));
            asyncMethods.put(FileMethods.DOWNLOAD_QUERY,
                    FileStore.class.getDeclaredMethod("download", Query.class, String.class, DownloaderProgressListener.class));

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }



    public AsyncFileStore(NetworkFileManager networkFileManager,
                          ICacheManager cacheManager, Long ttl, StoreType storeType, String cacheFolder) {
        super(networkFileManager, cacheManager, ttl, storeType, cacheFolder);
    }

    public void upload(File file, KinveyClientCallback<FileMetaData> metaCallback, UploaderProgressListener listener) throws IOException {
        new AsyncRequest<FileMetaData>(this, asyncMethods.get(FileMethods.UPLOAD_FILE), metaCallback, file, listener )
                .execute(AsyncClientRequest.ExecutorType.KINVEYSERIAL);
    }

    public void upload(File file, FileMetaData metadata, KinveyClientCallback<FileMetaData> metaCallback,
                               UploaderProgressListener listener) throws IOException {

        new AsyncRequest<FileMetaData>(this, asyncMethods.get(FileMethods.UPLOAD_FILE_METADATA), metaCallback,
                file, metadata, listener ).execute(AsyncClientRequest.ExecutorType.KINVEYSERIAL);
    }

    public void upload(InputStream is, FileMetaData metadata, KinveyClientCallback<FileMetaData> metaCallback, UploaderProgressListener listener) throws IOException {
        new AsyncRequest<FileMetaData>(this, asyncMethods.get(FileMethods.UPLOAD_STREAM_METADATA), metaCallback,
                is, metadata, listener ).execute(AsyncClientRequest.ExecutorType.KINVEYSERIAL);
    }

    public void upload(String filename, InputStream is, KinveyClientCallback<FileMetaData> metaCallback,
                       UploaderProgressListener listener) throws IOException {
        new AsyncRequest<FileMetaData>(this, asyncMethods.get(FileMethods.UPLOAD_STREAM_FILENAME), metaCallback,
                filename, is, listener ).execute(AsyncClientRequest.ExecutorType.KINVEYSERIAL);
    }

    public void delete(String id, KinveyDeleteCallback callback) throws IOException {
        new AsyncRequest<Integer>(this, asyncMethods.get(FileMethods.DELETE_ID), callback,
                id ).execute(AsyncClientRequest.ExecutorType.KINVEYSERIAL);
    }

    public void download(FileMetaData metadata, OutputStream os, KinveyClientCallback<FileMetaData> metaCallback,
                         DownloaderProgressListener progressListener) throws IOException {
        new AsyncRequest<FileMetaData>(this, asyncMethods.get(FileMethods.DOWNLOAD_METADATA), metaCallback,
                metaCallback, os, progressListener ).execute(AsyncClientRequest.ExecutorType.KINVEYSERIAL);
    }

    public void download(Query q, String dst, KinveyClientCallback<FileMetaData> metaCallback,
                         DownloaderProgressListener progressListener) throws IOException {

        new AsyncRequest<FileMetaData>(this, asyncMethods.get(FileMethods.DOWNLOAD_QUERY), metaCallback,
                metaCallback, q, dst, progressListener ).execute(AsyncClientRequest.ExecutorType.KINVEYSERIAL);

    }

    public void download(String filename, String dst, KinveyClientCallback<FileMetaData> metaCallback,
                         DownloaderProgressListener progressListener) throws IOException {
        new AsyncRequest<FileMetaData>(this, asyncMethods.get(FileMethods.DOWNLOAD_FILENAME), metaCallback,
                metaCallback, filename, dst, progressListener ).execute(AsyncClientRequest.ExecutorType.KINVEYSERIAL);
    }

    public void clear() {
    }
}