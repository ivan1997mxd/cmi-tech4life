package com.estethapp.media.helper;

import androidx.annotation.NonNull;

/**
 * Created by Irfan Ali on 1/2/2017.
 */
public class Item implements Comparable<Item>{
    private String name;
    private String path;
    private int image;

    public Item(String n,String p, int img)
    {
        name = n;
        path = p;
        image = img;
    }
    public String getName()
    {
        return name;
    }
    public String getPath()
    {
        return path;
    }

    public int getImage() {
        return image;
    }
    public int compareTo(Item o) {
        if(this.name != null)
            return this.name.toLowerCase().compareTo(o.getName().toLowerCase());
        else
            throw new IllegalArgumentException();
    }
}