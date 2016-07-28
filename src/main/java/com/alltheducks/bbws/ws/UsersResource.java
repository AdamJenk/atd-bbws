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

import com.alltheducks.bbws.model.CourseDto;
import com.alltheducks.bbws.model.UserDto;
import com.alltheducks.bbws.util.BbCourseHelper;


import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Harry Scells on 5/05/2016.
 * Copyright All the Ducks Pty. Ltd.
 */
@Path("users")
public class UsersResource {

    @Inject
    private CourseDbLoader courseDbLoader;
    @Inject
    private UserDbLoader userDbLoader;

    @GET 
    @Produces(MediaType.APPLICATION_JSON)
    public List<UserDto> listUsers() {
        List<UserDto> returnedList;
        Context context = ContextManagerFactory.getInstance().getContext();
        BbSession session = context.getSession();
        Id observerId = session.getUserId();
        try { 
            BbList<User> viewableUsers = userDbLoader.loadObservedByObserverId(observerId);
            returnedList = BbCourseHelper.convertBbListToListUserDto(viewableUsers);
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
/*
    @GET
    @Path
    @Produces
    public UserDTo getUser(){
        User bbUser = UserDbLoader.loadByUserName(username);
        UserDto userReq;
        userReq.
    }
*/

    @GET
    @Path("{username}/courses")
    @Produces(MediaType.APPLICATION_JSON)
    public List<CourseDto> getCoursesForUser(@PathParam("username") String username, @QueryParam("entitlement") @DefaultValue("") String entitlement) throws PersistenceException {

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
