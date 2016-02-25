package com.example.dai.lol.myStorage;

import android.content.Context;
import android.os.Environment;

import com.example.dai.lol.myException.StorageException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by dai on 2015/12/17.
 * 将剧照保存到外部存储上
 */
public class movieImageStorage extends myBaseStorage{
    //声明Context才能调用Activity的方法，例如getResource
    protected Context context;

    //存储基础路径
    private File path = Environment.getExternalStoragePublicDirectory(".");

    //存储的文件夹
    private File dirPath = new File(path,"LOL");

    //生成海报文件夹
    private File filePath = new File(dirPath,"PostImg");

    public movieImageStorage(Context context) throws StorageException {
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
        //检查dirpath是否存在
        if(!dirPath.exists()){
            dirPath.mkdirs();
        }
        //检查是否海报文件夹存在
        if(!filePath.exists()){
            filePath.mkdir();
        }
        try{
            File file = new File(filePath,imagename);
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
        File file = new File(filePath,name);
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
        File file = new File(filePath,name);
        try {
            return (InputStream)(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
