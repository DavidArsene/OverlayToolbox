package ro.davidarsene.overlaytoolbox;

import android.content.om.OverlayManagerTransaction;

interface IRootBridge {
    IBinder getService(in String name);

    void commit(in OverlayManagerTransaction transaction);
}
