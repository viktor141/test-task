package ru.teamlead.jira.plugins.tutorial.listener;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.permission.GlobalPermissionKey;

import java.util.List;

/**
 * @author V.Nikolaev
 * @version $Revision$
 */
//логики нет
public interface TutorialUtils {
    Boolean getNeededPermissions();

    List<Issue> getIssues(String jql);

    List<IssueVO> getIssuesVo(String jql);
}