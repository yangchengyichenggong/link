package com.example.link;

import java.util.Timer;
import java.util.TimerTask;

import com.example.link.View.GameView;
import com.example.link.View.Piece;
import com.example.link.board.GameService;
import com.example.link.board.impl.GameServiceImpl;
import com.example.link.object.GameConf;
import com.example.link.object.LinkInfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class Link extends Activity{

		//��Ϸ���ö���
		private GameConf config;
		//��Ϸҵ���߼��ӿ�
		private GameService gameService;
		//��Ϸ����
		private GameView gameView;
		//��ʼ��ť
		private Button startButton;
		//��¼ʣ��ʱ��TextView
		private TextView timeTextView;
		//ʧ�ܺ󵯳��ĶԻ���
		private  AlertDialog.Builder lostDialog;
		//ʤ���󵯳��ĶԻ���
		private  AlertDialog.Builder successDialog;
		
		
		//��ʱ��
		private Timer timer=new Timer();
		//��¼��Ϸ��ʣ��ʱ��
		private int gameTime;
		//��¼�Ƿ�����Ϸ״̬
		private boolean isPlaying;
		//�𶯴�����
		private Vibrator vibrator;
		//��¼�Ѿ�ѡ�еķ���
		private Piece selected=null;
		
		
	//------------------Handler---------------------------------------------------	
		private Handler handler=new Handler(){
			public void handleMessage(Message msg){
			switch(msg.what){
			case 0x123:
				timeTextView.setText("ʣ��ʱ�䣺"+gameTime);
				gameTime--;
				//ʱ��С��0����Ϸ����
				if(gameTime<0){
					stopTimer();
					isPlaying=false;
					lostDialog.show();
					return;
				}
				break;
			}
			}
		};


		@Override
		protected void onCreate(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);
			//this.requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.main);
			//��ʼ������
			init();
		}


	
	
//------------          -----��ʼ����Ϸ�ķ�����ʼ------------------------------------------------------		
	

		private void init() {
			// TODO Auto-generated method stub
			config=new GameConf(8,9,2,10,100000,this);
			//�õ���Ϸ�������
			gameView=(GameView)findViewById(R.id.gameView);
			//��¼ʣ��ʱ��TextView
			timeTextView=(TextView) findViewById(R.id.timeText);			
			//��ʼ��ť
			startButton=(Button) findViewById(R.id.startButton);	
			//��
			vibrator=(Vibrator) getSystemService(VIBRATOR_SERVICE);
			//��Ϸҵ���߼��ӿ�
			gameService=new GameServiceImpl(this.config);//?????????????????
			
			gameView.setGameService(gameService);
			
			//Ϊ��ʼ��ť���õ����¼�����ʼ��Ϸ��
			startButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					startGame(GameConf.DEFAULT_TIME);
				}
			});
			
			
			//----------Ϊ��Ϸ����ĵĴ����¼��󶨼�����----------
			this.gameView.setOnTouchListener(new View.OnTouchListener() {
				
				@Override
				public boolean onTouch(View view, MotionEvent e) {
					if(e.getAction()==MotionEvent.ACTION_DOWN){						
						gameViewTouchDown(e);
					}
					if(e.getAction()==MotionEvent.ACTION_UP){
						gameViewTouchUp(e);
					}
					return true;
				}

			});
			
			//��ʼ����Ϸʧ�ܶԻ���
			lostDialog=createDialog("Lost","��Ϸʧ��!���¿�ʼ",R.drawable.lost).
					setPositiveButton("ȷ��",new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							startGame(GameConf.DEFAULT_TIME);
						}
					});
			//��ʼ����Ϸʤ���ĶԻ���
			successDialog=createDialog("Success","��Ϸ�ɹ�!���¿�ʼ",R.drawable.success).
					setPositiveButton("ȷ��",new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							startGame(GameConf.DEFAULT_TIME);
						}
					});
			
		}
		
		
