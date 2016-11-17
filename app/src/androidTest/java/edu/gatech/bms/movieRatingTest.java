package edu.gatech.bms;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.backendless.Backendless;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.AllOf.allOf;

/**
 * Created by houqixuan on 4/4/16.
 */


@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class movieRatingTest {

    @Rule
    public ActivityTestRule<LoginActivity> LoginActivity = new ActivityTestRule<>(LoginActivity.class);

    @Before
    public void setUp() {
        Backendless.initApp(InstrumentationRegistry.getTargetContext(), BackendSetting.APPLICATION_ID, BackendSetting.ANDROID_SECRET_KEY, BackendSetting.VERSION);
        navigateToRatingPage();
    }

    private void navigateToRatingPage() {
        onView(withId(R.id.usernameTxt)).perform(typeText("qhou6"), closeSoftKeyboard());
        onView(withId(R.id.passwordTxt)).perform(typeText("aaaaa"), closeSoftKeyboard());
        onView(withId(R.id.LoginB)).perform(click());

        onView(withId(R.id.RecentMoviesButton)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("The Boss"))).perform(click());
        onView(withId(R.id.viewReviewB)).perform(scrollTo());
        onView(withId(R.id.viewReviewB)).perform(click());
    }

    @Test
    public void ratingWithoutComments() {
        onView(withId(R.id.userComment)).perform(closeSoftKeyboard());
        onView(withId(R.id.submit)).perform(click());
    }

    @Test
    public void ratingWithComments() {
        onView(withId(R.id.userComment)).perform(closeSoftKeyboard());
        onView(withId(R.id.ratingBar)).perform(click());
        onView(withId(R.id.userComment)).perform(typeText("user comments"), closeSoftKeyboard());
        onView(withId(R.id.submit)).perform(click());
    }

}
