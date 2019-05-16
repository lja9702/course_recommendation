<<<<<<< HEAD
package com.donsamo.dongsamo;
=======
package com.donsamo.Dongsamo;
>>>>>>> 430dd3cab30cf86ef77d8d98e2fc59ca5099c6e3

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

<<<<<<< HEAD
        assertEquals("com.donsamo.dongsamo", appContext.getPackageName());
=======
        assertEquals("com.donsamo.babple", appContext.getPackageName());
>>>>>>> 430dd3cab30cf86ef77d8d98e2fc59ca5099c6e3
    }
}
