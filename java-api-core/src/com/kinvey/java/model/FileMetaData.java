/** 
 * Copyright (c) 2013, Kinvey, Inc. All rights reserved.
 *
 * This software contains valuable confidential and proprietary information of
 * KINVEY, INC and is subject to applicable licensing agreements.
 * Unauthorized reproduction, transmission or distribution of this file and its
 * contents is a violation of applicable laws.
 * 
 */
package com.kinvey.java.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

/**
 * This class maintains information about a file that has been stored with Kinvey.  Every file has a unique ID, as well as other attributes
 * including but not limited too: filename, size, mimetype, acl, and a public access flag.
 *
 *
 * This object can also maintain any custom attributes, which can be set via accessor methods provided by {@link GenericJson}
 *
 * @author edwardf
 */
public class FileMetaData extends GenericJson{


    @Key("_id")
    private String id;

    @Key("_filename")
    private String fileName;

    @Key("size")
    private long size;

    @Key("mimeType")
    private String mimetype;

    @Key("_acl")
    private KinveyMetaData.AccessControlList acl;

    @Key("_uploadURL")
    private String uploadUrl;

    @Key("_downloadURL")
    private String downloadURL;

    @Key("_public")
    private boolean _public = false;


    /**
     * Create a new instance, without setting any fields
     *
     * If an ID is not set, it will be auto-generated by the service
     *
     */
    public FileMetaData() {
    }

    /**
     * Create a new instance, and set the id of the file
     *
     * @param id - the _id of the new file meta data
     */
    public FileMetaData(String id){
        setId(id);
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getMimetype() {
        return mimetype;
    }

    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public KinveyMetaData.AccessControlList getAcl() {
        return acl;
    }

    public void setAcl(KinveyMetaData.AccessControlList acl) {
        this.acl = acl;
    }

    public String getUploadUrl() {
        return uploadUrl;
    }

    public void setUploadUrl(String uploadUrl) {
        this.uploadUrl = uploadUrl;
    }

    public String getDownloadURL() {
        return downloadURL;
    }

    public void setDownloadURL(String downloadURL) {
        this.downloadURL = downloadURL;
    }

    public boolean isPublic() {
        return this._public;
    }

    public void setPublic(boolean isPublic) {
        this._public = isPublic;
    }
}
