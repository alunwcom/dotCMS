package com.wiley.wws.search.plugin;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.Node;
import org.dom4j.XPath;

import com.dotmarketing.util.Logger;
import com.wiley.wws.utils.HTTPUtils;

/**
 * Created by Gareth Wright
 * Date: 01/10/12
 * 
 * Simple WWS Search Service
 * 
 */
public class SearchService implements ISearchService {

	protected String endpointURI;
	protected Map<String,String> resultsXPathMap;
	protected Map<String,String> requestNamespaceMap;
	protected Map<String,String> responseNamespaceMap;
	protected String searchResultXPath;
	protected String hitCountXPath;
	
	public SearchResponse search(SearchRequest searchRequest) {
		Logger.info(SearchService.class, "Making search...");
		Document xmlRequest = createRequest(searchRequest);
		Document xmlResponse = HTTPUtils.postRequest(endpointURI, xmlRequest.asXML());
		SearchResponse searchResponse = parseResponse(xmlResponse, searchRequest);
		return searchResponse;
	}
	
	private Document createRequest(SearchRequest searchRequest) {

		Document xmlRequest = DocumentHelper.createDocument();
		Namespace atomSpace = new Namespace("atom", "http://www.w3.org/2005/Atom");
		Namespace wsSpace = new Namespace("ws", "http://schemas.ws.wiley.com/ws-core");
		Namespace wssSpace = new Namespace("wss", "http://schemas.ws.wiley.com/wiley-search-service");
		
		Element entryElmt = xmlRequest.addElement("atom:entry");
		entryElmt.add(atomSpace);
		entryElmt.add(wsSpace);		
		entryElmt.add(wssSpace);
		entryElmt.addElement("id", "atom").setText("urn:x-wiley:wws:MySearchId");
		Element payloadElmt = entryElmt.addElement("ws:payload");
		payloadElmt.addAttribute("type", "xml");
		Element searchRequestElmt = payloadElmt.addElement("wss:search-request");
		//Result modifiers
		Element resultModifiersElmt = searchRequestElmt.addElement("wss:result-modifiers");		
		if(searchRequest.getOrderBy()!=null){
			Element orderByElmt = resultModifiersElmt.addElement("wss:order-by");		
			orderByElmt.setText(searchRequest.getOrderBy());
			if(searchRequest.getOrderDirection()!=null){
				orderByElmt.addAttribute("sort-dir",searchRequest.getOrderDirection());
			}
		}
		Element firstResultElmt = resultModifiersElmt.addElement("wss:first-result");	
		firstResultElmt.setText(searchRequest.getFirstResult()+"");
		Element resultCountElmt = resultModifiersElmt.addElement("wss:result-count");	
		resultCountElmt.setText(searchRequest.getResultCount()+"");
//		Element applyHighlightElmt = resultModifiersElmt.addElement("wss:apply-highlight");	
//		applyHighlightElmt.setText("none");
		Element maxSnippetsElmt = resultModifiersElmt.addElement("wss:max-snippets");	
		maxSnippetsElmt.setText(searchRequest.getMaxSnippets()+"");
		//Search scope
		Element searchScopeElmt = searchRequestElmt.addElement("wss:search-scope");	
		searchScopeElmt.addAttribute("content-set", "documents");
		//if A doi is present pubType is constrained to Journals.
		if(searchRequest.getDois()!=null && searchRequest.getDois().length>0){
			
			for(String doi : searchRequest.getDois()) {
				Element doiElmt = searchScopeElmt.addElement("wss:doi");
				doiElmt.setText(doi);
				if(searchRequest.getDoiScope()!=null){
					doiElmt.addAttribute("doi-type", searchRequest.getDoiScope()); 
				}
			}
		} else if (searchRequest.getPubTypes()!=null && searchRequest.getPubTypes().length>0){
			for(String pubType : searchRequest.getPubTypes()){
				searchScopeElmt.addElement("wss:pub-type").setText(pubType);
			}
		}
		//search constraints
		if(searchRequest.getQuery()!=null && searchRequest.getQuery().length()>0){
			Element searchContraintsElmt = searchRequestElmt.addElement("wss:search-constraints");	
			Element termConstraintElmt = searchContraintsElmt.addElement("wss:term-constraint");
			Element termExpressionElmt = termConstraintElmt.addElement("wss:term-expression");
			termExpressionElmt.setText(searchRequest.getQuery());
		}
//    	Logger.info( this.getClass(), xmlRequest.asXML());
		return xmlRequest;
	}
		
