package ru.teamlead.jira.plugins.tutorial.customfields;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.IssueTypeManager;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.customfields.manager.OptionsManager;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.jira.util.I18nHelper;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.atlassian.sal.api.transaction.TransactionCallback;
import org.apache.velocity.app.Velocity;
import ru.teamlead.jira.plugins.tutorial.ao.Propconfitem;

import java.util.*;

//сохраняем настройки поля
public class PropertyAdvancedAction extends JiraWebActionSupport {
    private final CustomFieldManager customFieldManager;
    private final I18nHelper i18nBean;
    private final SearchService searchService;
    private final UserManager userManager;
    private final OptionsManager optionsManager;
    private final IssueTypeManager issueTypeManager;
    private final ProjectManager projectManager;
    private final ActiveObjects ao;

//    стандартные поля из конфигурации поля jira
    private Long fieldConfigSchemeId;
    private Long fieldConfigId;
    private String customFieldId;
    private String source;

    public PropertyAdvancedAction(
            SearchService _searchService,
            UserManager _userManager,
            CustomFieldManager _customFieldManager,
            OptionsManager optionsManager,
            IssueTypeManager issueTypeManager, ProjectManager _projectManager, ActiveObjects ao1) {
        this.customFieldManager = _customFieldManager;
        this.searchService = _searchService;
        this.userManager = _userManager;
        this.optionsManager = optionsManager;
        this.issueTypeManager = issueTypeManager;
        projectManager = _projectManager;
        this.ao = ao1;
        this.i18nBean = ComponentAccessor.getJiraAuthenticationContext().getI18nHelper();
    }

    public String getCustomFieldId() {
        return customFieldId;
    }

    public void setCustomFieldId(String _customFieldId) {
        customFieldId = _customFieldId;
    }

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

//    получение значения имени связанного поля для Velocity
    public String getSourceVal() {
        return ao.executeInTransaction(new TransactionCallback<String>() // (1)
        {
            @Override
            public String doInTransaction() {
                String sourceVal = "";

//                поиск настроек поля через Active Objects
                Propconfitem[] propconfitems = ao
                        .find(Propconfitem.class, " PROPCFID = ? ", customFieldId);

                if (propconfitems.length > 0) {
                    Propconfitem propconfitem = propconfitems[0];
                    sourceVal = propconfitem.getSourcefield();
                }

                return sourceVal;
            }
        });
    }

//    id связанного поля
    public String getSource() {
        return source;
    }

    public void setSource(String _source) {
        source = _source;
    }

    public String doDefault() throws Exception {
        return doList();
    }

//    сохранение настроек - стандартный метод сохранения данных с формы
    protected String doExecute() throws Exception {
        return doSave();
    }

//    метод !save
    public String doSave() throws Exception {
        ao.executeInTransaction(new TransactionCallback<Void>() // (1)
        {
            @Override
            public Void doInTransaction() {
//                найти объект из бд, связанный с кастомным полем
                Propconfitem[] propconfitems = ao
                        .find(Propconfitem.class, " PROPCFID = ? ", customFieldId);

                Propconfitem propconfitem;
                if (propconfitems.length > 0) {
                    propconfitem = propconfitems[0];
                } else {
//                создаем объект
                    propconfitem = ao.create(Propconfitem.class);
                }

//                заполняем поля
                propconfitem.setPropcfid(customFieldId);
                propconfitem.setSourcefield(source);
                propconfitem.save();

                return null;
            }
        });

        return doList();
    }

//    метод получения данных для Velocity
    public String doList() {
        ao.executeInTransaction(new TransactionCallback<Void>() // (1)
        {
            @Override
            public Void doInTransaction() {
                Propconfitem[] propconfitems = ao
                        .find(Propconfitem.class, " PROPCFID = ? ", customFieldId);

                if (propconfitems.length > 0) {
                    Propconfitem propconfitem = propconfitems[0];
                    source = propconfitem.getSourcefield();
                }

                return null;
            }
        });

        String save = request.getParameter("Save");
        if (save != null && save.equals(i18nBean.getText("tl.tutorial.save"))) {
//            редирект на стандартный конфиг
            setReturnUrl("/secure/admin/ConfigureCustomField!default.jspa?customFieldId=" + customFieldId);
            return getRedirect("not used");
        }
        return INPUT;
    }

//    список кастомных полей для Velocity
    public List<CustomField> getCfs() {
        List<CustomField> res = new ArrayList<>();
        for (CustomField cf : customFieldManager.getCustomFieldObjects()) {
            res.add(cf);
        }
        return res;
    }
}