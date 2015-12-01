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

		//游戏配置对象
		private GameConf config;
		//游戏业务逻辑接口
		private GameService gameService;
		//游戏界面
		private GameView gameView;
		//开始按钮
		private Button startButton;
		//记录剩余时间TextView
		private TextView timeTextView;
		//失败后弹出的对话框
		private  AlertDialog.Builder lostDialog;
		//胜利后弹出的对话框
		private  AlertDialog.Builder successDialog;
		
		
		//定时器
		private Timer timer=new Timer();
		//记录游戏的剩余时间
		private int gameTime;
		//记录是否处于游戏状态
		private boolean isPlaying;
		//震动处理类
		private Vibrator vibrator;
		//记录已经选中的方块
		private Piece selected=null;
		
		
	//------------------Handler---------------------------------------------------	
		private Handler handler=new Handler(){
			public void handleMessage(Message msg){
			switch(msg.what){
			case 0x123:
				timeTextView.setText("剩余时间："+gameTime);
				gameTime--;
				//时间小于0，游戏结束
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
			//初始化界面
			init();
		}


	
	
//------------          -----初始化游戏的方法开始------------------------------------------------------		
	

		private void init() {
			// TODO Auto-generated method stub
			config=new GameConf(8,9,2,10,100000,this);
			//得到游戏区域对象
			gameView=(GameView)findViewById(R.id.gameView);
			//记录剩余时间TextView
			timeTextView=(TextView) findViewById(R.id.timeText);			
			//开始按钮
			startButton=(Button) findViewById(R.id.startButton);	
			//震动
			vibrator=(Vibrator) getSystemService(VIBRATOR_SERVICE);
			//游戏业务逻辑接口
			gameService=new GameServiceImpl(this.config);//?????????????????
			
			gameView.setGameService(gameService);
			
			//为开始按钮设置单击事件（开始游戏）
			startButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					startGame(GameConf.DEFAULT_TIME);
				}
			});
			
			
			//----------为游戏区域的的触碰事件绑定监听器----------
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
			
			//初始化游戏失败对话框
			lostDialog=createDialog("Lost","游戏失败!重新开始",R.drawable.lost).
					setPositiveButton("确定",new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							startGame(GameConf.DEFAULT_TIME);
						}
					});
			//初始化游戏胜利的对话框
			successDialog=createDialog("Success","游戏成功!重新开始",R.drawable.success).
					setPositiveButton("确定",new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							startGame(GameConf.DEFAULT_TIME);
						}
					});
			
		}
		
		
//----------------------------------------init方法到此结束-----------------------------------------------------
		
		
		
		
		@Override
		protected void onPause(){
			//暂停游戏
			stopTimer();
			super.onPause();
		}
	
	@Override
	protected void onResume(){
		//如果处于游戏状态
		if(isPlaying){
			//以剩余的时间重写开始游戏
			startGame(gameTime);
		}
		super.onResume();
	}
	
	
		private void gameViewTouchDown(MotionEvent event){
			//获取GameServiceImpl中的Piece[][]数组
			
			Piece[][]pieces=gameService.getPieces();
			
			
			//获取用户点击的X坐标
			float touchX=event.getX();
Log.d("Link", "touchX"+touchX);
			//获取用户点击的Y坐标
			float touchY=event.getY();
Log.d("Link", "touchY"+touchY);
			//根据用户触碰的坐标得到对应的Piece对象

			Piece currentPiece=gameService.findPiece(touchX,touchY);
//Log.d("Link", "坐标x："+currentPiece.getBeginX()+"    坐标Y： "+currentPiece.getIndexY());	

			//如果没有选中任何Piece对象（即点击出没有图片），不再往下执行
			if(currentPiece==null){
				     return;}
			
	
			
			//将gameView中选中的方块设为当前的方块
			this.gameView.setSelectedPiece(currentPiece);
			//表示之前没有选中任何一个Piece
			if(this.selected==null){
				//将当前方块设为已选中的方块，重新将GamePanel绘制，并不在往下执行
				this.selected=currentPiece;			
				this.gameView.postInvalidate();
				return;
			}
			//表示之前已经选择了一个
			if(this.selected!=null){
				//在这里对currentPiece和prePiece进行判断并进行连接
				LinkInfo linkInfo=this.gameService.link(this.selected,currentPiece);
				//两个Piece不可连，linkInfo为null
				if(linkInfo==null){
					//如果连接不成功，将当前方块设为选中方块
					this.selected=currentPiece;
					this.gameView.postInvalidate();
				}else{
					//处理成功连接
					handleSuccessLink(linkInfo,this.selected,currentPiece,pieces);
				}
			}
		}
	
		
		
//----------------------触碰游戏区域处理办法---------------------------------------------------
		private void gameViewTouchUp(MotionEvent e){
			this.gameView.postInvalidate();
		}
	
		
		//以gameTime作为剩余时间开始或恢复游戏
		private void startGame(int gameTime){
			//如果之前的timer还未取消，取消timer
			if(this.timer!=null){
				stopTimer();
			}
			//重新设置游戏时间
			this.gameTime=gameTime;
			//如果游戏剩余时间和总游戏时间相等，则重新开始游戏
			if(gameTime==GameConf.DEFAULT_TIME){
				//开始新的游戏
				gameView.startGame();
			}
			isPlaying=true;
			this.timer=new Timer();
			//启动计时器
			
			this.timer.schedule(new TimerTask(){

				@Override
				public void run() {
				handler.sendEmptyMessage(0x123);
	
				}				
			}, 0, 1000);
			//将选中的方块设为null
			this.selected=null;
		}
		
		
		/*
		 * 成功连接后处理
		 * piece系统中还剩的全部方块
		 * */
		private void handleSuccessLink(LinkInfo linkInfo,
				Piece prePiece,Piece currentPiece,Piece[][]pieces){
			//他们可以相连，让GamePanel处理LinkInfo
			this.gameView.setLinkInfo(linkInfo);
			//将gameView中的选中方块设为null
			this.gameView.setSelectedPiece(null);
			this.gameView.postInvalidate();
			//将两个Piece对象从数组中删除
			pieces[prePiece.getIndexX()][prePiece.getIndexY()]=null;
			pieces[currentPiece.getIndexX()][currentPiece.getIndexY()]=null;
			//将选中 的方块设置null
			this.selected=null;
			//s手机震动（100毫秒）
			this.vibrator.vibrate(100);
			//判断是否还有剩下的方块，如果没有，游戏胜利
			if(!this.gameService.hasPieces()){
				//游戏胜利
				this.successDialog.show();
				//停止定时器
				stopTimer();
				//更改游戏状态
				isPlaying=false;
			}
		}
	
	
//----------------------------创建对话框的工具办法--------------------------------------------	
	
	private AlertDialog.Builder createDialog(String title,String message,int imageResource){
		return new AlertDialog.Builder(this).
				setTitle(title).setMessage(message).setIcon(imageResource);	
	}
		
		
		
	private void stopTimer(){
		//停止定时器
		this.timer.cancel();
		this.timer=null;
	}
		
		
		
}
