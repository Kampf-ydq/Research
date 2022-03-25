package com.powergrid.pojo;

import java.util.Arrays;

/*** @author 作者 E-mail:* @version 创建时间：2022年3月1日 下午10:23:16* 类说明*/
/**
 * @author ydq
 * @version 2022年3月1日
 */
/**
 * 特征向量(数频：数字出现的次数)
 */
public class NumberFrequency{
	private String userId; //用户id （1列）
	private String label; //标签（1列）
	private float[] vector = new float[10]; //词频向量（10列）

	// 无参构造
	public NumberFrequency(){
		
	}
	
	public NumberFrequency(float[] vector) {
		super();
		this.vector = vector;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public float[] getVector() {
		return vector;
	}

	public void setVector(float[] vector) {
		this.vector = vector;
	}

	@Override
	public String toString() {
		return "{userId=" + userId + ", label=" + label + ", vector=" + Arrays.toString(vector) + "}";
	}
	
}
