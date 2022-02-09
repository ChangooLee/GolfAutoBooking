package com.changoo.asianacc;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Hello world!
 *
 */
class Thread4p1 extends Thread {
  //  run() 메서드 오버라이딩
	public void run() {
    //스레드에서 실행할 작업
    	try {
    		//첫 번째 파라미터 - 주중 : false, 주말 : true
    		//두 번째 파라미터 - 시간대 08시 : 08, 09시 : 09 (08시에서 가까운 순서대로 예약)
    		//ex) AsianaCCBooking.bookAsianaCC40daysFromNow(주중주말, 목표티옵시간, 목표 날짜-지워버리면 예약 가능한 마지막 날짜 예약)
    		//ex) System.out.println(AsianaCCBooking.bookAsianaCC40daysFromNow(false, "08", ""));  <<이렇게 하면 자동 예약됨
    		//ex) System.out.println(AsianaCCBooking.bookAsianaCC40daysFromNow(false, "08", "20220311"));  <<이렇게 하면 주중 주말 무시하고 해당 일자 시간에 예약됨
			System.out.println(AsianaCCBooking.bookAsianaCC40daysFromNow(false, "08", "20220311"));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}


public class AsianaCCBooking 
{
    @SuppressWarnings("deprecation")
	public static void main( String[] args ) throws InterruptedException
    {   

    	System.out.println("Auto AsianaCC booking start!!!!!!");
		Thread4p1 t1 = new Thread4p1();
		t1.start();     	
    }
    
    /********************************************************************************************************************************************************************************************
     * 무조건 40일뒤 예약 함수
     * 파라미터 첫 번째 주중 예약할건지 주말 예약할건지 선택
     * 8시 근처로 예약
     ********************************************************************************************************************************************************************************************/
    static String sCookieKey = "";
	static String sCookieValue = "";
    public static String bookAsianaCC40daysFromNow(Boolean bWeekend, String sTargetTeeOfftime, String sTargetTeeOffDate) throws InterruptedException {
    	try {
    		while(true) {
    	    	//현재시간 구하기 로직 : 컴퓨터 시간임, 따라서 최대한 서버 시간과 맞아야 함, 내 컴퓨터는 거의 동기화 되어있는듯
    	    	SimpleDateFormat format = new SimpleDateFormat ( "HHmmss");
    	    	Date dTime = new Date();    			
    	    	int iTime = Integer.parseInt(format.format(dTime));
    	    	//아침 8시 59분 55초부터 예약 시작, 8시 0, 8, 15, 23, 30, 38, 45, 53분 티옵만 노리며 실패 시 1초 대기 후 재시도
    	    		
    	    	//if(iTime > 85955) {
    	    	if(iTime > 85955) {
    	    		Boolean bBookSuccess = false;
        			Boolean bBookable = false;
        			//로그인하여 쿠키 얻기 시작
    				String requestURL = "https://www.asianacc.co.kr:444/reservation/mbr/melogin.asp";
    				HttpClient client = HttpClientBuilder.create().build(); // HttpClient 생성
    				HttpPost postRequest = new HttpPost(requestURL); //POST 메소드 URL 새성 
    				postRequest.setHeader("Accept", "application/json");
    				postRequest.setHeader("Connection", "keep-alive");
    				postRequest.setHeader("Content-Type", "application/x-www-form-urlencoded");
    				
    			    ArrayList<NameValuePair> postParameters;
    			    	
    			    postParameters = new ArrayList<NameValuePair>();
    			    postParameters.add(new BasicNameValuePair("url_param", ""));
    			    postParameters.add(new BasicNameValuePair("swidth", "1536"));
    			    postParameters.add(new BasicNameValuePair("sheight", "864"));
    			    //로그인 ID
    			    postParameters.add(new BasicNameValuePair("mb_mbid", "jh1117"));
    			    //로그인 password
    			    postParameters.add(new BasicNameValuePair("mb_pswd", "hkj54578082"));
    			    postRequest.setEntity(new UrlEncodedFormEntity(postParameters, "UTF-8"));
    			    		    
    				HttpResponse response = client.execute(postRequest);
    				Header[] header = response.getAllHeaders();
    				
    				
    				for(int i = 0; i < header.length; i++){
    					Header getHeader = header[i];
    					if(getHeader.getName().toString().equals("Set-Cookie")){
    						
    						sCookieKey = getHeader.getValue().split(";")[0].split("=")[0];
    						sCookieValue = getHeader.getValue().split(";")[0].split("=")[1];
    					}
    				}
    				//로그인하여 쿠키 얻기 종료
    				
    				//예약 가능 날짜를 구하기 위한 날짜별 예약 화면 호출
    				Document doc2 = Jsoup.connect("https://www.asianacc.co.kr:444/reservation/resv/rvstatus_n.asp")
    					    .cookie(sCookieKey, sCookieValue)
    					    .get();
    				//예약 가능 평일 년월일 구하기
    				String sWeekdayYear = "";
    				String sWeekdayMonth = "";
    				String sWeekdayDay = "";
    				String sWeekday = doc2.toString().substring(doc2.toString().indexOf("오늘은 평일<b> ")+10, doc2.toString().indexOf("오늘은 평일<b> ") + 23).replaceAll(" ", "");
    				sWeekdayYear = sWeekday.split("년")[0];
    				sWeekday = sWeekday.split("년")[1];
    				sWeekdayMonth = sWeekday.split("월")[0];
    				sWeekday = sWeekday.split("월")[1];
    				sWeekdayDay = sWeekday.split("일")[0];
    				//예약 가능 주말 년월일 구하기
    				String sWeekendYear = "";
    				String sWeekendMonth = "";
    				String sWeekendDay = "";
    				String sWeekend = doc2.toString().substring(doc2.toString().indexOf("</b>, 휴일<b>")+11, doc2.toString().indexOf("</b>, 휴일<b>") + 24).replaceAll(" ", "");
    				sWeekendYear = sWeekend.split("년")[0];
    				sWeekend = sWeekend.split("년")[1];
    				sWeekendMonth = sWeekend.split("월")[0];
    				sWeekend = sWeekend.split("월")[1];
    				sWeekendDay = sWeekend.split("일")[0];
    				//예약 가능 날짜 구하기 종료
    				
    				if(!sTargetTeeOffDate.equals("")) {
    					sWeekdayYear = sTargetTeeOffDate.substring(0,4);
    					sWeekdayMonth = sTargetTeeOffDate.substring(4,6);
    					sWeekdayDay = sTargetTeeOffDate.substring(6,8);
    					sWeekendYear = sTargetTeeOffDate.substring(0,4);
    					sWeekendMonth = sTargetTeeOffDate.substring(4,6);
    					sWeekendDay = sTargetTeeOffDate.substring(6,8);
    				}
    				
    				//예약 조회 시작
    				Document doc = Jsoup.connect("https://www.asianacc.co.kr:444/reservation/resv/rvstatus_n.asp")
    						.cookie(sCookieKey, sCookieValue)
    					    .data("rvdate1", bWeekend==false?sWeekdayYear:sWeekendYear)
    					    .data("rvdate2", bWeekend==false?sWeekdayMonth:sWeekendMonth)
    					    .data("rvdate3", bWeekend==false?sWeekdayDay:sWeekendDay)
    					    .data("stpoint", "A")
    					    .data("viewflag", "2")
    					    .post();

    				if(doc.toString().contains("예약은 오전 9시부터 가능합니다.")) {
    					System.out.println("예약은 오전 9시부터 가능합니다.");
    					bBookable = false;
    				} else {
    					bBookable = true;
    				}

        			if(bBookable) {
        				String sRvdate = "";
        				String sRvtime = "";
        				String sDateflag = "";
        				String sHflag = "";
        				String sNight_flag = "";
        				
        				//예약 가능한 상태로 해당 일자의 테이블을 조회하여 예약 가능 여부 추가 확인
        				//document 전체의 table을 가져옴
        	    		Elements tablesOnThePage = doc.getElementsByTag("table");
        	    		// 11번 테이블이 예약 가능 여부 달력임
        	    		Element tableOfCalendar = tablesOnThePage.get(11);
        	    		Elements aTagBookableColumn = tableOfCalendar.getElementsByTag("a");
        	    		loop: 
        	    		for(int i = 0; i < aTagBookableColumn.size(); i++) {
        	    			Element oneATagBookableColumn = aTagBookableColumn.get(i);
        	    			//아래 08이 08시 티옵을 의미함!!!!! 05면 05시대 티옵을 의미함
        	    			if(oneATagBookableColumn.toString().contains("check_resv('"+sTargetTeeOfftime)) {
        	    				String sParseString = oneATagBookableColumn.toString();
        	    				String sParameters = sParseString.substring(sParseString.indexOf("check_resv(")+11, sParseString.indexOf(")\" title=\""));

        	    				sRvdate = sParameters.split(",")[1].replaceAll("'", "");
        	    				sRvtime = sParameters.split(",")[0].replaceAll("'", "");
        	    				sDateflag = sParameters.split(",")[2].replaceAll("'", "");
        	    				sHflag = sParameters.split(",")[3].replaceAll("'", "");
        	    				sNight_flag = sParameters.split(",")[4].replaceAll("'", "");
        	    				
        	    				Document doc3 = Jsoup.connect("https://www.asianacc.co.kr:444/reservation/resv/rvdetail.asp")
        	    						.cookie(sCookieKey, sCookieValue)
        	    					    .data("rvdate", sRvdate)
        	    					    .data("rvtime", sRvtime)
        	    					    .data("dateflag", sDateflag)
        	    					    .data("hflag", sHflag)
        	    					    .data("night_flag", sNight_flag)
        	    					    .get();
        	    				
        	    				Elements aTagBookableCourse = doc3.getElementsByTag("a");
        	    				for(int j = 0; j < aTagBookableCourse.size(); j++) {
        	    					Element oneATagBookableCourse = aTagBookableCourse.get(j);
        	    					
        	    					//서코스 우선, 코드는 21
        	    					if(oneATagBookableCourse.toString().contains("ChangeCourse('WOUT')")) {
        	    						if(booking("21", sRvdate, sRvtime, sDateflag, sHflag, sNight_flag)) {
        	    							bBookSuccess = true;
        	    							break loop;
        	    						}
        	    					//서코스 없으면 동IN, 코드는 12
        	    					} else if(oneATagBookableCourse.toString().contains("ChangeCourse('EIN')")) {
        	    						if(booking("12", sRvdate, sRvtime, sDateflag, sHflag, sNight_flag)) {
        	    							bBookSuccess = true;
        	    							break loop;
        	    						}
        	    					//서코스 없으면 동OUT, 코드는 11
        							} else if(oneATagBookableCourse.toString().contains("ChangeCourse('EOUT')")) {
        								if(booking("11", sRvdate, sRvtime, sDateflag, sHflag, sNight_flag)) {
        	    							bBookSuccess = true;
        	    							break loop;
        	    						}
        							} else {
        								continue;
        							}
        	    				}
        	    				
        	    			}
        	    		}
        				
        			} 
        			
    	    		//성공시
    	    		if(bBookSuccess) {	    			
    	    	    	return "Booking success!!! End booking";
    	    		} else {
    	    			System.out.println("Booking is not available... try again in 1 seconds");
    	    			Thread.sleep(1000);
    	    		}
    	    	} else {
    	    		System.out.println("It's not booking time. after 0859, it will start!!!!!!");
    	    		
    	    		Thread.sleep(1000);
        	    	/*
    	    		SimpleDateFormat detailformat = new SimpleDateFormat ( "yyyyMMddHHmmss");
        	    	SimpleDateFormat yyyyMMddformat = new SimpleDateFormat ( "yyyyMMdd");
        	    	Date detailTime = new Date();    
        	    	
    	    		Date dTargetStartTime = detailformat.parse(yyyyMMddformat.format(detailTime).toString()+"085955");
        	    	        	    	
    	    		long diff = dTargetStartTime.getTime() - dTime.getTime();
    	    		if(diff > 0) {
	    	    		System.out.println("Sleep "+diff + " micro seconds");
	    	    		System.out.println("Sleep "+diff/1000 + "  seconds");
	    	    		System.out.println("Sleep "+diff/1000/60 + " minutes");
	    	    		System.out.println("Sleep "+diff/3600000 + " hours");
	    	    		
	    	    		Thread.sleep(diff);
    	    		} else {
    	    			Thread.sleep(1000);
    	    		}
    	    		*/
    	    	}
    		}
    	} catch (Exception e) {
    	    // Exp : Connection Fail
    	    e.printStackTrace();
    	    return "booking failed with exception";
    	}
    }

    private static boolean booking(String sCourse, String sRvdate, String sRvtime, String sDateflag, String sHflag, String sNight_flag) {
    	Boolean bSucc = false;
		try {    	
			Document docTest1 = Jsoup.connect("https://www.asianacc.co.kr:444/reservation/resv/recheck.asp")
					.cookie(sCookieKey, sCookieValue)
					.data("dateflag"		,sDateflag)
					.data("hflag"			,sHflag)
					.data("min_teeup"		,sRvtime)
					.data("min_reserv_cnt","3")
					.data("max_reserv_cnt","4")
					.data("viewtime"		,"���� 06�� 30 ��")
					.data("rvtime"		,sRvtime)
					.data("cart"			,"1")
					.data("tema"			,"01")
					.data("viewcourse"	,"��")
					.data("course"		,sCourse)
					.data("emer"			,"01053025245")
					.data("rv_tel"		,"01053025245")
					.data("caddie"		,"1")
					.data("c_man"			,"D1")
					.data("rvcount"		,"4")
					.data("remarks"	    ,"")
					.data("rvdate"		,sRvdate)
				    .post();
			
			System.out.println(docTest1.toString());
			
			Document docTest2 = Jsoup.connect("https://www.asianacc.co.kr:444/reservation/resv/rvfo.asp")
					.cookie(sCookieKey, sCookieValue)
					.data("rvcount",	"4")
					.data("rvdate",		sRvdate)
					.data("rvtime",		sRvtime)
					.data("caddie",		"1")
					.data("course",		sCourse)
					.data("cart",		"1")
					.data("remarks",	"")
					.data("emer",		"01053025245")
					.data("tema",		"01")
					.data("rv_tel",		"01053025245")
					.data("dateflag",	sDateflag)
					.data("hflag",		sHflag)
					.data("joinflag",	"")
					.data("joinmemo",	"")	
				    .get();
			
			System.out.println(docTest2.toString());
			
			
		
			Document docTest3 = Jsoup.connect("https://www.asianacc.co.kr:444/reservation/resv/riinfo.asp")
					.cookie(sCookieKey, sCookieValue)
					.data("x",			"35")
					.data("y",			"17")
					.data("rvcount",	"4")
					.data("rvdate",		sRvdate)
					.data("rvtime",		sRvtime)
					.data("caddie",		"1")
					.data("course",		sCourse)
					.data("cart",		"1")
					.data("remarks",	"")
					.data("emer",		"01053025245")
					.data("tema",		"01")
					.data("rv_tel",		"01053025245")
					.data("dateflag",	sDateflag)
					.data("hflag",		sHflag)
					.data("sflag",	    "")
					.data("seqn",	    "")
					.data("joinflag",	"")
					.data("joinmemo",	"")
				    .post();
			
			System.out.println(docTest3.toString());
			bSucc = true;
	    } catch (Exception e){
			System.err.println(e.toString());
			return bSucc;
		} finally {
			return bSucc;
		}
	}

}
