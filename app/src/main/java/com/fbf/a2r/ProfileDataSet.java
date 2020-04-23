package com.fbf.a2r;

public class ProfileDataSet {
    String ProfileName;
    String ProfileImageUrl;
    String AllPostViewCount;
    String AllAdDisplayCount;
    String ProfileUid;
    String MypostCount;
    String ProfileImageName;

    public ProfileDataSet(){}

    public ProfileDataSet(String ProfileName, String ProfileImageUrl, String AllPostViewCount, String AllAdDisplayCount, String ProfileUid, String MypostCount, String ProfileIamgeName){
        this.ProfileName = ProfileName;
        this.ProfileImageUrl = ProfileImageUrl;
        this.AllPostViewCount = AllPostViewCount;
        this.AllAdDisplayCount = AllAdDisplayCount;
        this.ProfileUid = ProfileUid;
        this.MypostCount = MypostCount;
        this.ProfileImageName = ProfileIamgeName;
    }

    public String getProfileName() {
        return ProfileName;
    }

    public void setProfileName(String profileName) {
        ProfileName = profileName;
    }

    public String getProfileImageUrl() {
        return ProfileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        ProfileImageUrl = profileImageUrl;
    }

    public String getAllPostViewCount() {
        return AllPostViewCount;
    }

    public void setAllPostViewCount(String allPostViewCount) {
        AllPostViewCount = allPostViewCount;
    }

    public String getAllAdDisplayCount() {
        return AllAdDisplayCount;
    }

    public void setAllAdDisplayCount(String allAdDisplayCount) {
        AllAdDisplayCount = allAdDisplayCount;
    }

    public String getProfileUid() {
        return ProfileUid;
    }

    public void setProfileUid(String profileUid) {
        ProfileUid = profileUid;
    }

    public String getMypostCount() {
        return MypostCount;
    }

    public void setMypostCount(String mypostCount) {
        MypostCount = mypostCount;
    }

    public String getProfileImageName() {
        return ProfileImageName;
    }

    public void setProfileImageName(String profileImageName) {
        ProfileImageName = profileImageName;
    }
}
