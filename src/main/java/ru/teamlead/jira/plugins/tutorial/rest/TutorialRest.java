package ru.teamlead.jira.plugins.tutorial.rest;

import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.customfields.impl.DateCFType;
import com.atlassian.jira.issue.customfields.impl.DateTimeCFType;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.search.SearchResults;
import com.atlassian.jira.propertyset.JiraPropertySetFactory;
import com.atlassian.jira.rest.v1.model.errors.ValidationError;
import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.IssueTypeManager;
import com.atlassian.jira.config.StatusManager;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.fields.screen.FieldScreenManager;
import com.atlassian.jira.issue.security.IssueSecurityLevelManager;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.rest.api.messages.TextMessage;
import com.atlassian.jira.security.GlobalPermissionManager;
import com.atlassian.jira.security.groups.GroupManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.jira.util.I18nHelper;
import com.atlassian.jira.util.JiraDurationUtils;
import com.atlassian.jira.util.json.JSONArray;
import com.atlassian.jira.util.json.JSONException;
import com.atlassian.jira.util.json.JSONObject;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.jira.workflow.WorkflowManager;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.opensymphony.module.propertyset.PropertySet;
import net.java.ao.Query;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import ru.teamlead.jira.plugins.tutorial.ao.Tutorialitem;
import ru.teamlead.jira.plugins.tutorial.listener.IssueVO;
import ru.teamlead.jira.plugins.tutorial.listener.TutorialUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static com.atlassian.jira.rest.v1.util.CacheControl.NO_CACHE;

/**
 * @author V.Nikolaev
 * @version $Revision$
 */

//вызов REST
// /rest/tutorial-rest/latest/tutorial/myitems?param1=nothing

@Path("/tutorial")
@AnonymousAllowed
public class TutorialRest {
    private static final Logger log = Logger.getLogger(TutorialRest.class);

    protected static final String QUERY_STRING = "searchFilter";

    private final WorkflowManager workflowManager;
    private final GroupManager groupManager;
    private final ActiveObjects ao;
    private final I18nHelper i18nBean;
    private final ProjectManager projectManager;
    private final CustomFieldManager customFieldManager;
    private final IssueTypeManager issueTypeManager;
    private final StatusManager statusManager;
    private final IssueSecurityLevelManager issueSecurityLevelManager;
    private final FieldScreenManager fieldScreenManager;
    private final UserManager userManager;
    private final IssueManager issueManager;
    private final ApplicationProperties applicationProperties;
    private final GlobalPermissionManager globalPermissionManager;
    protected static PropertySet ps;
    private final JiraPropertySetFactory jiraPropertySetFactory;
    private final SearchService searchService;
    private final TutorialUtils tutorialUtils;

    public TutorialRest(WorkflowManager workflowManager, GroupManager groupManager, ActiveObjects _ao,
                        ProjectManager projectManager,
                        CustomFieldManager customFieldManager, IssueTypeManager issueTypeManager,
                        StatusManager statusManager, IssueSecurityLevelManager issueSecurityLevelManager,
                        FieldScreenManager fieldScreenManager, UserManager userManager, IssueManager issueManager,
                        ApplicationProperties applicationProperties, GlobalPermissionManager globalPermissionManager,
                        JiraPropertySetFactory jiraPropertySetFactory, SearchService searchService, TutorialUtils tutorialUtils) {
        this.workflowManager = workflowManager;
        this.groupManager = groupManager;
        ao = _ao;
        this.projectManager = projectManager;
        this.customFieldManager = customFieldManager;
        this.issueTypeManager = issueTypeManager;
        this.statusManager = statusManager;
        this.issueSecurityLevelManager = issueSecurityLevelManager;
        this.fieldScreenManager = fieldScreenManager;
        this.userManager = userManager;
        this.issueManager = issueManager;
        this.applicationProperties = applicationProperties;
        this.globalPermissionManager = globalPermissionManager;
        this.jiraPropertySetFactory = jiraPropertySetFactory;
        this.searchService = searchService;
        this.tutorialUtils = tutorialUtils;
        this.i18nBean = ComponentAccessor.getJiraAuthenticationContext().getI18nHelper();

        ps = this.jiraPropertySetFactory.buildCachingPropertySet("tutorial-ps.properties", 1l);
    }


    //    список задач для страницы /secure/myissues.jspa
    @GET
    @Path("/myitems")
    @Produces({MediaType.APPLICATION_JSON})
    public Response myitems(
            @QueryParam("param1") final String param1
    ) throws JSONException {
        final JSONArray ret = new JSONArray();

        ApplicationUser user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();

//        запрос JQL
        final StringBuilder jqlQuery = new StringBuilder();

//        только назначенные на текущего пользователя
        jqlQuery.append(" assignee = \"");
        jqlQuery.append(user.getName());
        jqlQuery.append("\" ");

        log.warn("myitems jqlQuery " + jqlQuery.toString());

//        поиск задач
        final SearchService.ParseResult parseResult = searchService.parseQuery(user, jqlQuery.toString());
        if (parseResult.isValid()) {
            try {
                long count = searchService.searchCount(user, parseResult.getQuery());
                if (count > 0) {
                    SearchResults results = searchService.search(user, parseResult.getQuery(), PagerFilter.newPageAlignedFilter(0, 20));
//                    List<Issue> issues = results.getIssues();
                    List<Issue> issues = getIssuesFromSearchResults(results);

//                    формирование ответа с полями задач
                    for (Issue issue : issues) {
                        final JSONObject result = new JSONObject();

                        result.put("id", issue.getId());
                        result.put("userEmail", issue.getReporter().getEmailAddress());
                        result.put("ts", issue.getCreated());
                        result.put("issueKey", issue.getKey());
                        result.put("issueSummary", issue.getSummary());
                        result.put("message", issue.getDescription());

                        ret.put(result);
                    }
                }
            } catch (Exception e) {
                log.error(e, e);
            }
        }

        return Response.ok(ret.toString()).build();
    }


