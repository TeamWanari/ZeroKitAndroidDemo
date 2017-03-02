package com.wanari.zerokit.zerokitdemo.utils;

import com.wanari.zerokit.zerokitdemo.ZerokitApplication;

import android.content.ClipData;
import android.content.ClipboardManager;

import static android.content.Context.CLIPBOARD_SERVICE;

public class CommonUtils {

    public static void copyToClipBoard(String label, String toCopy) {
        ClipboardManager clipboard = (ClipboardManager) ZerokitApplication.getInstance().getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, toCopy);
        clipboard.setPrimaryClip(clip);
    }

    public static String trimInvitationUrl(String invitationUrl) {
        if (invitationUrl != null) {
            String[] splitted = invitationUrl.split("#");
            return splitted[splitted.length - 1];
        } else {
            return "";
        }
    }
}
