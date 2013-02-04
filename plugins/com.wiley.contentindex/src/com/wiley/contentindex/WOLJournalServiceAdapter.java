package com.wiley.contentindex;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.wiley.wws.search.plugin.SearchRequest;
import com.wiley.wws.search.plugin.SearchResponse;
import com.wiley.wws.search.plugin.SearchService;

/**
 * Temporary adapter class to allow access to journal content via a demo WOL 
 * RESTful service, using Gareth's previously developed classes.
 */
public class WOLJournalServiceAdapter {

	protected static Logger logger = Logger.getLogger(WOLJournalServiceAdapter.class);
	
	private SearchService service;
	
	/**
	 * Local test main method.
	 */
	public static void main(String[] args) {
		
		WOLJournalServiceAdapter app = new WOLJournalServiceAdapter();
		app.testOutput(app.exec("10.1111/(ISSN)1740-9713", 0, "title", "asc", 0, "", 10));
	}
	
	/**
	 * Includes temporary hard-coded config.
	 */
	public WOLJournalServiceAdapter() {
		
		service = new SearchService();
		
		service.setEndpointURI("http://qae.ws.wiley.com:8001/wss/ol/search");
		service.setHitCountXPath(".//wss:hit-count");
		service.setSearchResultXPath("//wss:search-result");
		
		Map<String, String> resultMap = new HashMap<String, String>();
		resultMap.put("index", ".//wss:index");
		resultMap.put("title", ".//d:document-title");
		resultMap.put("doi", ".//wss:doi");
		resultMap.put("pubType", ".//wss:pub-type");
		resultMap.put("abstract", ".//a:abstract");
		resultMap.put("snippet", ".//wss:snippets/*");
		resultMap.put("journalTitle", ".//d:fullTitle");
		resultMap.put("journalIssueDoi", ".//d:journalIssueDoi");
		resultMap.put("journalDoi", ".//d:journalDoi");
		resultMap.put("journalVolume", ".//d:journalVolume");
		resultMap.put("journalIssue", ".//d:journalIssue");
		resultMap.put("onlinePublicationDate", ".//d:onlinePublicationDate");
		resultMap.put("issuePublicationDate", ".//d:issuePublicationDate");
		resultMap.put("sortableDate", ".//d:sortableDate");
		resultMap.put("issueCoverDate", ".//d:issueCoverDate");
		resultMap.put("lastUpdateDate", ".//d:lastUpdateDate");
		service.setResultsXPathMap(resultMap);
		
		Map<String, String> responseNamespaceMap = new HashMap<String, String>();
		responseNamespaceMap.put("ws", "http://schemas.ws.wiley.com/ws-core");
		responseNamespaceMap.put("wss", "http://schemas.ws.wiley.com/wiley-search-service");
		responseNamespaceMap.put("c", "urn://online.library.wiley.com/content/citation");
		responseNamespaceMap.put("d", "urn://wiley-online-library/content/document");
		responseNamespaceMap.put("a", "http://www.wiley.com/namespaces/wiley");
		service.setResponseNamespaceMap(responseNamespaceMap);
		
		Map<String, String> requestNamespaceMap = new HashMap<String, String>();
		requestNamespaceMap.put("atom", "http://www.w3.org/2005/Atom");
		requestNamespaceMap.put("ws", "http://schemas.ws.wiley.com/ws-core");
		requestNamespaceMap.put("wss", "http://schemas.ws.wiley.com/wiley-search-service");
		service.setRequestNamespaceMap(requestNamespaceMap);
	}
	
	/**
	 * Return a (map) results from the journal service.
	 */
	public List<Map<String, Object>> exec(String doi, int firstResult, String orderBy, String direction, int pageNumber, String query, int resultCount) {
		
		logger.debug("starting query of web service");
		
		SearchRequest searchRequest = new SearchRequest();
        searchRequest.setDois(new String[] {doi});
//        searchRequest.setDoiScope(doiScope);
        searchRequest.setFirstResult(firstResult);
//        searchRequest.setMaxSnippets(1);
        searchRequest.setOrderBy(orderBy);
        searchRequest.setOrderDirection(direction);
        searchRequest.setPageNumber(pageNumber);
//        searchRequest.setPubTypes(new String[] {"articles"});
        searchRequest.setQuery(query);
        searchRequest.setResultCount(resultCount);
        
        SearchResponse searchReponse = service.search(searchRequest);
        
//        System.out.println("Results" + searchReponse );

        logger.debug("finished query of web service");
        
        return searchReponse.getResults();
		
	}
	
	/**
	 * Demo output method
	 */
	private void testOutput(List<Map<String, Object>> results) {
		for (Map<String, Object> r : results) {
			System.out.println(r);
        }
	}
	
	/**
	 * Utility method to strip unwanted markup from result values. 
	 */
	public String stripResultMarkup(Object obj) {
		if (obj != null) {
			return obj.toString().replaceAll("\\<.*?\\>", "");
		}
		return "";
	}

}
