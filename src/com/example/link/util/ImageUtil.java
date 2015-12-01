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
			
		//����������������ͼƬ��Դ
		private static List<Integer>imageValues=getImageValues();
		//���������������ͼƬ��ԴID
		
	public static List<Integer> getImageValues(){
		List<Integer>resourceValues=new ArrayList<Integer>();
		try{
			//�õ�R.drawable�������ԣ������drawableĿ¼�µ�����ͼƬ
			
			Field[]drawableFields=R.drawable.class.getFields();			
			for(Field field:drawableFields){
				//�����Field��������P_��ͷ
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
	 * �����sourceValues�ļ����л�ȡsize��ͼƬid�����ؽ����id�ļ��� 
	 * @param sourceValues���л�ȡ�ļ���
	 * @param size ��Ҫ��ȡ�ĸ���
	 * @param size ��ͼƬid�ļ���
	 * */
	public static List<Integer>getRandomValues(List<Integer>sourceValues,int size){
		
		//����һ�������������
		Random random=new Random();
		//�����������
		List<Integer>  result=new ArrayList<Integer>();
		for(int i=0;i<size;i++){
			try{
				//�����ȡһ������С��sourceValues.size()�� ��ֵ
				int index=random.nextInt(sourceValues.size());
				//��ͼƬId�����л�ȡ��ͼƬ����
				Integer image=sourceValues.get(index);
				//��ӵ������
				result.add(image);
			}catch(Exception e){
				return result;
			}
		}
		return result;
	}
	
	/*
	 * ��drawable Ŀ¼�л�ȡsize��ͼƬ��ԴID������sizeΪ��Ϸ����
	 * @param size ��Ҫ��ȡ��ͼƬID������
	 * @return size��ͼƬid����
	 * */
	
	public static List<Integer>getPlayValues(int size){
		if(size%2!=0){
			//������Զ�����������size��һ����֤ÿ����Դ�����õ�
			size+=1;
		}
		
		//�ٴ����е�ͼƬֵ�������ȡsize��һ���ֵ��������һ������
		
		List<Integer>playImageValues=getRandomValues(imageValues,size/2);
		
		//��playImageValues���ϵ�Ԫ������һ������֤����ͼƬ������֮��Ե�ͼƬ��,����һ���Լ�
		playImageValues.addAll(playImageValues);
		//������ͼƬ���ϴ��
		Collections.shuffle(playImageValues);
		return playImageValues;
	}
	
	/*
	 * ��ͼƬId����ת��PieceImage���󼯺ϣ�PieceImage��װ��ͼƬId��ͼƬ����
	 * @param context
	 * @param resourcesValues
	 * @return size��PieceImage����ļ���
	 * */
	public static List<PieceImage> getPlayImages(Context context,int size){
		//��ȡͼƬid��ɵļ���
		List<Integer> resourcesValues=getPlayValues(size);
		List<PieceImage> result=new ArrayList<PieceImage>();
		//����ÿ��ͼƬ��id
		for(Integer value:resourcesValues){
			//����ͼƬ
			Bitmap bm=BitmapFactory.decodeResource( context.getResources(), value );
			//�¼�����
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
		
			
			//����ͼƬid��ͼƬ����
			PieceImage pieceImage=new PieceImage(bm2,value);
			result.add(pieceImage);
		}
		return result;
	}
	
	//��ȡѡ�б�ʾ��ͼƬ
	public static Bitmap getSelectImage(Context context){
		
		
		
		Bitmap bm=BitmapFactory.decodeResource( context.getResources(),R.drawable.selected);
		//�¼�����
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
