package com.virgil.choosephotos;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.virgil.choosephotos.photoscan.ShowpicActivity;
import com.virgil.choosephotos.utils.CompressPhotoUtils;

import java.util.ArrayList;
import java.util.List;

import me.nereo.multi_image_selector.MultiImageSelector;

public class MainActivity extends AppCompatActivity {
    private Context mContext = this;

    private RecyclerView mRV;
    private Intent mIntent = new Intent();
    private static ShowImageAdapter adapter;
    private Button mBtn_release;//发布

    private static int mPosition = 10;//标记被删除的图片,给一个默认值为10
    private static final int REQUEST_IMAGE = 2;
    protected static final int REQUEST_STORAGE_READ_ACCESS_PERMISSION = 101;
    protected static final int REQUEST_STORAGE_WRITE_ACCESS_PERMISSION = 102;
    private static ArrayList<String> mSelectPath = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mRV = (RecyclerView) findViewById(R.id.main_rv_showPhoto);
        mBtn_release = (Button) findViewById(R.id.btn_release);

        mBtn_release.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CompressPhotoUtils().CompressPhoto(MainActivity.this, mSelectPath, new CompressPhotoUtils.CompressCallBack() {

                    @Override
                    public void success(List<String> list) {
                        //upload(list);执行上传的方法
                        Toast.makeText(mContext, "上传成功", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void pickImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN // Permission was added in API Level 16
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
                    getString(R.string.mis_permission_rationale),
                    REQUEST_STORAGE_READ_ACCESS_PERMISSION);
        } else {
            MultiImageSelector selector = MultiImageSelector.create(MainActivity.this);
            selector.showCamera(true);
            selector.count(9);
            selector.multi();
            selector.origin(mSelectPath);
            selector.start(MainActivity.this, REQUEST_IMAGE);
        }
    }

    private void requestPermission(final String permission, String rationale, final int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.mis_permission_dialog_title)
                    .setMessage(rationale)
                    .setPositiveButton(R.string.mis_permission_dialog_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
                        }
                    })
                    .setNegativeButton(R.string.mis_permission_dialog_cancel, null)
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_STORAGE_READ_ACCESS_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImage();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                mSelectPath = data.getStringArrayListExtra(MultiImageSelector.EXTRA_RESULT);
                StringBuilder sb = new StringBuilder();
                for (String p : mSelectPath) {
                    sb.append(p);
                    sb.append("\n");
                }
                Log.i("photoAddress", sb.toString());
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ShowpicActivity.mPosition == 10) {
            mRV.setLayoutManager(new GridLayoutManager(mContext, 3));
            mRV.setHasFixedSize(true);
            adapter = new ShowImageAdapter(mContext, mSelectPath, mRV);
            mRV.setAdapter(adapter);
            adapter.setOnItemClickedListener(new ShowImageAdapter.OnItemClickedListener() {
                @Override
                public void onItemClick(int position) {
                    //Log.i("position", position + "");
                    if (position < mSelectPath.size()) {
                        mIntent.putExtra("position", position);
                        mIntent.putStringArrayListExtra("mImages", mSelectPath);
                        mIntent.setClass(mContext, ShowpicActivity.class);
                        mContext.startActivity(mIntent);
                    } else {
                        pickImage();
                    }
                }

                @Override
                public boolean onItemLongClick(int position) {
                    mPosition = position;
                    showDialog();
                    return false;
                }
            });
        } else {
            mSelectPath.remove(ShowpicActivity.mPosition);
            adapter.notifyDataSetChanged();
            ShowpicActivity.mPosition = 10;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSelectPath.clear();
        mPosition = 10;
    }

    public void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("确认对话框").setMessage("是否删除？")
                .setIcon(android.R.drawable.ic_menu_delete);
        builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Log.i("position", position + "");
                mSelectPath.remove(mPosition);
                adapter.notifyDataSetChanged();
                Toast.makeText(MainActivity.this, "删除成功", Toast.LENGTH_SHORT)
                        .show();
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
}
