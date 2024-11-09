package ru.teamlead.jira.plugins.tutorial.customfields;

import com.atlassian.jira.issue.customfields.impl.SelectCFType;
import com.atlassian.jira.issue.customfields.manager.GenericConfigManager;
import com.atlassian.jira.issue.customfields.manager.OptionsManager;
import com.atlassian.jira.issue.customfields.persistence.CustomFieldValuePersister;
import com.atlassian.jira.issue.fields.config.FieldConfigItemType;
import com.atlassian.jira.issue.fields.rest.json.beans.JiraBaseUrls;

import java.util.List;

/**
 * @author V.Nikolaev
 * @version $Revision$
 */
//вся логика поля -как у стандартного поля select с опциями
public class TutorialCFSelectType extends SelectCFType {
    public TutorialCFSelectType(CustomFieldValuePersister customFieldValuePersister, OptionsManager optionsManager, GenericConfigManager genericConfigManager, JiraBaseUrls jiraBaseUrls) {
        super(customFieldValuePersister, optionsManager, genericConfigManager, jiraBaseUrls);
    }

    public List<FieldConfigItemType> getConfigurationItemTypes() {
        final List<FieldConfigItemType> configurationItemTypes = super.getConfigurationItemTypes();
        configurationItemTypes.add(new PropSelectConfigItem());
        return configurationItemTypes;
    }
}