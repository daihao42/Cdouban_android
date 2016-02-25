package com.example.dai.lol.mySQLlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.dai.lol.somemovie.shortMovie;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dai on 2015/12/21.
 * 用来存储电影信息的数据库
 */
public class MovieSQLlite extends SQLiteOpenHelper {
    //数据库名
    private static final String DATABASE_NAME = "LOL";
    //版本号
    private static final int DATABASE_VERSION = 1;
    //表名
    private static final String TABLE_NAME = "movie";
    //表初始化
    private static final String TABLE_CREATE = "CREATE TABLE "+TABLE_NAME+" ("+
            "id TEXT NOT NULL," +
            "title TEXT NOT NULL,"+
            "director TEXT NOT NULL,"+
            "actor TEXT NOT NULL,"+
            "types TEXT NOT NULL,"+
            "ontime TEXT NOT NULL);";

    public MovieSQLlite(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_NAME);
        onCreate(db);
    }

    //插入数据
    public long insertDB(shortMovie m){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        //根据ID判断记录是否已经存在
        Cursor cursor = db.query(TABLE_NAME,new String[]{"id"},"id=?",new String[]{m.getId()},null,null,null);
        //如果已经存在就不插入
        if(cursor.moveToFirst()){
            return 0;
        }
        values.put("id",m.getId());
        values.put("title",m.getTitle());
        values.put("director",m.getDirector());
        values.put("actor",m.getActor());
        values.put("types",m.getTypes());
        values.put("ontime", m.getOntime());
        long rowID = db.insert(TABLE_NAME,null,values);
        Log.d("sql", "insert");
        db.close();
        return rowID;
    }

    //查询返回最新的10条数据，即ID最大的10位
    public ArrayList<shortMovie> getNews(){
        SQLiteDatabase db = getWritableDatabase();
        //CAST(`id` AS DECIMAL)表示将id字段转为int型，同样的还可以用ABS(`id`),主要是因为id字段为String型，使用
        //order by 时并不会按照数值大小(int)来排序
        Cursor cursor = db.query(TABLE_NAME, new String[]{"id", "title", "director", "actor", "types", "ontime"},
                null, null, null,null, "CAST(`id` AS DECIMAL)  desc", "10");
        ArrayList<shortMovie> r = new ArrayList<shortMovie>();
        while(cursor.moveToNext()){
            String id = cursor.getString(cursor.getColumnIndex("id"));
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String director = cursor.getString(cursor.getColumnIndex("director"));
            String actor = cursor.getString(cursor.getColumnIndex("actor"));
            String types = cursor.getString(cursor.getColumnIndex("types"));
            String ontime = cursor.getString(cursor.getColumnIndex("ontime"));
            shortMovie m = new shortMovie(id,title,director,actor,types,ontime);
            r.add(m);
        }
        return r;
    }

}
