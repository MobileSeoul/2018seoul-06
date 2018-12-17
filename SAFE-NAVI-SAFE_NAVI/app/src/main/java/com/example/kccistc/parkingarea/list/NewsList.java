package com.example.kccistc.parkingarea.list;

public class NewsList {
    private String title;
    private String description;
    private String publishedAt;
    private String channelTitle;
    private String url;
    private String videoId;

    public String getVideoId() { return videoId; }

    public void setVideoId(String videoId) { this.videoId = videoId; }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getChannelTitle() {
        return channelTitle;
    }

    public void setChannelTitle(String channelTitle) {
        this.channelTitle = channelTitle;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
