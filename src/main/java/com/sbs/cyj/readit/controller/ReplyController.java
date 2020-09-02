package com.sbs.cyj.readit.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sbs.cyj.readit.dto.Member;
import com.sbs.cyj.readit.dto.Reply;
import com.sbs.cyj.readit.dto.ResultData;
import com.sbs.cyj.readit.service.ReplyService;

@Controller
public class ReplyController {
	@Autowired
	private ReplyService replyService;
	
	// 댓글 작성
	@RequestMapping("/usr/reply/doWriteReplyAjax")
	@ResponseBody
	public String doWriteReplyAjax(@RequestParam Map<String, Object> param, @RequestParam int articleId, String body, Model model, HttpServletRequest req) {
		int memberId = (int) req.getAttribute("loginedMemberId");
		param.put("memberId", memberId);
		
		replyService.write(param);
		String msg = "댓글이 생성되었습니다.";

		String redirectUri = (String) param.get("redirectUri");
		System.out.println("redirectUri : " + redirectUri);
//		redirectUri = redirectUri.replace("#id", id + "");
		
		model.addAttribute("msg", msg);
		model.addAttribute("redirectUri", redirectUri);
		
		return "article/modify";
	}
	
	// 댓글 리스트
	@RequestMapping("/usr/reply/getForPrintReplies")
	@ResponseBody
	public ResultData getForPrintArticleReplies(@RequestParam Map<String, Object> param, HttpServletRequest req) {
		Map<String, Object> rsDataBody = new HashMap<>();

		Member actor = (Member) req.getAttribute("loginedMember");
		param.put("actor", actor);
		
		List<Reply> replies = replyService.getForPrintReplies(param);
		rsDataBody.put("replies", replies);

		return new ResultData("S-1", String.format("%d개의 댓글을 불러왔습니다.", replies.size()), rsDataBody);
	}
	
	// 댓글 삭제
	@RequestMapping("/usr/reply/doDeleteReplyAjax")
	@ResponseBody
	public ResultData doDeleteReplyAjax(int id, HttpServletRequest req) {
		Member loginedMember = (Member) req.getAttribute("loginedMember");
		Reply ar = replyService.getReplyById(id);
		
		if(replyService.actorCanDelete(loginedMember, ar) == false) {
			return new ResultData("F-1", String.format("%d번 댓글을 삭제할 권한이 없습니다.", id));
		}
		replyService.deleteReplyById(id);
		
		return new ResultData("S-1", String.format("%d번 댓글을 삭제하였습니다.", id));
	}
}