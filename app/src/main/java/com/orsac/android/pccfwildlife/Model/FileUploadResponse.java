package com.orsac.android.pccfwildlife.Model;

import com.google.gson.annotations.SerializedName;

public class FileUploadResponse {

    @SerializedName("folderName")
    public String folderName;
    @SerializedName("fileName")
    public String fileName;

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
