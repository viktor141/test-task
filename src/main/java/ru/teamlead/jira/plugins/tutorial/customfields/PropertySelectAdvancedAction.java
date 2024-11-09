package ru.teamlead.jira.plugins.tutorial.customfields;

import com.atlassian.jira.web.action.JiraWebActionSupport;

/**
 * @author V.Nikolaev
 * @version $Revision$
 */
//пустой класс - логика отсутствует
//    вся логика - на Velocity
public class PropertySelectAdvancedAction extends JiraWebActionSupport {
    private Long fieldConfigSchemeId;
    private Long fieldConfigId;
    private String customFieldId;

    public Long getFieldConfigSchemeId() {
        return fieldConfigSchemeId;
    }

    public void setFieldConfigSchemeId(Long fieldConfigSchemeId) {
        this.fieldConfigSchemeId = fieldConfigSchemeId;
    }

    public Long getFieldConfigId() {
        return fieldConfigId;
    }

    public void setFieldConfigId(Long fieldConfigId) {
        this.fieldConfigId = fieldConfigId;
    }

    public String getCustomFieldId() {
        return customFieldId;
    }

    public void setCustomFieldId(String customFieldId) {
        this.customFieldId = customFieldId;
    }

    public String doDefault() throws Exception {
        return doList();
    }

    protected String doExecute() throws Exception {
        return doSave();
    }

    public String doSave() throws Exception {
        return doList();
    }

    public String doList() {
        return INPUT;
    }
}