package akura.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.select.Selector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Scrapes data for the selected Amazon products.
 *
 * Accepts an url or a collection of urls.
 * For each one downloads the web page and scrapes the data for the product.
 * Get the product or the collection of products as Items by calling the getItem() or getItems() methods.
 */
public class ItemLookUp {

    // User agent used to get the web page.
    private final String USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.90 Safari/537.36";

    // Spoof the scraping by telling the page from where the request has been sent:
    private final String REFERRER = "https://www.google.com";

    // Pattern to check if a string could be an url.
    private final Pattern urlPattern = Pattern.compile("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");

    // Queries used to select the elements from the web page:
    private final String priceQuery = "span[id*=ourprice],span[id*=saleprice],span.a-size-large.a-color-price.guild_priceblock_ourprice";
    private final String dealPriceQuery = "span[id*=dealprice]";
    private final String titleQuery = "span[id=productTitle]";

    public String getItemDimensionsQuery(Document document) {
//        return itemDimensionsQuery;
        if (document == null) {
            return null;
        }

        try {

            Elements elements = document.select(itemDimensionsQuery);

            if (elements != null && elements.size() > 0) {
                return elements.get(0).text();
            } else {
                return null;
            }

        } catch (Selector.SelectorParseException exception) {
            return null;
        }
    }

    private final String itemDimensionsQuery = "#HLCXComparisonTable > tbody > tr:nth-child(10) > td.comparison_baseitem_column > span";

    private final String categoryQuery = "a.a-link-normal.a-color-tertiary";
    private final String availabilityQuery = "span[id=availability],div[id=availability],p.a-spacing-micro.a-color-secondary.a-text-bold";

    // Default time to wait between each connection, in millis.
    private final static long DEFAULT_INTERVAL = 750;

    // The Amazon products web pages:
    private List<Document> documents;

    /**
     * Connects to the url and gets its source code.
     *
     * @param url           The url to connect to.
     * @throws IOException  If the connection failed.
     */
    public ItemLookUp(String url) throws IOException {

        this.documents = new ArrayList<Document>();

        Document document = Jsoup.connect(url)
                .userAgent(this.USER_AGENT)
                .referrer(this.REFERRER)
                .get();

        documents.add(document);

    }

    /**
     * Connects to the urls in the list and gets their source code.
     *
     * Note that if a connection fails, the corresponding page is ignored.
     *
     * @param urls					List which contains the urls to connect to.
     * @param interval				Time to wait between each connection, in millis.
     * @throws InterruptedException When the sleep between connections has been interrupted.
     */
    public ItemLookUp(List<String> urls, long interval) throws InterruptedException {

        this.documents = new ArrayList<Document>();

        try {

            for (String url : urls) {
                Document document = Jsoup.connect(url)
                        .userAgent(this.USER_AGENT)
                        .referrer(this.REFERRER)
                        .get();

                documents.add(document);

                Thread.sleep(interval);
            }

        } catch (IOException e) {
            // The Document instance of a page which couldn't be read is null:
            documents.add(null);
        }

    }

    /**
     * Connects to the urls in the list and gets their source code.
     *
     * Note that if a connection fails, the corresponding page is ignored.
     *
     * @param urls					List which contains the urls to connect to.
     * @throws InterruptedException	When the sleep between connections has been interrupted.
     */
    public ItemLookUp(List<String> urls) throws InterruptedException {
        this(urls, DEFAULT_INTERVAL);
    }

    /**
     * Returns the list of formatted prices.
     * Each value is a string like EUR 0,00 or $0.00.
     *
     * Note that a price which couldn't be parsed is null.
     */
    public List<String> getFormattedPrices() {

        List<String> formattedPrices = new ArrayList<String>();

        for (Document document : documents) {
            formattedPrices.add(getFormattedPrice(document));
        }

        return formattedPrices;

    }

    /**
     * Returns the list of price values.
     * Each value is parsed from the corresponding formatted price string.
     *
     * Note that a price value which couldn't be parsed is null.
     */
    public List<Float> getPriceValues() {

        List<Float> priceValues = new ArrayList<Float>();

        for (Document document : documents) {
            priceValues.add(getPriceValue(document));
        }

        return priceValues;

    }

    /**
     * Returns the list of categories.
     * Each value is the category of an item.
     *
     * Note that a category which couldn't be parsed is null.
     */
    public List<String> getCategories() {

        List<String> categories = new ArrayList<String>();

        for (Document document : documents) {
            categories.add(getCategory(document));
        }

        return categories;

    }

    /**
     * Returns the list of availability texts.
     * Each value is the string which appears in each item web page, that shows if the item is available and how many
     * items are in stock.
     *
     * Note that an availability text which couldn't be parsed is null.
     */
    public List<String> getAvailabilities() {

        List<String> availabilities = new ArrayList<String>();

        for (Document document : documents) {
            availabilities.add(getAvailability(document));
        }

        return availabilities;

    }

