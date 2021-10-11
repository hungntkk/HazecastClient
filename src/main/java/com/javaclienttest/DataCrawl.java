package com.javaclienttest;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataCrawl implements Serializable {
    @JsonProperty("countSite")
    private int countSite;

    @JsonProperty("data")
    private String data;

    @JsonProperty("maxNumSite")
    private int maxNumSite;

    @JsonProperty("listSiteVisitted")
    public List<String> listSiteVisitted;

    public DataCrawl(int countSite, String data, int maxNumSite) {
        this.countSite = countSite;
        this.data = data;
        this.maxNumSite = maxNumSite;
        this.listSiteVisitted = new ArrayList<>();
    }

    public DataCrawl() {
    }

    public int getMaxNumSite() {
        return maxNumSite;
    }

    public void setMaxNumSite(int maxNumSite) {
        this.maxNumSite = maxNumSite;
    }

    public void setCountSite(int countSite) {
        this.countSite = countSite;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getCountSite() {
        return countSite;
    }

    public void add1CountSite() {
        this.countSite++;
    }

    public void setListSiteVisitted(List<String> listSiteVisitted) {
        this.listSiteVisitted = listSiteVisitted;
    }

    public List<String> getListSiteVisitted() {
        return listSiteVisitted;
    }

    public void addVisittedSite (String site){
        this.listSiteVisitted.add(site);
    }

    public String getData() {
        return data;
    }
}
