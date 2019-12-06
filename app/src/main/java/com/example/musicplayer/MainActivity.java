package com.example.musicplayer;

import android.Manifest;

import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import static java.lang.String.format;
import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private MediaPlayer mediaPlayer=new MediaPlayer();
    protected ArrayList<Music> musiclist;
    Handler handler=new Handler();
    private int time;
    SeekBar mediaSeekbar;
    private boolean click;
    private int pos;
    private int num;
    private int ok=1;
    private ArrayList<Map<String, Object>> music_item;




    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button play=(Button)findViewById(R.id.playButton);
        Button pause=(Button)findViewById(R.id.pauseButton);
        Button restart=(Button)findViewById(R.id.restartButton);
        Button pre=(Button)findViewById(R.id.pre);
        Button next=(Button)findViewById(R.id.next);
        final Button loop1=(Button)findViewById(R.id.loop);
        mediaSeekbar=(SeekBar)findViewById(R.id.mediaSeekBar);

        ListView listview = (ListView)findViewById(R.id.list_view);
        loop1.setOnClickListener(this);
        play.setOnClickListener(this);
        pre.setOnClickListener(this);
        next.setOnClickListener(this);
        pause.setOnClickListener(this);
        restart.setOnClickListener(this);
        mediaSeekbar.setOnSeekBarChangeListener(sbLis);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                begin_gain(position);
            }

        });
        //播放方式
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {   //判断歌曲是否播放完
            @Override
            public void onCompletion(MediaPlayer mp) {
                if(loop1.getText().toString().equals("循环")) {
                    mp.reset();
                    begin_gain(pos);
                }
                if(loop1.getText().toString().equals("顺序")) {
                    if (pos == num - 1) {
                        mp.reset();
                        begin_gain(0);
                        mediaPlayer.start();
                    } else {
                        mp.reset();
                        begin_gain(pos + 1);
                    }
                }
                if(loop1.getText().toString().equals("随机")) {
                    mp.reset();
                    Random ra =new Random();
                    begin_gain(ra.nextInt(num-1));
                }
            }
        });

        if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            },1);
        }
        else {
            initMediaPlayer();
        }
    }
    public void begin_gain(int position) {
        TextView  small_title=(TextView)findViewById(R.id.small_title);
        TextView  small_singer=(TextView)findViewById(R.id.small_singer);
        TextView  time_end=(TextView)findViewById(R.id.time_end);
        time=musiclist.get(position).getDuration();
        small_title.setText(musiclist.get(position).getTitle());
        small_singer.setText(musiclist.get(position).getSinger());
        pos=position;
        time_end.setText(change_time(musiclist.get(position).getDuration()));
        mediaSeekbar.setMax(time);
        handler.post(updateseekbar);
        Uri uri = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, String.valueOf(musiclist.get(position).getId()));   //找到路径
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(MainActivity.this, uri);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.start();
    }

    Runnable updateseekbar =new Runnable(){
        @Override
        public void run() {
            // TODO Auto-generated method stub
            handler.postDelayed(updateseekbar, 80);    //更新频率
            mediaSeekbar.setProgress(mediaPlayer.getCurrentPosition());
            TextView time_begin = (TextView) findViewById(R.id.time_begin);
            time_begin.setText(change_time(mediaPlayer.getCurrentPosition()));
        }

    };

    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults) {
        switch (requestCode) {
            case 1:
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                    initMediaPlayer();
                }
                else {
                    Toast.makeText(this,"拒绝权限无法使用该程序！",Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }
    public String change_size(long size)
    {
        long GB = 1024 * 1024 * 1024;
        long  MB = 1024 * 1024;
        long KB = 1024;
        DecimalFormat df = new DecimalFormat("0.00");//格式化小数
        String resultSize = "";
        if (size / GB >= 1) {

            resultSize = df.format(size / (float) GB) + "GB   ";
        } else if (size / MB >= 1) {

            resultSize = df.format(size / (float) MB) + "MB   ";
        } else if (size / KB >= 1) {

            resultSize = df.format(size / (float) KB) + "KB   ";
        } else {
            resultSize = size + "B   ";
        }
        return resultSize;

    }
    public String change_time(int time)
    {
        String result_time;
        int new_time=time/1000;
        int min=new_time/60;
        int sec=new_time%60;
        result_time=format("%02d:%02d",min,sec);
        return result_time;
    }
    private void initMediaPlayer()
    {

        findMusic();
        ListView listview = (ListView)findViewById(R.id.list_view);
        music_item = new ArrayList<Map<String, Object>>();
        for (Iterator iterator = musiclist.iterator(); iterator.hasNext();) {
            Map<String, Object> map = new HashMap<String, Object>();
            Music mp3 = (Music) iterator.next();
            map.put("title", mp3.getTitle());
            map.put("singer", mp3.getSinger());
            map.put("size",change_size(mp3.getSize()));
            map.put("duration", mp3.getDuration());
            map.put("url", mp3.getUrl());
            music_item.add(map);
        }
        SimpleAdapter mSimpleAdapter = new SimpleAdapter(
                this,
                music_item,
                R.layout.music_list_item,
                new String[] {"title","size","singer"},
                new int[] {R.id.title,R.id.size,R.id.singer}
        );
        listview.setAdapter(mSimpleAdapter);

    }

    public void findMusic(){
        musiclist = new ArrayList<Music>();
        num=0;
        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        if(cursor.moveToFirst()){
            while (!cursor.isAfterLast()) {
                num++;
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));  //id号
                String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));  //歌名
                System.out.println(title);
                String singer = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));  //歌手
                System.out.println(singer);
                String url = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));   //路径
                int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));  //事件
                Long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));  //大小
                if (size >1024*800){//大于800K
                    Music music = new Music();
                    music.setId(id);
                    music.setTitle(title);
                    music.setSinger(singer);
                    music.setDuration(duration);
                    music.setSize(size);
                    music.setUrl(url);
                    musiclist.add(music);
                }
                cursor.moveToNext();
            }
        }
    }

    public void onClick(View v)
    {
        Button loop1=(Button)findViewById(R.id.loop);
        switch(v.getId())
        {

            case R.id.playButton:
                if(!mediaPlayer.isPlaying())
                {
                    mediaPlayer.start(); //开始播放
                    handler.post(updateseekbar);
                    click=false;
                }
                break;
            case R.id.pauseButton:
                if(mediaPlayer.isPlaying())
                {
                    mediaPlayer.pause();
                }

                break;
            case R.id.restartButton:
                mediaSeekbar.setProgress(0);
                mediaPlayer.seekTo(0);
                mediaPlayer.start();
                break;

            case R.id.pre:
                if(pos==0)
                {
                    Toast.makeText(MainActivity.this, "该歌曲为第一首歌！", Toast.LENGTH_SHORT).show();
                    break;
                }
                else {
                    begin_gain(pos - 1);
                    break;
                }
            case R.id.next:
                System.out.println(pos);
                if(pos==num-1)
                {
                    begin_gain(0);
                    break;
                }
                else
                {
                    begin_gain(pos+1);
                }
                break;
            case R.id.loop:
                if(ok==1)
                {
                    loop1.setText("循环");
                    ok=ok+1;
                    break;
                }
                if(ok==2)
                {
                    ok=ok+1;
                    System.out.println(ok);
                    loop1.setText("顺序");
                    break;

                }
                if(ok==3)
                {
                    ok=ok-2;
                    System.out.println(ok);
                    loop1.setText("随机");
                    break;

                }
                break;


            default:
                break;
        }

    }
    private SeekBar.OnSeekBarChangeListener sbLis=new SeekBar.OnSeekBarChangeListener(){
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mediaPlayer.seekTo(mediaSeekbar.getProgress());
            //SeekBar确定位置后，跳到指定位置
        }

    };

    protected void onDestroy()
    {
        super.onDestroy();
        if(mediaPlayer!=null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.menu_item_add:
                initMediaPlayer();

                break;
            case R.id.menu_item_delete:

                break;
            default:
        }
        return true;
    }

}