    public List<Issue> getIssuesFromSearchResults(SearchResults results) {
        List<Issue> issues = new ArrayList<>();
        try {
            Method newGetMethod = null;

            try {
                newGetMethod = SearchResults.class.getMethod("getIssues");
            } catch (NoSuchMethodException e) {
                try {
                    if (log.isInfoEnabled()) {
                        log.info("SearchResults.getIssues does not exist - trying to use getResults!");
                    }
                    newGetMethod = SearchResults.class.getMethod("getResults");
                } catch (NoSuchMethodError e2) {
                    log.error("SearchResults.getResults does not exist!");
                }
            }

            if (newGetMethod != null) {
                issues = (List<Issue>) newGetMethod.invoke(results);
            } else {
                log.error("ERROR NO METHOD TO GET ISSUES !");
                throw new RuntimeException("ICT: SearchResults Service from JIRA NOT AVAILABLE (getIssue / getResults)");
            }
        } catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            log.error("Jql Helper can net get search result (ICT)", e);
        } catch (Exception e) {
            log.error("Jql Helper can net get search result - other exception (ICT)", e);
        }

        return issues;
    }


    //    получение списка записей из бд
    @GET
    @Path("/products")
    @Produces({MediaType.APPLICATION_JSON})
    public Response products(
            @QueryParam("searchFilter") final String searchFilter
    ) throws JSONException {
        final JSONArray ret = new JSONArray();
        Boolean res = ao.executeInTransaction(new TransactionCallback<Boolean>() // (1)
        {
            @Override
            public Boolean doInTransaction() {

//                поиск записей из таблицы
                Tutorialitem[] items = ao.find(Tutorialitem.class, Query.select().order("TABORDERN ASC"));

                for (Tutorialitem item : items) {
                    final JSONObject result = new JSONObject();
                    String selectName = item.getSelect();
                    if ("1".equals(item.getSelect())) {
                        selectName = "option1";
                    } else if ("2".equals(item.getSelect())) {
                        selectName = "option2";
                    }

                    JSONArray cell = new JSONArray();
                    try {
                        cell.put(item.getName());
                        cell.put(selectName);
                        cell.put(item.getCheckbox());

                        result.put("id", item.getID());
                        result.put("cell", cell);
                    } catch (JSONException e) {
                        log.error(e, e);
                    }

                    ret.put(result);
                }

                return true;
            }
        });

        List<IssueVO> issues = tutorialUtils.getIssuesVo(searchFilter);
        if (issues != null) {
            for (IssueVO issue : issues) {
                JSONArray cell = new JSONArray();
                final JSONObject result = new JSONObject();
                cell.put(issue.getSummary());
                cell.put("<a href='' target='blank'>" + issue.getKey() + "</a>");
                cell.put(issue.getDiff());

                result.put("id", issue.getId());
                result.put("cell", cell);

                ret.put(result);
            }
        }

//        List<Issue> issues = tutorialUtils.getIssues(searchFilter);

//        if (issues != null) {
////            issues.stream().map(issue -> )
//
//            CustomField cfstart = customFieldManager.getCustomFieldObject("customfield_10124");
//            CustomField cffinish = customFieldManager.getCustomFieldObject("customfield_10125");
//            JiraDurationUtils jiraDurationUtils = ComponentAccessor.getJiraDurationUtils();
//
//            for (Issue issue : issues) {
//                Date start = (Date) issue.getCustomFieldValue(cfstart);
//                Date finish = (Date) issue.getCustomFieldValue(cffinish);
//
//                if (start == null || finish == null) {
//                    continue;
//                }
//
//                Long diff = finish.getTime() - start.getTime();
//                String diffFmt = jiraDurationUtils.getShortFormattedDuration(diff);
//
//                JSONArray cell = new JSONArray();
//                final JSONObject result = new JSONObject();
//                cell.put(issue.getSummary());
//                cell.put("<a href='' target='blank'>" + issue.getKey() + "</a>");
//                cell.put(diffFmt);
//
//                result.put("id", issue.getId());
//                result.put("cell", cell);
//
//                ret.put(result);
//            }
//        }

        final JSONObject rows = new JSONObject();
        rows.put("rows", ret);
        return Response.ok(rows.toString()).build();
    }

    //    Валидация для гаджета
    @GET
    @Path("/pvalidate")
    public Response validateChart(@QueryParam(QUERY_STRING) String queryString) {
        Collection<ValidationError> errors = new ArrayList<>();
        return createValidationResponse(errors);
    }

    //    Сейчас всегда проходит успешно
    protected Response createValidationResponse(Collection<ValidationError> errors) {
        return Response.ok(new TextMessage("No input validation errors found.")).cacheControl(NO_CACHE).build();
    }

    //    сохранение настройки из админки
    @GET
    @Path("/setSettings")
    @Produces({MediaType.APPLICATION_JSON})
    public Response setSettings(
            @QueryParam("val") final String val,
            @QueryParam("start") final String start,
            @QueryParam("finish") final String finish
    ) throws JSONException {
        final JSONObject ret = new JSONObject();

        long timestamp = System.currentTimeMillis();

        Long test = null;

        log.warn("setSettings val start " + val + " , start = " + start + " , finish = " + finish
                + " : " + new Date());

//        через Property Set - набор ключ-значение
        ps.setText("test.val", val);
        ps.setString("test.start", start);
        ps.setString("test.finish", finish);

        log.warn("setSettings val finish: " + val + " , start = " + start + " , finish = " + finish
                + " : " + new Date() + " , took time = " + (timestamp - System.currentTimeMillis() / test ));

        ret.put("success", true);
        return Response.ok(ret.toString()).build();
    }

    //    получение настройки из админки
    @GET
    @Path("/getSettings")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getSettings() throws JSONException {
        final JSONObject ret = new JSONObject();
        ret.put("val", ps.exists("test.val") ? ps.getText("test.val") : "");
        return Response.ok(ret.toString()).build();
    }


    @GET
    @Path("/initCf")
    @Produces({MediaType.APPLICATION_JSON})
    public Response initCf(
            @QueryParam("customfieldId") final String customfieldId,
            @QueryParam("sourceCFId") final String sourceCFId
    ) throws JSONException {
        final JSONObject ret = new JSONObject();
        ret.put("customfieldId", customfieldId);
        ret.put("sourceCFId", sourceCFId);
        return Response.ok(ret.toString()).build();
    }

    //    Поиск записей по введенному значению для кастомного поля
    @GET
    @Path("/searchCfRecords")
    @Produces({MediaType.APPLICATION_JSON})
    public Response searchCfRecords(
            @QueryParam("customfieldId") final String customfieldId,
            @QueryParam("query") final String query
    ) throws JSONException {
        final JSONObject result = new JSONObject();
        final Map<String, Map<String, Object>> entities = new TreeMap<>();
        final Map<String, Object> retMap = new HashMap<>();

        retMap.put("groups", entities.values());

//        поиск записей в таблице
        Tutorialitem[] items = ao.find(Tutorialitem.class, " NAME LIKE ? ", "%" + query + "%");
        for (Tutorialitem item : items) {
            Map<String, Object> childVO = new HashMap<>();
            childVO.put("name", item.getName());
            childVO.put("html", item.getName());
            entities.put(item.getName(), childVO);
        }

//        Map<String, Object> childVO = new HashMap<>();
//        childVO.put("name", "-10000");
//        childVO.put("html", "Not defined");
//        entities.put("-10000", childVO);

        try {
            result.put("tt", retMap);
        } catch (JSONException e) {
            log.error(e, e);
        }

//        формирование ответа
        String res = new String(result.toString().substring(6));
        return Response.ok(res.substring(0, res.length() - 1)).build();
    }


    @GET
    @Path("/dateFields")
    @Produces({MediaType.APPLICATION_JSON})
    public Response dateFields(
            @QueryParam("query") final String titleVal
//            @QueryParam("exclude") final String exclude,
//            @QueryParam("exclude1") final String exclude1
    ) throws JSONException {
//        final JSONObject result = new JSONObject();
        final JSONArray entities = new JSONArray();
        final JSONObject retMap = new JSONObject();

        for (CustomField cf : customFieldManager.getCustomFieldObjects()) {
            if (cf.getCustomFieldType() instanceof DateTimeCFType
                    || DateTimeCFType.class.isAssignableFrom(cf.getCustomFieldType().getClass())
                    || cf.getCustomFieldType() instanceof DateCFType
                    || DateCFType.class.isAssignableFrom(cf.getCustomFieldType().getClass())) {

//                if (cf.getName().equals(exclude) || cf.getName().equals(exclude1)
//                        || cf.getId().equals(exclude) || cf.getId().equals(exclude1)) {
//                    continue;
//                }

                if (StringUtils.isNotEmpty(titleVal) && !cf.getName().toLowerCase().contains(titleVal.toLowerCase())) {
                    continue;
                }

                JSONObject childVO = new JSONObject();
                childVO.put("name", cf.getId());
                childVO.put("html", cf.getName());
                entities.put(childVO);
            }
        }

        retMap.put("groups", entities);
        retMap.put("total", entities.length());

//        result.put("tt", retMap);

        return Response.ok(retMap.toString()).build();

//        String res = new String(result.toString().substring(6));
//        return Response.ok(res.substring(0, res.length() - 1)).build();
    }
}