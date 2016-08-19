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
import com.alltheducks.bbws.model.AttemptDto;
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
        CourseDto.Attributes attributes = new CourseDto.Attributes();
        course.setId(bbCourse.getId().getExternalString());
        attributes.courseId = bbCourse.getCourseId();
        attributes.externalId = bbCourse.getBatchUid();
        attributes.title = bbCourse.getTitle();
        course.setAttributes(attributes);
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
        UserDto.Attributes attributes = new UserDto.Attributes();
        User currentUser = item.getUser();
        singleMember.setId(currentUser.getId().getExternalString());
        attributes.uun = currentUser.getUserName();
        attributes.familyName = currentUser.getFamilyName();
        attributes.middleName = currentUser.getMiddleName();
        attributes.givenName = currentUser.getGivenName();
        singleMember.setAttributes(attributes);

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
        AssessmentItemDto.Attributes attributes = new AssessmentItemDto.Attributes();
        assessmentItem.setId(item.getId().getExternalString());
        attributes.name = item.getTitle();
        attributes.pointsPossible = item.getPoints();
        attributes.courseId = item.getCourseId().getExternalString();
        assessmentItem.setAttributes(attributes);
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
    
    /*
    public static List<AttemptDto> convertListOfAttemptVOToAttemptDtoList(List<AttemptVO> attemptsVo) {
        List<AttemptDto> attemptItems = new ArrayList<>();

        for (AttemptVO attemptVo : attemptsVo) {
            AttemptDto attemptItem = convertAttemptVOToAttemptDto(attemptVo);
            attemptItems.add(attemptItem);
        }
        return attemptItems;
    }

    public static AttemptDto convertAttemptVOToAttemptDto(AttemptVO attemptVo) {
        AttemptDto attemptItem = new AttemptDto();
        attemptItem.setAttemptId(attemptVo.getId());
        attemptItem.setAttemptDate(attemptVo.getAttemptDate());
        attemptItem.setGrade(attemptVo.getGrade());
        attemptItem.setAssessmentId(attemptVo.getId());
        Id scoreId = (Id) attemptVo.getGradeId();
        ScoreVO score = scoreId.load();
        User user = score.getUserId().load();
        attemptItem.setUun(user.getUserName());
        
    
    }
    
    */

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
        UserDto.Attributes attributes = new UserDto.Attributes();
        singleMember.setId(item.getId().getExternalString());
        attributes.uun = item.getUserName();
        attributes.familyName = item.getFamilyName();
        attributes.middleName = item.getMiddleName();
        attributes.givenName = item.getGivenName();
        singleMember.setAttributes(attributes);
        return singleMember;
    }

}
