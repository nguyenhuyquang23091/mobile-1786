package com.example.coursework.data.local.entities;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.firebase.firestore.Exclude;


@Entity(tableName = "yoga_courses")
public class YogaCourse {
    @PrimaryKey(autoGenerate = true)
    @Exclude
    public int uid;
    @ColumnInfo(name = "day")
    public String day;
    @ColumnInfo(name = "time")
    public String time;
    @ColumnInfo(name = "capacity")
    public Integer capacity;
    @ColumnInfo(name = "duration")
    public Integer duration;
    @ColumnInfo(name = "price")
    public Double price;
    @ColumnInfo(name = "type")
    public String type;
    @ColumnInfo(name = "    description")
    public String description;
    @ColumnInfo(name = "intensity")
    public String intensity;

    public YogaCourse(){

    }
}
