package com.example.dai.lol.myComponent;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.dai.lol.myException.StorageException;
import com.example.dai.lol.myStorage.movieImageStorage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

/**
 * Created by dai on 2015/12/13.
 * 定义异步加载电影图片类
 */
public class AsyncImageLoader {
    //定义异步加载图片的缓存哈稀图
    private HashMap<String,SoftReference<Drawable>> imageCache;

    //定义activity以便使用Activity方法
    private Activity activity;

    //定义外部存储类
    private movieImageStorage MS;

    public AsyncImageLoader(Activity activity){
        imageCache = new HashMap<String,SoftReference<Drawable>>();
        this.activity = activity;
    }

    public Drawable loadDrawble(final String imagename ,final String imageUrl,final ImageCallback imageCallback) throws StorageException {
        //调用外部存储类
        MS = new movieImageStorage(activity);
        //如果图片缓存中有此图片，则直接获取并返回
        if(imageCache.containsKey(imageUrl)){
            SoftReference<Drawable> softReference = imageCache.get(imageUrl);
            Drawable drawable = softReference.get();
            if(drawable != null){
                return drawable;
            }
        }
        if(MS.isVaild(imagename)){
            //如果本地存在图片
            InputStream in = MS.get(imagename);
            Drawable d = Drawable.createFromStream(in, "src");
            Log.d("IOinput","sd");
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //将读取的地址加入到缓存列表
            imageCache.put(imageUrl,new SoftReference<Drawable>(d));
            return d;
        }
        final Handler handler = new Handler(){
            public void handleMessage(Message message){
                imageCallback.imageLoaded((Drawable)message.obj,imageUrl);
            }
        };
        //定义一条新线程来读取网络图片
        new Thread(){
            @Override
            public void run(){
                //读取网络图片地址
                Drawable drawable = loadImageFromUrl(imagename,imageUrl);
                //将读取的地址加入到缓存列表
                imageCache.put(imageUrl,new SoftReference<Drawable>(drawable));
                //定义message，将内容设置为要加载的图片对象
                Message message = handler.obtainMessage(0,drawable);
                //发送图片
                handler.sendMessage(message);
            }
        }.start();
        return null;
    }

    //从图片地址读取图片
    public Drawable loadImageFromUrl(String name,String url){
        //定义url对象，及inputstream对象
        URL m;
        InputStream i = null;
        try{
            //通过图片的url得片的InputStream对象
            //图片名中含有空格时会无法获取资源，需要对空格进行编码
            url = url.replace(" ","%20");
            m = new URL(url);
            i = (InputStream) m.getContent();
        } catch (MalformedURLException e1){
            e1.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
        //复制InputStream，因为我需要读取两次流，第一次写入本地，第二次前台显示
        //第一次读完后流就到了EOF上，或者close了，所以必须复制一份InputStream
        try {
            ByteArrayOutputStream baos = copyInputStream(i);
            //从ByteArrayOutputStream转换成InputStream
            InputStream in1 = new ByteArrayInputStream(baos.toByteArray());
            InputStream in2 = new ByteArrayInputStream(baos.toByteArray());
            try {
                //将流图片文件存在本地
                new movieImageStorage(activity).save(name,in1);
            }catch (StorageException e){
                e.printStackTrace();
            }
            //通过InputStream对象得到图片的Drawable对象
            Drawable d = Drawable.createFromStream(in2, "src");
            return d;
        }catch (IOException e){
            Drawable d = Drawable.createFromStream(i, "src");
            return d;
        }
    }

    //图片获取后的接口
    public interface ImageCallback{
        public void imageLoaded(Drawable imageDrawable,String imageUrl);
    }

    /**
     * 复制InputStream为ByteArrayOutputStream，因为我需要读取两次流，第一次写入本地，第二次前台显示
     * 第一次读完后流就到了EOF上，或者close了，所以必须复制一份InputStream
     * @param in
     * @return
     */
    public ByteArrayOutputStream copyInputStream(InputStream in) throws IOException {
        Log.d("IOinput","call");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = in.read(buffer)) > -1 ) {
            baos.write(buffer, 0, len);
        }
        baos.flush();
        return baos;
    }
}
