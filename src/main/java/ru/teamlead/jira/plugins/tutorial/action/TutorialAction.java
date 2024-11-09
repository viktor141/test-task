package ru.teamlead.jira.plugins.tutorial.action;

import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.jira.config.IssueTypeManager;
import com.atlassian.jira.config.StatusManager;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.fields.screen.FieldScreenManager;
import com.atlassian.jira.issue.security.IssueSecurityLevelManager;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.security.groups.GroupManager;
import com.atlassian.jira.security.request.RequestMethod;
import com.atlassian.jira.security.request.SupportedMethods;
import com.atlassian.jira.web.action.ActionViewData;
import com.atlassian.jira.web.action.JiraWebActionSupport;

@SupportedMethods({RequestMethod.POST, RequestMethod.PUT, RequestMethod.GET})
//пустая страница - вся логика на Velocity
public class TutorialAction extends JiraWebActionSupport {
    private final ProjectManager projectManager;
    private final GroupManager groupManager;
    private final CustomFieldManager customFieldManager;
    private final IssueTypeManager issueTypeManager;
    private final StatusManager statusManager;
    private final IssueSecurityLevelManager issueSecurityLevelManager;
    private final FieldScreenManager fieldScreenManager;
    protected final ApplicationProperties applicationProperties;

    public TutorialAction(ProjectManager projectManager, GroupManager groupManager,
                          CustomFieldManager customFieldManager, IssueTypeManager issueTypeManager,
                          StatusManager statusManager, IssueSecurityLevelManager issueSecurityLevelManager,
                          FieldScreenManager fieldScreenManager, ApplicationProperties applicationProperties) {
        this.projectManager = projectManager;
        this.groupManager = groupManager;
        this.customFieldManager = customFieldManager;
        this.issueTypeManager = issueTypeManager;
        this.statusManager = statusManager;
        this.issueSecurityLevelManager = issueSecurityLevelManager;
        this.fieldScreenManager = fieldScreenManager;
        this.applicationProperties = applicationProperties;
    }

    @SupportedMethods({RequestMethod.POST, RequestMethod.PUT, RequestMethod.GET})
    public String doDefault() throws Exception {
        return doView();
    }

    @SupportedMethods({RequestMethod.POST, RequestMethod.PUT, RequestMethod.GET})
    public String doView() throws Exception {
        return "success";
    }

    @SupportedMethods({RequestMethod.POST, RequestMethod.PUT, RequestMethod.GET})
    public String doSave() throws Exception {
        return doExecute();
    }

    @SupportedMethods({RequestMethod.POST, RequestMethod.PUT, RequestMethod.GET})
    public String doExecute() throws Exception {
        return doView();
    }

    @ActionViewData
    public Boolean getNeededPermissions() {
        return true;
    }

    @ActionViewData
    public String getJiraBaseUrl() {
        String baseUrl = applicationProperties.getString(APKeys.JIRA_BASEURL);
        return baseUrl;
    }
}
