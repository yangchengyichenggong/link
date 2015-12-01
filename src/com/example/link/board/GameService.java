package com.example.link.board;

import com.example.link.View.Piece;
import com.example.link.object.LinkInfo;

public interface GameService {
	/*
	 * 控制游戏开始的方法,在GameView当中需要用
	 * */
	void start();
	
	/*
	 * 定义一个接口方法，用于返回一个二维数组
	 * */
	Piece[][]getPieces();
	
	/*
	 * 判断参数Piece[][]数组中是否还存在非空的Piece对象
	 * */
	boolean hasPieces();
	
	/*
	 * 根据鼠标的x坐标和Y坐标，查找出一个Piece对象
	 * 返回对应的Piece对象，没有返回null
	 * */
	Piece findPiece(float touchX,float touchY);
	/*
	 * 判断两个Piece是否可以相连，如果可以相连，则返回LinkInfo对象
	 * */
	LinkInfo link(Piece p1,Piece p2);
	
	
}
