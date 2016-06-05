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

import log.LogHelper;
import sinvoice_lib.Buffer.BufferData;
import utils.MyLog;
import utils.WatchedList;

public class VoiceRecognition {
	private final static String TAG = "Recognition";

	private final static int STATE_START = 1;
	private final static int STATE_STOP = 2;
	private final static int STEP1 = 1;
	private final static int STEP2 = 2;
	// -4, -3, -2, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17
	// 10, 13, 16, 19, 22, 25, 28, 31, 34, 37, 40, 43, 46, 49, 52, 55, 58, 61,
	// 64, 67, 70
	private final static int INDEX[] = { -1, -1, -1, -1, -1, -1, -1, -1, -1,
			-1, -4, -1, -1, -3, -1, -1, -2, -1, -1, 0, -1, -1, 1, -1, -1, 2,
			-1, -1, 3, -1, -1, 4, -1, -1, 5, -1, -1, 6, -1, -1, 7, -1, -1, 8,
			-1, -1, 9, -1, -1, 10, -1, -1, 11, -1, -1, 12, -1, -1, 13, -1, -1,
			14, -1, -1, 15, -1, -1, 16, -1, -1, 17 };

	private final static int MIN_REG_CIRCLE_COUNT = 15;
//	private final static int MIN_REG_WATCH = 5;
	private int mState;
	private Listener mListener;
	private Callback mCallback;

	private int mSamplingPointCount = 0;

//	private static int MaxInterval=5;
//	private static int MinCount=20;
	
	
	private int mSampleRate;
	private int mChannel;
	private int mBits;

	private boolean mIsStartCounting = false;
	private int mStep;
	private boolean mIsBeginning = false;
	private boolean mStartingDet = false;
	private int mStartingDetCount;

//	private WatchedList watchedList;
	
	private int mRegValue;
	private int mRegIndex;
	private int mRegCount;
	private int mPreRegCircle;
	private boolean mIsRegStart = false;
	private StringBuffer stringBuffer;

	public static interface Listener {
		void onStartRecognition();

		void onRecognition(int index);

		void onStopRecognition();
	}

	public static interface Callback {
		BufferData getRecognitionBuffer();

		void freeRecognitionBuffer(BufferData buffer);
	}

	public VoiceRecognition(Callback callback, int SampleRate, int channel,
			int bits) {
		mState = STATE_STOP;

		mCallback = callback;
		mSampleRate = SampleRate;
		mChannel = channel;
		mBits = bits;
		stringBuffer = new StringBuffer();
		
	}

	public void setListener(Listener listener) {
		mListener = listener;
	}

	public void start() {
		stringBuffer = new StringBuffer();
		if (STATE_STOP == mState) {

			if (null != mCallback) {
				mState = STATE_START;
				mSamplingPointCount = 0;

				mIsStartCounting = false;
				mStep = STEP1;
				mIsBeginning = false;
				mStartingDet = false;
				mStartingDetCount = 0;
				mPreRegCircle = -1;
				if (null != mListener) {
					mListener.onStartRecognition();
				}
				while (STATE_START == mState) {
					BufferData data = mCallback.getRecognitionBuffer();
					if (null != data) {
						if (null != data.mData) {
							process(data);

							mCallback.freeRecognitionBuffer(data);
						} else {
							LogHelper.d(TAG, "end input buffer, so stop");
							break;
						}
					} else {
						LogHelper.e(TAG, "get null recognition buffer");
						break;
					}
				}

				mState = STATE_STOP;
				if (null != mListener) {
					mListener.onStopRecognition();
				}
			}
		}
	}

	public void stop() {
		if (STATE_START == mState) {
			mState = STATE_STOP;
		}
		MyLog.writeLogtoFile(stringBuffer.toString());
	}

	private void process(BufferData data) {

		int size = data.getFilledSize() - 1;
		short sh = 0;
		for (int i = 0; i < size; i++) {
			short sh1 = data.mData[i];
			sh1 &= 0xff;
			short sh2 = data.mData[++i];
			sh2 <<= 8;
			sh = (short) ((sh1) | (sh2));
			if (!mIsStartCounting) {
				if (STEP1 == mStep) {// 算法开始，检查sh的值，大于0就继续检查sh值，小于0时，才进行下一步
					if (sh < 0) {
						mStep = STEP2;
					}
				} else if (STEP2 == mStep) {// 会出现一片连续的值都小于0，这里一直检查sh的值，大于0时才执行下一步
					if (sh > 0) {
						mIsStartCounting = true;// 发现出现了第一个大于0的值，开始了计数
						mSamplingPointCount = 0;
						mStep = STEP1;
					}
				}
			} else {
				++mSamplingPointCount;
				if (STEP1 == mStep) {
					if (sh < 0) {
						mStep = STEP2;
					}
				} else if (STEP2 == mStep) {
					if (sh > 0) {

						int samplingPointCount = preReg(mSamplingPointCount);
						if(samplingPointCount!=0)
							reg(samplingPointCount);
						mSamplingPointCount = 0;
						mStep = STEP1;
					}
				}
			}
		}
	}

