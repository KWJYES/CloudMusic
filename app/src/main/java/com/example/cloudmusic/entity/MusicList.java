package com.example.cloudmusic.entity;

import java.io.Serializable;

/**
 * 歌单
 */
public class MusicList implements Serializable {

    private Long id;
    private Integer type;
    private String name;
    private String copywriter;
    private String picUrl;
    private Boolean canDislike;
    private Long trackNumberUpdateTime;
    private Long playCount;
    private Integer trackCount;
    private Boolean highQuality;
    private String alg;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCopywriter() {
        return copywriter;
    }

    public void setCopywriter(String copywriter) {
        this.copywriter = copywriter;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public Boolean getCanDislike() {
        return canDislike;
    }

    public void setCanDislike(Boolean canDislike) {
        this.canDislike = canDislike;
    }

    public Long getTrackNumberUpdateTime() {
        return trackNumberUpdateTime;
    }

    public void setTrackNumberUpdateTime(Long trackNumberUpdateTime) {
        this.trackNumberUpdateTime = trackNumberUpdateTime;
    }

    public Long getPlayCount() {
        return playCount;
    }

    public void setPlayCount(Long playCount) {
        this.playCount = playCount;
    }

    public Integer getTrackCount() {
        return trackCount;
    }

    public void setTrackCount(Integer trackCount) {
        this.trackCount = trackCount;
    }

    public Boolean getHighQuality() {
        return highQuality;
    }

    public void setHighQuality(Boolean highQuality) {
        this.highQuality = highQuality;
    }

    public String getAlg() {
        return alg;
    }

    public void setAlg(String alg) {
        this.alg = alg;
    }
}
