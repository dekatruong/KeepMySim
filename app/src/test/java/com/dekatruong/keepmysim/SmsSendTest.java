package com.dekatruong.keepmysim;

import com.dekatruong.keepmysim.dto.SmsSend;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * Created by Deka on 05/10/2016.
 */

public class SmsSendTest {

    @Test
    public void getRecipientsString_isCorrect() throws Exception {

        final String TEST_PHONE1 = "0986432189";
        final String TEST_PHONE2 = "01646424198";

        SmsSend test = new SmsSend(Arrays.asList(TEST_PHONE1, TEST_PHONE2), "");

        assertEquals(TEST_PHONE1 + "," + TEST_PHONE2, test.getRecipientsString());
    }
}
