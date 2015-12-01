package com.example.link.board.impl;

import java.util.ArrayList;
import java.util.List;

import com.example.link.View.Piece;
import com.example.link.board.AbstractBoard;
import com.example.link.object.GameConf;

public class HorizontalBoard extends AbstractBoard {
	@Override
	protected List<Piece> createPieces(GameConf config, Piece[][] pieces) {
		//����һ��Piece���ϣ��ü��������ų�ʼ����Ϸʱ�����Piece����
		
		List<Piece>notNullPieces=new ArrayList<Piece>();
		
		for(int i=0;i<pieces.length;i++){
			for(int j=0;j<pieces[i].length;j++){
				if(j%2==0){
					
				Piece piece=new Piece(i,j);
				//��ӵ�Piece������
				notNullPieces.add(piece);
				}
			}
		}
		return notNullPieces;
	}
}
