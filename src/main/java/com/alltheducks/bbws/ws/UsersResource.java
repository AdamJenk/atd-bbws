package com.alltheducks.bbws.ws;

import blackboard.base.BbList;
import blackboard.data.course.Course;
import blackboard.data.user.User;
import blackboard.persist.Id;
import blackboard.persist.KeyNotFoundException;
import blackboard.persist.PersistenceException;
import blackboard.persist.course.CourseDbLoader;
import blackboard.persist.user.UserDbLoader;
import blackboard.platform.context.*;
import blackboard.platform.security.Entitlement;
import blackboard.platform.security.SecurityUtil;
import blackboard.platform.session.BbSession;
import blackboard.db.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alltheducks.bbws.model.AttemptDto;
import com.alltheducks.bbws.model.CourseDto;
import com.alltheducks.bbws.model.UserDto;
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
@Path("users")
public class UsersResource {
    final Logger logger = LoggerFactory.getLogger(BbCourseHelper.class);

    @Inject
    private CourseDbLoader courseDbLoader;
    @Inject
    private UserDbLoader userDbLoader;

    @GET 
    @Produces(MediaType.APPLICATION_JSON)
    public List<UserDto> listUsers() {
        List<UserDto> returnedList = new ArrayList<>();
        Context context = ContextManagerFactory.getInstance().getContext();
        BbSession session = context.getSession();
        Id observerId = session.getUserId();
        try { 
            BbList<User> viewableUsers = userDbLoader.loadObservedByObserverId(observerId);
            //returnedList = BbCourseHelper.convertBbListToListUserDto(viewableUsers);
        } catch (KeyNotFoundException ex) {
            //logger.debug(String.format("No User with UserId {} found.", courseId));
            throw new WebApplicationException(String.format("No User with UserId %s found.", observerId.toExternalString()), 404);
        } catch (PersistenceException ex) {
            //logger.error("Error while retrieving courses", ex);
            throw new WebApplicationException("Error retrieving Users", 500);
        }
        
        //changed the return to list of users will need to create a helper which converts between.
        return returnedList;

    }

    @GET 
    @Path("/{userId}/attempts")
    @Produces(MediaType.APPLICATION_JSON)
    public List<AttemptDto> getCourseUsers(@PathParam("userId") String userIdStr) {

        ConnectionManager cManager = null; 
        Connection conn = null;  
        Statement stmt = null;
        List<AttemptDto> attempts = new ArrayList<>();
  
        try {  
    
            cManager = BbDatabase.getDefaultInstance().getConnectionManager();  
            conn = cManager.getConnection();  


            stmt = conn.createStatement();
            String sql =  "select a.pk1 as attempt_id, a.attempt_date as attempt_date, a.grade, gm.pk1 as assess_id, u.user_id from gradebook_main as gm inner join gradebook_grade as gg on gg.gradebook_main_pk1 = gm.pk1 inner join attempt as a on a.gradebook_grade_pk1 = gg.pk1 inner join users as u on gg.course_users_pk1 = u.pk1 where u.user_id = '"+userIdStr+ "'";

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
        
        return attempts;
    }


    @GET
    @Path("{userId}/courses")
    @Produces(MediaType.APPLICATION_JSON)
    public List<CourseDto> getCoursesForUser(@PathParam("userId") String username, @QueryParam("entitlement") @DefaultValue("") String entitlement) throws PersistenceException {

        User bbUser = userDbLoader.loadByUserName(username);
        Id bbUserId = bbUser.getId();

        List<Course> courses = courseDbLoader.loadByUserId(bbUserId);

        if (entitlement.isEmpty()) {
            // no entitlement specified, return all the courses
            return BbCourseHelper.convertBbCoursesToCourseDtos(courses);
        } else {
            // otherwise, filter courses based on the users entitlement
            Entitlement bbEntitlement = new Entitlement(entitlement);
            List<Course> filteredCourses = new ArrayList<>();
            for (Course course : courses) {
                if (SecurityUtil.userHasEntitlement(bbUserId, course.getId(), bbEntitlement)) {
                    filteredCourses.add(course);
                }
            }
            return BbCourseHelper.convertBbCoursesToCourseDtos(filteredCourses);
        }
    }

}
