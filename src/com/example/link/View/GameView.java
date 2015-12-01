package com.example.link.View;

import java.util.List;

import com.example.link.board.GameService;
import com.example.link.object.LinkInfo;
import com.example.link.util.ImageUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

/*
 * ������Ϸ��״̬���������ƽ����ϵķ���
 * */
public class GameView extends View{
	
	//��Ϸ�߼���ʵ����
	private GameService gameService;//--111--------------------------------------------------------
	
	//���浱ǰ�Ѿ���ѡ�еķ���
	private Piece selectedPiece;
	
	//������Ϣ����
	private LinkInfo linkInfo;//---------------------------------------------------------------
	private Paint paint;
	
	//ѡ�б�ʾ��ͼƬ����
	private Bitmap selectedIamge;
	
	public GameView(Context context,AttributeSet attres){
			super(context,attres);
			this.paint=new Paint();
		//���������ߵ���ɫ
			this.paint.setColor(Color.RED);
		//���������ߵĴ�ϸ
			this.paint.setStrokeWidth(9);
			this.selectedIamge=ImageUtil.getSelectImage(context);		
	}
		public void setLinkInfo(LinkInfo linkInfo){
			this.linkInfo=linkInfo;
		}
	
		public void setGameService(GameService gameService){
			this.gameService=gameService;
	}
	
		
		
	@Override
	//----------------------------�ص�  ����ͼƬ------------------------------------------------
	protected void onDraw(Canvas canvas){
		 super.onDraw(canvas);
		 if(this.gameService==null){
			 return;
		}
		
		Piece[][]pieces=gameService.getPieces();   //   222
		
		if(pieces!=null){
			//����pieces��ά����
			for(int i=0;i<pieces.length;i++){
				for(int j=0;j<pieces[i].length;j++){
					//�����ά�����и�Ԫ�ز�Ϊ�գ����з��飩����������黭����
					if(pieces[i][j]!=null){
						//�õ����piece����
						Piece piece=pieces[i][j];
						
						//���ݷ������Ͻ�x��y������Ʒ���
						
						canvas.drawBitmap(piece.getImage().getImage(),
								piece.getBeginX(), piece.getBeginY(),null);
						
					}
				}
			}
		}
		
		
		//�����ǰ������linkInfo���󣬼�������Ϣ
		if(this.linkInfo!=null){
			//����������
			drawLine(this.linkInfo,canvas);
			//����������linkInfo����
			this.linkInfo=null;
		}
		//����ѡ�б�ʶ ��ͼƬ
		if(this.selectedPiece!=null){
			canvas.drawBitmap(this.selectedIamge, this.selectedPiece.getBeginX(),
					this.selectedPiece.getBeginY(),null);
			
		}
		
	}
	
	
	//-------------------------------������������֮���������������--------------------------------------------------

	//����LinkInfo���������ߵķ���
	private void drawLine(LinkInfo linkInfo,Canvas canvas){
		//��ȡLinkInfo�з�װ�Ķ���
		List<Point>points=linkInfo.getLinkPoints();
		//���α���linkInfo�е�ÿ�����ӵ�
		for(int i=0;i<points.size()-1;i++){
			//��ȡ��ǰ���ӵ�����һ�����ӵ�
			Point currentPoint=points.get(i);
			Point nextPoint=points.get(i+1);
			//��������
			canvas.drawLine(currentPoint.x,currentPoint.y,nextPoint.x,nextPoint.y,this.paint);
		}
	}
	//���õ�ǰѡ�з���ķ���
	public void setSelectedPiece(Piece piece){
		this.selectedPiece=piece;
		
	}
	//��ʼ��Ϸ�ķ���
	public void startGame(){
		this.gameService.start();
		
		this.postInvalidate();
	}
	
	

	
	
	
}
