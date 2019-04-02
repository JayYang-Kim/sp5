package com.sp.bbs;

import java.io.File;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sp.common.FileManager;
import com.sp.common.MyUtil;
import com.sp.member.SessionInfo;

@Controller("bbs.boardController")
public class BoardController {
	@Autowired
	private BoardService boardService;
	@Autowired
	private MyUtil myUtil;
	@Autowired
	private FileManager fileManager;
	
	@RequestMapping(value="/bbs/list")
	public String list(
			@RequestParam(value="page", defaultValue="1") int current_page,
			@RequestParam(defaultValue="all") String condition,
			@RequestParam(defaultValue="") String keyword,
			HttpServletRequest req,
			Model model) throws Exception {
		
		if(req.getMethod().equalsIgnoreCase("GET")) {
			keyword=URLDecoder.decode(keyword, "UTF-8");
		}
		
		int total_page = 0;
		int dataCount = 0;
		int rows = 10;
		
		Map<String, Object> map=new HashMap<>();
		map.put("condition", condition);
		map.put("keyword", keyword);
		dataCount=boardService.dataCount(map);
		if(dataCount!=0)
			total_page=myUtil.pageCount(rows, dataCount);
		
		if(current_page>total_page)
			current_page=total_page;
		
		int start = (current_page-1)*rows+1;
		int end = current_page*rows;
		
		map.put("start", start);
		map.put("end", end);
		List<Board> list = boardService.listBoard(map);
		
		int listNum, n=0;
		for(Board dto:list) {
			listNum = dataCount - (start+n-1);
			dto.setListNum(listNum);
			n++;
		}
		
		String cp = req.getContextPath();
		String query = "";
		String listUrl = cp + "/bbs/list";
		String articleUrl = cp + "/bbs/article?page="+current_page;
		if(keyword.length()!=0) {
			query = "condition=" + condition + "&keyword=" +
		        URLEncoder.encode(keyword, "UTF-8");
			
			listUrl += "?" + query;
			articleUrl += "&" + query;
		}
		
		String paging = myUtil.paging(current_page, total_page, listUrl);
		
		model.addAttribute("list", list);
		model.addAttribute("dataCount", dataCount);
		model.addAttribute("page", current_page);
		model.addAttribute("total_page", total_page);
		model.addAttribute("paging", paging);
		model.addAttribute("articleUrl", articleUrl);
		model.addAttribute("condition", condition);
		model.addAttribute("keyword", keyword);
		
		return ".bbs.list";
	}
	
	@RequestMapping(value="/bbs/created", method=RequestMethod.GET)
	public String cretaedForm(
			Model model) throws Exception {
		
		model.addAttribute("mode", "created");
		return ".bbs.created";
	}
	
	@RequestMapping(value="/bbs/created", method=RequestMethod.POST)
	public String cretaedSubmit(
			HttpSession session,
			Board dto
			) throws Exception {
		SessionInfo info=(SessionInfo)session.getAttribute("member");
		
		String root = session.getServletContext().getRealPath("/");
		String pathname=root+"uploads"+File.separator+"bbs";
		
		dto.setUserId(info.getUserId());
		
		boardService.insertBoard(dto, pathname);
		
		return "redirect:/bbs/list";
	}
	
	@RequestMapping(value="/bbs/article")
	public String article(
			@RequestParam int num,
			@RequestParam String page,
			@RequestParam(defaultValue="all") String condition,
			@RequestParam(defaultValue="") String keyword,
			HttpSession session,
			Model model
			) throws Exception {
		
		String root = session.getServletContext().getRealPath("/");
		String pathname=root+"uploads"+File.separator+"bbs";
		
		keyword = URLDecoder.decode(keyword, "utf-8");
		String query="page="+page;
		if(keyword.length()!=0) {
			query+="&condition="+condition+"&keyword="+
		          URLEncoder.encode(keyword, "UTF-8");
		}
		
		boardService.updateHitCount(num);
		
		Board dto = boardService.readBoard(num);
		if(dto==null) {
			return "redirect:/bbs/list?"+query;
		}
		
		if(dto.getSaveFilename()!=null) {
			File f=new File(pathname+File.separator+dto.getSaveFilename());
			if(f.exists())
				dto.setFilesize(f.length());
		}
		
		dto.setContent(myUtil.htmlSymbols(dto.getContent()));
		
		Map<String, Object> map = new HashMap<>();
		map.put("condition", condition);
		map.put("keyword", keyword);
		map.put("num", num);
		Board preReadDto = boardService.preReadBoard(map);
		Board nextReadDto = boardService.nextReadBoard(map);
		
		model.addAttribute("dto", dto);
		model.addAttribute("preReadDto", preReadDto);
		model.addAttribute("nextReadDto", nextReadDto);
		model.addAttribute("page", page);
		model.addAttribute("query", query);
		
		return ".bbs.article";
	}
	
