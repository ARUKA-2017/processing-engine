package akura.crawler;

import akura.cloundnlp.EntityExtractor;
import akura.cloundnlp.EntityExtractorInterface;
import akura.cloundnlp.dtos.OntologyMapDto;

import java.io.IOException;
import java.util.List;

public class Execute {

    public static void main(String args[]) throws IOException {
        //call the crawler from the executive
        //http://www.amazon.com/Mojang-Minecraft-Pocket-Edition/dp/B00992CF6W/ref=sr_1_1?s=mobile-apps&ie=UTF8&qid=1462571451&sr=1-1&keywords=minecraft
        //for above url, the product id is B00992CF6W, simlarly extract all the product ids from the url and pass to this executive in code.
//        Crawler crawler = new Crawler();
//        crawler.fetchReview("B00UC9QKQ2", 1);//(Item Id, Number of pages to crawl)
        //B01DQCB930
        ItemLookUp il = new ItemLookUp("https://www.amazon.com/Samsung-Galaxy-SM-G930A-Unlocked-Smartphone/dp/B01CYYYRNK");
        System.out.println("Asin: " + il.getItem().getAsinCode());
        System.out.println("Availability: " + il.getItem().getAvailability());
        System.out.println("Category: " + il.getItem().getCategory());
        System.out.println("Formatted Price: " + il.getItem().getFormattedPrice());
        System.out.println("Title: " + il.getItem().getTitle());
        System.out.println("Url: " + il.getItem().getItemUrl());
        System.out.println("Availabilities: " + il.getAvailabilities());
        System.out.println("Dimensions: " + il.getItem().getDimensions());

        Crawler crawler = new Crawler();
        crawler.fetchReview(il.getItem().getAsinCode(), 1);//(Item Id, Number of pages to crawl)
        EntityExtractorInterface ex = new EntityExtractor();
        ex.extractEntityData("IPhone 6S");
    }

    /**
     * endpoint
     */
    public List<OntologyMapDto> extractReviewOntologyMap(String url, String searchKeyWord){
        ItemLookUp il = null;
        try {
            il = new ItemLookUp(url);
        } catch (IOException e) {
            e.printStackTrace();
        }


        Crawler crawler = new Crawler();
        crawler.fetchReview(il.getItem().getAsinCode(), 1);//(Item Id, Number of pages to crawl)
        EntityExtractorInterface ex = new EntityExtractor();
        return ex.extractEntityData(searchKeyWord);
    }
}