	private int preReg(int samplingPointCount) {
		// -4, -3, -2, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15
		// 10, 13, 16, 19, 22, 25, 28, 31, 34, 37, 40, 43, 46, 49, 52, 55, 58,
		// 61, 64
		// LogHelper.i("preReg", "come in preReg");

		if(stringBuffer!=null)
		{
			stringBuffer.append(samplingPointCount+" "+"\n");
		}
		switch (samplingPointCount) {
		case 9:
		case 10:
		case 11:
			samplingPointCount = 10;
			break;
		case 12:
		case 13:
		case 14:
			samplingPointCount = 13;
			break;
		case 15:
		case 16:
		case 17:
			samplingPointCount = 16;
			break;
		case 18:
		case 19:
		case 20:
			samplingPointCount = 19;
			break;
		case 21:
		case 22:
		case 23:
			samplingPointCount = 22;
			break;
		case 24:
		case 25:
		case 26:
			samplingPointCount = 25;
			break;
		case 27:
		case 28:
		case 29:
			samplingPointCount = 28;
			break;
		case 30:
		case 31:
		case 32:
			samplingPointCount = 31;
			break;
		case 33:
		case 34:
		case 35:
			samplingPointCount = 34;
			break;
		case 36:
		case 37:
		case 38:
			samplingPointCount = 37;
			break;
		case 39:
		case 40:
		case 41:
			samplingPointCount = 40;
			break;
		case 42:
		case 43:
		case 44:
			samplingPointCount = 43;
			break;
		case 45:
		case 46:
		case 47:
			samplingPointCount = 46;
			break;
		case 48:
		case 49:
		case 50:
			samplingPointCount = 49;
			break;
		case 51:
		case 52:
		case 53:
			samplingPointCount = 52;
			break;
		case 54:
		case 55:
		case 56:
			samplingPointCount = 55;
			break;
		case 57:
		case 58:
		case 59:
			samplingPointCount = 58;
			break;
		case 60:
		case 61:
		case 62:
			samplingPointCount = 61;
			break;
		case 63:
		case 64:
		case 65:
			samplingPointCount = 64;
			break;
		case 66:
		case 67:
		case 68:
			samplingPointCount = 67;
			break;
		case 69:
		case 70:
		case 71:
			samplingPointCount = 70;
			break;

		default:
			samplingPointCount = 0;
			break;
		}

		return samplingPointCount;
	}

	
	/**
	 * 整个函数有个相当大的问题，那就是在识别收到的字符时，如果一连串相同字符中间插了一个其它字符，
	 * 那么程序会重新计数，于是很有可能导致计数的最终个数小于MIN_REG_CIRCLE_COUNT
	 */
	private void reg(int samplingPointCount) {
		
		LogHelper.d(TAG, "reg  "+samplingPointCount);
		if (!mIsBeginning) {
			if (!mStartingDet) {
				// 判断是否为起始标志,若不是则退出函数
				if (Common.START == samplingPointCount) {
					mStartingDet = true;
					mStartingDetCount = 0;
				}
			} else {
				if (Common.START == samplingPointCount) {
					++mStartingDetCount;
					/**
					 * 此处有一个问题，在判断是否开始这里，已经接受了MIN_REG_CIRCLE_COUNT个次数才认为开始接受一句话，
					 * 但是正式开始接收后接收到的开始标志的次数可能就打不到MIN_REG_CIRCLE_COUNT个了，
					 * 可能导致上层的接口中会认为一直没有开始，收到的字符串中的字符的位置顺序不对，然后校验失败
					 * 下面是添加代码
					 */
					if (mStartingDetCount >= MIN_REG_CIRCLE_COUNT) {
						mIsBeginning = true;
						mIsRegStart = false;
						mRegCount = 0;

						// 添加的代码 开始
						if (null != mListener) {
							mListener.onRecognition(INDEX[Common.START]);
						}
						mPreRegCircle = Common.START;
						// 添加的代码 结束

					}
				} else {
					mStartingDet = false;
				}
			}
		} else {

			if (!mIsRegStart) {
				if (samplingPointCount > 0) {

					mRegValue = samplingPointCount;
					mRegIndex = INDEX[samplingPointCount];
					mIsRegStart = true;
					mRegCount = 1;
				}
			} else {

				if (samplingPointCount == mRegValue) {
					++mRegCount;

					if (mRegCount >= MIN_REG_CIRCLE_COUNT) {
						// ok
						if (mRegValue != mPreRegCircle) {
							if (null != mListener) {
								mListener.onRecognition(mRegIndex);
							}
							mPreRegCircle = mRegValue;
						}

						mIsRegStart = false;
					}
				} else {
					mIsRegStart = false;
				}
			}
		}
	}
}
