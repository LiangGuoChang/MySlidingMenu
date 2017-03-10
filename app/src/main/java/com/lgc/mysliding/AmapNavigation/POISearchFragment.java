package com.lgc.mysliding.AmapNavigation;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.lgc.mysliding.R;
import com.lgc.mysliding.views.MyEditTextDel;

import java.util.ArrayList;
import java.util.List;

public class POISearchFragment extends Fragment implements View.OnClickListener, TextWatcher, Inputtips.InputtipsListener, PoiSearch.OnPoiSearchListener, AdapterView.OnItemClickListener {

    private static final String TAG="POISearchFragment";
    private static final int POI_START_TYPE=303;
    private static final int POI_END_TYPE=308;
    private static final int POI_RESULT=300;
    private int poi_type=0;
    private View mView;
    private MyEditTextDel et_startEnd;
    private String etHint="";
    private ListView lv_poi_result;
    private PoiSearch.Query mQuery;
    private PoiSearch mPoiSearch;
    private PoiResult mPoiResult;
    private List<Tip> tipList=new ArrayList<Tip>();//获取关键字位置列表

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Intent intent=activity.getIntent();
        Bundle bundle=intent.getExtras();
        int item=bundle.getInt("item");
        poi_type=bundle.getInt("poiType");
        if (poi_type==POI_START_TYPE){
            etHint="请输入起点";
        }else if (poi_type==POI_END_TYPE){
            etHint="请输入终点";
        }
        Log.d(TAG,"onAttach-item::"+item);
        Log.d(TAG,"onAttach-poi_type::"+poi_type);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mView==null){
            mView = inflater.inflate(R.layout.fragment_poisearch, container, false);
        }
        initView();
        return mView;
    }

    //初始化控件
    private void initView(){
        ImageView poi_back= (ImageView) mView.findViewById(R.id.iv_poi_back);
        Button poi_search= (Button) mView.findViewById(R.id.btn_poi_search);
        poi_back.setOnClickListener(this);
        poi_search.setOnClickListener(this);
        lv_poi_result = (ListView) mView.findViewById(R.id.lv_poi_result);
        et_startEnd = (MyEditTextDel) mView.findViewById(R.id.et_start_end);
        et_startEnd.setHint(etHint);
        et_startEnd.addTextChangedListener(this);
        /*if (poi_type==POI_START_TYPE){
            et_startEnd.setHint("请输入起点");
        }else if (poi_type==POI_END_TYPE){
            et_startEnd.setHint("请输入终点");
        }*/
        lv_poi_result.setOnItemClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_poi_search://搜索
                doSearchQuery();
                break;
        }
    }

    //进行PoiSearch搜索
    protected void doSearchQuery(){
        if (!TextUtils.isEmpty(et_startEnd.getText().toString().trim())){
            String keyWord=et_startEnd.getText().toString().trim();
            mQuery = new PoiSearch.Query(keyWord,"","");
            mQuery.setPageSize(20);//每一也返回多少条
            mQuery.setPageNum(0);//设置当前页为第一页
            mPoiSearch = new PoiSearch(getActivity().getApplicationContext(),mQuery);
            //设置监听
            mPoiSearch.setOnPoiSearchListener(this);
            //发送请求
            mPoiSearch.searchPOIAsyn();
        }else {
            Toast.makeText(getActivity().getApplicationContext(),etHint,Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    //输入时poi关键字
    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        String newText=charSequence.toString().trim();
        if (!TextUtils.isEmpty(newText)){
            InputtipsQuery inputtipsQuery=new InputtipsQuery(newText,null);
            Inputtips inputtips=new Inputtips(getActivity().getApplicationContext(),inputtipsQuery);
            inputtips.setInputtipsListener(this);
            inputtips.requestInputtipsAsyn();
        }else {//清空前面保存过的数据
            if (lv_poi_result!=null){
                lv_poi_result.removeAllViews();
            }
            tipList.clear();
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    //输入关键字回调
    @Override
    public void onGetInputtips(List<Tip> list, int i) {
        if (i== AMapException.CODE_AMAP_SUCCESS){//正确返回
            tipList=list;//获取位置列表
            for (int j=0;j<list.size();j++){
                Log.d(TAG,"name--"+list.get(j).getName());
                Log.d(TAG,"address--"+list.get(j).getAddress());
                Log.d(TAG,"district--"+list.get(j).getDistrict());
            }
            PoiInputTipAdapter tipAdapter=new PoiInputTipAdapter(getActivity().getApplicationContext(),
                    list);
            lv_poi_result.setAdapter(tipAdapter);
            tipAdapter.notifyDataSetChanged();
        }else {
            Log.d(TAG,"返回失败");
        }
    }

    /**
     * PoiSearch搜索回调
     * @param poiResult
     * @param i
     */
    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {
        if (i==AMapException.CODE_AMAP_SUCCESS){
            if (poiResult!=null && poiResult.getQuery()!=null){//搜索poi结果
                if (poiResult.getQuery().equals(mQuery)){//是同一条
                    mPoiResult=poiResult;
                    List<PoiItem> poiItemList=mPoiResult.getPois();//取得第一页的PoiItem数据
                    if (poiItemList!=null && poiItemList.size()>0){
                        for (int j=0;j<poiItemList.size();j++){
                            Log.d(TAG,"onPoiSearched::"+j);
                            Log.d(TAG,"AdName::"+poiItemList.get(j).getAdName());
                            Log.d(TAG,"CityName::"+poiItemList.get(j).getCityName());
                            Log.d(TAG,"Distance::"+poiItemList.get(j).getDistance());
                            Log.d(TAG,"Snippet::"+poiItemList.get(j).getSnippet());
                        }
                    }
                }
            }else {
                Toast.makeText(getActivity().getApplicationContext(),"没有搜索到相关结果",Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(getActivity().getApplicationContext(),"查询失败",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    /**
     * 点击选择列表中位置事件
     * @param adapterView
     * @param view
     * @param i
     * @param l
     */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Log.d(TAG,"onItemClick--"+tipList.get(i).getName());
        Tip mTip=tipList.get(i);
        Intent tipIntent=new Intent();
        Bundle tipBundle=new Bundle();
        tipBundle.putParcelable("select_tip",mTip);
        tipIntent.putExtras(tipBundle);
        getActivity().setResult(POI_RESULT,tipIntent);
        getActivity().finish();
    }

}
