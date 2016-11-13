package models.dto

import play.api.libs.json._
import play.api.libs.functional.syntax._


case class LoginDTO(email: String, password: String)

object LoginDTO{
  implicit val implicitLoginDTOWrites = new Writes[LoginDTO] {
    def writes(loginDTO: LoginDTO): JsValue = {
      Json.obj(
        "email" -> loginDTO.email,
        "password" -> loginDTO.password
      )
    }
  }

  implicit val implicitLoginDTOReads: Reads[LoginDTO] = (
      (JsPath \ "email").read[String] and
      (JsPath \ "password").read[String]
    )(LoginDTO.apply _)
}

