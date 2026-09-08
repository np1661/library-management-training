package com.training.librarymanagementtraining.controller;

import com.training.librarymanagementtraining.entity.Member;
import com.training.librarymanagementtraining.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    // POST /api/members
    @PostMapping
    public Member createMember(@RequestBody Member member) {
        return memberService.createMember(member);
    }

    // GET /api/members
    @GetMapping
    public List<Member> getAllMembers() {
        return memberService.getAllMembers();
    }

    // GET /api/members/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Member> getMemberById(@PathVariable Long id) {
        return memberService.getMemberById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // PUT /api/members/{id}
    @PutMapping("/{id}")
    public Member updateMember(@PathVariable Long id,
                               @RequestBody Member member) {
        return memberService.updateMember(id, member);
    }

    // DELETE /api/members/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long id) {
        memberService.deleteMember(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/members/search?name=name
    @GetMapping("/search")
    public List<Member> searchMembers(@RequestParam String name) {
        return memberService.searchMembers(name);
    }
}