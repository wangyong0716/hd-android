package fm.dian.hdui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import fm.dian.hdservice.AuthService;
import fm.dian.hdservice.CoreService;
import fm.dian.hdservice.base.CallBack;
import fm.dian.hdservice.model.User;
import fm.dian.hdui.R;
import fm.dian.hdui.app.ActivityManager;
import fm.dian.hdui.app.Config;
import fm.dian.hdui.app.HDApp;
import fm.dian.hdui.util.ActivityCheck;
import fm.dian.hdui.util.HDImageLoadUtil;
import fm.dian.hdui.util.image.ImageScaleUtil;
import fm.dian.hdui.wximage.choose.ImageChooseActivity;
import fm.dian.hdui.wximage.choose.TakePictureActivity;
import fm.dian.service.core.HDTableUser.HDUser.GenderType;

public class HDUserEditActivity extends HDBase2Activity {

    public static final int REQUEST_CODE_EDIT_NAME = 1;
    public static final int REQUEST_CODE_EDIT_SIGNATURE = 2;
    public static final int REQUEST_CODE_EDIT_AVATAR = 3;

    private HDImageLoadUtil imageLoadUtil;

    private TextView userName;
    private TextView userGender;
    private ImageView userAvatar;
    private TextView signature;

    private CoreService coreService = CoreService.getInstance();
    private AuthService authService = AuthService.getInstance();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.getInstance().pushActivity(this);
        if (new ActivityCheck().checkAndGoIndex(this)) {
            return;
        }

        setContentView(R.layout.activity_user_edit);
        imageLoadUtil = new HDImageLoadUtil();

        userName = (TextView) findViewById(R.id.userName);
        userGender = (TextView) findViewById(R.id.userGender);
        userAvatar = (ImageView) findViewById(R.id.userAvatar);
        signature = (TextView) findViewById(R.id.signature);

