package com.mailssenger.search;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

public class MySearchableActivity extends ActionBarActivity{
	
//	@Override  
//	public void onCreate(Bundle savedInstanceState) {  
//	    super.onCreate(savedInstanceState);  
//	    setContentView(R.layout.search);  
//	    Intent intent = getIntent();  
//	    if (Intent.ACTION_SEARCH.equals(intent.getAction())) {  
//	      String query = intent.getStringExtra(SearchManager.QUERY);  
//	      doMySearch(query);  
//	    }  
//	}  


	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	    	super.onCreate(savedInstanceState);
	    	
	       
	        handleIntent(getIntent());
	    }

	    @Override
	    protected void onNewIntent(Intent intent) {
	        
	        handleIntent(intent);
	    }

	    private void handleIntent(Intent intent) {
	    	String query = intent.getStringExtra(SearchManager.QUERY) ;
	    	SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,  
                    SearchSuggestionProvider.AUTHORITY, SearchSuggestionProvider.MODE);  
    	    suggestions.saveRecentQuery(query, null);  
	    	

	        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
	            

	            Log.e("hello","search!!!");
	            //use the query to search your data somehow
	        }
	    }


}
