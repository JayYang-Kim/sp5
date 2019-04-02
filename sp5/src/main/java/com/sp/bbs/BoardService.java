package com.sp.bbs;

import java.util.List;
import java.util.Map;

public interface BoardService {
	public int insertBoard(Board dto, String pathname);
	public List<Board> listBoard(Map<String, Object> map);
	public int dataCount(Map<String, Object> map);
	public Board readBoard(int num);
	public int updateHitCount(int num);
	public Board preReadBoard(Map<String, Object> map);
	public Board nextReadBoard(Map<String, Object> map);
	public int updateBoard(Board dto, String pathname);
	public int deleteBoard(int num, String pathname, String userId);
	
	public int insertBoardLike(Map<String, Object> map);
	public int boardLikeCount(int num);
	
	public int insertReply(Reply dto);
	public List<Reply> listReply(Map<String, Object> map);
	public int replyCount(Map<String, Object> map);
	public int deleteReply(Map<String, Object> map);
	
	public List<Reply> listReplyAnswer(int answer);
	public int replyAnswerCount(int answer);
	
	public int insertReplyLike(Map<String, Object> map);
	public Map<String, Object> replyLikeCount(Map<String, Object> map);
	
}


