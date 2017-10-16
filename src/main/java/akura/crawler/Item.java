package akura.crawler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents an Amazon product.
 *
 * You can get the following data:
 * the product title, its category, if it's available and how many there are in stock, the price formatted as a String,
 * the price value as a Float, the product url, and its ASIN code.
 *
 * Note that a value could be null if invalid or not available.
 */
public class Item {

    private String title;
    private String category;

    public String getDimensions() {
        return dimensions;
    }

    private String dimensions;
    private String availability;
    private String formattedPrice;
    private Float priceValue;
    private String itemUrl;
    private String asinCode;

    // Pattern to check if a string could be an ASIN code.
    final Pattern asinCodePattern = Pattern.compile("[A-Z0-9]{10}");

    public Item(
            String title,
            String category,
            String dimensions,
            String availability,
            String formattedPrice,
            Float priceValue,
            String itemUrl) {

        this.title = title;
        this.category = category;
        this.dimensions = dimensions;
        this.availability = availability;
        this.formattedPrice = formattedPrice;
        this.priceValue = priceValue;
        this.itemUrl = itemUrl;
        this.asinCode = null;

        // Try to get the ASIN code:
        if (itemUrl != null) {
            // The ASIN code should be the first occurrence of a group of ten capital letters or numbers.
            Matcher matcher = asinCodePattern.matcher(itemUrl);
            if (matcher.find()) {
                this.asinCode = matcher.group(0);
            }
        }

    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public String getAvailability() {
        return availability;
    }

    public String getFormattedPrice() {
        return formattedPrice;
    }

    public Float getPriceValue() {
        return priceValue;
    }

    public String getItemUrl() {
        return itemUrl;
    }

    public String getAsinCode() {
        return asinCode;
    }

}