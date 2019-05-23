package com.sdsoon.client;

import java.io.Serializable;

/**
 * Created By Chr on 2019/4/17.
 */
public class TestBean implements Serializable {

    private static final long serialVersionUID = 425589149527593254L;


    private String comm;

    private String id;

    @Override
    public String toString() {
        return "TestBean{" +
                "comm='" + comm + '\'' +
                ", id='" + id + '\'' +
                '}';
    }


    public String getComm() {
        return comm;
    }

    public void setComm(String comm) {
        this.comm = comm;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

