package com.example.coursework.data.local.entities;

import androidx.room.Embedded;
import androidx.room.Relation;

public class ClassInstanceWIthDetail {

    @Embedded
    public ClassInstance classInstance;
    @Relation(
            parentColumn = "courseId",
            entityColumn = "uid"
    )
    public YogaCourse yogaCourse;

}
