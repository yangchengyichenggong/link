package com.example.link.board.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.example.link.View.Piece;
import com.example.link.board.AbstractBoard;
import com.example.link.board.GameService;
import com.example.link.object.GameConf;
import com.example.link.object.LinkInfo;

import android.graphics.Point;

public class GameServiceImpl implements GameService{

	//����һ��Piece[][]���飬ֻ�ṩgetter����
	private Piece[][]pieces;
	//��Ϸ���ö���
	private GameConf config;
	
	
	
	public GameServiceImpl(GameConf config){
		//����Ϸ�����ö������õ����൱��
		this.config=config;
	}
	
	@Override
	public void start() {
	//����һ��AbstractBoard����
		AbstractBoard board=null;
		Random random=new Random();
		//��ȡһ�������������֮0,1,2,3��ֵ
		int index=random.nextInt(4);
		//�������AbstractBoard������ʵ��
		switch(index){
		case 0:
			//0����VerticalBoard(����)
			board=new VerticalBoard();
			break;
		case 1:
			board=new HorizontalBoard();
			break;
		default:
		//Ĭ�Ϸ���FullBoard
			board=new Fullboard();
			break;
		}
		//��ʼ��Piece[][]����
		this.pieces=board.create(config);
	}
//--------------------------                     ----------��ȡpiece����-------------------------------------------------------------
	@Override
	public Piece[][] getPieces() {
		
		return this.pieces;
	}
	
	
//ʵ�ֽӿڵ�hasPieces�ķ���
	@Override
	public boolean hasPieces() {
		//����Piece[][]�����ÿ��Ԫ��
		for(int i=0;i<pieces.length;i++){
			for(int j=0;j<pieces[i].length;j++){
				//ֻҪ����һ������Ԫ�ز�Ϊnull��Ҳ���ǻ�ʣ�зǿյ�Piece����
				if(pieces[i][j]!=null){
					return true;
				}
			}
		}
		return false;
	}
//���ݴ������λ������Ӧ�ķ���
	@Override
	public Piece findPiece(float touchX, float touchY) {
		//���ڴ���Piece�����ʱ�򣬽�ÿ��Piece�Ŀ�ʼ�������
		//GameConf��ũ���õ�beginImageX/beginImageYֵ���������Ҫ��ȥ���ֵ
		int relativeX=(int)touchX-this.config.getBeginImageX();
		int relativeY=(int)touchY-this.config.getBeginImageY();
		//���������ĵط���board�еĵ�һ��ͼƬ�Ŀ�ʼX����Ϳ�ʼy����ҪС����û���ҵ���Ӧ�ķ���
		if(relativeX<0||relativeY<0){
			return null;
		}
		//��ȡrelativeX������Piece[][]�����еĵ�һά����
		//��һһ������Ϊû����ƫ�Ŀ�
		int indexX=getIndex(relativeX,GameConf.PIECE_WIDTH);
		
		int indexY=getIndex(relativeY,GameConf.PIECE_HEIGHT);
		
	
		
		if(indexX<0||indexY<0){
			return null;
		}
		//������ڻ�������鳤��
			if(indexX>=this.config.getXSize()||indexY>=this.config.getYSize()){
				return null;
			}
			
			
		return this.pieces[indexX][indexY];
	}

	
	
