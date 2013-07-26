/** 
 * Copyright (c) 2013, Kinvey, Inc. All rights reserved.
 *
 * This software contains valuable confidential and proprietary information of
 * KINVEY, INC and is subject to applicable licensing agreements.
 * Unauthorized reproduction, transmission or distribution of this file and its
 * contents is a violation of applicable laws.
 * 
 */
package com.kinvey.java.core;


import com.kinvey.java.model.FileMetaData;

import java.io.IOException;


/**
 * An interface for receiving progress notifications for uploads.
 *
 * <p>
 * Sample usage:
 * </p>
 *
 * <pre>
  public static class MyUploadProgressListener implements UploaderProgressListener {

    public void progressChanged(MediaHttpUploader uploader) throws IOException {
      switch (uploader.getUploadState()) {
        case INITIATION_STARTED:
          System.out.println("Initiation Started");
          break;
        case INITIATION_COMPLETE:
          System.out.println("Initiation Completed");
          break;
        case DOWNLOAD_IN_PROGRESS:
          System.out.println("Upload in progress");
          System.out.println("Upload percentage: " + uploader.getProgress());
          break;
        case DOWNLOAD_COMPLETE:
          System.out.println("Upload Completed!");
          break;
      }
    }
  }
 * </pre>
 *
 */
public interface UploaderProgressListener extends KinveyClientCallback<Void> {

    /**
     * Called to notify that progress has been changed.
     * <p/>
     * <p>
     * This method is called once before and after the initiation request. For media uploads it is
     * called multiple times depending on how many chunks are uploaded. Once the upload completes it
     * is called one final time.
     * </p>
     * <p/>
     * <p>
     * The upload state can be queried by calling {@link MediaHttpUploader#getUploadState} and the
     * progress by calling {@link MediaHttpUploader#getProgress}.
     * </p>
     *
     * @param uploader Media HTTP uploader
     */
    public void progressChanged(MediaHttpUploader uploader) throws IOException;


}
