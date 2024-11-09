package ru.teamlead.jira.plugins.tutorial.listener;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.type.EventType;
import com.atlassian.jira.propertyset.JiraPropertySetFactory;
import com.opensymphony.module.propertyset.PropertySet;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author V.Nikolaev
 * @version $Revision$
 */
//слушатель расширяет интерфейсы инициализации и разрушения бина
public class TutorialListener implements InitializingBean, DisposableBean {
    private static final Logger log = Logger.getLogger(TutorialListener.class);

//    публикатор событий
    private final EventPublisher eventPublisher;

//    хранилище ключ-значение
    private final PropertySet remindingOfbizPs;

    private final JiraPropertySetFactory jiraPropertySetFactory;

    public TutorialListener(EventPublisher eventPublisher, JiraPropertySetFactory jiraPropertySetFactory) {
        this.eventPublisher = eventPublisher;
        this.jiraPropertySetFactory = jiraPropertySetFactory;
        remindingOfbizPs = this.jiraPropertySetFactory.buildCachingPropertySet("reminding_entity.properties", 1l);
    }

    @Override
    public void destroy() throws Exception {
//        разрушение слушателя
        eventPublisher.unregister(this);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
//        инициализаци
        eventPublisher.register(this);
    }

    @EventListener
    public void workflowEvent(final IssueEvent event) throws Exception {
//        перехват событий c задачами
//        типы событий
        final Long eventTypeId = event.getEventTypeId();

        if (log.isDebugEnabled()) {
            log.debug("workflowEvent  "  + event);
        }

        log.warn("workflowEvent warn " + event);

        if (eventTypeId.equals(EventType.ISSUE_CREATED_ID)) {
        } else if (eventTypeId.equals(EventType.ISSUE_UPDATED_ID)) {
        } else if (eventTypeId.equals(EventType.ISSUE_ASSIGNED_ID)) {
        } else if (eventTypeId.equals(EventType.ISSUE_RESOLVED_ID)) {
        } else if (eventTypeId.equals(EventType.ISSUE_COMMENTED_ID)) {
        } else if (eventTypeId.equals(EventType.ISSUE_COMMENT_EDITED_ID)) {
        } else if (eventTypeId.equals(EventType.ISSUE_CLOSED_ID)) {
        } else if (eventTypeId.equals(EventType.ISSUE_REOPENED_ID)) {
        } else if (eventTypeId.equals(EventType.ISSUE_MOVED_ID)) {
        } else if (eventTypeId.equals(EventType.ISSUE_DELETED_ID)) {
        } else if (eventTypeId.equals(EventType.ISSUE_WORKSTARTED_ID)) {
        } else if (eventTypeId.equals(EventType.ISSUE_WORKSTOPPED_ID)) {
        } else if (eventTypeId.equals(EventType.ISSUE_WORKLOGGED_ID)) {
        } else if (eventTypeId.equals(EventType.ISSUE_WORKLOG_UPDATED_ID)) {
        } else if (eventTypeId.equals(EventType.ISSUE_WORKLOG_DELETED_ID)) {
        } else if (eventTypeId.equals(EventType.ISSUE_GENERICEVENT_ID)) {
        } else {
            customEvent(event);
        }
    }

    public void customEvent(final IssueEvent event) {
    }
}