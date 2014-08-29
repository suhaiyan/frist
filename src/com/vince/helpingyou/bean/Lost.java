package com.vince.helpingyou.bean;

import android.os.Parcel;
import android.os.Parcelable;
import cn.bmob.v3.BmobObject;

/** ����ʧ�����
  * @ClassName: Lost
  * @Description: TODO
  * @author smile
  * @date 2014-5-21 ����11:27:03
  */
public class Lost extends BmobObject implements Parcelable{

	private String title;//����
	private String describe;//����
	private String phone;//��ϵ�ֻ�
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescribe() {
		return describe;
	}
	public void setDescribe(String describe) {
		this.describe = describe;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
	 	dest.writeString(getObjectId());
	 	dest.writeString(title);
	 	dest.writeString(describe);
	 	dest.writeString(phone);
	 	dest.writeString(getCreatedAt());
	}
	
	public static final Parcelable.Creator<Lost>CREATOR = new Creator<Lost>() {

		@Override
		public Lost createFromParcel(Parcel in) {
			Lost lost = new Lost();
			lost.setObjectId(in.readString());
			lost.setTitle(in.readString());
			lost.setDescribe(in.readString());
			lost.setPhone(in.readString());
			lost.setCreatedAt(in.readString());
			return lost;
		}

		@Override
		public Lost[] newArray(int size) {
			return new Lost[size];
		}
	};

}
