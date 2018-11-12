package com.example.android.med_manager;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.android.med_manager.sync.ReminderTasks;
import com.example.android.med_manager.ui.HomeActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.core.AllOf.allOf;


/**
 * Created by SOLARIN O. OLUBAYODE on 19/06/18.
 */
@RunWith(AndroidJUnit4.class)
public class HomeActivityUnitTest {

    @Rule
    public ActivityTestRule<HomeActivity> mActivityRule = new ActivityTestRule<HomeActivity>(HomeActivity.class);

    @Test
    public void clickTakenButton_IncrementTakenCount(){
        onData(anything()).inAdapterView(withId(R.id.recycler_view)).atPosition(0)
                .onChildView(withId(R.id.taken_layout_card))
                .perform(click());

        intended(allOf(
                hasAction(ReminderTasks.ACTION_TAKE_MED_REMINDER)));
//        hasExtra()

        onView((withId(R.id.taken_count_text_view))).check(matches(withText("1")));
    }

    @Test
    public void clickCardItem_launchDetailActivity() {
        onData(anything()).inAdapterView(withId(R.id.recycler_view)).atPosition(0).perform(click());

//        intended(allOf(
//                hasData()));

    }
    }
