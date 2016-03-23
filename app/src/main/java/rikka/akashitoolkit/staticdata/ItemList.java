package rikka.akashitoolkit.staticdata;

import android.content.Context;
import android.content.res.AssetManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import rikka.akashitoolkit.model.Item;

/**
 * Created by Rikka on 2016/3/23.
 */
public class ItemList {
    private static final String FILE_NAME = "Item.json";

    private static List<Item> sList;

    public static synchronized List<Item> get(Context context) {
        if (sList == null) {
            try {
                AssetManager assetManager = context.getAssets();
                InputStream ims = assetManager.open(FILE_NAME);
                Gson gson = new Gson();
                Reader reader = new InputStreamReader(ims);
                Type listType = new TypeToken<ArrayList<Item>>() {}.getType();
                sList = gson.fromJson(reader, listType);
            } catch (IOException e) {
                e.printStackTrace();
                sList = new ArrayList<>();
            }
        }
        return sList;
    }
}