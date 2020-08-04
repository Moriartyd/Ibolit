package com.example.dima.mdbloader;

/**
 * Created by dima on 10.02.2017.
 */

public class Lekarstvo {
    private String name,id;
    private int price;

    public Lekarstvo(String name,String id, int price){
        this.setId(id);
        this.setName(name);
        this.setPrice(price);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
