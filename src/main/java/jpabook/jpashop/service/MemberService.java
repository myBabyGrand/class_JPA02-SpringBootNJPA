package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true) //클래스 단위로 읽기모드
@RequiredArgsConstructor//생성자 injection
public class MemberService {
    @Autowired
    private final MemberRepository memberRepository;

//    //생성자 Injection
//    @Autowired
//    public MemberService(MemberRepository memberRepository) {
//        this.memberRepository = memberRepository;
//    }

    //회원 가입
    @Transactional //쓰기가 있는 부분은 덮어주자. default : readOnly = false
    public Long join(Member member){

        validateDuplicateMember(member);
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        if(!memberRepository.findByName(member.getName()).isEmpty()){
            throw new IllegalStateException("이미 등록된 이름입니다."+member.getName());
        }
    }
    //회원 전체조회
    public List<Member> findMembers(){
        return memberRepository.findAll();
    }
    public Member findOne(Long memberId){
        return memberRepository.findOne(memberId);
    }
}