	@Override
	public LinkInfo link(Piece p1, Piece p2) {
		//����Piece��ͬһ������ѡ��ͬһ�鷽�飬����null
		if(p1.equals(p2)){
			return null;
		}
		//���p1��p2��ͼƬ����ͬ���򷵻�null
		if(!p1.isSameImage(p2)){
			return null;
		}
		//p2��p1����ߣ�����Ҫ����ִ�б�������������������
		if(p2.getIndexX()<p1.getIndexX()){
			return link(p2,p1);
		}
		//��ȡp1�����ĵ�
	Point p1Point=p1.getCenter();
		//��ȡp2�����ĵ�
		Point p2Point=p2.getCenter();
		//�������Piece��ͬһ��
		if(p1.getIndexY()==p2.getIndexY()){
			//��ͬһ�в���������
			if(!isXBlock(p1Point,p2Point,GameConf.PIECE_WIDTH)){
				return new LinkInfo(p1Point,p2Point);
			}
		}
		
	if(p1.getIndexX()==p2.getIndexX()){
			//��ͬһ�в���������
			if(!isYBlock(p1Point,p2Point,GameConf.PIECE_HEIGHT)){
			return new LinkInfo(p1Point,p2Point);
			}
	}
		
		//��һ��ת�۵�����
		//��ȡ2�����֮�������ĵ㣬��ֻ��һ��ת�۵�
		Point cornerPoint=getCornerPoint(p1Point,p2Point,
				GameConf.PIECE_WIDTH,GameConf.PIECE_HEIGHT);
		
		if(cornerPoint!=null){
		return (new LinkInfo(p1Point,cornerPoint,p2Point));
		}
		//��map��key��ŵ�һ��ת�۵㣬value��ŵڶ���ת�۵�	
		//map��size()˵���ж����ֿ������ӵķ�ʽ
		Map<Point,Point>turns=getLinkPoints(p1Point,p2Point,
				GameConf.PIECE_WIDTH,GameConf.PIECE_HEIGHT);
		
		if(turns.size()!=0){
		return getShortcut(p1Point,p2Point,turns,getDistance(p1Point,p2Point));
			
		}
		return null;
	}
	
	/*
	 * --------------------------������----------------------------------------------------
	 * */
	//���߷���������relative������������Piece[][]������ֵ��sizeΪÿ��ͼƬ�ĳ����
	private int getIndex(int relative,int size){
		//��ʾrelative����������
		int index=-1;
		//�������Ա߳���û������������ֵ��һ
		if(relative%size==0){
			index=relative/size-1;
		}else{
			index=relative/size;
		}
		return index;
	}
	
	/*
	 * ��һ��Point���󣬷����������ͨ��
	 * @param p
	 * @param pieceWidth piece ͼƬ�Ŀ�
	 * @param min �����������С�Ľ���
	 * @return ����Point��ߵ�ͨ��
	 * */
	private List<Point> getLeftChanel(Point p,int min,int pieceWidth){
		List<Point> result=new ArrayList<Point>();
		//��ȡ����ͨ������һ�����������������Ϊ PieceͼƬ��
		for(int i=p.x-pieceWidth;i>=min;i=i-pieceWidth){
			if(hasPiece(i,p.y)){
				return result;
			}
			result.add(new Point(i,p.y));
			
		}
			return result; 
	}
	
	/*
	 * ��һ��Point���󣬷��������ұ߱�ͨ��
	 * @param p
	 * @param pieceWidth piece ͼƬ�Ŀ�
	 * @param max �����������С�Ľ���
	 * @return ����Point�ұߵ�ͨ��
	 * */
	private List<Point> getRightChanel(Point p,int max,int pieceWidth){
		List<Point> result=new ArrayList<Point>();
		//��ȡ����ͨ������һ�������ұ���������Ϊ PieceͼƬ��
		for(int i=p.x+pieceWidth;i<=max;i=i+pieceWidth){
			if(hasPiece(i,p.y)){
				return result;
			}
			result.add(new Point(i,p.y));
			
		}
			return result; 
	}

	/*
	 * ��һ��Point���󣬷�����������ͨ��
	 * @param p
	 * @param pieceHeight piece ͼƬ�ĸ�
	 * @param min ���ϱ�������С�Ľ���
	 * @return ����Points�����ͨ��
	 * */
	private List<Point> getUpChanel(Point p,int min,int pieceHeight){
		List<Point> result=new ArrayList<Point>();
		//��ȡ����ͨ������һ�������ϱ���������Ϊ PieceͼƬ��
		for(int i=p.y-pieceHeight;i>=min;i=i-pieceHeight){
			if(hasPiece(p.x,i)){
				return result;
			}
			result.add(new Point(p.x,i));
			
		}
			return result; 
	}
	
	
	/*
	 * ��һ��Point���󣬷�����������ͨ��
	 * @param p
	 * @param pieceHeight piece ͼƬ�ĸ�
	 * @param max ���±��������Ľ���
	 * @return ����Points�����ͨ��
	 * */
	private List<Point> getDownChanel(Point p,int max,int pieceHeight){
		List<Point> result=new ArrayList<Point>();
		//��ȡ����ͨ������һ�������±���������Ϊ PieceͼƬ��
		for(int i=p.y+pieceHeight;i<=max;i=i+pieceHeight){
			if(hasPiece(p.x,i)){
				return result;
			}
			result.add(new Point(p.x,i));
			
		}
			return result; 
	}
	
