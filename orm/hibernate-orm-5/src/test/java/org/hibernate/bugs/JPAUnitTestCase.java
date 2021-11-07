package org.hibernate.bugs;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.hibernate.entities.Author;
import org.hibernate.entities.Book;
import org.hibernate.entities.Chapter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * This template demonstrates how to develop a test case for Hibernate ORM, using the Java Persistence API.
 */
public class JPAUnitTestCase {

	private EntityManagerFactory entityManagerFactory;

	@Before
	public void init() {
		entityManagerFactory = Persistence.createEntityManagerFactory( "templatePU" );
	}

	@After
	public void destroy() {
		entityManagerFactory.close();
	}

	// Entities are auto-discovered, so just add them anywhere on class-path
	// Add your tests, using standard JUnit.
	@Test
	public void hhh123Test() throws Exception {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();
		// Do stuff...
		entityManager.getTransaction().commit();
		entityManager.close();
	}

	@Test
	public void joinOnFetchNotWorking() throws Exception {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		// Insert data
		Chapter chapter = new Chapter();
		chapter.name = "Overview of HTTP";
		
		Book book = new Book();
		book.name = "HTTP Definitive guide";

		Author author = new Author();
		author.name = "David Gourley";

		book.chapters.add(chapter);
		author.books.add(book);

		chapter.book = book;
		book.author = author;

		entityManager.persist(author);

		// entityManager.createQuery
		// Author persistedAuthor = entityManager
		// 		.createQuery("SELECT author FROM Author author", Author.class).getSingleResult();
		// System.out.println(persistedAuthor.name);

		// CriteriaQuery<Author> query = new Criter
		// entityManager.createQuer
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Author> query = builder.createQuery(Author.class);

		Root<Author> root = query.from(Author.class);
		// ListJoin<Author, Book> authorBookJoin = root.joinList("books", JoinType.LEFT);
		ListJoin<Author, Book> authorBookJoin =  (ListJoin)root.fetch("books", JoinType.LEFT);

		ListJoin<Book, Chapter> bookChapterJoin = authorBookJoin.joinList("chapters", JoinType.LEFT);

		Predicate finalPredicate = builder.equal(bookChapterJoin.get("name"), "Overview of HTTP");
		query.where(finalPredicate);

		Author persistedAuthor = entityManager.createQuery(query).getSingleResult();

		System.out.println(persistedAuthor.name);
		persistedAuthor.books.forEach(b -> {
			System.out.println(b.name);
		});

		entityManager.getTransaction().commit();
		entityManager.close();
	}
}
