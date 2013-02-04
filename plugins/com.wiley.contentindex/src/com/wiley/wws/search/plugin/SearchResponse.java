package com.wiley.wws.search.plugin;

import java.util.List;
import java.util.Map;

public class SearchResponse {

	protected List<Map<String,Object>> results;
	protected int hitCount;
	protected SearchRequest searchRequest;
	protected int pageNumber;
	protected int pageCount;
	protected int previousPageNumber;
	protected int nextPageNumber;
	protected int pageRange = 5;
	protected int startPageRange;
	protected int endPageRange;
	
	public SearchResponse(){}
	
	private void updatePageStats() {
		pageNumber = (searchRequest.getFirstResult()+searchRequest.getResultCount())/searchRequest.getResultCount();
		pageCount = hitCount/searchRequest.getResultCount();
		if(pageNumber > 0) {
			previousPageNumber = pageNumber-1;
		}
		if(pageNumber < pageCount) {
			nextPageNumber = pageNumber+1;
		}
		startPageRange = pageNumber - pageRange;
		if (startPageRange < 1) {
			startPageRange = 1;
		}
		endPageRange = pageNumber + pageRange;
		if (endPageRange >= pageCount) {
			endPageRange = pageCount;
		}
	}
	public List<Map<String,Object>> getResults() {
		return results;
	}
	public void setResults(List<Map<String,Object>> results) {
		this.results = results;
	}
	public int getPageNumber() {
		return pageNumber;
	}
	public int getPageCount() {
		return pageCount;
	}
	public int getPreviousPageNumber() {
		return previousPageNumber;
	}
	public int getNextPageNumber() {
		return nextPageNumber;
	}
	public int getPageRange() {
		return pageRange;
	}
	public int getStartPageRange() {
		return startPageRange;
	}
	public int getEndPageRange() {
		return endPageRange;
	}
	public int getHitCount() {
		return hitCount;
	}
	public void setHitCount(int hitCount) {
		this.hitCount = hitCount;
		updatePageStats();
	}
	public SearchRequest getSearchRequest() {
		return searchRequest;
	}
	public void setSearchRequest(SearchRequest searchRequest) {
		this.searchRequest = searchRequest;
	}

	@Override
	public String toString() {
		return "SearchResponse [hitCount=" + hitCount
				+ ", searchRequest=" + searchRequest + ", pageNumber="
				+ pageNumber + ", pageCount=" + pageCount
				+ ", previousPageNumber=" + previousPageNumber
				+ ", nextPageNumber=" + nextPageNumber + ", pageRange="
				+ pageRange + ", startPageRange=" + startPageRange
				+ ", endPageRange=" + endPageRange + "]";
	}
	
}
