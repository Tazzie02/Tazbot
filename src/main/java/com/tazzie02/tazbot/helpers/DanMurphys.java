package com.tazzie02.tazbot.helpers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.tazzie02.tazbot.exceptions.NotFoundException;

public class DanMurphys {
	
	private static final String searchBase = "https://www.danmurphys.com.au/dm/search/dm_search_results_gallery.jsp?searchterm=";
	private String searchUrl;
	private Elements products = new Elements();
	
	public DanMurphys(String product) throws IOException {
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(searchBase).append(URLEncoder.encode(product, "UTF-8"));
			searchUrl = sb.toString();
			String page = loadPage(searchUrl);
			search(page);
		} catch (UnsupportedEncodingException ignored) {}
	}
	
	public String getSearchUrl() {
		return searchUrl;
	}
	
	public String getBrand(int productNumber) {
		return products.get(productNumber)
				.select("h2.independent-product-module-title").first()
				.select("span.brand").text();
	}
	
	public String getProduct(int productNumber) {
		return products.get(productNumber)
				.select("h2.independent-product-module-title").first()
				.select("a").text().substring(getBrand(productNumber).length() + 1);
	}
	
	public String getRating(int productNumber) {
		return products.get(productNumber)
				.select("div.independent-product-module-review").first()
				.select("img[title]").attr("title");
	}
	
	public String getReviews(int productNumber) {
		return products.get(productNumber)
				.select("div.independent-product-module-review").first()
				.select("a").text();
	}
	
	public String getImage(int productNumber) {
		return "https://www.danmurphys.com.au" + 
				products.get(productNumber)
				.select("div.independent-product-image-container").first()
				.select("img[src].product-image").attr("src");
	}
	
	public String[] getPrice(int productNumber) {
		Elements prices = products.get(productNumber).select("ul.independent-pricepoint-list").first().select("li");
		String[] array = new String[prices.size()];
		
		for (int i = 0; i < prices.size(); i++) {
			Element el = prices.get(i);
			Elements spans = el.select("span");
			StringBuilder sb = new StringBuilder();
			
			for (int j = 0; j < spans.size(); j++) {
				Element el2 = spans.get(j);
				if (el2.hasClass("price") && !el2.text().contains("$")) {
					float price = Float.parseFloat(el2.text());
					sb.append("$")
					.append(String.format("%.2f", price));
				}
				else {
					sb.append(el2.text());
				}
				if (j != spans.size() - 1) {
					sb.append(" ");
				}
			}
			
			array[i] = sb.toString();
		}
		return array;
	}
	
	public String getUrl(int productNumber) {
		return products.get(productNumber)
				.select("h2.independent-product-module-title").first()
				.select("a[href]").attr("href");
	}
	
	public int getProductCount() {
		return products.size();
	}
	
	private String loadPage(String url) throws IOException {
//		System.out.println(url);
		String doc = null;
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(java.util.logging.Level.OFF);
		
		try (WebClient webClient = new WebClient(BrowserVersion.FIREFOX_45)) {
			webClient.getOptions().setThrowExceptionOnScriptError(false);
			HtmlPage page = webClient.getPage(url);
			doc = page.asXml();
		}
		catch (FailingHttpStatusCodeException e) {
			throw new IOException("FailingHttpStatusCodeException");
//			System.out.println("FailingHttpStatusCodeException");
		} catch (MalformedURLException e) {
			throw new IOException("MalformedURLException");
//			System.out.println("MalformedURLException");
		}
		return doc;
	}
	
	private void search(String html) {
//		try {
//			Document doc = Jsoup.connect(url)
//					.userAgent("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36")
//					.referrer("http://www.google.com")
//					.get();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		Document doc = Jsoup.parse(html);
		List<Element> elements = doc.select("div.product-grid");
		if (elements.size() == 0) {
			throw new NotFoundException();
		}
		
		for (Element el : elements) {
			products.addAll(el.children());
		}
	}
	
}
