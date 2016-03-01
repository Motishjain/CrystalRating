package com.example.mjai37.database;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by mjai37 on 1/21/2016.
 */

@DatabaseTable(tableName = "QUESTION")
public class Question implements Serializable {

    @DatabaseField(generatedId = true)
    private Integer id;

    @DatabaseField
    private String name;

    @DatabaseField
    private String ratingValues;


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

    public String getRatingValues() {
        return ratingValues;
    }

    public void setRatingValues(String ratingValues) {
        this.ratingValues = ratingValues;
    }
}
