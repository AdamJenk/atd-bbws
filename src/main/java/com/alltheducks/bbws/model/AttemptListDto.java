package com.alltheducks.bbws.model;

import java.util.*;
import java.text.*;


class AttemptList{
	private String type = "attemptList";
	private String id;
	private String assId;
	private String uun;
	private String title;
	private List attempts = new ArrayList<Attempt>();
	private String latest_submission;
	private String latest_marked;
	private Calendar latest_submission_date;
	private Calendar latest_marked_date;

	public AttemptList(String assId, String uun, String title, String latest_submission,
									 String latest_marked, Calendar latest_submission_date, Calendar latest_marked_date){
		this.assId = assId;
		this.uun = uun;
		this.id= assId+"_"+uun;
		this.title = title;
		this.latest_submission = latest_submission;
		this.latest_marked = latest_marked;
		this.latest_submission_date = latest_submission_date;
		this.latest_marked_date = latest_marked_date;
	}

	public String getAssessmentId() {
		return this.assId;
	}

	public void setAssessmentId(String assId) {
		this.assId = assId;
	}
	public String getUun() {
		return this.uun;
	}

	public void setUun(String uun) {
		this.uun = uun;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLatestSub() {
		return this.latest_submission;
	}

	public void setLatestSub(String latest_submission) {
		this.latest_submission = latest_submission;
	}

	public String getLatestMark() {
		return this.latest_marked;
	}

	public void setLatestMark(String latest_marked) {
		this.latest_marked = latest_marked;
	}

	public Calendar getLatestSubDate() {
		return this.latest_submission_date;
	}

	public void setLatestSubDate(Calendar latest_submission_date) {
		this.latest_submission_date = latest_submission_date;
	}

	public Calendar getLatestMarkDate() {
		return this.latest_marked_date;
	}

	public void setLatestMarkDate(Calendar latest_marked_date) {
		this.latest_marked_date = latest_marked_date;
	}



	private String formatDate(Calendar date){
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		return dateFormat.format(date.getTime());
	}

	public String getId() {
		return this.id;
	}

	public void setId() {
		this.id = this.assId + "_" + this.uun;
	}
}

class Attempt{
	private String attempt_id;
	private Calendar attempt_date;
	private String grade;

	public String getAttemptId() {
        return attempt_id;
    }

    public void setAttemptId(String attempt_id) {
        this.attempt_id = attempt_id;
    }

    public Calendar getAttemptDate() {
        return attempt_date;
    }

    public void setAttemptDate(Calendar attempt_date) {
        this.attempt_date = attempt_date;
    }

    public String getGrade() {
        return grade;
    }

    public void setExternalId(String grade) {
        this.grade = grade;
    }


	private String formatDate(){
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		return dateFormat.format(this.attempt_date.getTime());
	}
}