	@RequestMapping(value="/bbs/download")
	public void download(
			@RequestParam int num,
			HttpServletRequest req,
			HttpServletResponse resp,
			HttpSession session
			) throws Exception {
		
		String root=session.getServletContext().getRealPath("/");
		String pathname=root+"uploads"+File.separator+"bbs";
		
		Board dto=boardService.readBoard(num);
		if(dto!=null) {
			boolean b=fileManager.doFileDownload(dto.getSaveFilename(),
					dto.getOriginalFilename(), pathname, resp);
			if(b) return;
		}
		
		resp.setContentType("text/html;charset=utf-8");
		PrintWriter out=resp.getWriter();
		out.print("<script>alert('파일 다운로드를 실패했습니다.');history.back();</script>");
	}
	
	
	@RequestMapping(value="/bbs/update", 
			method=RequestMethod.GET)
	public String updateForm(
			@RequestParam int num,
			@RequestParam String page,
			HttpSession session,
			Model model) throws Exception {
		SessionInfo info=(SessionInfo)session.getAttribute("member");
		
		Board dto = boardService.readBoard(num);
		if(dto==null) {
			return "redirect:/bbs/list?page="+page;
		}

		if(! info.getUserId().equals(dto.getUserId())) {
			return "redirect:/bbs/list?page="+page;
		}
		
		model.addAttribute("dto", dto);
		model.addAttribute("mode", "update");
		model.addAttribute("page", page);
		
		return ".bbs.created";
	}

	@RequestMapping(value="/bbs/update", 
			method=RequestMethod.POST)
	public String updateSubmit(
			Board dto, 
			@RequestParam String page,
			HttpSession session) throws Exception {
		
		String root=session.getServletContext().getRealPath("/");
		String pathname=root+"uploads"+File.separator+"bbs";		
		// 수정 하기
		boardService.updateBoard(dto, pathname);		
		
		return "redirect:/bbs/list?page="+page;
	}
	
	@RequestMapping(value="/bbs/deleteFile")
	public String deleteFile(
			@RequestParam int num,
			@RequestParam String page,
			HttpSession session
			) throws Exception {
		SessionInfo info=(SessionInfo)session.getAttribute("member");
		
		String root=session.getServletContext().getRealPath("/");
		String pathname=root+"uploads"+File.separator+"bbs";
		
		Board dto=boardService.readBoard(num);
		if(dto==null) {
			return "redirect:/bbs/list?page="+page;
		}
		
		if(! info.getUserId().equals(dto.getUserId())) {
			return "redirect:/bbs/list?page="+page;
		}
		
		if(dto.getSaveFilename()!=null) {
			fileManager.doFileDelete(dto.getSaveFilename(), pathname); // 실제파일삭제
			dto.setSaveFilename("");
			dto.setOriginalFilename("");
			boardService.updateBoard(dto, pathname); // DB 테이블의 파일명 변경(삭제)
		}
		
		return "redirect:/bbs/update?num="+num+"&page="+page;
	}
	
	@RequestMapping(value="/bbs/delete")
	public String delete(
			@RequestParam int num,
			@RequestParam String page,
			HttpSession session) throws Exception {
		SessionInfo info=(SessionInfo)session.getAttribute("member");
		
		String root=session.getServletContext().getRealPath("/");
		String pathname=root+"uploads"+File.separator+"bbs";
		
		boardService.deleteBoard(num, pathname, info.getUserId());
		
		return "redirect:/bbs/list?page="+page;
	}
	
