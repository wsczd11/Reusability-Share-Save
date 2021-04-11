package org.seng302.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.seng302.main.Main;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * SearchUserByName test class - specifically for testing the searching user by name feature of the UserRepository class
 */
@RunWith(SpringRunner.class)
@DataJpaTest
@ContextConfiguration(classes = {Main.class})
@ActiveProfiles("test")
public class SearchUserByNameTests {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User dGAA;
    private User user;
    private User anotherUser;
    private User searchUser1;
    private User searchUser2;
    private User searchUser3;
    private User searchUser4;
    private User searchUser5;
    private User searchUser6;
    private User searchUser7;
    private List<User> searchUsers;

    /**
     * Creates and inserts all users for testing.
     * Ideally this would be BeforeAll.
     * BeforeEach works but will replace all users before each test. Only functional difference when testing is that they will have new IDs.
     * @throws Exception Any exception.
     */
    @BeforeEach
    public void setup() throws Exception {

        dGAA = new User(
                "John",
                "Doe",
                "S",
                "Generic",
                "Biography",
                "email@email.com",
                LocalDate.of(2020, 2, 2),
                "0271316",
                "325 Citlalli Track, New Lois, Heard Island and McDonald Islands, HM, Antarctica",
                "password",
                LocalDateTime.of(LocalDate.of(2021, 2, 2),
                        LocalTime.of(0, 0)),
                Role.DEFAULTGLOBALAPPLICATIONADMIN);

        user = new User("testfirst",
                "testlast",
                "testmiddle",
                "testnick",
                "testbiography",
                "testemail@email.com",
                LocalDate.of(2020, 2, 2),
                "0271316",
                "57 Sydney Highway, Shire of Cocos Islands, West Island, Cocos (Keeling) Islands",
                "testpassword",
                LocalDateTime.of(LocalDate.of(2021, 2, 2),
                        LocalTime.of(0, 0)),
                Role.GLOBALAPPLICATIONADMIN);

        anotherUser = new User ("first",
                "last",
                "middle",
                "nick",
                "bio",
                "example@example.com",
                LocalDate.of(2021, 1, 1),
                "123456789",
                "47993 Norwood Garden, Mambere-Kadei Central African Republic, Africa",
                "password",
                LocalDateTime.of(LocalDate.of(2021, 1, 1),
                        LocalTime.of(0, 0)),
                Role.USER);

        searchUser1= new User(
                "Alex",
                "Doe",
                "S",
                "Generic",
                "Biography",
                "test@email.com",
                LocalDate.of(2020, 2, 2),
                "0271316",
                "129 Mastic Trail, Frank Sound, Cayman Islands, Caribbean, North America",
                "password",
                LocalDateTime.of(LocalDate.of(2021, 2, 2),
                        LocalTime.of(0, 0)),
                Role.USER);

        searchUser2 = new User(
                "Chad",
                "Taylor",
                "S",
                "Cha",
                "Biography123",
                "chad.taylor@example.com",
                LocalDate.of(2008, 2, 2),
                "0271316678",
                "80416 Jon Loop, Shaanxi, China",
                "password",
                LocalDateTime.of(LocalDate.of(2021, 2, 2),
                        LocalTime.of(0, 0)),
                Role.USER);

        searchUser3 = new User(
                "Naomi",
                "Wilson",
                "I",
                "Gm",
                "Biography",
                "naomi.wilson@example.com",
                LocalDate.of(2000, 2, 2),
                "0271316",
                "9205 Monique Vista, Bururi, Bigomogomo, Africa",
                "password",
                LocalDateTime.of(LocalDate.of(2021, 2, 2),
                        LocalTime.of(0, 0)),
                Role.USER);

        searchUser4 = new User(
                "Seth",
                "Murphy",
                "Tea",
                "S",
                "Biography",
                "seth.murphy@example.com",
                LocalDate.of(2008, 2, 2),
                "027188316",
                "240 Bernhard Run, Southland, New Zealand",
                "password",
                LocalDateTime.of(LocalDate.of(2021, 2, 2),
                        LocalTime.of(0, 0)),
                Role.USER);

        searchUser5 = new User(
                "Minttu",
                "Wainio",
                "A",
                "Min",
                "Biography",
                "minttu.wainio@example.com",
                LocalDate.of(2020, 2, 2),
                "0271316",
                "186 Simpsons Road, Ashburton, Canterbury, New Zealand",
                "password",
                LocalDateTime.of(LocalDate.of(2021, 2, 2),
                        LocalTime.of(0, 0)),
                Role.USER);

        searchUser6 = new User(
                "Francisca",
                "Benitez",
                "T",
                "Fran",
                "Biography",
                "francisca.benitez@example.com",
                LocalDate.of(2020, 2, 2),
                "0271316",
                "14798 Terry Highway, Queenstown-Lakes District, New Zealand",
                "password",
                LocalDateTime.of(LocalDate.of(2021, 2, 2),
                        LocalTime.of(0, 0)),
                Role.USER);

        searchUser7 = new User(
                "Francisca",
                "Bznitez",
                "T",
                "Fran",
                "Biography",
                "francisca.benitez@example.com",
                LocalDate.of(2020, 2, 2),
                "0271316",
                "3396 Bertram Parkway, Central Otago, New Zealand",
                "password",
                LocalDateTime.of(LocalDate.of(2021, 2, 2),
                        LocalTime.of(0, 0)),
                Role.USER);

        searchUsers = List.of(dGAA, user, anotherUser, searchUser1, searchUser2, searchUser3, searchUser4,
                searchUser5, searchUser6, searchUser7);

        for (User searchUser: searchUsers) {
            entityManager.persist(searchUser);

        }
        entityManager.flush();

    }

