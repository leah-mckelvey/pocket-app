package com.interview.leah.pocketactivityapp;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.LayerDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;


public class PocketActivity extends Activity {

    final Messenger mMessenger = new Messenger(new IncomingHandler());
    Messenger mService = null;

    private boolean mIsBound;
    private int hitScreenHeight;
    private int hitScreenWidth;

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            }
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.d("Leah", "Service called");
            mService = new Messenger(service);
            try {
                Message msg = Message.obtain();
                msg.arg1 = 0;
                msg.arg2 = 0;
                msg.what = 0xEF4056;
                msg.replyTo = mMessenger;
                Log.d("Leah", "Sending message");
                mService.send(msg);

            }
            catch (RemoteException e) {
                // In this case the service has crashed before we could even do anything with it
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been unexpectedly disconnected - process crashed.
            mService = null;
        }
    };

    void doBindService() {
        // Establish a connection with the service.  We use an explicit
        // class name because there is no reason to be able to let other
        // applications replace our component.
        //bindService(new Intent(this, MessengerService.class), mConnection, Context.BIND_AUTO_CREATE);
        try {
            Intent intentForMcuService = new Intent();
            Log.d("Leah", "Before init intent.componentName");
            intentForMcuService.setComponent(new ComponentName("com.pocket.doorway", "com.pocket.doorway.input.TractorBeamTargetingService"));
            Log.d("Leah", "Before bindService");
            if (bindService(intentForMcuService, mConnection, Context.BIND_AUTO_CREATE)){
                Log.d("Leah", "Binding to Tractor Beam returned true");
            } else {
                Log.d("Leah", "Binding to Tractor Beam returned false");
            }
        } catch (SecurityException e) {
            Log.e("Leah", "can't bind to TractorBeamTargetingService, check permission in Manifest");
        }
        mIsBound = true;
        Log.d("Leah", "Binding.");
    }

    void doUnbindService() {
        if (mIsBound) {
            // If we have received the service, and hence registered with
            // it, then now is the time to unregister.

            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
            Log.d("Leah", "Unbinding.");
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pocket);
        final View invisibleScreenView = findViewById(R.id.invisible_screenview);
        invisibleScreenView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Log.d("Leah", "Touch coordinates : " +
                            String.valueOf(event.getX()) + "x" + String.valueOf(event.getY()));
                    float locx = event.getX() / (float) hitScreenWidth;
                    float locy = event.getY() / (float) hitScreenHeight;
                    float translatedLocX = locx * 360.0f;// These constants obtained from decompiled code.  Not sure they're right, but I started here.
                    float translatedLocY = locy * 280.0f;
                    try {
                        Message msg = Message.obtain();
                        msg.arg1 = (int)translatedLocX;
                        msg.arg2 = (int)translatedLocY;
                        msg.what = 0xEF4056;
                        msg.replyTo = mMessenger;
                        Log.d("Leah", "Sending message");
                        mService.send(msg);

                    }
                    catch (RemoteException e) {
                        // In this case the service has crashed before we could even do anything with it
                    }
                }
                return true;
            }
        });
        invisibleScreenView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                hitScreenHeight = invisibleScreenView.getMeasuredHeight();
                hitScreenWidth = invisibleScreenView.getMeasuredWidth();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        doBindService();
    }

    @Override
    public void onStop() {
        super.onStop();
        doUnbindService();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pocket, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
