package com.NecroticBamboo.AudioSnap;

import javax.swing.*;

public class CallBack implements ICallBack {

    private final JSlider slider;
    private final IQuietListener listener;

    public CallBack(JSlider sliderIn,IQuietListener listenerIn ){
        slider=sliderIn;
        listener=listenerIn;
    }

    @Override
    public void callBack(int timeStamp) {
        try{
            listener.mute(true);
            slider.setValue(timeStamp);
            slider.repaint();
        } finally {
            listener.mute(false);
        }

    }
}
