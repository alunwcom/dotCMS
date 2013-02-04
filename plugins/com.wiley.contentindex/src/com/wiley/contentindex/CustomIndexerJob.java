package com.wiley.contentindex;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.dotcms.content.elasticsearch.business.ESIndexAPI;
import com.dotcms.content.elasticsearch.util.ESClient;
import com.dotmarketing.business.APILocator;

public class CustomIndexerJob implements Job {

	protected static Logger logger = Logger.getLogger(CustomIndexerJob.class);
	
	// Date format for dotCMS sitesearch index timestamp
	private DateFormat indexDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
	
	private DateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
	
	@Override
	public void execute(JobExecutionContext ctx) throws JobExecutionException {
		
		logger.debug("start custom job (" + ContentIndexPluginDeployer.VERSION + ")."); 
		
		// TODO Does index exist? Look-up from alias which should be passed as parameter
		String alias = "test1_alias";
		
		ESIndexAPI esAPI = APILocator.getESIndexAPI();
		// Lookup alias
		List<String> list = new ArrayList<String>(esAPI.listIndices());
		Map<String, String> aliases = esAPI.getAliasToIndexMap(list);
		String indexName = aliases.get(alias);
		
		// TODO Temporary hack to rebuild index each run!!		
		if (indexName != null) {
			logger.debug("Site search index already exists [" + indexName + "] alias = " + alias);
			
			deleteIndex(indexName);
		}
		
		indexName = buildIndex(indexName, alias);
				
		// Add/update index documents
		try {
			
			WOLJournalServiceAdapter content = new WOLJournalServiceAdapter();
			List<Map<String, Object>> results = content.exec("10.1111/(ISSN)1740-9713", 0, "title", "asc", 0, "", 200);
			
			Client client = new ESClient().getClient();
			XContentBuilder builder = null;
			for (Map<String, Object> article : results) {
				
				// Assuming dates to be UTC?
				builder = XContentFactory.jsonBuilder().startObject()
						.field("title", content.stripResultMarkup(article.get("title")))
						.field("author", content.stripResultMarkup(article.get("authors")))
						.field("content", content.stripResultMarkup(article.get("abstract")))
						.field("uri", "http://onlinelibrary.wiley.com/doi/" + content.stripResultMarkup(article.get("doi"))  + "/abstract")
//						.field("modified", "2012-08-09T00:00:00Z")
						.field("mimeType", "text/html")
						// Journal specific fields
						.field("pubType", content.stripResultMarkup(article.get("pubType")))
						.field("journalDoi", content.stripResultMarkup(article.get("journalDoi")))
						.field("journalVolume", content.stripResultMarkup(article.get("journalVolume")))
						.field("doi", content.stripResultMarkup(article.get("doi")))
						.field("journalTitle", content.stripResultMarkup(article.get("journalTitle")))
						.field("journalIssue", content.stripResultMarkup(article.get("journalIssue")))
						.field("journalIssueDoi", content.stripResultMarkup(article.get("journalIssueDoi")))
						.field("issuePublicationDate", getISODate((Date) article.get("issuePublicationDate")))
						.field("onlinePublicationDate", getISODate((Date) article.get("onlinePublicationDate")))
						.field("sortableDate", getISODate((Date) article.get("sortableDate")))
						.field("lastUpdateDate", getISODate((Date) article.get("lastUpdateDate")))
						.field("issueCoverDate", getISODate((Date) article.get("issueCoverDate")))
						.field("contentType", "journalArticle")
//						.field("doiKey", content.stripResultMarkup(article.get("doi")).replaceAll("[^a-zA-Z0-9]", "-"))
						.endObject();

				IndexResponse response = client.prepareIndex(indexName, "article", content.stripResultMarkup(article.get("doi")))
				        .setSource(builder)
				        .execute()
				        .actionGet();
				
				logger.debug("Updated document [" + indexName + "] [" + response.getType() + "] [" + response.getId() + "]");
				
	        }
			
		} catch (Exception e) {
			logger.error("Exception updating document [" + indexName + "] ", e);
			return;
		}
		
		logger.debug("end custom job (" + ContentIndexPluginDeployer.VERSION + ")."); 
		
	}
	
	private void deleteIndex(String indexName) {
		
		ESIndexAPI esAPI = APILocator.getESIndexAPI();
		esAPI.delete(indexName);
		
		logger.info("Search index deleted [" + indexName + "]");

	}
	
	private String buildIndex(String indexName, String alias) throws JobExecutionException {
		
		ESIndexAPI esAPI = APILocator.getESIndexAPI();
		
		// Create new index
		indexName = "sitesearch_" + indexDateFormat.format(new Date());
		try {
			esAPI.createIndex(indexName);
			esAPI.createAlias(indexName, alias);

			logger.info("New site search index created [" + indexName + "] alias = " + alias);
			
		} catch (Exception e) {
			logger.error("Exception creating index [" + indexName + "] alias = " + alias, e);
			throw new JobExecutionException("Exception creating index. ", e);
		}
		
		// Add mappings
		try {
			
			Client client = new ESClient().getClient();
			
			XContentBuilder mapping = XContentFactory.jsonBuilder()
				.startObject()
					.startObject("article")
						.startObject("properties")
							.startObject("title")
								.field("type", "multi_field")
								.field("path", "just_name")
								.startObject("fields")
									.startObject("title")
										.field("type", "string")
										.field("index", "analyzed")
									.endObject()
									.startObject("sortableTitle")
										.field("type", "string")
										.field("index", "not_analyzed")
										.field("store", "yes")
									.endObject()
								.endObject()
							.endObject()
						.endObject()
					.endObject()
				.endObject();
			
			client.admin().indices().preparePutMapping(indexName).setType("article").setSource(mapping).execute().actionGet();		

			logger.info("New mappings added [" + indexName + "]");
		} catch (Exception e) {
			logger.error("Exception creating mapping [" + indexName + "] alias = " + alias, e);
			throw new JobExecutionException("Exception creating mapping. ", e);
		}

		return indexName;
	}
	
	public String getISODate(Date date) {
		return isoDateFormat.format(date);
	}

}
