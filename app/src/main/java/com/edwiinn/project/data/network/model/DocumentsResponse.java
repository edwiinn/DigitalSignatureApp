package com.edwiinn.project.data.network.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.File;
import java.util.List;

public class DocumentsResponse {

    @Expose
    @SerializedName("data")
    private List<Document> documents;

    @Expose
    @SerializedName("count")
    private Integer count;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    public static class Document implements Parcelable {

        @Expose
        @SerializedName("title")
        private String mName;

        @Expose
        @SerializedName("id")
        private Integer id;

        @Expose
        @SerializedName("is_signed")
        public Boolean isSigned;

        private Boolean isUserSigned;

        public String getName() {
            return mName;
        }

        public void setName(String name) {
            this.mName = name;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Boolean getSigned() {
            isSigned = isSigned == null ? false : isSigned;
            return isSigned;
        }

        public void setSigned(Boolean signed) {
            isSigned = signed;
        }

        public Document(int id, String name, boolean isSigned){
            mName = name;
            this.id = id;
            this.isSigned = isSigned;
        }

        public Document(Parcel in){
            String[] data = new String[3];

            in.readStringArray(data);
            this.id = Integer.parseInt(data[0]);
            this.mName = data[1];
            this.isSigned = Boolean.parseBoolean(data[2]);
        }

        public static final Creator<Document> CREATOR = new Creator<Document>() {
            @Override
            public Document createFromParcel(Parcel in) {
                return new Document(in);
            }

            @Override
            public Document[] newArray(int size) {
                return new Document[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeStringArray(new String[]{this.id.toString(), this.mName, this.isSigned.toString()});
        }

        public Boolean getUserSigned() {
            return isUserSigned = isUserSigned == null ? false : isUserSigned;
        }

        public void setUserSigned(Boolean userSigned) {
            isUserSigned = userSigned;
        }

        public boolean checkIfDocumentUserSigned(String signedStoragePath){
            if (isSigned) return false;
            File file = new File(signedStoragePath, mName);
            isUserSigned = file.exists();
            return file.exists();
        }
    }
}
