package ru.teamlead.jira.plugins.tutorial.rest;

import net.java.ao.Query;
import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.util.json.JSONArray;
import com.atlassian.jira.util.json.JSONException;
import com.atlassian.jira.util.json.JSONObject;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.sal.api.transaction.TransactionCallback;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import ru.teamlead.jira.plugins.tutorial.ao.Tutorialitem;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

//класс для приема REST вызовов
//часть пути вызова
// /rest/tutorial-rest/latest/table/tableItems
@Path("/table")
@AnonymousAllowed
public class TableRest {
    private static final Logger log = Logger.getLogger(TableRest.class);

    private final ActiveObjects ao;

    public TableRest(ActiveObjects _ao) {
        ao = _ao;
    }

//    получение записи таблицы по id методом GET
    @GET
//    имени пути нет - вместо пути - id
    @Path("{id}")
    public Response getItem(@PathParam("id") final Integer id) {
        final JSONObject result = new JSONObject();

//        получение записи из бд
        Boolean res = ao.executeInTransaction(new TransactionCallback<Boolean>() // (1)
        {
            @Override
            public Boolean doInTransaction() {
                Tutorialitem item = ao.get(Tutorialitem.class, id);

//                получаем значение поля по id поля
                String selectName = item.getSelect();
                if ("1".equals(item.getSelect())) {
                    selectName = "option1";
                } else if ("2".equals(item.getSelect())) {
                    selectName = "option2";
                }

//                записываем поля в ответ
                try {
                    result.put("id", item.getID());
                    result.put("select", item.getSelect());
                    result.put("name", item.getName());
                    result.put("checkbox", item.getCheckbox());
                    result.put("selectName", selectName);

                } catch (JSONException e) {
                    log.error(e, e);
                }

                return true;
            }
        });

//        формируем ответ на запрос
        return Response.ok(result.toString()).build();
    }

//    апдейт записи по id
    @PUT
    @Path("{id}")
    public Response updateItem(@PathParam("id") final Integer id,
//                               новые значения для полей записи
                               final Map<String, Object> params) {
        final JSONObject result = new JSONObject();

        String res = ao.executeInTransaction(new TransactionCallback<String>() // (1)
        {
            @Override
            public String doInTransaction() {
                Tutorialitem item = ao.get(Tutorialitem.class, id);

//                проставляем новые значения
//                item.setCreated(new Date());
                item.setName((String) params.get("name"));
                item.setSelect((String) params.get("select"));
                item.setCheckbox((Boolean) params.get("checkbox"));
                item.save();

                String selectName = item.getSelect();
                if ("1".equals(item.getSelect())) {
                    selectName = "option1";
                } else if ("2".equals(item.getSelect())) {
                    selectName = "option2";
                }

                try {
                    result.put("id", item.getID());
                    result.put("select", item.getSelect());
                    result.put("name", item.getName());
                    result.put("checkbox", item.getCheckbox());
                    result.put("selectName", selectName);

                } catch (JSONException e) {
                    log.error(e, e);
                }

                return "true";
            }
        });

//        валидация - сейчас всегда проходит
        if (!res.equals("true")) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("{\"errors\": {\"" + res + "\"}}")
                    .build();
        } else {
            return Response.ok(result.toString()).build();
        }
    }

//    создание записи
    @POST
    public Response createItem(final Map<String, Object> params) {
        final JSONObject result = new JSONObject();

        String res = ao.executeInTransaction(new TransactionCallback<String>() // (1)
        {
            @Override
            public String doInTransaction() {

//                вычисляем порядок следования записи
                Integer fieldorder = 0;
                Query query = Query.select().order("TABORDERN ASC");
                Tutorialitem[] exFieldSetRecord = ao.find(Tutorialitem.class, query);
                if (exFieldSetRecord.length > 0) {
                    Tutorialitem lastFieldSetRecord = exFieldSetRecord[0];
                    fieldorder = lastFieldSetRecord.getTabordern() - 1;
                }

                Tutorialitem item = ao.create(Tutorialitem.class);
                item.setTabordern(fieldorder);
                item.setCreated(new Date());
                item.setName((String) params.get("name"));
                item.setSelect((String) params.get("select"));
                item.setCheckbox((Boolean) params.get("checkbox"));
                item.save();

                String selectName = item.getSelect();
                if ("1".equals(item.getSelect())) {
                    selectName = "option1";
                } else if ("2".equals(item.getSelect())) {
                    selectName = "option2";
                }

                try {
                    result.put("id", item.getID());
                    result.put("select", item.getSelect());
                    result.put("name", item.getName());
                    result.put("checkbox", item.getCheckbox());
                    result.put("selectName", selectName);
                } catch (JSONException e) {
                    log.error(e, e);
                }

                return "true";
            }
        });

        if (!res.equals("true")) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("{\"errors\": {\"" + res + "\"}}")
                    .build();
        } else {
            return Response.ok(result.toString()).build();
        }
    }

