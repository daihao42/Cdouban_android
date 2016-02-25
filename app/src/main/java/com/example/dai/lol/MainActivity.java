package com.example.dai.lol;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Movie;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.storage.StorageManager;
import android.renderscript.ScriptGroup;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.dai.lol.myComponent.MyApplication;
import com.example.dai.lol.myComponent.MyUrlHttp;
import com.example.dai.lol.myComponent.myProgressBar;
import com.example.dai.lol.myException.StorageException;
import com.example.dai.lol.myImageListView.ListItem;
import com.example.dai.lol.myImageListView.ListViewAdapter;
import com.example.dai.lol.mySQLlite.MovieSQLlite;
import com.example.dai.lol.myStorage.headImageStorage;
import com.example.dai.lol.somemovie.shortMovie;
import com.example.dai.lol.someuser.CurrentUser;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
        ,SwipeRefreshLayout.OnRefreshListener{

    //声明变量以获取全局Application
    protected MyApplication app;

    //网络封装类
    private MyUrlHttp myurlhttp;

    //下拉刷新控件
    private SwipeRefreshLayout refresh_layout = null;

    //ListView的数据
    private List<ListItem> list;

    //添加listview
    //得到GridView控件
    private ListView listView;

    //ListViewAdapter
    private ListViewAdapter listViewAdapter;

    //声明ListView的footer
    private View footer;

    //下拉刷新(true)点击加载更多(false)
    private boolean Update_Or_Getmore;

    //当前刷新到第几页
    private int movie_page;

    //声明Handler，用来进程间通信
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            if(msg.obj != null) {
                switch ((String) msg.obj) {
                    case "offline":
                        createDialog("离线", "未联网，是否退出？(离线测试账号123)");
                        break;
                    default:
                        Toast.makeText(MainActivity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(MainActivity.this, "Server Has Gone!", Toast.LENGTH_SHORT).show();
            }
            super.handleMessage(msg);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //获取全局Application
        this.app = (MyApplication) getApplication();

        //初始化MyUrlHttp
        myurlhttp = new MyUrlHttp();

        //测试网络
        //PingNet();

        //检查SharedPreferences
        checkSharedPerences();

        //初始化ListView组件
        ListViewInit();
    }


    public void ListViewInit(){
        //获取下拉刷新控件
        refresh_layout = (SwipeRefreshLayout)findViewById(R.id.refresh_layout);
        //设置跑动的颜色值
        refresh_layout.setColorSchemeResources(R.color.colorGreen, R.color.colorGray, R.color.colorSkyblue, R.color.colorPrimary);
        //监听下拉刷新时间
        refresh_layout.setOnRefreshListener(MainActivity.this);
        //添加listview
        //得到GridView控件
        listView = (ListView)findViewById(R.id.Lv);
        //获取footer
        LayoutInflater inflater = getLayoutInflater();
        footer = inflater.inflate(R.layout.footerlist,null);
        //定义数据源(不需要，在pre，执行异步任务前初始化)
        //list = new ArrayList<ListItem>();
        //设置为下拉刷新模式
        Update_Or_Getmore = true;
        //加载初始数据
        onRefresh();

        //设置Item监听器
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView movieid = (TextView)view.findViewById(R.id.movie_id);
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                //将ID传入电影展示页面
                bundle.putString("ID",movieid.getText().toString());
                intent.setClass(MainActivity.this, MovieActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        //初始的加载更多的页数为1
        movie_page = 1;
        footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //设置为加载更多模式
                Update_Or_Getmore = false;
                //先将页数增加1
                movie_page++;
                new myAsyncTaskOnListView().execute(movie_page);
            }
        });
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        //findViews()将登录图片事件绑定
        findViews();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {
            Log.v("Test", "click item");
            jumpToLogin();
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * 为侧边栏header的图片绑定登录事件
     * 点击进入登陆界面(2015-11-30)
     * 由于onCreate并未创建抽屉的menu，故无法find LoginBtn的ID，
     * 需要在抽屉创建时(onCreateOptionsMenu)才能绑定
     */
    private void findViews() {
        ImageButton login_img = (ImageButton) findViewById(R.id.LoginBtn);
        //按登录状态设置login的ImageButton
        boolean is_login = app.getLogin();
        if (is_login) {
            String image = app.getCurrentUser().getImage();
            try {
                headImageStorage HS = new headImageStorage(MainActivity.this);
                //如果本地存在头像
                if(HS.isVaild(image)){
                    //读取本地头像并设置在login_img上
                    login_img.setImageDrawable(Drawable.createFromStream(HS.get(image),"headimage"));
                }
                //本地不存在头像
                else{
                    String imageurl = app.getCurrentUser().getImageUrl();
                    //使用AsyncTask加载用户图片
                    new myAsyncTaskLoadImage((ImageView)login_img,imageurl).execute();
                }
            } catch (StorageException e) {
                String imageurl = app.getCurrentUser().getImageUrl();
                //使用AsyncTask加载用户图片
                new myAsyncTaskLoadImage((ImageView)login_img,imageurl).execute();
                //显示出错信息
                Toast.makeText(MainActivity.this, (String)e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            login_img.setImageDrawable(getResources().getDrawable(R.drawable.nologin));
            //login_img.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }
        login_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("Test", "LoginBtn Clicked");
                //如果当前已登陆，跳转userCenter
                if (app.getLogin()) {
                    jumpToUserCenter();
                }
                //未登录跳转登陆
                else {
                    jumpToLogin();
                }
            }
        });

//        Button test_btn = (Button)findViewById(R.id.test_btn);
//        test_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                GetFromNet("getmovie");
//            }
//        });
        TextView Name = (TextView)findViewById(R.id.textViewName);
        CurrentUser c = app.getCurrentUser();
        Name.setText(c.getName());
    }

    /**
     * 跳转login界面
     */
    private void jumpToLogin() {
        //进入登陆界面会清除掉当前Activity
        // 以保证返回时直接进入主界面而不是进入drawer
        MainActivity.this.finish();
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    /**
     * 跳转userCenter用户中心
     */
    private void jumpToUserCenter() {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, UserCenterActivity.class);
        startActivity(intent);
    }

    /**
     * 创建dialog对话框
     * param string title 对话框标题
     * param string content 对话框内容
     */
    protected void createDialog(String title, String content) {
        //初始化AlertDialog构造器对象
        AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);
        //设置dialog信息
        b.setMessage(content);
        //设置title
        b.setTitle(title);
        //添加确认取消按钮
        b.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //确认，对话框消失，执行函数
                dialog.dismiss();
                MainActivity.this.finish();
            }
        });
        b.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //取消则对话框消失
                dialog.dismiss();
            }
        });
        b.create().show();
    }

    /**
     * 监听键盘返回键事件
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            createDialog("退出", "确定退出？");
        }
        return true;
    }

    /**
     * 测试网络是否正常连接
     * 离线时弹出消息
     */
    private void PingNet() {
        new Thread() {
            @Override
            public void run() {
                if (!myurlhttp.pingNet()) {
                    Message msg = new Message();
                    msg.obj = "offline";
                    mHandler.sendMessage(msg);
                }
                super.run();
            }
        }.start();
    }

    /**
     * get方式从服务端拉取数据
     * String 获取的url
     */
    private void GetFromNet(final String url) {
        new Thread() {
            @Override
            public void run(){
                Message msg = new Message();
                msg.obj = myurlhttp.GetFromNet(url);
                mHandler.sendMessage(msg);
                super.run();
            }
        }.start();
    }

    /**
     * 下拉刷新的回调函数
     */
    @Override
    public void onRefresh() {
        //设置为下拉刷新模式
        Update_Or_Getmore = true;
        //获取电影信息并添加到listadapter
        new myAsyncTaskOnListView().execute(1);
    }


    /**
     * AsyncTask下载用户头像
     */
    class myAsyncTaskLoadImage extends AsyncTask <Void,Void,Drawable>{
        //用来存储加载成功后展示图片的对象
        private ImageView m;
        //记录网络图片地址
        private String imageurl;
        //构造函数，引入image对象和图片地址
        public myAsyncTaskLoadImage(ImageView i,String t){
            m = i;
            imageurl = t;
        }
        @Override
        protected Drawable doInBackground(Void... params) {
            return loadImages(imageurl);
        }

        @Override
        protected void onPostExecute(Drawable result){
            super.onPostExecute(result);
            if(result != null){
                m.setImageDrawable(result);
                m.setScaleType(ImageView.ScaleType.FIT_CENTER);
            }
            else{
                m.setImageDrawable(getResources().getDrawable(R.drawable.logined));
            }
        }

        /**
         * 下载网络图片
         * @param url
         * @return
         */
        public Drawable loadImages(String url){
            try{
                InputStream in = (InputStream)(new URL(url)).openStream();
                //复制InputStream，因为我需要读取两次流，第一次写入本地，第二次前台显示
                //第一次读完后流就到了EOF上，或者close了，所以必须复制一份InputStream
                ByteArrayOutputStream baos = copyInputStream(in);
                //从ByteArrayOutputStream转换成InputStream
                InputStream in1 = new ByteArrayInputStream(baos.toByteArray());
                InputStream in2 = new ByteArrayInputStream(baos.toByteArray());
                new headImageStorage(MainActivity.this).save(app.getCurrentUser().getImage(),in1);
                return Drawable.createFromStream(in2,"test");
            } catch (IOException e){
                return null;
            } catch (StorageException e) {
                Toast.makeText(MainActivity.this, (String)e.getMessage(), Toast.LENGTH_SHORT).show();
                return null;
            }
        }
    }

    /**
     * 程序退出时记录当前用户信息到SharedPreferences
     */
    @Override
    protected void onStop(){
        SharedPreferences savecurrentuser = getSharedPreferences("currentUser", Context.MODE_PRIVATE);
        CurrentUser c = app.getCurrentUser();
        savecurrentuser.edit()
                .putString("username", c.getName())
                .putString("email",c.getEmail())
                .putString("password",c.getPassword())
                .putString("city",c.getCity())
                .putString("image",c.getRawImage())
                //出错了，不是geiPassword()，记得改回来
                .putString("about", c.getPassword())
                .commit();
        super.onStop();
    }

    /**
     * 检查SharedPreferences是否存在记录的用户
     * 存在则将用户保存到Application并置为登录
     */
    private void checkSharedPerences(){
        SharedPreferences savecurrentuser = getSharedPreferences("currentUser", Context.MODE_PRIVATE);
        //检查密码是否存在，存在则读入所有信息
        if(savecurrentuser.contains("password")){
            Map<String,String> m = (Map<String,String>)savecurrentuser.getAll();
            CurrentUser c = new CurrentUser();
            c.setName(m.get("username"));
            c.setEmail(m.get("email"));
            c.setPassword(m.get("password"));
            c.setCity(m.get("city"));
            c.setImage(m.get("image"));
            c.setAbout(m.get("about"));
            app.setCurrentUser(c);
            app.setLogin(true);
        }
    }

    /**
     * 复制InputStream为ByteArrayOutputStream，因为我需要读取两次流，第一次写入本地，第二次前台显示
     * 第一次读完后流就到了EOF上，或者close了，所以必须复制一份InputStream
      * @param in
     * @return
     */
    public ByteArrayOutputStream copyInputStream(InputStream in) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = in.read(buffer)) > -1 ) {
            baos.write(buffer, 0, len);
        }
        baos.flush();
        return baos;
    }

    /**
     * 获取电影信息，并更新首页
     */
    class myAsyncTaskOnListView extends AsyncTask<Integer,Integer,List<shortMovie>>{

        private ProgressBar pb;
        //加载更多文本框
        private TextView getmore_text = (TextView)findViewById(R.id.getmore_text);

        @Override
        protected List<shortMovie> doInBackground(Integer... params)
        {
            Integer page = params[0];
            String resp;
            JSONObject obj;
            String url = "getMovie?page="+page.toString();
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
                JSONArray data = obj.getJSONArray("data");
                List<shortMovie> li = new ArrayList<shortMovie>();
                for(int i = 0;i < data.size();i++){
                    JSONObject o = (JSONObject)data.get(i);
                    shortMovie m = (shortMovie)JSONObject.toJavaObject(o,shortMovie.class);
                    if(m!=null){
                        li.add(m);
                    }
                }
                //貌似List<Map>可以强转，但是List<shortMovie>不可以强转
                //List<shortMovie> movies = (List<shortMovie>) JSONArray.parseObject(data.toJSONString());
                //List<Map<String,String>> movies = (List<Map<String,String>>) JSON.parse(data.toJSONString());
                //String m = li.get(1).getTitle();
                return li;
            }
        }

        @Override
        protected void onPreExecute(){
            //如果是加载更多，则使用processbar
            if(!Update_Or_Getmore) {
                pb = new myProgressBar(MainActivity.this).progressBar;
                pb.setVisibility(View.VISIBLE);
                //progressBar.;
                //修改加载更多为正在加载。。。
                getmore_text.setText("正在加载。。。");
            }
            //如果是下拉刷新模式，清除list
            else{
                list = new ArrayList<ListItem>();
            }

        }

        @Override
        protected void onPostExecute(List<shortMovie> result){
            //新建数据库实例
            MovieSQLlite db = new MovieSQLlite(MainActivity.this);
            //去掉listView的footer
            listView.removeFooterView(footer);
            //如果是下拉刷新模式
            if(Update_Or_Getmore) {
                //下拉刷新先从网络获取，没网再从本地数据库获取
                //如果result为空，从本地数据库获取数据
                if(result == null){
                    result = db.getNews();
                }
                //如果result还为空，退出处理
                if(result == null){
                    //加上footer
                    listView.addFooterView(footer);
                    //关掉加载条
                    refresh_layout.setRefreshing(false);
                    return;
                }
                //添加数据
                //list.add(new ListItem("http://cdn.duitang.com/uploads/blog/201311/30/20131130215423_LEP5C.thumb.600_0.jpeg","oo","<span style='color:red;'>不显示</span>\n换行测试"));
                for(int i = 0;i < result.size();i++){
                    shortMovie m =result.get(i);
                    db.insertDB(m);
                    list.add(new ListItem(m));
                }
                //设置ListView的数据源
                listViewAdapter = new ListViewAdapter(MainActivity.this, list, listView);
                listView.addFooterView(footer);
                listView.setAdapter(listViewAdapter);
                //关掉加载条
                refresh_layout.setRefreshing(false);
            }
            //加载更多只从网上获取，没有就退出
            else{
                //如果result为空，退出处理
                if(result == null){
                    //加上footer
                    listView.addFooterView(footer);
                    //取消processbar
                    pb.setVisibility(View.GONE);
                    //修改正在加载。。。为加载更多
                    getmore_text.setText("加载更多(联网失败)");
                    //修正page位置，如果断网又突然联网，前几次的刷新会导致获取的page累加，需要修正为1
                    movie_page = 1;
                    return;
                }
                //添加数据
                //list.add(new ListItem("http://cdn.duitang.com/uploads/blog/201311/30/20131130215423_LEP5C.thumb.600_0.jpeg","oo","<span style='color:red;'>不显示</span>\n换行测试"));
                for(int i = 0;i < result.size();i++){
                    shortMovie m =result.get(i);
                    db.insertDB(m);
                    list.add(new ListItem(m));
                }
                listViewAdapter.notifyDataSetChanged();
                listView.addFooterView(footer);
                //取消processbar
                pb.setVisibility(View.GONE);
                //修改正在加载。。。为加载更多
                getmore_text.setText("加载更多");
            }
        }
    }

}




