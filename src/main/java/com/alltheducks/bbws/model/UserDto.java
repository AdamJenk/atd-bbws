package com.alltheducks.bbws.model;


public class UserDto {
	private String type = "user";
	private String id;
    private Attributes attributes;
    //private String uun;
    //private String familyName;
    //private String middleName;
    //private String givenName;

    //Something for relationships
	
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
        public String uun;
        public String familyName;
        public String middleName;
        public String givenName;
    }
    
    /*
    public String getUun() {
        return uun;
    }

    public void setUun(String uun) {
        this.uun = uun;
    }
    
    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }
/*
    public List<CourseDto> getChildren() {
        return children;
    }

    public void setChildren(List<CourseDto> children) {
        this.children = children;
    }
*/
}