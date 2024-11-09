package ru.teamlead.jira.plugins.tutorial.listener;

import java.io.Serializable;

/**
 * @author V.Nikolaev
 * @version $Revision$
 */
public class IssueVO implements Serializable {
    private String key;
    private String summary;
    private String diff;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public IssueVO(String key, String summary, String diff, Long id) {
        this.key = key;
        this.summary = summary;
        this.diff = diff;
        this.id = id;
    }

    private Long id;

    public IssueVO() {
    }

    public String getDiff() {
        return diff;
    }

    public void setDiff(String diff) {
        this.diff = diff;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public IssueVO(String key, String summary, String diff) {
        this.key = key;
        this.summary = summary;
        this.diff = diff;
    }
}