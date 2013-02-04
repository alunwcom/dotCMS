package com.wiley.wws.search.plugin;

import java.util.Arrays;

public class SearchRequest {

	protected String query;
	protected String orderBy;
	protected String orderDirection;
	protected String[] pubTypes;
	protected String[] dois;
	protected String doiScope;
	protected int resultCount;
	protected int firstResult;
	protected int maxSnippets = 1;
	protected int pageNumber;
	
	public SearchRequest(){}
	
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public String getOrderBy() {
		return orderBy;
	}
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
	public String getOrderDirection() {
		return orderDirection;
	}
	public void setOrderDirection(String orderDirection) {
		this.orderDirection = orderDirection;
	}
	public String[] getPubTypes() {
		return pubTypes;
	}
	public void setPubTypes(String[] pubTypes) {
		this.pubTypes = pubTypes;
	}
	public String[] getDois() {
		return dois;
	}
	public void setDois(String[] dois) {
		this.dois = dois;
	}
	public String getDoiScope() {
		return doiScope;
	}
	public void setDoiScope(String doiScope) {
		this.doiScope = doiScope;
	}
	public int getResultCount() {
		return resultCount;
	}
	public void setResultCount(int resultCount) {
		this.resultCount = resultCount;
	}
	public int getFirstResult() {
		return firstResult;
	}
	public void setFirstResult(int firstResult) {
		this.firstResult = firstResult;
	}
	public int getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(int pageNumber) {
		this.firstResult = (pageNumber-1)*resultCount;
		this.pageNumber = pageNumber;
	}

	public int getMaxSnippets() {
		return maxSnippets;
	}
	public void setMaxSnippets(int maxSnippets) {
		this.maxSnippets = maxSnippets;
	}

	@Override
	public String toString() {
		return "SearchRequest [query=" + query + ", orderBy=" + orderBy
				+ ", orderDirection=" + orderDirection + ", pubTypes="
				+ Arrays.toString(pubTypes) + ", dois=" + Arrays.toString(dois)
				+ ", doiScope=" + doiScope + ", resultCount=" + resultCount
				+ ", firstResult=" + firstResult + ", maxSnippets="
				+ maxSnippets + ", pageNumber=" + pageNumber + "]";
	}
	
}
