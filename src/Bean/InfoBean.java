package Bean;
import java.io.Serializable;


public class InfoBean implements Serializable{
	private int type;
	private String title;
	private String str;
	
	public InfoBean(int type, String title, String str) {
		super();
		this.type = type;
		this.title = title;
		this.str = str;
	}
	public InfoBean() {
		super();
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getStr() {
		return str;
	}
	public void setStr(String str) {
		this.str = str;
	}
	
	
	
}
