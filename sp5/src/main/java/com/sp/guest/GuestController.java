package com.sp.guest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sp.common.MyUtil;
import com.sp.member.SessionInfo;

@Controller("guest.guestController")
public class GuestController {
	@Autowired
	private GuestService guestService;
	@Autowired
	private MyUtil myUtil;
	
	// AJAX (X)
	@RequestMapping(value="/guest/guest")
	public String main() {
		return ".guest.guest";
	}
	
	/*
	 * @ResponseBody : 리턴 객체를 HTTP 응답으로 전송
	 * HttpMessageConverter를 이용하여 객체를 Http 응답 스트림으로 변환 (일반적으로 XML이나 JSON과 같은 메세지 기반의 커뮤니케이션을 위해 사용)
	 * 
	 * 기본적으로 @ResponseBody에서 Map<>을 리턴하면 JSON으로 변환하여 전송
	 * */
	@RequestMapping(value="/guest/insert", method=RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> insertSubmit(Guest dto, HttpSession session) {
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		dto.setUserId(info.getUserId());
		
		guestService.insertGuest(dto);
		
		Map<String, Object> model = new HashMap<>();
		model.put("state", "true");
		
		return model;
	}
	
	@RequestMapping(value="/guest/list")
	@ResponseBody
	public Map<String, Object> list(@RequestParam(value="pageNo", defaultValue="1") int current_page) {
		int rows = 10;
		int dataCount;
		int total_page;
		
		dataCount = guestService.dataCount();
		total_page = myUtil.pageCount(rows, dataCount);
		
		if(current_page > total_page) {
			current_page = total_page;
		}
		
		int start = (current_page - 1) * rows + 1;
		int end = current_page * rows;
		
		Map<String, Object> map = new HashMap<>();
		map.put("start", start);
		map.put("end", end);
		
		List<Guest> list = guestService.listGuest(map);
		
		for(Guest dto : list) {
			dto.setContent(myUtil.htmlSymbols(dto.getContent()));
		}
		
		// listPage : javaScript 함수명
		String paging = myUtil.pagingMethod(current_page, total_page, "listPage");
		
		Map<String, Object> model = new HashMap<>();
		
		model.put("list", list);
		model.put("dataCount", dataCount);
		model.put("pageNo", current_page);
		model.put("total_page", total_page);
		/*model.put("paging", paging);*/
		
		return model;
	}
	
	@RequestMapping(value="/guest/delete", method=RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> deleteSubmit(@RequestParam int num, HttpSession session) {
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		Map<String, Object> map = new HashMap<>();
		map.put("num", num);
		map.put("userId", info.getUserId());
		
		guestService.deleteGuest(map);
		
		Map<String, Object> model = new HashMap<>();
		model.put("state", "true");
		
		return model;
	}
}
