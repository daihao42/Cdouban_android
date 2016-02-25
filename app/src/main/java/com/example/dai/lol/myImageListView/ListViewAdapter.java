package com.example.dai.lol.myImageListView;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.dai.lol.MainActivity;
import com.example.dai.lol.R;
import com.example.dai.lol.myComponent.AsyncImageLoader;
import com.example.dai.lol.myException.StorageException;

import java.util.List;

/**
 * Created by dai on 2015/12/16.
 * 自定义Adapter，继承自ArrayAdapter
 */
public class ListViewAdapter extends ArrayAdapter<ListItem> {

    private ListView listView;
    //异步图片加载对象
    private AsyncImageLoader asyncImageLoader;
    //activity对象
    private Activity activity;

    public ListViewAdapter(Activity activity, List<ListItem> imageAndTexts,ListView listView) {
        super(activity,0,imageAndTexts);
        //初始化数据
        this.listView = listView;
        this.asyncImageLoader = new AsyncImageLoader(activity);
        this.activity = activity;
    }

    public View getView(int position,View convertView,ViewGroup parent){
        Activity activity = (Activity)getContext();

        //定义GridView的Item布局，读取griditem.xml布局

        View rowView = convertView;
        ViewCache viewCache;
        if(rowView == null){
            //读取griditem.xml
            LayoutInflater inflater = activity.getLayoutInflater();
            rowView = inflater.inflate(R.layout.listviewitem,null);
            //通过当前的布局视图，初始化缓存视图
            viewCache = new ViewCache(rowView);
            //设置当前视图标签
            rowView.setTag(viewCache);
        } else{
            viewCache = (ViewCache)rowView.getTag();
        }
        //得到需要显示的对象
        final ListItem imageAndText = getItem(position);

        //读取对象的相应内容值
        final String imageurl = imageAndText.getImageurl();
        final String imagename = imageAndText.getImagename();
        ImageView imageView = viewCache.getImageView();
        imageView.setTag(imageurl);

        Drawable cachedImage=null;
        //读取缓存图片
        try {
            cachedImage = asyncImageLoader.loadDrawble(imagename, imageurl,
                    new AsyncImageLoader.ImageCallback() {
                        @Override
                        public void imageLoaded(Drawable imageDrawable, String imageUrl) {
                            //得到对应的ImageView对象
                            ImageView imageViewByTag = (ImageView) listView.findViewWithTag(imageUrl);
                            //设置图片控件显示的图像
                            if (imageViewByTag != null) {
                                imageViewByTag.setImageDrawable(imageDrawable);
                            }
                        }
                    });
        }catch(StorageException e){
            e.printStackTrace();
        }
        //如果图片缓存为空，则显示默认图片
        if(cachedImage == null){
            imageView.setImageResource(R.drawable.ic_lanucher);
        } else{
            imageView.setImageDrawable(cachedImage);
        }
        //显示简介文字
        TextView textViewTitle = viewCache.getTitleTextView();
        textViewTitle.setText(imageAndText.getTitle());

        TextView textViewDirector = viewCache.getDirectorTextView();
        textViewDirector.setText("导演："+imageAndText.getDirector());

        TextView textViewActor = viewCache.getActorTextView();
        textViewActor.setText("主演："+imageAndText.getActors());

        TextView textViewTypes = viewCache.getTypesTextView();
        textViewTypes.setText("类型："+imageAndText.getTypes());

        TextView textViewOntime = viewCache.getOntimeTextView();
        textViewOntime.setText("上映时间："+imageAndText.getOntime());

        //ListItem的imagename其实就是影片ID，为了方便onclicklistener调用所以不可见的传入itemView里
        TextView textViewID = viewCache.getIDTextView();
        textViewID.setText(imagename);

        return rowView;
    }

    //将drawable写入外存设备
    //我决定写在url获取图片的流后（2015-12-21）
//    public void saveDrawable(String name,Drawable drawable){
//        Bitmap bitmap = Bitmap
//                .createBitmap(
//                        drawable.getIntrinsicWidth(),
//                        drawable.getIntrinsicHeight(),
//                        drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
//                                : Bitmap.Config.RGB_565);
//        Canvas canvas = new Canvas(bitmap);
//        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
//                drawable.getIntrinsicHeight());
//        drawable.draw(canvas);
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//        InputStream in = new ByteArrayInputStream(baos.toByteArray());
//        try{
//            new movieImageStorage(this.activity).save(name, in);
//        } catch (StorageException e){
//            e.printStackTrace();
//        }
//    }
}
