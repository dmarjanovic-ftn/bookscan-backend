package dao

import javax.inject.{Inject, Singleton}

import models.User
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.driver.JdbcProfile

import scala.concurrent.Future

trait UserComponent { self: HasDatabaseConfigProvider[JdbcProfile] =>
  import driver.api._

  class Users(tag: Tag) extends Table[User](tag, "user") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def first_name = column[String]("first_name")
    def last_name = column[String]("last_name")
    def email = column[String]("email")
    def password = column[String]("password")

    def * = (id.?, first_name, last_name, email, password) <> ((User.apply _).tupled, User.unapply _)
  }
}


@Singleton()
class UserDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider) extends UserComponent
  with HasDatabaseConfigProvider[JdbcProfile] {
  import driver.api._

  private val users = TableQuery[Users]

  /** Retrieve a user from the id. */
  def findById(id: Long): Future[Option[User]] = {
    db.run(users.filter(_.id === id).result.headOption)
  }

  def findOneByEmail(email: String): Future[Option[User]] = {
    db.run(users.filter(_.email === email).result.headOption)
  }

  def findOneByEmailAndPassword(email: String, password: String): Future[Option[User]] = {
    db.run(users.filter(x => (x.email === email) && (x.password === password)).result.headOption)
  }

  /** Count all users. */
  def count(): Future[Int] = {
    db.run(users.map(_.id).length.result)
  }

  /** Insert a new user. */
  def insert(user: User): Future[Unit] = {
    db.run(users += user).map(_ => ())
  }

  /** Update a user. */
  def update(id: Long, user: User): Future[Unit] = {
    val userToUpdate: User = user.copy(Some(id))
    db.run(users.filter(_.id === id).update(userToUpdate)).map(_ => ())
  }

  /** Delete a user. */
  def delete(id: Long): Future[Unit] = {
    db.run(users.filter(_.id === id).delete).map(_ => ())
  }

  /** User exists */
  def exists(email : String) : Future[Boolean] =
    db.run(users.filter(_.email === email).exists.result)
}