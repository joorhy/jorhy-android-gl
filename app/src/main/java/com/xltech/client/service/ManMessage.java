package com.xltech.client.service;

import android.os.Message;

import com.xltech.client.ui.ActivityImage;
import com.xltech.client.ui.ActivityLogin;
import com.xltech.client.data.EnumMessage;
import com.xltech.client.ui.ActivityPlayer;
import com.xltech.client.ui.PopupCategory;

/**
 * Created by JooLiu on 2016/3/29.
 */
public class ManMessage {
    public static void DispatchRetLoginMessage() {
        Message message = new Message();
        message.what = EnumMessage.LOGIN_RETURN;

        ActivityLogin.myHandler.sendMessage(message);
    }

    public static void DispatchRefreshCategoryMessage() {
        Message message = new Message();
        message.what = EnumMessage.LOGIN_RETURN;

        PopupCategory.myHandler.sendMessage(message);
    }

    public static void DispatchOpenVideoMessage() {
        Message message = new Message();
        message.what = EnumMessage.OPEN_VIDEO;

        ActivityImage.myHandler.sendMessage(message);
    }

    public static void DispatchChangeVideoMessage() {
        Message message = new Message();
        message.what = EnumMessage.CHANGE_VIDEO;

        ActivityPlayer.myHandler.sendMessage(message);
    }
}
