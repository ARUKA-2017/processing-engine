/**
 *
 */
package akura.crawler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import akura.corenlp.TokenExtraction;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Crawler {	
	public static ArrayList<Review> reviewList = new ArrayList<Review>();
    public static ArrayList<JSONObject> finalReviewList = new ArrayList<JSONObject>();

    ArrayList<Integer>  pagesScraped;
	public void fetchReview(String itemID, int noOfPages) {
        String url = Configuration.ITEM_ID_PREFIX + itemID + Configuration.ITEM_ID_POSTFIX + 0; //(1 + (int) (Math.random() * 10))
        // http://www.amazon.com/product-reviews/B00UC9QKQ2/?showViewpoints=0&sortBy=byRankDescending&pageNumber=0
        //Modify this file name to reflect the name of the product you are reviewing.
        try {
            // Get the max number of review pages;
            org.jsoup.nodes.Document reviewpage1 = null;
            reviewpage1 = Jsoup.connect(url).timeout(10 * 1000).get();
            int maxpage = 1;
            Elements pagelinks = reviewpage1.select(Configuration.PAGE_LINKS_HTML_QUERY);
            
            System.out.println("**Page Links**" + pagelinks);
            
            if (pagelinks.size() != 0) {
                ArrayList<Integer> pagenum = new ArrayList<Integer>();
                for (Element link : pagelinks) {
                    try {
                    	System.out.println("**link**" + link);
                        pagenum.add(Integer.parseInt(link.text()));
                    } catch (NumberFormatException nfe) {
                    }
                }
                maxpage = Collections.max(pagenum);
            }
            // collect review from each of the review pages;
            int p = 0;
            while (p <= maxpage) {
                System.out.println("Now in Page: " + p);

                url = 	Configuration.URL_PREFIX + itemID + Configuration.URL_POSTFIX + p;
                org.jsoup.nodes.Document reviewpage = null;
                reviewpage = Jsoup.connect(url).timeout(10 * 1000).get();
                if (reviewpage.select(Configuration.REVIEW_SECTION_HTML_QUERY).isEmpty()) {
                	System.out.println("Review section is empty");
                } else {
                    Elements reviewsHTMLs = reviewpage.select(Configuration.REVIEW_SECTION_HTML_QUERY);
                    for (Element reviewBlock : reviewsHTMLs) {
                        Review theReview = parseReview(reviewBlock, url);
                        reviewList.add(theReview);
                        theReview.printReviews();
                    }
                }

                if(p==noOfPages) {
//                    System.out.println(reviewList);
                    for (Review review: reviewList){
                        finalReviewList.add(review.getJSONObject());
                    }
                    System.out.println(finalReviewList);
                    TokenExtraction tokenExtraction = new TokenExtraction();
                    tokenExtraction.generateOntologyJson(finalReviewList);
                    break;
                }
                p++;
            }
        } catch (Exception e) {
        	e.printStackTrace();
            try {
                Thread.sleep((int)(1000.0 + Math.random() * 10000));
                fetchReview(itemID,noOfPages);
                e.printStackTrace();
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }
	
    public Review parseReview(Element reviewBlock, String url) throws ParseException {
        String review_id = "";
        String title = "";
        int rating = 0;
        String reviewContent = "";
        int classLabel = 0;

        // review id
        review_id = reviewBlock.id();

        // title
        Element reviewTitle = reviewBlock.select(Configuration.REVIEW_TITLE_HTML_QUERY).first();
        title = reviewTitle.text();

        // rating
        Element star = reviewBlock.select(Configuration.REVIEW_RATING_HTML_QUERY).first();
        String starinfo = star.text();
        rating = Integer.parseInt(starinfo.substring(0, 1));

        //class label
        classLabel = rating;

        // review date
        Elements date = reviewBlock.select(Configuration.REVIEW_DATE_HTML_QUERY);
        String datetext = date.first().text();
        datetext = datetext.substring(3); // remove "On "
        Date reviewDate = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH)
                .parse(datetext);

        // review content
        Element contentDoc = reviewBlock.select(Configuration.REVIEW_BODY_HTML_QUERY).first();
        reviewContent = contentDoc.text();

        return new Review(review_id, title, rating, url, reviewDate, reviewContent, classLabel);

    }
}
