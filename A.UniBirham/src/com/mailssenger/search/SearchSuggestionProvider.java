package com.mailssenger.search;

import android.content.SearchRecentSuggestionsProvider;

public class SearchSuggestionProvider extends SearchRecentSuggestionsProvider {  
    /** 
     * Authority 
     */  
    final static String AUTHORITY = "com.mailssenger.search.SearchSuggestionProvider";  
    /** 
     * Mode 
     */  
    final static int MODE = DATABASE_MODE_QUERIES;  
    public SearchSuggestionProvider() {  
        super();  
        setupSuggestions(AUTHORITY, MODE);  
    }  
}  