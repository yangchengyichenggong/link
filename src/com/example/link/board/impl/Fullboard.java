package com.example.link.board.impl;

import java.util.ArrayList;
import java.util.List;

import com.example.link.View.Piece;
import com.example.link.board.AbstractBoard;
import com.example.link.object.GameConf;

//------------------------�������ף����գ�-----------------------------------------------------
public class Fullboard extends AbstractBoard {

	@Override
	protected List<Piece> createPieces(GameConf config, Piece[][] pieces) {
		//����һ��Piece���ϣ��ü��������ų�ʼ����Ϸʱ�����Piece����
		
		List<Piece>notNullPieces=new ArrayList<Piece>();
		
		for(int i=1;i<pieces.length-1;i++){
			for(int j=1;j<pieces[i].length-1;j++){
				//�ȹ���һ��Piece����ֻ��������Piece[][]�����е�����ֵ
				//����Ҫ��PieceImage���丸���������
				Piece piece=new Piece(i,j);
				//��ӵ�Piece������
				notNullPieces.add(piece);
			}
		}
		return notNullPieces;
	}

}