	/*
	 * �ж�2��Y������ͬ�ĵ����֮���Ƿ����ϰ�����p1λ�������ұ���
	 * @param p1
	 * @param p2
	 * @param pieceWidth
	 * @return ����Piece֮�����ϰ�����true�����򷵻�false
	 * */
	private boolean isXBlock(Point p1,Point p2,int pieceWidth){
		if(p2.x<p1.x){
			return isXBlock(p2,p1,pieceWidth);
		}
		for(int i=p1.x+pieceWidth;i<p2.x;i=i+pieceWidth){
			if(hasPiece(i,p1.y)){
				//���ϰ�
				return true;
			}
		}
		return false;
	}
	
	/*
	 * �ж�2��X������ͬ�ĵ����֮���Ƿ����ϰ�����p1λ�������±���
	 * @param p1
	 * @param p2
	 * @param pieceHeight
	 * @return ����Piece֮�����ϰ�����true�����򷵻�false
	 * */
	private boolean isYBlock(Point p1,Point p2,int pieceHeight){
		if(p2.y<p1.y){
			return isYBlock(p2,p1,pieceHeight);
		}
		for(int i=p1.y+pieceHeight;i<p2.y;i=i+pieceHeight){
			if(hasPiece(p1.x,i)){
				//���ϰ�
				return true;
			}
		}
		return false;
	}
	
	/*
	 * ��������ͨ����������ǵĽ���
	 * @param p1Chanel ��һ��ͨ��
	 * @param p2Chanel �ڶ���ͨ��
	 * @return ͨ���н��㣬���ؽ��㣬���򷵻�null
	 * */

	private Point getWrapPoint(List<Point>p1Chanel,List<Point>p2Chanel){
		
		for(int i=0;i<p1Chanel.size();i++){
			Point temp1=p1Chanel.get(i);
			for(int j=0;j<p2Chanel.size();j++){
				Point temp2=p2Chanel.get(j);
				if(temp1.equals(temp2)){
					return temp1;
				}
					
				}
			}
		return null;
		}
		
	/*
	 * @param point1 ��һ����
	 * @param point2 �ڶ�����
	 * @return ��������ͬһ�л���ͬһ�е�������ֱ�����ӵ�
	 * */
		private Point getCornerPoint(Point point1,Point point2,int pieceWidth,int pieceHeight){
			//���ж����������λ�ù�ϵ
			//point2�� point1�����ʱ
			if(isLeftUp(point1,point2)||isLeftDown(point1,point2)){
				//��������
				return getCornerPoint(point2,point1,pieceWidth,pieceHeight);
			}
			//��ȡp1���ң����ϣ����µ�����ͨ��
			List<Point>point1RightChanel=getRightChanel(point1,point2.x,pieceWidth);
			List<Point>point1UpChanel=getUpChanel(point1,point2.y,pieceHeight);
			List<Point>point1DownChanel=getDownChanel(point1,point2.y,pieceHeight);
			
			//��ȡp2�������ϣ����µ�����ͨ��			
			List<Point>point2UpChanel=getUpChanel(point2,point1.y,pieceHeight);
			List<Point>point2DownChanel=getDownChanel(point2,point1.y,pieceHeight);			
			List<Point>point2LeftChanel=getLeftChanel(point2,point1.x,pieceWidth);
			
			if(isRightUp(point1,point2)){
				//point2��point1�����Ͻ�
				Point linkPoint1=getWrapPoint(point1RightChanel,point2DownChanel);
				Point linkPoint2=getWrapPoint(point1UpChanel,point2LeftChanel);
				//��������һ�����㣬���ô�н��㣬null
				return (linkPoint1==null)?linkPoint2:linkPoint1;
			}
			if(isRightDown(point1,point2)){
				//point2��point1�����½�
				Point linkPoint1=getWrapPoint(point1DownChanel,point2LeftChanel);
				Point linkPoint2=getWrapPoint(point1RightChanel,point2UpChanel);
				//��������һ�����㣬���ô�н��㣬null
				return (linkPoint1==null)?linkPoint2:linkPoint1;
			}
			return null;
		}
	
