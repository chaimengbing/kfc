package com.auv.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonWriter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by hhr on 15/12/10.
 */
public class SharedPreferenceHelper {

    private SharedPreferences preferences;
    private static SharedPreferenceHelper instance;

    private SharedPreferenceHelper(Context context) {
        this.preferences = PreferenceManager.getDefaultSharedPreferences( context );
    }

    public static SharedPreferenceHelper getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPreferenceHelper( context );
        }
        return instance;
    }


    public void removeByKey(String key) {
        SharedPreferences.Editor editor = this.preferences.edit();
        editor.remove( key );
        editor.apply();
    }

    public void saveBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = this.preferences.edit();
        editor.putBoolean( key, value );
        editor.apply();
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return this.preferences.getBoolean( key, defaultValue );
    }

    public void saveInt(String key, int value) {
        SharedPreferences.Editor editor = this.preferences.edit();
        editor.putInt( key, value );
        editor.apply();
    }

    public int getInt(String key, int defaultValue) {
        return this.preferences.getInt( key, defaultValue );
    }

    public void saveLong(String key, long value) {
        SharedPreferences.Editor editor = this.preferences.edit();
        editor.putLong( key, value );
        editor.apply();
    }

    public long getLong(String key, long defaultValue) {
        return this.preferences.getLong( key, defaultValue );
    }

    public void saveFloat(String key, long value) {
        SharedPreferences.Editor editor = this.preferences.edit();
        editor.putFloat( key, (float) value );
        editor.apply();
    }

    public float getFloat(String key, float defaultValue) {
        return this.preferences.getFloat( key, defaultValue );
    }

    public void saveString(String key, String value) {
        SharedPreferences.Editor editor = this.preferences.edit();
        editor.putString( key, value );
        editor.apply();
    }

    public String getString(String key, String defaultValue) {
        return this.preferences.getString( key, defaultValue );
    }

    /**
     * 保存List<Object> 数据
     */
    public void putListObject(String key, List<Object> value) {
        SharedPreferences.Editor editor = this.preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson( value );
        editor.putString( key, json );
        editor.apply();
    }

    public void putList(String key, List<Object> value) {
        SharedPreferences.Editor editor = this.preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson( value );
        editor.putString( key, json );
        editor.apply();
    }

    public void putMapList(String key, Map<String, List<String>> value) {
        SharedPreferences.Editor editor = this.preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson( value );
        editor.putString( key, json );
        editor.apply();
    }

    public void putMapJSONArray(String key, Map<String, JSONArray> value) {
        SharedPreferences.Editor editor = this.preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson( value );
        editor.putString( key, json );
        editor.apply();
    }

    /**
     * 保存List 数据
     */
    public void putListString(String key, List<String> list) {
        SharedPreferences.Editor editor = this.preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson( list );
        editor.putString( key, json );
        editor.apply();
    }

    /**
     * 保存Set数据
     */
    public void putSet(String key, Set<Object> list) {
        SharedPreferences.Editor editor = this.preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson( list );
        editor.putString( key, json );
        editor.apply();
    }

    /**
     * 保存Set数据
     */
    public int putObject(String key, Object object) {
        SharedPreferences.Editor editor = this.preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson( object );
        try {
            editor.putString( key, json );
            editor.apply();
            return 1;
        } catch (Exception e) {
            return -1;
        }

    }

    /**
     * 获取List数据
     */
    public List<Object> getList(String key) {
        Gson gson = new Gson();
        String json = this.preferences.getString( key, null );
        Type type = new TypeToken<List<Object>>() {
        }.getType();
        return gson.fromJson( json, type );
    }

    public Map<String, List<String>> getMapList(String key) {
        Gson gson = new Gson();
        String json = this.preferences.getString( key, null );
        Type type = new TypeToken<Map<String, List<String>>>() {
        }.getType();
        return gson.fromJson( json, type );
    }

    public Map<String, String> getMapString(String key) {
        Gson gson = new Gson();
        String json = this.preferences.getString( key, null );
        Type type = new TypeToken<Map<String, String>>() {
        }.getType();
        return gson.fromJson( json, type );
    }

    public Map<String, Integer> getMapInteger(String key) {
        Gson gson = new Gson();
        String json = this.preferences.getString( key, null );
        Type type = new TypeToken<Map<String, Integer>>() {
        }.getType();
        return gson.fromJson( json, type );
    }

    public Map<String, JSONArray> getMapJSONArray(String key) {
        Gson gson = new Gson();
        String json = this.preferences.getString( key, null );
        Type type = new TypeToken<Map<String, JSONArray>>() {
        }.getType();
        return gson.fromJson( json, type );
    }

    public JSONArray getJSONArray(String key) {
        Gson gson = new Gson();
        String json = this.preferences.getString( key, null );
        Type type = new TypeToken<JSONArray>() {
        }.getType();
        return gson.fromJson( json, type );
    }

    /**
     * 获取List数据
     */
    public List<String> getListString(String key) {
        Gson gson = new Gson();
        String json = this.preferences.getString( key, null );
        Type type = new TypeToken<List<String>>() {
        }.getType();
        return gson.fromJson( json, type );
    }

    /**
     * 获取List数据
     */
    public List<JSONObject> getListJSONObject(String key) {
        Gson gson = new Gson();
        String json = this.preferences.getString( key, null );
        Type type = new TypeToken<List<JSONObject>>() {
        }.getType();
        return gson.fromJson( json, type );
    }


    /**
     * 获取Set数据
     */
    public Set<Object> getSet(String key) {
        Gson gson = new Gson();
        String json = this.preferences.getString( key, null );
        Type type = new TypeToken<Set<Object>>() {
        }.getType();
        return gson.fromJson( json, type );
    }

    public Object getObject(String key) {
        Gson gson = new Gson();
        String json = this.preferences.getString( key, null );
        Type type = new TypeToken<Object>() {
        }.getType();
        return gson.fromJson( json, type );
    }


    /**
     * 清除所有数据
     */
    public void clear() {
        SharedPreferences.Editor editor = this.preferences.edit();
        editor.clear();
        editor.apply();
    }


    public void putObject(String key, Object data, Class<?> dataClass) {
        SharedPreferences.Editor editor = this.preferences.edit();
        String json = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        JsonWriter writer;
        try {
            writer = new JsonWriter( new OutputStreamWriter( out, "UTF-8" ) );
            if (dataClass == null) {
                dataClass = String.class;
            }
            Gson gson = new Gson();
            gson.toJson( data, dataClass, writer );
            writer.close();
            json = out.toString( "UTF-8" );
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!TextUtils.isEmpty( json )) {
            editor.putString( key, json );
            editor.apply();
        }

    }

    public Object getCellStatus(String key, Class<?> mClass) {
        Object object = null;
        try {
            String json = this.preferences.getString( key, null );
            if (!TextUtils.isEmpty( json )) {
                Type type = TypeToken.get( mClass ).getType();
                Gson gson = new Gson();
                object = gson.fromJson( json, type );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return object;
    }

}
