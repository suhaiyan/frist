package com.vince.helpingyou;

import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import com.vince.helpingyou.bean.Found;
import com.vince.helpingyou.bean.Lost;
import com.vince.to_help_you.R;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class AddActivity extends BaseActivity implements OnClickListener{

	private EditText edit_title, edit_phone, edit_describe;
	private Button btn_back, btn_true;

	private TextView tv_add;
	String from = "";
	
	String old_title = "";
	String old_describe = "";
	String old_phone = "";
	
	@Override
	public void setContentView() {
		setContentView(R.layout.activity_add);
	}

	@Override
	public void initViews() {
		tv_add = (TextView) findViewById(R.id.tv_add);
		btn_back = (Button) findViewById(R.id.btn_back);
		btn_true = (Button) findViewById(R.id.btn_true);
		edit_phone = (EditText) findViewById(R.id.edit_phone);
		edit_describe = (EditText) findViewById(R.id.edit_describe);
		edit_title = (EditText) findViewById(R.id.edit_title);
	}

	@Override
	public void initListeners() {
		btn_back.setOnClickListener(this);
		btn_true.setOnClickListener(this);
	}

	private Lost editLost;
	private Found editFound;
	@Override
	public void initData() {
		from = getIntent().getStringExtra("from");   
		if("lost".equals(from)){
			tv_add.setText("添加失物信息");
		}else if("found".equals(from)){
			tv_add.setText("添加招领信息");
		}else if("edit_lost".equals(from)){
			tv_add.setText("编辑失物信息");
			editLost = getIntent().getParcelableExtra("lost");
			old_title = editLost.getTitle();
			old_describe = editLost.getDescribe();
			old_phone = editLost.getPhone();
			initViewData();
		}else if("edit_found".equals(from)){
			tv_add.setText("编辑招领信息");
			editFound = getIntent().getParcelableExtra("found");
			old_title = editFound.getTitle();
			old_describe =editFound.getDescribe();
			old_phone = editFound.getPhone();
			initViewData();
		}
	}

	private void initViewData() {
		edit_title.setText(old_title);
		edit_describe.setText(old_describe);
		edit_phone.setText(old_phone);
	}

	@Override
	public void onClick(View v) {
		if(btn_true==v){
			if("lost".equals(from)){
				final Lost lost = new Lost();
				lost.setTitle(edit_title.getText().toString());
				lost.setDescribe(edit_describe.getText().toString());
				lost.setPhone(edit_phone.getText().toString());
				lost.save(this, new SaveListener() {
					@Override
					public void onSuccess() {
						ShowToast("失物信息添加成功");
						Intent intent = getIntent();
						intent.putExtra("lost", lost);
						intent.putExtra("lost_op", "add");
						setResult(RESULT_OK,intent);
						finish();
					}
					
					@Override
					public void onFailure(int code, String arg1) {
					}
				});
			   
			}else if("found".equals(from)){
				final Found found = new Found();
				found.setTitle(edit_title.getText().toString());
				found.setDescribe(edit_describe.getText().toString());
				found.setPhone(edit_phone.getText().toString());
				found.save(this, new SaveListener() {
					@Override
					public void onSuccess() {
						ShowToast("添加招领信息成功");
						Intent intent = getIntent();
						intent.putExtra("found", found);
						intent.putExtra("found_op", "add");
						setResult(RESULT_OK,intent);
						finish();
					}
					
					@Override
					public void onFailure(int code, String arg1) {
					}
				});
			}else if("edit_lost".equals(from)){
				editLost.setTitle(edit_title.getText().toString());
				editLost.setDescribe(edit_describe.getText().toString());
				editLost.setPhone(edit_phone.getText().toString());  
				editLost.update(this, editLost.getObjectId(), new UpdateListener() {
					@Override
					public void onSuccess() {
						ShowToast("失物信息已更新");
						Intent intent = getIntent();
						intent.putExtra("lost", editLost);
						intent.putExtra("lost_top", "update");
						setResult(RESULT_OK, intent);
						finish();
						
					}
					
					@Override
					public void onFailure(int arg0, String arg1) {
						
					}
				});
			}else if("edit_found".equals(from)){
				editFound.setTitle(edit_title.getText().toString());
				editFound.setDescribe(edit_describe.getText().toString());
				editFound.setPhone(edit_phone.getText().toString());
				editFound.delete(this, editFound.getObjectId(), new DeleteListener() {
					@Override
					public void onSuccess() {
						ShowToast("招领信息已更新");
						Intent intent = getIntent();
						intent.putExtra("found", editFound);
						intent.putExtra("found_top", "update");
						setResult(RESULT_OK, intent);
						finish();
					}
					@Override
					public void onFailure(int arg0, String arg1) {
						
					}
				});
			}
		}else if(btn_back==v){
			finish();
		}
	}

}