		/*
		 * �ж�point2
		 * �Ƿ���point1�����Ͻ�
		 * */
	private boolean isLeftUp(Point point1,Point point2){
		return (point2.x<point1.x&point2.y<point1.y);
	}
		/*
		 * ����
		 * */
	private boolean isLeftDown(Point point1,Point point2){
		return (point2.x<point1.x&point2.y>point1.y);
	}
		/*
		 * ����
		 * */
	private boolean isRightUp(Point point1,Point point2){
		return (point2.x>point1.x&point2.y<point1.y);
	}	
		/*
		 * ����
		 * */
	private boolean isRightDown(Point point1,Point point2){
		return (point2.x>point1.x&point2.y>point1.y);
	}
	
	/*
	 * ��ȡ����ת�۵�����
	 * @param point1
	 * @param point2
	 * @return Map �����ÿһ��key_value�Դ���һ�����ӷ�ʽ����key��value�ֱ�����һ�����ڶ������ӵ�  
	 * */
	
	     private Map<Point,Point> getLinkPoints(Point point1,Point point2,
	    		 int pieceWidth,int pieceHeight){
	    	 Map<Point,Point> result=new HashMap<Point,Point>();
	    	 //��ȡ��point1λ���ĵ����ϣ����ң����µ�ͨ��
	    	 List<Point>p1UpChanel=getUpChanel(point1,point2.y,pieceHeight);
	    	 List<Point>p1RightChanel=getRightChanel(point1,point2.x,pieceWidth);			
			 List<Point>p1DownChanel=getDownChanel(point1,point2.y,pieceHeight);
			 
	    	 //��ȡ��point2Ϊ���ĵ����£���������
			 
			 List<Point>p2DownChanel=getDownChanel(point2,point1.y,pieceHeight);			
			 List<Point>p2LeftChanel=getLeftChanel(point2,point1.x,pieceWidth);
			 List<Point>p2UpChanel=getUpChanel(point2,point1.y,pieceHeight);
			 
			 //��ȡBoard�����߶�,���
			 int heightMax=(this.config.getYSize()+1)*pieceHeight+
					 this.config.getBeginImageY();
			 int  widthMax=(this.config.getXSize()+1)*pieceWidth+
					 this.config.getBeginImageX();
			 
			 //��ȷ�������Ĺ�ϵ,point2��point1�����ϻ�����
			 if(isLeftUp(point1,point2)||isLeftDown(point1,point2)){
				 //������λ
				 return getLinkPoints(point2,point1,pieceWidth,pieceHeight);
			 }
			 
			 //p1,p2λ�� (ͬһ��)     ����ֱ������
			 if(point1.y==point2.y){
				 
				 //��ͬһ�� ���ϱ���        ��p1Ϊ���ı�����ȡ����
				 p1UpChanel=getUpChanel(point1,0,pieceHeight);
				 //��p2Ϊ���ı�����ȡ����
				 p2UpChanel=getUpChanel(point2,0,pieceHeight);
				 
		 Map<Point,Point>upLinkPoints=getXLinkPoints(p1UpChanel,p2UpChanel,pieceHeight);
				 
				 //���±�����������Board���з���ĵط����ı߿���p1���ĵ����±�����ȡ�㼯��
				 p1DownChanel=getDownChanel(point1,heightMax,pieceWidth);
				 //��p2Ϊ���ı�����ȡ����
				 p2DownChanel=getDownChanel(point2,heightMax,pieceWidth);
				 
		 Map<Point,Point>downLinkPoints=getXLinkPoints(p1DownChanel,
						 p2DownChanel,pieceHeight);
				 result.putAll(upLinkPoints);
				 result.putAll(downLinkPoints);
			 }
			 
			 
			 //p1,p2λ��(ͬһ��)����ֱ������
			 if(point1.x==point2.x){
				 //ͬһ�� ( �������  ) ��p1λ�������������ȡ�㼯��
				 List<Point>p1LeftChanel=getLeftChanel(point1,0,pieceHeight);
				 //��p2λ�������������ȡ�㼯��
				 p2LeftChanel=getLeftChanel(point2,0,pieceHeight);
				 
		 Map<Point,Point>leftLinkPoints=getYLinkPoints(p1LeftChanel,
				 p2LeftChanel,pieceWidth);
		 
		 		//���ұ��������ó���Board�ı߿��з���ĵط���
		 		//��p1�����ĵ����ұ�����ȡ����
		 	p1RightChanel=getRightChanel(point1,widthMax,pieceWidth);
List<Point> p2RightChanel=getRightChanel(point2,widthMax,pieceWidth);

		Map<Point,Point>rightLinkPoints=getYLinkPoints(p1RightChanel,
				p2RightChanel,pieceWidth);
		
				result.putAll(leftLinkPoints);
				result.putAll(rightLinkPoints);
			 }
			 
			 
			 
			 
//-------------------------------���Ͻǣ�6�������------�ص�----------------------------------------
	
		//point2λ��point1�����Ͻ�	 
			 if(isRightUp(point1,point2)){
				 
				 //��ȡpoint1���ϱ�����point2���±���ʱ��  ���� �������ӵģ����������յ��Ǻ����
			Map<Point,Point>upDownLinkPoints=getXLinkPoints(p1UpChanel,p2DownChanel,pieceWidth);
				 //point1���ң�point2��������
			Map<Point,Point>rightLeftPoints=getYLinkPoints(p1RightChanel,p2LeftChanel,pieceHeight);
			 	
			 
			 	//��ȡ��p1Ϊ���ĵ�����ͨ��
			 p1UpChanel=getUpChanel(point1,0,pieceHeight);
			 	//��ȡ��p2λ���ĵ�����ͨ��
			 p2UpChanel=getUpChanel(point2,0,pieceHeight);
			 	//��ȡ���ϣ����ϵ����ӵ�
			Map<Point,Point>upUpLinkPonts=getXLinkPoints(p1UpChanel,p2UpChanel,pieceWidth);
			 
			
				//��ȡ��p1Ϊ���ĵ����¶�ͨ��
			 p1DownChanel=getDownChanel(point1,heightMax,pieceHeight);
			 	//��ȡ��p2Ϊ���ĵ����¶�ͨ��
			 p2DownChanel=getDownChanel(point2,heightMax,pieceHeight);
			Map<Point,Point>downDownLinkPonts=getXLinkPoints(p1DownChanel,p2DownChanel,pieceWidth);
			
			
				//��ȡp1��p2����
			  p1RightChanel=getRightChanel(point1,widthMax,pieceWidth);
   List<Point>p2RightChanel=getRightChanel(point2,widthMax,pieceWidth);
   			Map<Point,Point>rightRightLinkPonts=getYLinkPoints(p1RightChanel,p2RightChanel,
   					pieceHeight);
   			
   			
   				//���� ����
   			  List<Point>p1LeftChanel=getLeftChanel(point1,0,pieceWidth);
   			  			 p2LeftChanel=getLeftChanel(point2,0,pieceWidth);
   			Map<Point,Point>letfLeftLinkPonts=getYLinkPoints(p1LeftChanel,p2LeftChanel,pieceHeight);
   			
   			result.putAll(upDownLinkPoints);
   			result.putAll(rightLeftPoints);
   			result.putAll(upUpLinkPonts);
   			result.putAll(downDownLinkPonts);
   			result.putAll(rightRightLinkPonts);
   			result.putAll(letfLeftLinkPonts);
			 }
			 
			 
			//=-------------------------���½�------------------------�ص�----------------------- 
     if(isRightDown(point1,point2)){
				 
				 //��ȡpoint1���±�����point2���ϱ���ʱ��  ���� �������ӵģ����������յ��Ǻ����
			Map<Point,Point>downUpLinkPoints=getXLinkPoints(p1DownChanel,p2UpChanel,pieceWidth);
				 //point1���ң�point2��������
			Map<Point,Point>rightLeftPoints=getYLinkPoints(p1RightChanel,p2LeftChanel,pieceHeight);
			 	
			 
			 	//��ȡ��p1Ϊ���ĵ�����ͨ��
			 p1UpChanel=getUpChanel(point1,0,pieceHeight);
			 	//��ȡ��p2λ���ĵ�����ͨ��
			 p2UpChanel=getUpChanel(point2,0,pieceHeight);
			 	//��ȡ���ϣ����ϵ����ӵ�
			Map<Point,Point>upUpLinkPonts=getXLinkPoints(p1UpChanel,p2UpChanel,pieceWidth);
			 
			
				//��ȡ��p1Ϊ���ĵ����¶�ͨ��
			 p1DownChanel=getDownChanel(point1,heightMax,pieceHeight);
			 	//��ȡ��p2Ϊ���ĵ����¶�ͨ��
			 p2DownChanel=getDownChanel(point2,heightMax,pieceHeight);
			Map<Point,Point>downDownLinkPonts=getXLinkPoints(p1DownChanel,p2DownChanel,pieceWidth);
			
			
				//��ȡp1��p2����
			  p1RightChanel=getRightChanel(point1,widthMax,pieceWidth);
   List<Point>p2RightChanel=getRightChanel(point2,widthMax,pieceWidth);
   			Map<Point,Point>rightRightLinkPonts=getYLinkPoints(p1RightChanel,p2RightChanel,
   					pieceHeight);
   			
   			
   				//���� ����
   			  List<Point>p1LeftChanel=getLeftChanel(point1,0,pieceWidth);
   			  			 p2LeftChanel=getLeftChanel(point2,0,pieceWidth);
   			Map<Point,Point>letfLeftLinkPonts=getYLinkPoints(p1LeftChanel,p2LeftChanel,pieceHeight);
   			result.putAll(downUpLinkPoints);
   			result.putAll(rightLeftPoints);
   			result.putAll(upUpLinkPonts);
   			result.putAll(downDownLinkPonts);
   			result.putAll(letfLeftLinkPonts);
   			result.putAll(rightRightLinkPonts);
     }
     return result;
	     }
	
	
	 
	     
	     /*
	      * �����������ϣ����жϵ�һ�����ϵ�X��������һ�����ϵ�Ԫ��������ͬ
	      * �����ͬ������ͬһ�У����ж��Ƿ����ϰ���û�мӵ�Map��
	      * @param p1Chanel
	      * @      p2Chanel
	      * @param pieceHeight
	      * @return 
	      * */
	     
