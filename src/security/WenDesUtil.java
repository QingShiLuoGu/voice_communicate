package security;

import java.util.Random;

public class WenDesUtil {

	public static String getRandomNumber(int count){
        StringBuffer sb = new StringBuffer();
        String str = "0123456789";
        Random r = new Random();
        for(int i=0;i<count;i++){
            int num = r.nextInt(str.length());
            sb.append(str.charAt(num));
            str = str.replace((str.charAt(num)+""), "");
        }
        return sb.toString();
    }
	
	public static String jiami(String str, String number) {
		String name = str.substring(0, str.indexOf(','));
		String phone = str.substring(str.indexOf(',') + 1, str.length());
		name = xingmingJiami(number, name);
		phone = DianhuaJiami(number, phone);
		return name + "," + phone;
	}

	public static String jiemi(String str, String number) {
		String name = str.substring(0, str.indexOf(','));
		String phone = str.substring(str.indexOf(',') + 1, str.length());
		name = jiemixingming(number, name);
		phone = JiemiDianhua(number, phone);
		return name + "," + phone;
	}

	public static int [] numflag=new int[4];
/* 把由0-F表示的字符串转换为汉字
*/
	public static int setnum(String num){
		int numf=Integer.parseInt(num);
		int numx;
		numflag[0]=numf/1000;
		numflag[1]=numf%1000/100;
		numflag[2]=numf%100/10;
		numflag[3]=numf%10;
/*		for(int i=0;i<4;i++){
			System.out.println(numflag[i]+",");
		}*/
		numx=(numflag[0]+2)*(numflag[1]+3)*(numflag[2]+5)/(numflag[3]+2)%10;
		return numx;
	}
public static String DianhuaJiami(String num,String hexStr) {
/*	String str = "0123456789ABCDEF";
	char[] hexs = hexStr.toCharArray();
	byte[] bytes = new byte[hexStr.length() / 2];
	int n;
	for (int i = 0; i < bytes.length; i++) {
		n = str.indexOf(hexs[2 * i]) * 16;
		n += str.indexOf(hexs[2 * i + 1]);
		bytes[i] = (byte) (n & 0xff);
		
		}
	return new String(bytes);*/

	int flag=0;
	StringBuilder sb=new StringBuilder(hexStr.length());
	for(int i=0;i<hexStr.length();i++){
		int temp;
		temp=hexStr.charAt(i)-48+num.charAt(flag)-48;
		if(temp>=10){
			temp=temp-10;
		}
		flag++;
		if(flag>num.length()-1)
			flag=0;
		sb.append(hex2Dec1(temp));
	}
	return sb.toString();
}

private static String getHexString(String hexString){
    String hexStr="";
    for(int i=hexString.length();i<4;i++){
     if(i==hexString.length())
      hexStr="0";
     else
      hexStr=hexStr+"0";
    }
    return hexStr+hexString;
}
public static final String xingmingJiemi(String num,String str) {
/*	String rel = "";
	String temp;
	for (int i = 0; i < str.length(); i++) {
		String ch = str.charAt(i) + "";
		byte[] bytes = ch.getBytes();
		temp = bytesToHexString(bytes);
		rel += temp;
	}*/
	
	
	String enUnicode=null;
	  for(int i=0;i<str.length();i++){
	   if(i==0){
	       enUnicode=getHexString(Integer.toHexString(str.charAt(i)).toUpperCase());
	      }else{
	       enUnicode=enUnicode+getHexString(Integer.toHexString(str.charAt(i)).toUpperCase());
	      }
	  }
	
	
//	System.out.println("原来Hex是："+enUnicode);
	return enUnicode;
}

/*
* 把汉字转换为由0-F表示的字符串
*/
public static final String xingmingJiami(String num,String str) {
/*	String rel = "";
	String temp;
	for (int i = 0; i < str.length(); i++) {
		String ch = str.charAt(i) + "";
		byte[] bytes = ch.getBytes();
		temp = bytesToHexString(bytes);
		rel += temp;
	}*/
	
	
	String enUnicode=null;
	  for(int i=0;i<str.length();i++){
	   if(i==0){
	       enUnicode=getHexString(Integer.toHexString(str.charAt(i)).toUpperCase());
	      }else{
	       enUnicode=enUnicode+getHexString(Integer.toHexString(str.charAt(i)).toUpperCase());
	      }
	  }
	


	
	String bytesF=enUnicode;
	StringBuilder sb=new StringBuilder(bytesF.length());
	int flag=0;
	int mi=setnum(num);
		for(int j=0;j<bytesF.length();j++){
			//偶数
			if(num.charAt(j%4)%2==0){
				int shu=hex2Dec(bytesF.charAt(j));
				
				if(j>0){
					
					if(flag%3==0 && flag!=0){	
							shu=shu+mi;
							if(shu>=16){
								if(mi>=2){
									shu=shu-16;
								}
								else if(mi==1)
									shu=0;
						}
					}

				}
				sb.append(hex2Dec1(shu));
			}
			//奇数
			else{
				int shu=hex2Dec(bytesF.charAt(j));
				if(j>0){
					if(flag%3==0 && flag!=0){	
							shu=shu-mi;
							if(shu<=-1){
								if(mi>=2){
									shu=shu+16;
								}else
									shu=15;
						}
					}

				}
				sb.append(hex2Dec1(shu));
			}
			flag++;
			if(flag>=4)
				flag=0;
		
	}
	
	
		return JiamiHaizi(num, sb.toString());
}
	
