package com.edwiinn.project.data.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GoogleResponse {

    public static class UserInfo{

        @Expose
        @SerializedName("sub")
        private String id;

        @Expose
        @SerializedName("name")
        private String mName;

        @Expose
        @SerializedName("picture")
        private String mProfilePicUrl;

        public String getName() {
            return mName;
        }

        public void setName(String name) {
            mName = name;
        }

        public String getProfilePicUrl() {
            return mProfilePicUrl;
        }

        public void setProfilePicUrl(String profilePicUrl) {
            mProfilePicUrl = profilePicUrl;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}