	     private Map<Point,Point> getYLinkPoints(List<Point>p1Chanel,
	    		 List<Point>p2Chanel,int pieceHeight){
	    	 Map<Point,Point>result=new HashMap<Point,Point>();
	    	 for(int i=0;i<p1Chanel.size();i++){
	    		 Point temp1=p1Chanel.get(i);
	    		 for(int j=0;j<p2Chanel.size();j++){
	    			 Point temp2=p2Chanel.get(j);
	    			 //���X������ͬ����ͬһ�У�
	    			 if(temp1.x==temp2.x){
	    				 //û���ϰ����ŵ�map��ȥ
	    				 if(!isYBlock(temp1,temp2,pieceHeight)){
	    					 result.put(temp1, temp2);
	    				 }
	    			 }
	    		 }
	    	 }
	    	 return result;
	     }
	     
	     /*
	      * �������������ϣ����жϵ�һ�����ϵ�Ԫ�ص�Y��������һ�������е�Ԫ��Y������ͬ,����
	      * */
	     private Map<Point,Point>getXLinkPoints(List<Point>p1Chanel,
	    		 List<Point>p2Chanel,int pieceWidth){
	    	 Map<Point,Point>result=new HashMap<Point,Point>();
	    	 for(int i=0;i<p1Chanel.size();i++){
	    		 Point temp1=p1Chanel.get(i);
	    		 for(int j=0;j<p2Chanel.size();j++){
	    			 Point temp2=p2Chanel.get(j);
	    			 //���X������ͬ����ͬһ�У�
	    			 if(temp1.y==temp2.y){
	    				 //û���ϰ����ŵ�map��ȥ
	    				 if(!isYBlock(temp1,temp2,pieceWidth)){
	    					 result.put(temp1, temp2);
	    				 }
	    			 }
	    		 }
	    	 }
	    	 return result;
	     }
	     
