package com.example.dai.lol;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.example.dai.lol.myComponent.AsyncImageLoader;
import com.example.dai.lol.myComponent.MyUrlHttp;
import com.example.dai.lol.myComponent.myProgressBar;
import com.example.dai.lol.myException.StorageException;
import com.example.dai.lol.somemovie.allMovie;


/**
 * Created by dai on 2015/12/24.
 * 电影展示
 */
public class MovieActivity extends Activity{
    //电影ID，从bundle获取的
    private String movie_id;

    //封装urlconnect
    private MyUrlHttp myurlhttp;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie);
        Bundle bundle = new Bundle();
        bundle = this.getIntent().getExtras();
        //初始化MyUrlHttp
        myurlhttp = new MyUrlHttp();
        movie_id = bundle.getString("ID");
        //获取影片信息
        new myAsyncTaskOnShowMovie().execute(movie_id);
        //获取影片海报
        loadPost();
    }

    /**
     * 利用ListView的异步获取图片
     */
    private void loadPost(){
        Drawable cachedImage=null;
        AsyncImageLoader asyncImageLoader = new AsyncImageLoader(this);
        final ImageView post = (ImageView)findViewById(R.id.movie_post);
        //读取缓存图片
        try {
            cachedImage = asyncImageLoader.loadDrawble(movie_id, "",
                    new AsyncImageLoader.ImageCallback() {
                        @Override
                        public void imageLoaded(Drawable imageDrawable, String imageUrl) {
                            //设置图片控件显示的图像
                                post.setImageDrawable(imageDrawable);
                        }
                    });
        }catch(StorageException e){
            e.printStackTrace();
        }
        //如果图片缓存为空，则显示默认图片
        if(cachedImage == null){
            post.setImageResource(R.drawable.ic_lanucher);
        } else{
            post.setImageDrawable(cachedImage);
        }
    }

    /**
     * AsyncTask解析JSON获取电影信息
     */
    class myAsyncTaskOnShowMovie extends AsyncTask<String,Integer,allMovie> {

        private ProgressBar pb;

        @Override
        protected allMovie doInBackground(String... params)
        {
            String movie_id = params[0];
            String resp;
            JSONObject obj;
            String url = "Movie?id="+movie_id;
            try {
                String res = myurlhttp.GetFromNet(url);
                obj = JSON.parseObject(res);
                resp = obj.getString("response");
            } catch (Exception e){
                return null;
            }
            //如果response为empty，表明数据结束了
            if(resp.equals("empty")){
                return null;
            } else{
                //不为空，处理数据
                allMovie m = JSON.parseObject(obj.getString("data"), new TypeReference<allMovie>(){});
                return m;
            }
        }

        @Override
        protected void onPreExecute(){
            pb = new myProgressBar(MovieActivity.this).progressBar;
            pb.setVisibility(View.VISIBLE);
            //progressBar.;
        }

        @Override
        protected void onPostExecute(allMovie result){
            //如果网络为空，退出处理
            if(result == null){
                //取消processbar
                pb.setVisibility(View.GONE);
                return;
            }
            TextView title = (TextView)findViewById(R.id.movie_title);
            title.setText(result.getTitle());
            TextView director = (TextView)findViewById(R.id.movie_director);
            director.setText(result.getDirector());
            TextView actor = (TextView)findViewById(R.id.movie_actor);
            actor.setText(result.getActor());
            TextView writer = (TextView)findViewById(R.id.movie_writer);
            writer.setText(result.getWriter());
            TextView another = (TextView)findViewById(R.id.movie_another);
            another.setText(result.getAnother());
            TextView country = (TextView)findViewById(R.id.movie_country);
            country.setText(result.getCountry());
            TextView summary = (TextView)findViewById(R.id.movie_summary);
            summary.setText(result.getSummary());
            TextView average = (TextView)findViewById(R.id.movie_average);
            average.setText("评分："+result.getAverage());

            //ratingbar
            RatingBar ratingbar = (RatingBar)findViewById(R.id.ratingBar);
            ratingbar.setRating(result.getAverage());

            //取消processbar
            pb.setVisibility(View.GONE);
        }
    }
}
