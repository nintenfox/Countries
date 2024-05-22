package com.ntsarenkov.countries.view

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.ntsarenkov.countries.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginActivityTest {

    @Rule
    @JvmField
    var activityRule = ActivityTestRule(LoginActivity::class.java)

    private val email = "nikita@gmail.com"
    private val password = "12345678"

    @Test
    fun loginPerform() {
        onView(withId(R.id.et_email)).perform(ViewActions.typeText(email))
        onView(withId(R.id.et_password)).perform(ViewActions.typeText(password))

        onView(withId(R.id.btn_sign_in)).perform(ViewActions.click())

        onView(withId(R.id.tv_password))
            .check(matches(withText("Enter a valid password and try again")))
    }
}