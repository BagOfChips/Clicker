package loggerUtils;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class loggerUtils{

    public static String getCallingMethod(){
        return Thread.currentThread().getStackTrace()[2].getMethodName();
    }

    public static void setLoggerConfig(Logger logger){
        Handler handlerObj = new ConsoleHandler();
        handlerObj.setLevel(Level.ALL);
        logger.addHandler(handlerObj);
        logger.setLevel(Level.ALL);
        logger.setUseParentHandlers(false);
    }



}
