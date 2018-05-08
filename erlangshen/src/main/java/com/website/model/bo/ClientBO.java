package com.website.model.bo;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

import com.fastjavaframework.base.BaseBean;

/**
 * @author https://github.com/shuli495/erlangshen
 */
public class ClientBO extends BaseBean {
	private static final long serialVersionUID = 1L;

	//client key
	private String id;

	//client名称
	@NotNull(message="{client.name.null}")
	@Size(max=64, min=0, message="{client.name.size}")
	private String name;

	// 创建人
	@NotNull(message="{client.createdBy.null}")
	@Size(max=64, min=0, message="{client.createdBy.size}")
	private String createdBy;

	//是否删除 0未删除 1删除
	@NotNull(message="{client.deleted.null}")
	private Boolean deleted;

	private Date deletedTime;	


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	public Date getDeletedTime() {
		return deletedTime;
	}

	public void setDeletedTime(Date deletedTime) {
		this.deletedTime = deletedTime;
	}
}