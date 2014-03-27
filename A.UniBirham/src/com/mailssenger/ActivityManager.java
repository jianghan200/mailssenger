package com.mailssenger;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import android.app.Activity;
import android.content.Context;
 
/** 
 * This is use to manage the activity
 */
public class ActivityManager { 
          
        private Context context; 
          
        private static ActivityManager activityManager; 
         
        /**
         * Get  ActivityManager instance
         */
        public static ActivityManager getActivityManager(Context context){ 
                if(activityManager == null){ 
                        activityManager = new ActivityManager(context); 
                } 
                return activityManager; 
        }
        
        /**
         * Set  ActivityManager context
         */
        private ActivityManager(Context context){ 
                this.context = context; 
        } 
          
        /** 
         * Task Map, to record the Activity Stack for the exit of the Application
         * SoftReference is used here to avoid the influence of the System to recycle the activity.
         */
        private final HashMap<String, SoftReference<Activity>> taskMap = new HashMap<String, SoftReference<Activity>>(); 
          
        /** 
         * Add activity to the Task Map
         */
        public final void putActivity(Activity atv) { 
                taskMap.put(atv.toString(), new SoftReference<Activity>(atv)); 
        } 
          
        /** 
         * Remove activity to the Task Map
         */
        public final void removeActivity(Activity atv) { 
                taskMap.remove(atv.toString()); 
        } 
          
        /** 
         * clean the Task Stake,if app is running this will make it back to the desktop
         */
        public final void exit() { 
                for (Iterator<Entry<String, SoftReference<Activity>>> iterator = taskMap.entrySet().iterator(); iterator.hasNext();) { 
                        SoftReference<Activity> activityReference =  iterator.next().getValue(); 
                        Activity activity = activityReference.get(); 
                        if (activity != null) { 
                                activity.finish(); 
                        } 
                } 
                taskMap.clear(); 
        } 
  
}