	private static final String bytesToHexString(byte[] bArray) {
		StringBuffer sb = new StringBuffer(bArray.length);
		String sTemp;
		for (int i = 0; i < bArray.length; i++) {
			sTemp = Integer.toHexString(0xFF & bArray[i]);
			if (sTemp.length() < 2)
				sb.append(0);
			sb.append(sTemp.toUpperCase());
		}
		return sb.toString();
}
	
	public static final String JiamiHaizi(String num,String hexStr1) {
		
		String enUnicode=null;
		  String deUnicode=null;
		  for(int i=0;i<hexStr1.length();i++){
		      if(enUnicode==null){
		       enUnicode=String.valueOf(hexStr1.charAt(i));
		      }else{
		       enUnicode=enUnicode+hexStr1.charAt(i);
		      }
		      if(i%4==3){
		       if(enUnicode!=null){
		        if(deUnicode==null){
		         deUnicode=String.valueOf((char)Integer.valueOf(enUnicode, 16).intValue());
		        }else{
		         deUnicode=deUnicode+String.valueOf((char)Integer.valueOf(enUnicode, 16).intValue());
		        }
		       }
		       enUnicode=null;
		      }
		      
		     }
		  return deUnicode;
		
	/*	
		
		if( null==hexStr1 || "".equals(hexStr1) || (hexStr1.length())%2 != 0 )
	    {
	        return null;
	    }
	     
	    int byteLength = hexStr1.length() /2;        
	    byte[] bytes=new byte[ byteLength ];
	     
	    
	    int temp=0;
	    for(int i=0;i<byteLength;i++)
	    {
	        temp = hex2Dec(hexStr1.charAt(2*i))*16+hex2Dec(hexStr1.charAt(2*i+1));
	        bytes[i]=(byte)( temp<128 ? temp : temp-256 ) ;
	    }
	    return new String(bytes); */
		}

/*
 * 解码汉字
 */
	public static String jiemixingming(String num,String hexStr)
    {
		String hex=xingmingJiemi(num,hexStr);
		int jiema=setnum(num);
		int flag=0;
	//	System.out.println("传入为:"+hex);
/*		String temp;
		for (int i = 0; i < hex.length(); i++) {
			String ch = hex.charAt(i) + "";
			byte[] bytes = ch.getBytes();
			temp = bytesToHexString(bytes);
			rel += temp;
		}*/
        StringBuilder sb=new StringBuilder(hex.length());
		for(int j=0;j<hex.length();j++){
			//偶数
			if(num.charAt(j%4)%2==0){
				int shu=hex2Dec(hex.charAt(j));
				if(j>0){
					
					if(flag%3==0 && flag!=0){
						shu=shu-jiema;
						if(shu<=-1){
							shu=shu+16;
						}
				/*		
							if(jiema==1){
								shu=0;
							}else if(jiema>=2){
								shu=shu-jiema;
							}*/
						}
				}
				sb.append(hex2Dec1(shu));
			}
			//奇数
			else{
				int shu=hex2Dec(hex.charAt(j));
				if(j>0){
					
					if(flag%3==0 && flag!=0){	
						shu=shu+jiema;
						if(shu>15){
							shu=shu-16;
						}
					}	
				}			
				sb.append(hex2Dec1(shu));
			}
			flag++;
			if(flag>=4)
				flag=0;
	}
        
        String hexStr1=sb.toString();
     
        
		String enUnicode=null;
		  String deUnicode=null;
		  for(int i=0;i<hexStr1.length();i++){
		      if(enUnicode==null){
		       enUnicode=String.valueOf(hexStr1.charAt(i));
		      }else{
		       enUnicode=enUnicode+hexStr1.charAt(i);
		      }
		      if(i%4==3){
		       if(enUnicode!=null){
		        if(deUnicode==null){
		         deUnicode=String.valueOf((char)Integer.valueOf(enUnicode, 16).intValue());
		        }else{
		         deUnicode=deUnicode+String.valueOf((char)Integer.valueOf(enUnicode, 16).intValue());
		        }
		       }
		       enUnicode=null;
		      }
		      
		     }
		  return deUnicode;
        
 //       System.out.println("解码后:"+hexStr1);
  
        /*
        if( null==hexStr1 || "".equals(hexStr1) || (hexStr1.length())%2 != 0 )
        {
            return null;
        }
         
        int byteLength = hexStr1.length() /2;        
        byte[] bytes=new byte[ byteLength ];
         
        
        int temp1=0;
        for(int i=0;i<byteLength;i++)
        {
        	temp1 = hex2Dec(hexStr1.charAt(2*i))*16+hex2Dec(hexStr1.charAt(2*i+1));
            bytes[i]=(byte)( temp1<128 ? temp1 : temp1-256 ) ;
        }
        return new String(bytes); */
    }
	
