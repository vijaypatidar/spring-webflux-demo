package com.vkpapps.demo.security;

import com.vkpapps.demo.models.User;
import com.vkpapps.demo.services.user.UserService;
import java.util.Collection;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class MyUserDetailService implements ReactiveUserDetailsService {
  private final UserService userService;

  public MyUserDetailService(UserService userService) {
    this.userService = userService;
  }

  @Override
  public Mono<UserDetails> findByUsername(String userId) {
    log.info("User trying to login : " + userId);
    return userService.getUsername(userId)
        .map(MyUserDetail::new);
  }

  public static class MyUserDetail implements UserDetails {
    private final User user;

    public MyUserDetail(User user) {
      this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
      return user.getRoles().stream().map(role -> (GrantedAuthority) () -> role)
          .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
      return user.getPassword();
    }

    @Override
    public String getUsername() {
      return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
      return true;
    }

    @Override
    public boolean isAccountNonLocked() {
      return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
      return true;
    }

    @Override
    public boolean isEnabled() {
      return true;
    }
  }
}
