package ru.teamlead.jira.plugins.tutorial.postfunction;

import java.util.*;

import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory;
import com.atlassian.jira.plugin.workflow.WorkflowPluginFunctionFactory;
import com.atlassian.jira.util.I18nHelper;
import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.FunctionDescriptor;
import org.apache.log4j.Logger;

/**
 * @author V.Nikolaev
 * @version $Revision$
 */
public class TutorialFunctionFactory extends AbstractWorkflowPluginFactory implements WorkflowPluginFunctionFactory {
    private static final Logger log = Logger.getLogger(TutorialFunctionFactory.class);

    protected CustomFieldManager customFieldManager;
    protected IssueManager issueManager;
    private final I18nHelper i18nBean;

    public TutorialFunctionFactory(
            IssueManager issueManager,
            CustomFieldManager customFieldManager,
            I18nHelper i18nBean
    ) {
        this.issueManager = issueManager;
        this.customFieldManager = customFieldManager;
        this.i18nBean = i18nBean;
    }


    @Override
    protected void getVelocityParamsForInput(Map<String, Object> velocityParams) {
        Set<CustomField> fields = new TreeSet<>();
        for (CustomField cf : customFieldManager.getCustomFieldObjects()) {
            fields.add(cf);
        }

        velocityParams.put("fields", fields);

        velocityParams.put("sourceField", "");
        velocityParams.put("sourceFieldName", "");
    }


    @Override
    protected void getVelocityParamsForEdit(Map<String, Object> velocityParams, AbstractDescriptor descriptor) {
        getVelocityParamsForInput(velocityParams);
        getVelocityParamsForView(velocityParams, descriptor);
    }

    @Override
    protected void getVelocityParamsForView(Map<String, Object> velocityParams, AbstractDescriptor descriptor) {
        if (!(descriptor instanceof FunctionDescriptor)) {
            throw new IllegalArgumentException("Descriptor must be a FunctionDescriptor.");
        }

        FunctionDescriptor functionDescriptor = (FunctionDescriptor) descriptor;

        String sourceField = (String) functionDescriptor.getArgs().get("sourceField");
        velocityParams.put("sourceField", sourceField);

        String sourceFieldName = (String) functionDescriptor.getArgs().get("sourceFieldName");
        velocityParams.put("sourceFieldName", sourceFieldName);
    }


    public Map getDescriptorParams(Map formParams) {
        Map params = new HashMap();

        String sourceField = extractSingleParam(formParams, "sourceField");
        params.put("sourceField", sourceField == null ? "" : sourceField);

        String sourceFieldName = "";
        CustomField slaCFFields = customFieldManager.getCustomFieldObject(sourceField);
        if (slaCFFields != null) {
            sourceFieldName = slaCFFields.getName();
        }

        params.put("sourceFieldName", sourceFieldName);

        return params;
    }
}