package study.datajpa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;

import javax.annotation.PostConstruct;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    @GetMapping("/members/{id}")
    public String findmember(@PathVariable("id") long id) {
        Member member = memberRepository.findById(id).get();
        return member.getUsername();
    }

    /**
     * 도메인 컨버터 사용
     * HTTP 요청은 회원 id를 받지만 도메인 클래스 컨버터가 중간에 동작해서 회원 엔티티 객체를 반환한다.
     * 도메인 클래스 컨버터도 레파지토리를 사용해서 엔티티를 찾는다.
     * 트랜잭션이 없는 상태에서 엔티티를 조회하므로 아래와 같이
     * 도메인 클래스 컨버터로 엔티티를 파라미터로 받을 때는 단순 조회용으로만 사용해야 한다.
     */
    @GetMapping("/members2/{id}")
    public String findmember(@PathVariable("id") Member member) {
        return member.getUsername();
    }

    @GetMapping("/members")
    public Page<MemberDto> list(@PageableDefault(size = 5) Pageable pageable) {
        Page<Member> page = memberRepository.findAll(pageable);
        // Page<MemberDto> map = page.map(m -> new MemberDto(m.getId(), m.getUsername(), null));
        // Page<MemberDto> map = page.map(m -> new MemberDto(m));
        Page<MemberDto> map = page.map(MemberDto::new);
        return map;
    }

    // @PostConstruct : 어플레케이션 실행 시 함께 실행된다.
    @PostConstruct
    public void init() {
        memberRepository.save(new Member("memberA", 10));
    }

    // @PostConstruct
    public void initForPaging() {
        for (int i = 0; i < 100; i++) {
            memberRepository.save(new Member("user" + i, i));
        }
    }
}
