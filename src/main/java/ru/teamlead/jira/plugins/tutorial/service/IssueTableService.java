package ru.teamlead.jira.plugins.tutorial.service;

import com.atlassian.activeobjects.external.ActiveObjects;
import net.java.ao.Query;
import ru.teamlead.jira.plugins.tutorial.ao.IssueTable;

public class IssueTableService {

    private final ActiveObjects ao;

    public IssueTableService(ActiveObjects ao) {
        this.ao = ao;
    }

    public IssueTable createIssueTable(String name) {
        return ao.executeInTransaction(() -> {
            IssueTable issueTab = ao.create(IssueTable.class);
            issueTab.setName(name);
            issueTab.save();
            return issueTab;
        });
    }

    public IssueTable[] getAllIssueTables() {
        return ao.find(IssueTable.class, Query.select());
    }
}
