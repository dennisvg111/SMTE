package csi.fhict.org.wastedtime;

import android.app.Activity;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class MyUtility {

    public static boolean addFavoriteItem(Activity activity,String favoriteItem){
        //Get previous favorite items
        String favoriteList = getStringFromPreferences(activity,null,"favorites");
        // Append new Favorite item
        if(favoriteList!=null && favoriteList != ""){
            favoriteList = favoriteList+","+favoriteItem;
        }else{
            favoriteList = favoriteItem;
        }
        // Save in Shared Preferences
        return putStringInPreferences(activity,favoriteList,"favorites");
    }
    public static boolean removeFavoriteItem(Activity activity,String favoriteItem){
        //Get previous favorite items
        String favoriteList = getStringFromPreferences(activity,null,"favorites");
        // Append new Favorite item
        if(favoriteList!=null){
            String[] favoriteArray = convertStringToArray(favoriteList);
            if (favoriteArray.length > 0)
            {
                List<String> list = new ArrayList<String>(Arrays.asList(favoriteArray));
                list.remove(favoriteItem);
                boolean first = true;
                favoriteList = "";
                for (String item : list)
                {
                    if (!first)
                    {
                        favoriteList += ",";
                    }
                    favoriteList += item;
                    first = false;
                }
                // Save in Shared Preferences
                return putStringInPreferences(activity, favoriteList,"favorites");
            }
        }
        return false;
    }
    public static String[] getFavoriteList(Activity activity){
        String favoriteList = getStringFromPreferences(activity,null,"favorites");
        return convertStringToArray(favoriteList);
    }
    private static boolean putStringInPreferences(Activity activity,String nick,String key){
        SharedPreferences sharedPreferences = activity.getPreferences(Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, nick);
        editor.commit();
        return true;
    }
    private static String getStringFromPreferences(Activity activity,String defaultValue,String key){
        SharedPreferences sharedPreferences = activity.getPreferences(Activity.MODE_PRIVATE);
        String temp = sharedPreferences.getString(key, defaultValue);
        return temp;
    }

    private static String[] convertStringToArray(String str) {
        if (str == null || str == "") {
            return new String[0];
        }
        String[] arr = str.split(",");
        return arr;
    }
}
