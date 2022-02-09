package com.changoo.owners;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class OwnersGCBooking {
	
	static String sCookieKey = "";
	static String sCookieValue = "";
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.print(runBooking());
	}
	
	public static Boolean runBooking() {
		Boolean bSucc = false;
		try {
			Boolean bWeekend = false;
			Boolean bNotFinish = true;

	        while(bNotFinish) {
			
				Connection.Response loginPageResponse = Jsoup.connect("https://www.ownersgc.co.kr/html/member/login.asp")
	                    //.timeout(3000)
	                    .header("Accept", "text/html, application/xhtml+xml, image/jxr, */*")
	                    .header("Accept-Language", "ko-KR")
	                    .header("Content-Type", "application/x-www-form-urlencoded")
	                    .header("User-Agent", " Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Geckod")
	                    .header("Accept-Encoding", "gzip, deflate")
	                    .header("Host", "www.ownersgc.co.kr")
	                    .header("Connection", "Keep-Alive")
	                    .header("Cookie", "MEM%5FID=80100900; MEM%5FPASS=yungyung60%21; ASPSESSIONIDQGCRQSQR=HPFAEPCADFAIKNOJKHPOALAH")
	                    .method(Connection.Method.GET)
	                    .execute();
	
				//로그인 페이지에서 얻은 쿠키
				Map<String, String> loginTryCookie = loginPageResponse.cookies();
				
				// 전송할 폼 데이터
				Map<String, String> data = new HashMap<String, String>();
				data.put("gopath", "");
				data.put("memb_passpw", "");
				data.put("memb_inet_no", "80100900");
				data.put("memb_inet_pass", "yungyung60!");
				data.put("mnSave", "1"); 				
							
				// 로그인(POST)
				Connection.Response response = Jsoup.connect("https://www.ownersgc.co.kr/html/member/login_ok.asp")
				                                    .header("Accept", "text/html, application/xhtml+xml, image/jxr, */*")
				                                    .header("Referer", "https://www.ownersgc.co.kr/html/member/login.asp")
				                                    .header("Accept-Language", "ko-KR")
				                                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko")
				                                    .header("Content-Type", "application/x-www-form-urlencoded")
				                                    .header("Accept-Encoding", "gzip, deflate")
				                                    .header("Host", "www.ownersgc.co.kr")
				                                    .header("Content-Length", "80")
				                                    .header("Connection", "Keep-Alive")
				                                    .header("Cache-Control", "no-cache")
				                                    .header("Cache-Control", "no-cache")
				                                    .cookies(loginTryCookie)
				                                    .data(data)
				                                    .method(Connection.Method.POST)
				                                    .execute();
	
				// 로그인 성공 후 얻은 쿠키.
				Map<String, String> loginCookie = response.cookies();
				
				Document reservePageDocument = Jsoup.connect("https://www.ownersgc.co.kr/html/reservation/reservation_01_01.asp")
	                    .header("Accept", "text/html, application/xhtml+xml, image/jxr, */*")
	                    .header("Referer", "https://www.ownersgc.co.kr/index.asp")
	                    .header("Accept-Language", "ko-KR")
	                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko")
	                    .header("Accept-Encoding", "gzip, deflate")
	                    .header("Host", "www.ownersgc.co.kr")
	                    .header("Connection", "Keep-Alive")
	                    .cookies(loginCookie) // 위에서 얻은 '로그인 된' 쿠키
	                    .get();
	
				Elements eTdElementsList = reservePageDocument.getElementsByTag("td");
				Elements eBookingElementsList = new Elements(); 
				for(int i=0; i < eTdElementsList.size(); i++) {
					Element eBookingElement = eTdElementsList.get(i);
					if(eBookingElement.toString().contains("<td title=\"예약가능\"")) {
						eBookingElementsList.add(eBookingElement);
					}
				}
	
		        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
	       
		        // 뒤에달 예약 리스트 가져오기
		        Calendar cal = Calendar.getInstance();	        
		        cal.add(Calendar.MONTH, 1); // 1달 후
		        String sNextMonth = sdf.format(cal.getTime());
				
				data = new HashMap<String, String>();
				data.put("ThisDate", sNextMonth);
				
				Document reservePageDocumentNextMonth = Jsoup.connect("https://www.ownersgc.co.kr/html/reservation/reservation_01_01.asp")
	                    .header("Accept", "text/html, application/xhtml+xml, image/jxr, */*")
	                    .header("Referer", "https://www.ownersgc.co.kr/html/reservation/reservation_01_01.asp")
	                    .header("Accept-Language", "ko-KR")
	                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko")
	                    .header("Accept-Encoding", "gzip, deflate")
	                    .header("Host", "www.ownersgc.co.kr")
	                    .header("Connection", "Keep-Alive")
	                    .cookies(loginCookie) // 위에서 얻은 '로그인 된' 쿠키
	                    .data(data)
					    .get();
								
				eTdElementsList.clear();
				eTdElementsList = reservePageDocumentNextMonth.getElementsByTag("td");
				for(int i=0; i < eTdElementsList.size(); i++) {
					Element eBookingElement = eTdElementsList.get(i);
					if(eBookingElement.toString().contains("<td title=\"예약가능\"")) {
						eBookingElementsList.add(eBookingElement);
					}
				}
				
				cal = Calendar.getInstance();	    	        
		        cal.add(Calendar.DATE, 8); // 8일 후
		        
		        sdf = new SimpleDateFormat("yyyyMMdd");
		        
		        String sNextWeek = sdf.format(cal.getTime());
		        //마지막날 예약
		        String sLastBookingElement = eBookingElementsList.get(eBookingElementsList.size()-1).toString();
		        String sYear = sLastBookingElement.substring(sLastBookingElement.indexOf("JavaScript:Date_Click('")+23,sLastBookingElement.indexOf("JavaScript:Date_Click('")+27);
	        	String sMonth = sLastBookingElement.substring(sLastBookingElement.indexOf("JavaScript:Date_Click('")+30,sLastBookingElement.indexOf("JavaScript:Date_Click('")+32);
	        	String sDate = sLastBookingElement.substring(sLastBookingElement.indexOf("JavaScript:Date_Click('")+35,sLastBookingElement.indexOf("JavaScript:Date_Click('")+37);
		        sNextWeek = sYear + sMonth + sDate;
		        for(int i=0; i < eBookingElementsList.size(); i++) {
		        	String sBookingDate = eBookingElementsList.get(i).toString();
		        	sYear = sBookingDate.substring(sBookingDate.indexOf("JavaScript:Date_Click('")+23,sBookingDate.indexOf("JavaScript:Date_Click('")+27);
		        	sMonth = sBookingDate.substring(sBookingDate.indexOf("JavaScript:Date_Click('")+30,sBookingDate.indexOf("JavaScript:Date_Click('")+32);
		        	sDate = sBookingDate.substring(sBookingDate.indexOf("JavaScript:Date_Click('")+35,sBookingDate.indexOf("JavaScript:Date_Click('")+37);
		        	String sPossibleBookingDate = sYear.concat(sMonth).concat(sDate);
		        	if(Integer.parseInt(sPossibleBookingDate) >= Integer.parseInt(sNextWeek)) {
		        		//예약 가능 날짜를 구하기 위한 날짜별 예약 화면 호출
		    			data = new HashMap<String, String>();
		    			data.put("book_date_bd", sPossibleBookingDate);
		    			data.put("book_date_be", "");
		    			data.put("book_crs", "");
		    			data.put("book_crs_name", "");
		    			data.put("book_time", "");	
		    			
		    			Document reserveDateDocument = Jsoup.connect("https://www.ownersgc.co.kr/html/reservation/reservation_01_01.asp")
		                        .header("Accept", "text/html, application/xhtml+xml, image/jxr, */*")
		                        .header("Referer", "https://www.ownersgc.co.kr/html/reservation/reservation_01_01.asp")
		                        .header("Accept-Language", "ko-KR")
		                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko")
		                        .header("Accept-Encoding", "gzip, deflate")
		                        .header("Host", "www.ownersgc.co.kr")
		                        .header("Connection", "Keep-Alive")
		                        .cookies(loginCookie) // 위에서 얻은 '로그인 된' 쿠키
		                        .data(data)
		    				    .get();
	
		    			Elements eRevElementsList = reserveDateDocument.getElementsByClass("rev_time");
		    			if(eRevElementsList.size()==0) {
		    				System.out.println("N/A on "+sPossibleBookingDate+"...");
		    				continue;
		    			} else {
		    				
		    				//일단 첫 번째꺼라도 예약 되도록 진행해보고 나중에 수정
		    				/*<div class="rev_time">
		    				 11:52 
		    				 <span class="time_price">90,000</span>
		    				 <a href="JavaScript:Book_Confirm1('20220204','금','1', 'HILL','1152');"><span class="reservation_button_2">18홀예약</span></a>
		    				</div>
		    				*/
		    				Element eRevElement = eRevElementsList.get(0);
		    				String sRevElementTemp = eRevElement.toString();
		    				String[] sRevElement = sRevElementTemp.substring(sRevElementTemp.indexOf("Confirm1(")+9, sRevElementTemp.indexOf(");\"")).split(",");
		    				//function Book_Confirm1 (i_date, i_week,  i_crs, i_crs_name, i_time) {
		    				/*
								obj.book_date_a.value = i_date;
								obj.book_crs.value = i_crs;
								obj.book_crs_name.value = i_crs_name;
								obj.book_time.value = i_time;
								obj.action = 'reservation_01_02.asp';
		    				 */
		    				data = new HashMap<String, String>();
			    			data.put("book_date_a", sRevElement[0].replaceAll("'", ""));
			    			data.put("book_crs", sRevElement[2].replaceAll("'", ""));
			    			data.put("book_crs_name", sRevElement[3].replaceAll("'", ""));
			    			data.put("book_time", sRevElement[4].replaceAll("'", ""));	
			    			
			    			Document reserveCompleteDocument = Jsoup.connect("https://www.ownersgc.co.kr/html/reservation/reservation_01_02.asp")
			                        .header("Accept", "text/html, application/xhtml+xml, image/jxr, */*")
			                        .header("Referer", "https://www.ownersgc.co.kr/html/reservation/reservation_01_01.asp")
			                        .header("Accept-Language", "ko-KR")
			                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko")
			                        .header("Accept-Encoding", "gzip, deflate")
			                        .header("Host", "www.ownersgc.co.kr")
			                        .header("Connection", "Keep-Alive")
			                        .cookies(loginCookie) // 위에서 얻은 '로그인 된' 쿠키
			                        .data(data)
			    				    .post();
			    			System.out.println(reserveCompleteDocument);
			    			bNotFinish = false;
			    			bSucc = true;
			    			break;
		    			}
		        	}
		        }
				System.out.println("All available booking date has reserved... trying again in 3...");
		        Thread.sleep(1000);
		        System.out.println("All available booking date has reserved... trying again in 2...");
		        Thread.sleep(1000);
		        System.out.println("All available booking date has reserved... trying again in 1...");
		        Thread.sleep(1000);
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
