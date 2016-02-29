package com.example.mjai37.database;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by mjai37 on 1/21/2016.
 */

@DatabaseTable(tableName = "GOODIE")
public class Goodie implements Serializable {

    @DatabaseField(generatedId = true)
    private Integer id;

    @DatabaseField
    private String name;

    @DatabaseField
    private String cost;

    @DatabaseField(dataType = DataType.STRING_BYTES)
    private String image;

    @DatabaseField
    private String goodieType;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getGoodieType() {
        return goodieType;
    }

    public void setGoodieType(String goodieType) {
        this.goodieType = goodieType;
    }
}
