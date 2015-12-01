package com.example.link.View;

import android.graphics.Point;

/*
 * ���飺ͼƬ����ά����id��ȷ��ͼƬ�Ƿ���ͬ����x y����
 * 
 * */
public class Piece {
	//���淽���������Ӧ��ͼƬ
		private PieceImage image;
		//�������ҽ�����
		private int beginX;
		
		private int beginY;
		//�ö�����Piece[][]������һ��ά���е�����ֵ
		private int indexX;
		
		private int indexY;
		
		public Piece(int indexX,int indexY){
			this.indexX=indexX;
			this.indexY=indexY;
			
		}

		public boolean isSameImage(Piece other){
			if(image==null){
				if(other.image!=null)
					return false;
			}
			//ֻҪPiece��װͼƬID��ͬ��������Ϊ����PIECE���
			return this.image.getImageId()==other.image.getImageId();
		}
		
		
		
		
		
		
		
		
	public PieceImage getImage() {
		return image;
	}


	public void setImage(PieceImage image) {
		this.image = image;
	}


	public int getBeginX() {
		return beginX;
	}


	public void setBeginX(int beginX) {
		this.beginX = beginX;
	}


	public int getBeginY() {
		return beginY;
	}


	public void setBeginY(int beginY) {
		this.beginY = beginY;
	}


	public int getIndexX() {
		return indexX;
	}


	public void setIndexX(int indexX) {
		this.indexX = indexX;
	}


	public int getIndexY() {
		return indexY;
	}


	public void setIndexY(int indexY) {
		this.indexY = indexY;
	}



	// ��ȡ��Piece������
	public Point getCenter()
	{
		return new Point(getImage().getImage().getWidth() / 2
			+ getBeginX(), getBeginY()
			+ getImage().getImage().getHeight() / 2);
	}	
	
	
	
	
	
	
	
	
}
