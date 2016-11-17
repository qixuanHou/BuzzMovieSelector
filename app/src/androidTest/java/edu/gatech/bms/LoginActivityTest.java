package edu.gatech.bms;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.test.ActivityInstrumentationTestCase2;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.support.test.rule.ActivityTestRule;
import android.test.UiThreadTest;
import android.util.Log;
import android.widget.Button;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.AllOf.allOf;

import static org.junit.Assert.assertEquals;
/**
 * Created by Alex on 4/2/16.
 */

@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {
    private BackendlessUser user;

    @Rule
    public ActivityTestRule<LoginActivity> LoginActivityRule = new ActivityTestRule<>(LoginActivity.class);

    @Before
    public void setUp() {
        Backendless.initApp(InstrumentationRegistry.getTargetContext(), BackendSetting.APPLICATION_ID, BackendSetting.ANDROID_SECRET_KEY, BackendSetting.VERSION);
        LoginActivity.clearLoginAttempts("login");
        user = BackendlessFunctions.findUserSync("login");
        user.setProperty("status", "active");
        Backendless.UserService.update(user);

    }

    @Test
    public void correctUserLogin() {
        onView(withId(R.id.usernameTxt)).perform(typeText("login"), closeSoftKeyboard());
        onView(withId(R.id.passwordTxt)).perform(typeText("a"), closeSoftKeyboard());
        onView(withId(R.id.LoginB)).perform(click());
        onView(withText("Welcome,")).check(matches(isDisplayed()));
    }


    @Test
    public void emptyLogin() throws Exception {
        onView(withId(R.id.LoginB)).perform(click());
        onView(withText("User does not exist")).check(matches(isDisplayed()));
    }

    @Test
    public void invalidLogin() {
        onView(withId(R.id.usernameTxt)).perform(typeText("login"), closeSoftKeyboard());
        onView(withId(R.id.passwordTxt)).perform(typeText("aawefads"), closeSoftKeyboard());
        onView(withId(R.id.LoginB)).perform(click());
        onView(withText("Invalid Information")).check(matches(isDisplayed()));
    }


    @Test
    public void lockUser() {
        onView(withId(R.id.usernameTxt)).perform(typeText("login"), closeSoftKeyboard());
        onView(withId(R.id.passwordTxt)).perform(typeText("aawefads"), closeSoftKeyboard());
        for (int i = 0; i < 3; i++ ) {
            onView(withId(R.id.LoginB)).perform(click());
            Espresso.pressBack();
        }
        onView(withId(R.id.LoginB)).perform(click());
        onView(withText("Account is locked due to too many failed logins")).check(matches(isDisplayed()));
        BackendlessUser user = BackendlessFunctions.findUserSync("login");
        assertEquals(user.getProperty("status"), "locked");

        LoginActivity.clearLoginAttempts("login");
        user.setProperty("status", "active");
        Backendless.UserService.update(user);
    }

    @Test
    public void bannedUser() {
        user.setProperty("status", "banned");
        Backendless.UserService.update(user);
        onView(withId(R.id.usernameTxt)).perform(typeText("login"), closeSoftKeyboard());
        onView(withId(R.id.passwordTxt)).perform(typeText("a"), closeSoftKeyboard());
        onView(withId(R.id.LoginB)).perform(click());
        onView(withText("Account is banned")).check(matches(isDisplayed()));
    }

    @Test
    public void adminUser() {
        onView(withId(R.id.usernameTxt)).perform(typeText("admin"), closeSoftKeyboard());
        onView(withId(R.id.passwordTxt)).perform(typeText("a"), closeSoftKeyboard());
        onView(withId(R.id.LoginB)).perform(click());
        onView(withText("Admin")).check(matches(isDisplayed()));
        onView(withId(R.id.adminLogout)).perform(click());
        onView(withText("Movie Selector")).check(matches(isDisplayed()));
    }

}
