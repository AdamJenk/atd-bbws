package com.alltheducks.bbws.model;

/**
 * Created by Wiley Fuller on 4/03/15.
 * Copyright All the Ducks Pty. Ltd.
 */
public class AssessmentItemDto {
    private String type = "assessment";
    private String id;
    private Attributes attributes;
    //private String name;
    //private double pointsPossible;
    //private String courseId;
    //private ValueType valueType;

    public String getType() {
        return this.type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setAttributes(Attributes attributes){
        this.attributes = attributes;
    }

    public Attributes getAttributes(){
        return attributes;
    }
    
    public static class Attributes {
        public String name;
        public double pointsPossible;
        public String courseId;
    }

    /*
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPointsPossible() {
        return pointsPossible;
    }

    public void setPointsPossible(double pointsPossible) {
        this.pointsPossible = pointsPossible;
    }

    public String getCourseId() {
        return this.courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    
    public ValueType getValueType() {
        return valueType;
    }

    public void setValueType(ValueType valueType) {
        this.valueType = valueType;
    }

    public enum ValueType {
        TEXT,
        NUMBER,
        PERCENT,
        DATE
    }
    */
    
}

