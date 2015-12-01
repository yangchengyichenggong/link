package com.example.link.board;

import java.util.List;

import com.example.link.View.Piece;
import com.example.link.View.PieceImage;
import com.example.link.object.GameConf;
import com.example.link.util.ImageUtil;

import android.R;
import android.util.Log;

public abstract class AbstractBoard {
		//����һ�����󷽷���������ȥʵ����
	protected abstract List<Piece> createPieces(GameConf config,Piece[][]pieces);
	
		public Piece[][]   create(GameConf config){
		//����Piece[][]����
			
			Piece[][]pieces=new Piece[config.getXSize()][config.getYSize()];
			
			/*for(int i=0;i<config.getXSize();i++){
				for(int j=0;j<config.getYSize();j++){
					pieces[i][j]=new Piece(i,j);
				}
			}*/
			
			//���طǿ�Piece���ϣ��ü���������ȥ����
			
			List<Piece>notNullpieces=createPieces(config,pieces);
			
			//���ݷǿ�Piece����ļ��ϴ�С��ȥȡͼƬ
		
			List<PieceImage>playImages=ImageUtil.getPlayImages(config.getContext(),
					notNullpieces.size());
			
			//����ͼƬ�Ŀ�߶�����ͬ��
			int imageWidth=playImages.get(0).getImage().getWidth();
			int imageHeight=playImages.get(0).getImage().getHeight();
			
			//�����ǿյ�Piece����
			for(int i=0;i<notNullpieces.size();i++){
				//���λ�ȡÿ��Piece����
				Piece piece=notNullpieces.get(i);
				
				piece.setImage(playImages.get(i));
				//����ÿ�����������Ͻǵ�x��y����
				piece.setBeginX(piece.getIndexX()*imageWidth+config.getBeginImageX());
				
				piece.setBeginY(piece.getIndexY()*imageHeight+config.getBeginImageY());
				//�����������뷽������Ķ�Ӧλ�ô�
				pieces[piece.getIndexX()][piece.getIndexY()]=piece;
			}
			return pieces;
	}
		
		
		
		
		
		
		
		
}
