package com.example.dai.lol.myStorage;

import android.content.Context;
import android.os.Environment;

import com.example.dai.lol.myException.StorageException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by dai on 2015/12/15.
 * 存储头像到外部存储上(emmc)，部分手机识别为SD卡，视具体型号而定
 * 方式为私有外部文件，/sdcard/Android/data/<包名>/files
 */
public class headImageStorage extends myBaseStorage{
    //声明Context才能调用Activity的方法，例如getResource
    protected Context context;

    public headImageStorage(Context context) throws StorageException {
        super(context);
        this.context = context;
    }

    /**
     * 存储图片
     * @param imagename 图片名称
     * @return
     */
    @Override
    public boolean save(String imagename, InputStream in) throws StorageException {
        try{
            File path = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File file = new File(path,imagename);
            createFile(file,in);
            return true;
        } catch (StorageException e){
            throw new StorageException("图片保存失败");
        }
    }

    /**
     * 判断图片是否存在
     * @param name
     * @return
     */
    @Override
    public boolean isVaild(String name) {
            File path = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File file = new File(path,name);
            if(file.exists())
            {
                return true;
            }
        return false;
    }

    /**
     * 读取图片
     * @param name
     * @return
     */
    @Override
    public InputStream get(String name) {
        File path = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File file = new File(path,name);
        try {
            return (InputStream)(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
