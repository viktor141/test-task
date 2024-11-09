package ru.teamlead.jira.plugins.tutorial.contextproviders;

import com.atlassian.jira.util.collect.MapBuilder;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.ContextProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.teamlead.jira.plugins.tutorial.ao.IssueTable;
import ru.teamlead.jira.plugins.tutorial.service.IssueTableService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IssueTableContextProvider implements ContextProvider {

    private static final Logger log = LoggerFactory.getLogger(IssueTableContextProvider.class);
    private final IssueTableService issueTableService;

    public IssueTableContextProvider(IssueTableService issueTableService) {
        this.issueTableService = issueTableService;
    }

    @Override
    public void init(Map<String, String> map) throws PluginParseException {

    }

    @Override
    public Map<String, Object> getContextMap(Map<String, Object> map) {
        IssueTable[] issueTables = issueTableService.getAllIssueTables();
        List<String> issueTableNames = new ArrayList<>();
        for (IssueTable issueTable : issueTables) {
            issueTableNames.add(issueTable.getName());
        }
        return MapBuilder.build("issueTables", issueTableNames);
    }
}