//    удаление записи
    @DELETE
    @Path("{id}")
    public Response deleteItem(@PathParam("id") final Integer id) {
        final JSONArray result = new JSONArray();

        Boolean res = ao.executeInTransaction(new TransactionCallback<Boolean>() // (1)
        {
            @Override
            public Boolean doInTransaction() {
                Tutorialitem item = ao.get(Tutorialitem.class, id);
                ao.delete(item);
                return true;
            }
        });

        return Response.ok(result.toString()).build();
    }

//    список записей
    @GET
    @Path("/tableItems")
    @Produces({MediaType.APPLICATION_JSON})
    public Response tableItems() {
        final JSONArray result = new JSONArray();

        Boolean res = ao.executeInTransaction(new TransactionCallback<Boolean>() // (1)
        {
            @Override
            public Boolean doInTransaction() {
                Query query = Query.select().order("TABORDERN ASC");
                Tutorialitem[] items = ao.find(Tutorialitem.class, query);

                for (Tutorialitem item : items) {
                    String selectName = item.getSelect();
                    if ("1".equals(item.getSelect())) {
                        selectName = "option1";
                    } else if ("2".equals(item.getSelect())) {
                        selectName = "option2";
                    }

                    JSONObject ret = new JSONObject();
                    try {
                        ret.put("id", item.getID());
                        ret.put("select", item.getSelect());
                        ret.put("name", item.getName());
                        ret.put("checkbox", item.getCheckbox());
                        ret.put("selectName", selectName);
                    } catch (JSONException e) {
                        log.error(e, e);
                    }
                    result.put(ret);
                }

                return true;
            }
        });

        return Response.ok(result.toString()).build();
    }

//    меняем две записи местами в таблице
    @POST
    @Path("{id}/move")
    @Produces({MediaType.APPLICATION_JSON})
    public Response moveFieldTab(
//            id записи
            @PathParam("id") final Integer idTarget,
            final Map<String, String> params
    ) {
//        новая позиция записи - оибо абсолютная либо относительно другой записи
        final String position = params.get("position");
        final String after = params.get("after");

        final JSONArray result = new JSONArray();

        Boolean res = ao.executeInTransaction(new TransactionCallback<Boolean>() // (1)
        {
            @Override
            public Boolean doInTransaction() {
                Tutorialitem targetFieldSetRecord = ao.get(Tutorialitem.class, Integer.valueOf(idTarget));

                if (StringUtils.isNotEmpty(position) && "First".equals(position)) {
//                    Если абсолютная позиция
                    Query query = Query.select().order("TABORDERN ASC");
                    List<Tutorialitem> fieldSetRecords = Arrays.asList(ao.find(Tutorialitem.class, query));
                    Tutorialitem sourceFieldSetRecord = fieldSetRecords.get(0);
                    Integer tabOrderNumSource = sourceFieldSetRecord.getTabordern() - 1;
                    targetFieldSetRecord.setTabordern(tabOrderNumSource);
                    targetFieldSetRecord.save();
                } else if (StringUtils.isNotEmpty(after)) {
                    //если после другой записи
                    String[] parts = after.split("/");
                    if (parts.length < 1) {
                        return false;
                    }

                    String idSourceVal = parts[parts.length - 1];
                    Tutorialitem sourceFieldSetRecord = ao.get(Tutorialitem.class, Integer.parseInt(idSourceVal));

                    int tabOrderNumSource = sourceFieldSetRecord.getTabordern();
                    int tabOrderNumTarget = tabOrderNumSource + 1;
                    targetFieldSetRecord.setTabordern(tabOrderNumTarget);
                    targetFieldSetRecord.save();

                    Query query = Query.select().where(" TABORDERN >= ? AND ID <> ? ",
                            tabOrderNumTarget, targetFieldSetRecord.getID()).order("TABORDERN ASC");
                    List<Tutorialitem> fieldSetRecords = Arrays.asList(ao.find(Tutorialitem.class, query));

                    tabOrderNumTarget++;
                    for (Tutorialitem fieldSetRecord : fieldSetRecords) {
                        fieldSetRecord.setTabordern(tabOrderNumTarget);
                        fieldSetRecord.save();
                        tabOrderNumTarget++;
                    }
                } else {
                    return false;
                }

                return true;
            }
        });

        return Response.ok(result.toString()).build();
    }
}