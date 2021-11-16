package study.datajpa.repository;

import org.assertj.core.api.Assert;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

// Jnit5부터는 RunWith(SpringRunner.class)를 적어주지 않아도 된다.
@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {
    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;
    @PersistenceContext
    EntityManager em;

    @Autowired
    MemberQueryRepository memberQueryRepository;

    @Test
    public void testMember(){
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(member.getId()).get();

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);

    }

    @Test
    public void findByUesrnameAndAgeGreaterThen() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("AAA", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        Assertions.assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        Assertions.assertThat(result.get(0).getAge()).isEqualTo(20);
        Assertions.assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void findHelloyBy() {
        List<Member> helloBy = memberRepository.findTop3HelloBy();
    }

    @Test
    public void testNamedQuery() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsername("AAA");
        Member findMember = result.get(0);

        Assertions.assertThat(findMember).isEqualTo(m1);
    }

    @Test
    public void testQuery() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 10);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> findUser = memberRepository.findUser("AAA", 10);
        assertThat(findUser.get(0)).isEqualTo(m1);
    }

    @Test
    public void findNameList() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> nameList = memberRepository.findUsernameList();

        nameList.stream().forEach(System.out::println);
    }

    @Test
    public void findDtoList() {
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member m1 = new Member("AAA", 10);
        m1.setTeam(team);
        memberRepository.save(m1);

        List<MemberDto> memberDto = memberRepository.findMemberDto();
        memberDto.stream().forEach(System.out::println);
    }

    @Test
    public void findByNames(){
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> usernameList = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));
        usernameList.stream().forEach(System.out::println);
    }

    @Test
    public void returnType() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> a1 = memberRepository.findListByUsername("AAA");
        Member a2 = memberRepository.findMemberByUsername("AAA"); // AAA가 두 건이상일 경우 에러
        Optional<Member> a3 = memberRepository.findOptionalByUsername("AAA"); // AAA가 두 건이상일 경우 에러

        System.out.println("a1:"+a1);
        System.out.println("a2:"+a2);
        System.out.println("a3:"+a3);
    }

    @Test
    public void paging() {
        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        /**
         * 첫 번째 파라미터 : 현재 페이지
         * 두 번째 파라미터 : 조회할 데이터 수
         * 페이지는 1이 아닌 0부터 시작한다는 점 주의
         */
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        // when
        Page<Member> page = memberRepository.findByAge(age, pageRequest);
        Page<MemberDto> toMap = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));

        // then
        List<Member> content = page.getContent();
        long totalElements = page.getTotalElements();

        /*
        for (Member member : content) {
            System.out.println("member = " + member);
        }
        System.out.println("totalElements = " + totalElements); */

        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();
    }

    @Test
    public void bulkUpdate() {
        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 20));
        memberRepository.save(new Member("member3", 30));
        memberRepository.save(new Member("member5", 50));
        memberRepository.save(new Member("member6", 60));

        // when
        int resultCnt = memberRepository.bulkAgePlus(20);
        // em.clear();

        /**
         * 벌크 현상은 영속성 컨테이너를 무시하고 DB에 반영하기 때문에
         * clear()를 통해 영속성 컨테이너를 한번 비워주지 않으면 아래 데이터는 51이 아닌 50을 반환한다.
         * 또는 @Modifying에 clearAutomatically = true를 추가한다.
         */
        List<Member> member5 = memberRepository.findByUsername("member5");
        System.out.println("member5 = " + member5);

        assertThat(resultCnt).isEqualTo(4);
    }

    @Test
    public void findMemberLazy() {
        // given
        Team teamA = new Team("TeamA");
        Team teamB = new Team("TeamB");

        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);

        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        // when
        List<Member> members = memberRepository.findAll();

        for (Member member : members) {
            System.out.println("member = " + member.getUsername());
            // 멤버를 기준으로 getTeam()을 할 경우 proxy라는 가짜 객체만 가져온다.(lazy 타입이므로)
            System.out.println("member.teamClass = " + member.getTeam().getClass()); // Team$HibernateProxy$h1cuAAkq
            // getName() 시점에 team을 조회하는 쿼리가 나간다.
            System.out.println("member.team = " + member.getTeam().getName());
        }

        em.clear();
        System.out.println("========================================================================");

        // 때문에 아래처럼 Fetch 조인을 통하여 멤버를 조회하면서 연관된 팀까지 한번에 조회함으로써 N+1문제를 해결한다.
        List<Member> members2 = memberRepository.findMemberFetchJoin();

        for (Member member : members2) {
            System.out.println("member = " + member.getUsername());
            // 실 객체를 가져온다.
            System.out.println("member.teamClass = " + member.getTeam().getClass()); // study.datajpa.entity.Team
            // getName() 시점에 team을 조회하는 쿼리가 나간다.
            System.out.println("member.team = " + member.getTeam().getName());
        }
    }

    @Test
    public void queryHint() {
        Member member = new Member("member1", 10);
        memberRepository.save(member);
        em.flush(); // 영속성 컨텍스트의 변경 내용을 DB에 반영
        em.clear();

        // Member findMember = memberRepository.findById(member.getId()).get();
        Member findMember = memberRepository.findReadOnlyByUsername("member1");
        findMember.setUsername("member2");
        em.flush();
    }

    @Test
    public void lock() {
        // given
        Member member1 = new Member("member1", 10);
        em.flush();
        em.clear();

        List<Member> findMember = memberRepository.findLockByUsername("member1");
        // 쿼리 끝에 for update가 붙는다.
        System.out.println("findMember = " + findMember);
    }

    @Test
    public void customTest() {
        memberRepository.save(new Member("memberA", 10));

        List<Member> member = memberRepository.findMemberCustom();
        System.out.println("member = " + member);
    }

    @Test
    public void repositoryTest() {
        memberRepository.save(new Member("memberA", 10));

        List<Member> member = memberQueryRepository.findAllMembers();
        System.out.println("member = " + member);
    }
}