package com.example.link.View;

import android.graphics.Point;

/*
 * 方块：图片，二维数组id（确定图片是否相同），x y坐标
 * 
 * */
public class Piece {
	//保存方块对象所对应的图片
		private PieceImage image;
		//方块左右角坐标
		private int beginX;
		
		private int beginY;
		//该对象在Piece[][]数组中一二维度中的索引值
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
			//只要Piece封装图片ID相同，即可认为两个PIECE相等
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



	// 获取该Piece的中心
	public Point getCenter()
	{
		return new Point(getImage().getImage().getWidth() / 2
			+ getBeginX(), getBeginY()
			+ getImage().getImage().getHeight() / 2);
	}	
	
	
	
	
	
	
	
	
}
