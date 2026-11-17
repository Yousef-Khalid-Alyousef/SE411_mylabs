package psu.edu.ch06.crud04.security;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.*;
import psu.edu.ch06.crud04.repository.UserRepository;
import psu.edu.ch06.crud04.model.UserPrincipal;
@Service
public class MyUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    public MyUserDetailsService(UserRepository userRepository) { this.userRepository = userRepository; }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).map(UserPrincipal::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
