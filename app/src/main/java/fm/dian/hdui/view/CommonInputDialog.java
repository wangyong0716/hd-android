package fm.dian.hdui.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import fm.dian.hdui.R;

@SuppressLint("InflateParams")
public class CommonInputDialog {
    private Context mContext;

    //	String title, String message,String okBtnTxt,String cancelBtnTxt
    public CommonInputDialog(Context context, ButtonType type,
                             final DialogClickListener listener, String... params) {
        final Dialog dialog = new Dialog(context, R.style.DialogStyle);
        dialog.setContentView(R.layout.layout_common_input_dialog);
        this.mContext = context;
        TextView title_text_view = (TextView) dialog.findViewById(R.id.title_text_view);
        final EditText et_name = (EditText) dialog.findViewById(R.id.et_name);
        TextView v_ok = (TextView) dialog.findViewById(R.id.dialog_ok);
        TextView v_cancel = (TextView) dialog.findViewById(R.id.dialog_cancel);
        v_ok.postDelayed(new Runnable() {

            @Override
            public void run() {

                toggleKeyBoard();
            }
        }, 500);
        if (type == ButtonType.oneButton) {
            v_cancel.setVisibility(View.GONE);
            dialog.findViewById(R.id.view_button_part2).setVisibility(View.GONE);
        } else {
            v_cancel.setVisibility(View.VISIBLE);
            dialog.findViewById(R.id.view_button_part2).setVisibility(View.VISIBLE);
        }

        for (int i = 0; i < params.length; i++) {

            if (i == 0) {//cancel button
                if (params[i] != null && params[i].length() > 0) {
                    title_text_view.setText(params[i]);
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
        }


        v_ok.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (listener != null) {
                    String input = et_name.getText().toString();
                    listener.doPositiveClick(input);
                }
                toggleKeyBoard();
            }

        });

        v_cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (listener != null) {
                    listener.doNegativeClick();
                }
                toggleKeyBoard();
            }

        });

        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    public static interface DialogClickListener {
        public void doPositiveClick(String input);

        public void doNegativeClick();
    }

    public enum ButtonType {
        oneButton, TowButton
    }

    /**
     * 隐藏键盘
     */
    public void toggleKeyBoard() {
        // 收起软键盘
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }
}