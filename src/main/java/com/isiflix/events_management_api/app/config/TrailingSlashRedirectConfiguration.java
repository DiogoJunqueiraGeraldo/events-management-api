package com.isiflix.events_management_api.app.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import java.io.IOException;

@Configuration
public class TrailingSlashRedirectConfiguration {
    @Bean
    public FilterRegistrationBean<Filter> trailingSlashRedirectFilter() {
        FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new Filter() {
            @Override
            public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
                if (!(servletRequest instanceof HttpServletRequest) || !(servletResponse instanceof HttpServletResponse)) {
                    filterChain.doFilter(servletRequest, servletResponse);
                    return;
                }

                this.doHttpFilter((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse, filterChain);
            }


            private void doHttpFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
                    throws IOException, ServletException {
                String requestURI = req.getRequestURI();

                boolean isNotRootPath = requestURI.length() > 1;
                boolean isTrailingSlash = requestURI.endsWith("/");
                if (isNotRootPath && isTrailingSlash) {
                    String matchAnyAmountOfTrailingSlashes = "/+$";
                    String sanitizedURI = requestURI.replaceAll(matchAnyAmountOfTrailingSlashes, "");

                    String queryString = req.getQueryString();
                    if (queryString != null) {
                        sanitizedURI += "?".concat(queryString);
                    }

                    res.setStatus(HttpStatus.PERMANENT_REDIRECT.value());
                    res.setHeader("Location", sanitizedURI);
                    return;
                }
                chain.doFilter(req, res);
            }
        });
        registrationBean.setOrder(0);
        return registrationBean;
    }

}