	@RequestMapping(value="/bbs/insertReply", method=RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> insertReply(Reply dto,
			HttpSession session  
			) {
		// AJAX-JSON
		SessionInfo info=(SessionInfo)session.getAttribute("member");
		dto.setUserId(info.getUserId());
		
		int result=boardService.insertReply(dto);
		String state="true";
		if(result==0)
			state="false";
		
		Map<String, Object> model=new HashMap<>();
		model.put("state", state);
		return model;
	}
	
	
	@RequestMapping(value="/bbs/listReply")
	public String listReply(
			@RequestParam int num,
			@RequestParam(value="pageNo", defaultValue="1") int current_page,
			Model model
			) throws Exception {
		// AJAX-text/html
		
		int rows = 5;
		int total_page;
		int dataCount = 0;
		
		Map<String, Object> map = new HashMap<>();
		map.put("num", num);
		
		dataCount = boardService.replyCount(map);
		total_page = myUtil.pageCount(rows, dataCount);
		if(current_page>total_page)
			current_page=total_page;
		
		int start = (current_page-1)*rows+1;
		int end = current_page*rows;
		
		map.put("start", start);
		map.put("end", end);
		
		List<Reply> listReply = boardService.listReply(map);
		for(Reply dto : listReply) {
			dto.setContent(myUtil.htmlSymbols(dto.getContent()));
		}
		
		// AJAX용 페이징
		String paging = myUtil.pagingMethod(current_page, 
				total_page, "listPage");
		
		// 포워딩할 JSP에 넘길 값
		model.addAttribute("listReply", listReply);
		model.addAttribute("pageNo", current_page);
		model.addAttribute("replyCount", dataCount);
		model.addAttribute("total_page", total_page);
		model.addAttribute("paging", paging);
		
		return "bbs/listReply";
	}
	
	@RequestMapping(value="/bbs/listReplyAnswer")
	public String listReplyAnswer(
			@RequestParam int answer,
			Model model) throws Exception {
		// 댓글의 답글 리스트 - AJAX : TEXT
		List<Reply> listReplyAnswer=boardService.listReplyAnswer(answer);
		for(Reply dto:listReplyAnswer) {
			dto.setContent(myUtil.htmlSymbols(dto.getContent()));
		}
		
		model.addAttribute("listReplyAnswer", listReplyAnswer);
		
		return "bbs/listReplyAnswer";
	}
	
	@RequestMapping(value="/bbs/countReplyAnswer")
	@ResponseBody
	public Map<String, Object>countReplyAnswer(
			@RequestParam int answer) throws Exception {
		// 댓글별 답글 개수 - AJAX : JSON
		int count=boardService.replyAnswerCount(answer);
		
		Map<String, Object> model=new HashMap<>();
		model.put("count", count);
		return model;
	}
	
	
	@RequestMapping(value="/bbs/boardLike", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> boardLike(@RequestParam int num,
			HttpSession session) throws Exception {
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		Map<String, Object> map = new HashMap<>();
		map.put("num", num);
		map.put("userId", info.getUserId());
		
		int result = boardService.insertBoardLike(map);
		String state = "true";
		
		if(result == 0) {
			state = "false";
		}
		
		int count = boardService.boardLikeCount(num);
		
		Map<String, Object> model = new HashMap<>();
		model.put("state", state);
		model.put("count", count);
		return model;
	}
	
	@RequestMapping(value="/bbs/insertReplyLike", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> insertReplyLike(@RequestParam Map<String, Object> paramMap,
			HttpSession session) throws Exception {
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		paramMap.put("userId", info.getUserId());
		
		int result = boardService.insertReplyLike(paramMap);
		String state = "true";
		
		if(result == 0) {
			state = "false";
		}
		
		Map<String, Object> countMap = boardService.replyLikeCount(paramMap);
		// 마이바티스에서 resultType이 map인 경우 int는 BigDecimal로 넘어옴
		int likeCount = ((BigDecimal)countMap.get("LIKECOUNT")).intValue();
		int disLikeCount = ((BigDecimal)countMap.get("DISLIKECOUNT")).intValue(); 
		
		Map<String, Object> model = new HashMap<>();
		model.put("state", state);
		model.put("likeCount", likeCount);
		model.put("disLikeCount", disLikeCount);
		return model;
	}
	
	@RequestMapping(value="/bbs/deleteReply", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> deleteReply(@RequestParam Map<String, Object> paramMap) throws Exception {
		int result = boardService.deleteReply(paramMap);
		
		String state = "true";
		
		if(result == 0) {
			state = "false";
		} 
		
		Map<String, Object> model = new HashMap<>();
		model.put("state", state);

		return model;
	}
}
