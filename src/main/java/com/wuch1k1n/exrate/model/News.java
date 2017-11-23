package com.wuch1k1n.exrate.model;

/**
 * Created by Administrator on 2017/11/22.
 */

public class News {
    /**
     * 新闻id
     */
    private String id;
    /**
     * 新闻图片url
     */
    private String imageUrl;
    /**
     * 新闻标题
     */
    private String title;
    /**
     * 网页url
     */
    private String webUrl;
    /**
     * 新闻条目是否已读
     */
    private Boolean isRead = false;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public Boolean getRead() {
        return isRead;
    }

    public void setRead(Boolean read) {
        isRead = read;
    }
}