	/*
	 * ��ȡp1��p2֮����̵�������Ϣ
	 * turns ��ת�۵��map
	 *  shortDistance
	 *  return p1��p2֮����̵�������Ϣ
	 *  
	 * */
	     private LinkInfo getShortcut(Point p1,Point p2,Map<Point,Point>turns,int shortDistance){
	    	 List<LinkInfo> infos=new ArrayList<LinkInfo>();
	    	 //�������Map
	    	 for(Point point1:turns.keySet()){
	    		 Point point2=turns.get(point1);
	    		 //��ת�۵���ѡ����װ��LinkInfo���󣬷ŵ�List������
	    		 infos.add(new LinkInfo(p1,point1,point2,p2));
	    		 }
	    	 return getShortcut(infos,shortDistance);
	     }
	     
	     /*
	      * ��infos�л�ȡ��������̵��Ǹ�Linkinfo����
	      * @param infos
	      * @return ��������̵��Ǹ�LinkInfo����
	      * */
	     private LinkInfo getShortcut(List<LinkInfo>infos,int shortDistance){
	    	 int temp1=0;
	    	 LinkInfo result=null;
	    	 
	    	 for(int i=0;i<infos.size();i++){
	    		 LinkInfo info=infos.get(i);
	    		 //�������������ܾ���
	    		 int distance=countAll(info.getLinkPoints());
	    		 //��ѭ����һ�������temp1����
	    		 if(i==0){
	    			 temp1=distance-shortDistance;
	    			 result=info;
	    		 }
	    		 //�����һ��ѭ����ֵ��temp1�Ļ�С�����õ�ǰ��ֵ��Ϊtemp1
	    		 if(distance-shortDistance<temp1){
	    			 temp1=distance-shortDistance;
	    			 result=info;
	    		 }
	    	 }
	    	 return result;
	     }
	     
