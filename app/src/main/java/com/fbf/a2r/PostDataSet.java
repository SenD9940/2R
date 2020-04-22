package com.fbf.a2r;

import android.view.View;

import com.google.firebase.auth.FirebaseUser;

public class PostDataSet {
    private String PostTitle;
    private String PostExplanation;
    private String PostEditor;
    private String PostViewCount;
    private String PostAdExposureCount;
    private String Uid;
    private String CommentOption;
    private String DownloadURL;
    private String firebaseUser;
    private String Category;
    private String ImageName;

    public PostDataSet(){ }

    public PostDataSet(String PostTitle, String PostExplanation, String PostEditor, String CommentOption, String PostViewCount, String AdViewCount, String Uid, String firebaseUserImage, String Category){
        this.PostTitle = PostTitle;
        this.PostEditor = PostEditor;
        this.PostExplanation = PostExplanation;
        this.CommentOption = CommentOption;
        this.PostViewCount = PostViewCount;
        this.PostAdExposureCount = AdViewCount;
        this.firebaseUser = firebaseUserImage;
        this.Category = Category;
        this.Uid = Uid;
    }
    public PostDataSet(String PostTitle, String PostExplanation, String PostEditor, String CommentOption, String DownloadURL, String PostViewCount, String AdViewCount, String Uid, String firebaseUserImage, String Category, String ImageName){
        this.firebaseUser = firebaseUserImage;
        this.PostTitle = PostTitle;
        this.PostEditor = PostEditor;
        this.PostExplanation = PostExplanation;
        this.CommentOption = CommentOption;
        this.DownloadURL = DownloadURL;
        this.PostViewCount = PostViewCount;
        this.PostAdExposureCount = AdViewCount;
        this.Category = Category;
        this.Uid = Uid;
        this.ImageName = ImageName;
    }

    public String getCommentOption() {
        return CommentOption;
    }

    public void setCommentOption(String commentOption) {
        CommentOption = commentOption;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }


    public String getPostTitle() {
        return PostTitle;
    }

    public void setPostTitle(String postTitle) {
        PostTitle = postTitle;
    }

    public String getPostExplanation() {
        return PostExplanation;
    }

    public void setPostExplanation(String postExplanation) {
        PostExplanation = postExplanation;
    }

    public String getPostEditor() {
        return PostEditor;
    }

    public void setPostEditor(String postEditor) {
        PostEditor = postEditor;
    }

    public String getPostViewCount() {
        return PostViewCount;
    }

    public void setPostViewCount(String postViewCount) {
        PostViewCount = postViewCount;
    }

    public String getPostAdExposureCount() {
        return PostAdExposureCount;
    }

    public void setPostAdExposureCount(String postAdExposureCount) {
        PostAdExposureCount = postAdExposureCount;
    }

    public String getDownloadURL() {
        return DownloadURL;
    }

    public void setDownloadURL(String downloadURL) {
        DownloadURL = downloadURL;
    }

    public String getFirebaseUser() {
        return firebaseUser;
    }

    public void setFirebaseUser(String firebaseUser) {
        this.firebaseUser = firebaseUser;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }


    public String getImageName() {
        return ImageName;
    }

    public void setImageName(String imageName) {
        ImageName = imageName;
    }


}