//----------------------------------------init�������˽���-----------------------------------------------------
		
		
		
		
		@Override
		protected void onPause(){
			//��ͣ��Ϸ
			stopTimer();
			super.onPause();
		}
	
	@Override
	protected void onResume(){
		//���������Ϸ״̬
		if(isPlaying){
			//��ʣ���ʱ����д��ʼ��Ϸ
			startGame(gameTime);
		}
		super.onResume();
	}
	
	
		private void gameViewTouchDown(MotionEvent event){
			//��ȡGameServiceImpl�е�Piece[][]����
			
			Piece[][]pieces=gameService.getPieces();
			
			
			//��ȡ�û������X����
			float touchX=event.getX();
Log.d("Link", "touchX"+touchX);
			//��ȡ�û������Y����
			float touchY=event.getY();
Log.d("Link", "touchY"+touchY);
			//�����û�����������õ���Ӧ��Piece����

			Piece currentPiece=gameService.findPiece(touchX,touchY);
//Log.d("Link", "����x��"+currentPiece.getBeginX()+"    ����Y�� "+currentPiece.getIndexY());	

			//���û��ѡ���κ�Piece���󣨼������û��ͼƬ������������ִ��
			if(currentPiece==null){
				     return;}
			
	
			
			//��gameView��ѡ�еķ�����Ϊ��ǰ�ķ���
			this.gameView.setSelectedPiece(currentPiece);
			//��ʾ֮ǰû��ѡ���κ�һ��Piece
			if(this.selected==null){
				//����ǰ������Ϊ��ѡ�еķ��飬���½�GamePanel���ƣ�����������ִ��
				this.selected=currentPiece;			
				this.gameView.postInvalidate();
				return;
			}
			//��ʾ֮ǰ�Ѿ�ѡ����һ��
			if(this.selected!=null){
				//�������currentPiece��prePiece�����жϲ���������
				LinkInfo linkInfo=this.gameService.link(this.selected,currentPiece);
				//����Piece��������linkInfoΪnull
				if(linkInfo==null){
					//������Ӳ��ɹ�������ǰ������Ϊѡ�з���
					this.selected=currentPiece;
					this.gameView.postInvalidate();
				}else{
					//����ɹ�����
					handleSuccessLink(linkInfo,this.selected,currentPiece,pieces);
				}
			}
		}
	
		
		
//----------------------������Ϸ������취---------------------------------------------------
		private void gameViewTouchUp(MotionEvent e){
			this.gameView.postInvalidate();
		}
	
		
		//��gameTime��Ϊʣ��ʱ�俪ʼ��ָ���Ϸ
		private void startGame(int gameTime){
			//���֮ǰ��timer��δȡ����ȡ��timer
			if(this.timer!=null){
				stopTimer();
			}
			//����������Ϸʱ��
			this.gameTime=gameTime;
			//�����Ϸʣ��ʱ�������Ϸʱ����ȣ������¿�ʼ��Ϸ
			if(gameTime==GameConf.DEFAULT_TIME){
				//��ʼ�µ���Ϸ
				gameView.startGame();
			}
			isPlaying=true;
			this.timer=new Timer();
			//������ʱ��
			
			this.timer.schedule(new TimerTask(){

				@Override
				public void run() {
				handler.sendEmptyMessage(0x123);
	
				}				
			}, 0, 1000);
			//��ѡ�еķ�����Ϊnull
			this.selected=null;
		}
		
		
		/*
		 * �ɹ����Ӻ���
		 * pieceϵͳ�л�ʣ��ȫ������
		 * */
		private void handleSuccessLink(LinkInfo linkInfo,
				Piece prePiece,Piece currentPiece,Piece[][]pieces){
			//���ǿ�����������GamePanel����LinkInfo
			this.gameView.setLinkInfo(linkInfo);
			//��gameView�е�ѡ�з�����Ϊnull
			this.gameView.setSelectedPiece(null);
			this.gameView.postInvalidate();
			//������Piece�����������ɾ��
			pieces[prePiece.getIndexX()][prePiece.getIndexY()]=null;
			pieces[currentPiece.getIndexX()][currentPiece.getIndexY()]=null;
			//��ѡ�� �ķ�������null
			this.selected=null;
			//s�ֻ��𶯣�100���룩
			this.vibrator.vibrate(100);
			//�ж��Ƿ���ʣ�µķ��飬���û�У���Ϸʤ��
			if(!this.gameService.hasPieces()){
				//��Ϸʤ��
				this.successDialog.show();
				//ֹͣ��ʱ��
				stopTimer();
				//������Ϸ״̬
				isPlaying=false;
			}
		}
	
	
//----------------------------�����Ի���Ĺ��߰취--------------------------------------------	
	
	private AlertDialog.Builder createDialog(String title,String message,int imageResource){
		return new AlertDialog.Builder(this).
				setTitle(title).setMessage(message).setIcon(imageResource);	
	}
		
		
		
	private void stopTimer(){
		//ֹͣ��ʱ��
		this.timer.cancel();
		this.timer=null;
	}
		
		
		
}
