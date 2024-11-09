package ru.teamlead.jira.plugins.tutorial.action;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.jira.config.IssueTypeManager;
import com.atlassian.jira.config.StatusManager;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.customfields.impl.DateCFType;
import com.atlassian.jira.issue.customfields.impl.DateTimeCFType;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.screen.FieldScreenManager;
import com.atlassian.jira.issue.security.IssueSecurityLevelManager;
import com.atlassian.jira.permission.GlobalPermissionKey;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.propertyset.JiraPropertySetFactory;
import com.atlassian.jira.security.groups.GroupManager;
import com.atlassian.jira.security.request.RequestMethod;
import com.atlassian.jira.security.request.SupportedMethods;
import com.atlassian.jira.web.action.ActionViewData;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.opensymphony.module.propertyset.PropertySet;
import ru.teamlead.jira.plugins.tutorial.listener.TutorialUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author V.Nikolaev
 * @version $Revision$
 */
@SupportedMethods({RequestMethod.POST, RequestMethod.PUT, RequestMethod.GET})
//пустая страница - вся логика на Velocity
public class TutorialAdminAction extends JiraWebActionSupport {
    private final ProjectManager projectManager;
    private final GroupManager groupManager;
    private final CustomFieldManager customFieldManager;
    private final IssueTypeManager issueTypeManager;
    private final StatusManager statusManager;
    private final IssueSecurityLevelManager issueSecurityLevelManager;
    private final FieldScreenManager fieldScreenManager;
    protected final ApplicationProperties applicationProperties;
    protected final TutorialUtils tutorialUtils;
    protected static PropertySet ps;

    public TutorialAdminAction(ProjectManager projectManager, GroupManager groupManager,
                               CustomFieldManager customFieldManager, IssueTypeManager issueTypeManager,
                               StatusManager statusManager, IssueSecurityLevelManager issueSecurityLevelManager,
                               FieldScreenManager fieldScreenManager, TutorialUtils tutorialUtils,
                               JiraPropertySetFactory jiraPropertySetFactory) {
        this.projectManager = projectManager;
        this.groupManager = groupManager;
        this.customFieldManager = customFieldManager;
        this.issueTypeManager = issueTypeManager;
        this.statusManager = statusManager;
        this.issueSecurityLevelManager = issueSecurityLevelManager;
        this.fieldScreenManager = fieldScreenManager;
        this.tutorialUtils = tutorialUtils;
        this.applicationProperties = ComponentAccessor.getApplicationProperties();

        ps = jiraPropertySetFactory.buildCachingPropertySet("tutorial-ps.properties", 1l);
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
//    метод для Velocity - проверка прав
    public Boolean getNeededPermissions() {
        return tutorialUtils.getNeededPermissions();
//        return hasGlobalPermission(GlobalPermissionKey.ADMINISTER);
    }

    @ActionViewData
    public String getJiraBaseUrl() {
        String baseUrl = applicationProperties.getString(APKeys.JIRA_BASEURL);
        return baseUrl;
    }

    @ActionViewData
    public String getStartDateCFVal() {
        String val = null;
        if (ps.exists("test.start")) {
            val = ps.getString("test.start");
        }
        return val == null ? "" : val;
    }

    @ActionViewData
    public String getEndDateCFVal() {
        String val = null;
        if (ps.exists("test.finish")) {
            val = ps.getString("test.finish");
        }
        return val == null ? "" : val;
    }

    @ActionViewData
    public List<FieldVO> getDateCustomFields() {
        List<FieldVO> res = new ArrayList<>();
        for (CustomField cf : ComponentAccessor.getCustomFieldManager().getCustomFieldObjects()) {
            if (cf.getCustomFieldType() instanceof DateTimeCFType
                    || DateTimeCFType.class.isAssignableFrom(cf.getCustomFieldType().getClass())
                    || cf.getCustomFieldType() instanceof DateCFType
                    || DateCFType.class.isAssignableFrom(cf.getCustomFieldType().getClass())) {
                res.add(new FieldVO(cf));
            }
        }
        return res;
    }
}
