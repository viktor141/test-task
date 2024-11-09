package ru.teamlead.jira.plugins.tutorial;

import com.atlassian.jira.plugin.webfragment.conditions.AbstractJiraCondition;
import com.atlassian.jira.plugin.webfragment.model.JiraHelper;
import com.atlassian.jira.user.ApplicationUser;
import org.apache.log4j.Logger;

/**
 * @author V.Nikolaev
 * @version $Revision$
 */
//пустое условие - всегда показывать
public class TutorialCondition extends AbstractJiraCondition {
    private static final Logger log = Logger.getLogger(TutorialCondition.class);

    public TutorialCondition() {
    }

    @Override
    public boolean shouldDisplay(ApplicationUser admUser, JiraHelper jiraHelper) {
        return true;
    }
}

