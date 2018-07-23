package com.michaelwilliamjones.dynamicfragmenttest;


import android.Manifest;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.rule.GrantPermissionRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class ChatFragmentTest {

    @Rule
    public ActivityTestRule<BottomNavigationActivity> mActivityTestRule = new ActivityTestRule<>(BottomNavigationActivity.class);

    @Test
    public void chatFragmentTest() {
        GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);

        ViewInteraction bottomNavigationItemView = onView(
                allOf(withId(R.id.navigation_dashboard),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.navigation),
                                        0),
                                1),
                        isDisplayed()));
        bottomNavigationItemView.perform(click());

        ViewInteraction editText = onView(
                allOf(withId(R.id.messageContent),
                        childAtPosition(
                                allOf(withId(R.id.linearLayout),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class),
                                                0)),
                                0),
                        isDisplayed()));
        editText.check(matches(isDisplayed()/*withText("")*/));

        ViewInteraction button = onView(
                allOf(withId(R.id.button),
                        childAtPosition(
                                allOf(withId(R.id.linearLayout),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class),
                                                0)),
                                1),
                        isDisplayed()));
        button.check(matches(isDisplayed()));

        ViewInteraction textView = onView(
                allOf(withId(R.id.smallLabel), withText("Connect"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.navigation_home),
                                        1),
                                0),
                        isDisplayed()));
        textView.check(matches(withText("Connect")));

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.largeLabel), withText("Chat"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.navigation_dashboard),
                                        1),
                                1),
                        isDisplayed()));
        textView2.check(matches(withText("Chat")));

        ViewInteraction textView3 = onView(
                allOf(withId(R.id.smallLabel), withText("Map"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.navigation_notifications),
                                        1),
                                0),
                        isDisplayed()));
        textView3.check(matches(withText("Map")));

    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
