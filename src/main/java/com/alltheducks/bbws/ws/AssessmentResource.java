package com.alltheducks.bbws.ws;

import blackboard.data.course.Course;
import blackboard.data.user.User;
import blackboard.persist.Id;
import blackboard.persist.KeyNotFoundException;
import blackboard.persist.PersistenceException;
import blackboard.persist.course.CourseDbLoader;
import blackboard.persist.gradebook.LineitemDbLoader;
import blackboard.persist.user.UserDbLoader;
import blackboard.platform.gradebook2.*;
import blackboard.platform.gradebook2.impl.*;
import blackboard.data.gradebook.Lineitem;
import blackboard.platform.context.*;
import blackboard.platform.security.Entitlement;
import blackboard.platform.security.SecurityUtil;
import blackboard.platform.session.BbSession;
import blackboard.db.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alltheducks.bbws.model.AssessmentItemDto;
import com.alltheducks.bbws.model.CourseDto;
import com.alltheducks.bbws.model.AttemptDto;
import com.alltheducks.bbws.util.BbCourseHelper;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;



/**
 * Created by Harry Scells on 5/05/2016.
 * Copyright All the Ducks Pty. Ltd.
 */
@Path("assessment")
public class AssessmentResource {
    final Logger logger = LoggerFactory.getLogger(BbCourseHelper.class);

    
    @Inject
    private GradableItemDAO gradableItemDAO;
    @Inject
    private GradingSchemaDAO gradingSchemaDAO;
    @Inject
    private UserDbLoader userDbLoader;

    @GET 
    @Path("/{assessmentId}")
    @Produces(MediaType.APPLICATION_JSON)
    public AssessmentItemDto listAssessments(@PathParam("assessmentId") String assessmentIdStr) {
       AssessmentItemDto assessmentFinal = new AssessmentItemDto();
       try {
            LineitemDbLoader lineitemDbLoader = LineitemDbLoader.Default.getInstance();
            Id assessmentId = Id.generateId(GradableItem.DATA_TYPE, assessmentIdStr);
            Lineitem assessment = lineitemDbLoader.loadById(assessmentId);
            AssessmentItemDto.Attributes attributes = new AssessmentItemDto.Attributes();
            assessmentFinal.setId(assessmentIdStr);
            attributes.name = assessment.getName();
            attributes.pointsPossible = assessment.getPointsPossible();
            Course temp = (Course)assessment.getCourseId().load();
            attributes.courseId = temp.getCourseId();
            assessmentFinal.setAttributes(attributes);

        } catch (KeyNotFoundException ex) {
            logger.debug(String.format("No Assessment with AssessmentId {} found.", assessmentIdStr));
            throw new WebApplicationException(String.format("No Assessment with AssessmentId %s found.", assessmentIdStr), 404);
        } catch (PersistenceException ex) {
            logger.error("Error while retrieving assessment", ex);
            throw new WebApplicationException("Error retrieving Assessment", 500);
        }

        return assessmentFinal;

    }

    @GET 
    @Path("/{assessmentId}/course")
    @Produces(MediaType.APPLICATION_JSON)
    public CourseDto showCourse(@PathParam("assessmentId") String assessmentIdStr) {
        CourseDto finalCourse = null;
        try {
            LineitemDbLoader lineitemDbLoader = LineitemDbLoader.Default.getInstance();
            Id assessmentId = Id.generateId(GradableItem.DATA_TYPE, assessmentIdStr);
            Lineitem assessment = lineitemDbLoader.loadById(assessmentId);
            Course courseForAs = (Course)assessment.getCourseId().load();
            finalCourse = BbCourseHelper.convertBbCourseToCourseDto(courseForAs);

        } catch (KeyNotFoundException ex) {
            logger.debug(String.format("No Assessment with AssessmentId {} found.", assessmentIdStr));
            throw new WebApplicationException(String.format("No Assessment with AssessmentId %s found.", assessmentIdStr), 404);
        } catch (PersistenceException ex) {
            logger.error("Error while retrieving assessment", ex);
            throw new WebApplicationException("Error retrieving Assessment", 500);
        }
        
        return finalCourse;
    }

    @GET 
    @Path("/{assessmentId}/attempts")
    @Produces(MediaType.APPLICATION_JSON)
    public List<AttemptDto> getAssessmentAttempts(@PathParam("assessmentId") String assessmentIdStr) {

        ConnectionManager cManager = null; 
        Connection conn = null;  
        Statement stmt = null;
        List<AttemptDto> attempts = new ArrayList<>();
        //SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
  
        try {  
    
            cManager = BbDatabase.getDefaultInstance().getConnectionManager();  
            conn = cManager.getConnection();  


            stmt = conn.createStatement();
            String sql =  "select a.pk1 as attempt_id, a.attempt_date as attempt_date, a.grade, gm.pk1 as assess_id, u.user_id from gradebook_main as gm inner join gradebook_grade as gg on gg.gradebook_main_pk1 = gm.pk1 inner join attempt as a on a.gradebook_grade_pk1 = gg.pk1 inner join users as u on gg.course_users_pk1 = u.pk1 where gm.pk1 ="+assessmentIdStr;

            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                AttemptDto attempt = new AttemptDto();
                AttemptDto.Attributes attributes = new AttemptDto.Attributes();


                int attemptId = rs.getInt(1);
                String attmId = attemptId+"";
                attempt.setId(attmId);

                Timestamp date = rs.getTimestamp(2);
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(date.getTime());
                attributes.attempt_date = cal;

                String grade = rs.getString(3);
                attributes.grade = grade;

                int assessId = rs.getInt(4);
                String assId = assessId+"";
                attributes.assessmentId = assId;

                String uun = rs.getString(5);
                attributes.uun = uun;
                
                
                attempt.setAttributes(attributes);
                attempts.add(attempt);
        
            }

            rs.close();

        } catch (java.sql.SQLException sE) {  
            logger.error(sE.getMessage());  
        } catch (ConnectionNotAvailableException cE) {  
            logger.error(cE.getMessage());  
        } finally {  
            if (conn != null) {  
            cManager.releaseConnection(conn);  
            }  
        }
        
        /*

        for (AttemptDto attempt:attempts) {
            String username = attempt.attributes.uun;
            String assessmentId = attempt.attributes.assessmentId;
            AttemptDto.Relationships relationships = new AttemptDto.Relationships();
            relationships.assessment.id = assessmentId;
            attempt.setRelationships(relationships);
            
            try {
                //Id assId = Id.generateId(GradableItem.DATA_TYPE, assessmentId);
                relationships.assessment.id = assessmentId; //.toExternalString();
                //User bbUser = userDbLoader.loadByUserName(username);
                //Id bbUserId = bbUser.getId();
                //relationships.user.id = bbUserId.toExternalString();
                attempt.setRelationships(relationships);

            } catch (KeyNotFoundException ex) {
                logger.debug(String.format("No Course with CourseId {} found.", assessmentId));
                throw new WebApplicationException(String.format("No Course with CourseId %s found.", assessmentId), 404);
            } catch (PersistenceException ex) {
                logger.error("Error while retrieving courses", ex);
                throw new WebApplicationException("Error retrieving Courses", 500);
            }
          
             

        }
          */
        return attempts;
        
    }

}