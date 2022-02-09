package com.changoo.asianacc;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

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

public class TestConnection {
	
	static String sCookieKey = "";
	static String sCookieValue = "";
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.print(test());
	}
	
	public static Boolean test() {
		Boolean bSucc = false;
		try {
			Boolean bWeekend = false;
			
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
					System.out.println(getHeader.getValue());
					sCookieKey = getHeader.getValue().split(";")[0].split("=")[0];
					sCookieValue = getHeader.getValue().split(";")[0].split("=")[1];
				}
			}
			
			
			
			
			
			
			
			
			
			
			//예약 가능 날짜를 구하기 위한 날짜별 예약 화면 호출
			Document doc2 = Jsoup.connect("https://www.asianacc.co.kr:444/reservation/resv/rvstatus_n.asp")
				    .cookie(sCookieKey, sCookieValue)
				    .get();
			//예약 가능 평일 년월일 구하기
			String sWeekdayYear = "";
			String sWeekdayMonth = "";
			String sWeekdayDay = "";
			System.out.println(doc2);
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
			
			Document doc = Jsoup.connect("https://www.asianacc.co.kr:444/reservation/resv/rvstatus_n.asp")
					.cookie(sCookieKey, sCookieValue)
				    .data("rvdate1", bWeekend==false?sWeekdayYear:sWeekendYear)
				    .data("rvdate2", bWeekend==false?sWeekdayMonth:sWeekendMonth)
				    .data("rvdate3", "03")
				    .data("stpoint", "A")
				    .data("viewflag", "2")
				    .post();
			
			System.out.println(doc.toString());
			if(doc.toString().contains("예약은 오전 9시부터 가능합니다.")) {
				System.out.println("예약은 오전 9시부터 가능합니다.");
			}
			
			//document 전체의 table을 가져옴
    		Elements tablesOnThePage = doc.getElementsByTag("table");
    		// 11번 테이블이 예약 가능 여부 달력임
    		Element tableOfCalendar = tablesOnThePage.get(11);
    		Elements aTagBookableColumn = tableOfCalendar.getElementsByTag("a");
    		for(int i = 0; i < aTagBookableColumn.size(); i++) {
    			Element oneATagBookableColumn = aTagBookableColumn.get(i);
    			
    			if(oneATagBookableColumn.toString().contains("check_resv('05")) {
    				String sParseString = oneATagBookableColumn.toString();
    				String sParameters = sParseString.substring(sParseString.indexOf("check_resv(")+11, sParseString.indexOf(")\" title=\""));

    				Document doc3 = Jsoup.connect("https://www.asianacc.co.kr:444/reservation/resv/rvdetail.asp")
    						.cookie(sCookieKey, sCookieValue)
    					    .data("rvdate", sParameters.split(",")[1].replaceAll("'", ""))
    					    .data("rvtime", sParameters.split(",")[0].replaceAll("'", ""))
    					    .data("dateflag", sParameters.split(",")[2].replaceAll("'", ""))
    					    .data("hflag", sParameters.split(",")[3].replaceAll("'", ""))
    					    .data("night_flag", sParameters.split(",")[4].replaceAll("'", ""))
    					    .get();
    				
    				System.out.println(doc3);
    				Elements aTagBookableCourse = doc3.getElementsByTag("a");
    				for(int j = 0; j < aTagBookableCourse.size(); j++) {
    					Element oneATagBookableCourse = aTagBookableCourse.get(j);
    					
    					//서코스 우선, 코드는 21
    					if(oneATagBookableCourse.toString().contains("ChangeCourse('WOUT')")) {
    						if(booking("21")) {
    							
    						}
    					//서코스 없으면 동IN, 코드는 12
    					} else if(oneATagBookableCourse.toString().contains("ChangeCourse('EIN')")) {
    						
    					//서코스 없으면 동OUT, 코드는 11
						} else if(oneATagBookableCourse.toString().contains("ChangeCourse('EOUT')")) {
							
						}
    				}
    				System.out.println(aTagBookableCourse);
    				//https://www.asianacc.co.kr:444/reservation/resv/rvdetail.asp?rvdate=20210503&rvtime=0600&dateflag=2&hflag=&night_flag=D
    			}
    		}
    		
			ResponseHandler<String> handler = new BasicResponseHandler();
			String body = handler.handleResponse(response);
			if(body.contains("success")) {
				System.out.println(body);
				bSucc = true;
			}
			//Response 출력
			if (response.getStatusLine().getStatusCode() == 200) {
			} else {
				System.out.println("response is error : " + response.getStatusLine().getStatusCode());
				bSucc = false;
			}
		} catch (Exception e){
			System.err.println(e.toString());
			return bSucc;
		} finally {
			return bSucc;
		}
	}
	
	public static Boolean booking(String sCourse) throws Exception {
		/////////////////테스트////////////////////////////
			Document docTest1 = Jsoup.connect("https://www.asianacc.co.kr:444/reservation/resv/recheck.asp")
					.cookie(sCookieKey, sCookieValue)
					.data("dateflag"		,"2")
					.data("hflag"			,"2")
					.data("min_teeup"		,"0630")
					.data("min_reserv_cnt","3")
					.data("max_reserv_cnt","4")
					.data("viewtime"		,"���� 06�� 30 ��")
					.data("rvtime"		,"0630")
					.data("cart"			,"1")
					.data("tema"			,"01")
					.data("viewcourse"	,"��")
					.data("course"		,"21")
					.data("emer"			,"01053025245")
					.data("rv_tel"		,"01053025245")
					.data("caddie"		,"1")
					.data("c_man"			,"D1")
					.data("rvcount"		,"4")
					.data("remarks"	    ,"")
					.data("rvdate"		,"20210503")
				    .post();
			
			System.out.println(docTest1.toString());
			
			Document docTest2 = Jsoup.connect("https://www.asianacc.co.kr:444/reservation/resv/rvfo.asp")
					.cookie(sCookieKey, sCookieValue)
					.data("rvcount",	"4")
					.data("rvdate",		"20210503")
					.data("rvtime",		"0630")
					.data("caddie",		"1")
					.data("course",		"21")
					.data("cart",		"1")
					.data("remarks",	"")
					.data("emer",		"01053025245")
					.data("tema",		"01")
					.data("rv_tel",		"01053025245")
					.data("dateflag",	"2")
					.data("hflag",		"2")
					.data("joinflag",	"")
					.data("joinmemo",	"")	
				    .get();
			
			System.out.println(docTest2.toString());
			
			
		
			Document docTest3 = Jsoup.connect("https://www.asianacc.co.kr:444/reservation/resv/riinfo.asp")
					.cookie(sCookieKey, sCookieValue)
					.data("x",			"35")
					.data("y",			"17")
					.data("rvcount",	"4")
					.data("rvdate",		"20210503")
					.data("rvtime",		"0630")
					.data("caddie",		"1")
					.data("course",		"21")
					.data("cart",		"1")
					.data("remarks",	"")
					.data("emer",		"01053025245")
					.data("tema",		"01")
					.data("rv_tel",		"01053025245")
					.data("dateflag",	"2")
					.data("hflag",		"2")
					.data("sflag",	    "")
					.data("seqn",	    "")
					.data("joinflag",	"")
					.data("joinmemo",	"")
				    .post();
			
			System.out.println(docTest3.toString());
		return true;
	}

}
