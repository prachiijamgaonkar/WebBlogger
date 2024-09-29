
package com.jspiders.springmvc.dto;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "blogs")
@Data
public class WebBlogDTO implements  Comparable<WebBlogDTO>  {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@Column(nullable = false, unique = false)
	private String title;
	@Column(nullable = false, unique = false)
	private String content;
	@Column(nullable = false, unique = false)
	private Date date;
	@Column(nullable = false, unique = false)
	private String author;
//	The userId field in your WebBlogDTO class represents the unique identifier of the user who authored the blog post
	@Column(nullable = false, unique = false)
	private int userId;
	@Column(nullable = false,unique = false)
	private  String category;
	@Override
	public int compareTo(WebBlogDTO o) {
		if (this.date.after(o.date)) {
			return 1;
		} else if (this.date.before(o.date)) {
			return -1;
		} else {
			return 0;
		}
	}

}
