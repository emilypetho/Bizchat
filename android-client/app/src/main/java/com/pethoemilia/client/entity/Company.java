package com.pethoemilia.client.entity;

import java.util.ArrayList;
import java.util.List;

public class Company {
    protected Long id;
    private String name;
    private String address;

    public Company(String name,String address) {
        this.name = name;
        this.address = address;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
