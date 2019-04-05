package com.sp.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller("mail.mailController")
public class MailController {
	@Autowired
	private MailSender mailSender;
	
	@RequestMapping(value="/mail/send", method=RequestMethod.GET)
	public String sendForm() throws Exception {
		return ".mail.send";
	}

	@RequestMapping(value="/mail/send", method=RequestMethod.POST)
	public String sendSubmit(Mail dto, Model model) throws Exception {

		boolean b=mailSender.mailSend(dto);
		
		String msg="<span style='color:blue;'>"+dto.getReceiverEmail()+"</span> 님에게<br>";
		if(b) {
			msg+="메일을 성공적으로 전송 했습니다.";
		} else {
			msg+="메일을 전송하는데 실패했습니다.";
		}
		
		model.addAttribute("message", msg);
		return ".mail.complete";
	}
}
