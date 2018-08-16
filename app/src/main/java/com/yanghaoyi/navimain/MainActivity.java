package com.yanghaoyi.navimain;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author : YangHaoYi on 2018/8/15.
 *         Email  :  yang.haoyi@qq.com
 *         Description :AIDL传输Demo服务端
 *         Change : YangHaoYi on 2018/8/15.
 *         Version : V 1.0
 */
public class MainActivity extends AppCompatActivity {

    private static final String SERVICE_ACTION = "com.yanghaoyi.AIDLService";
    private static final String SERVICE_PACKAGE = "com.yanghaoyi.navimain";
    private TextView tvSendMessage;
    private TextView tvStartAssist;
    private EditText edMessage;

    private IRouteInterface mStub;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mStub = IRouteInterface.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mStub = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        connectService();
    }

    private void init(){
        initView();
        initEvent();
    }

    private void initView(){
        tvSendMessage = findViewById(R.id.tvSendMessage);
        tvStartAssist = findViewById(R.id.tvStartAssist);
        edMessage = findViewById(R.id.edMessage);
    }

    private void initEvent(){
        tvSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null==mStub){
                    connectService();
                }else {
                    RouteInfo routeInfo = new RouteInfo();
                    routeInfo.setName(edMessage.getText().toString());
                    try {
                        mStub.addRouteInfo(routeInfo);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        tvStartAssist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAssist();
            }
        });
    }


    private void startAssist(){
        // 通过包名获取要跳转的app，创建intent对象
        Intent intent = getPackageManager().getLaunchIntentForPackage("com.yanghaoyi.naviassist");
        if (intent != null) {
            startActivity(intent);
        } else {
            // 未安装应用
            Toast.makeText(getApplicationContext(), "辅屏App未安装", Toast.LENGTH_LONG).show();
        }
    }

    private void connectService(){
        Intent intent = new Intent();
        intent.setAction(SERVICE_ACTION);
        intent.setPackage(SERVICE_PACKAGE);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }
}
