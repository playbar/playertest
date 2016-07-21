package com.rednovo.ace.core.session;

import com.rednovo.ace.communication.EventInvokerManager;
import com.rednovo.ace.communication.client.ClientSession;

public class SessionEngine {
    private static SessionEngine sessionEngine = null;
    private SessionPackageListener mSessionPackageListener = null;
    private SessionPackParser mSessionPackParser = null;
    private ClientSession clientSesstion = null;

    public static synchronized SessionEngine getSessionEngine() {
        if (sessionEngine == null) {
            sessionEngine = new SessionEngine();
        }
        return sessionEngine;

    }

    public SessionEngine() {
        mSessionPackParser = new SessionPackParser();
        mSessionPackageListener = new SessionPackageListener(mSessionPackParser);
    }

    public void start() {
        try {
            EventInvokerManager mgr = EventInvokerManager.getInstance();
            mgr.registInvoker(mSessionPackageListener);
            // 注入心跳UID
            clientSesstion = ClientSession.getInstance();
            clientSesstion.openSession();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void close() {

        if (clientSesstion != null) {
            try {
                clientSesstion.stopHeartBeat();
                clientSesstion.closeSession();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
