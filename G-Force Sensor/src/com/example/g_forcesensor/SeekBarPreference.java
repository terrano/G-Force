package com.example.g_forcesensor;

import java.text.DecimalFormat;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class SeekBarPreference extends Preference implements OnSeekBarChangeListener {
    
    private static final int DEFAULT_VALUE = 30;    
	private int maxValue = 70;
    private int minValue = 20;
    private int mInterval = 1;
    private int mCurrentValue;
    private SeekBar mSeekBar;    
    private TextView mStatusText;    
    private DecimalFormat df2;

    public SeekBarPreference(Context context, AttributeSet attrs) {
        super(context, attrs); 

        this.df2 = new DecimalFormat("##.##");
		this.mSeekBar = new SeekBar(context, attrs);
		this.mSeekBar.setMax(maxValue - minValue);
		this.mSeekBar.setOnSeekBarChangeListener(this);    
        setWidgetLayoutResource(R.layout.seek_bar_preference);
        this.mCurrentValue = attrs.getAttributeIntValue("http://schemas.android.com/apk/res/com.example.g_forcesensor", "threshold", DEFAULT_VALUE); 
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
    	View view = super.onCreateView(parent);     
        LinearLayout layout = (LinearLayout) view;
        layout.setOrientation(LinearLayout.VERTICAL);        
        return view;
    }
    
    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
    	if (restorePersistedValue) this.mCurrentValue = this.getPersistedInt(DEFAULT_VALUE);
        else {
        	mCurrentValue = (Integer) defaultValue;
            persistInt(mCurrentValue);
        } 
    }
    
 	@Override
    public void onBindView(View view) {
        super.onBindView(view);
        
        try {
            ViewParent oldContainer = mSeekBar.getParent();
            ViewGroup newContainer = (ViewGroup) view.findViewById(R.id.seekBarPrefBarContainer);
            
            if (oldContainer != newContainer) {
                if (oldContainer != null) ((ViewGroup) oldContainer).removeView(mSeekBar);
                newContainer.removeAllViews();
                newContainer.addView(mSeekBar, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            }
        } catch(Exception ex) {}
        
        updateView(view);
    }
    
    protected void updateView(View view) {
    	try {
        	this.mStatusText = (TextView) view.findViewById(R.id.seekBarPrefValue);
            this.mStatusText.setText(String.valueOf(this.df2.format(this.mCurrentValue / 10.0D)));
            this.mStatusText.setMinimumWidth(30);            
            this.mSeekBar.setProgress(mCurrentValue - minValue);
        } catch(Exception e) {}        
    }
   
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    	int newValue = progress + minValue;
        
        if(newValue > maxValue) newValue = maxValue;
        else if(newValue < minValue) newValue = minValue;
        else if(mInterval != 1 && newValue % mInterval != 0) newValue = Math.round(((float)newValue)/mInterval)*mInterval;  
        
        if(!callChangeListener(newValue)){
            seekBar.setProgress(mCurrentValue - minValue); 
            return; 
        }

        this.mCurrentValue = newValue;
        this.mStatusText.setText(String.valueOf(this.df2.format(this.mCurrentValue / 10.0D)));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    	notifyChanged();    	
    	persistInt(mCurrentValue);    	
    }
}