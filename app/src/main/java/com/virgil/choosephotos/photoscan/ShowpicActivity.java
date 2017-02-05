package com.virgil.choosephotos.photoscan;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.virgil.choosephotos.R;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ShowpicActivity extends AppCompatActivity {
    private int position;
    private ArrayList<String> mDatas;
    private HackyViewPager hViewPager;
    public static int mPosition = 10;//用于标记被删除图片的位置，给一个任意的默认值10

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showpic);
        getFrontPageData();
        initViews();
    }

    public void getFrontPageData() {
        //点击图片的位置
        position = getIntent().getIntExtra("position", 0);
        //获取传递过来的图片地址
        mDatas = getIntent().getStringArrayListExtra("mImages");
    }

    private void initViews() {
        hViewPager = (HackyViewPager) findViewById(R.id.hViewPager);
        hViewPager.setAdapter(new ImageAdapter());
        //为ViewPager当前page的数字
        hViewPager.setCurrentItem(position);
    }

    public class ImageAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return null == mDatas ? 0 : mDatas.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            //创建显示图片的控件
            PhotoView photoView = new PhotoView(container.getContext());
            //设置背景颜色
            photoView.setBackgroundColor(Color.BLACK);
            //把photoView添加到viewpager中，并设置布局参数
            container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            //加载当前PhtotoView要显示的数据
            String url = mDatas.get(position);

            if (!TextUtils.isEmpty(url)) {
                //使用使用Glide进行加载图片进行加载图片
                Glide.with(ShowpicActivity.this).load(url).into(photoView);
            }

            //图片单击事件的处理
            photoView.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
                @Override
                public void onViewTap(View view, float x, float y) {
                    finish();
                }
            });

            photoView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showDialog();
                    return false;
                }
            });
            return photoView;
        }
    }

    public void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ShowpicActivity.this);
        builder.setTitle("确认对话框").setMessage("是否删除？")
                .setIcon(android.R.drawable.ic_menu_delete);
        builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Log.i("position", position + "");
                mPosition = position;
                Toast.makeText(ShowpicActivity.this, "删除成功", Toast.LENGTH_SHORT)
                        .show();
                finish();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                /*Toast.makeText(ShowpicActivity.this, "取消", Toast.LENGTH_SHORT)
                        .show();*/
            }
        });
        builder.setCancelable(false);
        builder.create().show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
