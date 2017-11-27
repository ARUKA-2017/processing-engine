package akura.utility;

import org.apache.log4j.PropertyConfigurator;

import java.io.IOException;

public class Logger {
    final static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getRootLogger();


    public static void Log(String s){
        PropertyConfigurator.configure("log4j.properties");
        logger.info(s);
        Logger.LogOverall(s);
        System.out.println(s);
    }

    public static void LogOverall(String s){
        PropertyConfigurator.configure("log4j-overall.properties");
        logger.info(s+"##$$$"+ "NLU Engine");
    }
}
