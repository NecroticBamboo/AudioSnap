package com.NecroticBamboo.AudioSnap;

import be.tarsos.dsp.*;
import be.tarsos.dsp.effects.DelayEffect;

import javax.sound.sampled.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileManipulation implements ActionListener {

    private static final GainProcessor gain = new GainProcessor(1.0);
    private static double currentFactor = 1;

    private static boolean echoTriggered=false;
    private static DelayEffect delayEffect;
    private static GainProcessor inputGain;
    private static int defaultInputGain = 100;//%
    private static int defaultDelay = 200;//ms
    private static int defaultDecay = 50;//%

    private static File file;
    private static AudioFormat format;

//    private final static File csvOutputFile = new File("C:\\Users\\andre\\IdeaProjects\\AudioSnap\\csvHolder\\test.csv");
    private static final List<String[]> dataLines = new ArrayList<>();


    public FileManipulation(File fileIn) {
        file = fileIn;
        try {
            format=AudioSystem.getAudioFileFormat(file).getFormat();
        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }
    }

    public void changeVolume(double newVolume) {
        gain.setGain(newVolume);
    }

    public GainProcessor getVolume(){return gain;}

    public void changeFactor(double newFactor) {
       currentFactor=newFactor;
    }

    public double getFactor(){return currentFactor;}

    public void makeEcho(){
        delayEffect = new DelayEffect(defaultDelay/1000.0,defaultDecay/100.0,format.getSampleRate());
        inputGain = new GainProcessor(defaultInputGain/100.0);
        echoTriggered= !echoTriggered;
    }

    public boolean getEcho(){return echoTriggered;}
    public GainProcessor getInputGain(){return inputGain;}
    public DelayEffect getDelayEffect(){return delayEffect;}

    public double getFileLengthInSeconds(){
        AudioInputStream audioInputStream = null;
        try {
            audioInputStream = AudioSystem.getAudioInputStream(file);

        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }
        long frames = audioInputStream.getFrameLength();
        return Math.round((frames+0.0) / format.getFrameRate());
    }

    public void setDefaultSettings(){
        gain.setGain(1);
        currentFactor=1;
        defaultInputGain = 100;
        defaultDelay = 200;
        defaultDecay = 50;
        echoTriggered=false;
    }

    public String  showInformation(){
        return format.toString();
    }

//    public void givenDataArray_whenConvertToCSV_thenOutputCreated() throws IOException {
//        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
//            dataLines.stream()
//                    .map(this::convertToCSV)
//                    .forEach(pw::println);
//        }
//    }

//    public String convertToCSV(String[] data) {
//        return Stream.of(data)
//                .map(this::escapeSpecialCharacters)
//                .collect(Collectors.joining(","));
//    }

//    public String escapeSpecialCharacters(String data) {
//        String escapedData = data.replaceAll("\\R", " ");
//        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
//            data = data.replace("\"", "\"\"");
//            escapedData = "\"" + data + "\"";
//        }
//        return escapedData;
//    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

}
