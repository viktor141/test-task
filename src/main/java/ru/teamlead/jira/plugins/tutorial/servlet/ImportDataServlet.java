package ru.teamlead.jira.plugins.tutorial.servlet;

import com.atlassian.jira.issue.customfields.manager.OptionsManager;
import com.atlassian.jira.issue.customfields.option.Option;
import com.atlassian.jira.issue.customfields.option.Options;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.atlassian.jira.issue.fields.config.manager.FieldConfigManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.cluster.ClusterMessagingService;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.customfields.persistence.CustomFieldValuePersister;
import com.atlassian.jira.issue.index.IssueIndexManager;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.security.groups.GroupManager;
import com.atlassian.jira.user.util.UserManager;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

import java.io.File;

import org.w3c.dom.*;

/**
 * @author V.Nikolaev
 * @version $Revision$
 */
//импорт файла с формы
public class ImportDataServlet extends HttpServlet {
    protected final Logger log = Logger.getLogger(this.getClass());

    private final ActiveObjects ao;
    private final UserManager userManager;
    private final IssueIndexManager issueIndexManager;
    private final ProjectManager projectManager;
    private final CustomFieldManager customFieldManager;
    private final SearchService searchService;
    private final IssueManager issueManager;
    private final CustomFieldValuePersister customFieldValuePersister;
    private final ClusterMessagingService clusterMessagingService;
    private final GroupManager groupManager;
    private final OptionsManager optionsManager;
    private final FieldConfigManager fieldConfigManager;

    public ImportDataServlet(
            ActiveObjects ao,
            UserManager userManager,
            IssueIndexManager issueIndexManager,
            ProjectManager projectManager,
            CustomFieldManager customFieldManager,
            SearchService searchService,
            IssueManager issueManager,
            CustomFieldValuePersister customFieldValuePersister,
            ClusterMessagingService clusterMessagingService,
            GroupManager groupManager, OptionsManager optionsManager, FieldConfigManager fieldConfigManager) {
        this.ao = ao;
        this.userManager = userManager;
        this.issueIndexManager = issueIndexManager;
        this.projectManager = projectManager;
        this.customFieldManager = customFieldManager;
        this.searchService = searchService;
        this.issueManager = issueManager;
        this.customFieldValuePersister = customFieldValuePersister;
        this.clusterMessagingService = clusterMessagingService;
        this.groupManager = groupManager;
        this.optionsManager = optionsManager;
        this.fieldConfigManager = fieldConfigManager;
    }

//    путь к папке хранения временных файлов в ОС
    protected static final String tmpPath = System.getProperty("java.io.tmpdir");

//    точка входа в сервлет
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
        File file = null;

        try {

            File tmpDir = new File(tmpPath);

            DiskFileItemFactory fileItemFactory = new DiskFileItemFactory();
            fileItemFactory.setSizeThreshold(1 * 1024 * 1024); //1 MB
            fileItemFactory.setRepository(tmpDir);

            ServletFileUpload uploadHandler = new ServletFileUpload(fileItemFactory);

//            получаем файл из парметров запроса
            List items = null;
            try {
                items = uploadHandler.parseRequest(request);
            } catch (FileUploadException e) {
                log.error(e, e);
                throw new IOException(e);
            }

            Iterator itr = items.iterator();

//            кастомное поле
            String customFieldId = "";
            String fieldConfigSchemeId = "";
            String fieldConfigId = "";

//            fieldConfigSchemeId=10402
// fieldConfigId=10402
// customFieldId=10302
            while (itr.hasNext()) {
                FileItem item = (FileItem) itr.next();
                if (item.isFormField()) {
                    if ("customFieldId".equals(item.getFieldName())) {
                        customFieldId = item.getString();
                    } else if ("fieldConfigId".equals(item.getFieldName())) {
                        fieldConfigId = item.getString();
                    } else if ("fieldConfigSchemeId".equals(item.getFieldName())) {
                        fieldConfigSchemeId = item.getString();
                    }
                } else {
                    try {
//                        файл
                        if (StringUtils.isNotEmpty(item.getName())) {
                            File tmpFile = new File(item.getName());
                            file = new File(tmpPath, tmpFile.getName());
                            item.write(file);
                        }
                    } catch (Exception e) {
                        log.error(e, e);
                    }
                }
            }

//            парсинг файла xml
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new FileInputStream(file));
            doc.getDocumentElement().normalize();

            log.warn("Root element of the doc is " + doc.getDocumentElement().getNodeName());

//            получение списка узлов по имени тэга
            NodeList listOfPersons = doc.getElementsByTagName("person");
            int totalPersons = listOfPersons.getLength();

            log.warn("Total no of people : " + totalPersons);

//            CustomField cf = customFieldManager.getCustomFieldObject(Long.parseLong(customFieldId));

//            пробегаем по списку узлов
            for (int s = 0; s < listOfPersons.getLength(); s++) {
                Node firstPersonNode = listOfPersons.item(s);
                if (firstPersonNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element firstPersonElement = (Element) firstPersonNode;

                    //-------
                    NodeList firstNameList = firstPersonElement.getElementsByTagName("first");
                    Element firstNameElement = (Element) firstNameList.item(0);

                    NodeList textFNList = firstNameElement.getChildNodes();
                    log.warn("First Name : " +
                            ((Node) textFNList.item(0)).getNodeValue().trim());

                    String fn = ((Node) textFNList.item(0)).getNodeValue().trim();

                    //-------
                    NodeList lastNameList = firstPersonElement.getElementsByTagName("last");
                    Element lastNameElement = (Element) lastNameList.item(0);

                    NodeList textLNList = lastNameElement.getChildNodes();
                    log.warn("Last Name : " +
                            ((Node) textLNList.item(0)).getNodeValue().trim());

                    //----
                    NodeList ageList = firstPersonElement.getElementsByTagName("age");
                    Element ageElement = (Element) ageList.item(0);

                    NodeList textAgeList = ageElement.getChildNodes();
                    log.warn("Age : " +
                            ((Node) textAgeList.item(0)).getNodeValue().trim());

                    //------

//                    customFieldManager
//                    optionsManager.createOption();
//                    IssueContextImpl issueContext = new IssueContextImpl(issue.getProjectObject().getId(), issue.getIssueTypeObject().getId());
                    //                    FieldConfig fieldConfig = cf.getRelevantConfig(issueContext);

//                    получение конфигурации поля
                    FieldConfig fieldConfig = fieldConfigManager.getFieldConfig(Long.parseLong(fieldConfigId));

//                    поиск опции по имени
                    Options options = optionsManager.getOptions(fieldConfig);
                    Option option = options.getOptionForValue(fn, null);
                    if (option == null) {
//                        добавление новой опции
                        options.addOption(null, fn);
                    }

                }//end of if clause


            }//end of for loop with s var

        } catch (Exception e) {
            log.error(e, e);
            PrintWriter writer = resp.getWriter();

//            ответ на странице после отработки вызова при ошибке
            writer.append("<script type='text/javascript'>");
            writer.append("  parent.document.getElementById('import-status').value=1;");
            writer.append("</script>");

            writer.flush();
            return;
        }


        PrintWriter writer = resp.getWriter();
        writer.append("<script type='text/javascript'>");

//            ответ на странице после отработки вызова
        if (file == null) {
            writer.append("  parent.document.getElementById('import-status').value=3;");
        } else {
            writer.append("  parent.document.getElementById('import-status').value=2;");
        }

        writer.append("</script>");
        writer.flush();
    }
}