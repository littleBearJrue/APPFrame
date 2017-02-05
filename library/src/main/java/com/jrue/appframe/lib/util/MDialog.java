package com.jrue.appframe.lib.util;

import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jrue.appframe.lib.R;
import com.jrue.appframe.lib.widget.PickerView;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Dialog基础框架
 */
public class MDialog {
    private static final String TAG = "MDialog";
    public static final int BYTES_NAME = 9;
    private static final int REMOTE_BYTE_NAME = 16;
    private static final int REMOTE_BYTE_TEXT_LIMIT = 32;
    private static final String GB18030 = "GB18030";
    private static final String UTF_8 = "UTF-8";

    private MDialog() {
        throw new AssertionError("Don't create " + TAG);
    }

    /**
     * 得到自定义的progressDialog
     */
    public static Dialog showWaitDialog(Context context, String msg) {
        return showWaitDialog(context, msg, false);
    }

    /**
     * 得到自定义的progressDialog
     */
    public static Dialog showWaitDialog(Context context, String msg, boolean isTransBg) {
        return showWaitDialog(context, msg, isTransBg, false);
    }

    /**
     * 得到自定义的progressDialog
     */
    public static Dialog showWaitDialog(Context context, String msg, boolean isTransBg, boolean isCancelable) {
        //final int finalType = type;
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.dialog_loading, null);             // 得到加载view
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局

