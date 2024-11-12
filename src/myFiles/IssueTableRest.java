package ru.teamlead.jira.plugins.tutorial.rest;

import com.atlassian.jira.util.json.JSONArray;
import com.atlassian.jira.util.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.teamlead.jira.plugins.tutorial.ao.IssueTable;
import ru.teamlead.jira.plugins.tutorial.service.IssueTableService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/issue-table")
public class IssueTableRest {

    private static final Logger log = LoggerFactory.getLogger(IssueTableRest.class);
    private final IssueTableService issueTableService;

    public IssueTableRest(IssueTableService issueTableService) {
        this.issueTableService = issueTableService;
    }

    @GET
    @Path("/tables")
    @Produces(MediaType.APPLICATION_JSON)
    public Response tables() {
        IssueTable[] tables = issueTableService.getAllIssueTables();
        JSONObject result = new JSONObject();
        log.info("start collecting IssueTab");

        try {
            JSONArray tablesArray = new JSONArray();
            for (IssueTable table : tables) {
                log.info(table.toString());
                JSONObject tableJson = new JSONObject();
                tableJson.put("Name", table.getName());
                tablesArray.put(tableJson);
            }
            result.put("IssueTables", tablesArray);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        log.info("data collected");
        return Response.ok(result.toString()).build();
    }

    @POST
    @Path("/add")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addTable(String object) {
        try {
            IssueTable newTable = issueTableService.createIssueTable(new JSONObject(object).get("name").toString());
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("id", newTable.getID());
            jsonResponse.put("name", newTable.getName());
            return Response.ok(jsonResponse.toString()).build();
        } catch (Exception e) {
            log.error("Error adding table: ", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
}
