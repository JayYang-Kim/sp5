package com.sp.nodechat;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller("nodechat.nodeChattingController")
public class NodeChattingController {
	@RequestMapping(value="/nodeChat/main")
	public String main(
			Model model) throws Exception {
		return ".nodeChat.chat";
	}
}
