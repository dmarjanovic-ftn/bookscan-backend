package models

import play.api.libs.json._
import play.api.libs.functional.syntax._

case class User(id: Option[Long] = None, firstName: String, lastName: String, email: String, password: String)

object User{
  implicit val implicitUserWrites = new Writes[User] {
    def writes(user: User): JsValue = {
      Json.obj(
        "firstName" -> user.firstName,
        "lastName" -> user.lastName,
        "email" -> user.email
      )
    }
  }

  implicit val implicitUserReads: Reads[User] = (
      (JsPath \ "id").readNullable[Long] and
      (JsPath \ "firstName").read[String] and
      (JsPath \ "lastName").read[String] and
      (JsPath \ "email").read[String] and
      (JsPath \ "password").read[String]
    )(User.apply _)
}

