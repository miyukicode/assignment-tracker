package com.example.d308app;

import static org.junit.Assert.assertEquals;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.example.d308app.UI.AssignmentDetails;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class DueDateValidationTest {

    @Test
    public void testDateBeforeClassStart() {
        String error = AssignmentDetails.DateValidator.validate("11/15/25", "11/20/25", "11/27/25");
        assertEquals("Due date can't be before class start date", error);
    }

    @Test
    public void testDateAfterClassEnd() {
        String error = AssignmentDetails.DateValidator.validate("11/30/25", "11/20/25", "11/27/25");
        assertEquals("Due date can't be after class end date", error);
    }
}
