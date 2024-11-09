package ru.teamlead.jira.plugins.tutorial.listener;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import ru.teamlead.jira.plugins.tutorial.service.IssueTableService;

public class PluginEventListener implements InitializingBean, DisposableBean {


    private final EventPublisher eventPublisher;
    private final IssueTableService issueTableService;

    public PluginEventListener(EventPublisher eventPublisher, IssueTableService issueTableService) {
        this.eventPublisher = eventPublisher;
        this.issueTableService = issueTableService;
    }

    @EventListener
    public void onPluginEnabled(com.atlassian.plugin.event.events.PluginEnabledEvent event) {
        if ("ru.teamlead.plugin.jira-plugin-tutorial".equals(event.getPlugin().getKey()) && issueTableService.getAllIssueTables().length == 0) {
            issueTableService.createIssueTable("Issue table 1");
            issueTableService.createIssueTable("Issue table 2");
            issueTableService.createIssueTable("Issue table 3");
        }
    }


    @Override
    public void afterPropertiesSet() {
        eventPublisher.register(this);
    }

    @Override
    public void destroy() {
        eventPublisher.unregister(this);
    }
}
