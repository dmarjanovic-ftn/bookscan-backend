package dao

import javax.inject.{Inject, Singleton}

import models.{Book}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import play.api.libs.concurrent.Execution.Implicits.defaultContext


import scala.concurrent.Future


trait BookComponent { self: HasDatabaseConfigProvider[JdbcProfile] =>
  import driver.api._

  class Books(tag: Tag) extends Table[Book](tag, "books") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def title = column[String]("title")
    def author = column[String]("author")
    def language = column[String]("language")
    def year = column[String]("year")
    def publisher = column[String]("publisher")

    def * = (id.?, title, author, language, year, publisher) <> ((Book.apply _).tupled, Book.unapply _)
  }
}


@Singleton()
class BookDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider) extends BookComponent
  with HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  private val books = TableQuery[Books]

  /** Retrieve a book from the id. */
  def findById(id: Long): Future[Option[Book]] = {
    db.run(books.filter(_.id === id).result.headOption)
  }

  /** Insert a new book. */
  def insert(book: Book): Future[Unit] = {
    db.run(books += book).map(_ => ())
  }

  /** Update a book. */
  def update(id: Long, book: Book): Future[Unit] = {
    val bookToUpdate: Book = book.copy(Some(id))
    db.run(books.filter(_.id === id).update(bookToUpdate)).map(_ => ())
  }

  /** Delete a book. */
  def delete(id: Long): Future[Unit] = {
    db.run(books.filter(_.id === id).delete).map(_ => ())
  }
}