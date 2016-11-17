package edu.gatech.bms;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.backendless.Backendless;

import org.hamcrest.Description;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.*;

/**
 * Created by chenzhijian on 4/6/16.
 */
@RunWith(AndroidJUnit4.class)
public class RecommendationOverallTest {

    @Rule
    public ActivityTestRule<RecommendationOverall> LoginActivityRule = new ActivityTestRule<>(RecommendationOverall.class);


    @Before
    public void setUp() throws Exception {
        Backendless.initApp(InstrumentationRegistry.getTargetContext(), BackendSetting.APPLICATION_ID, BackendSetting.ANDROID_SECRET_KEY, BackendSetting.VERSION);
    }
    @Test
    public void checkRecommendingListMajors() throws Exception {
        String majors[] = {"CSE", "CS", "ISYE", "MATH", "ECE", "All"};
        for (String major: majors) {
            onView(withId(R.id.filterMajor)).perform(click());
            onData(allOf(is(instanceOf(String.class)), is(major))).perform(click());
            Thread.sleep(1000);
            checkHasItems();
        }
    }
    /*
    @Test
    public void checkRecommendingListCSE() throws Exception{
        onView(withId(R.id.filterMajor)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("CSE"))).perform(click());
        Thread.sleep(1000);
        checkHasItems();

    }

    @Test
    public void checkRecommendingListCS() throws InterruptedException{
        onView(withId(R.id.filterMajor)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("CS"))).perform(click());
        Thread.sleep(1000);
        checkHasItems();

    }

    @Test
    public void checkRecommendingListECE() throws Exception{
        onView(withId(R.id.filterMajor)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("ECE"))).perform(click());
        Thread.sleep(1000);
        checkHasItems();
    }





    @Test
    public void selectMajor() {
        onView(withId(R.id.filterMajor)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("CSE"))).perform(click());
        onView(withId(R.id.filterMajor)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("CS"))).perform(click());
        onView(withId(R.id.filterMajor)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("ISYE"))).perform(click());
        onView(withId(R.id.filterMajor)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("MATH"))).perform(click());
        onView(withId(R.id.filterMajor)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("ECE"))).perform(click());
        onView(withId(R.id.filterMajor)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("All"))).perform(click());
    }

//    @Test
//    public void clickOnMovie() throws InterruptedException {
//
//        onData(allOf(Matchers.is(Matchers.instanceOf(String.class)), Matchers.is("Laila B.,   5.0")))
//                .inAdapterView(withId(R.id.recommendmoviesoverall)).perform(click());
//
//        onData(hasToString(startsWith("Laila")))
//                .inAdapterView(withId(R.id.recommendmoviesoverall)).atPosition(0)
//                .perform(click());
//        Thread.sleep(5000);
//        onData(anything()).inAdapterView(withContentDescription("desc")).atPosition(2).perform(click());
//        onData(hasToString(startsWith("Laila")))
//                .inAdapterView(withId(R.id.recommendmoviesoverall))
//                .perform(click());
//
//
//        onData(anything()).inAdapterView(withId(R.id.recommendmoviesoverall)).atPosition(0).perform(click());
//        onData(anything()).inAdapterView(withId(R.id.recommendmoviesoverall)).atPosition(1).perform(click());
//
//
//    }
    */
    private void checkHasItems() {
        final int[] counts = new int[1];
        onView(withId(R.id.recommendmoviesoverall)).check(matches(new org.hamcrest.TypeSafeMatcher<View>() {
            @Override
            public boolean matchesSafely(View view) {
                ListView listView = (ListView) view;
                counts[0] = listView.getAdapter().getCount();
                System.out.println(counts[0]);
                assertTrue(counts[0] > 0);
                return true;
            }

            @Override
            public void describeTo(Description description) {

            }
        }));
    }

}