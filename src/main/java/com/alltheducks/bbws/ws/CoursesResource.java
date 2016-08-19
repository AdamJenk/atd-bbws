package com.alltheducks.bbws.ws;

import blackboard.data.course.Course;
import blackboard.data.course.CourseCourse;
import blackboard.data.user.User;
import blackboard.persist.Id;
import blackboard.persist.KeyNotFoundException;
import blackboard.persist.PersistenceException;
import blackboard.persist.course.CourseCourseDbLoader;
import blackboard.persist.course.CourseDbLoader;
import blackboard.data.course.CourseMembership;
import blackboard.persist.course.CourseMembershipDbLoader;
import blackboard.persist.user.UserDbLoader;
import blackboard.platform.gradebook2.*;
import blackboard.platform.gradebook2.impl.*;
import blackboard.platform.ws.*;
import blackboard.base.BbList;
import blackboard.db.*;

import com.alltheducks.bbws.model.AssessmentItemDto;
import com.alltheducks.bbws.model.CourseDto;
import com.alltheducks.bbws.model.MarkDto;
import com.alltheducks.bbws.model.UserDto;
import com.alltheducks.bbws.security.RequiresAuthentication;
import com.alltheducks.bbws.util.BbCourseHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import blackboard.platform.context.Context;
import blackboard.platform.context.ContextManagerFactory;
import blackboard.platform.session.BbSession;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;

@Path("courses")
//@RequiresAuthentication
public class CoursesResource {
    final Logger logger = LoggerFactory.getLogger(CoursesResource.class);
    public static final String CLICHED_MESSAGE = "Hello World!";

    @Inject
    private CourseDbLoader courseDbLoader;

    @Inject
    private GradableItemDAO gradableItemDAO;
    @Inject
    private GradingSchemaDAO gradingSchemaDAO;
    @Inject
    private UserDbLoader userDbLoader;
    @Inject
    private GradeDetailDAO gradeDetailDAO;

    @Inject
    private CourseCourseDbLoader courseCourseDbLoader;


    @GET
    @Produces("application/json")
    public List<CourseDto> listCourses() {
        Context context = ContextManagerFactory.getInstance().getContext();
        BbSession session = context.getSession();
        if (session != null && session.isAuthenticated()){
            List<CourseDto> courses = new ArrayList<CourseDto>();
            try {
                List<Course> bbCourses = courseDbLoader.loadAllCourses();
                courses = BbCourseHelper.convertBbCoursesToCourseDtos(bbCourses);
            } catch (PersistenceException ex) {
                logger.error("Error while retrieving courses", ex);
                throw new WebApplicationException("Error retrieving Courses", 500);
            }

            return courses; 
        } else {
            throw new Error("No valid session found");
        }
         
    } 

    @GET
    @Path("/{courseId}")
    @Produces("application/json")
    public CourseDto getCourseInfo(@PathParam("courseId") String courseId) {
        CourseDto course = null;
        Context context = ContextManagerFactory.getInstance().getContext();
        BbSession session = context.getSession();
        if (session != null && session.isAuthenticated()){
            try {
                Course bbCourse = courseDbLoader.loadByCourseId(courseId);
                List<CourseCourse> children = courseCourseDbLoader.loadByParentId(bbCourse.getId());
                course = BbCourseHelper.convertBbCourseToCourseDto(bbCourse);
                /*
                if (children != null && !children.isEmpty()) {
                    course.setChildren(new ArrayList<CourseDto>());
                    for (CourseCourse childCc : children) {
                        Course childBbCourse = courseDbLoader.loadById(childCc.getChildCourseId());
                        CourseDto childCourse = BbCourseHelper.convertBbCourseToCourseDto(childBbCourse);
                        course.getChildren().add(childCourse);
                    }
                } */

            } catch (KeyNotFoundException ex) {
                logger.debug(String.format("No Course with CourseId {} found.", courseId));
                throw new WebApplicationException(String.format("No Course with CourseId %s found.", courseId), 404);
            } catch (PersistenceException ex) {
                logger.error("Error while retrieving courses", ex);
                throw new WebApplicationException("Error retrieving Courses", 500);
            }
        }

        return course;
    }

    @GET
    @Path("/{courseId}/gradebook/assessments")
    @Produces("application/json")
    public List<AssessmentItemDto> getAssessmentItemsForCourse(@PathParam("courseId") String courseId) {
        List<AssessmentItemDto> assessmentItems = new ArrayList<>();
        Context context = ContextManagerFactory.getInstance().getContext();
        BbSession session = context.getSession();
        if (session != null && session.isAuthenticated()){
            try {
                Course bbCourse = courseDbLoader.loadByCourseId(courseId);

                List<GradableItem> gradableItems = gradableItemDAO.loadCourseGradebook(bbCourse.getId(), 0);

                assessmentItems = BbCourseHelper.convertGradableItemsToAssessmentDtos(gradableItems);

            } catch (KeyNotFoundException ex) {
                logger.debug(String.format("No Course with CourseId {} found.", courseId));
                throw new WebApplicationException(String.format("No Course with CourseId %s found.", courseId), 404);
            } catch (PersistenceException ex) {
                logger.error("Error while retrieving courses", ex);
                throw new WebApplicationException("Error retrieving Courses", 500);
            }
        }
        return assessmentItems;
    }

