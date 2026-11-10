package haagahelia.fi.gamelib.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import haagahelia.fi.gamelib.domain.AppUser;
import haagahelia.fi.gamelib.domain.AppUserRepo;

// Plain unit test: no Spring context, the repository is a Mockito mock
class UserDetailServiceImplTests {

    @Test
    void loadUserByUsernameMapsAppUserToUserDetails() {
        AppUserRepo repo = mock(AppUserRepo.class);
        when(repo.findByUsername("alice")).thenReturn(new AppUser("alice", "secret-hash", "ADMIN"));

        UserDetails details = new UserDetailServiceImpl(repo).loadUserByUsername("alice");

        assertThat(details.getUsername()).isEqualTo("alice");
        assertThat(details.getPassword()).isEqualTo("secret-hash");
        assertThat(details.getAuthorities()).extracting(GrantedAuthority::getAuthority).containsExactly("ADMIN");
    }
}
