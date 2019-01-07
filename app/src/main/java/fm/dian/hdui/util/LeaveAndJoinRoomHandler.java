//package fm.dian.hdui.util;
//
//import android.app.Activity;
//import android.widget.Toast;
//import fm.dian.hddata.business.auth.HDAuthHandler;
//import fm.dian.hddata.business.auth.HDAuthModelMessage;
//import fm.dian.hddata.channel.HDDataChannelClient;
//import fm.dian.hddata.channel.HDDataChannelClient.HDDataCallbackListener;
//import fm.dian.hddata.channel.message.HDDataMessage;
//import fm.dian.hddata.util.HDLog;
//import fm.dian.hdui.activity.HDBaseTabFragmentActivity;
//import fm.dian.hdui.activity.HDJoinRoomActivity.HDJoinRoomHandler;
//import fm.dian.service.rpc.HDResponse.Response.ResponseStatus;
//
//public class LeaveAndJoinRoomHandler {
//
//	private HDLog log = new HDLog(LeaveAndJoinRoomHandler.class);
//
//	public void leaveAndJoin(final Activity activity, final long roomId, final String passwd, final HDJoinRoomHandler joinRoomHandler, final HDDataChannelClient dataChannel, final HDAuthHandler authHandler){
//		final long rid = joinRoomHandler.getLeavingRoomId();
//
//		if (rid < 1) {
//			join(activity, roomId, passwd, joinRoomHandler, dataChannel, authHandler);
//			return ;
//		}
//
//		boolean isSend = authHandler.leaveRoom(rid, dataChannel, new HDDataCallbackListener() {
//			public void onTimeout() {
//				String msg = "onTimeout";
//				log.i(msg);
//			}
//
//			public void onSuccess(HDDataMessage dataMessage) {
//				if (!HDAuthModelMessage.class.isInstance(dataMessage)) {
//					String msg = "onSuccess [ERROR]: dataMessage is not HDAuthModelMessage : "+dataMessage;
//					log.i(msg);
//					return ;
//				}
//				HDAuthModelMessage roomModelMessage = (HDAuthModelMessage)dataMessage;
//				String msg = "onSuccess: "+roomModelMessage.getRoomId();
//				log.i(msg);
//				if (roomModelMessage.getResponseStatus() == ResponseStatus.OK) {
//					joinRoomHandler.onLeavedRoom(rid);
//					join(activity, roomId, passwd, joinRoomHandler, dataChannel, authHandler);
//				} else {
//					Toast.makeText(activity, "发生错误", Toast.LENGTH_SHORT).show();
//				}
//			}
//
//			public void onFail() {
//				String msg = "onFail";
//				log.i(msg);
//			}
//		});
//		log.i("leaveRoom: "+isSend);
//	}
//
//	public void join(final Activity activity, final long roomId, final String passwd, final HDJoinRoomHandler joinRoomHandler, HDDataChannelClient dataChannel, HDAuthHandler authHandler){
//		boolean isSend = authHandler.joinRoom(roomId, passwd, dataChannel, new HDDataCallbackListener() {
//			public void onTimeout() {
//				String msg = "onTimeout";
//				log.i(msg);
//			}
//
//			public void onSuccess(HDDataMessage dataMessage) {
//				if (!HDAuthModelMessage.class.isInstance(dataMessage)) {
//					String msg = "onSuccess [ERROR]: dataMessage is not HDAuthModelMessage : "+dataMessage;
//					log.i(msg);
//					return ;
//				}
//				HDAuthModelMessage roomModelMessage = (HDAuthModelMessage)dataMessage;
//				String msg = "onSuccess: "+roomModelMessage.getRoomId()+" "+roomModelMessage.getAuthResponeType();
//				log.i(msg);
//				if (roomModelMessage.getResponseStatus() == ResponseStatus.OK) {
//
//					switch (roomModelMessage.getAuthResponeType()) {
//					case OK:{
//						joinRoomHandler.onJoinedRoom(roomId, joinRoomHandler.getRoomPasswd(roomId));
//						activity.finish();
//					}
//						break;
//					case NEED_PASSWORD:{
//						Toast.makeText(activity, "进入该频道需要密码！", Toast.LENGTH_SHORT).show();
//					}
//						break;
//					case PASSWORD_INVALIDATE:{
//						Toast.makeText(activity, "密码错误", Toast.LENGTH_SHORT).show();
//					}
//						break;
//
//					default:
//						break;
//					}
//
//				} else {
//					Toast.makeText(activity, "发生错误", Toast.LENGTH_SHORT).show();
//				}
//			}
//
//			public void onFail() {
//				String msg = "onFail";
//				log.i(msg);
//			}
//		});
//
//		log.i("joinRoom: "+isSend);
//	}
//}
