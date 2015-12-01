package com.example.link.util;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.example.link.View.PieceImage;

import com.example.link.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

public class ImageUtil {
			
		//保存所有连连看的图片资源
		private static List<Integer>imageValues=getImageValues();
		//获得所有连连看的图片资源ID
		
	public static List<Integer> getImageValues(){
		List<Integer>resourceValues=new ArrayList<Integer>();
		try{
			//得到R.drawable所有属性，即获得drawable目录下的所有图片
			
			Field[]drawableFields=R.drawable.class.getFields();			
			for(Field field:drawableFields){
				//如果该Field的名称已P_开头
				if(field.getName().indexOf("p_")!=-1){
					resourceValues.add(field.getInt(R.drawable.class));
				}
				
			}
		}catch(Exception e){
			return null;
		}
		return resourceValues;
	}
	
	/*
	 * 随机从sourceValues的集合中获取size个图片id，返回结果是id的集合 
	 * @param sourceValues从中获取的集合
	 * @param size 需要获取的个数
	 * @param size 个图片id的集合
	 * */
	public static List<Integer>getRandomValues(List<Integer>sourceValues,int size){
		
		//创建一个随机数生成器
		Random random=new Random();
		//创建结果集合
		List<Integer>  result=new ArrayList<Integer>();
		for(int i=0;i<size;i++){
			try{
				//随机获取一个数字小于sourceValues.size()的 数值
				int index=random.nextInt(sourceValues.size());
				//从图片Id集合中获取该图片对象
				Integer image=sourceValues.get(index);
				//添加到结果集
				result.add(image);
			}catch(Exception e){
				return result;
			}
		}
		return result;
	}
	
	/*
	 * 从drawable 目录中获取size个图片资源ID，其中size为游戏数量
	 * @param size 需要获取的图片ID的数量
	 * @return size个图片id集合
	 * */
	
	public static List<Integer>getPlayValues(int size){
		if(size%2!=0){
			//如果除以二有余数，将size加一，保证每个资源都能用到
			size+=1;
		}
		
		//再从所有的图片值中随机获取size的一半的值，调用上一个方法
		
		List<Integer>playImageValues=getRandomValues(imageValues,size/2);
		
		//将playImageValues集合的元素增加一倍（保证所有图片都有与之配对的图片）,复制一遍自己
		playImageValues.addAll(playImageValues);
		//将所有图片随机洗牌
		Collections.shuffle(playImageValues);
		return playImageValues;
	}
	
	/*
	 * 将图片Id集合转成PieceImage对象集合，PieceImage封装了图片Id与图片本身
	 * @param context
	 * @param resourcesValues
	 * @return size个PieceImage对象的集合
	 * */
	public static List<PieceImage> getPlayImages(Context context,int size){
		//获取图片id组成的集合
		List<Integer> resourcesValues=getPlayValues(size);
		List<PieceImage> result=new ArrayList<PieceImage>();
		//遍历每个图片的id
		for(Integer value:resourcesValues){
			//加载图片
			Bitmap bm=BitmapFactory.decodeResource( context.getResources(), value );
			//新加内容
			int width = bm.getWidth();  
	        int height = bm.getHeight();  
	        
	        int newWidth = 125;  
	        int newHeight = 125; 
	        float scaleWidth = ((float) newWidth) / width;  
	        float scaleHeight = ((float) newHeight) / height; 
		
	        Matrix matrix = new Matrix();  
	        matrix.postScale(scaleWidth, scaleHeight); 
	        Bitmap bm2 = Bitmap.createBitmap(bm, 0, 0, width,  
                    height, matrix, true);  
		
			
			//加载图片id与图片本身
			PieceImage pieceImage=new PieceImage(bm2,value);
			result.add(pieceImage);
		}
		return result;
	}
	
	//获取选中表示的图片
	public static Bitmap getSelectImage(Context context){
		
		
		
		Bitmap bm=BitmapFactory.decodeResource( context.getResources(),R.drawable.selected);
		//新加内容
		  	int width = bm.getWidth();  
	        int height = bm.getHeight();  
	        
	        int newWidth = 120;  
	        int newHeight = 120; 
	        float scaleWidth = ((float) newWidth) / width;  
	        float scaleHeight = ((float) newHeight) / height; 
		
	        Matrix matrix = new Matrix();  
	        matrix.postScale(scaleWidth, scaleHeight); 
	        Bitmap bm2 = Bitmap.createBitmap(bm, 0, 0, width,  
                    height, matrix, true);  
		
		return bm2;
	}

	
	
	
	
}