    /**
     * Tests that the search functionality will order users by nickname in ascending order i.e. in alphabetical order.
     */
    @Test
    public void whenFindAllUsersByNames_thenReturnNicknameOrderedUsersAscending() throws Exception {
        // given
        int pageNo = 0;
        int pageSize = 11;
        Sort sortBy = Sort.by(Sort.Order.asc("nickname").ignoreCase());
        Pageable pageable = PageRequest.of(pageNo, pageSize, sortBy);
        ArrayList<String> orderedNicknames = new ArrayList<>();
        orderedNicknames.add("Cha");
        orderedNicknames.add("Fran");
        orderedNicknames.add("Fran");
        orderedNicknames.add("Generic");
        orderedNicknames.add("Generic");
        orderedNicknames.add("Gm");
        orderedNicknames.add("Min");
        orderedNicknames.add("nick");
        orderedNicknames.add("S");
        orderedNicknames.add("testnick");

        // when
        Page<User> userPage = userRepository.findAllUsersByNames("", pageable);

        // then
        for (int i = 0; i < userPage.getContent().size(); i++) {
            assertThat(userPage.getContent().get(i).getNickname()).isEqualTo(orderedNicknames.get(i));
        }

    }

    /**
     * Tests that the search functionality will order users by nickname in descending order i.e. in reverse alphabetical order.
     */
    @Test
    public void whenFindAllUsersByNames_thenReturnNicknameOrderedUsersDescending() throws Exception {
        // given
        int pageNo = 0;
        int pageSize = 11;
        Sort sortBy = Sort.by(Sort.Order.desc("nickname").ignoreCase());
        Pageable pageable = PageRequest.of(pageNo, pageSize, sortBy);
        ArrayList<String> orderedNicknames = new ArrayList<>();

        orderedNicknames.add("testnick");
        orderedNicknames.add("S");
        orderedNicknames.add("nick");
        orderedNicknames.add("Min");
        orderedNicknames.add("Gm");
        orderedNicknames.add("Generic");
        orderedNicknames.add("Generic");
        orderedNicknames.add("Fran");
        orderedNicknames.add("Fran");
        orderedNicknames.add("Cha");

        // when
        Page<User> userPage = userRepository.findAllUsersByNames("", pageable);
//        assertThat(userPage.getContent()).isEqualTo(0);

        // then
        for (int i = 0; i < userPage.getContent().size(); i++) {
            assertThat(userPage.getContent().get(i).getNickname()).isEqualTo(orderedNicknames.get(i));
        }

    }

    /*

    Full name ascending/descending
    email ascending/descending
    address ascending/descending
    Ordering is consistent with duplicate values (secondary order by needed)

    Filter by firstname
    Filter by middlename
    Filter by lastname
    Filter by firstname and middlename
    Filter by firstname and lastname
    Filter by middlename and lastname
    Filter by all three
    Filter by nickname
    Filter by empty string gives all users
    Filter by non-existent input

    Pagination test half full page |
    Pagination test that we receive page 2 or later |
    Pagination test empty page |
    Pagination test full page |
    Pagination test ordering works across pages, not just within a page |

     */

