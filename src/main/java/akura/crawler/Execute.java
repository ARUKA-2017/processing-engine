package akura.crawler;

import java.io.IOException;

public class Execute {

    public static void main(String args[]) throws IOException {
        //call the crawler from the executive
        //http://www.amazon.com/Mojang-Minecraft-Pocket-Edition/dp/B00992CF6W/ref=sr_1_1?s=mobile-apps&ie=UTF8&qid=1462571451&sr=1-1&keywords=minecraft
        //for above url, the product id is B00992CF6W, simlarly extract all the product ids from the url and pass to this executive in code.
        Crawler crawler = new Crawler();
        crawler.fetchReview("B06XYQGPKT", 0);//(Item Id, Number of pages to crawl)


        // iPhone 7 - https://www.amazon.com/Apple-iPhone-Unlocked-32-GB/dp/B01M044EYV
        // Galaxy S7 Edge - https://www.amazon.com/Samsung-Factory-Unlocked-International-Version/dp/B01CJU9BBM
        // iPhone 6S - https://www.amazon.com/Apple-Unlocked-Smartphone-Certified-Refurbished/dp/B00YD545CC/
        // iPhone 5 - https://www.amazon.com/product-reviews/B00WZR5URO
    }

}
