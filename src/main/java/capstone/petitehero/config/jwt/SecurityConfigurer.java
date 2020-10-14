package capstone.petitehero.config.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
public class SecurityConfigurer extends WebSecurityConfigurerAdapter {

    @Autowired
    private PetiteHeroUserDetailService petiteHeroUserDetailService;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(petiteHeroUserDetailService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests().antMatchers("/test/token").permitAll()
                .antMatchers("/v2/api-docs", "/configuration/**", "/swagger*/**", "/webjars/**").permitAll()
                .antMatchers("/account/login").permitAll()
                .antMatchers("/account/admin/register").permitAll()
                .antMatchers("/account/parent/register").permitAll()
                .antMatchers("/parent/register-profile").permitAll()
                .antMatchers("/parent/{phone}/children").permitAll()
                .antMatchers("/child/{childId}/tasks").permitAll()
                .antMatchers("/location/current-location").permitAll()
                .antMatchers("/location/list/{child}/{time}").permitAll()
                .antMatchers("/location/latest/{child}").permitAll()
                .antMatchers("/child/task").permitAll()
                .antMatchers("/child/quest").permitAll()
                .antMatchers("/task/{taskId}").permitAll()
                .antMatchers("/task/list/{childId}").permitAll()
                .antMatchers("/quest/list/{childId}").permitAll()
                .antMatchers("/quest/{questId}").permitAll()
                .antMatchers("/child/test").permitAll()
                .anyRequest().authenticated()
                .and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    @Bean
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}
