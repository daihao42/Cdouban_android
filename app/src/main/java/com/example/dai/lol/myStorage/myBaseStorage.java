package com.example.dai.lol.myStorage;

import android.content.Context;
import android.os.Environment;

import com.example.dai.lol.myException.StorageException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by dai on 2015/12/15.
 */
public abstract class myBaseStorage {
    //声明Context才能调用Activity的方法，例如getResource
    protected Context context;

    //判断外部存储是否可用
    public myBaseStorage(Context context) throws StorageException {
        this.context = context;
        String state = Environment.getExternalStorageState();
        if(state.equals(Environment.MEDIA_MOUNTED)){
            //可以读写存储媒体
        } else if(state.equals(Environment.MEDIA_MOUNTED_READ_ONLY)){
            //存储媒体只读
            throw new StorageException("外部存储只读!");
        } else {
            //外部存储不可用
            throw new StorageException("外部存储不可用!");
        }
    }

    protected void createFile(File file,InputStream in) throws StorageException {
        //获取文件上一层文件夹
        File parentPath = file.getParentFile();
        try{
            //如果父文件夹不存在，创建它
            if(!parentPath.exists()){
                parentPath.mkdir();
            }
            //如果文件已经存在，删除它，也许我要更新资源所以，删除
            if(file.exists()){
                file.delete();
            }
            //打开创建文件流，写入文件
            OutputStream out = new FileOutputStream(file);
            //创建流大小的bytes
            byte[] data = new byte[in.available()];
            //将inputstream写入bytes
            in.read(data);
            //将bytes写入outputstream
            out.write(data);
            in.close();
            out.close();
        } catch (Exception e){
            e.printStackTrace();
            throw new StorageException("文件存储失败");
        }
    }

    /**
     * 必须实现存储文件的方法
     */
    public abstract boolean save(String name,InputStream in) throws StorageException;

    /**
     * 必须实现判断文件是否存在的方法
     */
    public abstract boolean isVaild(String name);

    /**
     * 实现读取文件的方法
     */
    public abstract InputStream get(String name);
}
