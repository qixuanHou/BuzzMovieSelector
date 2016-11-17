package edu.gatech.bms;

import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.AllOf.allOf;

/**
 * Created by Joon on 2016-04-04.
 */
@RunWith(AndroidJUnit4.class)
public class ProfilePageActivityTest extends TestCase {
    private BackendlessUser user;

    @Rule
    public ActivityTestRule<LoginActivity> LoginActivityRule = new ActivityTestRule<>(LoginActivity.class);

    @Before
    public void setUp() {
        Backendless.initApp(InstrumentationRegistry.getTargetContext(), BackendSetting.APPLICATION_ID, BackendSetting.ANDROID_SECRET_KEY, BackendSetting.VERSION);
    }

    @Test
    public void saveTest() {
        onView(withId(R.id.usernameTxt)).perform(typeText("jjjjj"), closeSoftKeyboard());
        onView(withId(R.id.passwordTxt)).perform(typeText("jjjjj"), closeSoftKeyboard());
        onView(withId(R.id.LoginB)).perform(click());
        onView(withId(R.id.ProfileButton)).perform(click());

        onView(withId(R.id.displayFirstName)).perform(clearText());
        onView(withId(R.id.displayFirstName)).perform(typeText("Test"), closeSoftKeyboard());
        onView(withId(R.id.displayLastName)).perform(clearText());
        onView(withId(R.id.displayLastName)).perform(typeText("Testiest"), closeSoftKeyboard());
        onView(withId(R.id.displayStudentId)).perform(replaceText("77777777"), closeSoftKeyboard());
        onView(withId(R.id.profileMajor)).perform(scrollTo());
        onView(withId(R.id.profileMajor)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("MATH"))).perform(click());
        onView(withId(R.id.displayEmail)).perform(scrollTo());
        onView(withId(R.id.displayEmail)).perform(clearText());
        onView(withId(R.id.displayEmail)).perform(typeText("Test@test.test"), closeSoftKeyboard());
        onView(withId(R.id.saveButton)).perform(scrollTo());
        onView(withId(R.id.saveButton)).perform(click());
    }

    @Test
    public void cancelTest() {
        onView(withId(R.id.usernameTxt)).perform(typeText("jjjjj"), closeSoftKeyboard());
        onView(withId(R.id.passwordTxt)).perform(typeText("jjjjj"), closeSoftKeyboard());
        onView(withId(R.id.LoginB)).perform(click());
        onView(withId(R.id.ProfileButton)).perform(click());
        onView(withId(R.id.CancelButton)).perform(scrollTo());
        onView(withId(R.id.CancelButton)).perform(click());
    }

    @Test
    public void noChangeSaveTest() {
        onView(withId(R.id.usernameTxt)).perform(typeText("jjjjj"), closeSoftKeyboard());
        onView(withId(R.id.passwordTxt)).perform(typeText("jjjjj"), closeSoftKeyboard());
        onView(withId(R.id.LoginB)).perform(click());
        onView(withId(R.id.ProfileButton)).perform(click());
        onView(withId(R.id.saveButton)).perform(scrollTo());
        onView(withId(R.id.saveButton)).perform(click());
    }

    @Test
    public void compareData() {
        onView(withId(R.id.usernameTxt)).perform(typeText("jjjjj"), closeSoftKeyboard());
        onView(withId(R.id.passwordTxt)).perform(typeText("jjjjj"), closeSoftKeyboard());
        onView(withId(R.id.LoginB)).perform(click());
        onView(withId(R.id.ProfileButton)).perform(click());
        final BackendlessUser user = Backendless.UserService.CurrentUser();
        assertEquals(user.getEmail(), "Test@test.test");
        assertEquals(user.getProperty("studentID"), "77777777");
        assertEquals(user.getProperty("firstName"), "Test");
        assertEquals(user.getProperty("lastName"), "Testiest");
        assertEquals(user.getProperty("major"), "MATH");
        onView(withId(R.id.CancelButton)).perform(scrollTo());
        onView(withId(R.id.CancelButton)).perform(click());
    }

    @Test
    public void saveTest2() {
        onView(withId(R.id.usernameTxt)).perform(typeText("jjjjj"), closeSoftKeyboard());
        onView(withId(R.id.passwordTxt)).perform(typeText("jjjjj"), closeSoftKeyboard());
        onView(withId(R.id.LoginB)).perform(click());
        onView(withId(R.id.ProfileButton)).perform(click());

        onView(withId(R.id.displayFirstName)).perform(clearText());
        onView(withId(R.id.displayFirstName)).perform(typeText("JJJJJ"), closeSoftKeyboard());
        onView(withId(R.id.displayLastName)).perform(clearText());
        onView(withId(R.id.displayLastName)).perform(typeText("CHOI"), closeSoftKeyboard());
        onView(withId(R.id.displayStudentId)).perform(replaceText("998859933"), closeSoftKeyboard());
        onView(withId(R.id.profileMajor)).perform(scrollTo());
        onView(withId(R.id.profileMajor)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("CS"))).perform(click());
        onView(withId(R.id.displayEmail)).perform(scrollTo());
        onView(withId(R.id.displayEmail)).perform(clearText());
        onView(withId(R.id.displayEmail)).perform(typeText("CCC@SSS.JJJ"), closeSoftKeyboard());
        onView(withId(R.id.saveButton)).perform(scrollTo());
        onView(withId(R.id.saveButton)).perform(click());
    }

    @Test
    public void compareData2() {
        onView(withId(R.id.usernameTxt)).perform(typeText("jjjjj"), closeSoftKeyboard());
        onView(withId(R.id.passwordTxt)).perform(typeText("jjjjj"), closeSoftKeyboard());
        onView(withId(R.id.LoginB)).perform(click());
        onView(withId(R.id.ProfileButton)).perform(click());

        final BackendlessUser user = Backendless.UserService.CurrentUser();
        assertEquals(user.getEmail(), "Test@test.test");
        assertEquals(user.getProperty("studentID"), "77777777");
        assertEquals(user.getProperty("firstName"), "Test");
        assertEquals(user.getProperty("lastName"), "Testiest");
        assertEquals(user.getProperty("major"), "MATH");
        onView(withId(R.id.CancelButton)).perform(scrollTo());
        onView(withId(R.id.CancelButton)).perform(click());
    }
}
