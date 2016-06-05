package utils;

/*
 * ������Ǳ��۲�����ֵ��࣬�洢֮ǰ��ע�����п�����Ӧ�ý��ܵ����֣�
 * ���������ּ����һ������ͬ���ֵ��ܵĸ������������
 * �����������ĳһ��ֵ����Ϊ��Ӧ�ý����������
 */
public class WatchedBean {
	private int refValue;
	private int total;
	private int interval;
	private int maxInterval;
	private int minCount;
	
	/**
	 * 
	 * @param refValue ʶ���������
	 * @param total �����ֵĸ���
	 * @param maxInterval ������ͬ������ɵĴ��������
	 * @param minCount  ȷ���յ�һ�����ֵ���С��������
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
