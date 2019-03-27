package com.sp.bbs;

import java.io.File;
import java.io.PrintWriter;
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
			keyword = URLDecoder.decode(keyword, "UTF-8");
		}
		
		int total_page = 0;
		int dataCount = 0;
		int rows = 10;
		
		Map<String, Object> map = new HashMap<>();
		map.put("condition", condition);
		map.put("keyword", keyword);
		
		dataCount = boardService.dataCount(map);
		
		if(dataCount != 0) {
			total_page = myUtil.pageCount(rows, dataCount);
		}
		
		if(current_page > total_page) {
			current_page = total_page;
		}
		
		int start = (current_page - 1) * rows + 1;
		int end = current_page * rows;
		
		map.put("start", start);
		map.put("end", end);
		
		List<Board> list = boardService.listBoard(map);
		
		int listNum, n = 0;
		
		for(Board dto : list) {
			listNum = dataCount - (start + n - 1);
			dto.setListNum(listNum);
			n++;
		}
		
		String cp = req.getContextPath();
		String query = "";
		String listUrl, articleUrl;
		
		listUrl = cp + "/bbs/list";
		articleUrl = cp + "/bbs/article?page=" + current_page;
		
		if(keyword.length() != 0) {
			query += "contidion=" + condition + "&keyword=" + URLEncoder.encode(keyword, "UTF-8");
			
			listUrl += "?" + query;
			articleUrl += "&" + query;
		}
		
		String paging = myUtil.paging(current_page, total_page, listUrl);
		
		model.addAttribute("list", list);
		model.addAttribute("dataCount", dataCount);
		model.addAttribute("page", current_page);
		model.addAttribute("current_page", current_page);
		model.addAttribute("total_page", total_page);
		model.addAttribute("paging", paging);
		model.addAttribute("articleUrl", articleUrl);
		model.addAttribute("condition", condition);
		model.addAttribute("keyword", keyword);
		
		return ".bbs.list";
	}
	
	@RequestMapping(value="/bbs/created", method = RequestMethod.GET)
	public String createdForm(HttpSession session, Model model) throws Exception {
		
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		if(info == null) {
			return "redirect:/member/login";
		}
		
		model.addAttribute("mode", "created");
		
		return ".bbs.created";
	}
	
	@RequestMapping(value="/bbs/created", method = RequestMethod.POST)
	public String createdSubmit(HttpSession session, Board dto) throws Exception {
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		if(info == null) {
			return "redirect:/member/login";
		}
		
		String root = session.getServletContext().getRealPath("/");
		// 파일 저장 경로
		String pathname = root + "uploads" + File.separator + "bbs";
		
		dto.setUserId(info.getUserId());
		
		boardService.insertBoard(dto, pathname);
		
		return "redirect:/bbs/list";
	}
	
	@RequestMapping(value="/bbs/article")
	public String article(
			HttpSession session,
			@RequestParam int num,
			@RequestParam String page,
			@RequestParam(defaultValue="all") String condition, 
			@RequestParam(defaultValue="") String keyword,
			Model model) throws Exception {
		
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		if(info == null) {
			return "redirect:/member/login";
		}
		
		String root = session.getServletContext().getRealPath("/");
		String pathname = root + "uploads" + File.separator + "bbs";
		
		// 검색값 디코딩
		keyword = URLDecoder.decode(keyword, "UTF-8");
		
		// 이전글, 다음글, 리스트에서 사용할 파라미터
		String query = "page=" + page;
		if(keyword.length() != 0) {
			query += "&condition=" + condition + "&keyword=" + URLEncoder.encode(keyword, "UTF-8");
		}
		
		// 조회 수 증가
		boardService.updateHitCount(num);
		
		// 게시글 가져오기
		Board dto = null;
		dto = boardService.readBoard(num);
		
		// 게시글이 없으면 리스트로 리다이렉트
		if(dto == null) {
			return "redirect:/bbs/list?" + query;
		}
		
		if(dto.getSaveFilename() != null) {
			File f = new File(pathname + File.separator + dto.getSaveFilename());
			if(f.exists()) {
				dto.setFilesize(f.length());
			}
		}
		
		System.out.println(dto.getFilesize());
		
		// 글내용 엔터등을 <br>로 변경
		dto.setContent(myUtil.htmlSymbols(dto.getContent()));
		
		// 이전글 가져오기
		Map<String, Object> map = new HashMap<>();
		map.put("condition", condition);
		map.put("keyword", keyword);
		map.put("num", num);
		
		Board preReadBoard = boardService.preReadBoard(map);
		
		// 다음글 가져오기
		Board nextReadBoard = boardService.nextReadBoard(map);
		
		// 포워딩할 jsp에 넘길 데이터 dto, 이전글, 다음글, query, 페이지 번호, rows
		model.addAttribute("dto", dto);
		model.addAttribute("preReadBoard", preReadBoard);
		model.addAttribute("nextReadBoard", nextReadBoard);
		model.addAttribute("query", query);
		model.addAttribute("page", page);

		return ".bbs.article";
	}
	
	@RequestMapping(value="/bbs/download")
	public void download(@RequestParam int num,
			HttpServletRequest req,
			HttpServletResponse resp,
			HttpSession session) throws Exception {
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		if(info == null) {
			String cp = req.getContextPath();
			resp.sendRedirect(cp + "/member/login");
			return;
		}
		
		String root = session.getServletContext().getRealPath("/");
		String pathname = root + "uploads" + File.separator + "bbs";
		
		Board dto = boardService.readBoard(num);
		if(dto != null) {
			boolean b = fileManager.doFileDownload(dto.getSaveFilename(), dto.getOriginalFilename(), pathname, resp);
			if(b) {
				return;
			}
		}
		
		// 파일 다운로드 실패 시 이전 페이지로 이동하는 script
		resp.setContentType("text/html;charset='UTF-8'");
		PrintWriter out = resp.getWriter();
		out.print("<script>alert('파일 다운로드를 실패했습니다.');history.back();</script>");
	}
}