    /**
     * Returns the list of titles.
     *
     * Note that a title which couldn't be parsed is null.
     */
    public List<String> getTitles() {

        List<String> titles = new ArrayList<String>();

        for (Document document : documents) {
            titles.add(getTitle(document));
        }

        return titles;

    }

    /**
     * Checks for a sale price and then returns the current price as a formatted String.
     * Each value is a string like EUR 0,00 or $0.00.
     *
     * Note that the value is null if can't be parsed.
     */
    private String getFormattedPrice(Document document) {

        if (document == null) {
            return null;
        }

        // Check if there is a deal price:
        try {

            Elements elements = document.select(dealPriceQuery);

            // Return the deal price, if there is one:
            if (elements != null && elements.size() > 0) {
                return elements.get(0).text();
            }

            // If the isn't a deal price, get the current price:
            elements = document.select(priceQuery);

            if (elements != null && elements.size() > 0) {
                return elements.get(0).text();
            } else {
                return null;
            }

        } catch (Selector.SelectorParseException exception) {
            return null;
        }

    }

    /**
     * Parses the current price from the formatted price string, and return the value as a Float.
     *
     * Note that the value is null if can't be parsed.
     * Note that can parse only euros and dollars.
     */
    private Float getPriceValue(Document document) {

        String formattedPrice = getFormattedPrice(document);

        if (formattedPrice == null) {
            return null;
        }

        // Replace the comma with a dot, if present:
        formattedPrice = formattedPrice.replace(',', '.');

        try {

            // Check if it's in dollars or euros:
            if (formattedPrice.charAt(0) == '$') {

                return Float.parseFloat(formattedPrice.substring(1, formattedPrice.length()));

            } else if (formattedPrice.substring(0, 3).equals("EUR")) {

                return Float.parseFloat(formattedPrice.substring(4, formattedPrice.length()));

            } else {
                // It's an unrecognized currency. Not supported.
                return null;
            }

        } catch (NumberFormatException exception) {
            return null;
        }

    }

    /**
     * Gets the availability text, as seen on the Amazon page.
     *
     * Note that the value is null if can't be parsed.
     */
    private String getAvailability(Document document) {

        if (document == null) {
            return null;
        }

        try {

            Elements elements = document.select(availabilityQuery);

            if (elements != null && elements.size() > 0) {
                return elements.get(0).text();
            } else {
                return null;
            }

        } catch (Selector.SelectorParseException exception) {
            return null;
        }

    }

    /**
     * Gets the item title.
     *
     * Note that the value is null if can't be parsed.
     */
    private String getTitle(Document document) {

        if (document == null) {
            return null;
        }

        try {

            Elements elements = document.select(titleQuery);

            if (elements != null && elements.size() > 0) {
                return elements.get(0).text();
            } else {
                return null;
            }

        } catch (Selector.SelectorParseException exception) {
            return null;
        }

    }

    /**
     * Gets the item category.
     *
     * Note that the value is null if can't be parsed.
     */
    private String getCategory(Document document) {

        if (document == null) {
            return null;
        }

        try {

            Elements elements = document.select(categoryQuery);

            if (elements != null && elements.size() > 0) {
                return elements.get(0).text();
            } else {
                return null;
            }

        } catch (Selector.SelectorParseException exception) {
            return null;
        }

    }

    /**
     * Get all the scraped data as a list of Items.
     *
     * Note that some values could be null if invalid or not found.
     */
//    public List<Item> getItems() {
//
//        if (documents == null) {
//            return null;
//        }
//
//        List<String> titles = getTitles();
//        List<String> categories = getCategories();
//        List<String> availabilities = getAvailabilities();
//        List<String> formattedPrices = getFormattedPrices();
//        List<Float> priceValues = getPriceValues();
//
//        List<Item> items = new ArrayList<Item>();
//
//        for (int i = 0; i < documents.size(); i++) {
//
//            if (documents.get(i) == null) {
//                items.add(null);
//            } else {
//
//                Item item = new Item(
//                        titles.get(i),
//                        categories.get(i),
//
//                        availabilities.get(i),
//                        formattedPrices.get(i),
//                        priceValues.get(i),
//                        documents.get(i).location());
//
//                items.add(item);
//
//            }
//
//        }
//
//        return items;
//
//    }

    /**
     * Returns all the data for the first item, as an Item.
     * Call this method in case you instantiated the class with a single url.
     *
     * Note that some values could be null if invalid or not found.
     */
    public Item getItem() {

        if (documents == null || documents.get(0) == null) {
            return null;
        }

        String title = getTitle(documents.get(0));
        String category = getCategory(documents.get(0));
        String availability = getAvailability(documents.get(0));
        String dimensions = getItemDimensionsQuery(documents.get(0));
        String formattedPrice = getFormattedPrice(documents.get(0));
        Float priceValue = getPriceValue(documents.get(0));

        return new Item(
                title,
                category,
                dimensions,
                availability,
                formattedPrice,
                priceValue,
                documents.get(0).location()
        );

    }

}