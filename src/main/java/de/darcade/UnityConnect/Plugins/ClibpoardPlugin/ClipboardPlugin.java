package de.darcade.UnityConnect.Plugins.ClibpoardPlugin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.widget.Button;

import de.darcade.UnityConnect.NetworkPackage;
import de.darcade.UnityConnect.Plugins.Plugin;
import org.kde.kdeconnect_tp.R;

public class ClipboardPlugin extends Plugin {

    /*static {
        PluginFactory.registerPlugin(ClipboardPlugin.class);
    }*/

    @Override
    public String getPluginName() {
        return "plugin_clipboard";
    }
    @Override
    public String getDisplayName() {
        return context.getResources().getString(R.string.pref_plugin_clipboard);
    }

    @Override
    public String getDescription() {
        return context.getResources().getString(R.string.pref_plugin_clipboard_desc);
    }

    @Override
    public Drawable getIcon() {
        return context.getResources().getDrawable(R.drawable.icon);
    }

    @Override
    public boolean isEnabledByDefault() {
        return (Build.VERSION.SDK_INT >= 11);
    }

    private ClipboardListener listener;

    @Override
    public boolean onCreate() {

        if (Build.VERSION.SDK_INT < 11) {
            return false;
        }

        listener = new ClipboardListener(context, device);

        return true;

    }

    @Override
    public void onDestroy() {

        if (Build.VERSION.SDK_INT < 11) return;

        listener.stop();

    }

    @Override
    public boolean onPackageReceived(NetworkPackage np) {

        if (!np.getType().equals(NetworkPackage.PACKAGE_TYPE_CLIPBOARD)) {
            return false;
        }

        String content = np.getString("content");
        listener.setText(content);
        return true;

    }

    @Override
    public AlertDialog getErrorDialog(Context baseContext) {
        return new AlertDialog.Builder(baseContext)
                .setTitle(R.string.pref_plugin_clipboard)
                .setMessage(R.string.plugin_not_available)
                .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create();
    }

    @Override
    public Button getInterfaceButton(Activity activity) {
        return null;
    }
}
