package ru.teamlead.jira.plugins.tutorial.ao;

import net.java.ao.Entity;

/**
 * @author V.Nikolaev
 * @version $Revision$
 */
//имя таблицы
///plugins/servlet/active-objects/tables/list
//AO_DAAA07_PROPCONFITEM
//    настройки кастомного поля
public interface Propconfitem extends Entity {

//    поле в таблице
//    id кастомного поля
    String getPropcfid();

    void setPropcfid(String value);


    //    поле в таблице
//    настройка кастомного поля - id связанного поля
    String getSourcefield();

    void setSourcefield(String value);
}