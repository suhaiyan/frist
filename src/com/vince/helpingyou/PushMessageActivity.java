package com.vince.helpingyou;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.vince.helping.db.DatabaseAdapter;
import com.vince.helpingyou.base.DeletePopupWindow;
import com.vince.helpingyou.i.IPopupItemClick;
import com.vince.to_help_you.R;

public class PushMessageActivity extends BaseActivity implements
OnClickListener,OnItemClickListener,IPopupItemClick{

	private Button btn_back;
	private ListView list_push;
	private LinearLayout layout_no;
	private DatabaseAdapter da;
	private ArrayList<HashMap<String, String>>list = new ArrayList<HashMap<String,String>>();
	private SimpleAdapter sa;
	
	private DeletePopupWindow mPopupWindow;
	private int position;
	
	@Override
	public void setContentView() {
		setContentView(R.layout.activity_push);
		da = new DatabaseAdapter(this);
		initEditPop();
	}

	
	private void initEditPop() {
		mPopupWindow = new DeletePopupWindow(this, 100, 48);
		mPopupWindow.setOnPopupItemClickListner(this);
	}

	@Override
	public void initViews() {
		btn_back = (Button) findViewById(R.id.btn_back);
		list_push = (ListView) findViewById(R.id.list_push);
		layout_no = (LinearLayout) findViewById(R.id.layout_no);
	}

	@Override
	public void initListeners() {
		btn_back.setOnClickListener(this);
		list_push.setOnItemClickListener(this);
	}

	@Override
	public void initData() {
		list = da.list();
		if(list==null){
			layout_no.setVisibility(View.VISIBLE);
		}else{
			sa = new SimpleAdapter(this, list, R.layout.list_item_noti, new String[]{"create_date","message"}, new int[]{R.id.tv_date,R.id.tv_message});
			list_push.setAdapter(sa);
		}
	}

	@Override
	public void onEdit(View v) {}

	@Override
	public void onDelete(View v) {
		HashMap<String, String> messageMap = list.get(position);
		da.delete(Integer.parseInt(messageMap.get(DatabaseAdapter.ID)));
		list.remove(position);
		sa.notifyDataSetChanged();
	}

	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		this.position = position;
		int [] location = new int[2];
		view.getLocationInWindow(location);
		mPopupWindow.showAtLocation(view, Gravity.RIGHT | Gravity.TOP, location[0], getStateBar()+location[1]);
	}

	@Override
	public void onClick(View v) {
		if(v==btn_back){
			Intent intent = new Intent(this,MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();
		}
	}

}
