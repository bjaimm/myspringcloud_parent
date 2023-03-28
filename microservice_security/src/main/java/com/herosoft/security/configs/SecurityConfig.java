package com.herosoft.security.configs;

import com.herosoft.commons.utils.JwtUtils;
import com.herosoft.security.authentication.UsernamePasswordAuthenticationProvider;
import com.herosoft.security.filters.SecurityOncePerRequestFilter;
import com.herosoft.security.filters.SecurityUsernamePasswordAuthenticationFilter;
import com.herosoft.security.handlers.SecurityAuthenticationFailureHandler;
import com.herosoft.security.handlers.SecurityAuthenticationSuccessHandler;
import com.herosoft.security.service.SecurityUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    private AccessDeniedHandler accessDeniedHandler;

    @Autowired
    private SecurityOncePerRequestFilter securityOncePerRequestFilter;

    @Autowired
    private SecurityUserDetailService securityUseretailsService;
    @Autowired
    private UsernamePasswordAuthenticationProvider usernamePasswordAuthenticationProvider;

    @Bean
    public JwtUtils jwtUtils() {
        return new JwtUtils();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);
        web.ignoring().antMatchers("/css/**", "/js/**", "/images/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        //关闭跨域请求伪造的校验
        http.csrf().disable();

        //启用表单认证
        /*http.formLogin()
                //如果指定了successHandler，那defaultSuccessUrl就不起作用,需要在successHandler的实现类中再设置setDefaultTargetUrl和setAlwaysUseDefaultTargetUrl
                //.defaultSuccessUrl("/security/welcome", true)
                .successHandler(authenticationSuccessHandler())
                .failureHandler(authenticationFailureHandler()).permitAll()
                .failureUrl("/security/failLogin").permitAll();*/

        //设置请求权限
        http.authorizeRequests()
                .antMatchers("/security/login").permitAll()
                .antMatchers("/security/oauth/**").permitAll()
                .antMatchers("/security/failLogin").permitAll()
                .antMatchers("/security/authority").permitAll()
                .antMatchers("/security/whitelist").permitAll()
                .antMatchers("/security/blacklist").permitAll()
                .antMatchers("/security/getToken").permitAll()
                .antMatchers("/security/refreshToken").permitAll()
                .antMatchers("/security/p1/**").hasAuthority("security_p1")
                .antMatchers("/security/p2/**").hasAuthority("security_p2")
                .antMatchers("/security/user/**").hasRole("User")
                .anyRequest().authenticated();

        //设置过滤器
        //http.addFilterAt(securityUsernamePasswordAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(securityOncePerRequestFilter, UsernamePasswordAuthenticationFilter.class);

        //设置认证和授权异常处理器
        http.exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler);
    }
    //@Bean
    public UsernamePasswordAuthenticationFilter securityUsernamePasswordAuthenticationFilter() throws Exception {
        SecurityUsernamePasswordAuthenticationFilter usernamePasswordAuthenticationFilter = new SecurityUsernamePasswordAuthenticationFilter();

        usernamePasswordAuthenticationFilter.setAuthenticationManager(authenticationManager());
        //如果选择自定义过滤器，则这里需要重新指定successHandler才能生效
        usernamePasswordAuthenticationFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler());
        usernamePasswordAuthenticationFilter.setAuthenticationFailureHandler(authenticationFailureHandler());
        usernamePasswordAuthenticationFilter.setFilterProcessesUrl("/security/login");

        return usernamePasswordAuthenticationFilter;
    }
    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        AuthenticationSuccessHandler authenticationSuccessHandler = new SecurityAuthenticationSuccessHandler();

        return authenticationSuccessHandler;
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        AuthenticationFailureHandler authenticationFailureHandler = new SecurityAuthenticationFailureHandler();

        return authenticationFailureHandler;
    }

    /**
     * AuthenticationManager在Oauth2中的密码授权模式中也需要用到
     * @return
     * @throws Exception
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //从数据库中查询用户信息
        auth.userDetailsService(securityUseretailsService);
        auth.authenticationProvider(usernamePasswordAuthenticationProvider);

    }

    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
