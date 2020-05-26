package com.NecroticBamboo.AudioSnap;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class SliderListener implements IQuietListener, ChangeListener {
    private boolean muted;
    private final Player audioPlayer;
    private final JSlider audioSlider;

    public SliderListener(Player audioPlayerIn, JSlider audioSliderIn){
        audioPlayer=audioPlayerIn;
        audioSlider=audioSliderIn;
    }

    @Override
    public void mute(boolean isMuted) {
        muted=isMuted;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (muted){
            return;
        }
        int test=audioSlider.getValue();
        System.out.println(test);
//        audioPlayer.playFile(test);
    }
}
