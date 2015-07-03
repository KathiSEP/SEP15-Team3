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
@SuiteClasses({ OverdraftCreditAdminTest.class, AuthenticationTest.class, CreateUserBasti3.class, CreateUserPatrickC.class, CreateUserRicky1.class,
    CreateCourseTest.class, EditCourseYogaTest.class, CreateYogaUnitTest.class, CreateYogaUnitsTest.class, CreateUserKathi5.class, AdaptedEditUnitTest.class, 
    SearchAndViewCourseTest.class, ActivationTypeAdminTest.class, RegistrationTest.class, UserNotYetActivatedTest.class, AccountActivationByAdminTest.class, SignUpForCourseYogaTest.class,
    SignUpForYogaCourseunitsTest.class, AdaptedDeleteUnitTest.class, UploadProfileImageTest.class, EditEmailTest.class, ListParticipantsTest.class,  
     LogoutTest.class, ViewSchedulerTest.class, LostPasswordTest.class, AdminTopUpTest.class,  
     AddLeaderToCourseTest.class, DeleteCourseTest.class, HelpTest.class, DeleteUserTest.class         
          })
public class AllTests {

}
