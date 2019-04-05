package com.sp.bbs;

import java.io.File;
import java.io.PrintWriter;
import java.net.URLDecoder;
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

@Controller("bbs.aBoardController")
public class ABoardController {
	@Autowired
	private BoardService boardService;
	@Autowired
	private MyUtil myUtil;
	@Autowired
	private FileManager fileManager;
	
	@RequestMapping(value="/abbs")
	public String main() {
		return ".abbs.main";
	}
	
	/**
	 * 페이지 리스트 -AJAX : TEXT
	 * @param current_page
	 * @param condition
	 * @param keyword
	 * @param req
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/abbs/list")
	public String list(
			@RequestParam(value="pageNo", defaultValue="1") int current_page,
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
		
		String paging = myUtil.pagingMethod(current_page, total_page, "listPage");
		
		model.addAttribute("list", list);
		model.addAttribute("dataCount", dataCount);
		model.addAttribute("page", current_page);
		model.addAttribute("total_page", total_page);
		model.addAttribute("paging", paging);
		model.addAttribute("condition", condition);
		model.addAttribute("keyword", keyword);
		
		return "abbs/list";
	}
	
	/**
	 * 글쓰기 폼 - AJAX : TEXT
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/abbs/created", method=RequestMethod.GET)
	public String cretaedForm(
			Model model) throws Exception {
		
		model.addAttribute("mode", "created");
		return "abbs/created";
	}
	
	@RequestMapping(value="/abbs/created", method=RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> cretaedSubmit(
			HttpSession session,
			Board dto
			) throws Exception {
		SessionInfo info=(SessionInfo)session.getAttribute("member");
		
		String root = session.getServletContext().getRealPath("/");
		String pathname = root+"uploads"+File.separator+"abbs";
		
		dto.setUserId(info.getUserId());
		
		String state = "true";
		
		int result = boardService.insertBoard(dto, pathname);
		
		if(result == 0) {
			state = "false";
		}
		
		Map<String, Object> model = new HashMap<>();
		model.put("state", state);
		
		return model;
	}
	
	@RequestMapping(value="/abbs/article")
	public String article(
			@RequestParam int num,
			@RequestParam int pageNo,
			@RequestParam(defaultValue="all") String condition,
			@RequestParam(defaultValue="") String keyword,
			HttpSession session,
			HttpServletRequest req,
			Model model
			) throws Exception {
		
		// 글보기 - AJAX : TEXT
		String root = session.getServletContext().getRealPath("/");
		String pathname=root+"uploads"+File.separator+"abbs";
		
		boardService.updateHitCount(num);
		
		Board dto = boardService.readBoard(num);
		if(dto==null) {
			return list(pageNo, condition, keyword, req, model);
		}
		
		if(dto.getSaveFilename()!=null) {
			File f=new File(pathname+File.separator+dto.getSaveFilename());
			if(f.exists())
				dto.setFilesize(f.length());
		}
		
		dto.setContent(myUtil.htmlSymbols(dto.getContent()));
		
		Map<String, Object> map = new HashMap<>();
		keyword = URLDecoder.decode(keyword, "UTF-8");
		
		map.put("condition", condition);
		map.put("keyword", keyword);
		map.put("num", num);
		Board preReadDto = boardService.preReadBoard(map);
		Board nextReadDto = boardService.nextReadBoard(map);
		
		model.addAttribute("dto", dto);
		model.addAttribute("preReadDto", preReadDto);
		model.addAttribute("nextReadDto", nextReadDto);
		
		return "abbs/article";
	}
	
	@RequestMapping(value="/abbs/update", method=RequestMethod.GET)
	public String updateForm(
			@RequestParam int num,
			@RequestParam int pageNo,
			@RequestParam(defaultValue="all") String condition,
			@RequestParam(defaultValue="") String keyword,
			HttpServletRequest req,
			HttpSession session,
			Model model) throws Exception {
		// 글 수정 폼 - AJAX : TEXT
		SessionInfo info=(SessionInfo)session.getAttribute("member");
		
		Board dto = boardService.readBoard(num);
		if(dto==null) {
			return list(pageNo, condition, keyword, req, model);
		}

		if(! info.getUserId().equals(dto.getUserId())) {
			return list(pageNo, condition, keyword, req, model);
		}
		
		model.addAttribute("dto", dto);
		model.addAttribute("mode", "update");
		
		return "abbs/created";
	}
	
	@RequestMapping(value="/abbs/update", method=RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> updateSubmit(
			Board dto,
			HttpSession session) throws Exception {
		
		String root=session.getServletContext().getRealPath("/");
		String pathname=root+"uploads"+File.separator+"abbs";		
		// 수정 하기
		String state = "true";
		
		int result = boardService.updateBoard(dto, pathname);
		
		if(result == 0) {
			state = "false";
		}
		
		Map<String, Object> model = new HashMap<>();
		model.put("state", state);
		
		return model;
	}
	
	@RequestMapping(value="/abbs/download")
	public void download(
			@RequestParam int num,
			HttpServletRequest req,
			HttpServletResponse resp,
			HttpSession session
			) throws Exception {
		
		String root=session.getServletContext().getRealPath("/");
		String pathname=root+"uploads"+File.separator+"abbs";
		
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
	
	@RequestMapping(value="/abbs/deleteFile", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> deleteFile(
			@RequestParam int num,
			HttpSession session
			) throws Exception {
		SessionInfo info=(SessionInfo)session.getAttribute("member");
		
		String root=session.getServletContext().getRealPath("/");
		String pathname=root+"uploads"+File.separator+"abbs";
		
		String state = "true";
		Map<String, Object> model = new HashMap<>();
		
		Board dto=boardService.readBoard(num);
		if(dto==null) {
			state = "false";
			model.put("state", state);
			return model;
		}
		
		if(! info.getUserId().equals(dto.getUserId())) {
			state = "false";
			model.put("state", state);
			return model;
		}
		
		if(dto.getSaveFilename()!=null) {
			fileManager.doFileDelete(dto.getSaveFilename(), pathname); // 실제파일삭제
			dto.setSaveFilename("");
			dto.setOriginalFilename("");
			boardService.updateBoard(dto, pathname); // DB 테이블의 파일명 변경(삭제)
		}
		
		model.put("state", state);
		return model;
	}
	
	@RequestMapping(value="/abbs/delete", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> delete(
			@RequestParam int num,
			HttpSession session) throws Exception {
		SessionInfo info=(SessionInfo)session.getAttribute("member");
		
		String root=session.getServletContext().getRealPath("/");
		String pathname=root+"uploads"+File.separator+"abbs";
		
		int result = boardService.deleteBoard(num, pathname, info.getUserId());
		
		String state = "true";
		if(result == 0) {
			state = "false";
		}
		
		Map<String, Object> model = new HashMap<>();
		model.put("state", state);

		return model;
	}
}
