package com.sp.member;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sp.common.dao.CommonDAO;

@Service("member.memberService")
public class MemberServiceImpl implements MemberService {
	@Autowired
	private CommonDAO  dao;

	@Override
	public Member loginMember(String userId) {
		Member dto=null;
		
		try {
			dto=dao.selectOne("member.loginMember", userId);
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		
		return dto;
	}
	
	@Override
	public int insertMember(Member dto) {
		int result=0;
		
		try {
			if(dto.getEmail1() != null && dto.getEmail1().length()!=0 &&
					dto.getEmail2() != null && dto.getEmail2().length()!=0)
				dto.setEmail(dto.getEmail1() + "@" + dto.getEmail2());
			
			if(dto.getTel1() != null && dto.getTel1().length()!=0 &&
					dto.getTel2() != null && dto.getTel2().length()!=0 &&
							dto.getTel3() != null && dto.getTel3().length()!=0)
				dto.setTel(dto.getTel1() + "-" + dto.getTel2() + "-" + dto.getTel3());
			
			long memberSeq = dao.selectOne("member.memberSeq");
			dto.setMemberIdx(memberSeq);
			
			// 회원정보 저장
			dao.insertData("member.insertMember", memberSeq);
			
			// dao.insertData("member.insertMember1", dto);
			// dao.insertData("member.insertMember2", dto);
			dao.updateData("member.insertMember12", dto); // member1, member2 테이블 동시에 
			
			result=1;
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		
		return result;
	}
	
	@Override
	public Member readMember(String userId) {
		Member dto=null;
		
		try {
			dto=dao.selectOne("member.readMember", userId);
			
			if(dto!=null) {
				if(dto.getEmail()!=null) {
					String [] s=dto.getEmail().split("@");
					dto.setEmail1(s[0]);
					dto.setEmail2(s[1]);
				}

				if(dto.getTel()!=null) {
					String [] s=dto.getTel().split("-");
					dto.setTel1(s[0]);
					dto.setTel2(s[1]);
					dto.setTel3(s[1]);
				}
			}
			
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		
		return dto;
	}
	
	@Override
	public Member readMember(long memberIdx) {
		Member dto=null;
		
		try {
			dto=dao.selectOne("member.readMember2", memberIdx);
			
			if(dto!=null) {
				if(dto.getEmail()!=null) {
					String [] s=dto.getEmail().split("@");
					dto.setEmail1(s[0]);
					dto.setEmail2(s[1]);
				}

				if(dto.getTel()!=null) {
					String [] s=dto.getTel().split("-");
					dto.setTel1(s[0]);
					dto.setTel2(s[1]);
					dto.setTel3(s[1]);
				}
			}
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		
		return dto;
	}
	
	@Override
	public int updateMembership(Map<String, Object> map) {
		int result=0;
		
		try {
			result=dao.updateData("member.updateMembershep", map);
		} catch (Exception e) {
			System.out.println(e.toString());
		}

		return result;
	}
	
	@Override
	public int updateLastLogin(String userId) {
		int result=0;
		
		try {
			result=dao.updateData("member.updateLastLogin", userId);
		} catch (Exception e) {
			System.out.println(e.toString());
		}

		return result;
	}
	
	@Override
	public int updateMember(Member dto) {
		int result=0;
		try {
			if(dto.getEmail1() != null && dto.getEmail1().length()!=0 &&
					dto.getEmail2() != null && dto.getEmail2().length()!=0)
				dto.setEmail(dto.getEmail1() + "@" + dto.getEmail2());
			
			if(dto.getTel1() != null && dto.getTel1().length()!=0 &&
					dto.getTel2() != null && dto.getTel2().length()!=0 &&
							dto.getTel3() != null && dto.getTel3().length()!=0)
				dto.setTel(dto.getTel1() + "-" + dto.getTel2() + "-" + dto.getTel3());
			
			dao.updateData("member.updateMember1", dto);
			dao.updateData("member.updateMember2", dto);
			
			result=1;
			
		} catch (Exception e) {
		}
		return result;
	}

	@Override
	public int deleteMember(Map<String, Object> map) {
		int result=0;
		try {
			map.put("membershep", 0);
			updateMembership(map);
			
			dao.deleteData("member.deleteMember2", map);
			dao.deleteData("member.deleteMember1", map);
			
			result=1;
		} catch (Exception e) {
		}
		return result;
	}

	@Override
	public int dataCount(Map<String, Object> map) {
		int result=0;

		return result;
	}

	@Override
	public List<Member> listMember(Map<String, Object> map) {
		List<Member> list=null;

		return list;
	}

}
