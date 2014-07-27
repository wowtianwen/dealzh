package com.junhong.forum.entity;

import java.util.List;

import javax.enterprise.context.Dependent;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.junhong.auth.entity.User;
import com.junhong.forum.constaint.CategoryNameUniqueness;

@Dependent
@Entity
@Table(name = "event")
@NamedQueries({ @NamedQuery(name = "event.all", query = "select distinct e from Event as e") })
@CategoryNameUniqueness
public class Event extends AbstractEntity {
    private static final long serialVersionUID = -3407173016363186021L;
    /* ------------instance Variable-------------- */
    @NotNull
    @Size(min = 3, message = "{com.junhong.forum.entity.forumCategoryName}")
    private String name;
    @Column(length = 1000)
    private String description;
    private int numOfThread;
    // the owner of the category
    @ManyToOne
    @JoinColumn(name = "owner_Id")
    private User owner = new User();
    @OneToMany(mappedBy = "event", cascade = { CascadeType.REMOVE })
    private List<ForumThread> threadList;

    /* -------------constructor----------------- */
    public Event() {
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    public int getNumOfThread() {
	return numOfThread;
    }

    public void setNumOfThread(int numOfThread) {
	this.numOfThread = numOfThread;
    }

    public User getOwner() {
	return owner;
    }

    public void setOwner(User owner) {
	this.owner = owner;
    }

    public List<ForumThread> getThreadList() {
	return threadList;
    }

    public void setThreadList(List<ForumThread> threadList) {
	this.threadList = threadList;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = super.hashCode();
	result = prime * result + ((name == null) ? 0 : name.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (getClass() != obj.getClass())
	    return false;
	Event other = (Event) obj;
	if (name == null) {
	    if (other.name != null)
		return false;
	} else if (!name.equals(other.name))
	    return false;
	return true;
    }

    /* -------------business logic----------------- */

}
