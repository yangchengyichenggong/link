package com.example.link.board;

import com.example.link.View.Piece;
import com.example.link.object.LinkInfo;

public interface GameService {
	/*
	 * ������Ϸ��ʼ�ķ���,��GameView������Ҫ��
	 * */
	void start();
	
	/*
	 * ����һ���ӿڷ��������ڷ���һ����ά����
	 * */
	Piece[][]getPieces();
	
	/*
	 * �жϲ���Piece[][]�������Ƿ񻹴��ڷǿյ�Piece����
	 * */
	boolean hasPieces();
	
	/*
	 * ��������x�����Y���꣬���ҳ�һ��Piece����
	 * ���ض�Ӧ��Piece����û�з���null
	 * */
	Piece findPiece(float touchX,float touchY);
	/*
	 * �ж�����Piece�Ƿ������������������������򷵻�LinkInfo����
	 * */
	LinkInfo link(Piece p1,Piece p2);
	
	
}
