package akura;

import akura.Service.EntityExtractorService;
import akura.Service.SparkMiddleware;
import com.google.gson.Gson;

import java.util.HashMap;

import static spark.Spark.get;
import static spark.Spark.post;
import spark.ModelAndView;
import spark.Spark;
import spark.template.jade.JadeTemplateEngine;

public class App {

    public static void main(String[] args) {

        Gson gson = new Gson();
        EntityExtractorService es = new EntityExtractorService();


        Spark.staticFileLocation("/public");

        SparkMiddleware.enableCORS("*", "POST, GET, OPTIONS, PUT, DELETE",
                "Content-Type, x-xsrf-token, content-Type, X-Auth-Token, Origin, Authorization");

        get("/test", (req, res) -> gson.toJson("name"));

        post("/extract-entity", (req,res)->{
            String response =  es.extractEntity(req.body());
            return  response;
        });

    }
}
