/*
 * Copyright (C) 2013 gujicheng
 * 
 * Licensed under the GPL License Version 2.0;
 * you may not use this file except in compliance with the License.
 * 
 * If you have any question, please contact me.
 * 
 *************************************************************************
 **                   Author information                                **
 *************************************************************************
 ** Email: gujicheng197@126.com                                         **
 ** QQ   : 29600731                                                     **
 ** Weibo: http://weibo.com/gujicheng197                                **
 *************************************************************************
 */
package sinvoice_lib;

public class Common {
    public final static int START_TOKEN = -2;
//    public final static int STOP_TOKEN = 6;
    public final static int STOP_TOKEN = -3;
    public final static int FENGE_TOKEN = -4;
    public final static char FENGE = '$';
    public final static String CODEBOOK = "0123456789ABCDEF#@";
    public final static int START = 16;//起始标志,-2对应的
    public final static int MAX_NUMBER = 16;
    public final static int DUR = 800;
    public  static int DEFAULT_GEN_DURATION = 50;//100
    
    public final static int DEFAULT_BUFFER_SIZE = 4096*4;
    public final static int DEFAULT_BUFFER_COUNT = 3;
    public final static int DEFAULT_SAMPLE_RATE = 44100;
}
