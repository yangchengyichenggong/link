package com.example.link.object;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Point;

public class LinkInfo {
	//创建集合保存连接点
	private List<Point>points=new ArrayList<Point>();
	
	//提供一个构造器，表示两个Point可以直接连接，没有转折点
	public LinkInfo(Point p1,Point p2){
		//加到集合中去
		points.add(p1);
		points.add(p2);
	}
	//提供一个构造器表示三个Point可以相连，p2是p1和p3的转折点
	public LinkInfo(Point p1,Point p2,Point p3){
		//加到集合中去
		points.add(p1);
		points.add(p2);
		points.add(p3);
		}
	//提供一个构造器表示四个Point可以相连，p2，p3是p1和p4的转折点
		public LinkInfo(Point p1,Point p2,Point p3,Point p4){
			//加到集合中去
			points.add(p1);
			points.add(p2);
			points.add(p3);
			points.add(p4);
			}
	//返回连接的集合
	public List<Point> getLinkPoints(){
		return points;
	}
	
	
}