        User user = HDApp.getInstance().getCurrUser();
        setUser(user);
    }

    private void setUser(final User user) {
        try {
            if (user == null) {
                return;
            }

            userName.setText(user.getNickname());
            signature.setText(user.getSignature());

            if (user.getGender() == GenderType.MALE) {
                userGender.setText("男");
            } else if (user.getGender() == GenderType.FEMALE) {
                userGender.setText("女");
            } else {
                userGender.setText("");
            }

            if (!imageLoadUtil.isLoadingComplete(userAvatar, user.getAvatar())) {
                HDApp.getInstance().imageLoader.displayImage(user.getAvatar(), userAvatar, Config.getUserConfig(), new SimpleImageLoadingListener() {
                    public void onLoadingComplete(String url, View arg1, Bitmap arg2) {
                        imageLoadUtil.setImageLoad(userAvatar, user.getAvatar());
                    }
                });
            }

        } catch (Throwable e) {
            if(e.getMessage()!=null){
                Log.e("setUser [ERROR]: ", e.getMessage());
            }
        }
    }

    public void back(View v) {
        setResult(RESULT_OK);
        finish();
    }

    public void userName(View v) {
        Intent intent = new Intent(getApplicationContext(), HDUserEditNameActivity.class);
        startActivityForResult(intent, REQUEST_CODE_EDIT_NAME);
    }

    public void signature(View v) {
        Intent intent = new Intent(getApplicationContext(), HDUserEditSignatureActivity.class);
        startActivityForResult(intent, REQUEST_CODE_EDIT_SIGNATURE);
    }

    public void userGender(View v) {
        final Dialog builder = new Dialog(this, R.style.HDDialog);
        builder.setContentView(R.layout.activity_user_edit_gender_dialog);
        builder.setCanceledOnTouchOutside(true);
        builder.show();

        final User user = HDApp.getInstance().getCurrUser();

        if (user.getGender() == GenderType.MALE) {
            builder.findViewById(R.id.gender11).setVisibility(View.VISIBLE);
        } else if (user.getGender() == GenderType.FEMALE) {
            builder.findViewById(R.id.gender01).setVisibility(View.VISIBLE);
        }

        builder.findViewById(R.id.gender1).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {

                if (user.getGender() == GenderType.MALE) {
                    builder.dismiss();
                    return;
                }
                user.setGender(GenderType.MALE);

                builder.findViewById(R.id.gender11).setVisibility(View.VISIBLE);
                builder.findViewById(R.id.gender01).setVisibility(View.GONE);

                coreService.updateUserInfo(user, new CallBack() {
                    @Override
                    public void process(Bundle data) {
                        builder.dismiss();
                        int e = data.getInt("error_code");
                        if (CoreService.OK == e) {
                            User user = (User) data.getSerializable("user");
                            setUser(user);
                            HDApp.getInstance().setCurrUser(user);
                        } else {
                            Toast.makeText(getApplicationContext(), "修改性别失败" + e, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

        builder.findViewById(R.id.gender0).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (user.getGender() == GenderType.FEMALE) {
                    builder.dismiss();
                    return;
                }

                user.setGender(GenderType.FEMALE);

                builder.findViewById(R.id.gender11).setVisibility(View.GONE);
                builder.findViewById(R.id.gender01).setVisibility(View.VISIBLE);

                coreService.updateUserInfo(user, new CallBack() {
                    @Override
                    public void process(Bundle data) {
                        builder.dismiss();
                        int e = data.getInt("error_code");
                        if (CoreService.OK == e) {
                            User user = (User) data.getSerializable("user");
                            setUser(user);
                            HDApp.getInstance().setCurrUser(user);
                        } else {
                            Toast.makeText(getApplicationContext(), "修改性别失败" + e, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    public void userAvatar(View v) {
        final Dialog builder = new Dialog(this, R.style.HDDialog);
        builder.setContentView(R.layout.activity_room_edit_avatar_dialog);
        builder.setCanceledOnTouchOutside(true);
        builder.show();

        builder.findViewById(R.id.share).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                builder.dismiss();
                Intent intent = new Intent(getApplicationContext(), TakePictureActivity.class);
                startActivityForResult(intent, REQUEST_CODE_EDIT_AVATAR);
            }
        });

        builder.findViewById(R.id.editWebaddr).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                builder.dismiss();
                Intent intent = new Intent(getApplicationContext(), ImageChooseActivity.class);
                startActivityForResult(intent, REQUEST_CODE_EDIT_AVATAR);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_EDIT_NAME:
            case REQUEST_CODE_EDIT_SIGNATURE:
                if (resultCode == RESULT_OK) {
                    User user = HDApp.getInstance().getCurrUser();
                    setUser(user);
                }
                break;
            case REQUEST_CODE_EDIT_AVATAR:
                if (HDBaseTabFragmentActivity.chooseBitmapPathLocal != null) {
                    Bitmap scaleBitmap = ImageScaleUtil.scaleBitmap(HDBaseTabFragmentActivity.chooseBitmapPathLocal);
                    HDBaseTabFragmentActivity.chooseBitmapPathLocal = null;
                    userAvatar.setImageBitmap(scaleBitmap);
                }

                if (HDBaseTabFragmentActivity.chooseBitmapPathRemote != null) {
                    String avatar = HDBaseTabFragmentActivity.chooseBitmapPathRemote;
                    HDBaseTabFragmentActivity.chooseBitmapPathRemote = null;

                    User user = HDApp.getInstance().getCurrUser();
                    user.setAvatar(avatar);

                    coreService.updateUserInfo(user, new CallBack() {
                        @Override
                        public void process(Bundle data) {
                            int e = data.getInt("error_code");
                            if (CoreService.OK == e) {
                                User user = (User) data.getSerializable("user");
                                HDApp.getInstance().setCurrUser(user);
                            }
                        }
                    });

                }
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }
}
