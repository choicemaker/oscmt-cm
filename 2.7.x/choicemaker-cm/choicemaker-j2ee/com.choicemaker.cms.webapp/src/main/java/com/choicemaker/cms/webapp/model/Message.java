package com.choicemaker.cms.webapp.model;

import java.io.Serializable;

/** @deprecated moved to cm-server-web4 module*/
@Deprecated
public class Message implements Serializable {


	private static final long serialVersionUID = 1L;

	//@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	//@Column(nullable = false) @Lob
	private /*@NotNull*/ String text = "";

	public Long getId() {
		return id;
	}

	protected void setId(Long id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
