package ua.xbterminal.bitcoin;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;
import com.dlazaro66.qrcodereaderview.QRCodeReaderView.OnQRCodeReadListener;

public class DecoderActivity extends Activity implements OnQRCodeReadListener {


	private QRCodeReaderView mydecoderview;
	private boolean find = false;
	private ImageView  line_image;
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        requestWindowFeature(Window.FEATURE_NO_TITLE);     
        setContentView(R.layout.activity_decoder);
        
        mydecoderview = (QRCodeReaderView) findViewById(R.id.qrdecoderview);
        mydecoderview.setOnQRCodeReadListener(this);
        
        
          line_image = (ImageView) findViewById(R.id.red_line_image);
      	Button btn = (Button)findViewById(R.id.button1);
		btn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				finish();		
			}
			
		});
          
        TranslateAnimation mAnimation = new TranslateAnimation(
                TranslateAnimation.ABSOLUTE, 0f,
                TranslateAnimation.ABSOLUTE, 0f,
                TranslateAnimation.RELATIVE_TO_PARENT, -0.2f,
                TranslateAnimation.RELATIVE_TO_PARENT, 0.2f);
       mAnimation.setDuration(1000);
       mAnimation.setRepeatCount(-1);
       mAnimation.setRepeatMode(Animation.REVERSE);
       mAnimation.setInterpolator(new LinearInterpolator());
       line_image.setAnimation(mAnimation);
    }

    
    // Called when a QR is decoded
    // "text" : the text encoded in QR
    // "points" : points where QR control points are placed
	@Override
	public void onQRCodeRead(String text, PointF[] points){

		if(!find){
			Intent intent = new Intent();
		    
			try{
		find = true;
		intent.putExtra("code",text);
	    setResult(RESULT_OK, intent);
	    finish();
			}catch(Exception r){
				 setResult(RESULT_CANCELED, intent);
				Toast.makeText(getBaseContext(), r.getMessage(), Toast.LENGTH_LONG).show();
				r.getMessage();
			}
		}
	}

	
	// Called when your device have no camera
	@Override
	public void cameraNotFound() {
		Toast.makeText(getBaseContext(), "cameraNotFound!", Toast.LENGTH_LONG).show();
	}

	// Called when there's no QR codes in the camera preview image
	@Override
	public void QRCodeNotFoundOnCamImage() {
		
	}
	@Override
	protected void onStop() {
		super.onStop();
		find = true;
	}
	@Override
	protected void onResume() {
		super.onResume();
		mydecoderview.getCameraManager().startPreview();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		mydecoderview.getCameraManager().stopPreview();
	}
}
