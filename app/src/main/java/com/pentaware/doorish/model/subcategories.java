package com.pentaware.doorish.model;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class subcategories implements Serializable {

    public String Category;

    public double Commission;

    public List<String> sub_categories = new ArrayList<>();

    public List<String> img_url = new ArrayList<>();
}
