package com.wiley.wws.search.plugin;

import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.dotmarketing.util.Logger;

/**
 * Created by Gareth Wright
 * Date: 01/10/12
 */

@EnableWebMvc
@Configuration
@RequestMapping ("/search")
@Controller
public class SearchServiceController {

	private ISearchService searchService;
	
    
    @RequestMapping(value = "/{searchQuery}", method = RequestMethod.GET)
    public ModelAndView getHello(@PathVariable String searchQuery, Model model) {

        Logger.info( this.getClass(), "Received search query: " + searchQuery );
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setQuery(searchQuery);
        SearchResponse searchReponse = searchService.search(searchRequest);
        Logger.info( this.getClass(), "Results" + searchReponse );
        return new ModelAndView("searchResults", "searchResults", searchReponse);
    }
    
    
	public void setSearchService(ISearchService searchService) {
		this.searchService = searchService;
	}

    
}