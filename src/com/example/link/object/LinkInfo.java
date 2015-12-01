package com.example.link.object;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Point;

public class LinkInfo {
	//�������ϱ������ӵ�
	private List<Point>points=new ArrayList<Point>();
	
	//�ṩһ������������ʾ����Point����ֱ�����ӣ�û��ת�۵�
	public LinkInfo(Point p1,Point p2){
		//�ӵ�������ȥ
		points.add(p1);
		points.add(p2);
	}
	//�ṩһ����������ʾ����Point����������p2��p1��p3��ת�۵�
	public LinkInfo(Point p1,Point p2,Point p3){
		//�ӵ�������ȥ
		points.add(p1);
		points.add(p2);
		points.add(p3);
		}
	//�ṩһ����������ʾ�ĸ�Point����������p2��p3��p1��p4��ת�۵�
		public LinkInfo(Point p1,Point p2,Point p3,Point p4){
			//�ӵ�������ȥ
			points.add(p1);
			points.add(p2);
			points.add(p3);
			points.add(p4);
			}
	//�������ӵļ���
	public List<Point> getLinkPoints(){
		return points;
	}
	
	
}
