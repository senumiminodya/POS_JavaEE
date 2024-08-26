package lk.ijse.eebackend.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@WebFilter(urlPatterns = "/*")
public class CORSFilter extends HttpFilter {
    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        String origin = req.getHeader("Origin");
        String configedOrigin = getServletContext().getInitParameter("origin");

        // Allowing multiple configured origins (comma-separated in the context param)
        List<String> allowedOrigins = configedOrigin != null ? Arrays.asList(configedOrigin.split(",")) : null;

        // Check for null values to avoid NullPointerException
        if (origin != null && (allowedOrigins == null || allowedOrigins.contains(configedOrigin))) {
            res.setHeader("Access-Control-Allow-Origin", origin);
            res.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, OPTIONS");
            res.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
            res.setHeader("Access-Control-Expose-Headers", "Content-Type, Authorization");
            res.setHeader("Access-Control-Allow-Credentials", "true");
        } else {
            // Optionally handle cases where the Origin is not allowed
            res.setHeader("Access-Control-Allow-Origin", "*"); // Allow all origins or handle as needed
        }

        // Handle OPTIONS request separately for CORS preflight requests
        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            res.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        chain.doFilter(req, res);
    }
}
