package utils;

import java.util.ArrayList;
import java.util.List;

public class WatchedList {
	List<WatchedBean> list = new ArrayList<WatchedBean>();
	private final static int MAXCOUNT = 1;
	
	public void addToList(WatchedBean bean)
	{
//		bean.setInterval(0);
		for(WatchedBean b:list)
		{
			if(b.getRefValue()==bean.getRefValue())
			{
				b.addToTotal(bean.getTotal());
				return;
			}
		}
		if(list.size()>=MAXCOUNT)
		{
			list.remove(0);
		}
		list.add(bean);
		
	}
	
	public void clearList()
	{
		list.clear();
	}
	
	/**
	 * 
	 * @return 为-1时表示未找到
	 */
	public int getConfirmedValue()
	{
		clear(list);
		for(WatchedBean b:list)
		{
			if(b.confirm())
			{
				list.remove(b);
				return b.getRefValue();
			}
		}
		return -1;
	}
	
	/**
	 * 将间隔大于某数的清理了
	 */
	private void clear(List<WatchedBean> list)
	{
		for(int i=0;i<list.size();i++)
		{
			if(list.get(i).shouldDelete())
			{
				list.remove(i);
				clear(list);
			}
		}
	}
	
	
//	public void addInterval()
//	{
//		for(WatchedBean b:list)
//		{
//			b.addInterval();
//		}
//	}
	
//	public boolean exist(int value)
//	{
//		for(WatchedBean bean:list)
//		{
//			if(bean.getRefValue()==value)
//			{
//				return true;
//			}
//		}
//		return false;
//	}
}
