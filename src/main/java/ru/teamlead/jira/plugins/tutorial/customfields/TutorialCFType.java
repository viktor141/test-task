package ru.teamlead.jira.plugins.tutorial.customfields;

import com.atlassian.jira.issue.customfields.impl.GenericTextCFType;
import java.util.List;
import java.util.Map;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.customfields.impl.FieldValidationException;
import com.atlassian.jira.issue.customfields.manager.GenericConfigManager;
import com.atlassian.jira.issue.customfields.persistence.CustomFieldValuePersister;
import com.atlassian.jira.issue.customfields.view.CustomFieldParams;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.config.FieldConfigItemType;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.sal.api.transaction.TransactionCallback;
import ru.teamlead.jira.plugins.tutorial.ao.Propconfitem;

//расширяем тестовый тип поля для хранения строки 255 символов
public class TutorialCFType extends GenericTextCFType {
    private final ActiveObjects ao;
    private final JiraAuthenticationContext jiraAuthenticationContext;

    public TutorialCFType(
            CustomFieldValuePersister customFieldValuePersister,
            GenericConfigManager genericConfigManager,
            ActiveObjects _ao,
            JiraAuthenticationContext jiraAuthenticationContext) {
        super(customFieldValuePersister, genericConfigManager);
        ao = _ao;
        this.jiraAuthenticationContext = jiraAuthenticationContext;
    }

    public List<FieldConfigItemType> getConfigurationItemTypes() {
//        ссылка на конфигурацию - страницу PropertyAdvancedAction

        final List<FieldConfigItemType> configurationItemTypes = super.getConfigurationItemTypes();
        configurationItemTypes.add(new PropConfigItem(ao));
        return configurationItemTypes;
    }

    @Override
//    настройка контекста просмотра значения поля - что будем использовать на страницах Velocity
    public Map<String, Object> getVelocityParameters(
            final Issue issue,
            final CustomField field,
            final FieldLayoutItem fieldLayoutItem) {

//        хранилище значений для передачи в Velocity
        final Map<String, Object> parameters = super.getVelocityParameters(issue, field, fieldLayoutItem);
        parameters.put("sourceVal", "");

//        запрос к базе через ActiveObjects
//        открытие транзакции
        String res = ao.executeInTransaction(new TransactionCallback<String>() // (1)
        {
            @Override
            public String doInTransaction() {
                String sourceVal = "";

//                найти все записи по нужному полю
                Propconfitem[] propconfitems = ao
                        .find(Propconfitem.class, " PROPCFID = ? ", field.getIdAsLong().toString());

                if (propconfitems.length > 0) {
                    Propconfitem propconfitem = propconfitems[0];
                    sourceVal = propconfitem.getSourcefield();
                    parameters.put("sourceVal", sourceVal);
                }

                return sourceVal;
            }
        });

        return parameters;
    }

    public String getValueFromCustomFieldParams(final CustomFieldParams relevantParams) throws FieldValidationException {
//        получение значения поля из контекста
        String value = super.getValueFromCustomFieldParams(relevantParams);
        return value;
    }
}