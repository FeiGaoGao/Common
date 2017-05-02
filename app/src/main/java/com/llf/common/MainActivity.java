package com.llf.common;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDelegate;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.llf.basemodel.base.BaseActivity;
import com.llf.basemodel.base.BaseFragment;
import com.llf.basemodel.base.BaseFragmentAdapter;
import com.llf.basemodel.utils.SettingUtil;
import com.llf.common.ui.girl.GirlFragment;
import com.llf.common.ui.mine.MineFragment;
import com.llf.common.ui.news.NewsFragment;
import com.llf.common.ui.video.VideoFragment;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends BaseActivity implements ViewPager.OnPageChangeListener, EasyPermissions.PermissionCallbacks {
    @Bind(R.id.news)
    Button mNews;
    @Bind(R.id.video)
    Button mVideo;
    @Bind(R.id.girl)
    Button mGirl;
    @Bind(R.id.mine)
    Button mMine;
    @Bind(R.id.viewPager)
    ViewPager mViewPager;

    private String[] mTitles;
    private BaseFragment[] fragments;
    int currentTabPosition = 0;
    public static final String CURRENT_TAB_POSITION = "HOME_CURRENT_TAB_POSITION";
    private String[] params;
    public static final int PERMITTION = 100;

    static {
        //vector支持selector
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        /**
         * 6.0系统动态权限申请需要
         */
        params = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE};
        if (EasyPermissions.hasPermissions(MainActivity.this, params)) {
            initFragment();
        } else {
            EasyPermissions.requestPermissions(MainActivity.this, "应用需要权限才能安全运行", PERMITTION, params);
        }
    }

    private void initFragment() {
        mTitles = getResources().getStringArray(R.array.main_titles);
        fragments = new BaseFragment[mTitles.length];
        fragments[0] = NewsFragment.getInstance();
        fragments[1] = VideoFragment.getInstance();
        fragments[2] = GirlFragment.getInstance();
        fragments[3] = MineFragment.getInstance();
        BaseFragmentAdapter mAdapter = new BaseFragmentAdapter(getSupportFragmentManager(), fragments);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setPageMargin(SettingUtil.dip2px(this, 16));
        mViewPager.addOnPageChangeListener(this);
        mViewPager.setCurrentItem(currentTabPosition);
        mNews.setSelected(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("退出");
            builder.setMessage("你确定要离开我吗?");
            builder.setNegativeButton("依依不舍", null);
            builder.setPositiveButton("狠心离开", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    moveTaskToBack(true);
                }
            });
            builder.show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //奔溃前保存位置
        outState.putInt(CURRENT_TAB_POSITION, currentTabPosition);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        currentTabPosition = savedInstanceState.getInt(CURRENT_TAB_POSITION);
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //分发到fragment的onActivityResult，用于解决qq分享接收不到回调
        if(requestCode!=PERMITTION){
            BaseFragment fragment = fragments[3];
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        initFragment();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        switch (requestCode) {
            case PERMITTION:
                //引导用户跳转到设置界面
                new AppSettingsDialog.Builder(MainActivity.this, "希望您通过权限")
                        .setTitle("权限设置")
                        .setPositiveButton("设置")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        })
                        .setRequestCode(PERMITTION)
                        .build()
                        .show();
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //Empty
    }

    @Override
    public void onPageSelected(int position) {
        resetTab();
        switch (position) {
            case 0:
                mNews.setSelected(true);
                break;
            case 1:
                mVideo.setSelected(true);
                break;
            case 2:
                mGirl.setSelected(true);
                break;
            case 3:
                mMine.setSelected(true);
                break;
            default:
                //其他
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        //Empty
    }

    @OnClick({R.id.news, R.id.video, R.id.girl, R.id.mine})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.news:
                mViewPager.setCurrentItem(0, false);
                break;
            case R.id.video:
                mViewPager.setCurrentItem(1, false);
                break;
            case R.id.girl:
                mViewPager.setCurrentItem(2, false);
                break;
            case R.id.mine:
                mViewPager.setCurrentItem(3, false);
                break;
            default:
                break;
        }
    }

    private void resetTab() {
        mNews.setSelected(false);
        mVideo.setSelected(false);
        mGirl.setSelected(false);
        mMine.setSelected(false);
    }
}
