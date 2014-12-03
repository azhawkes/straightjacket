package com.andyhawkes.straightjacket;

import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptEngine;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * A simple utility for creating Nashorn script engines, while selectively limiting what Java classes and other
 * resources the scripts have access to.
 */
public class Straightjacket {
    private static final Logger log = LoggerFactory.getLogger(Straightjacket.class);

    private NashornScriptEngineFactory factory = new NashornScriptEngineFactory();
    private Set<Pattern> exposed = Collections.synchronizedSet(new HashSet<Pattern>());

    /**
     * Creates a JavaScript engine subject to the straightjacket configuration.
     */
    public ScriptEngine createJavaScriptEngine() {
        return factory.getScriptEngine(new SafeClassLoader());
    }

    /**
     * Exposes a Java class or package to any script engines that are created with this straightjacket.
     * Valid glob examples include "java.lang.String" or "java.io.*".
     */
    public Straightjacket exposeJavaClass(String glob) {
        StringBuilder regex = new StringBuilder();

        for (int i = 0; i < glob.length(); i++) {
            char c = glob.charAt(i);

            if (c == '*') {
                regex.append(".*");
            } else if (c == '.') {
                regex.append("\\.");
            } else {
                regex.append(c);
            }
        }

        exposed.add(Pattern.compile(regex.toString()));

        return this;
    }

    private boolean isClassExposed(String name) {
        for (Pattern pattern : exposed) {
            if (pattern.matcher(name).matches()) {
                return true;
            }
        }

        return false;
    }

    private class SafeClassLoader extends ClassLoader {
        public Class<?> loadClass(String name) throws ClassNotFoundException {
            if (!isClassExposed(name)) {
                log.warn("Script tried to load " + name + " which is forbidden!");

                throw new ClassNotFoundException("cannot load " + name + " because it is forbidden!");
            }

            return super.loadClass(name);
        }
    }
}
