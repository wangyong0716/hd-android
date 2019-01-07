package fm.dian.hdui.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class HDRoomChangedBroadCastReceiver extends BroadcastReceiver {

    private static final String INTENT_FILTER_ACTION = "fm.dian.room";
    private static final String INTENT_FILTER_ACTION_CHANGED = "fm.dian.room.changed";
    private static final String INTENT_FILTER_ACTION_CREATE = "fm.dian.room.create";
    private static final String INTENT_FILTER_ACTION_DELETE = "fm.dian.room.delete";
    private static final String INTENT_FILTER_ACTION_CANCELFOLLOW = "fm.dian.room.cancelFollow";
    private static final String INTENT_FILTER_ACTION_ADDFOLLOW = "fm.dian.room.addFollow";
    private static final String ROOM_ID = "roomId";

    private HDRoomChangedListener changedListener;
    private boolean isRegister;

    public HDRoomChangedBroadCastReceiver(HDRoomChangedListener changedListener) {
        this.changedListener = changedListener;
    }

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(INTENT_FILTER_ACTION)) {
            if (changedListener != null) {
                String type = intent.getStringExtra(INTENT_FILTER_ACTION);
                long roomId = intent.getLongExtra(ROOM_ID, 0);
                if (roomId != 0) {
                    if (INTENT_FILTER_ACTION_CHANGED.equals(type)) {
                        changedListener.onChanged(roomId);
                    } else if (INTENT_FILTER_ACTION_CREATE.equals(type)) {
                        changedListener.onCreate(roomId);
                    } else if (INTENT_FILTER_ACTION_DELETE.equals(type)) {
                        changedListener.onDelete(roomId);
                    } else if (INTENT_FILTER_ACTION_CANCELFOLLOW.equals(type)) {
                        changedListener.onCancelFollow(roomId);
                    } else if (INTENT_FILTER_ACTION_ADDFOLLOW.equals(type)) {
                        changedListener.onAddFollow(roomId);
                    }
                }
            }
        }
    }

    public void register(Context context) {
        if (context != null && !isRegister) {
            isRegister = true;
            IntentFilter filter = new IntentFilter(INTENT_FILTER_ACTION);
            context.registerReceiver(this, filter);
        }
    }

    public void unregister(Context context) {
        if (context != null) {
            context.unregisterReceiver(this);
        }
    }

    public static void changeRoom(Context context, long roomId) {
        if (context != null) {
            Intent intent = new Intent(INTENT_FILTER_ACTION);
            intent.putExtra(INTENT_FILTER_ACTION, INTENT_FILTER_ACTION_CHANGED);
            intent.putExtra(ROOM_ID, roomId);
            context.sendBroadcast(intent);
        }
    }

    public static void createRoom(Context context, long roomId) {
        if (context != null) {
            Intent intent = new Intent(INTENT_FILTER_ACTION);
            intent.putExtra(INTENT_FILTER_ACTION, INTENT_FILTER_ACTION_CREATE);
            intent.putExtra(ROOM_ID, roomId);
            context.sendBroadcast(intent);
        }
    }

    public static void deleteRoom(Context context, long roomId) {
        if (context != null) {
            Intent intent = new Intent(INTENT_FILTER_ACTION);
            intent.putExtra(INTENT_FILTER_ACTION, INTENT_FILTER_ACTION_DELETE);
            intent.putExtra(ROOM_ID, roomId);
            context.sendBroadcast(intent);
        }
    }

    public static void cancelFollowRoom(Context context, long roomId) {
        if (context != null) {
            Intent intent = new Intent(INTENT_FILTER_ACTION);
            intent.putExtra(INTENT_FILTER_ACTION, INTENT_FILTER_ACTION_CANCELFOLLOW);
            intent.putExtra(ROOM_ID, roomId);
            context.sendBroadcast(intent);
        }
    }

    public static void addFollowRoom(Context context, long roomId) {
        if (context != null) {
            Intent intent = new Intent(INTENT_FILTER_ACTION);
            intent.putExtra(INTENT_FILTER_ACTION, INTENT_FILTER_ACTION_ADDFOLLOW);
            intent.putExtra(ROOM_ID, roomId);
            context.sendBroadcast(intent);
        }
    }

    public interface HDRoomChangedListener {
        void onChanged(long roomId);

        void onCreate(long roomId);

        void onDelete(long roomId);

        void onCancelFollow(long roomId);

        void onAddFollow(long roomId);
    }

}
