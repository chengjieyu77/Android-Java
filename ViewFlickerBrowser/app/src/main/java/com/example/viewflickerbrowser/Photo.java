package com.example.viewflickerbrowser;

import java.io.Serializable;

class Photo implements Serializable {
    private static final long serialVersionUID = 1L;//the version number's used by the serialization code to check that the
    //data it's retrieving is the same version as the data that was stored. if you don't define it your own, java will generate one for you,
    //but different java version generate different id
    private String mTitle;
    private String mAuthor;
    private String mAuthorId;
    private String mImage;
    private String tags;
    private String mLink;

    public Photo(String title, String author, String authorId, String image, String tags, String link) {
        mTitle = title;
        mAuthor = author;
        mAuthorId = authorId;
        mImage = image;
        this.tags = tags;
        mLink = link;
    }

    String getTitle() {
        return mTitle;
    }
   String getAuthor() {
        return mAuthor;
    }

    String getAuthorId() {
        return mAuthorId;
    }

     String getImage() {
        return mImage;
    }

    String getTags() {
        return tags;
    }
    String getLink() {
        return mLink;
    }

    @Override
    public String toString() {
        return "Photo" +
                "mTitle='" + mTitle + '\n' +
                ", mAuthor='" + mAuthor + '\n' +
                ", mAuthorId='" + mAuthorId + '\n' +
                ", mImage='" + mImage + '\'' +
                ", tags='" + tags + '\n' +
                ", mLink='" + mLink + '\n';
    }
}
