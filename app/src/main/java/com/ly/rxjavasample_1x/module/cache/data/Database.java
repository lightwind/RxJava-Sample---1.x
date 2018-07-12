package com.ly.rxjavasample_1x.module.cache.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ly.rxjavasample_1x.App;
import com.ly.rxjavasample_1x.model.Item;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.List;

/**
 * Create by LiuYang on 2018/7/12 09:14
 */
public class Database {

    private static String DATA_FILE_NAME = "data.db";

    private static Database INSTANCE;

    private File mDataFile = new File(App.getInstance().getFilesDir(), DATA_FILE_NAME);
    private Gson mGson = new Gson();

    private Database() {
    }

    public static Database getInstance() {
        if (INSTANCE == null) INSTANCE = new Database();
        return INSTANCE;
    }

    public List<Item> readItems() {
        // Hard code adding some delay, to distinguish reading from memory and reading disk clearly
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            Reader reader = new FileReader(mDataFile);
            return mGson.fromJson(reader, new TypeToken<List<Item>>() {
            }.getType());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void writeItems(List<Item> items) {
        String json = mGson.toJson(items);
        try {
            if (!mDataFile.exists()) {
                try {
                    mDataFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Writer writer = new FileWriter(mDataFile);
            writer.write(json);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void delete() {
        mDataFile.delete();
    }

}
