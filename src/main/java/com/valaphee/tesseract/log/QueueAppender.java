/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

package com.valaphee.tesseract.log;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.util.Booleans;

import java.io.Serializable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Kevin Ludwig
 */
@Plugin(name = "Queue", category = "Core", elementType = "appender", printObject = true)
public final class QueueAppender extends AbstractAppender {
    private static final BlockingQueue<String> queue = new LinkedBlockingQueue<>();

    private QueueAppender(final String name, final Filter filter, final Layout<? extends Serializable> layout, final boolean ignoreExceptions) {
        super(name, filter, layout, ignoreExceptions);
    }

    @PluginFactory
    public static QueueAppender createAppender(@PluginAttribute("name") final String name, @PluginElement("Filters") final Filter filter, @PluginElement("Layout") Layout<? extends Serializable> layout, @PluginAttribute("ignoreExceptions") final String ignoreExceptions) {
        if (name == null) {
            LOGGER.log(Level.ERROR, "Name cannot be null");

            return null;
        }
        if (layout == null) {
            layout = PatternLayout.createDefaultLayout();
        }
        return new QueueAppender(name, filter, layout, Booleans.parseBoolean(ignoreExceptions, true));
    }

    public static String getMessage() {
        try {
            return queue.take();
        } catch (final InterruptedException ignore) {
        }
        return null;
    }

    @Override
    public void append(final LogEvent event) {
        queue.add(getLayout().toSerializable(event).toString());
    }
}
