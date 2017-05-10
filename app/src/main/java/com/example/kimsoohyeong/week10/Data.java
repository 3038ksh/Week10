package com.example.kimsoohyeong.week10;

/**
 * Created by KimSooHyeong on 2017. 5. 6..
 */

public class Data {
    String name;
    String url;

    public Data(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String toString() {
        return "<" + name + "> " + url;
    }

    public String getUrl() {
        return url;
    }
}
