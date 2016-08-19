package com.alltheducks.bbws.model;

import java.util.*;
import java.text.*;


public class AttemptDto{
	private String type = "attempt";
	private String id;
	public Attributes attributes;
	private Relationships relationships;
	//private Calendar attempt_date;
	//private String grade;
	//private String assId;
	//private String uun;


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
        public Calendar attempt_date;
		public String grade;
		public String assessmentId;
		public String uun;
    }


    public static class Relationships {
    	public RefUser user; 
    	public RefAssessment assessment;
    }

    public void setRelationships(Relationships attributes){
        this.relationships = relationships;
    }

    public Relationships getRelationships(){
        return relationships;
    }

    public static class RefUser {
    	public String type = "user";
    	public String id; 
    }

    public static class RefAssessment {
    	public String type = "assessment";
    	public String id; 
    }

  
    /*

    public Calendar getAttemptDate() {
        return attempt_date;
    }

    public void setAttemptDate(Calendar attempt_date) {
        this.attempt_date = attempt_date;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getAssessmentId() {
        return assId;
    }

    public void setAssessmentId(String assId) {
        this.assId = assId;
    }
	
    public String getUun() {
        return uun;
    }

    public void setUun(String uun) {
        this.uun = uun;
    }
    
	private String formatDate(){
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		return dateFormat.format(this.attempt_date.getTime());
	}
	*/
}