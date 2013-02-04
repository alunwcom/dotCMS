package com.wiley.wws.search.viewtools;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.tools.view.tools.ViewTool;

import com.wiley.wws.search.plugin.ISearchService;
import com.wiley.wws.search.plugin.SearchRequest;
import com.wiley.wws.search.plugin.SearchResponse;

public class SearchServiceViewTool implements ViewTool {

	private ISearchService searchService;
	
	public void init(Object initData) {
	}

	public SearchResponse search(String query, String pubTypes, String orderBy, String orderDirection, String dois, String doiScope, String resultCount, String pageNumber) {
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setQuery(query);
		searchRequest.setPubTypes(parseArray(pubTypes));
		searchRequest.setOrderBy(orderBy);
		searchRequest.setOrderDirection(orderDirection);
		searchRequest.setDois(parseArray(dois));
		searchRequest.setDoiScope(doiScope);
		searchRequest.setResultCount(parseInt(resultCount));
		searchRequest.setPageNumber(parseInt(pageNumber));
		return searchService.search(searchRequest);
	}
	
	private int parseInt(String resultCount) {
		int resultCountInt = 0;
		try{
			resultCountInt = Integer.parseInt(resultCount);
		} catch (Exception ignore){
		}
		return resultCountInt;
	}
	
	private String[] parseArray(String csArray) {
		String[] stringArray = new String[]{};
		if(csArray!=null && csArray.length()>0){
			stringArray = StringUtils.split(csArray, ",");	
		}
		return stringArray;
	}

	public void setSearchService(ISearchService searchService) {
		this.searchService = searchService;
	}
	
}