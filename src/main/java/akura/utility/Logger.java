package akura.utility;

import org.apache.log4j.PropertyConfigurator;

import java.io.IOException;

public class Logger {
    final static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getRootLogger();


    public static void Log(String s){
        PropertyConfigurator.configure("log4j.properties");
        logger.info(s);
//        try {
//            APIConnection.sendSocketRequest("VALUE", s);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        System.out.println(s);
    }
}