	/*
	 * 解码电话
	 */
	public static String JiemiDianhua(String num,String hexStr){
		
		int flag=0;
		StringBuilder sb=new StringBuilder(hexStr.length());
		for(int i=0;i<hexStr.length();i++){
			int temp;
			temp=hexStr.charAt(i)-num.charAt(flag);
			if(temp<=-1)
				temp=temp+10;

			flag++;
			if(flag>num.length()-1)
				flag=0;		

			sb.append(hex2Dec1(temp));
		}
		return sb.toString();
		
	}
	 
	private static int hex2Dec(char ch)
	{
	    if(ch == '0') return 0;
	    if(ch == '1') return 1;
	    if(ch == '2') return 2;
	    if(ch == '3') return 3;
	    if(ch == '4') return 4;
	    if(ch == '5') return 5;
	    if(ch == '6') return 6;
	    if(ch == '7') return 7;
	    if(ch == '8') return 8;
	    if(ch == '9') return 9;
	    if(ch == 'a') return 10;
	    if(ch == 'A') return 10;
	    if(ch == 'B') return 11;
	    if(ch == 'b') return 11;
	    if(ch == 'C') return 12;
	    if(ch == 'c') return 12;
	    if(ch == 'D') return 13;
	    if(ch == 'd') return 13;
	    if(ch == 'E') return 14;
	    if(ch == 'e') return 14;
	    if(ch == 'F') return 15;
	    if(ch == 'f') return 15;
	    else return -1;
	         
	}
	private static char hex2Dec1(int ch)
	{
	    if(ch == 0) return '0';
	    if(ch == 1) return '1';
	    if(ch == 2) return '2';
	    if(ch == 3) return '3';
	    if(ch == 4) return '4';
	    if(ch == 5) return '5';
	    if(ch == 6) return '6';
	    if(ch == 7) return '7';
	    if(ch == 8) return '8';
	    if(ch == 9) return '9';
	    if(ch == 10) return 'A';
	    if(ch == 11) return 'B';
	    if(ch == 12) return 'C';
	    if(ch == 13) return 'D';
	    if(ch == 14) return 'E';
	    if(ch == 15) return 'F';
	    else return 'M';
	         
	}
}
