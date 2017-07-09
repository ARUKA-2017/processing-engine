package akura.crawler;

public final class Configuration {
	public static final String ITEM_ID_PREFIX = "http://www.amazon.com/product-reviews/";
	public static final String ITEM_ID_POSTFIX = "/?showViewpoints=0&sortBy=byRankDescending&pageNumber=";
	
	public static final String URL_PREFIX = "http://www.amazon.com/product-reviews/";
	public static final String URL_POSTFIX = "/?sortBy=helpful&pageNumber=";
	
	public static final String PAGE_LINKS_HTML_QUERY = "a[href*=pageNumber=]";
	public static final String REVIEW_SECTION_HTML_QUERY = "div.a-section.review";
	public static final String REVIEW_TITLE_HTML_QUERY = "a.review-title";
	public static final String REVIEW_RATING_HTML_QUERY = "i.a-icon-star";
	public static final String REVIEW_DATE_HTML_QUERY = "span.review-date";
	public static final String REVIEW_BODY_HTML_QUERY = "span.review-text";
}
