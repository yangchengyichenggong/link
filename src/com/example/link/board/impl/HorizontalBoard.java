package com.example.link.board.impl;

import java.util.ArrayList;
import java.util.List;

import com.example.link.View.Piece;
import com.example.link.board.AbstractBoard;
import com.example.link.object.GameConf;

public class HorizontalBoard extends AbstractBoard {
	@Override
	protected List<Piece> createPieces(GameConf config, Piece[][] pieces) {
		//创建一个Piece集合，该集合里面存放初始化游戏时所需的Piece对象
		
		List<Piece>notNullPieces=new ArrayList<Piece>();
		
		for(int i=0;i<pieces.length;i++){
			for(int j=0;j<pieces[i].length;j++){
				if(j%2==0){
					
				Piece piece=new Piece(i,j);
				//添加到Piece集合中
				notNullPieces.add(piece);
				}
			}
		}
		return notNullPieces;
	}
}
