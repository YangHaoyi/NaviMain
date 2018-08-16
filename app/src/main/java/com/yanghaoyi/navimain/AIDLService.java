package com.yanghaoyi.navimain;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.support.annotation.Nullable;

/**
 * @author : YangHaoYi on 2018/8/16.
 *         Email  :  yang.haoyi@qq.com
 *         Description :AIDL传输Demo服务端
 *         Change : YangHaoYi on 2018/8/16.
 *         Version : V 1.0
 */
public class AIDLService extends Service{

    private RouteInfo routeInfo;
    private RemoteCallbackList<ITaskCallback> mCallBacks = new RemoteCallbackList<>();
    private IRouteInterface.Stub mStub = new IRouteInterface.Stub() {
        @Override
        public RouteInfo getRouteInfo() throws RemoteException {
            return routeInfo;
        }

        @Override
        public void addRouteInfo(RouteInfo info) throws RemoteException {
            routeInfo = info;
            notifyCallBack(1);
        }

        @Override
        public void registerCallback(ITaskCallback cb) throws RemoteException {
            if(null!=cb){
                mCallBacks.register(cb);
            }
        }

        @Override
        public void unregisterCallback(ITaskCallback cb) throws RemoteException {
            if(null!=cb){
                mCallBacks.unregister(cb);
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        routeInfo = new RouteInfo();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mStub;
    }

    private void notifyCallBack(int code) {
        final int len = mCallBacks.beginBroadcast();
        for (int i = 0; i < len; i++) {
            try {
                // 通知回调
                mCallBacks.getBroadcastItem(i).callback(code);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        mCallBacks.finishBroadcast();
    }

    @Override
    public void onDestroy() {
        //销毁回调资源 否则要内存泄露
        mCallBacks.kill();
        super.onDestroy();
    }
}