	     /*
	      * ����List<Point>�����еľ����ܺ�
	      * 
	      * */
	     private int countAll(List<Point>points){
	    	 int result=0;
	    	 for(int i=0;i<points.size()-1;i++){
	    		 //��ȡ��i����
	    		 Point point1=points.get(i);
	    		 //��ȡ��i+1����
	    		 Point point2=points.get(i+1);
	    		 //�����i�������i+1����ľ��룬����ӵ��ܾ�����
	    		 result+=getDistance(point1,point2);
	    		 
	    		 
	    	 }
	    	return result; 
	     }
	     
	    /*
	     * ��ȡ����LinkPoint֮����̵ľ���
	     * return ����������ܺ�
	     * */ 
	     private int getDistance(Point p1,Point p2){
	    	 int xDistance=Math.abs(p1.x-p2.x);
	    	 int yDistance=Math.abs(p1.y-p2.y);
	    	 return xDistance+yDistance;
	     }
	     
	 	/**
	 	 * �ж�GamePanel�е�x, y�������Ƿ���Piece����
	 	 * 
	 	 * @param x
	 	 * @param y
	 	 * @return true ��ʾ�и�������piece���� false ��ʾû��
	 	 */
	 	private boolean hasPiece(int x, int y)
	 	{
	 		if (findPiece(x, y) == null)
	 			return false;
	 		return true;
	 	}
     }
