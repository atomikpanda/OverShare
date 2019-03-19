// Bailey Seymour
// DVP6 - 1903
// CustomVisualizer.java

package com.baileyseymour.overshare.views;

import android.content.Context;
import android.util.AttributeSet;

import com.tyorikan.voicerecordingvisualizer.VisualizerView;

public class CustomVisualizer extends VisualizerView {
    public CustomVisualizer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    // Simply calls the protected receive method
    public void triggerReceive(int volume) {
        receive(volume);
    }

    // Estimates the volume for the visualizer view
    public static int calcVolume(byte[] bytes) {
        int sum = 0;
        for (byte aByte : bytes) {
            sum += Math.abs(aByte);
        }
        return sum / bytes.length;
    }
}
