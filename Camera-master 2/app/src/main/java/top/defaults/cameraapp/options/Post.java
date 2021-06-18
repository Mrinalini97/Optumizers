package top.defaults.cameraapp.options;



import com.google.gson.annotations.SerializedName;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;

public class Post {
    private HashMap<String,List<String>> result;

    //    private String title;
    @SerializedName("body")
//    private String text;
    public HashMap<String,List<String>> getResults() {
        return result;
    }
//    public List<HashMap<String, String>> getId() {
//        return products;
//    }
//    public String getTitle() {
//        return title;
//    }
//    public String getText() {
//        return text;
//    }
}