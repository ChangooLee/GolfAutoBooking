package com.changoo.owners;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

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
		//System.out.print(sendMail());
		System.out.print(runBooking());
	}
	
	@SuppressWarnings("finally")
	public static Boolean runBooking() {
		Boolean bSucc = false;
		try {
			Boolean bWeekend = false;
			Boolean bNotFinish = true;

	        while(bNotFinish) {
	        	//현재시간 구하기 로직 : 컴퓨터 시간임, 따라서 최대한 서버 시간과 맞아야 함, 내 컴퓨터는 거의 동기화 되어있는듯
    	    	SimpleDateFormat format = new SimpleDateFormat ( "HHmmss");
    	    	Date dTime = new Date();    			
    	    	int iTime = Integer.parseInt(format.format(dTime));
    	    	//아침 8시 59분 55초부터 예약 시작, 8시 0, 8, 15, 23, 30, 38, 45, 53분 티옵만 노리며 실패 시 1초 대기 후 재시도
    	    		
    	    	//if(iTime > 85955) {
    	    	//if(iTime > 85955) {
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
			        //sNextWeek = sYear + sMonth + sDate;
			        //sNextWeek = "20220302";
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
			    				//08시 가까운순 예약
			    				for(int k=0; k < eRevElementsList.size(); k++) {
				    				Element eRevElement = eRevElementsList.get(k);
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
				    				String sBookingTime = sRevElement[4].substring(1, 3);
				    				if(sBookingTime.equals("08")) {
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
						    			
						    			reserveCompleteDocument = Jsoup.connect("https://www.ownersgc.co.kr/html/reservation/reservation_01_03.asp")
						                        .header("Accept", "text/html, application/xhtml+xml, image/jxr, */*")
						                        .header("Referer", "https://www.ownersgc.co.kr/html/reservation/reservation_01_02.asp")
						                        .header("Accept-Language", "ko-KR")
						                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko")
						                        .header("Accept-Encoding", "gzip, deflate")
						                        .header("Host", "www.ownersgc.co.kr")
						                        .header("Connection", "Keep-Alive")
						                        .cookies(loginCookie) // 위에서 얻은 '로그인 된' 쿠키
						                        .data(data)
						    				    .post();
						    			System.out.println(reserveCompleteDocument);
						    			sendMail(sPossibleBookingDate, sRevElement[4].replaceAll("'", ""), sRevElement[3].replaceAll("'", ""), reserveCompleteDocument.toString());
						    			bNotFinish = false;
						    			bSucc = true;
						    			break;
				    				}				    				
			    				}
			    				//09시 가까운순 예약
			    				for(int k=0; k < eRevElementsList.size(); k++) {
				    				Element eRevElement = eRevElementsList.get(k);
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
				    				String sBookingTime = sRevElement[4].substring(1, 3);
				    				
				    				if(sBookingTime.equals("09")) {
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
						    			
						    			reserveCompleteDocument = Jsoup.connect("https://www.ownersgc.co.kr/html/reservation/reservation_01_03.asp")
						                        .header("Accept", "text/html, application/xhtml+xml, image/jxr, */*")
						                        .header("Referer", "https://www.ownersgc.co.kr/html/reservation/reservation_01_02.asp")
						                        .header("Accept-Language", "ko-KR")
						                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko")
						                        .header("Accept-Encoding", "gzip, deflate")
						                        .header("Host", "www.ownersgc.co.kr")
						                        .header("Connection", "Keep-Alive")
						                        .cookies(loginCookie) // 위에서 얻은 '로그인 된' 쿠키
						                        .data(data)
						    				    .post();
						    			System.out.println(reserveCompleteDocument);
						    			sendMail(sPossibleBookingDate, sRevElement[4].replaceAll("'", ""), sRevElement[3].replaceAll("'", ""), reserveCompleteDocument.toString());
						    			bNotFinish = false;
						    			bSucc = true;
						    			break;
				    				}
			    				}
			    				//07시 가까운순 예약
			    				for(int k=0; k < eRevElementsList.size(); k++) {
				    				Element eRevElement = eRevElementsList.get(k);
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
				    				String sBookingTime = sRevElement[4].substring(1, 3);
				    				
				    				//07시 가까운순 예약
				    				if(sBookingTime.equals("07")) {
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
						    			
						    			reserveCompleteDocument = Jsoup.connect("https://www.ownersgc.co.kr/html/reservation/reservation_01_03.asp")
						                        .header("Accept", "text/html, application/xhtml+xml, image/jxr, */*")
						                        .header("Referer", "https://www.ownersgc.co.kr/html/reservation/reservation_01_02.asp")
						                        .header("Accept-Language", "ko-KR")
						                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko")
						                        .header("Accept-Encoding", "gzip, deflate")
						                        .header("Host", "www.ownersgc.co.kr")
						                        .header("Connection", "Keep-Alive")
						                        .cookies(loginCookie) // 위에서 얻은 '로그인 된' 쿠키
						                        .data(data)
						    				    .post();
						    			System.out.println(reserveCompleteDocument);
						    			sendMail(sPossibleBookingDate, sRevElement[4].replaceAll("'", ""), sRevElement[3].replaceAll("'", ""), reserveCompleteDocument.toString());
						    			bNotFinish = false;
						    			bSucc = true;
						    			break;
				    				}
			    				}
			    				//06시 가까운순 예약
			    				for(int k=0; k < eRevElementsList.size(); k++) {
				    				Element eRevElement = eRevElementsList.get(k);
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
				    				String sBookingTime = sRevElement[4].substring(1, 3);
				    				if(sBookingTime.equals("06")) {
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
						    			
						    			reserveCompleteDocument = Jsoup.connect("https://www.ownersgc.co.kr/html/reservation/reservation_01_03.asp")
						                        .header("Accept", "text/html, application/xhtml+xml, image/jxr, */*")
						                        .header("Referer", "https://www.ownersgc.co.kr/html/reservation/reservation_01_02.asp")
						                        .header("Accept-Language", "ko-KR")
						                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko")
						                        .header("Accept-Encoding", "gzip, deflate")
						                        .header("Host", "www.ownersgc.co.kr")
						                        .header("Connection", "Keep-Alive")
						                        .cookies(loginCookie) // 위에서 얻은 '로그인 된' 쿠키
						                        .data(data)
						    				    .post();
						    			System.out.println(reserveCompleteDocument);
						    			sendMail(sPossibleBookingDate, sRevElement[4].replaceAll("'", ""), sRevElement[3].replaceAll("'", ""), reserveCompleteDocument.toString());
						    			bNotFinish = false;
						    			bSucc = true;
						    			break;
				    				}
			    				}
			    				//05시 가까운순 예약
			    				for(int k=0; k < eRevElementsList.size(); k++) {
				    				Element eRevElement = eRevElementsList.get(k);
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
				    				String sBookingTime = sRevElement[4].substring(1, 3);
				    				if(sBookingTime.equals("05")) {
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
						    			
						    			reserveCompleteDocument = Jsoup.connect("https://www.ownersgc.co.kr/html/reservation/reservation_01_03.asp")
						                        .header("Accept", "text/html, application/xhtml+xml, image/jxr, */*")
						                        .header("Referer", "https://www.ownersgc.co.kr/html/reservation/reservation_01_02.asp")
						                        .header("Accept-Language", "ko-KR")
						                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko")
						                        .header("Accept-Encoding", "gzip, deflate")
						                        .header("Host", "www.ownersgc.co.kr")
						                        .header("Connection", "Keep-Alive")
						                        .cookies(loginCookie) // 위에서 얻은 '로그인 된' 쿠키
						                        .data(data)
						    				    .post();
						    			System.out.println(reserveCompleteDocument);
						    			sendMail(sPossibleBookingDate, sRevElement[4].replaceAll("'", ""), sRevElement[3].replaceAll("'", ""), reserveCompleteDocument.toString());
						    			bNotFinish = false;
						    			bSucc = true;
						    			break;
				    				}
			    				}
			    				
			    				//그 외
			    				for(int k=0; k < eRevElementsList.size(); k++) {
				    				Element eRevElement = eRevElementsList.get(k);
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
				    				String sBookingTime = sRevElement[4].substring(1, 3);
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
					    			
					    			reserveCompleteDocument = Jsoup.connect("https://www.ownersgc.co.kr/html/reservation/reservation_01_03.asp")
					                        .header("Accept", "text/html, application/xhtml+xml, image/jxr, */*")
					                        .header("Referer", "https://www.ownersgc.co.kr/html/reservation/reservation_01_02.asp")
					                        .header("Accept-Language", "ko-KR")
					                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko")
					                        .header("Accept-Encoding", "gzip, deflate")
					                        .header("Host", "www.ownersgc.co.kr")
					                        .header("Connection", "Keep-Alive")
					                        .cookies(loginCookie) // 위에서 얻은 '로그인 된' 쿠키
					                        .data(data)
					    				    .post();
					    			System.out.println(reserveCompleteDocument);
					    			sendMail(sPossibleBookingDate, sRevElement[4].replaceAll("'", ""), sRevElement[3].replaceAll("'", ""), reserveCompleteDocument.toString());
					    			bNotFinish = false;
					    			bSucc = true;
					    			break;
			    				}
			    				if(bSucc == true) {
			    					break;
			    				}
			    			}
			        	}
			        }
			        int iRandom = (int) (Math.round(Math.random()*100));

		        	//현재시간 구하기 로직 : 컴퓨터 시간임, 따라서 최대한 서버 시간과 맞아야 함, 내 컴퓨터는 거의 동기화 되어있는듯
	    	    	SimpleDateFormat logFormat = new SimpleDateFormat ( "yyyyMMdd HH:mm:ss");
	    	    	Date dLog = new Date();	    			
	    	    	String sLogTime = logFormat.format(dLog);
			        System.out.println("["+sLogTime+"] All available booking date has reserved... trying again in " +iRandom + "ms...");
			        Thread.sleep(iRandom);
    	    	}
	        //}
			
		} catch (Exception e){
			System.err.println(e.toString());
			runBooking();
			return bSucc;
		} finally {
			return bSucc;
		}
	}

	static String user = "lchangoo@naver.com"; // 패스워드 
	static String password = "Cksrn0604!";      

	public static Boolean sendMail(String sReservedDate, String sReservedTime, String sReservedCourse, String sReservedContent) {
		String host = "smtp.naver.com"; // 네이버일 경우 네이버 계정, gmail경우 gmail 계정 
		// SMTP 서버 정보를 설정한다. 
		Properties props = new Properties(); 
		props.put("mail.smtp.host", host); 
		props.put("mail.smtp.port", 587); 
		props.put("mail.smtp.auth", "true"); 

		Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() { 
			protected PasswordAuthentication getPasswordAuthentication() { 
				return new PasswordAuthentication(user, password); 
			} 
		}); 
		try { 
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(user)); 
			
			InternetAddress[] addArray = new InternetAddress[2]; 
			addArray[0] = new InternetAddress("lchangoo@gmail.com"); 
			addArray[1] = new InternetAddress("wonjushon@gmail.com"); 
			message.addRecipients(Message.RecipientType.TO, addArray);

			
			// 메일 제목 
			message.setSubject("오너스 예약 완료 : 일자 [" +sReservedDate+ "], 티옵 [" + sReservedTime + "], 코스 [" + sReservedCourse + "]"); 
			
			// 메일 내용 
			message.setText("오너스 예약 완료 : 일자 [" +sReservedDate+ "], 티옵 [" + sReservedTime + "], 코스 [" + sReservedCourse + "] \rn" +  sReservedContent); 
			
			// send the message 
			Transport.send(message); 
			System.out.println("Success Message Send"); 
		} catch (MessagingException e) { 
			e.printStackTrace(); 
		}
		return true;
	}
	

}
