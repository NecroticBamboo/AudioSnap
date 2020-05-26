package com.NecroticBamboo.AudioSnap;

import be.tarsos.dsp.*;
import be.tarsos.dsp.io.jvm.AudioDispatcherFactory;
import be.tarsos.dsp.io.jvm.AudioPlayer;
import be.tarsos.dsp.resample.RateTransposer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class Player {

    private static File file;
    private static GraphPanel graphPanel;
    private static FileManipulation fileManipulation;
    private static AudioFormat format;

    private static AudioPlayer audioPlayer;
    private static AudioDispatcher dispatcher;
    private static WaveformSimilarityBasedOverlapAdd wsola;
    private static RateTransposer rateTransposer;
    private AudioProcessor processor;

    private double currentTime;
    private double pausedAt;
    private String state="";
    private ICallBack callBack;
    private boolean loop=false;


    public Player(File fileIn,FileManipulation fileManipulationIn,GraphPanel graphPanelIn,ICallBack callBackIn){
        file=fileIn;
        fileManipulation=fileManipulationIn;
        graphPanel=graphPanelIn;
        callBack=callBackIn;
    }

    public void playFile(){
        playFile(pausedAt,0);
    }

    public void playFile(double startTime,double endTime) {
        try {
            if(!state.equals("playing")) {

                currentTime = 0;
                state = "playing";
                format = AudioSystem.getAudioFileFormat(file).getFormat();
                wsola = new WaveformSimilarityBasedOverlapAdd(WaveformSimilarityBasedOverlapAdd.Parameters.musicDefaults(fileManipulation.getFactor(), format.getSampleRate()));

                rateTransposer = new RateTransposer(fileManipulation.getFactor());
                audioPlayer = new AudioPlayer(format);

//            JVMAudioInputStream test=new JVMAudioInputStream(new AudioInputStream((TargetDataLine) file));

                if (format.getChannels() != 1) {
                    dispatcher = AudioDispatcherFactory.fromFile(file, wsola.getInputBufferSize() * format.getChannels(), wsola.getOverlap() * format.getChannels());
                    dispatcher.addAudioProcessor(new MultichannelToMono(format.getChannels(), true));
                } else {
                    dispatcher = AudioDispatcherFactory.fromFile(file, wsola.getInputBufferSize(), wsola.getOverlap());
                }

                processor = new SoundLevelDetector(graphPanel);

                wsola.setDispatcher(dispatcher);
                dispatcher.skip(startTime);
                dispatcher.addAudioProcessor(new AudioProcessor() {
                    @Override
                    public boolean process(AudioEvent audioEvent) {
                        currentTime = audioEvent.getTimeStamp();
                        if(Math.round(currentTime) == endTime){
                            pauseFile();
                        }
                        else{
                            callBack.callBack((int) currentTime);
                        }
                        return true;
                    }

                    @Override
                    public void processingFinished() {
                        if (state.equals("playing")) {
                            state = "stopped";
                        }
                        if(loop){
                            playFile(startTime,endTime);
                        }
                    }
                });

                dispatcher.addAudioProcessor(wsola);
                dispatcher.addAudioProcessor(processor);
                dispatcher.addAudioProcessor(rateTransposer);
                dispatcher.addAudioProcessor(fileManipulation.getVolume());

                if (fileManipulation.getEcho()) {
                    dispatcher.addAudioProcessor(fileManipulation.getInputGain());
                    dispatcher.addAudioProcessor(fileManipulation.getDelayEffect());
                }

                dispatcher.addAudioProcessor(audioPlayer);

                Thread start = new Thread(dispatcher);
                start.start();
            }

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException unsupportedAudioFileException) {
            unsupportedAudioFileException.printStackTrace();
        }
    }

    public void stopFile(){
        if(state.equals("playing") || state.equals("paused")){
            state="stopped";
            currentTime=0;
            pausedAt=0;
            loop=false;
            dispatcher.stop();
        }
    }

    public void pauseFile(){
        if(state.equals("playing")){
            state="paused";
            pauseFile(currentTime);
        }
    }

    private void pauseFile(double time){
        pausedAt=time;
        dispatcher.stop();
    }

    public double getPausedAt(){
        return pausedAt;
    }

    public void loopPart(boolean loopIn,double startTime,double endTime){
        loop=loopIn;
        playFile(startTime,endTime);
    }
}
