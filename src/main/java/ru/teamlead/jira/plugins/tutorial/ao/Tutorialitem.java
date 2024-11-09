package ru.teamlead.jira.plugins.tutorial.ao;

import net.java.ao.Entity;
import net.java.ao.schema.StringLength;

import java.util.Date;

/**
 * @author V.Nikolaev
 * @version $Revision$
 */
//AO_DAAA07_TUTORIALITEM
//
public interface Tutorialitem extends Entity {
    Date getCreated();

    void setCreated(Date value);


    Boolean getCheckbox();

    void setCheckbox(Boolean value);


//    Длина поля
    @StringLength(value = StringLength.MAX_LENGTH)
    String getName();

    @StringLength(value = StringLength.MAX_LENGTH)
    void setName(String value);


    @StringLength(value = StringLength.MAX_LENGTH)
    String getSelect();

    @StringLength(value = StringLength.MAX_LENGTH)
    void setSelect(String value);


    Integer getTabordern();

    void setTabordern(Integer val);
}