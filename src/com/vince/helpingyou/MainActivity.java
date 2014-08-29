package com.vince.helpingyou;

import java.util.List;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;

import com.vince.helpingyou.base.EditPopupWindow;
import com.vince.helpingyou.bean.Found;
import com.vince.helpingyou.bean.Lost;
import com.vince.helpingyou.config.Constants;
import com.vince.helpingyou.i.IPopupItemClick;
import com.vince.helpingyou.AddActivity;
import com.vince.to_help_you.R;

public class MainActivity extends BaseActivity implements
		OnClickListener, IPopupItemClick, OnItemLongClickListener {

	private RelativeLayout layout_action;// 标题栏
	private LinearLayout layout_all;// 标题栏中间下拉视图
	private TextView tv_lost;
	private ListView listview;
	private Button btn_add;
	private Button btn_push_message;

	private List<Lost> lostList;
	private LostAdapter lostAdapter;

	private List<Found> foundList;
	private FoundAdapter foundAdapter;

	private Button layout_found;
	private Button layout_lost;
	PopupWindow morePop;
	private int position;

	RelativeLayout progress;
	LinearLayout layout_no;
	TextView tv_no;

	@Override
	public void setContentView() {
		setContentView(R.layout.activity_main);
	}
	

	@Override
	public void initViews() {
		progress = (RelativeLayout) findViewById(R.id.progress);
		layout_no = (LinearLayout) findViewById(R.id.layout_no);
		tv_no = (TextView) findViewById(R.id.tv_no);

		layout_action = (RelativeLayout) findViewById(R.id.layout_action);
		layout_all = (LinearLayout) findViewById(R.id.layout_all);
		// 默认是失物界面
		tv_lost = (TextView) findViewById(R.id.tv_lost);
		tv_lost.setTag("lost");
		listview = (ListView) findViewById(R.id.list_lost);
		btn_add = (Button) findViewById(R.id.btn_add);
		btn_push_message = (Button) findViewById(R.id.button1_push_message);
		initEditPop();
	}


	@Override
	public void initListeners() {
		layout_all.setOnClickListener(this);
		listview.setOnItemLongClickListener(this);
		btn_add.setOnClickListener(this);
		btn_push_message.setOnClickListener(this);
	}
  
	@Override
	public void initData() {
		findLostAll();
	}

	/**
	 * 请求出错或者无数据时候显示的界面 showErrorView
	 * 
	 * @return void
	 * @throws
	 */
	private void showErrorView(int tag) {
		progress.setVisibility(View.GONE);
		listview.setVisibility(View.GONE);
		layout_no.setVisibility(View.VISIBLE);
		if (tag == 0) {
			tv_no.setText(getResources().getText(R.string.list_no_data_lost));
		} else {
			tv_no.setText(getResources().getText(R.string.list_no_data_found));
		}
	}
	private void showView() {
		listview.setVisibility(View.VISIBLE);
		layout_no.setVisibility(View.GONE);
		progress.setVisibility(View.GONE);
	}               

	/**
	 * 长按编辑接口事件
	 */
	@Override
	public void onEdit(View v) {
		if ("lost".equals(tv_lost.getTag().toString())) {
			Lost lost = (Lost) lostAdapter.getItem(position);
			Intent intent = new Intent(this, AddActivity.class);
			intent.putExtra("lost", lost);
			intent.putExtra("from", "edit_lost");
			startActivityForResult(intent, Constants.REQUESTCODE_ADD);
		} else if("found".equals(tv_lost.getTag().toString())){
			Found f = (Found)foundAdapter.getItem(position);
			Intent intent = new Intent(this,AddActivity.class);
			intent.putExtra("found", f);
			intent.putExtra("from", "edit_found");
			startActivityForResult(intent, Constants.REQUESTCODE_ADD);
		}
	}

	/**
	 * 长按删除接口事件
	 */
	@Override
	public void onDelete(View v) {
		if("lost".equals(tv_lost.getTag().toString())){
			Lost lost = (Lost) lostAdapter.getItem(position);
			lost.delete(this, lost.getObjectId(), new DeleteListener() {
				@Override
				public void onSuccess() {
					ShowToast("失物信息已删除");
					lostList.remove(position);
					lostAdapter.notifyDataSetChanged();
				}
				
				@Override
				public void onFailure(int arg0, String arg1) {
					ShowToast("失物信息删除失败");  
				}
			});
		}else if("found".equals(tv_lost.getTag().toString())){
			Found found = (Found) foundAdapter.getItem(position);
			found.delete(this, found.getObjectId(), new DeleteListener() {
				@Override
				public void onSuccess() {
					ShowToast("招领信息已删除");
					foundList.remove(position);
					foundAdapter.notifyDataSetChanged();
				}
				@Override
				public void onFailure(int arg0, String arg1) {
					ShowToast("招领信息删除失败");
				}
			});
		}
		
	}

	@Override
	public void onClick(View v) {
		if (v == btn_add) { // 添加按钮
			Intent intent = new Intent(this, AddActivity.class);
			intent.putExtra("from", tv_lost.getTag().toString());
			startActivityForResult(intent, Constants.REQUESTCODE_ADD);
		} else if (v == layout_all) { // 下拉选择窗口
			showListPop();
		} else if (layout_found == v) {
			// 表示点击了招领
			tv_lost.setText("招领");
			tv_lost.setTag("found");
			findFoundAll();
		} else if (layout_lost == v) {
			// 表示点击了失物
			tv_lost.setText("失物");
			tv_lost.setTag("lost");
			findLostAll();
		}else if(btn_push_message == v){
			Intent intent = new Intent(this,PushMessageActivity.class);
			startActivity(intent);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == Constants.REQUESTCODE_ADD && resultCode == RESULT_OK) {
			if ("lost".equals(tv_lost.getTag())) {
				Lost lost = data.getParcelableExtra("lost");
				String lost_op = data.getStringExtra("lost_op");
				if ("add".equals(lost_op)) {
					lostList.add(0,lost);
				}else if("update".equals(lost_op)){
					Lost old = (Lost) lostAdapter.getItem(position);
					old.setTitle(lost.getTitle());
					old.setDescribe(lost.getDescribe());
					old.setPhone(lost.getPhone());
				}
				lostAdapter.notifyDataSetChanged();
			}else if("found".equals(tv_lost.getTag())){
				Found found = data.getParcelableExtra("found");
				String found_op = data.getStringExtra("found_op");
				if("add".equals(found_op)){
					foundList.add(0,found);
				}else if("update".equals(found_op)){
					Found old = (Found) foundAdapter.getItem(position);
					old.setTitle(found.getTitle());
					old.setDescribe(found.getDescribe());
					old.setPhone(found.getPhone());
				}
				foundAdapter.notifyDataSetChanged();
			}
		}

	}

	private void initEditPop() {
		mPopupWindow = new EditPopupWindow(this, 200, 48);
		mPopupWindow.setOnPopupItemClickListner(this);
	}
	EditPopupWindow mPopupWindow;

	/**
	 * 查询Lost列表
	 * 
	 * @author Administrator
	 * 
	 */
	public void findLostAll() {
		BmobQuery<Lost> query = new BmobQuery<Lost>();
		query.order("-createdAt");// 按照时间降序
		query.findObjects(this, new FindListener<Lost>() {

			@Override
			public void onSuccess(List<Lost> lostList) {
				MainActivity.this.lostList = lostList;
				lostAdapter = new LostAdapter(lostList);
				listview.setAdapter(lostAdapter);
				showView();
			}

			@Override
			public void onError(int arg0, String arg1) {
				showErrorView(0);
			}
		});
	}

	// Lost失物适配器
	class LostAdapter extends BaseAdapter {

		private List<Lost> lostList;
		
		public LostAdapter(List<Lost> lostList) {
			this.lostList = lostList;
		}

		@Override
		public int getCount() {
			return lostList.size();
		}

		@Override
		public Object getItem(int position) {
			return lostList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder vh;
			
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(R.layout.item_list,
						null);
				vh = new ViewHolder();
				vh.tv_title = (TextView) convertView
						.findViewById(R.id.tv_title);
				vh.tv_describe = (TextView) convertView.findViewById(R.id.tv_describe);
				vh.tv_phone = (TextView) convertView
						.findViewById(R.id.tv_phone);
				vh.tv_createdAt = (TextView) convertView
						.findViewById(R.id.tv_time);
				convertView.setTag(vh);
			}
			vh = (ViewHolder) convertView.getTag();
			Lost lost = lostList.get(position);
			vh.tv_title.setText(lost.getTitle());
			vh.tv_describe.setText(lost.getDescribe());
			vh.tv_phone.setText(lost.getPhone());
			vh.tv_createdAt.setText(lost.getCreatedAt());
			//打电话
			vh.tv_phone.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					//打开 拨号面板，并输入号码
					intent.setAction(Intent.ACTION_DIAL);
					String phoneNumber = ((TextView)v).getText().toString();
					intent.setData(Uri.parse("tel:"+phoneNumber));
					startActivity(intent);
				}
			});
			return convertView;
		}
		
	}

	/**
	 * 查询Found列表
	 */
	public void findFoundAll() {
		BmobQuery<Found> query = new BmobQuery<Found>();
		query.order("--createAt");
		query.findObjects(this, new FindListener<Found>() {

			@Override
			public void onError(int arg0, String arg1) {
				showErrorView(1);
			}

			@Override
			public void onSuccess(List<Found> foundList) {
				MainActivity.this.foundList = foundList;
				foundAdapter = new FoundAdapter();
				listview.setAdapter(foundAdapter);
				showView();
			}
		});

	}

	// Found适配器
	class FoundAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return foundList.size();
		}

		@Override
		public Object getItem(int position) {
			return foundList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder vh;
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(R.layout.item_list,
						null);
				vh = new ViewHolder();
				vh.tv_title = (TextView) convertView
						.findViewById(R.id.tv_title);
				vh.tv_describe = (TextView) convertView
						.findViewById(R.id.tv_describe);
				vh.tv_phone = (TextView) convertView
						.findViewById(R.id.tv_phone);
				vh.tv_createdAt = (TextView) convertView
						.findViewById(R.id.tv_time);
				convertView.setTag(vh);
			}
			vh = (ViewHolder) convertView.getTag();
			Found found = foundList.get(position);
			vh.tv_title.setText(found.getTitle());
			vh.tv_describe.setText(found.getDescribe());
			vh.tv_phone.setText(found.getPhone());
			vh.tv_createdAt.setText(found.getCreatedAt());
			
			vh.tv_phone.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					//打开 拨号面板，并输入号码
					intent.setAction(Intent.ACTION_DIAL);
					String phoneNumber = ((TextView)v).getText().toString();
					intent.setData(Uri.parse("tel:"+phoneNumber));
					startActivity(intent);
				}
			});
			return convertView;
		}
	}

	class ViewHolder {
		TextView tv_title;
		TextView tv_describe;
		TextView tv_phone;
		TextView tv_createdAt;
	}

	// 在标题栏下拉事件的弹窗
	private void showListPop() {
		View view = LayoutInflater.from(this).inflate(R.layout.pop_lost, null);
		// 注入
		layout_found = (Button) view.findViewById(R.id.layout_found);
		layout_lost = (Button) view.findViewById(R.id.layout_lost);
		layout_found.setOnClickListener(this);
		layout_lost.setOnClickListener(this);
		morePop = new PopupWindow(view, mScreenWidth, 600);

		// 弹窗
		morePop.setTouchInterceptor(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					morePop.dismiss();
					return true;
				}
				return false;
			}
		});

		morePop.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
		morePop.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
		morePop.setTouchable(true);
		morePop.setFocusable(true);
		morePop.setOutsideTouchable(true);
		morePop.setBackgroundDrawable(new BitmapDrawable());
		// 动画效果 从顶部弹下
		morePop.setAnimationStyle(R.style.MenuPop);
		morePop.showAsDropDown(layout_action, 0, -dip2px(this, 2.0F));
	}



	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position,
			long id) {
		this.position = position;
		int[] location = new int[2];
		view.getLocationOnScreen(location);
		mPopupWindow.showAtLocation(view, Gravity.RIGHT | Gravity.TOP,
				location[0], getStateBar() + location[1]);
		return false;
	}
	
}
