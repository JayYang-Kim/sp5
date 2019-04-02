package com.sp.bbs;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sp.common.FileManager;
import com.sp.common.dao.CommonDAO;

@Service("bbs.boardService")
public class BoardServiceImpl implements BoardService{
	@Autowired
	private CommonDAO  dao;
	
	@Autowired
	private FileManager fileManager;
	
	@Override
	public int insertBoard(Board dto, String pathname) {
		int result=0;
		try{
			
			String saveFilename=fileManager.doFileUpload(dto.getUpload(), pathname);
			if(saveFilename!=null) {
				dto.setSaveFilename(saveFilename);
				dto.setOriginalFilename(dto.getUpload().getOriginalFilename());
			}
			
			result=dao.insertData("bbs.insertBoard", dto);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public List<Board> listBoard(Map<String, Object> map) {
		List<Board> list=null;
		
		try{
			list=dao.selectList("bbs.listBoard", map);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}

	@Override
	public int dataCount(Map<String, Object> map) {
		int result=0;
		
		try{
			result=dao.selectOne("bbs.dataCount", map);			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}

	@Override
	public Board readBoard(int num) {
		Board dto=null;
		
		try{
			// 게시물 가져오기
			dto=dao.selectOne("bbs.readBoard", num);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return dto;
	}

	@Override
	public int updateHitCount(int num) {
		int result=0;
		
		try{
			// 조회수 증가
			result=dao.updateData("bbs.updateHitCount", num);
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	@Override
	public Board preReadBoard(Map<String, Object> map) {
		Board dto=null;
		
		try{
			dto=dao.selectOne("bbs.preReadBoard", map);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return dto;

	}

	@Override
	public Board nextReadBoard(Map<String, Object> map) {
		Board dto=null;
		
		try{
			dto=dao.selectOne("bbs.nextReadBoard", map);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return dto;
	}
	
	@Override
	public int updateBoard(Board dto, String pathname) {
		int result=0;

		try{
			String saveFilename=fileManager.doFileUpload(dto.getUpload(), pathname);
			if(saveFilename != null) {
				if(dto.getSaveFilename()!=null && dto.getSaveFilename().length()!=0)
					fileManager.doFileDelete(dto.getSaveFilename(), pathname);
				
				dto.setSaveFilename(saveFilename);
				dto.setOriginalFilename(dto.getUpload().getOriginalFilename());
			}
			
			dao.updateData("bbs.updateBoard", dto);
			result=1;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public int deleteBoard(int num, String pathname, String userId) {
		int result=0;

		try{
			Board dto=readBoard(num);
			if(dto==null || (! userId.equals("admin") && ! userId.equals(dto.getUserId())))
				return result;
			
			fileManager.doFileDelete(dto.getSaveFilename(), pathname);
			
			dao.deleteData("bbs.deleteBoard", num);
			result=1;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public int insertReply(Reply dto) {
		int result=0;
		try {
			result=dao.insertData("bbs.insertReply", dto);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public List<Reply> listReply(Map<String, Object> map) {
		List<Reply> list=null;
		try {
			list=dao.selectList("bbs.listReply", map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public int replyCount(Map<String, Object> map) {
		int result=0;
		try {
			result=dao.selectOne("bbs.replyCount", map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public int deleteReply(Map<String, Object> map) {
		int result=0;
		try {
			result=dao.deleteData("bbs.deleteReply", map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public List<Reply> listReplyAnswer(int answer) {
		List<Reply> list=null;
		try {
			list=dao.selectList("bbs.listReplyAnswer", answer);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public int replyAnswerCount(int answer) {
		int result=0;
		try {
			result=dao.selectOne("bbs.replyAnswerCount", answer);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public int insertReplyLike(Map<String, Object> map) {
		int result=0;
		try {
			result=dao.insertData("bbs.insertReplyLike", map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public Map<String, Object> replyLikeCount(Map<String, Object> map) {
		Map<String, Object> countMap=null;
		try {
			countMap=dao.selectOne("bbs.replyLikeCount", map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return countMap;
	}

	@Override
	public int insertBoardLike(Map<String, Object> map) {
		int result=0;
		try {
			result=dao.insertData("bbs.insertBoardLike", map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public int boardLikeCount(int num) {
		int result=0;
		try {
			result=dao.selectOne("bbs.boardLikeCount", num);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
