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

import android.text.TextUtils;

import sinvoice_lib.Buffer.BufferData;

public class SinVoiceRecognition implements Record.Listener, Record.Callback, VoiceRecognition.Listener, VoiceRecognition.Callback {
    private final static String TAG = "SinVoiceRecognition";

    private final static int STATE_START = 1;
    private final static int STATE_STOP = 2;
    private final static int STATE_PENDING = 3;

    private int mMaxCodeIndex = Common.MAX_NUMBER-1;
    private Buffer mBuffer;
    private Record mRecord;
    private VoiceRecognition mRecognition;

    private Thread mRecordThread;
    private Thread mRecognitionThread;
    private int mState;
    private Listener mListener;

    private String mCodeBook;

    public static interface Listener {
        void onRecognitionStart();

        void onRecognition(char ch);

        void onRecognitionEnd();
    }

    public SinVoiceRecognition() {
        this(Common.CODEBOOK);
    }

    public SinVoiceRecognition(String codeBook) {
        this(codeBook, Common.DEFAULT_SAMPLE_RATE, Common.DEFAULT_BUFFER_SIZE, Common.DEFAULT_BUFFER_COUNT);
    }

    public SinVoiceRecognition(String codeBook, int sampleRate, int bufferSize, int bufferCount) {
        mState = STATE_STOP;
        mBuffer = new Buffer(bufferCount, bufferSize);

        mRecord = new Record(this, sampleRate, Record.CHANNEL_1, Record.BITS_16, bufferSize);
        mRecord.setListener(this);
        mRecognition = new VoiceRecognition(this, sampleRate, Record.CHANNEL_1, Record.BITS_16);
        mRecognition.setListener(this);

        setCodeBook(codeBook);
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public void setCodeBook(String codeBook) {
        if (!TextUtils.isEmpty(codeBook)){
            mCodeBook = codeBook;
        }
    }

    public void start() {
        if (STATE_STOP == mState) {
            mState = STATE_PENDING;

            mRecognitionThread = new Thread() {
                @Override
                public void run() {
                    mRecognition.start();
                }
            };
            if (null != mRecognitionThread) {
                mRecognitionThread.start();
            }

            mRecordThread = new Thread() {
                @Override
                public void run() {
                    mRecord.start();

                    LogHelper.d(TAG, "record thread end");

                    LogHelper.d(TAG, "stop recognition start");
                    stopRecognition();
                    LogHelper.d(TAG, "stop recognition end");
                }
            };
            if (null != mRecordThread) {
                mRecordThread.start();
            }

            mState = STATE_START;
        }
    }

    private void stopRecognition() {
        mRecognition.stop();

        // put end buffer
        BufferData data = new BufferData(0);
        mBuffer.putFull(data);

        if (null != mRecognitionThread) {
            try {
                mRecognitionThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                mRecognitionThread = null;
            }
        }

        mBuffer.reset();
    }

    public void stop() {
        if (STATE_START == mState) {
            mState = STATE_PENDING;

            LogHelper.d(TAG, "force stop start");
            mRecord.stop();
            mBuffer.reset();
            if (null != mRecordThread) {
                try {
                    mRecordThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    mRecordThread = null;
                }
            }

            mState = STATE_STOP;
            LogHelper.d(TAG, "force stop end");
        }
    }

    @Override
    public void onStartRecord() {
        LogHelper.d(TAG, "start record");
    }

    @Override
    public void onStopRecord() {
        LogHelper.d(TAG, "stop record");
    }

    @Override
    public BufferData getRecordBuffer() {
        BufferData buffer = mBuffer.getEmpty();
        if (null == buffer) {
            LogHelper.e(TAG, "get null empty buffer");
        }
//        System.out.println("public BufferData getRecordBuffer()"+buffer.mData[0]);
        return buffer;
    }

    @Override
    public void freeRecordBuffer(BufferData buffer) {
        if (null != buffer) {
//        	System.out.println("public void freeRecordBuffer(BufferData buffer)"+buffer.mData[0]);
            if (!mBuffer.putFull(buffer)) {
                LogHelper.e(TAG, "put full buffer failed");
            }
        }
    }

    @Override
    public BufferData getRecognitionBuffer() {
        BufferData buffer = mBuffer.getFull();
//        System.out.println("public BufferData getRecognitionBuffer()"+buffer.mData.toString());
        if (null == buffer) {
            LogHelper.e(TAG, "get null full buffer");
        }
        return buffer;
    }

    @Override
    public void freeRecognitionBuffer(BufferData buffer) {
        if (null != buffer) {
//        	System.out.println("public void freeRecognitionBuffer(BufferData buffer)"+buffer.mData.toString());
            if (!mBuffer.putEmpty(buffer)) {
                LogHelper.e(TAG, "put empty buffer failed");
            }
        }
    }

    @Override
    public void onStartRecognition() {
        LogHelper.d(TAG, "start recognition");
    }

    @Override
    public void onRecognition(int index) {
//        LogHelper.d("recognition", "recognition is :" + index);
        if (null != mListener) {
            if (Common.START_TOKEN == index) {
                mListener.onRecognitionStart();
            } else if (Common.STOP_TOKEN == index) {
                mListener.onRecognitionEnd();
            } else if (index >= 0 && index <= 9) {
            	mListener.onRecognition(mCodeBook.charAt(index));
            }else if(index>=10&&index<=15)
            {
            	mListener.onRecognition((char)('A'+index-10));
            }
            else if(index>=16)
            {
            	switch (index) {
				case 16:	
					mListener.onRecognition('#');
					break;
				case 17:	
					mListener.onRecognition('@');
					break;
				default:
					break;
				}
            }
            else if(index==-4)
            {
            	mListener.onRecognition(Common.FENGE);//×÷Îª¼ä¸ô·û
            }
        }
    }

    @Override
    public void onStopRecognition() {
        LogHelper.d(TAG, "stop recognition");
    }

}
