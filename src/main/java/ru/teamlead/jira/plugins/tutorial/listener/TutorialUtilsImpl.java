package ru.teamlead.jira.plugins.tutorial.listener;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.bc.JiraServiceContext;
import com.atlassian.jira.bc.JiraServiceContextImpl;
import com.atlassian.jira.bc.filter.SearchRequestService;
import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.IssueTypeManager;
import com.atlassian.jira.config.StatusManager;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.screen.FieldScreenManager;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchRequest;
import com.atlassian.jira.issue.search.SearchResults;
import com.atlassian.jira.issue.security.IssueSecurityLevelManager;
import com.atlassian.jira.permission.GlobalPermissionKey;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.propertyset.JiraPropertySetFactory;
import com.atlassian.jira.security.GlobalPermissionManager;
import com.atlassian.jira.security.groups.GroupManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.jira.util.JiraDurationUtils;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.jira.web.util.AuthorizationSupport;
import com.atlassian.jira.workflow.WorkflowManager;
import com.opensymphony.module.propertyset.PropertySet;
import org.apache.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author V.Nikolaev
 * @version $Revision$
 */
//логики нет
public class TutorialUtilsImpl implements TutorialUtils {
    private static final Logger log = Logger.getLogger(TutorialUtilsImpl.class);

    private final ActiveObjects ao;
    private final WorkflowManager workflowManager;
    private final GroupManager groupManager;
    private final ProjectManager projectManager;
    private final CustomFieldManager customFieldManager;
    private final IssueTypeManager issueTypeManager;
    private final StatusManager statusManager;
    private final IssueSecurityLevelManager issueSecurityLevelManager;
    private final FieldScreenManager fieldScreenManager;
    private final UserManager userManager;
    private final IssueManager issueManager;
    private final ApplicationProperties applicationProperties;
    private final GlobalPermissionManager globalPermissionManager;
    private final JiraPropertySetFactory jiraPropertySetFactory;
    private final SearchService searchService;
    protected static PropertySet ps;

    public TutorialUtilsImpl(ActiveObjects ao, WorkflowManager workflowManager, GroupManager groupManager,
                             ProjectManager projectManager, CustomFieldManager customFieldManager,
                             IssueTypeManager issueTypeManager, StatusManager statusManager,
                             IssueSecurityLevelManager issueSecurityLevelManager, FieldScreenManager fieldScreenManager,
                             UserManager userManager, IssueManager issueManager, ApplicationProperties applicationProperties,
                             GlobalPermissionManager globalPermissionManager, JiraPropertySetFactory jiraPropertySetFactory,
                             SearchService searchService) {
        this.ao = ao;
        this.workflowManager = workflowManager;
        this.groupManager = groupManager;
        this.projectManager = projectManager;
        this.customFieldManager = customFieldManager;
        this.issueTypeManager = issueTypeManager;
        this.statusManager = statusManager;
        this.issueSecurityLevelManager = issueSecurityLevelManager;
        this.fieldScreenManager = fieldScreenManager;
        this.userManager = userManager;
        this.issueManager = issueManager;
        this.applicationProperties = applicationProperties;
        this.globalPermissionManager = globalPermissionManager;
        this.jiraPropertySetFactory = jiraPropertySetFactory;
        this.searchService = searchService;

        ps = this.jiraPropertySetFactory.buildCachingPropertySet("tutorial-ps.properties", 1l);
    }

    @Override
    public Boolean getNeededPermissions() {
        return ComponentAccessor.getComponent(AuthorizationSupport.class).hasGlobalPermission(GlobalPermissionKey.ADMINISTER);
    }

