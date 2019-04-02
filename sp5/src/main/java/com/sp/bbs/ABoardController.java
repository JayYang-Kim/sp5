package com.sp.bbs;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller("bbs.aBoardController")
public class ABoardController {
	@RequestMapping(value="/abbs")
	public String main() {
		return ".abbs.main";
	}
}