	private SearchResponse parseResponse(Document xmlResponse, SearchRequest searchRequest){
		SearchResponse searchResponse = new SearchResponse();
		searchResponse.setSearchRequest(searchRequest);
		
		Node hitCountNode = xmlResponse.getRootElement().selectSingleNode(hitCountXPath);
		searchResponse.setHitCount(Integer.parseInt(hitCountNode.getText()));
		
		List<Map<String,Object>> searchResults = new ArrayList<Map<String,Object>>();
		
		if(xmlResponse != null) {
			List nodes = xmlResponse.getRootElement().selectNodes(searchResultXPath);
			for(Object node:nodes) {
				Map<String,Object> resultMap = new HashMap<String,Object>();				
				if (node instanceof Element) {
					Element searchElement = (Element) node;				
					for(Object keyObj: resultsXPathMap.keySet()){
						String key = (String)keyObj;
						String xpath = (String)resultsXPathMap.get(key);
						parseSearchResultElement(resultMap, searchElement, xpath, key);
					}
					parseAuthors(resultMap, searchElement);
					searchResults.add(resultMap);
				}	
			}
		}
		searchResponse.setResults(searchResults);
		return searchResponse;
	}
	
	private void parseSearchResultElement(Map<String,Object> resultMap, Element searchElement, String xpath, String key) {
		if (searchElement != null) {
//	    	Logger.info( this.getClass(), "Extracting child element: " + key + " from " +  searchElement);

			XPath xpathObj = createXPath(xpath);
			Object childNode = xpathObj.selectSingleNode(searchElement);

			if(childNode != null) {
				if (childNode instanceof Element){
					Element childElement = (Element)childNode;
					if(childElement.isTextOnly()){
						String elementText = childElement.getText();
//						Logger.info(this.getClass(), "Extracted text: " + elementText);
						if(key.endsWith("Date")){
							String dateString = StringUtils.substringBefore(elementText,"T"); 
							//DateFormat dfIn = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSz"); //wired in pattern
							DateFormat dfIn = new SimpleDateFormat("yyyy-MM-dd");
							DateFormat dfOut = new SimpleDateFormat("dd MMM yyyy"); //could be provided by the searchRequest
							try {
								Date date =  dfIn.parse(dateString);
								resultMap.put(key, date);
								String formattedDate = dfOut.format(date);
								resultMap.put(key+"Formatted", formattedDate);
							} catch (ParseException ignore) {
								Logger.info(this.getClass(), "Failed to parse date: " + elementText);
							}
						} else {
							resultMap.put(key, childElement.getText());
						}

					} else {
//						Logger.info(this.getClass(), childElement.asXML());
						resultMap.put(key, childElement.asXML());
					}
				}
			}
		}
	}
	
	//WIP
	//TODO: Cannot use <title type="shortAuthors"> as rarely populated, shame as would be easier (one entry in resultsXPathMap) rather than this:
	private void parseAuthors(Map<String,Object> resultMap, Element searchElement) {
		List<String> authorList = new ArrayList<String>();
		
		if (searchElement != null) {

			XPath authorXPathObj = createXPath(".//d:author"); //TODO wire in config.
			List authorNodes = authorXPathObj.selectNodes(searchElement);
			XPath authorFirstNameXPathObj = createXPath(".//a:givenNames");
			XPath authorLastNameXPathObj = createXPath(".//a:familyName");
			
			for(Object authorNode:authorNodes) {
				if (authorNode instanceof Element) {
					Element authorElement = (Element)authorNode;
					String firstName = "";
					String lastName = "";
					Node authorFirstNameNode = authorFirstNameXPathObj.selectSingleNode(authorElement);
					Node authorLastNameNode = authorLastNameXPathObj.selectSingleNode(authorElement);
					if(authorFirstNameNode != null) {
						firstName = authorFirstNameNode.getText();
					}
					if(authorLastNameNode != null) {
						lastName = authorLastNameNode.getText();
					}
					String author = firstName + " " + lastName;
					StringUtils.trim(author);
					authorList.add(author);
				}	
			}
			resultMap.put("authors", StringUtils.join(authorList, ", "));
		}
	}
	
	private XPath createXPath(String path) {
		XPath xPath = DocumentHelper.createXPath(path);  
		xPath.setNamespaceURIs(responseNamespaceMap); 
		return xPath;
	}
	
	public void setEndpointURI(String endpointURI) {
		this.endpointURI = endpointURI;
	}
	
	public void setResultsXPathMap(Map<String,String> xpathMap) {
		this.resultsXPathMap = xpathMap;
	}

	public void setRequestNamespaceMap(Map<String,String> requestNamespaceMap) {
		this.requestNamespaceMap = requestNamespaceMap;
	}

	public void setResponseNamespaceMap(Map<String,String> namespaceMap) {
		this.responseNamespaceMap = namespaceMap;
	}

	public void setSearchResultXPath(String searchResultXPath) {
		this.searchResultXPath = searchResultXPath;
	}
	public void setHitCountXPath(String hitCountXPath) {
		this.hitCountXPath = hitCountXPath;
	}
	
}
