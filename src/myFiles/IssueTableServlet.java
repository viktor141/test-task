package ru.teamlead.jira.plugins.tutorial.servlet;

import net.java.ao.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.teamlead.jira.plugins.tutorial.ao.IssueTable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class IssueTableServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(IssueTableServlet.class);

    private final EntityManager entityManager;

    public IssueTableServlet(final EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            IssueTable[] issueTabs = entityManager.find(IssueTable.class);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            StringBuilder jsonResponse = new StringBuilder("{\"issueTables\": [");

            for (int i = 0; i < issueTabs.length; i++) {
                if (i > 0) jsonResponse.append(",");
                jsonResponse.append("{\"name\": \"").append(issueTabs[i].getName()).append("\"}");
            }

            jsonResponse.append("]}");
            resp.getWriter().write(jsonResponse.toString());
            log.info("Issue tables successfully retrieved and sent.");
        } catch (Exception e) {
            log.error("Failed to retrieve issue tables", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error occurred while retrieving issue tables.");
        }
    }
}
