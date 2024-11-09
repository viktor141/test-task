package ru.teamlead.jira.plugins.tutorial.customfields;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.atlassian.jira.issue.fields.config.FieldConfigItemType;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;

/**
 * @author V.Nikolaev
 * @version $Revision$
 */
//пункт настроек поля - связь поля и страницы настроек
public class PropSelectConfigItem implements FieldConfigItemType {
    public PropSelectConfigItem() {
    }

    public String getDisplayName() {
        return "Load options";
    }

    public String getDisplayNameKey() {
        return "tl.tutorial.source";
    }

    public String getViewHtml(FieldConfig config, FieldLayoutItem fieldLayoutItem) {
//        final String customFieldId = String.valueOf(config.getCustomField().getIdAsLong());
        return "";
    }

    public String getObjectKey() {
        return "default";
    }

    public Object getConfigurationObject(Issue issue, FieldConfig config) {
        return config.getCustomField().getCustomFieldType().getDefaultValue(config);
    }

//    путь к странице настроек поля
    public String getBaseEditUrl() {
        return "PropertySelectAdvancedAction!default.jspa";
    }
}