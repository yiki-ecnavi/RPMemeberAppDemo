/**
 * Copyright © VOYAGE GROUP, Inc. 
 * Author: yiki
 * Last modified: $date$
 */
package jp.researchpanel.rpmemberapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

/**
 * Dialog Helper Class
 */
public class DialogHelper {

	private static final OnClickListener sCancelClickListener = new OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			dialog.dismiss();
		}
	};

	public enum DialogId {
		EXIT_APPLICATION, // アプリ終了
		OPEN_EXTERNAL_BROWSER,
	};

	public static void showDialog(DialogId dialogId, Context context,
			OnClickListener positiveListener) {
		showDialog(dialogId, context, positiveListener, sCancelClickListener);
	}

	public static void showDialog(DialogId dialogId, Context context,
			OnClickListener positiveListener, OnClickListener negativeListener) {
		if (negativeListener == null)
			negativeListener = sCancelClickListener;

		switch (dialogId) {
		case EXIT_APPLICATION:
			showExitApplicationDialog(context, positiveListener,
					negativeListener);
			break;
		case OPEN_EXTERNAL_BROWSER:
			openExternalBrowserDialog(context, positiveListener,
					negativeListener);
			break;
		default:
			break;
		}
	}

	private static void showExitApplicationDialog(Context context,
			OnClickListener positiveListener, OnClickListener negativeListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.dialog_warning))
				.setMessage(
						context.getString(R.string.dialog_application_exit_confirm))
				.setNegativeButton(context.getString(R.string.dialog_cancel),
						negativeListener)
				.setPositiveButton(context.getString(R.string.dialog_exit),
						positiveListener);
        AlertDialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}

	private static void openExternalBrowserDialog(Context context,
			OnClickListener positiveListener, OnClickListener negativeListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.dialog_warning))
                .setMessage(
                        context.getString(R.string.dialog_open_external_browser_confirm))
                .setNegativeButton(context.getString(R.string.dialog_cancel),
                        negativeListener)
                .setPositiveButton(context.getString(R.string.dialog_open),
                        positiveListener);
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
	}

}
