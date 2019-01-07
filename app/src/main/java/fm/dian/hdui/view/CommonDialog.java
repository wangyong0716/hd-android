package fm.dian.hdui.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.TextView;

import fm.dian.hdui.R;

@SuppressLint("InflateParams")
public class CommonDialog {
    //	String title, String message,String okBtnTxt,String cancelBtnTxt
    Dialog dialog;
    public CommonDialog(Context context, ButtonType type,
                        final DialogClickListener listener, String... params) {
        dialog = new Dialog(context, R.style.DialogStyle);
//        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.setContentView(R.layout.layout_common_dialog_fragment);

        TextView tv_title = (TextView) dialog.findViewById(R.id.title_text_view);
        TextView tv_msg = (TextView) dialog.findViewById(R.id.content_text_view);
        TextView v_ok = (TextView) dialog.findViewById(R.id.dialog_ok);
        TextView v_cancel = (TextView) dialog.findViewById(R.id.dialog_cancel);

        if (type == ButtonType.oneButton) {
            v_cancel.setVisibility(View.GONE);
            dialog.findViewById(R.id.view_button_part2).setVisibility(View.GONE);
        } else if(type == ButtonType.TowButton){
            v_cancel.setVisibility(View.VISIBLE);
            dialog.findViewById(R.id.view_button_part2).setVisibility(View.VISIBLE);
        }else if(type == ButtonType.alert1Type){
            v_cancel.setVisibility(View.GONE);
            dialog.findViewById(R.id.view_button_part2).setVisibility(View.GONE);
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }else if(type == ButtonType.alert2Type){
            v_cancel.setVisibility(View.VISIBLE);
            dialog.findViewById(R.id.view_button_part2).setVisibility(View.VISIBLE);
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }

        for (int i = 0; i < params.length; i++) {
            if (i == 0) {//message
                if (params[i] != null && params[i].length() > 0) {
                    tv_msg.setText(params[i]);
                } else {
                    tv_msg.setVisibility(View.GONE);
                }
            }

            if (i == 1) {//cancel button
                if (params[i] != null && params[i].length() > 0) {
                    v_cancel.setText(params[i]);
                }
            }

            if (i == 2) {//ok button
                if (params[i] != null && params[i].length() > 0) {
                    v_ok.setText(params[i]);
                }
            }

            if (i == 3) {//title
                if (params[i] != null && params[i].length() > 0) {
                    tv_title.setText(params[i]);
                    dialog.findViewById(R.id.view_button_part1).setVisibility(View.VISIBLE);
                    tv_title.setVisibility(View.VISIBLE);
                } else {
                    dialog.findViewById(R.id.view_button_part1).setVisibility(View.GONE);
                    tv_title.setVisibility(View.GONE);
                }
            }

        }


        v_ok.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (listener != null) {
                    listener.doPositiveClick();
                }

            }

        });

        v_cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (listener != null) {
                    listener.doNegativeClick();
                }
            }

        });

        dialog.setCanceledOnTouchOutside(false);//禁用所有外部点击
        dialog.show();
    }
    public void setTouchOutsideAndCalceled(boolean outside,boolean canceled){
        dialog.setCanceledOnTouchOutside(outside);
        dialog.setCancelable(canceled);
    }

    public void setAlertType(){
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
    }

    public static interface DialogClickListener {
        public void doPositiveClick();

        public void doNegativeClick();
    }

    public enum ButtonType {
        oneButton, TowButton, alert1Type,//悬浮在最上层的Activity
        alert2Type
    }

    public void dismissDialog(){
        dialog.dismiss();
    }
}