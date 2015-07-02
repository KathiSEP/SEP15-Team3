/**
 * 
 */
package de.ofCourse.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * @author Sebastian Schwarz
 *
 */
@RunWith(Suite.class)
@SuiteClasses({ CreateUserBasti3.class, AccountActivationByAdminTest.class,
        ActivationTypeAdminTest.class, AdaptedDeleteUnitTest.class,
        AdaptedEditUnitTest.class, AddLeaderToCourseTest.class,
        AdminCreateUserTest.class, AdminTopUpTest.class,
        AuthenticationTest.class, CreateCourseTest.class, 
        CreateYogaUnitsTest.class, CreateYogaUnitTest.class,
        EditCourseYogaTest.class, EditEmailTest.class, HelpTest.class,
        ListParticipantsTest.class, LogoutTest.class, LostPasswordTest.class,
        OverdraftCreditAdminTest.class, OwnCourseEditTest.class,
        RegistrationTest.class, SearchAndViewCourseTest.class,
        SignUpForCourseYogaTest.class, SignUpForKungFuCoursAndUnitsTest.class,
        SignUpForYogaCourseunitsTest.class, UploadProfileImageTest.class,
        UserNotYetActivatedTest.class, ViewSchedulerTest.class })
public class AllTests {

}
