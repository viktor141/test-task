package ru.teamlead.jira.plugins.tutorial.ao;

import net.java.ao.Entity;

public interface IssueTable extends Entity {
    String getName();
    void setName(String name);
}