    /**
     * Tests that the search functionality will return paginated results correctly when the page is not full with users.
     */
    @Test
    public void whenFindAllUsersByNames_thenReturnPageHalfFull() throws Exception {
        // given
        int pageNo = 0;
        // Page size 20 means page will be half full with the default 10 users inserted
        int pageSize = 20;
        Pageable pageable = PageRequest.of(pageNo, pageSize);

        // when
        Page<User> userPage = userRepository.findAllUsersByNames("", pageable);

        // then
        assertThat(userPage.getTotalElements()).isEqualTo(10);
        for (int i = 0; i < searchUsers.size(); i++) {
            assertThat(userPage.getContent().get(i)).isEqualTo(searchUsers.get(i));
        }

    }

    /**
     * Tests that the search functionality will return pages other than the first one with correct users.
     */
    @Test
    public void whenFindAllUsersByNames_thenReturnPagesFromTwoOnward() throws Exception {
        // given
        int pageSize = 2;

        // when
        Page<User> userPage2 = userRepository.findAllUsersByNames("", PageRequest.of(1, pageSize));
        Page<User> userPage3 = userRepository.findAllUsersByNames("", PageRequest.of(2, pageSize));
        Page<User> userPage4 = userRepository.findAllUsersByNames("", PageRequest.of(3, pageSize));
        Page<User> userPage5 = userRepository.findAllUsersByNames("", PageRequest.of(4, pageSize));

        // then
        assertThat(userPage2.getTotalPages()).isEqualTo(5);
        assertThat(userPage2.getContent().get(0)).isEqualTo(searchUsers.get(2));
        assertThat(userPage2.getContent().get(1)).isEqualTo(searchUsers.get(3));
        assertThat(userPage3.getContent().get(0)).isEqualTo(searchUsers.get(4));
        assertThat(userPage3.getContent().get(1)).isEqualTo(searchUsers.get(5));
        assertThat(userPage4.getContent().get(0)).isEqualTo(searchUsers.get(6));
        assertThat(userPage4.getContent().get(1)).isEqualTo(searchUsers.get(7));
        assertThat(userPage5.getContent().get(0)).isEqualTo(searchUsers.get(8));
        assertThat(userPage5.getContent().get(1)).isEqualTo(searchUsers.get(9));

    }

    /**
     * Tests that the search functionality will return an empty page when given a filter value
     * that does not match anything in the database.
     */
    @Test
    public void whenFindAllUsersByNames_thenReturnEmptyPage() throws Exception {
        // given
        int pageNo = 0;
        int pageSize = 20;
        Pageable pageable = PageRequest.of(pageNo, pageSize);

        // when
        Page<User> userPage = userRepository.findAllUsersByNames("ThisValueDoesNotExist", pageable);

        // then
        assertThat(userPage.getTotalElements()).isEqualTo(0);
        assertThat(userPage.getTotalPages()).isEqualTo(0);

    }

    /**
     * Tests that the search functionality will return the page correctly when the page is full.
     */
    @Test
    public void whenFindAllUsersByNames_thenReturnFullPage() throws Exception {
        // given
        int pageNo = 0;
        // Page size 8 means tested page will be full as there are 10 total values
        int pageSize = 8;
        Pageable pageable = PageRequest.of(pageNo, pageSize);

        // when
        Page<User> userPage = userRepository.findAllUsersByNames("", pageable);

        // then
        assertThat(userPage.getTotalPages()).isEqualTo(2);
        assertThat(userPage.getSize()).isEqualTo(8);
        for (int i = 0; i < userPage.getSize(); i++) {
            assertThat(userPage.getContent().get(i)).isEqualTo(searchUsers.get(i));
        }


    }

    /**
     * Tests that the search functionality ordering works across pages, not just within a single page.
     *  I.e. That data is ordered 'globally' from all results in the database,
     *      not just the few values that are returned are correctly ordered.
     */
    @Test
    public void whenFindAllUsersByNames_thenReturnGloballyOrderedUsers() throws Exception {
        // given
        int pageNo = 2;
        int pageSize = 3;
        Sort sortBy = Sort.by(Sort.Order.asc("nickname").ignoreCase());
        Pageable pageable = PageRequest.of(pageNo, pageSize, sortBy);

        // when
        Page<User> userPage = userRepository.findAllUsersByNames("", pageable);

        // then
        assertThat(userPage.getTotalPages()).isEqualTo(4);
        assertThat(userPage.getSize()).isEqualTo(3);
        assertThat(userPage.getContent().get(0).getNickname()).isEqualTo("Min");
        assertThat(userPage.getContent().get(1).getNickname()).isEqualTo("nick");
        assertThat(userPage.getContent().get(2).getNickname()).isEqualTo("S");

    }

}
