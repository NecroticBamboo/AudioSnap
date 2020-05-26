package com.NecroticBamboo.AudioSnap;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;

public class SoundLevelDetector implements AudioProcessor {

    private final GraphPanel panel;

    public SoundLevelDetector(GraphPanel panelIn){
        panel = panelIn;
    }


    @Override
    public boolean process(AudioEvent audioEvent) {
        float [] buffer = audioEvent.getFloatBuffer();
        float avg = 0.0f;

        for (float v : buffer) {
            avg = avg + Math.abs(v);
        }

        avg=avg/buffer.length;
        panel.addDataPoint((avg*120)-60,System.currentTimeMillis());
//        position++;
//        dataLines.add(new String[]{position +". ",""+avg});
        return true;
    }

    @Override
    public void processingFinished() {
//        try {
//            givenDataArray_whenConvertToCSV_thenOutputCreated();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
