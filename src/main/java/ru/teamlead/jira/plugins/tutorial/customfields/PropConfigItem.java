package ru.teamlead.jira.plugins.tutorial.customfields;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.atlassian.jira.issue.fields.config.FieldConfigItemType;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;
import com.atlassian.sal.api.transaction.TransactionCallback;
import ru.teamlead.jira.plugins.tutorial.ao.Propconfitem;

//конфигурация поля
public class PropConfigItem implements FieldConfigItemType {
    private final ActiveObjects ao;

    public PropConfigItem(ActiveObjects ao) {
        this.ao = ao;
    }

//    имя секции настроек поля
    public String getDisplayName() {
        return "Settings: set up source field";
    }

    public String getDisplayNameKey() {
        return "tl.tutorial.source";
    }

//    внешний вид секции настроек поля для Velocity
    public String getViewHtml(FieldConfig config, FieldLayoutItem fieldLayoutItem) {
        final String customFieldId = String.valueOf(config.getCustomField().getIdAsLong());

        String res = ao.executeInTransaction(new TransactionCallback<String>() // (1)
        {
            @Override
            public String doInTransaction() {
                String sourceVal = "";
                Propconfitem[] propconfitems = ao
                        .find(Propconfitem.class, " PROPCFID = ? ", customFieldId);

                if (propconfitems.length > 0) {
                    Propconfitem propconfitem = propconfitems[0];
                    sourceVal = propconfitem.getSourcefield();
                }

                return sourceVal;
            }
        });

        return res;
    }

    public String getObjectKey() {
        return "default";
    }

//    объект со значение поля по умолчанию
    public Object getConfigurationObject(Issue issue, FieldConfig config) {
        return config.getCustomField().getCustomFieldType().getDefaultValue(config);
    }

//    страница настроек поля
    public String getBaseEditUrl() {
        return "PropertyAdvancedAction!default.jspa";
    }
}