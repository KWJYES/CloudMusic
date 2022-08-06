package com.example.cloudmusic.entity;

import com.example.cloudmusic.base.BaseEntity;

import org.litepal.crud.LitePalSupport;

public class HistorySearch extends BaseEntity {
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private String keywords;

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }
}
