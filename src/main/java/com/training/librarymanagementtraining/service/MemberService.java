package com.training.librarymanagementtraining.service;

import com.training.librarymanagementtraining.entity.Member;
import com.training.librarymanagementtraining.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    // Create Member
    public Member createMember(Member member) {
        return memberRepository.save(member);
    }

    // Get All Members
    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    // Get Member By ID
    public Optional<Member> getMemberById(Long id) {
        return memberRepository.findById(id);
    }

    // Update Member
    public Member updateMember(Long id, Member memberDetails) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        member.setName(memberDetails.getName());
        member.setEmail(memberDetails.getEmail());
        member.setPhoneNumber(memberDetails.getPhoneNumber());

        return memberRepository.save(member);
    }

    // Delete Member
    public void deleteMember(Long id) {
        memberRepository.deleteById(id);
    }

    // Search Member By Name
    public List<Member> searchMembers(String name) {
        return memberRepository.findByNameContainingIgnoreCase(name);
    }
}