package com.sp.member;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;


public class LoginCheckInterceptor extends HandlerInterceptorAdapter {
	private final Logger logger = LoggerFactory.getLogger(LoginCheckInterceptor.class);
	
	// AJAX 확인용 Method
	private boolean isAjaxRequest(HttpServletRequest req) {
		// AJAX인 경우 Header에 AJAX = true라는 값을 전송해서 확인 할 수 있다.
		String header = req.getHeader("AJAX");
		
		return header != null && header.equals("true");
	}
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		// Client 요청이 Controller에 도착하기 전에 호출
		// false return하면 HandlerInterceptorAdapter 또는 컨트롤러를 실행하지 않고 요청 종료
		boolean b = true;
		
		try {
			HttpSession session = request.getSession();
			SessionInfo info = (SessionInfo)session.getAttribute("member");
			String cp = request.getContextPath();
			String uri = request.getRequestURI();
			String queryString = request.getQueryString();
			
			if(info == null) {
				b = false;
				
				if(isAjaxRequest(request)) {
					response.sendError(403); // AJAX인 경우 상태코드 403을 넘김
				} else {
					if(uri.indexOf(cp) == 0) {
						uri = uri.substring(request.getContextPath().length());
					}
					if(queryString != null) {
						uri += "?" + queryString;
					}
					
					//System.out.println(uri);
					
					session.setAttribute("preLoginURI", uri);
					response.sendRedirect(cp + "/member/login");
				}
			}
		} catch (Exception e) {
			logger.error("pre :" + e.toString());
		}
		
		return b;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// Controller의 요청을 처리한 후에 호출
		// Controller 실행 중 예외가 발생하면 실행하지 않음
		
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// Client 요청을 처리한 후, 즉 뷰를 통해 클라이너트에 응답을 전송한 뒤에 실행
		// Controller 처리 중 또는 뷰를 생성하는 과정에서 예외가 발생해도 실행
	}
	
}
