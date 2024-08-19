package org.example;

import java.io.File;

public class Listing {
    public String title;
    public String category;
    public String type;
    public String price;
    public String description;

    public transient  File[] photos;
}
