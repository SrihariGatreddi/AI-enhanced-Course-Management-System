package com.cms.filter;

import com.cms.model.ErrorLog;
import com.cms.model.ViewLog;
import com.cms.repository.ErrorLogRepository;
import com.cms.repository.ViewLogRepository;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@Order(1)
public class ErrorLogFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(ErrorLogFilter.class);

    private final ViewLogRepository viewLogRepository;
    private final ErrorLogRepository errorLogRepository;//why? final

    @Autowired
    public ErrorLogFilter(ViewLogRepository viewLogRepository, ErrorLogRepository errorLogRepository) {
        this.viewLogRepository = viewLogRepository;
        this.errorLogRepository = errorLogRepository;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        // Ensure session is created and set timeout to 60 seconds
        req.getSession(true).setMaxInactiveInterval(60);
        try {
            // Log page view
            String pageName = req.getRequestURI();
            if (!pageName.startsWith("/css") && !pageName.startsWith("/js") && !pageName.startsWith("/images")) {
                logger.debug("Logging page view for: {}", pageName);
                viewLogRepository.save(new ViewLog(pageName, LocalDateTime.now()));
            }
            chain.doFilter(request, response);
            // Log HTTP errors (status >= 400)
            if (res.getStatus() >= 400) {
                String errorMsg = "HTTP Error " + res.getStatus() + " for URI: " + req.getRequestURI();
                errorLogRepository.save(new ErrorLog(errorMsg, LocalDateTime.now()));
            }
        } catch (Exception e) {
            // Log unhandled exceptions
            logger.error("Unhandled exception: {}", e.getMessage(), e);
            errorLogRepository.save(new ErrorLog("Exception: " + e.getMessage(), LocalDateTime.now()));
            throw e; // Rethrow to allow normal error handling
        }
    }
}
