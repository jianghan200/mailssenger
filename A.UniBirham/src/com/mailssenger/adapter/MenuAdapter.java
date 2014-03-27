package com.mailssenger.adapter;

import com.mailssenger.Task;
import com.mailssenger.mail.MailAccount;
import com.mailssenger.service.MainService;

import com.mailssenger.R;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MenuAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater inflater;

	private String[] item_name = { "Message", "Space", "Collection","Setting"};
	private int[] mMenuIcon = { R.drawable.menu_inbox, R.drawable.menu_sent,
			R.drawable.menu_trash,
			R.drawable.menu_write, R.drawable.menu_xin,R.drawable.menu_xin};

	public MenuAdapter(Context context) {
		super();
		this.context = context;
		this.inflater = LayoutInflater.from(context);
	}

	public void setMenuIcon(int[] menuIcon) {
		this.mMenuIcon = menuIcon;
	}

	public void setItem_name(String[] item) {
		this.item_name = item;
	}

	public int getCount() {
		return item_name.length;
	}

	public Object getItem(int position) {
		return item_name[position];

	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		MenuViewHolder viewHolder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.slidingmenu_menu_item, null);

			viewHolder = new MenuViewHolder();
			
			viewHolder.menu_row = (RelativeLayout) convertView
					.findViewById(R.id.menu_row);
			viewHolder.menu_item_icon = (ImageView) convertView
					.findViewById(R.id.menu_item_icon);
			viewHolder.menu_item_name = (TextView) convertView
					.findViewById(R.id.menu_item_name);
			viewHolder.menu_tips = (TextView) convertView
					.findViewById(R.id.menu_tips);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (MenuViewHolder) convertView.getTag();
		}

		viewHolder.menu_item_icon.setImageResource(mMenuIcon[position]);
		viewHolder.menu_item_name.setText(item_name[position]);
		viewHolder.menu_tips.setText("3");

		viewHolder.menu_item_name
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {

						if (position == 0) {
							
							Message msg = new Message();
							Bundle bundle = new Bundle();
							// save value in bundle
						
							bundle.putString( "sign", item_name[position] ); 
							// msg using bundle to pass value
							msg.setData(bundle); 
//							MainListActivity.handler.sendMessage(msg);
//							
//							MainListActivity.slidingMenu.showContent();

						}
						if (position == 1) {
							
						}
						if (position == 2) {
						
						}
						if (position == 3) {
							Task task = new Task();

							//Start another activity
							task.setContext(context);
							task.setPriority(0);
							// the priority is the lowest(not to disturb the user)
							task.setMethod(MailAccount.class, "getNewUnreadMailSet", "inbox");
							MainService.newTask(task);
						}
						if (position == 4) {
							
						}
						
						if (position == 5) {
							
						}
						
					}
				});

		return convertView;
	}

	final class MenuViewHolder {
		public RelativeLayout menu_row;
		public ImageView menu_item_icon;
		public TextView menu_item_name;
		public TextView menu_tips;

	}
}