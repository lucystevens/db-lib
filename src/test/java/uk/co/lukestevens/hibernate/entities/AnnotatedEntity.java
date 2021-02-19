package uk.co.lukestevens.hibernate.entities;

import javax.persistence.Entity;

//Class used for HibernateControllerTest
@Entity
public class AnnotatedEntity {
	int id;

	public AnnotatedEntity(int id) { this.id = id; }
	
}
