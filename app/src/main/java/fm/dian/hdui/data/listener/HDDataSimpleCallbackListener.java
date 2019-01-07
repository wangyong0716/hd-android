package fm.dian.hdui.data.listener;

import android.content.Context;
import android.widget.Toast;

import fm.dian.hddata.channel.HDDataChannelClient.HDDataCallbackListener;
import fm.dian.hddata.channel.message.HDDataMessage;

/**
 * ******************
 * fileName  :HDDataSimpleCallbackListener.java
 * author    : song
 * createTime:2015-1-7 下午6:33:59
 * fileDes   :
 */
public class HDDataSimpleCallbackListener implements HDDataCallbackListener {
    private Context mContext;

    public HDDataSimpleCallbackListener(Context mContext) {
        super();
        this.mContext = mContext;
    }

    @Override
    public void onFail() {
        Toast.makeText(mContext, "加载失败了", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSuccess(HDDataMessage arg0) {

    }

    @Override
    public void onTimeout() {
        Toast.makeText(mContext, "网络超时了", Toast.LENGTH_SHORT).show();
    }

}
