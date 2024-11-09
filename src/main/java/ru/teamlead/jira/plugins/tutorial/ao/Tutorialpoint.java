package ru.teamlead.jira.plugins.tutorial.ao;

import net.java.ao.Entity;
import net.java.ao.Preload;
import net.java.ao.schema.Indexed;

/**
 * @author V.Nikolaev
 * @version $Revision$
 */
@Preload
//AO_DAAA07_TUTORIALPOINT
public interface Tutorialpoint extends Entity {
    String getName();

    void setName(String value);


    @Indexed
    Long getTnumber();

    @Indexed
    void setTnumber(Long value);
}