        // main.xml中的ImageView
        ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img);
        TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);   // 提示文字
        // 加载动画
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(context, R.anim.rotate_animation);
        // 使用ImageView显示动画
        spaceshipImage.startAnimation(hyperspaceJumpAnimation);
        tipTextView.setText(msg);// 设置加载信息

        Dialog loadingDialog = new Dialog(context, isTransBg ? R.style.TransDialogStyle : R.style.WhiteDialogStyle);    // 创建自定义样式dialog
        loadingDialog.setContentView(layout);
        loadingDialog.setCancelable(isCancelable);
        loadingDialog.setCanceledOnTouchOutside(false);

        Window window = loadingDialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setGravity(Gravity.BOTTOM);
        window.setAttributes(lp);
        window.setWindowAnimations(R.style.PopWindowAnimStyle);
        if (MLog.DEBUG) MLog.zlx.d(TAG, "makeWaitDialog: lp.height=" + lp.height);
        loadingDialog.show();
        return loadingDialog;
    }

    public static Dialog showCommonDialog(Context context, int iconId, int titleId, int promptId, int operateId, final OnConfirmBtnListener3 onConfirmBtnListener) {
        return showCommonDialog(context, iconId, (titleId == 0) ? null : context.getString(titleId), (promptId == 0) ? null : context.getString(promptId), (operateId == 0) ? null : context.getString(operateId), onConfirmBtnListener);
    }

    public interface OnConfirmBtnListener3 {
        void onConfirmBtn();
    }

    public static Dialog showCommonDialog(Context context, int iconId, String title, String prompt, String operate, final OnConfirmBtnListener3 onConfirmBtnListener) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.common_popup_layout, null);             // 得到加载view
        final Dialog dialog = new Dialog(context, R.style.WhiteDialogStyle);    // 创建自定义样式dialog
        dialog.setContentView(layout);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);

        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wManager.getDefaultDisplay().getMetrics(dm);
        lp.width = dm.widthPixels/* - MUtils.dip2px(context, 60)*/; //设置宽度

        //lp.height = MUtils.dip2px(context, getHeightBySize(list.size()));
        lp.y = dm.heightPixels - lp.height;
        Window window = dialog.getWindow();
        window.setAttributes(lp);
        window.setWindowAnimations(R.style.PopWindowAnimStyle);
        ((ImageView) layout.findViewById(R.id.ivIcon)).setImageResource(iconId);
        if (!TextUtils.isEmpty(title)) {
            ((TextView) layout.findViewById(R.id.tvTitle)).setText(title);
        }
        if (!TextUtils.isEmpty(prompt)) {
            ((TextView) layout.findViewById(R.id.tvPrompt)).setText(prompt);
        }

        Button btnOperate = (Button) layout.findViewById(R.id.btnOperate);
        btnOperate.setText(operate);
        btnOperate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onConfirmBtnListener != null) {
                    onConfirmBtnListener.onConfirmBtn();
                }
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        dialog.show();
        return dialog;
    }


    public static Dialog showConfirmDialog(Context context, CharSequence title, CharSequence message, CharSequence cancelMessage,
                                           CharSequence confirmMessage, final OnConfirmListener confirmListener) {
        final Dialog dialog = new Dialog(context, R.style.mDialog);
        final View view = LayoutInflater.from(context).inflate(R.layout.dialog_confirm_layout, null);
        TextView titleTextView = (TextView) view.findViewById(R.id.title);
        TextView messageTextView = (TextView) view.findViewById(R.id.confirmText);
        if (!TextUtils.isEmpty(title)) {
            titleTextView.setVisibility(View.VISIBLE);
            titleTextView.setText(title);
        }
        if (!TextUtils.isEmpty(message)) {
            messageTextView.setVisibility(View.VISIBLE);
            messageTextView.setText(message);
        } else {
            messageTextView.setVisibility(View.GONE);
        }
        Button okBtn = (Button) view.findViewById(R.id.okBtn);
        if (confirmMessage != null)
            okBtn.setText(confirmMessage);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (confirmListener != null) {
                    confirmListener.onConfirm(view);
                    dialog.dismiss();
                }
            }
        });
        Button cancelBtn = (Button) view.findViewById(R.id.cancelBtn);
        if (cancelMessage != null) {
            cancelBtn.setText(cancelMessage);
        }
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.setContentView(view);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wManager.getDefaultDisplay().getMetrics(dm);
        lp.width = dm.widthPixels - MUtils.dip2px(context, 60); //设置宽度
        lp.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN;
        Window window = dialog.getWindow();
        window.setAttributes(lp);
        dialog.show();
        return dialog;
    }

    public static Dialog showConfirmDialog(Context context, CharSequence title, CharSequence message, final OnConfirmListener confirmListener) {
        return showConfirmDialog(context, title, message, null, null, confirmListener);
    }

    public static Dialog showInputDialog(Context context, CharSequence title, CharSequence editText, CharSequence editHint, CharSequence message,
                                         final OnInputListener onInputListener, int maxInputLength) {
        final Dialog dialog = new Dialog(context, R.style.mDialog);
        final View view = LayoutInflater.from(context).inflate(R.layout.dialog_input_layout, null);
        TextView titleTextView = (TextView) view.findViewById(R.id.title);
        final EditText editView = (EditText) view.findViewById(R.id.name);
        TextView promptText = (TextView) view.findViewById(R.id.promptText);
        titleTextView.setText(title);

        if (maxInputLength > 0) {
            editView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxInputLength)});
        }
        if (!TextUtils.isEmpty(editHint)) {
            editView.setHint(editHint);
        }
        if (!TextUtils.isEmpty(editText)) {
            editView.setText(editText);
            editView.setSelection(editText.length());
        }
        if (!TextUtils.isEmpty(title)) {
            titleTextView.setVisibility(View.VISIBLE);
            titleTextView.setText(title);
        }
        if (!TextUtils.isEmpty(message)) {
            promptText.setVisibility(View.VISIBLE);
            promptText.setText(message);
        } else {
            promptText.setVisibility(View.GONE);
        }
        Button okBtn = (Button) view.findViewById(R.id.okBtn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(editView.getText().toString())) {
                    dialog.dismiss();
                }
                if (onInputListener != null) {
                    onInputListener.onInput(editView.getText());
                }
            }
        });
        Button cancelBtn = (Button) view.findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        dialog.setContentView(view);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wManager.getDefaultDisplay().getMetrics(dm);
        lp.width = dm.widthPixels - MUtils.dip2px(context, 60); //设置宽度
        lp.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE;
        dialog.getWindow().setAttributes(lp);
        dialog.show();
        return dialog;
    }

    public static Dialog showRenameDialog(Context context, CharSequence title, CharSequence editText, CharSequence editHint,
                                          final OnInputListener onInputListener) {
        return showInputDialog(context, title, editText, editHint, null, onInputListener, 32);
    }

    public static Dialog showRenameInputDialog(Context context, CharSequence title, CharSequence editText, CharSequence editHint,
                                               final OnInputListener onInputListener) {
        final Dialog dialog = new Dialog(context, R.style.mDialog);
        final View view = LayoutInflater.from(context).inflate(R.layout.dialog_input_layout, null);
        TextView titleTextView = (TextView) view.findViewById(R.id.title);
        final EditText editView = (EditText) view.findViewById(R.id.name);
        TextView promptTextView = (TextView) view.findViewById(R.id.promptText);
        promptTextView.setVisibility(View.VISIBLE);
        promptTextView.setText(context.getString(R.string.home_device_rename_prompt));
        editView.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        titleTextView.setText(title);
        editView.setHint(editHint);
        editView.setText(editText);
        editView.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (editView.getText().toString().getBytes(Charset.forName(GB18030)).length > BYTES_NAME) {
                    String str = editView.getText().toString();
                    editView.setText(str.substring(0, str.length() - 1));
                    editView.setSelection(editView.getText().toString().length());
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        MUtils.popUpSoftKeyboard(editView, context);//默认弹出软键盘
        Button okBtn = (Button) view.findViewById(R.id.okBtn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onInputListener.onInput(editView.getText());
            }
        });
        Button cancelBtn = (Button) view.findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.setContentView(view);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wManager.getDefaultDisplay().getMetrics(dm);
        lp.width = dm.widthPixels - MUtils.dip2px(context, 60); //设置宽度
        dialog.getWindow().setAttributes(lp);
        dialog.show();
        return dialog;
    }

    //遥控器应用使用输入框
    public static Dialog showRemoteInputDialog(Context context, CharSequence title, CharSequence editText, CharSequence editHint,
                                               final OnInputListener onInputListener) {
        final Dialog dialog = new Dialog(context, R.style.mDialog);
        final View view = LayoutInflater.from(context).inflate(R.layout.dialog_input_layout, null);
        TextView titleTextView = (TextView) view.findViewById(R.id.title);
        final EditText editView = (EditText) view.findViewById(R.id.name);
        TextView promptTextView = (TextView) view.findViewById(R.id.promptText);
        promptTextView.setVisibility(View.GONE);
        editView.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        titleTextView.setText(title);
        editView.setHint(editHint);
        editView.setText(editText);
        editView.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (editView.getText().toString().getBytes(Charset.forName(UTF_8)).length > REMOTE_BYTE_TEXT_LIMIT) {
                    String str = editView.getText().toString();
                    editView.setText(str.substring(0, str.length() - 1));
                    editView.setSelection(editView.getText().toString().length());
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        MUtils.popUpSoftKeyboard(editView, context);//默认弹出软键盘
        Button okBtn = (Button) view.findViewById(R.id.okBtn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onInputListener.onInput(editView.getText());
            }
        });
        Button cancelBtn = (Button) view.findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.setContentView(view);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wManager.getDefaultDisplay().getMetrics(dm);
        lp.width = dm.widthPixels - MUtils.dip2px(context, 60); //设置宽度
        dialog.getWindow().setAttributes(lp);
        dialog.show();
        return dialog;
    }

    public static Dialog modifyRemoteNameDialog(Context context, CharSequence title, final CharSequence editText, CharSequence editHint,
                                                final OnInputListener onInputListener) {
        final Dialog dialog = new Dialog(context, R.style.mDialog);
        final View view = LayoutInflater.from(context).inflate(R.layout.dialog_input_layout, null);
        TextView titleTextView = (TextView) view.findViewById(R.id.title);
        final EditText editView = (EditText) view.findViewById(R.id.name);
        TextView promptTextView = (TextView) view.findViewById(R.id.promptText);
        promptTextView.setVisibility(View.GONE);
        editView.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        titleTextView.setText(title);
        editView.setHint(editHint);
        editView.setText(editText);
        editView.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (editView.getText().toString().getBytes(Charset.forName(UTF_8)).length > REMOTE_BYTE_NAME) {
                    String str = editView.getText().toString();
                    editView.setText(str.substring(0, str.length() - 1));
                    editView.setSelection(editView.getText().toString().length());
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        MUtils.popUpSoftKeyboard(editView, context);//默认弹出软键盘
        Button okBtn = (Button) view.findViewById(R.id.okBtn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onInputListener.onInput(editView.getText());
            }
        });
        Button cancelBtn = (Button) view.findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.setContentView(view);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wManager.getDefaultDisplay().getMetrics(dm);
        lp.width = dm.widthPixels - MUtils.dip2px(context, 60); //设置宽度
        dialog.getWindow().setAttributes(lp);
        dialog.show();
        return dialog;
    }

    //提示框
    public static Dialog showWarningDialog(Context context, CharSequence title, CharSequence message, final OnConfirmListener listener) {
        final Dialog dialog = new Dialog(context, R.style.mDialog);
        final View view = LayoutInflater.from(context).inflate(R.layout.dialog_prompt_layout, null);
        Button okBtn = (Button) view.findViewById(R.id.okBtn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if (null != listener) {
                    listener.onConfirm(view);
                }
            }
        });
        Button cancelBtn = (Button) view.findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        TextView titleTV = (TextView) view.findViewById(R.id.title);
        titleTV.setText(title);
        TextView promptContentTV = (TextView) view.findViewById(R.id.promptContent);
        promptContentTV.setText(message);
        dialog.setContentView(view);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wManager.getDefaultDisplay().getMetrics(dm);
        lp.width = dm.widthPixels - MUtils.dip2px(context, 60); //设置宽度
        dialog.getWindow().setAttributes(lp);
        dialog.show();
        return dialog;
    }

    //用户操作提示框（一个按键）
    public static Dialog showHintingDialog(Context context, CharSequence message, final OnConfirmListener listener) {
        final Dialog dialog = new Dialog(context, R.style.mDialog);
        final View view = LayoutInflater.from(context).inflate(R.layout.dialog_hint_layout, null);
        Button okBtn = (Button) view.findViewById(R.id.confirmBtn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != listener) {
                    listener.onConfirm(view);
                }
                dialog.dismiss();
            }
        });
        TextView promptContentTV = (TextView) view.findViewById(R.id.hintContent);
        promptContentTV.setText(message);
        dialog.setContentView(view);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wManager.getDefaultDisplay().getMetrics(dm);
        lp.width = dm.widthPixels - MUtils.dip2px(context, 60);
        dialog.getWindow().setAttributes(lp);
        dialog.show();
        return dialog;
    }

    //带icon的用户提示框
    public static Dialog showWeakInformationDialog(Context context, int icon, CharSequence message) {
        final Dialog dialog = new Dialog(context, R.style.mDialog);
        final View view = LayoutInflater.from(context).inflate(R.layout.dialog_weak_signal_layout, null);
        Button okBtn = (Button) view.findViewById(R.id.confirmBtn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        ImageView hintIcon = (ImageView) view.findViewById(R.id.dialogHintIcon);
        hintIcon.setImageResource(icon);
        TextView promptContentTV = (TextView) view.findViewById(R.id.hintContent);
        promptContentTV.setText(message);
        dialog.setContentView(view);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wManager.getDefaultDisplay().getMetrics(dm);
        lp.width = dm.widthPixels - MUtils.dip2px(context, 60);
        dialog.getWindow().setAttributes(lp);
        dialog.show();
        return dialog;
    }

    //不带标题的提示框
    public static Dialog showDoubleChoiceDialog(Context context, CharSequence message, String leftButtonText, String rightButtonText, final OnConfirmListener listener, final onErrorListener errorListener) {
        final Dialog dialog = new Dialog(context, R.style.mDialog);
        final View view = LayoutInflater.from(context).inflate(R.layout.dialog_double_choice_layout, null);
        Button confirmBtn = (Button) view.findViewById(R.id.confirmBtn);
        confirmBtn.setText(rightButtonText);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if (null != listener) {
                    listener.onConfirm(view);
                }
            }
        });
        Button errorBtn = (Button) view.findViewById(R.id.errorBtn);
        errorBtn.setText(leftButtonText);
        errorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (null != errorListener) {
                    errorListener.onError(v);
                }

            }
        });
        TextView doubleChoiceContent = (TextView) view.findViewById(R.id.doubleChoiceContent);
        doubleChoiceContent.setText(message);
        dialog.setContentView(view);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wManager.getDefaultDisplay().getMetrics(dm);
        lp.width = dm.widthPixels - MUtils.dip2px(context, 60);
        dialog.getWindow().setAttributes(lp);
        dialog.show();
        return dialog;
    }

    public static Dialog showUpdateHintDialog(Context context, CharSequence message, CharSequence versionHint, CharSequence Version, long size, final OnConfirmListener listener) {
        final Dialog dialog = new Dialog(context, R.style.mDialog);
        final View view = LayoutInflater.from(context).inflate(R.layout.dialog_update_choice_layout, null);
        Button confirmBtn = (Button) view.findViewById(R.id.updateBtn);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if (null != listener) {
                    listener.onConfirm(view);
                }
            }
        });

        TextView updateVersionHint = (TextView) view.findViewById(R.id.updateVersionHint);
        TextView updateContent = (TextView) view.findViewById(R.id.updateContent);
        TextView updateVersion = (TextView) view.findViewById(R.id.updateVersion);
        TextView updateSize = (TextView) view.findViewById(R.id.updateSize);
        updateVersionHint.setText(versionHint);
        updateVersion.setText(Version);
        updateSize.setText(context.getString(R.string.dialog_update_version_size, MUtils.getDataSize(size)));
        updateContent.setText(message);
        dialog.setContentView(view);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wManager.getDefaultDisplay().getMetrics(dm);
        lp.width = dm.widthPixels - MUtils.dip2px(context, 70);
        dialog.getWindow().setAttributes(lp);
        dialog.show();
        return dialog;
    }


    //时间滚轮
    public static Dialog showPickerDialog(Context context, final int hour, final int minute, final OnPickerListener onPickerListener) {
        final Dialog dialog = new Dialog(context, R.style.mDialog);
        final View view = LayoutInflater.from(context).inflate(R.layout.dialog_timer_layout, null);
        final String[] str = new String[2];
        Button okBtn = (Button) view.findViewById(R.id.okBtn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(str[0])) {
                    str[0] = String.valueOf(hour);
                }
                if (TextUtils.isEmpty(str[1])) {
                    str[1] = String.valueOf(minute);
                }
                onPickerListener.onPicker(str);
            }
        });
        Button cancelBtn = (Button) view.findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        PickerView mHourPickerView = (PickerView) view.findViewById(R.id.hourPickerView);
        PickerView mMinutePickerView = (PickerView) view.findViewById(R.id.minutePickerView);

        List<String> hours = new ArrayList<String>();
        List<String> minutes = new ArrayList<String>();
        for (int i = 0; i < 24; i++) {
            hours.add(i < 10 ? "0" + i : String.valueOf(i));
        }
        for (int i = 0; i < 60; i++) {
            minutes.add(i < 10 ? "0" + i : String.valueOf(i));
        }

        mHourPickerView.setData(hours);
        mHourPickerView.setSelected(hour);
        mHourPickerView.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                str[0] = text;
            }
        });

        mMinutePickerView.setData(minutes);
        mMinutePickerView.setSelected(minute);
        mMinutePickerView.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                str[1] = text;
            }
        });
        dialog.setContentView(view);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wManager.getDefaultDisplay().getMetrics(dm);
        lp.width = dm.widthPixels - MUtils.dip2px(context, 60); //设置宽度
        dialog.getWindow().setAttributes(lp);
        dialog.show();
        return dialog;
    }

    public static void dismissDialog(Dialog dialog) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }


    public interface OnInputListener {
        void onInput(CharSequence text);
    }

    public interface OnConfirmListener {
        void onConfirm(View v);
    }

    public interface onErrorListener {
        void onError(View v);
    }

    public interface OnConfirmBtnListener2 {
        void onConfirmBtn(long rx, long tx);
    }

    public interface OnPickerListener {
        void onPicker(String[] str);
    }
}