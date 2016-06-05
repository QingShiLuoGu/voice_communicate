package utils;

/*
 * 这个类是被观察的数字的类，存储之前关注到的有可能是应该接受的数字，
 * 被其他数字间隔的一连串相同数字的总的个数计算出来，
 * 如果个数大于某一个值就认为是应该接受这个数字
 */
public class WatchedBean {
	private int refValue;
	private int total;
	private int interval;
	private int maxInterval;
	private int minCount;
	
	/**
	 * 
	 * @param refValue 识别出的数字
	 * @param total 该数字的个数
	 * @param maxInterval 两个相同数字组成的串的最大间隔
	 * @param minCount  确认收到一个数字的最小计数个数
	 */
	public WatchedBean(int refValue,int total,int maxInterval,int minCount)
	{
		setRefValue(refValue);
		setTotal(total);
		setMaxInterval(maxInterval);
		setMinCount(minCount);
		setInterval(0);
	}
	
	public void addToTotal(int num)
	{
		if(interval<maxInterval)
		{			
			total+=num;
			setInterval(0);
		}
	}
	
	public boolean shouldDelete()
	{
		if(interval>=maxInterval)
			return true;
		return false;
	}
//	public void addInterval()
//	{
//		interval++;
//	}
	
	public boolean confirm()
	{
		if(total>=minCount)
			return true;
		return false;
	}
	
	public int getRefValue() {
		return refValue;
	}
	public void setRefValue(int refValue) {
		this.refValue = refValue;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public int getInterval() {
		return interval;
	}
	public void setInterval(int interval) {
		this.interval = interval;
	}
	public int getMaxInterval() {
		return maxInterval;
	}
	public void setMaxInterval(int maxInterval) {
		this.maxInterval = maxInterval;
	}
	public int getMinCount() {
		return minCount;
	}
	public void setMinCount(int minCount) {
		this.minCount = minCount;
	}
	
	
}
