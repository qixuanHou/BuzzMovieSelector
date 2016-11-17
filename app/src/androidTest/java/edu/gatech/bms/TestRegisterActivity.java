package edu.gatech.bms;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.support.test.rule.ActivityTestRule;

import junit.framework.TestCase;

import edu.gatech.bms.RegisterActivity;
import static junit.framework.Assert.assertTrue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Created by Thanh Phan on 4/3/16.
 */

@RunWith(AndroidJUnit4.class)
public class TestRegisterActivity extends TestCase {
    @Rule
    public ActivityTestRule<RegisterActivity> RegisterActivityRule = new ActivityTestRule<>(RegisterActivity.class);

    @Before
    public void setUp() {
        //android.support.test.espresso.Espresso.in
    }

    @Test
    public void testEmail() {
        assertTrue(RegisterActivity.validateEmailAddress("al@foobar.com"));
    }

    @Test
    public void testNull() {
        assertFalse(RegisterActivity.validateEmailAddress(null));
    }

    @Test
    public void testBlank() {
        assertFalse(RegisterActivity.validateEmailAddress(""));
    }

    @Test
    public void testEmailExtension() {
        assertTrue(RegisterActivity.validateEmailAddress("bleach@hahahoho.com"));
        assertTrue(RegisterActivity.validateEmailAddress("naruto@hahahoho.net"));
        assertTrue(RegisterActivity.validateEmailAddress("onepiece@hahahoho.info"));
        assertTrue(RegisterActivity.validateEmailAddress("bleach@hahahoho.org"));
        assertFalse(RegisterActivity.validateEmailAddress("bleach@hahahoho.c"));
    }

    @Test
    public void testSpecialCharacter() {

        //Email with slash
        assertTrue(RegisterActivity.validateEmailAddress("al.idiot@haha-hoho.com"));

        //Email with a dot at the end
        assertFalse(RegisterActivity.validateEmailAddress("al.idiot@haha-hoho.com."));

        //Email with comma
        assertFalse(RegisterActivity.validateEmailAddress("vlowjob@replace,vwithb.org"));

        //Email with space between
        assertFalse(RegisterActivity.validateEmailAddress("al haha@foobar.com"));

    }
}
