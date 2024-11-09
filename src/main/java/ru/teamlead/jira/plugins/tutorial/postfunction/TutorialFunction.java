package ru.teamlead.jira.plugins.tutorial.postfunction;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.event.type.EventDispatchOption;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.customfields.option.Option;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.UserUtils;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.jira.user.util.UserUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import com.atlassian.jira.workflow.function.issue.AbstractJiraFunctionProvider;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowException;

import java.util.List;
import java.util.Map;

/**
 * @author V.Nikolaev
 * @version $Revision$
 */
public class TutorialFunction extends AbstractJiraFunctionProvider {
    private static final Logger log = Logger.getLogger(TutorialFunction.class);

    private final ActiveObjects ao;
    private final CustomFieldManager customFieldManager;

    public TutorialFunction(ActiveObjects ao, CustomFieldManager customFieldManager) {
        this.ao = ao;
        this.customFieldManager = customFieldManager;
    }

    @Override
    public void execute(
            final Map transientVars,
            final Map args,
            final PropertySet ps
    ) throws WorkflowException {
        final String sourceField = (String) args.get("sourceField");

        log.debug("TutorialFunction sourceField = " + sourceField);

        if (StringUtils.isEmpty(sourceField)) {
            log.warn("TutorialFunction sourceField is empty !!!");
            return;
        }

        final CustomField sourceCF = customFieldManager.getCustomFieldObject(sourceField);

        if (sourceCF == null) {
            log.warn("TutorialFunction sourceCF is null ");
            return;
        }

        final MutableIssue issue = getIssue(transientVars);

        log.debug("CopyValueFunction issue = " + issue.getKey());

        if (issue == null || issue.getId() == null) {
            return;
        }

        UserManager userManager = ComponentAccessor.getUserManager();
        ApplicationUser user = null;
        String userName = null;

        final Object sourceVal = issue.getCustomFieldValue(sourceCF);

        log.warn(" TutorialFunction sourceVal " + sourceVal);

        if (sourceVal != null) {
            if (sourceVal instanceof String) {
                user = userManager.getUserByName((String) sourceVal);
                if (user == null) {
                }
            } else if (sourceVal instanceof Option) {
                String displayName = ((Option) sourceVal).getValue();
                user = UserUtils.getUserByEmail(displayName);
                if (user == null) {
                    user = userManager.getUserByName(displayName);
                }
                if (user != null) {
                    userName = user.getName();
                }
            } else {
                userName = sourceVal.toString();
            }

            log.debug(" TutorialFunction source userName " + userName);

            if (userName != null) {
                user = userManager.getUserByName(userName);
            }

            log.warn(" TutorialFunction source user " + user);

            if (user == null) {
                user = UserUtils.getUserByEmail(sourceVal.toString());
                if (user == null) {
                    user = userManager.getUserByName(sourceVal.toString());
                }
            }

            issue.setCustomFieldValue(sourceCF, user);
        }

        ApplicationUser updater = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();
        IssueManager issueManager = ComponentAccessor.getIssueManager();
        issueManager.updateIssue(updater, issue, EventDispatchOption.ISSUE_UPDATED, false);
    }
}