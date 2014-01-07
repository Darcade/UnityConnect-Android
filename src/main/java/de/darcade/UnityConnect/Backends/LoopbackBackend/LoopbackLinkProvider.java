package de.darcade.UnityConnect.Backends.LoopbackBackend;

import android.content.Context;

import de.darcade.UnityConnect.Backends.BaseLinkProvider;
import de.darcade.UnityConnect.NetworkPackage;

public class LoopbackLinkProvider extends BaseLinkProvider {

    private Context context;

    public LoopbackLinkProvider(Context context) {
        this.context = context;
    }

    @Override
    public void onStart() {
        onNetworkChange();
    }

    @Override
    public void onStop() {

    }

    @Override
    public void onNetworkChange() {

        NetworkPackage np = NetworkPackage.createIdentityPackage(context);
        connectionAccepted(np, new LoopbackLink(this));

    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public String getName() {
        return "LoopbackLinkProvider";
    }
}
