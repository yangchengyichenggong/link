package com.example.link.board;

import java.util.List;

import com.example.link.View.Piece;
import com.example.link.View.PieceImage;
import com.example.link.object.GameConf;
import com.example.link.util.ImageUtil;

import android.R;
import android.util.Log;

public abstract class AbstractBoard {
		//定义一个抽象方法，让子类去实现它
	protected abstract List<Piece> createPieces(GameConf config,Piece[][]pieces);
	
		public Piece[][]   create(GameConf config){
		//创建Piece[][]数组
			
			Piece[][]pieces=new Piece[config.getXSize()][config.getYSize()];
			
			/*for(int i=0;i<config.getXSize();i++){
				for(int j=0;j<config.getYSize();j++){
					pieces[i][j]=new Piece(i,j);
				}
			}*/
			
			//返回非空Piece集合，该集合由子类去创建
			
			List<Piece>notNullpieces=createPieces(config,pieces);
			
			//根据非空Piece对象的集合大小来去取图片
		
			List<PieceImage>playImages=ImageUtil.getPlayImages(config.getContext(),
					notNullpieces.size());
			
			//所有图片的宽高都是相同的
			int imageWidth=playImages.get(0).getImage().getWidth();
			int imageHeight=playImages.get(0).getImage().getHeight();
			
			//遍历非空的Piece集合
			for(int i=0;i<notNullpieces.size();i++){
				//依次获取每个Piece对象
				Piece piece=notNullpieces.get(i);
				
				piece.setImage(playImages.get(i));
				//计算每个方法在左上角的x，y坐标
				piece.setBeginX(piece.getIndexX()*imageWidth+config.getBeginImageX());
				
				piece.setBeginY(piece.getIndexY()*imageHeight+config.getBeginImageY());
				//将方块对象放入方块数组的对应位置处
				pieces[piece.getIndexX()][piece.getIndexY()]=piece;
			}
			return pieces;
	}
		
		
		
		
		
		
		
		
}
