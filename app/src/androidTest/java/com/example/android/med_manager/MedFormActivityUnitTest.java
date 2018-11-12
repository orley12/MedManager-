package com.example.android.med_manager;

import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.android.med_manager.ui.MedFormActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.core.StringStartsWith.startsWith;

/**
 * Created by SOLARIN O. OLUBAYODE on 24/06/18.
 */
@RunWith(AndroidJUnit4.class)
public class MedFormActivityUnitTest {

    @Rule
    public ActivityTestRule<MedFormActivity> mActivityRule = new ActivityTestRule<MedFormActivity>(MedFormActivity.class);

    @Test
    public void clickTheSaveButton_AddsANewItemList() {
        onView(withId(R.id.med_name)).perform(typeText("Penicillin"), closeSoftKeyboard());

        onView(withId(R.id.spinner_med_type)).perform(click());
        onData(anything()).atPosition(0).perform(click());
        onView(withId(R.id.spinner_med_type)).check(matches(withSpinnerText(containsString("Capsules"))));

        onView(withId(R.id.med_description)).perform(typeText("Penicillin"), closeSoftKeyboard());

        onView(withId(R.id.med_dosage)).perform(typeText("2"), closeSoftKeyboard());

        onView(withId(R.id.med_interval)).perform(typeText("2"), closeSoftKeyboard());

        onView(withId(R.id.start_time_edit_text)).perform(click());
        onView(withText("Set time")).check(matches(isDisplayed()));
        onView(withText("Done")).perform(click());

        onView(withId(R.id.med_start_date)).perform(typeText("12/07/2018"), closeSoftKeyboard());

        onView(withId(R.id.med_end_date)).perform(typeText("12/07/2018"), closeSoftKeyboard());

        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().getTargetContext());
        onView(withText("Save")).perform(click());

        onView(withText(startsWith("Medication"))).inRoot(withDecorView(not(is(mActivityRule.getActivity()
                                                                            .getWindow()
                                                                            .getDecorView()))))
                                                                            .check(matches(isDisplayed()));
    }


    }
