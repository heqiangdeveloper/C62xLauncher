package com.chinatsp.apppanel.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anarchy.classifyview.event.ChangeTitleEvent;
import com.anarchy.classifyview.event.HideSubContainerEvent;
import com.anarchy.classifyview.simple.SimpleAdapter;
import com.anarchy.classifyview.simple.widget.InsertAbleGridView;
import com.anarchy.classifyview.util.L;
import com.anarchy.classifyview.util.MyConfigs;
import com.chinatsp.apppanel.R;
import com.chinatsp.apppanel.bean.InfoBean;
import com.chinatsp.apppanel.bean.LocationBean;
import com.chinatsp.apppanel.db.MyAppDB;
import com.chinatsp.apppanel.event.SelectedCallback;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class MyAppInfoAdapter extends SimpleAdapter<LocationBean, MyAppInfoAdapter.ViewHolder> {
    public List<List<LocationBean>> mData;
    public Context context;
    private MyAppDB db;
    private LocationBean locationBean;
    private InfoBean infoBean;
    //private ByteArrayOutputStream baos;
    //private Bitmap bitmap;
    //private Drawable drawable;
    private List<String> titleLists = new ArrayList<>();
    private List<LocationBean> infos = null;
    private String titleStr = null;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private int parentIndex;
    private String title;
    private boolean showDelete;
    private int selectSize = 0;

    public MyAppInfoAdapter(Context context, List<List<LocationBean>> mData) {
        super(mData);
        this.mData = mData;
        mData.removeAll(Collections.singleton(null));//清除掉null对象
        this.context = context;
        preferences = context.getSharedPreferences(MyConfigs.APPPANELSP, Context.MODE_PRIVATE);
        editor = preferences.edit();
        db = new MyAppDB(context);
        for(int i = 0; i < mData.size(); i++){
            infos = mData.get(i);
            titleLists.add(infos.get(0).getTitle());
        }
    }

    @Override
    protected ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    protected void onBindMainViewHolder(ViewHolder holder, int position) {
        super.onBindMainViewHolder(holder, position);
        List<LocationBean> infos = mData.get(position);
        //holder.tvName.setText("");
        String title = holder.tvName.getText().toString();
        Log.d("hqtest","info size is: " + infos.size());
        if(infos != null && infos.size() > 1){//文件夹
            if(!TextUtils.isEmpty(infos.get(0).getTitle())){
                titleStr = infos.get(0).getTitle();
            }else {
                int index = getExistDirIndex(db.getAllTitles());
                if(index == -1){
                    titleStr = "文件夹";
                }else {
                    index++;
                    titleStr = "文件夹" + index;
                }
            }
            for(LocationBean locationBean : infos){
                if(locationBean != null){
                    locationBean.setTitle(titleStr);
                }
            }
            holder.tvName.setText(titleStr);
            holder.deleteIv.setVisibility(View.GONE);

            for(int i = 0; i < infos.size(); i++){
                if(infos.get(i) == null){
                    continue;
                }
                LocationBean lb = infos.get(i);
                holder.deleteIv.setTag(lb.getCanuninstalled());
//                locationBean.setParentIndex(position);
//                locationBean.setChildIndex(i);
                //infos.get(i).setTitle(titleStr);
//                locationBean.setPackageName(infos.get(i).getPackageName());
//
                Drawable drawable;
                if(null == lb.getImgDrawable()){
                    byte[] b = lb.getImgByte();
                    drawable = new BitmapDrawable(BitmapFactory.decodeByteArray(b, 0, b.length));
                }else {
                    drawable = lb.getImgDrawable();
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                drawable.draw(canvas);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                lb.setImgByte(baos.toByteArray());
//                locationBean.setName(infos.get(i).getName());
//                locationBean.setAddBtn(0);
//                locationBean.setStatus(0);
//                locationBean.setPriority(0);
//                locationBean.setInstalled(1);
//                locationBean.setCanuninstalled(1);
                int num = db.isExistPackage(lb.getPackageName());
                Log.d("hqtest","dir package is: " + lb.getPackageName() + ",count = " + num + ",parent = " + position + ",child = " + i);
                if(num == 0){
                    db.insertLocation(lb);
                }else {
                    db.updateTitle(lb);
                }
            }
        } else if(infos.size() == 1){
            holder.tvName.setText(mData.get(position).get(0).getName());
            holder.deleteIv.setTag(mData.get(position).get(0).getCanuninstalled());
            //是否显示删除按钮
            parentIndex = preferences.getInt(MyConfigs.SHOWDELETEPOSITION ,  -1);
            showDelete = preferences.getBoolean(MyConfigs.SHOWDELETE,false);
            if(parentIndex != -1 && parentIndex == position){
                if(showDelete){
                    holder.deleteIv.setVisibility((int)holder.deleteIv.getTag() == 1 ? View.VISIBLE : View.GONE);
                }else {
                    holder.deleteIv.setVisibility(View.GONE);
                }
                editor.putBoolean(MyConfigs.SHOWDELETE,false);
                editor.putInt(MyConfigs.SHOWDELETEPOSITION,-1);
                editor.commit();
            }

            locationBean = mData.get(position).get(0);
//            locationBean.setParentIndex(position);
//            locationBean.setChildIndex(-1);
            locationBean.setTitle("");
//            locationBean.setPackageName(mData.get(position).get(0).getPackageName());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Drawable drawable;
            if(null == locationBean.getImgDrawable()){
                byte[] b = locationBean.getImgByte();
                drawable = new BitmapDrawable(BitmapFactory.decodeByteArray(b, 0, b.length));
            }else {
                drawable = mData.get(position).get(0).getImgDrawable();
            }
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            locationBean.setImgByte(baos.toByteArray());
//            locationBean.setName(mData.get(position).get(0).getName());
            locationBean.setAddBtn(0);
            locationBean.setStatus(0);
            locationBean.setPriority(0);
            locationBean.setInstalled(1);
//            locationBean.setCanuninstalled(1);
            int num = db.isExistPackage(locationBean.getPackageName());
            Log.d("hqtest","package package is: " + mData.get(position).get(0).getPackageName() + ",count = " + num + ",parent = " + position + ",child = " + -1);
            if(num == 0){
                db.insertLocation(locationBean);
            }else {
                db.updateTitle(locationBean);
            }

//            showDelete = preferences.getBoolean(MyConfigs.SHOWDELETE ,  false);
//            if(showDelete){
//                holder.deleteIv.setVisibility(mData.get(position).get(0).getCanuninstalled() == 1 ? View.VISIBLE : View.GONE);
//            }else {
//                holder.deleteIv.setVisibility(View.GONE);
//            }

        }
    }

    /*
    *   -1没有“文件夹” 0有“文件夹” X“文件夹X”
     */
    private int getExistDirIndex(List<String> titleLists){
        List<String> titles = new ArrayList<>();
        int lastNumber = -1;
        for(String title : titleLists){
            if(title.startsWith("文件夹") && title.length() <= 5){
                if(title.length() == 5){
                    if(title.substring(3,4).matches("[1-9]") && title.substring(4,5).matches("[0-9]")){
                        if(Integer.parseInt(title.substring(3,5)) > lastNumber){
                            lastNumber = Integer.parseInt(title.substring(3,5));
                        }
                    }
                }else if(title.length() == 4){
                    if(title.substring(3,4).matches("[0-9]")){
                        if(Integer.parseInt(title.substring(3,4)) > lastNumber){
                            lastNumber = Integer.parseInt(title.substring(3,4));
                        }
                    }
                }else {
                    if(lastNumber == -1) lastNumber = 0;
                }
            }
        }

        return lastNumber;
    }

    @Override
    protected void onBindSubViewHolder(ViewHolder holder, int mainPosition, int subPosition) {
        super.onBindSubViewHolder(holder, mainPosition, subPosition);
        if(subPosition < mData.get(mainPosition).size()){
            if(mData.get(mainPosition).get(subPosition) == null){
                holder.tvName.setText(context.getString(R.string.add));
            }else {
                holder.tvName.setText(mData.get(mainPosition).get(subPosition).getName());
                Log.d("hqtest","onBindSubViewHolder package is: " + mData.get(mainPosition).get(subPosition).getPackageName() + ",parent = " + mainPosition + ",child = " + subPosition);
            }
        }
    }

    @Override
    public View getView(ViewGroup parent, int mainPosition, int subPosition) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inner,parent,false);
        ImageView iconIv = (ImageView) view.findViewById(R.id.icon_iv);
        if(subPosition < mData.get(mainPosition).size()){
            if(mData.get(mainPosition).get(subPosition) == null){
                iconIv.setImageResource(R.drawable.ic_add_black_24dp);
            }else{
                if(null == mData.get(mainPosition).get(subPosition).getImgDrawable()){
                    byte[] b = mData.get(mainPosition).get(subPosition).getImgByte();
                    iconIv.setImageDrawable(new BitmapDrawable(BitmapFactory.decodeByteArray(b, 0, b.length)));
                }else {
                    iconIv.setImageDrawable(mData.get(mainPosition).get(subPosition).getImgDrawable());
                }
            }
        }

        return view;
    }

    public void changeTitle(ChangeTitleEvent event){
        L.d("changeTile to " + event.getTitle());
        List<LocationBean> infos = mData.get(event.getParentIndex());
        for(LocationBean locationBean : infos){
            if(locationBean != null){
                locationBean.setTitle(event.getTitle());
            }
        }
    }

    /*
     *打开应用
     * @param packageName包名
     */
    private void launchApp(String packageName){
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        context.startActivity(intent);
    }

    @Override
    protected void onItemClick(View view, int parentIndex, int index) {
        //Toast.makeText(view.getContext(),"x: "+parentIndex+"\nindex: "+index,Toast.LENGTH_SHORT).show();

        RelativeLayout relativeLayout = (RelativeLayout) view;
        ImageView iv = (ImageView) relativeLayout.getChildAt(1);
        TextView tv = (TextView) relativeLayout.getChildAt(2);
        if(tv.getText().toString().trim().equals(context.getString(R.string.add))){
            showAddDialog(parentIndex);
        }else {
            if(iv.getVisibility() == View.VISIBLE){//如果删除按钮显示了，执行删除应用逻辑
                hideDeleteIcon((RecyclerView) relativeLayout.getParent());
                showDeleteDialog(tv.getText().toString());
            }else {
                hideDeleteIcon((RecyclerView) relativeLayout.getParent());
                String packageName = "";
                if(index == -1){//-1 是main area
                    packageName = mData.get(parentIndex).get(0).getPackageName();
                }else {
                    if(mData.get(parentIndex).get(index) != null){
                        packageName = mData.get(parentIndex).get(index).getPackageName();
                    }else {
                        return;
                    }
                }
                launchApp(packageName);
            }
        }
    }

    private void showAddDialog(int parentIndex){
        Dialog dialog = new Dialog(context, com.anarchy.classifyview.R.style.mydialog);
        dialog.setContentView(R.layout.add_dialog);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setAttributes(params);

        TextView positiveTv = (TextView) dialog.getWindow().findViewById(R.id.add_dialog_positive_tv);
        TextView negativeTv = (TextView) dialog.getWindow().findViewById(R.id.add_dialog_negative_tv);
        TextView titleTv = (TextView) dialog.getWindow().findViewById(R.id.add_dialog_title);

        RecyclerView rv = (RecyclerView) dialog.getWindow().findViewById(R.id.add_recyclerview);
        List<LocationBean> initlists = getAddAppLists(parentIndex,titleTv);//初始化titleTv的内容
        AddAppAdapter addAppAdapter = new AddAppAdapter(context,initlists,parentIndex,new SelectedCallback(){

            @Override
            public void onSelect(String selectContent) {
                titleTv.setText(context.getString(R.string.add_dialog_title,selectContent));
            }
        });
        rv.setAdapter(addAppAdapter);
        rv.setLayoutManager(new GridLayoutManager(context, 3));

        positiveTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                /*
                *  策略：
                *  情况一 所选择的应用为0个
                *        方案：将sub中第一个应用替换该parentIndex位置，其余的应用添加在桌面最后,隐藏sub
                *  情况二 所选择的应用为1个
                *        1.此一个应用是sub中原来就有的
                *          方案：将此应用替代当前的位置，sub中剩余的应用添加在桌面最后，隐藏sub
                *        2.此一个应用不是sub中原来就有的
                *          方案：将此应用替代当前位置，同时取sub中的第一个应用替代此应用在桌面上原来的位置，sub中剩余的应用添加在桌面最后,隐藏sub
                *  情况三 所选择的应用超过1个时
                *        1.原sub中被遗弃的应用个数 大于或等于 新增的应用个数
                *           方案：用被遗弃的应用替换新增的应用在桌面上原来的位置，剩余的被遗弃的应用添加在桌面的最后，并更新sub
                *        2.原sub中被遗弃的应用个数 小于 新增的应用个数
                *           方案：用被遗弃的应用替换新增的应用在桌面上原来的位置，不够的使用空数组代替，并更新sub
                 */
                List<LocationBean> selectedLists = addAppAdapter.getSelectdItems();//确认添加的应用list
                if(selectedLists.size() == 0){
                    EventBus.getDefault().post(new HideSubContainerEvent());//通知ClassifyView隐藏subContainer
                    //将sub中第一个应用替换该parentIndex位置，其余的应用添加在桌面最后
                    LocationBean lb = mData.get(parentIndex).get(0);
                    mData.remove(parentIndex);
                    lb.setParentIndex(parentIndex);
                    lb.setChildIndex(-1);
                    List<LocationBean> newList = new ArrayList<>();
                    newList.add(lb);
                    mData.add(parentIndex,newList);

                    List<LocationBean> subLists = getSubAdapter().getSubData();//原有的sub中的应用list
                    subLists.removeAll(Collections.singleton(null));//清除掉null对象
                    subLists.remove(0);
                    for (int m = 0; m < subLists.size(); m++) {
                        List<LocationBean> newList1 = new ArrayList<>();
                        LocationBean locationBean = subLists.get(m);
                        locationBean.setParentIndex(mData.size());
                        locationBean.setChildIndex(-1);
                        newList1.add(locationBean);
                        mData.add(newList1);
                    }
                    notifyDataSetChanged();
                }else if(selectedLists.size() == 1){
                    EventBus.getDefault().post(new HideSubContainerEvent());//通知ClassifyView隐藏subContainer
                    LocationBean lb = selectedLists.get(0);
                    int originPosition = lb.getParentIndex();
                    if(mData.get(parentIndex).contains(lb)){//原来就有的
                        mData.remove(parentIndex);
                        lb.setParentIndex(parentIndex);
                        lb.setChildIndex(-1);
                        List<LocationBean> newList = new ArrayList<>();
                        newList.add(lb);
                        mData.add(parentIndex,newList);

                        List<LocationBean> subLists = getSubAdapter().getSubData();
                        subLists.removeAll(Collections.singleton(null));//清除掉null对象
                        //找出这个lb在原有的subLists中的位置，并移除
                        for(LocationBean locationBean:subLists){
                            if(locationBean != null && locationBean.getPackageName().equals(lb.getPackageName())){
                                subLists.remove(locationBean);
                                break;
                            }
                        }
                        //原有的subLists剩余的放在桌面最后
                        for (int m = 0; m < subLists.size(); m++) {
                            List<LocationBean> newList1 = new ArrayList<>();
                            LocationBean locationBean = subLists.get(m);
                            locationBean.setParentIndex(mData.size());
                            locationBean.setChildIndex(-1);
                            newList1.add(locationBean);
                            mData.add(newList1);
                        }
                    }else {//不是原来就有的
                        mData.remove(parentIndex);
                        lb.setParentIndex(parentIndex);
                        lb.setChildIndex(-1);
                        List<LocationBean> newList = new ArrayList<>();
                        newList.add(lb);
                        mData.add(parentIndex,newList);

                        List<LocationBean> subLists = getSubAdapter().getSubData();
                        subLists.removeAll(Collections.singleton(null));//清除掉null对象
                        LocationBean locationBean = subLists.get(0);//取原有的subLists中的第一个替代被选中的那个原来在桌面的位置
                        locationBean.setParentIndex(originPosition);
                        locationBean.setChildIndex(-1);
                        List<LocationBean> childList = new ArrayList<>();
                        childList.add(locationBean);
                        mData.remove(originPosition);
                        mData.add(originPosition,childList);

                        subLists.remove(0);//移除掉第一个
                        //原有的subLists剩余的放在桌面最后
                        for (int m = 0; m < subLists.size(); m++) {
                            List<LocationBean> newList1 = new ArrayList<>();
                            LocationBean locationBean1 = subLists.get(m);
                            locationBean1.setParentIndex(mData.size());
                            locationBean1.setChildIndex(-1);
                            newList1.add(locationBean1);
                            mData.add(newList1);
                        }
                    }
                    notifyDataSetChanged();
                }else {//所选择的应用超过1个时
                    List<LocationBean> subLists = getSubAdapter().getSubData();
                    subLists.removeAll(Collections.singleton(null));//清除掉null对象

                    //此时发现新添加的那个应用，在mData中ParentIndex，ChildIndex位置也会变，需要修正
                    for(int i = 0; i < mData.size(); i++){
                        List<LocationBean> lists = mData.get(i);
                        if(lists != null && lists.size() == 1){
                            lists.get(0).setParentIndex(i);
                            lists.get(0).setChildIndex(-1);
                        }
                    }

                    //selectedLists中属于sub原有的放在originItems中
                    List<LocationBean> originItems = new ArrayList<>();
                    for(LocationBean lb : subLists){
                        if(selectedLists.contains(lb)){
                            originItems.add(lb);
                        }
                    }

                    //移除掉originItems，剩下的selectedLists就是新增的
                    selectedLists.removeAll(originItems);
                    //移除掉originItems，剩下的subLists就是sub遗弃的（指未选中的）
                    subLists.removeAll(originItems);
                    LocationBean originlb;//sub遗弃的应用bean
                    LocationBean newlb;//新增的应用bean
                    List<LocationBean> currentLists = null;
                    //被遗弃的应用数量 大于或等于 新增的应用数量
                    if(subLists.size() >= selectedLists.size()){
                        //替换被选中的那些原来在桌面的位置
                        for(int i = 0; i < selectedLists.size(); i++){
                            newlb = selectedLists.get(i);
                            List<LocationBean> targetLists = mData.get(newlb.getParentIndex());
                            targetLists.clear();
                            originlb = subLists.get(i);
                            originlb.setParentIndex(newlb.getParentIndex());
                            originlb.setChildIndex(-1);
                            originlb.setTitle("");
                            targetLists.add(originlb);
                            mData.set(newlb.getParentIndex(),targetLists);
                        }

                        //原有的subLists剩余的放在桌面最后
                        for (int m = selectedLists.size(); m < subLists.size(); m++) {
                            List<LocationBean> newList1 = new ArrayList<>();
                            LocationBean locationBean1 = subLists.get(m);
                            locationBean1.setParentIndex(mData.size());
                            locationBean1.setChildIndex(-1);
                            newList1.add(locationBean1);
                            mData.add(newList1);
                        }

                        //重置selectedLists中的ParentIndex，ChildIndex
                        for (int i = 0; i < selectedLists.size(); i++) {
                            LocationBean lb = selectedLists.get(i);
                            lb.setParentIndex(parentIndex);
                            lb.setChildIndex(i);
                        }
                        currentLists = mData.get(parentIndex);
                        currentLists.clear();
                        currentLists.addAll(originItems);//原有的
                        currentLists.addAll(selectedLists);//新增的
                        //对每项，重新设置ChildIndex
                        for(int i = 0; i < currentLists.size(); i++){
                            currentLists.get(i).setChildIndex(i);
                        }
                        mData.set(parentIndex,currentLists);
                    }else {//被遗弃的应用数量 小于 新增的应用数量
                        for(int i = 0; i < selectedLists.size(); i++){
                            //替换被选中的那些原来在桌面的位置
                            if(i < subLists.size()){
                                newlb = selectedLists.get(i);
                                originlb = subLists.get(i);
                                originlb.setParentIndex(newlb.getParentIndex());
                                originlb.setChildIndex(-1);
                                originlb.setTitle("");
                                List<LocationBean> originLists = new ArrayList<>();
                                originLists.add(originlb);
                                mData.set(newlb.getParentIndex(),originLists);
                            }else {//不够的使用空数组代替
                                newlb = selectedLists.get(i);
                                List<LocationBean> originLists = new ArrayList<>();
                                mData.set(newlb.getParentIndex(),originLists);
                            }
                        }

                        //重置selectedLists中的ParentIndex，ChildIndex
                        for (int i = 0; i < selectedLists.size(); i++) {
                            LocationBean lb = selectedLists.get(i);
                            lb.setParentIndex(parentIndex);
                            lb.setChildIndex(i);
                        }
                        currentLists = mData.get(parentIndex);
                        currentLists.clear();
                        currentLists.addAll(originItems);//原有的
                        currentLists.addAll(selectedLists);//新增的
                        //对每项，重新设置ChildIndex
                        for(int i = 0; i < currentLists.size(); i++){
                            currentLists.get(i).setChildIndex(i);
                        }
                        mData.set(parentIndex,currentLists);
                    }

                    //删除mData中item下的空数组
                    for(int i = 0; i < mData.size(); i++){
                        if(mData.get(i) != null && mData.get(i).size() == 0){
                            mData.remove(i);
                            i--;
                        }
                    }
                    notifyDataSetChanged();

                    //找出桌面位置，因为桌面应用位置可能已经调整了，不能再使用parentIndex
                    int newIndex = 0;
                    for(int k =0; k < mData.size(); k++){
                        if(mData.get(k).get(0).getPackageName().equals(currentLists.get(0).getPackageName())){
                            newIndex = k;
                            break;
                        }
                    }
                    mData.get(newIndex).add(null);//新增添加按钮
                    getSubAdapter().initData(newIndex,mData.get(newIndex));//更新sub
                }
            }
        });
        negativeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private List<LocationBean> getAddAppLists(int parentIndex,TextView tv){
        List<LocationBean> addAppLists = new ArrayList<>();
        List<LocationBean> lists;
        addAppLists.addAll(mData.get(parentIndex));
        addAppLists.removeAll(Collections.singleton(null));//移除null
        selectSize = addAppLists.size();
        //拖动应用至文件夹中时，要修正其parentIndex值
        for (LocationBean info:addAppLists) {
            if(null != info) info.setParentIndex(parentIndex);
        }
        for(int i = 0; i < mData.size(); i++){
            lists = mData.get(i);
            if(lists != null && lists.size() == 1){//非文件夹
                lists.get(0).setParentIndex(i);//修正其parentIndex值
                addAppLists.add(lists.get(0));
            }
        }

        tv.setText(context.getString(R.string.add_dialog_title,selectSize + "/" + addAppLists.size()));
        return addAppLists;
    }

    private void showDeleteDialog(String name){
        Dialog dialog = new Dialog(context, com.anarchy.classifyview.R.style.mydialog);
        dialog.setContentView(R.layout.uninstall_dialog);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setAttributes(params);
        TextView positiveTv = (TextView) dialog.getWindow().findViewById(R.id.uninstall_dialog_positive_tv);
        TextView negativeTv = (TextView) dialog.getWindow().findViewById(R.id.uninstall_dialog_negative_tv);
        TextView titleTv = (TextView) dialog.getWindow().findViewById(R.id.uninstall_dialog_title);
        titleTv.setText(context.getString(R.string.uninstall_dialog_title,name));
        positiveTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        negativeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void hideDeleteIcon(RecyclerView recyclerView){
        RelativeLayout relativeLayout;
        InsertAbleGridView insertAbleGridView;
        for(int i = 0; i < recyclerView.getChildCount(); i++){
            relativeLayout = (RelativeLayout) recyclerView.getChildAt(i);
            insertAbleGridView = (InsertAbleGridView) relativeLayout.getChildAt(0);
            if(insertAbleGridView.getChildCount() == 1){//非文件夹
                ImageView iv = (ImageView) relativeLayout.getChildAt(1);
                iv.setVisibility(View.GONE);
            }
        }
    }

    static class ViewHolder extends SimpleAdapter.ViewHolder {
        public TextView tvName;
        public ImageView deleteIv;
        public ViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.app_name_tv);
            deleteIv = (ImageView) itemView.findViewById(R.id.delete_iv);
        }
    }
}
