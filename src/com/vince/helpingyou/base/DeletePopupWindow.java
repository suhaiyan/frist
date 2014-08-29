package com.vince.helpingyou.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.vince.helpingyou.i.IPopupItemClick;
import com.vince.to_help_you.R;


/** ³¤°´±à¼­µ¯´°
  * @ClassName: EditPopupWindow
  * @Description: TODO
  * @author smile
  * @date 2014-5-21 ÏÂÎç3:46:26
  */
public class DeletePopupWindow extends BasePopupWindow implements OnClickListener {

	private TextView mDelete;
	private IPopupItemClick mOnPopupItemClickListner;

	public DeletePopupWindow(Context context, int width, int height) {
		super(LayoutInflater.from(context).inflate(
				R.layout.pop_device_delete, null), dpToPx(context,width), dpToPx(context,height));
		setAnimationStyle(R.style.PopupAnimation);
	}
	
	private static int dpToPx(Context context, int dp) {
        return (int) (context.getResources().getDisplayMetrics().density * dp + 0.5f);
    }

	@Override
	public void initViews() {
		mDelete = (TextView) findViewById(R.id.tv_pop_delete);
	}

	@Override
	public void initEvents() {
		mDelete.setOnClickListener(this);
	}

	@Override
	public void init() {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_pop_delete:
			if (mOnPopupItemClickListner != null) {
				mOnPopupItemClickListner.onDelete(v);
			}
			break;
		}
		dismiss();
	}

	public void setOnPopupItemClickListner(IPopupItemClick l) {
		mOnPopupItemClickListner = l;
	}

}
