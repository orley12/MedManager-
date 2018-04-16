//package com.example.android.med_manager;
//
//import android.content.Intent;
//import android.support.test.rule.ActivityTestRule;
//import android.support.test.runner.AndroidJUnit4;
//
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import static android.support.test.espresso.Espresso.onView;
//import static android.support.test.espresso.action.ViewActions.click;
//import static android.support.test.espresso.intent.Intents.intended;
//import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
//import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
//import static android.support.test.espresso.matcher.ViewMatchers.withId;
//import static java.util.EnumSet.allOf;
//
//
///**
// * Created by SOLARIN O. OLUBAYODE on 10/04/18.
// */
//
//@RunWith(AndroidJUnit4.class)
//public class MedFormActivityBasicTest {
//
//    @Rule
//    public ActivityTestRule<MedFormActivity> mActivityTestRule = new ActivityTestRule<>(MedFormActivity.class);
//
//    @Test
//    public void clickSaveButton() {
//
//        // Click on decrement button
//        onView((withId(R.id.action_save)))
//                .perform(click());
//
//            // intended(Matcher<Intent> matcher) asserts the given matcher matches one and only one
//            // intent sent by the application.
//            intended(allOf(
//                    hasExtra(Intent.EXTRA_TEXT, emailMessage)));
//
//    }
//
//}
