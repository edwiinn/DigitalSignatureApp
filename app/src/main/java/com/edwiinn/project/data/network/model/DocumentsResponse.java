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

        private Boolean isSigned;

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

        public Boolean checkIfDocumentSigned(String folderSrc) {
            File file = new File(folderSrc, mName);
            isSigned = file.exists();
            return file.exists();
        }

        public Document(Parcel in){
            String[] data = new String[2];

            in.readStringArray(data);
            this.id = Integer.parseInt(data[0]);
            this.mName = data[1];
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
            dest.writeStringArray(new String[]{this.id.toString(), this.mName});
        }
    }
}
