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

	//定义一个Piece[][]数组，只提供getter方法
	private Piece[][]pieces;
	//游戏配置对象
	private GameConf config;
	
	
	
	public GameServiceImpl(GameConf config){
		//将游戏的配置对象设置到本类当中
		this.config=config;
	}
	
	@Override
	public void start() {
	//定义一个AbstractBoard对象
		AbstractBoard board=null;
		Random random=new Random();
		//获取一个随机数，可趋之0,1,2,3的值
		int index=random.nextInt(4);
		//随机生成AbstractBoard的子类实例
		switch(index){
		case 0:
			//0返回VerticalBoard(竖向)
			board=new VerticalBoard();
			break;
		case 1:
			board=new HorizontalBoard();
			break;
		default:
		//默认返回FullBoard
			board=new Fullboard();
			break;
		}
		//初始化Piece[][]数组
		this.pieces=board.create(config);
	}
//--------------------------                     ----------获取piece集合-------------------------------------------------------------
	@Override
	public Piece[][] getPieces() {
		
		return this.pieces;
	}
	
	
//实现接口的hasPieces的方法
	@Override
	public boolean hasPieces() {
		//遍历Piece[][]数组的每个元素
		for(int i=0;i<pieces.length;i++){
			for(int j=0;j<pieces[i].length;j++){
				//只要任意一个数组元素不为null，也就是还剩有非空的Piece对象
				if(pieces[i][j]!=null){
					return true;
				}
			}
		}
		return false;
	}
