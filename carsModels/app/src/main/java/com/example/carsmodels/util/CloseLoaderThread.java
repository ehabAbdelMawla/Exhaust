package com.example.carsmodels.util;

import com.example.carsmodels.util.Loader.Loader;

/**
 * This Thread Used To Await after a working Thread End And Close Loader
 */
public class CloseLoaderThread extends Thread {
    /**
     * Instance Attributes
     */
    private Thread workingThread;
    private Loader loaderDialog;

    /**
     * Constructor
     */
    public CloseLoaderThread(Thread workingThread, Loader loaderDialog) {
        this.workingThread = workingThread;
        this.loaderDialog = loaderDialog;
    }


    @Override
    public void run() {
        try {
            workingThread.start();
            workingThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        loaderDialog.dismissLoader();
    }
}
