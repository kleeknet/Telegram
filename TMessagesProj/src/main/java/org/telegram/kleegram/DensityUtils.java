/**
 * Copyright (C) 2013, Easiio, Inc.
 * All Rights Reserved.
 */
package org.telegram.kleegram;


import org.telegram.messenger.ApplicationLoader;

public class DensityUtils {
	
	public static int dp_px(float dpValue) {  
        final float scale = ApplicationLoader.applicationContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);  
    } 
	
	public static int px_dp(float pxValue) {  
        final float scale = ApplicationLoader.applicationContext.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);  
    } 

}
