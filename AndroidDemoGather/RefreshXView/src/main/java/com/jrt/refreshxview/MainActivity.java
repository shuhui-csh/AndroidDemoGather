package com.jrt.refreshxview;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.jrt.refreshxview.contact.AutoLoadState;
import com.jrt.refreshxview.customview.RefreshView.SwipeRefreshListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private SwipeRefreshListView mSRListview;
    private MainActivity mContext;
    private ArrayList<String> mList=null;
    private ArrayAdapter<String> mAdapter;
    private SliderLayout mSlider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        initView();
        //下拉刷新
        mSRListview.setOnPullRefreshListener(new SwipeRefreshListView.OnPullRefreshListener() {
            @Override
            public void onRefresh() {
                notifyDataSetChanged(AutoLoadState.REFRESH);
            }
        });
        //上拉加载
        mSRListview.setOnAutoLoadingListener(new SwipeRefreshListView.OnAutoLoadListener() {
            @Override
            public void onAutoLoad() {
                notifyDataSetChanged(AutoLoadState.LOAD);
            }
        });
    }

    /**
     * 刷新列表
     * @param refresh
     */
    private void notifyDataSetChanged(AutoLoadState refresh) {
        initData(refresh);
        mAdapter.notifyDataSetChanged();
        mSRListview.doComplete();
    }

    private void initView() {
        mContext =this;
        mList=new ArrayList<>();
        mSRListview = (SwipeRefreshListView) findViewById(R.id.sr_Listview);
        initHeaderView();//初始化头布局
        mSRListview.setRefreshing(true);//刚进来显示加载进度条
        mAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, initData(AutoLoadState.NORMAL));
        mSRListview.setAdapter(mAdapter);
        mSRListview.doComplete();//完成初始化 隐藏进度条
    }


    /**
     * 加载对应数据 正式开发中这里应该是网络数据请求的操作requesNetWorkData(AutoLoadState loadState)
     * @param loadState 判断列表的操作状态
     * @return
     */
    private List<String> initData(AutoLoadState loadState) {
        String itemTip="";
        switch (loadState){
            case NORMAL:
                itemTip="初始化数据";
                mList.clear();
                break;
            case REFRESH:
                itemTip="下拉刷新数据";
                mList.clear();
                break;
            case LOAD:
                itemTip="上拉加载更多数据";
                break;
        }
        for (int i = 1; i <10 ; i++) {
            mList.add(itemTip+i);
        }
        return mList;
    }

    /**
     * 初始化头布局
     * 注意：SliderLayout在使用的时候必须外面包裹一次布局，不然不现实
     */
    private void initHeaderView() {
        View headerView = View.inflate(mContext, R.layout.header_layout, null);
        mSlider = (SliderLayout)headerView.findViewById(R.id.slider);
        HashMap<String,Integer> fileMaps = new HashMap<String, Integer>();
        fileMaps.put("banner1", R.drawable.banner1);
        fileMaps.put("banner2",R.drawable.banner2);
        fileMaps.put("banner3",R.drawable.banner3);
        fileMaps.put("banner4",R.drawable.banner4);
        fileMaps.put("banner5",R.drawable.banner5);

        for(String name : fileMaps.keySet()){
            TextSliderView textSliderView = new TextSliderView(mContext);
            // initialize a SliderLayout
            textSliderView.description(name)
                          .image(fileMaps.get(name))
                          .setScaleType(BaseSliderView.ScaleType.Fit);
            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra",name);

            mSlider.addSlider(textSliderView);
        }
        mSlider.setPresetTransformer(SliderLayout.Transformer.ZoomRotateOut);
        mSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mSlider.setCustomAnimation(new DescriptionAnimation());
        mSlider.setDuration(2000);
        mSRListview.addHeaderView(headerView);
    }
    @Override
    protected void onStop() {
        // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed
        mSlider.stopAutoCycle();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSlider.recoverCycle();
    }
}
