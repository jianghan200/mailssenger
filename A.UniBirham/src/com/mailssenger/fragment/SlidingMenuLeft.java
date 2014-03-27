package com.mailssenger.fragment;

import com.mailssenger.adapter.MenuAdapter;
import com.mailssenger.util.KenBurnsView;

import com.mailssenger.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

public class SlidingMenuLeft extends Fragment{
	
	

    private static final String TAG = "SlidingMenuLeft";

    private KenBurnsView mHeaderPicture;
    private ImageView mHeaderLogo;
    private View mHeader;

	
	private ListView menuListView; 
	private MenuAdapter adapter;


	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
			
		View view = inflater.inflate(R.layout.slidingmenu, null);
		
		mHeader = view.findViewById(R.id.header);
        mHeaderPicture = (KenBurnsView) view.findViewById(R.id.header_picture);
        mHeaderPicture.setResourceIds(R.drawable.picture0, R.drawable.picture1);
        mHeaderLogo = (ImageView) view.findViewById(R.id.header_logo);

		return view;
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		
		super.onActivityCreated(savedInstanceState);
		
		menuListView=(ListView)this.getActivity().findViewById(R.id.listview);
		menuListView.setDivider(null);
		adapter = new MenuAdapter(this.getActivity());
		menuListView.setAdapter(adapter);
				
	}
}