    @Override
    public List<Issue> getIssues(String jql) {
        ApplicationUser user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();
        if (jql.contains("project-")) {
            String projectId = jql.replace("project-", "");
            Project project = projectManager.getProjectObj(Long.valueOf(projectId));
            String projectQuery = " project = " + project.getKey();
            final SearchService.ParseResult parseResult = searchService.parseQuery(user, projectQuery);
            if (parseResult.isValid()) {
                try {
                    final SearchResults results = searchService
                            .search(user, parseResult.getQuery(),
//                                    PagerFilter.getUnlimitedFilter()
                                    PagerFilter.newPageAlignedFilter(0, 50)
                            );
//                    List<Issue> issues = results.getIssues();
                    List<Issue> issues = getIssuesFromSearchResults(results);
                    return issues;
                } catch (SearchException e) {
                    log.error(e, e);
                }
            } else {
                log.error(parseResult.getErrors());
            }
        }

        if (jql.contains("filter-")) {
            String filterId = jql.replace("filter-", "");

            JiraServiceContext jiraServiceContext = new JiraServiceContextImpl(user);
            SearchRequest searchRequest = ComponentAccessor.getComponent(SearchRequestService.class).getFilter(jiraServiceContext,
                    Long.valueOf(filterId));

            if (searchRequest == null) {
                return null;
            }

            try {
                final SearchResults results = searchService.search(user, searchRequest.getQuery(),

                        PagerFilter.newPageAlignedFilter(0, 50)
//                        PagerFilter.getUnlimitedFilter()

                );
//                List<Issue> issues = results.getIssues();
                List<Issue> issues = getIssuesFromSearchResults(results);

                return issues;
            } catch (SearchException e) {
                log.error(e, e);
            }
        }

        return null;
    }


    public List<Issue> getIssuesFromSearchResults(SearchResults results) {
        List<Issue> issues = new ArrayList<>();
        try {
            Method newGetMethod = null;

            try {
                newGetMethod = SearchResults.class.getMethod("getIssues");
            } catch (NoSuchMethodException e) {
                try {
                    if (log.isInfoEnabled()) {
                        log.info("SearchResults.getIssues does not exist - trying to use getResults!");
                    }
                    newGetMethod = SearchResults.class.getMethod("getResults");
                } catch (NoSuchMethodError e2) {
                    log.error("SearchResults.getResults does not exist!");
                }
            }

            if (newGetMethod != null) {
                issues = (List<Issue>) newGetMethod.invoke(results);
            } else {
                log.error("ERROR NO METHOD TO GET ISSUES !");
                throw new RuntimeException("ICT: SearchResults Service from JIRA NOT AVAILABLE (getIssue / getResults)");
            }
        } catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            log.error("Jql Helper can net get search result (ICT)", e);
        } catch (Exception e) {
            log.error("Jql Helper can net get search result - other exception (ICT)", e);
        }

        return issues;
    }


    @Override
    public List<IssueVO> getIssuesVo(String jql) {
        List<Issue> issues = getIssues(jql);

        if (issues == null) {
            return null;
        }
//            issues.stream().map(issue -> )

        CustomField cfstart = customFieldManager.getCustomFieldObject(ps.getString("test.start") == null ? "customfield_10124" : ps.getString("test.start"));
        CustomField cffinish = customFieldManager.getCustomFieldObject(ps.getString("test.finish") == null ? "customfield_10125" : ps.getString("test.finish"));
        JiraDurationUtils jiraDurationUtils = ComponentAccessor.getJiraDurationUtils();

        List<IssueVO> ret = new ArrayList<>();
        for (Issue issue : issues) {
            Date start = (Date) issue.getCustomFieldValue(cfstart);
            Date finish = (Date) issue.getCustomFieldValue(cffinish);

            if (start == null || finish == null) {
                continue;
            }

            Long diff = finish.getTime() - start.getTime();

            String diffFmt = jiraDurationUtils.getShortFormattedDuration(diff);
            if (diff < 0) {
                diffFmt = "-" + jiraDurationUtils.getShortFormattedDuration(-diff);
            }

            ret.add(new IssueVO(issue.getKey(), issue.getSummary(), diffFmt, issue.getId()));
        }

        return ret;
    }


}