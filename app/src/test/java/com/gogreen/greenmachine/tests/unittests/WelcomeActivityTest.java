package com.gogreen.greenmachine.tests.unittests;

import android.content.Context;
import android.widget.TextView;

import com.gogreen.greenmachine.BuildConfig;
import com.gogreen.greenmachine.R;
import com.gogreen.greenmachine.main.WelcomeActivity;
import com.gogreen.greenmachine.tests.mocks.GreenMachineTestApplication;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by jonathanlui on 8/26/15.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class,
        sdk = 19,
        application = GreenMachineTestApplication.class)
public class WelcomeActivityTest {

    private Context context;
    private WelcomeActivity activity;

    @Before
    public void setUp() throws Exception {
        activity = Robolectric.setupActivity(WelcomeActivity.class);
        context = RuntimeEnvironment.application;

    }

    @Test
    public void testing_framework_setup() {
        assertThat(true).isEqualTo(true);
    }

    @Test
    public void slogan_text_is_correct() {

        TextView sloganTextView = (TextView) activity.findViewById(R.id.slogan);

        String expectedText = context.getString(R.string.slogan);
        assertThat(expectedText).isEqualTo(sloganTextView.getText().toString());
    }
}
