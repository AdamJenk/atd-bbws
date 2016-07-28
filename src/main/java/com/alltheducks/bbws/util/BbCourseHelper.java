package com.alltheducks.bbws.util;

import blackboard.data.course.*;
import blackboard.persist.*;
import blackboard.platform.gradebook2.*;
import blackboard.platform.gradebook2.impl.*;
import blackboard.data.user.*;
import blackboard.data.gradebook.*;
import blackboard.base.BbList;

import com.alltheducks.bbws.model.CourseDto;
import com.alltheducks.bbws.model.UserDto;
import com.alltheducks.bbws.model.AssessmentItemDto;
import blackboard.platform.ws.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

/**
 * Created by Harry Scells on 5/05/2016.
 * Copyright All the Ducks Pty. Ltd.
 */
public class BbCourseHelper {
    final Logger logger = LoggerFactory.getLogger(BbCourseHelper.class);

    @Inject
    private GradingSchemaDAO gradingSchemaDAO;


    public static List<CourseDto> convertBbCoursesToCourseDtos(List<Course> bbCourses) {
        List<CourseDto> courses = new ArrayList<>();
        for (Course bbCourse : bbCourses) {
            courses.add(convertBbCourseToCourseDto(bbCourse));
        }
        return courses;
    }

    public static CourseDto convertBbCourseToCourseDto(Course bbCourse) {
        CourseDto course = new CourseDto();
        course.setId(bbCourse.getId().getExternalString());
        course.setTitle(bbCourse.getTitle());
        course.setExternalId(bbCourse.getBatchUid());
        course.setCourseId(bbCourse.getCourseId());
        return course;
    }
    public static List<UserDto> convertCourseMembershipToUserDtos(List<CourseMembership> courseMembership) throws PersistenceException {
        List<UserDto> userDtosItems = new ArrayList<>();

        for (CourseMembership courseMember : courseMembership) {
            UserDto singleMember = convertCourseMemberToUserDto(courseMember);
            userDtosItems.add(singleMember);
        }

        return userDtosItems;
    }

    public static UserDto convertCourseMemberToUserDto(CourseMembership item) throws PersistenceException {
        UserDto singleMember = new UserDto();
        User currentUser = item.getUser();
        singleMember.setId(currentUser.getId().getExternalString());
        singleMember.setUun(currentUser.getUserName());
        singleMember.setFamilyName(currentUser.getFamilyName());
        singleMember.setMiddleName(currentUser.getMiddleName());
        singleMember.setGivenName(currentUser.getGivenName());

        return singleMember;
    }
    public static List<AssessmentItemDto> convertGradableItemsToAssessmentDtos(List<GradableItem> gradableItems) throws PersistenceException {
        List<AssessmentItemDto> assessmentItems = new ArrayList<>();

        for (GradableItem gradableItem : gradableItems) {
            AssessmentItemDto assessmentItem = convertGradableItemToAssessmentDto(gradableItem);
            assessmentItems.add(assessmentItem);
        }

        return assessmentItems;
    }

    public static AssessmentItemDto convertGradableItemToAssessmentDto(GradableItem item) throws PersistenceException {
        AssessmentItemDto assessmentItem = new AssessmentItemDto();
        assessmentItem.setId(item.getId().getExternalString());
        assessmentItem.setName(item.getDisplayTitle());
        assessmentItem.setPointsPossible(item.getPoints());
        assessmentItem.setCourseId(item.getDisplayTitle());
        /* removed as it caused non-static to static error. Surplus. But can come back to. 
        GradingSchema schema = gradingSchemaDAO.loadById(item.getGradingSchemaId());
        GradingSchema.Type bbType = schema.getScaleType();

        logger.debug("Gradable item type is: {}", bbType);
        if (bbType == BaseGradingSchema.Type.SCORE) {
            assessmentItem.setValueType(AssessmentItemDto.ValueType.NUMBER);
        } else if (bbType == BaseGradingSchema.Type.PERCENT) {
            assessmentItem.setValueType(AssessmentItemDto.ValueType.PERCENT);
        } else if (bbType == BaseGradingSchema.Type.TEXT) {
            assessmentItem.setValueType(AssessmentItemDto.ValueType.TEXT);
        }*/
        return assessmentItem;
    }

    public static List<UserDto> convertBbListToListUserDto(BbList<User> users) {
        List<UserDto> userDtosItems = new ArrayList<>();
        for (User userItem : users) {
            UserDto userDto = convertUserToUserDtos(userItem);
            userDtosItems.add(userDto);
        }
        return userDtosItems;
    }

    public static UserDto convertUserToUserDtos(User item){
        UserDto singleMember = new UserDto();
        singleMember.setId(item.getId().getExternalString());
        singleMember.setUun(item.getUserName());
        singleMember.setFamilyName(item.getFamilyName());
        singleMember.setMiddleName(item.getMiddleName());
        singleMember.setGivenName(item.getGivenName());

        return singleMember;
    }

}
