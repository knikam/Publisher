package com.swami.kalpesh.publisher;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.swami.kalpesh.publisher.Activity.RegisterActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.AllOf.allOf;

@RunWith(AndroidJUnit4.class)
public class SignupTest {

    @Rule
    public ActivityTestRule<RegisterActivity> activityTestRule=new ActivityTestRule<RegisterActivity>(RegisterActivity.class);

    @Test
    public void SignUp()
    {
        String name="Kalpesh Nikam";
        String email="kalpeshnikam1080@gmail.com";
        String qualification="Computer TE";
        String Designation="Student";
        String contact="9098757990";
        String password="kalpesh";

        Espresso.onView(withId(R.id.id_name)).perform(typeText(name), closeSoftKeyboard());
        Espresso.onView(withId(R.id.id_email)).perform(typeText(email),closeSoftKeyboard());
        Espresso.onView(withId(R.id.id_qualification)).perform(typeText(qualification),closeSoftKeyboard());
        Espresso.onView(withId(R.id.id_Designation)).perform(typeText(Designation),closeSoftKeyboard());
        Espresso.onView(withId(R.id.id_contact_no)).perform(typeText(contact),closeSoftKeyboard());
        Espresso.onView(withId(R.id.id_password)).perform(typeText(password),closeSoftKeyboard());

        Espresso.onView(withId(R.id.id_signup_btn)).perform(click());

        String sucess= InstrumentationRegistry.getTargetContext().getString(R.string.success);
        Espresso.onView(withId(R.id.id_name)).check(matches(allOf(withText(sucess),isDisplayed())));

    }
}
