package com.ssafy.memberservice.controller;

import com.ssafy.memberservice.exception.member.TimeoutException;
import com.ssafy.memberservice.service.MemberService;

import com.ssafy.memberservice.vo.MemberVO;
import com.ssafy.memberservice.vo.MyPageVO;
import com.ssafy.memberservice.vo.dto.response.TokenResponseDto;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Member;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member-service")
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> getKakao(@RequestBody Map<String, String> code) {
        try{
            //ResponseEntity<TokenResponseDto> responseLogin = memberService.joinOrLogin(code.get("code"));
            return memberService.joinOrLogin(code.get("code"));
        } catch(TimeoutException e){
            TokenResponseDto forbidden = new TokenResponseDto("forbidden", "금지된 사용자");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(forbidden);
        }
    }

    @GetMapping("/me/{memberid}")
    public ResponseEntity<MyPageVO> getMypage(@PathVariable Long memberid){
        MyPageVO myPageVO = memberService.getMypage(memberid);
        return ResponseEntity.status(HttpStatus.OK).body(myPageVO);
    }


    @PatchMapping ("/bang/{bang}")
    public ResponseEntity calBang(@PathVariable int bang, HttpServletRequest request){
        JSONObject loginMember = new JSONObject(request.getHeader("member"));
        Long loginMemberId = loginMember.getLong("id");
        memberService.calBang(bang, loginMemberId);
        return ResponseEntity.status(HttpStatus.OK).body("뱅 업데이트 완료");
    }

    //타임아웃 횟수 추가
    @PatchMapping("/time-out")
    public ResponseEntity addTimeout(HttpServletRequest request){
        JSONObject loginMember = new JSONObject(request.getHeader("member"));
        Long loginMemberId = loginMember.getLong("id");

        if(!memberService.isBan(loginMemberId)){
            memberService.addTimeout(loginMemberId);
            return ResponseEntity.status(HttpStatus.OK).body("timeout 증가 완료");
        }else{
            memberService.addTimeout(loginMemberId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("영구 정지");
        }

    }







    @GetMapping("/member-service/{memberid}")
    public ResponseEntity<MemberVO> getOrders(@PathVariable Long memberid){
        MemberVO memberVO = memberService.getMemberVO(memberid);

        return ResponseEntity.status(HttpStatus.OK).body(memberVO);
    }

    //gateway에서 오는 정보 확인
    @GetMapping("/health-check")
    public String checkConnection(HttpServletRequest request){
        String memberStr = request.getHeader("member");

        System.out.println(memberStr);

        return "MemberService Check Completed!";
    }



}
