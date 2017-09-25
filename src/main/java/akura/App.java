package akura;

import akura.service.EntityExtractorService;
import akura.service.SparkMiddleware;
import akura.cloundnlp.dtos.OntologyMapDto;
import com.google.gson.Gson;

import java.util.List;

import static spark.Spark.get;
import static spark.Spark.post;

import com.google.gson.GsonBuilder;
import spark.Spark;

public class App {

    public static void main(String[] args) {

        Gson gson = new Gson();
        EntityExtractorService es = new EntityExtractorService();


        Spark.staticFileLocation("/public");

        SparkMiddleware.enableCORS("*", "POST, GET, OPTIONS, PUT, DELETE",
                "Content-Type, x-xsrf-token, content-Type, X-Auth-Token, Origin, Authorization");

        get("/test", (req, res) -> gson.toJson("name"));

        post("/extract-entity", (req,res)->{
            List<OntologyMapDto> response =  es.extractEntity(req.body());

            return new GsonBuilder().setPrettyPrinting().create().toJson(response);
        });

    }
}
