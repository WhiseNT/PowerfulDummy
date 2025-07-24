package com.whisent.powerful_dummy.utils;

import com.whisent.powerful_dummy.Powerful_dummy;
import org.slf4j.Logger;

public class Debugger {
    private static boolean debug_mode = false;
    private final static Logger logger = Powerful_dummy.LOGGER;

    public static boolean isOpen() {
        return debug_mode;
    }

    public static void sendDebugMessage(String message) {
        if(isOpen()) {
            logger.debug( message);
        }
    }
    public static void sendAlwaysDebugMessage(String message) {
        logger.debug( message);
    }
    public static void setOpen(boolean debug_mode) {
        Debugger.debug_mode = debug_mode;
    }
}
