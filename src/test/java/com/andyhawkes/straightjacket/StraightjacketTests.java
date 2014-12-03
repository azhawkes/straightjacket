package com.andyhawkes.straightjacket;

import org.junit.Assert;
import org.junit.Test;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Test cases to make sure the straightjacket is able to restrain a lunatic.
 */
public class StraightjacketTests {
    @Test
    public void testAddingNumbers() throws Exception {
        ScriptEngine engine = new Straightjacket().createJavaScriptEngine();

        engine.eval(getScript("js/add_numbers.js"));

        int result = (int) ((Invocable) engine).invokeFunction("add_numbers", 1, 3);

        Assert.assertEquals("The total should be 4", 4, result);
    }

    @Test
    public void testTouchFileAllowed() throws Exception {
        String filePath = "/tmp/touchme-" + Math.random();
        Straightjacket straightjacket = new Straightjacket().exposeJavaClass("java.io.*");
        ScriptEngine engine = straightjacket.createJavaScriptEngine();

        engine.eval(getScript("js/touch_a_file.js"));

        boolean created = (boolean) ((Invocable) engine).invokeFunction("touch_a_file", filePath);

        Assert.assertTrue("The return value should be true", created);
        Assert.assertTrue("The file should really exist", new File(filePath).exists());

        new File(filePath).deleteOnExit();
    }

    @Test
    public void testTouchFileProhibited() throws Exception {
        String filePath = "/tmp/touchme-" + Math.random();
        Straightjacket straightjacket = new Straightjacket();
        ScriptEngine engine = straightjacket.createJavaScriptEngine();

        engine.eval(getScript("js/touch_a_file.js"));

        try {
            ((Invocable) engine).invokeFunction("touch_a_file", filePath);

            Assert.fail("An exception should have been thrown when trying to access files");
        } catch (Exception e) {
            // good!
        } finally {
            new File(filePath).deleteOnExit();
        }
    }

    @Test
    public void testURLConnectionAllowed() throws Exception {
        Straightjacket straightjacket = new Straightjacket().exposeJavaClass("java.net.*");
        ScriptEngine engine = straightjacket.createJavaScriptEngine();

        engine.eval(getScript("js/open_url_connection.js"));

        Object response = ((Invocable) engine).invokeFunction("open_url_connection", "http://www.google.com");

        Assert.assertNotNull("The response should not be null", response);
    }

    @Test
    public void testURLConnectionProhibited() throws Exception {
        Straightjacket straightjacket = new Straightjacket();
        ScriptEngine engine = straightjacket.createJavaScriptEngine();

        engine.eval(getScript("js/open_url_connection.js"));

        try {
            ((Invocable) engine).invokeFunction("open_url_connection", "http://www.google.com");

            Assert.fail("An exception should have been thrown because we haven't exposed network stuff");
        } catch (Exception e) {
            // good
        }
    }

    private Reader getScript(String jsFileName) {
        return new InputStreamReader(StraightjacketTests.class.getClassLoader().getResourceAsStream(jsFileName));
    }
}