    @GET 
    @Path("/{courseId}/users")
    @Produces(MediaType.APPLICATION_JSON)
    public List<UserDto> getCourseUsers(@PathParam("courseId") String courseIdStr) {

        ConnectionManager cManager = null; 
        Connection conn = null;  
        Statement stmt = null;
        List<UserDto> users = new ArrayList<>();
  
        try {  
    
            cManager = BbDatabase.getDefaultInstance().getConnectionManager();  
            conn = cManager.getConnection();  


            stmt = conn.createStatement();
            String sql =  "select u.pk1 as user_id, u.user_id as uun, u.lastname as familyname, u.middlename, u.firstname as givenname from course_main as cm inner join course_users as cu on cm.pk1 = cu.crsmain_pk1 inner join users as u on u.pk1 = cu.users_pk1 where cm.course_id = '"+courseIdStr+"'";

            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                UserDto user = new UserDto();
                UserDto.Attributes attributes = new UserDto.Attributes();
                int userId = rs.getInt(1);
                String usId = userId+"";
                user.setId(usId);
                String uun = rs.getString(2);
                attributes.uun = uun;
                String famNam = rs.getString(3);
                attributes.familyName = famNam;
                String midNam = rs.getString(4);
                attributes.middleName = midNam;
                String givNam = rs.getString(5);
                attributes.givenName = givNam;
                user.setAttributes(attributes);
                users.add(user);
        
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
        
        return users;
    }


        /*List<UserDto> userItems = new ArrayList<>();
        Context context = ContextManagerFactory.getInstance().getContext();
        BbSession session = context.getSession();
        if (session != null && session.isAuthenticated()){
            try {
                Course bbCourse = courseDbLoader.loadByCourseId(courseId);
                BbList<CourseMembership> courseMembers = CourseMembershipDbLoader.Default.getInstance().loadByCourseId(bbCourse.getId());
                for (CourseMembership item: courseMembers){
                    userItems.add(BbCourseHelper.convertCourseMemberToUserDto(item));
                }
            } catch (KeyNotFoundException ex) {
                logger.debug(String.format("No Course with CourseId {} found.", courseId));
                throw new WebApplicationException(String.format("No Course with CourseId %s found.", courseId), 404);
            } catch (PersistenceException ex) {
                logger.error("Error while retrieving courses", ex);
                throw new WebApplicationException("Error retrieving Courses", 500);
            }
        }

        return userItems;
    }*/


    @GET
    @Path("/{courseId}/gradebook/assessments/{assessmentId}")
    @Produces("application/json")
    public AssessmentItemDto getAssessmentItemForCourse(
            @PathParam("courseId") String courseId,
            @PathParam("assessmentId") String assessmentIdStr) {

        AssessmentItemDto assessmentItem;
        try {
            Course bbCourse = courseDbLoader.loadByCourseId(courseId);
            Id assessmentId = Id.generateId(GradableItem.DATA_TYPE, assessmentIdStr);

            //TODO Check that the gradableItem actually belongs to this course.
            GradableItem gradableItem = gradableItemDAO.loadById(assessmentId);

            assessmentItem = BbCourseHelper.convertGradableItemToAssessmentDto(gradableItem);

        } catch (KeyNotFoundException ex) {
            logger.debug(String.format("No Course with CourseId {} found.", courseId));
            throw new WebApplicationException(String.format("No Course with CourseId %s found.", courseId), 404);
        } catch (PersistenceException ex) {
            logger.error("Error while retrieving courses", ex);
            throw new WebApplicationException("Error retrieving Courses", 500);
        }
        return assessmentItem;
    }

    @GET
    @Path("/{courseId}/gradebook/assessments/{assessmentId}/marks")
    @Produces("application/json")
    public List<MarkDto> getMarksForAssessmentItem(
            @PathParam("courseId") String courseId,
            @PathParam("assessmentId") String assessmentIdStr) {

        List<MarkDto> marks = new ArrayList<>();
        try {
            Course bbCourse = courseDbLoader.loadByCourseId(courseId);
            Id assessmentId = Id.generateId(GradableItem.DATA_TYPE, assessmentIdStr);

            //TODO Check that the gradableItem actually belongs to this course.
            GradableItem gradableItem = gradableItemDAO.loadById(assessmentId);
            if (!bbCourse.getId().equals(gradableItem.getCourseId())) {
                throw new WebApplicationException("Invalid IDs in request.", 400);
            }



            List<GradeDetail> grades = gradeDetailDAO.getGradeDetails(gradableItem.getId());
            for (GradeDetail grade : grades) {
                //TODO This is probably not very efficient. Fix it.
                User user = userDbLoader.loadUserByCourseMembership(grade.getCourseUserId());
                MarkDto mark = new MarkDto();
                String gradeVal = grade.getGrade(gradableItem.getAggregationModel());
                double scoreVal = 0;
                try {
                    scoreVal = Double.parseDouble(gradeVal);
                } catch (NumberFormatException e) {
                    // Do Nothing for now...
                }
                mark.setValue(gradableItem.getSchemaValue(scoreVal));
                mark.setExternalUserKey(user.getUserName());
                marks.add(mark);
            }



        } catch (KeyNotFoundException ex) {
            logger.debug(String.format("No Course with CourseId {} found.", courseId));
            throw new WebApplicationException(String.format("No Course with CourseId %s found.", courseId), 404);
        } catch (PersistenceException ex) {
            logger.error("Error while retrieving courses", ex);
            throw new WebApplicationException("Error retrieving Courses", 500);
        }
        return marks;
    }

}