//根据触碰点的位置找相应的方块
	@Override
	public Piece findPiece(float touchX, float touchY) {
		//由于创建Piece对象的时候，将每个Piece的开始坐标加了
		//GameConf蔗农设置的beginImageX/beginImageY值，因此这里要减去这个值
		int relativeX=(int)touchX-this.config.getBeginImageX();
		int relativeY=(int)touchY-this.config.getBeginImageY();
		//如果鼠标点击的地方比board中的第一张图片的开始X坐标和开始y坐标要小，即没有找到相应的方块
		if(relativeX<0||relativeY<0){
			return null;
		}
		//获取relativeX坐标在Piece[][]数组中的第一维索引
		//第一一个参数为没长相偏的宽
		int indexX=getIndex(relativeX,GameConf.PIECE_WIDTH);
		
		int indexY=getIndex(relativeY,GameConf.PIECE_HEIGHT);
		
	
		
		if(indexX<0||indexY<0){
			return null;
		}
		//如果大于或等于数组长度
			if(indexX>=this.config.getXSize()||indexY>=this.config.getYSize()){
				return null;
			}
			
			
		return this.pieces[indexX][indexY];
	}

	
	
	@Override
	public LinkInfo link(Piece p1, Piece p2) {
		//两个Piece是同一个，即选中同一块方块，返回null
		if(p1.equals(p2)){
			return null;
		}
		//如果p1和p2的图片不相同，则返回null
		if(!p1.isSameImage(p2)){
			return null;
		}
		//p2在p1的左边，则需要重新执行本方法，两个参数互掉
		if(p2.getIndexX()<p1.getIndexX()){
			return link(p2,p1);
		}
		//获取p1的中心点
	Point p1Point=p1.getCenter();
		//获取p2的中心点
		Point p2Point=p2.getCenter();
		//如果两个Piece在同一行
		if(p1.getIndexY()==p2.getIndexY()){
			//在同一行并可以相连
			if(!isXBlock(p1Point,p2Point,GameConf.PIECE_WIDTH)){
				return new LinkInfo(p1Point,p2Point);
			}
		}
		
	if(p1.getIndexX()==p2.getIndexX()){
			//在同一列并可以相连
			if(!isYBlock(p1Point,p2Point,GameConf.PIECE_HEIGHT)){
			return new LinkInfo(p1Point,p2Point);
			}
	}
		
		//有一个转折点的情况
		//获取2个点的之间相连的点，即只有一个转折点
		Point cornerPoint=getCornerPoint(p1Point,p2Point,
				GameConf.PIECE_WIDTH,GameConf.PIECE_HEIGHT);
		
		if(cornerPoint!=null){
		return (new LinkInfo(p1Point,cornerPoint,p2Point));
		}
		//该map的key存放第一个转折点，value存放第二个转折点	
		//map的size()说明有多少种可以连接的方式
		Map<Point,Point>turns=getLinkPoints(p1Point,p2Point,
				GameConf.PIECE_WIDTH,GameConf.PIECE_HEIGHT);
		
		if(turns.size()!=0){
		return getShortcut(p1Point,p2Point,turns,getDistance(p1Point,p2Point));
			
		}
		return null;
	}
	
	/*
	 * --------------------------工具类----------------------------------------------------
	 * */
	//工具方法，根据relative坐标计算相对于Piece[][]的索引值，size为每张图片的长或宽
	private int getIndex(int relative,int size){
		//表示relative不再数组中
		int index=-1;
		//让坐标以边长，没有余数，索引值减一
		if(relative%size==0){
			index=relative/size-1;
		}else{
			index=relative/size;
		}
		return index;
	}
	
	/*
	 * 给一个Point对象，返回他的左边通道
	 * @param p
	 * @param pieceWidth piece 图片的宽
	 * @param min 向左遍历是最小的界限
	 * @return 给定Point左边的通道
	 * */
	private List<Point> getLeftChanel(Point p,int min,int pieceWidth){
		List<Point> result=new ArrayList<Point>();
		//获取向左通道，由一个点向左遍历，步长为 Piece图片宽
		for(int i=p.x-pieceWidth;i>=min;i=i-pieceWidth){
			if(hasPiece(i,p.y)){
				return result;
			}
			result.add(new Point(i,p.y));
			
		}
			return result; 
	}
	
	/*
	 * 给一个Point对象，返回他的右边边通道
	 * @param p
	 * @param pieceWidth piece 图片的宽
	 * @param max 向左遍历是最小的界限
	 * @return 给定Point右边的通道
	 * */
	private List<Point> getRightChanel(Point p,int max,int pieceWidth){
		List<Point> result=new ArrayList<Point>();
		//获取向右通道，由一个点向右遍历，步长为 Piece图片宽
		for(int i=p.x+pieceWidth;i<=max;i=i+pieceWidth){
			if(hasPiece(i,p.y)){
				return result;
			}
			result.add(new Point(i,p.y));
			
		}
			return result; 
	}

	/*
	 * 给一个Point对象，返回他的上面通道
	 * @param p
	 * @param pieceHeight piece 图片的高
	 * @param min 向上遍历是最小的界限
	 * @return 给定Points上面的通道
	 * */
	private List<Point> getUpChanel(Point p,int min,int pieceHeight){
		List<Point> result=new ArrayList<Point>();
		//获取向上通道，由一个点向上遍历，步长为 Piece图片宽
		for(int i=p.y-pieceHeight;i>=min;i=i-pieceHeight){
			if(hasPiece(p.x,i)){
				return result;
			}
			result.add(new Point(p.x,i));
			
		}
			return result; 
	}
	
	
	/*
	 * 给一个Point对象，返回他的下面通道
	 * @param p
	 * @param pieceHeight piece 图片的高
	 * @param max 向下遍历是最大的界限
	 * @return 给定Points下面的通道
	 * */
	private List<Point> getDownChanel(Point p,int max,int pieceHeight){
		List<Point> result=new ArrayList<Point>();
		//获取向下通道，由一个点向下遍历，步长为 Piece图片宽
		for(int i=p.y+pieceHeight;i<=max;i=i+pieceHeight){
			if(hasPiece(p.x,i)){
				return result;
			}
			result.add(new Point(p.x,i));
			
		}
			return result; 
	}
	
	/*
	 * 判断2个Y坐标相同的点对象之间是否有障碍，以p1位中心向右遍历
	 * @param p1
	 * @param p2
	 * @param pieceWidth
	 * @return 两个Piece之间有障碍返回true，否则返回false
	 * */
	private boolean isXBlock(Point p1,Point p2,int pieceWidth){
		if(p2.x<p1.x){
			return isXBlock(p2,p1,pieceWidth);
		}
		for(int i=p1.x+pieceWidth;i<p2.x;i=i+pieceWidth){
			if(hasPiece(i,p1.y)){
				//有障碍
				return true;
			}
		}
		return false;
	}
	
	/*
	 * 判断2个X坐标相同的点对象之间是否有障碍，以p1位中心向下遍历
	 * @param p1
	 * @param p2
	 * @param pieceHeight
	 * @return 两个Piece之间有障碍返回true，否则返回false
	 * */
	private boolean isYBlock(Point p1,Point p2,int pieceHeight){
		if(p2.y<p1.y){
			return isYBlock(p2,p1,pieceHeight);
		}
		for(int i=p1.y+pieceHeight;i<p2.y;i=i+pieceHeight){
			if(hasPiece(p1.x,i)){
				//有障碍
				return true;
			}
		}
		return false;
	}
	
	/*
	 * 遍历两个通道，获得他们的交点
	 * @param p1Chanel 第一个通道
	 * @param p2Chanel 第二个通道
	 * @return 通道有交点，返回交点，否则返回null
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
	 * @param point1 第一个点
	 * @param point2 第二个点
	 * @return 两个不在同一行或者同一列的坐标点的直角连接点
	 * */
		private Point getCornerPoint(Point point1,Point point2,int pieceWidth,int pieceHeight){
			//先判断这两个点的位置关系
			//point2在 point1的左边时
			if(isLeftUp(point1,point2)||isLeftDown(point1,point2)){
				//参数互换
				return getCornerPoint(point2,point1,pieceWidth,pieceHeight);
			}
			//获取p1向右，向上，向下的三个通道
			List<Point>point1RightChanel=getRightChanel(point1,point2.x,pieceWidth);
			List<Point>point1UpChanel=getUpChanel(point1,point2.y,pieceHeight);
			List<Point>point1DownChanel=getDownChanel(point1,point2.y,pieceHeight);
			
			//获取p2向左，向上，向下的三个通道			
			List<Point>point2UpChanel=getUpChanel(point2,point1.y,pieceHeight);
			List<Point>point2DownChanel=getDownChanel(point2,point1.y,pieceHeight);			
			List<Point>point2LeftChanel=getLeftChanel(point2,point1.x,pieceWidth);
			
			if(isRightUp(point1,point2)){
				//point2在point1的右上角
				Point linkPoint1=getWrapPoint(point1RightChanel,point2DownChanel);
				Point linkPoint2=getWrapPoint(point1UpChanel,point2LeftChanel);
				//返回其中一个交点，如果么有交点，null
				return (linkPoint1==null)?linkPoint2:linkPoint1;
			}
			if(isRightDown(point1,point2)){
				//point2在point1的右下角
				Point linkPoint1=getWrapPoint(point1DownChanel,point2LeftChanel);
				Point linkPoint2=getWrapPoint(point1RightChanel,point2UpChanel);
				//返回其中一个交点，如果么有交点，null
				return (linkPoint1==null)?linkPoint2:linkPoint1;
			}
			return null;
		}
	
		/*
		 * 判断point2
		 * 是否在point1的左上角
		 * */
	private boolean isLeftUp(Point point1,Point point2){
		return (point2.x<point1.x&point2.y<point1.y);
	}
		/*
		 * 左下
		 * */
	private boolean isLeftDown(Point point1,Point point2){
		return (point2.x<point1.x&point2.y>point1.y);
	}
		/*
		 * 右上
		 * */
	private boolean isRightUp(Point point1,Point point2){
		return (point2.x>point1.x&point2.y<point1.y);
	}	
		/*
		 * 右下
		 * */
	private boolean isRightDown(Point point1,Point point2){
		return (point2.x>point1.x&point2.y>point1.y);
	}
	
	/*
	 * 获取两个转折点的情况
	 * @param point1
	 * @param point2
	 * @return Map 对象的每一个key_value对代表一种连接方式其中key，value分别代表第一个，第二个连接点  
	 * */
	
	     private Map<Point,Point> getLinkPoints(Point point1,Point point2,
	    		 int pieceWidth,int pieceHeight){
	    	 Map<Point,Point> result=new HashMap<Point,Point>();
	    	 //获取以point1位中心的向上，向右，向下的通道
	    	 List<Point>p1UpChanel=getUpChanel(point1,point2.y,pieceHeight);
	    	 List<Point>p1RightChanel=getRightChanel(point1,point2.x,pieceWidth);			
			 List<Point>p1DownChanel=getDownChanel(point1,point2.y,pieceHeight);
			 
	    	 //获取以point2为中心的向下，向左，向上
			 
			 List<Point>p2DownChanel=getDownChanel(point2,point1.y,pieceHeight);			
			 List<Point>p2LeftChanel=getLeftChanel(point2,point1.x,pieceWidth);
			 List<Point>p2UpChanel=getUpChanel(point2,point1.y,pieceHeight);
			 
			 //获取Board的最大高度,宽度
			 int heightMax=(this.config.getYSize()+1)*pieceHeight+
					 this.config.getBeginImageY();
			 int  widthMax=(this.config.getXSize()+1)*pieceWidth+
					 this.config.getBeginImageX();
			 
			 //先确定两个的关系,point2在point1的左上或左下
			 if(isLeftUp(point1,point2)||isLeftDown(point1,point2)){
				 //参数换位
				 return getLinkPoints(point2,point1,pieceWidth,pieceHeight);
			 }
			 
			 //p1,p2位于 (同一行)     不能直接相连
			 if(point1.y==point2.y){
				 
				 //在同一行 向上遍历        以p1为中心遍历获取集合
				 p1UpChanel=getUpChanel(point1,0,pieceHeight);
				 //以p2为中心遍历获取集合
				 p2UpChanel=getUpChanel(point2,0,pieceHeight);
				 
		 Map<Point,Point>upLinkPoints=getXLinkPoints(p1UpChanel,p2UpChanel,pieceHeight);
				 
				 //向下遍历，不超过Board（有方块的地方）的边框，以p1中心点向下遍历获取点集合
				 p1DownChanel=getDownChanel(point1,heightMax,pieceWidth);
				 //以p2为中心遍历获取集合
				 p2DownChanel=getDownChanel(point2,heightMax,pieceWidth);
				 
		 Map<Point,Point>downLinkPoints=getXLinkPoints(p1DownChanel,
						 p2DownChanel,pieceHeight);
				 result.putAll(upLinkPoints);
				 result.putAll(downLinkPoints);
			 }
			 
			 
			 //p1,p2位于(同一列)不能直接相连
			 if(point1.x==point2.x){
				 //同一列 ( 向左遍历  ) 以p1位中心向左遍历获取点集合
				 List<Point>p1LeftChanel=getLeftChanel(point1,0,pieceHeight);
				 //以p2位中心向左遍历获取点集合
				 p2LeftChanel=getLeftChanel(point2,0,pieceHeight);
				 
		 Map<Point,Point>leftLinkPoints=getYLinkPoints(p1LeftChanel,
				 p2LeftChanel,pieceWidth);
		 
		 		//向右遍历，不得超过Board的边框（有方块的地方）
		 		//以p1的中心点向右遍历获取集合
		 	p1RightChanel=getRightChanel(point1,widthMax,pieceWidth);
List<Point> p2RightChanel=getRightChanel(point2,widthMax,pieceWidth);

		Map<Point,Point>rightLinkPoints=getYLinkPoints(p1RightChanel,
				p2RightChanel,pieceWidth);
		
				result.putAll(leftLinkPoints);
				result.putAll(rightLinkPoints);
			 }
			 
			 
			 
			 
//-------------------------------右上角（6种情况）------重点----------------------------------------
	
		//point2位于point1的右上角	 
			 if(isRightUp(point1,point2)){
				 
				 //获取point1向上遍历，point2向下遍历时，  横向 可以连接的，就是两个拐点是横向的
			Map<Point,Point>upDownLinkPoints=getXLinkPoints(p1UpChanel,p2DownChanel,pieceWidth);
				 //point1向右，point2向左，纵向
			Map<Point,Point>rightLeftPoints=getYLinkPoints(p1RightChanel,p2LeftChanel,pieceHeight);
			 	
			 
			 	//获取以p1为中心的向上通道
			 p1UpChanel=getUpChanel(point1,0,pieceHeight);
			 	//获取以p2位中心的向上通道
			 p2UpChanel=getUpChanel(point2,0,pieceHeight);
			 	//获取向上，向上的连接点
			Map<Point,Point>upUpLinkPonts=getXLinkPoints(p1UpChanel,p2UpChanel,pieceWidth);
			 
			
				//获取以p1为中心的向下额通道
			 p1DownChanel=getDownChanel(point1,heightMax,pieceHeight);
			 	//获取以p2为中心的向下额通道
			 p2DownChanel=getDownChanel(point2,heightMax,pieceHeight);
			Map<Point,Point>downDownLinkPonts=getXLinkPoints(p1DownChanel,p2DownChanel,pieceWidth);
			
			
				//获取p1，p2向右
			  p1RightChanel=getRightChanel(point1,widthMax,pieceWidth);
   List<Point>p2RightChanel=getRightChanel(point2,widthMax,pieceWidth);
   			Map<Point,Point>rightRightLinkPonts=getYLinkPoints(p1RightChanel,p2RightChanel,
   					pieceHeight);
   			
   			
   				//向左 向左
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
			 
			 
			//=-------------------------右下角------------------------重点----------------------- 
     if(isRightDown(point1,point2)){
				 
				 //获取point1向下遍历，point2向上遍历时，  横向 可以连接的，就是两个拐点是横向的
			Map<Point,Point>downUpLinkPoints=getXLinkPoints(p1DownChanel,p2UpChanel,pieceWidth);
				 //point1向右，point2向左，纵向
			Map<Point,Point>rightLeftPoints=getYLinkPoints(p1RightChanel,p2LeftChanel,pieceHeight);
			 	
			 
			 	//获取以p1为中心的向上通道
			 p1UpChanel=getUpChanel(point1,0,pieceHeight);
			 	//获取以p2位中心的向上通道
			 p2UpChanel=getUpChanel(point2,0,pieceHeight);
			 	//获取向上，向上的连接点
			Map<Point,Point>upUpLinkPonts=getXLinkPoints(p1UpChanel,p2UpChanel,pieceWidth);
			 
			
				//获取以p1为中心的向下额通道
			 p1DownChanel=getDownChanel(point1,heightMax,pieceHeight);
			 	//获取以p2为中心的向下额通道
			 p2DownChanel=getDownChanel(point2,heightMax,pieceHeight);
			Map<Point,Point>downDownLinkPonts=getXLinkPoints(p1DownChanel,p2DownChanel,pieceWidth);
			
			
				//获取p1，p2向右
			  p1RightChanel=getRightChanel(point1,widthMax,pieceWidth);
   List<Point>p2RightChanel=getRightChanel(point2,widthMax,pieceWidth);
   			Map<Point,Point>rightRightLinkPonts=getYLinkPoints(p1RightChanel,p2RightChanel,
   					pieceHeight);
   			
   			
   				//向左 向左
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
	      * 遍历两个集合，先判断第一个集合的X坐标与另一个集合的元素坐标相同
	      * 如果相同，记载同一列，在判断是否有障碍，没有加到Map中
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
	    			 //如果X坐标相同（在同一列）
	    			 if(temp1.x==temp2.x){
	    				 //没有障碍，放到map中去
	    				 if(!isYBlock(temp1,temp2,pieceHeight)){
	    					 result.put(temp1, temp2);
	    				 }
	    			 }
	    		 }
	    	 }
	    	 return result;
	     }
	     
	     /*
	      * 遍历；两个集合，先判断第一个集合的元素的Y坐标与另一个集合中的元素Y坐标相同,横向
	      * */
	     private Map<Point,Point>getXLinkPoints(List<Point>p1Chanel,
	    		 List<Point>p2Chanel,int pieceWidth){
	    	 Map<Point,Point>result=new HashMap<Point,Point>();
	    	 for(int i=0;i<p1Chanel.size();i++){
	    		 Point temp1=p1Chanel.get(i);
	    		 for(int j=0;j<p2Chanel.size();j++){
	    			 Point temp2=p2Chanel.get(j);
	    			 //如果X坐标相同（在同一列）
	    			 if(temp1.y==temp2.y){
	    				 //没有障碍，放到map中去
	    				 if(!isYBlock(temp1,temp2,pieceWidth)){
	    					 result.put(temp1, temp2);
	    				 }
	    			 }
	    		 }
	    	 }
	    	 return result;
	     }
	     
	/*
	 * 获取p1和p2之间最短的连接信息
	 * turns 放转折点的map
	 *  shortDistance
	 *  return p1和p2之间最短的连接信息
	 *  
	 * */
	     private LinkInfo getShortcut(Point p1,Point p2,Map<Point,Point>turns,int shortDistance){
	    	 List<LinkInfo> infos=new ArrayList<LinkInfo>();
	    	 //遍历结果Map
	    	 for(Point point1:turns.keySet()){
	    		 Point point2=turns.get(point1);
	    		 //将转折点与选择点封装成LinkInfo对象，放到List集合中
	    		 infos.add(new LinkInfo(p1,point1,point2,p2));
	    		 }
	    	 return getShortcut(infos,shortDistance);
	     }
	     
	     /*
	      * 从infos中获取连接线最短的那个Linkinfo对象
	      * @param infos
	      * @return 连接线最短的那个LinkInfo对象
	      * */
	     private LinkInfo getShortcut(List<LinkInfo>infos,int shortDistance){
	    	 int temp1=0;
	    	 LinkInfo result=null;
	    	 
	    	 for(int i=0;i<infos.size();i++){
	    		 LinkInfo info=infos.get(i);
	    		 //计算出几个点的总距离
	    		 int distance=countAll(info.getLinkPoints());
	    		 //将循环第一个差距用temp1保存
	    		 if(i==0){
	    			 temp1=distance-shortDistance;
	    			 result=info;
	    		 }
	    		 //如果下一次循环的值比temp1的还小，则用当前的值作为temp1
	    		 if(distance-shortDistance<temp1){
	    			 temp1=distance-shortDistance;
	    			 result=info;
	    		 }
	    	 }
	    	 return result;
	     }
	     
	     /*
	      * 计算List<Point>中所有的距离总和
	      * 
	      * */
	     private int countAll(List<Point>points){
	    	 int result=0;
	    	 for(int i=0;i<points.size()-1;i++){
	    		 //获取第i个点
	    		 Point point1=points.get(i);
	    		 //获取第i+1个点
	    		 Point point2=points.get(i+1);
	    		 //计算第i个点与第i+1个点的距离，并添加到总距离中
	    		 result+=getDistance(point1,point2);
	    		 
	    		 
	    	 }
	    	return result; 
	     }
	     
	    /*
	     * 获取两个LinkPoint之间最短的距离
	     * return 两个点距离总和
	     * */ 
	     private int getDistance(Point p1,Point p2){
	    	 int xDistance=Math.abs(p1.x-p2.x);
	    	 int yDistance=Math.abs(p1.y-p2.y);
	    	 return xDistance+yDistance;
	     }
	     
	 	/**
	 	 * 判断GamePanel中的x, y座标中是否有Piece对象
	 	 * 
	 	 * @param x
	 	 * @param y
	 	 * @return true 表示有该座标有piece对象 false 表示没有
	 	 */
	 	private boolean hasPiece(int x, int y)
	 	{
	 		if (findPiece(x, y) == null)
	 			return false;
	 		return true;
	 	}
     }
