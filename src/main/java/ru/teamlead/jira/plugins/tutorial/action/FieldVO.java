package ru.teamlead.jira.plugins.tutorial.action;

import com.atlassian.extras.common.org.springframework.util.StringUtils;
import com.atlassian.jira.issue.fields.CustomField;

import java.io.Serializable;

/**
 * @author V.Nikolaev
 * @version $Revision$
 */
public class FieldVO implements Serializable {
    private String id;
    private String name;

    public FieldVO(String id, String name) {
        this.id = id;
        this.name = StringUtils.replace(name, "\"", " ");
        this.name = StringUtils.replace(this.name, "'", " ");
    }

    public FieldVO() {
    }

    public FieldVO(CustomField cf) {
        id = cf.getId();
        name = StringUtils.replace(cf.getName(), "\"", " ");
        name = StringUtils.replace(name, "'", " ");
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
