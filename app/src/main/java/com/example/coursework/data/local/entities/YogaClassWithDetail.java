package com.example.coursework.data.local.entities;

import androidx.room.Embedded;
import androidx.room.Relation;

public class YogaClassWithDetail {

    @Embedded
    public YogaClass yogaClass;
    @Relation(
            parentColumn = "courseId",
            entityColumn = "uid"
    )
    public YogaCourse yogaCourse;

}
