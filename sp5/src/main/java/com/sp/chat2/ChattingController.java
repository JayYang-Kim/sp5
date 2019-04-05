package com.sp.chat2;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller("chat2.chattingController")
public class ChattingController {
	@RequestMapping(value="/chat2/main")
	public String main() {
		return ".chat2.chat";
	}
}
